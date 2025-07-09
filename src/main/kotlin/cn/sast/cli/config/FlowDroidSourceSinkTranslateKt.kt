package cn.sast.cli.config

import org.utbot.common.ClassLocation
import org.utbot.common.FileUtil
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser
import soot.jimple.infoflow.android.source.parsers.xml.XMLSourceSinkParser
import soot.jimple.infoflow.rifl.RIFLSourceSinkDefinitionProvider
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

/** FlowDroid 所在 JAR / class-files 的定位信息 */
val flowDroidLoc: ClassLocation =
    FileUtil.locateClass(InfoflowAndroidConfiguration::class.java)

/** FlowDroid 主包的 class 路径 */
val flowDroidClass: Path =
    FileUtil.findPathToClassFiles(flowDroidLoc)

/**
 * 根据文件扩展名构建合适的 Source/Sink provider。
 *
 * * `txt`  → `PermissionMethodParser`
 * * `xml`  → `XMLSourceSinkParser`
 * * `rifl` → `RIFLSourceSinkDefinitionProvider`
 */
fun getFlowDroidSourceSinkProvider(
    fileExtension: String,
    sourceSinkFile: String
): ISourceSinkDefinitionProvider? =
    when (fileExtension.lowercase()) {
        "txt"  -> PermissionMethodParser.fromFile(sourceSinkFile)
        "xml"  -> XMLSourceSinkParser.fromFile(sourceSinkFile, /* component filter */ null)
        "rifl" -> RIFLSourceSinkDefinitionProvider(sourceSinkFile)
        else   -> null
    }

/** 便捷入口，直接转入 CLI */
fun main(args: Array<String>) {
    try {
        FlowDroidSourceSinkTranslatorCli().main(args)
    } catch (t: Throwable) {
        t.printStackTrace()
        exitProcess(1)
    }
}
