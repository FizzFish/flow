package cn.sast.cli.command

import soot.jimple.infoflow.InfoflowConfiguration

public interface IClassAnalyzeOptionGroup {
   public val infoFlowConfig: InfoflowConfiguration
}
