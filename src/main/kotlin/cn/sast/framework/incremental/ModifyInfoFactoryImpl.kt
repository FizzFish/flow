package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import org.eclipse.jgit.diff.DiffEntry
import soot.*
import soot.jimple.InstanceInvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.collections.MutableSet
import kotlin.collections.set

/** Default implementation converting *raw* targets to [XDecl] wrappers and building dependency graphs. */
class ModifyInfoFactoryImpl : ModifyInfoFactory {

    // ---------------------------------------------------------------------
    //  XDecl conversions
    // ---------------------------------------------------------------------
    override fun toDecl(target: Any): XDecl = when (target) {
        is SootClass  -> ClassDecl(target)
        is SootMethod -> MethodDecl(target)
        is SootField  -> FieldDecl(target)
        is Path       -> FileDecl(Resource.of(target))
        is File       -> FileDecl(Resource.of(target.toPath()))
        is IResource  -> FileDecl(target)
        else -> error("Not supported target type: ${target::class.qualifiedName}")
    }

    override fun getPatchedDeclsByDiff(target: Any, diff: DiffEntry): Collection<XDecl> =
        getSubDecls(toDecl(target))

    override fun getSubDecls(decl: XDecl): Collection<XDecl> = when (decl) {
        is FileDecl  -> listOf(decl)
        is ClassDecl -> buildList {
            add(decl)
            decl.target.methods.forEach { add(toDecl(it)) }
            decl.target.fields.forEach  { add(toDecl(it)) }
        }
        is MethodDecl, is FieldDecl -> listOf(decl)
        else -> error("Unexpected XDecl: $decl")
    }

    // ---------------------------------------------------------------------
    //  Dependency‑graph builders
    // ---------------------------------------------------------------------
    override fun createInterProceduralAnalysisDependsGraph(): IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph {
        val core = DependsGraph(this)
        return object : IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph by core {
            /**
             * Build *method‑level* call dependencies out of Soot [CallGraph].
             * `src → tgt` means *src depends on tgt*.
             */
            fun update(cg: CallGraph) {
                cg.iterator().forEachRemaining { edge ->
                    val src = edge.src()
                    val tgt = edge.tgt()
                    val invokeStmt = edge.srcUnit() as? Stmt ?: return@forEachRemaining
                    val invokeExpr = invokeStmt.invokeExpr as? InstanceInvokeExpr
                    val baseType  = invokeExpr?.base?.type as? RefType
                    // Skip `java.lang.Object` virtual invokes that are usually noise
                    if (baseType?.className == "java.lang.Object") return@forEachRemaining
                    dependsOn(toDecl(src), toDecl(tgt))
                }
            }
        }
    }

    override fun createSimpleDeclAnalysisDependsGraph(): IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph =
        DependsGraph(this).let { core ->
            object : IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph by core {}
        }

    // ---------------------------------------------------------------------
    //  Scan‑action shortcuts
    // ---------------------------------------------------------------------
    override fun getScanAction(target: XDecl): ScanAction =
        if (target is MethodDecl && OthersKt.isDummy(target.target)) ScanAction.Skip else ScanAction.Keep

    // ---------------------------------------------------------------------
    //  XDecl wrappers
    // ---------------------------------------------------------------------
    data class ClassDecl(val target: SootClass) : XDecl
    data class MethodDecl(val target: SootMethod) : XDecl
    data class FieldDecl(val target: SootField) : XDecl
    data class FileDecl(val target: IResource) : XDecl {
        constructor(path: Path) : this(Resource.of(path))
        constructor(file: File) : this(Resource.of(file.toPath()))
        override fun toString(): String = target.absolute.normalize().toString()
    }
}