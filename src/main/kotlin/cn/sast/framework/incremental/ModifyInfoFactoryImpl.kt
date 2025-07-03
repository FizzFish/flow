package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import java.io.File
import java.nio.file.Path
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import org.eclipse.jgit.diff.DiffEntry
import soot.RefType
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.util.Chain

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/ModifyInfoFactoryImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,395:1\n1628#2,3:396\n1628#2,3:399\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/ModifyInfoFactoryImpl\n*L\n86#1:396,3\n87#1:399,3\n*E\n"])
class ModifyInfoFactoryImpl : ModifyInfoFactory {
    override fun toDecl(target: Any): XDecl {
        return when (target) {
            is SootClass -> ClassDecl(target)
            is SootMethod -> MethodDecl(target)
            is SootField -> FieldDecl(target)
            is Path -> FileDecl(target)
            is File -> FileDecl(target.toPath())
            is IResource -> FileDecl(target)
            else -> throw IllegalStateException("not support yet")
        }
    }

    override fun getPatchedDeclsByDiff(target: Any, diff: DiffEntry): Collection<XDecl> {
        return getSubDecls(toDecl(target))
    }

    override fun getSubDecls(decl: XDecl): Collection<XDecl> {
        return when (decl) {
            is FileDecl -> listOf(decl)
            is ClassDecl -> {
                val t = decl.target
                val methods = t.methods
                val fields = t.fields
                val mutableSet = ArrayList<XDecl>(methods.size + fields.size + 1)
                mutableSet.add(decl)
                
                methods.forEach { mutableSet.add(toDecl(it)) }
                fields.forEach { mutableSet.add(toDecl(it)) }
                
                mutableSet
            }
            is MethodDecl -> listOf(decl)
            is FieldDecl -> listOf(decl)
            else -> throw IllegalStateException("invalid type of $decl")
        }
    }

    override fun createInterProceduralAnalysisDependsGraph(): InterProceduralAnalysisDependsGraph {
        val d = DependsGraph(this)
        return object : InterProceduralAnalysisDependsGraph(d) {
            override fun update(cg: CallGraph) {
                for (edge in cg) {
                    val src = edge.src()
                    val tgt = edge.tgt()
                    val callSite = edge.srcUnit()
                    
                    val invokeExpr = (callSite as? Stmt)?.takeIf { it.containsInvokeExpr() }?.invokeExpr
                    val baseType = (invokeExpr as? InstanceInvokeExpr)?.base?.type
                    
                    if (baseType != RefType.v("java.lang.Object")) {
                        val subSignature = tgt.subSignature
                        val declaringClass = tgt.declaringClass

                        for (method in SootUtilsKt.findMethodOrNull(declaringClass, subSignature)) {
                            dependsOn(factory.toDecl(src), factory.toDecl(method))
                        }
                    }
                }
            }

            override fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
                d.visitChangedDecl(diffPath, changed)
            }

            override fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
                return d.shouldReAnalyzeDecl(target)
            }

            override fun shouldReAnalyzeTarget(target: Any): ScanAction {
                return d.shouldReAnalyzeTarget(target)
            }

            override fun targetRelate(target: XDecl): Sequence<XDecl> {
                return d.targetRelate(target)
            }

            override fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> {
                return d.targetsRelate(targets)
            }

            override fun getFactory(): ModifyInfoFactory {
                return d.factory
            }

            override fun toDecl(target: Any): XDecl {
                return d.toDecl(target)
            }

            override fun dependsOn(thisDependsOn: XDecl, dep: XDecl) {
                d.dependsOn(thisDependsOn, dep)
            }

            override fun dependsOn(thisDependsOn: Collection<XDecl>, deps: Collection<XDecl>) {
                d.dependsOn(thisDependsOn, deps)
            }
        }
    }

    override fun getScanAction(target: XDecl): ScanAction {
        return if (target is MethodDecl && OthersKt.isDummy(target.target)) {
            ScanAction.Skip
        } else {
            ScanAction.Keep
        }
    }

    override fun createSimpleDeclAnalysisDependsGraph(): SimpleDeclAnalysisDependsGraph {
        val d = DependsGraph(this)
        return object : SimpleDeclAnalysisDependsGraph(d) {
            override fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
                d.visitChangedDecl(diffPath, changed)
            }

            override fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
                return d.shouldReAnalyzeDecl(target)
            }

            override fun shouldReAnalyzeTarget(target: Any): ScanAction {
                return d.shouldReAnalyzeTarget(target)
            }

            override fun targetRelate(target: XDecl): Sequence<XDecl> {
                return d.targetRelate(target)
            }

            override fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> {
                return d.targetsRelate(targets)
            }

            override fun getFactory(): ModifyInfoFactory {
                return d.factory
            }

            override fun toDecl(target: Any): XDecl {
                return d.toDecl(target)
            }

            override fun dependsOn(thisDependsOn: XDecl, dep: XDecl) {
                d.dependsOn(thisDependsOn, dep)
            }

            override fun dependsOn(thisDependsOn: Collection<XDecl>, deps: Collection<XDecl>) {
                d.dependsOn(thisDependsOn, deps)
            }
        }
    }

    data class ClassDecl(val target: SootClass) : XDecl {
        override fun component1(): SootClass = target
        fun copy(target: SootClass = this.target): ClassDecl = ClassDecl(target)
        override fun toString(): String = "ClassDecl(target=$target)"
        override fun hashCode(): Int = target.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ClassDecl) return false
            return target == other.target
        }
    }

    data class FieldDecl(val target: SootField) : XDecl {
        override fun component1(): SootField = target
        fun copy(target: SootField = this.target): FieldDecl = FieldDecl(target)
        override fun toString(): String = "FieldDecl(target=$target)"
        override fun hashCode(): Int = target.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FieldDecl) return false
            return target == other.target
        }
    }

    data class FileDecl private constructor(val target: String) : XDecl {
        constructor(path: IResource) : this(path.absolute.normalize.toString())
        constructor(path: Path) : this(Resource.INSTANCE.of(path))

        override fun component1(): String = target
        fun copy(target: String = this.target): FileDecl = FileDecl(target)
        override fun toString(): String = "FileDecl(target=$target)"
        override fun hashCode(): Int = target.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FileDecl) return false
            return target == other.target
        }
    }

    data class MethodDecl(val target: SootMethod) : XDecl {
        override fun component1(): SootMethod = target
        fun copy(target: SootMethod = this.target): MethodDecl = MethodDecl(target)
        override fun toString(): String = "MethodDecl(target=$target)"
        override fun hashCode(): Int = target.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MethodDecl) return false
            return target == other.target
        }
    }
}