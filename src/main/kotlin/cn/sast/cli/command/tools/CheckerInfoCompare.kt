package cn.sast.cli.command.tools

import cn.sast.api.config.CheckerInfo
import cn.sast.common.IResFile
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.system.exitProcess

/**
 * Compare two `checker_info.json` snapshots (old vs. new) and output added / removed / changed rules.
 * This is a *pure Kotlin* re‑implementation of the original VineFlower decompiled source.
 */
class CheckerInfoCompare {
    fun compareWith(output: Path, left: IResFile, right: IResFile) {
        require(!output.exists() || output.isDirectory()) { "output must be a directory" }

        val json = jsonFormat
        val rightInfos: List<CheckerInfo> = Files.newInputStream(right.path).use { json.decodeFromStream(infoSerializer, it) }
        val leftInfos: List<CheckerInfo> = Files.newInputStream(left.path).use { json.decodeFromStream(infoSerializer, it) }

        val rightJavaCanary = rightInfos.filter { it.analyzer == "Java(canary)" }
        val leftJavaCanary = leftInfos.filter { it.analyzer == "Java(canary)" }

        val leftIds = leftJavaCanary.map { it.checker_id }.toSet()
        val rightIds = rightJavaCanary.map { it.checker_id }.toSet()

        val deletedIds = leftIds - rightIds
        val newIds = rightIds - leftIds

        val leftBaseName = left.path.fileName.toString().substringBeforeLast('.')
        val compareDir = output.resolve("compare-$leftBaseName").apply { if (!exists()) createDirectories() }

        // 1. deleted-checker-ids.json
        Files.newOutputStream(compareDir.resolve("deleted-checker-ids.json")).use { out ->
            json.encodeToStream(ListSerializer(String.serializer()), deletedIds.toList(), out)
        }

        // 2. deleted.json (full objects)
        Files.newOutputStream(compareDir.resolve("deleted.json")).use { out ->
            json.encodeToStream(infoSerializer, leftJavaCanary.filter { it.checker_id in deletedIds }, out)
        }

        // 3. checker_name_mapping.json
        val nameMapping = leftJavaCanary.associate { it.checker_id to it.checker_name }
        Files.newOutputStream(compareDir.resolve("checker_name_mapping.json")).use { out ->
            json.encodeToStream(MapSerializer(String.serializer(), String.serializer()), nameMapping, out)
        }

        // 4. new.json (ids only)
        Files.newOutputStream(compareDir.resolve("new.json")).use { out ->
            json.encodeToStream(ListSerializer(String.serializer()), newIds.toList(), out)
        }

        logger.info { "Compare completed. Output dir: $compareDir" }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private val jsonFormat: Json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            useArrayPolymorphism = true
        }
        private val infoSerializer = ListSerializer(CheckerInfo.serializer())
    }
}
