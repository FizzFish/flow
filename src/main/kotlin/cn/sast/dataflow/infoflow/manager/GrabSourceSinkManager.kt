package cn.sast.dataflow.infoflow.manager

import kotlin.jvm.internal.SourceDebugExtension
import soot.Scene
import soot.jimple.Stmt
import soot.jimple.infoflow.InfoflowManager
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager
import soot.jimple.infoflow.sourcesSinks.manager.SinkInfo
import soot.jimple.infoflow.sourcesSinks.manager.SourceInfo

@SourceDebugExtension(["SMAP\nGrabSourceSinkManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrabSourceSinkManager.kt\ncn/sast/dataflow/infoflow/manager/GrabSourceSinkManager\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,16:1\n1#2:17\n*E\n"])
public class GrabSourceSinkManager(delegate: ISourceSinkManager) : ISourceSinkManager {
   public final val delegate: ISourceSinkManager

   init {
      this.delegate = delegate;
   }

   public open fun initialize() {
      if (!Scene.v().hasCallGraph()) {
         throw new IllegalArgumentException("have no call graph".toString());
      } else {
         this.delegate.initialize();
      }
   }

   public open fun getSourceInfo(p0: Stmt, p1: InfoflowManager): SourceInfo {
      return this.delegate.getSourceInfo(p0, p1);
   }

   public open fun getSinkInfo(p0: Stmt, p1: InfoflowManager, p2: AccessPath): SinkInfo {
      return this.delegate.getSinkInfo(p0, p1, p2);
   }
}
