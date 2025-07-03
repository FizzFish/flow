package cn.sast.api.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import org.eclipse.jgit.diff.DiffEntry

public interface ModifyInfoFactory {
    public abstract fun toDecl(target: Any): XDecl

    public abstract fun getPatchedDeclsByDiff(target: Any, diff: DiffEntry): Collection<XDecl>

    public abstract fun getSubDecls(decl: XDecl): Collection<XDecl>

    public abstract fun createSimpleDeclAnalysisDependsGraph(): SimpleDeclAnalysisDependsGraph

    public abstract fun createInterProceduralAnalysisDependsGraph(): InterProceduralAnalysisDependsGraph

    public abstract fun getScanAction(target: XDecl): ScanAction
}