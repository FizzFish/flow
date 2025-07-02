package cn.sast.api.incremental

import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import org.eclipse.jgit.diff.DiffEntry
import soot.SootClass
import soot.jimple.toolkits.callgraph.CallGraph

public interface IncrementalAnalyzeByChangeFiles : IncrementalAnalyze {
   public val simpleDeclAnalysisDependsGraph: cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
   public val interProceduralAnalysisDependsGraph: cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph

   public abstract fun getChangeTypeOfClass(cls: SootClass): Pair<DiffEntry?, DiffEntry?> {
   }

   public abstract fun getChangeTypeOfFile(file: String): Pair<DiffEntry?, DiffEntry?> {
   }

   public interface IDependsGraph : IAnalysisDepends {
      public val factory: ModifyInfoFactory

      public abstract fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
      }

      public abstract fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
      }

      public abstract fun shouldReAnalyzeTarget(target: Any): ScanAction {
      }

      public abstract fun targetRelate(target: XDecl): Sequence<XDecl> {
      }

      public abstract fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> {
      }
   }

   public interface InterProceduralAnalysisDependsGraph : IncrementalAnalyzeByChangeFiles.IDependsGraph {
      public abstract fun update(cg: CallGraph) {
      }
   }

   public interface SimpleDeclAnalysisDependsGraph : IncrementalAnalyzeByChangeFiles.IDependsGraph
}
