package cn.sast.cli.config

import java.nio.file.Path
import org.utbot.common.ClassLocation
import org.utbot.common.FileUtil
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser
import soot.jimple.infoflow.android.source.parsers.xml.XMLSourceSinkParser
import soot.jimple.infoflow.rifl.RIFLSourceSinkDefinitionProvider
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

public val flowDroidLoc: ClassLocation = FileUtil.INSTANCE.locateClass(InfoflowAndroidConfiguration::class.java)
public val flowDroidClass: Path = FileUtil.INSTANCE.findPathToClassFiles(flowDroidLoc)

public fun getFlowDroidSourceSinkProvider(fileExtension: String, sourceSinkFile: String): ISourceSinkDefinitionProvider? {
    return when (fileExtension.hashCode()) {
        115312 -> if (fileExtension == "txt") {
            PermissionMethodParser.fromFile(sourceSinkFile) as ISourceSinkDefinitionProvider
        } else {
            null
        }
        118807 -> if (fileExtension == "xml") {
            XMLSourceSinkParser.fromFile(sourceSinkFile, null) as ISourceSinkDefinitionProvider
        } else {
            null
        }
        3500349 -> if (fileExtension == "rifl") {
            RIFLSourceSinkDefinitionProvider(sourceSinkFile) as ISourceSinkDefinitionProvider
        } else {
            null
        }
        else -> null
    }
}

public fun main(args: Array<String>) {
    try {
        FlowDroidSourceSinkTranslatorCli().main(args)
    } catch (e: Throwable) {
        e.printStackTrace()
        System.exit(1)
        throw RuntimeException("System.exit returned normally, while it was supposed to halt JVM.")
    }
}