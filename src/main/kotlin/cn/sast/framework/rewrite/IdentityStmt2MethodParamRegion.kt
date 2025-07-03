package cn.sast.framework.rewrite

import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.analysis.SootMethodExtend
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.CallableDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.nodeTypes.NodeWithRange
import kotlin.jvm.internal.SourceDebugExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import soot.Body
import soot.BodyTransformer
import soot.PatchingChain
import soot.SootMethod
import soot.Unit
import soot.UnitPatchingChain
import soot.Value
import soot.jimple.IdentityStmt
import soot.jimple.ParameterRef
import soot.jimple.ThisRef
import soot.options.Options
import soot.tagkit.Host
import soot.tagkit.SourceLnPosTag
import soot.tagkit.Tag

@SourceDebugExtension(["SMAP\nIdentityStmt2MethodParamRegion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IdentityStmt2MethodParamRegion.kt\ncn/sast/framework/rewrite/IdentityStmt2MethodParamRegion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Region.kt\ncom/feysh/corax/config/api/report/Region\n*L\n1#1,61:1\n1#2:62\n1#2:64\n59#3:63\n57#3:65\n57#3:66\n*S KotlinDebug\n*F\n+ 1 IdentityStmt2MethodParamRegion.kt\ncn/sast/framework/rewrite/IdentityStmt2MethodParamRegion\n*L\n40#1:64\n40#1:63\n40#1:65\n47#1:66\n*E\n"])
class IdentityStmt2MethodParamRegion(info: SootInfoCache) : BodyTransformer() {
    val info: SootInfoCache

    init {
        this.info = info
    }

    override fun internalTransform(b: Body, phaseName: String, options: Map<String, String>) {
        if (Options.v().verbose()) {
            logger.debug("[${b.method.name}] Rewrite IdentityStmt region ...")
        }

        val units: PatchingChain<Unit> = b.units
        if (!units.isEmpty()) {
            if (!b.method.isStatic || b.method.parameterCount != 0) {
                val methodExtend = AnalysisCache.G.INSTANCE.sootHost2decl(b.method as Host) as? SootMethodExtend
                if (methodExtend != null) {
                    val decl = methodExtend.decl
                    if (decl != null) {
                        for (unit in units) {
                            if (unit is IdentityStmt) {
                                val rop = unit.rightOp
                                when (rop) {
                                    is ThisRef -> {
                                        val region = methodExtend.nameDecl?.let { Region(it) }
                                        addTag(unit as Host, region)
                                    }
                                    is ParameterRef -> {
                                        val parameter = decl.parameters.getOrNull(rop.index) as? Parameter
                                        parameter?.name?.let { name ->
                                            Region(name as NodeWithRange<*>)?.let { region ->
                                                if (region.startLine >= 0) {
                                                    addTag(unit as Host, region)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Host.addTag(region: Region?) {
        if (region != null && region.startLine >= 0) {
            if (Options.v().keep_line_number) {
                this.removeTag("SourceLnPosTag")
                this.removeTag("SourceLnPosTag")
                this.addTag(SourceLnPosTag(region.startLine, region.endLine, region.startColumn, region.endColumn) as Tag)
            }
        }
    }

    companion object {
        const val phase: String = TODO("FIXME — uninitialized constant")
        private val logger: Logger = LoggerFactory.getLogger(IdentityStmt2MethodParamRegion::class.java)
    }
}