package cn.sast.cli.config

import java.nio.file.Path
import org.utbot.common.ClassLocation
import org.utbot.common.FileUtil
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser
import soot.jimple.infoflow.android.source.parsers.xml.XMLSourceSinkParser
import soot.jimple.infoflow.rifl.RIFLSourceSinkDefinitionProvider
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider

public final val flowDroidLoc: ClassLocation = FileUtil.INSTANCE.locateClass(InfoflowAndroidConfiguration.class)
public final val flowDroidClass: Path = FileUtil.INSTANCE.findPathToClassFiles(flowDroidLoc)

public fun getFlowDroidSourceSinkProvider(fileExtension: String, sourceSinkFile: String): ISourceSinkDefinitionProvider? {
   switch (fileExtension.hashCode()) {
      case 115312:
         if (fileExtension.equals("txt")) {
            return PermissionMethodParser.fromFile(sourceSinkFile) as ISourceSinkDefinitionProvider;
         }
         break;
      case 118807:
         if (fileExtension.equals("xml")) {
            return XMLSourceSinkParser.fromFile(sourceSinkFile, null) as ISourceSinkDefinitionProvider;
         }
         break;
      case 3500349:
         if (fileExtension.equals("rifl")) {
            return (new RIFLSourceSinkDefinitionProvider(sourceSinkFile)) as ISourceSinkDefinitionProvider;
         }
      default:
   }

   return null;
}

public fun main(args: Array<String>) {
   try {
      new FlowDroidSourceSinkTranslatorCli().main(args);
   } catch (var2: java.lang.Throwable) {
      var2.printStackTrace();
      System.exit(1);
      throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
   }
}
