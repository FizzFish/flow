package cn.sast.dataflow.infoflow.manager

import soot.Scene
import soot.jimple.Stmt
import soot.jimple.infoflow.InfoflowManager
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager
import soot.jimple.infoflow.sourcesSinks.manager.SinkInfo
import soot.jimple.infoflow.sourcesSinks.manager.SourceInfo

class GrabSourceSinkManager(
   private val delegate: ISourceSinkManager
) : ISourceSinkManager {

   override fun initialize() {
      if (!Scene.v().hasCallGraph()) {
         error("No call graph available")
      }
      delegate.initialize()
   }

   override fun getSourceInfo(stmt: Stmt, manager: InfoflowManager): SourceInfo {
      return delegate.getSourceInfo(stmt, manager)
   }

   override fun getSinkInfo(stmt: Stmt, manager: InfoflowManager, accessPath: AccessPath): SinkInfo {
      return delegate.getSinkInfo(stmt, manager, accessPath)
   }
}
