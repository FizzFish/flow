package cn.sast.framework.compiler

import cn.sast.common.IResource
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.time.LocalDateTime
import kotlin.jvm.internal.Ref
import kotlinx.collections.immutable.PersistentSet
import mu.KLogger
import mu.KotlinLogging
import org.eclipse.jdt.core.compiler.CompilationProgress
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath
import org.eclipse.jdt.internal.compiler.batch.Main
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

/**
 * A thin wrapper around Eclipse ECJ that understands wildcard class‑path entries (e.g. `libs/`) and
 * provides structured logging via UTBot’s `LoggingKt` helpers.
*/

class EcjCompiler(
    private val sourcePath: PersistentSet<IResource>,
    private val classpath: PersistentSet<String>,
    private val classOutput: IResource,
    private val customOptions: List<String> = emptyList(),
    private val useDefaultJava: Boolean = true,
    outWriter: PrintWriter = PrintWriter(System.out),
    errWriter: PrintWriter = PrintWriter(System.err),
    systemExitWhenFinished: Boolean = false,
    customDefaultOptions: MutableMap<String, String>? = null,
    compilationProgress: CompilationProgress? = null,
) : Main(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress) {

    /** All concrete class‑path entries discovered during expansion, useful for diagnostics. */
    val collectedClasspath: MutableSet<String> = LinkedHashSet()

    /**
     * Temporary holder for the *raw* class‑path element handed to `addNewEntry` by ECJ.  We use it
     * to discover what ECJ replaced when expanding wildcard entries so that we can mirror the same
     * replacement logic externally.
     */
    private val currentClasspathNameHack: MutableSet<String> = LinkedHashSet()

    // -------------------------------------------------------------------------
    // Class‑path helpers
    // -------------------------------------------------------------------------

    private fun defaultClasspath(): List<Classpath> =
        super.handleClasspath(null, null)

    override fun addNewEntry(
        paths: ArrayList<Classpath?>?,
        currentClasspathName: String,
        currentRuleSpecs: ArrayList<String?>?,
        customEncoding: String?,
        destPath: String?,
        isSourceOnly: Boolean,
        rejectDestinationPathOnJars: Boolean,
    ) {
        currentClasspathNameHack.add(currentClasspathName)
        super.addNewEntry(
            paths,
            currentClasspathName,
            currentRuleSpecs,
            customEncoding,
            destPath,
            isSourceOnly,
            rejectDestinationPathOnJars,
        )
    }

    /**
     * Ask ECJ to expand the given *wildcard* class‑path entry and return the resolved list of
     * [Classpath] objects – or `null` if ECJ rejected it (e.g. bad syntax).
     */
    private fun resolveEcjClasspath(path: String): List<Classpath>? = try {
        currentClasspathNameHack.clear()
        ArrayList<Classpath>().also { processPathEntries(4, it, path, null, false, false) }
    } catch (_: IllegalArgumentException) {
        null
    }

    /**
     * Expand wildcard entries (e.g. `libs/`) to concrete jar paths exactly the way ECJ does while
     * remembering every resolved file in [collectedClasspath] for later inspection.
    */
    private fun replace(ecjClasspathName: String): List<String> {
        val paths = resolveEcjClasspath(ecjClasspathName)
        return if (currentClasspathNameHack.size == 1 && paths?.size == 1) {
            val original = currentClasspathNameHack.first()
            val result = mutableListOf<String>()

            if (original.isNotEmpty()) {
                val originalFile = File(original)
                if (!originalFile.exists()) {
                    val dir = originalFile.parentFile.toPath()
                    val glob = originalFile.name
                    Files.newDirectoryStream(dir, glob).use { stream ->
                        stream.forEach { resolvedPath ->
                            result += ecjClasspathName.replace(original, resolvedPath.toString(), ignoreCase = false)
                            collectedClasspath += resolvedPath.toString()
                        }
                    }
                }
            }

            collectedClasspath += ecjClasspathName
            if (result.isEmpty()) listOf(ecjClasspathName) else result
        } else {
            collectedClasspath += ecjClasspathName
            listOf(ecjClasspathName)
        }
    }

    // -------------------------------------------------------------------------
    // Compilation entry‑point
    // -------------------------------------------------------------------------

    fun compile(): Boolean {
        val args = mutableListOf<String>()

        if (customOptions.isEmpty()) {
            // ---------------------------------------------------------------------------------
            // Default ECJ flags
            // ---------------------------------------------------------------------------------
            args += listOf(
                "-source", "11",
                "-target", "11",
                "-proceedOnError",
                "-warn:none",
                "-g:lines,vars,source",
                "-preserveAllLocals",
            )

            // ---------------------------------------------------------------------------------
            // Class‑path handling (expands wildcards)
            // ---------------------------------------------------------------------------------
            if (classpath.isNotEmpty()) {
                val classpathMap = LinkedHashMap<String, List<String>>(classpath.size.coerceAtLeast(16))
                classpath.forEach { entry -> classpathMap[entry] = replace(entry) }

                val classpathList = classpathMap.values.flatten().toMutableList()
                if (useDefaultJava) {
                    classpathList += defaultClasspath().map { it.path }
                }

                classpathList.forEach { cp ->
                    args += "-classpath"
                    args += cp
                }
            }

            // Destination directory
            args += listOf("-d", classOutput.toString())

            // Source files
            sourcePath.forEach { args += it.file.path }

            kLogger.info { "ecj cmd args:\n[ ${args.joinToString("\n")} ]" }
        } else {
            require(sourcePath.isEmpty()) { "sourcePath must be empty when using customOptions: $customOptions" }
            require(classpath.isEmpty()) { "classpath must be empty when using customOptions: $customOptions" }
            args += customOptions
        }

        // ---------------------------------------------------------------------------------
        // Perform compilation with structured logging
        // ---------------------------------------------------------------------------------
        val log = LoggingKt.info(kLogger)
        val msg = "compile java source"
        log.logMethod.invoke { "Started: $msg" }

        val startTime = LocalDateTime.now()
        var caught: Throwable? = null
        val maybeResult = Ref.ObjectRef<Maybe<Boolean>>().apply { element = Maybe.empty() }

        val result: Boolean = try {
            super.compile(args.toTypedArray(), null, null).also {
                maybeResult.element = Maybe.just(it)
            }
        } catch (t: Throwable) {
            caught = t
            log.logMethod.invoke {
                "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): $msg :: EXCEPTION :: ${t.message}"
            }
            throw t
        } finally {
            if (caught == null) {
                val elapsed = LoggingKt.elapsedSecFrom(startTime)
                if (maybeResult.element?.hasValue == true) {
                    log.logMethod.invoke { "Finished (in $elapsed): $msg" }
                } else {
                    log.logMethod.invoke { "Finished (in $elapsed): $msg <Nothing>" }
                }
            }
        }

        return result
    }

    companion object {
        private val kLogger: KLogger = KotlinLogging.logger {}
    }
}