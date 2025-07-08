package cn.sast.api.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import org.eclipse.jgit.diff.DiffEntry

interface ModifyInfoFactory {
    fun toDecl(target: Any): XDecl

    fun getPatchedDeclsByDiff(target: Any, diff: DiffEntry): Collection<XDecl>

    fun getSubDecls(decl: XDecl): Collection<XDecl>

    fun createSimpleDeclAnalysisDependsGraph(): SimpleDeclAnalysisDependsGraph

    fun createInterProceduralAnalysisDependsGraph(): InterProceduralAnalysisDependsGraph

    fun getScanAction(target: XDecl): ScanAction
}