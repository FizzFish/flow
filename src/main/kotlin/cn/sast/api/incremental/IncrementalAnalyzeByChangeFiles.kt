package cn.sast.api.incremental

import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import org.eclipse.jgit.diff.DiffEntry
import soot.SootClass
import soot.jimple.toolkits.callgraph.CallGraph

interface IncrementalAnalyzeByChangeFiles : IncrementalAnalyze {
    val simpleDeclAnalysisDependsGraph: SimpleDeclAnalysisDependsGraph
    val interProceduralAnalysisDependsGraph: InterProceduralAnalysisDependsGraph

    fun getChangeTypeOfClass(cls: SootClass): Pair<DiffEntry?, DiffEntry?>

    fun getChangeTypeOfFile(file: String): Pair<DiffEntry?, DiffEntry?>

    interface IDependsGraph : IAnalysisDepends {
        val factory: ModifyInfoFactory

        fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>)

        fun shouldReAnalyzeDecl(target: XDecl): ScanAction

        fun shouldReAnalyzeTarget(target: Any): ScanAction

        fun targetRelate(target: XDecl): Sequence<XDecl>

        fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl>
    }

    interface InterProceduralAnalysisDependsGraph : IDependsGraph {
        fun update(cg: CallGraph)
    }

    interface SimpleDeclAnalysisDependsGraph : IDependsGraph
}