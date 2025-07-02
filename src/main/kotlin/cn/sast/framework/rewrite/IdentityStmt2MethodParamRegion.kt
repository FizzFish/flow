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
public class IdentityStmt2MethodParamRegion(info: SootInfoCache) : BodyTransformer {
   public final val info: SootInfoCache

   init {
      this.info = info;
   }

   protected open fun internalTransform(b: Body, phaseName: String, options: Map<String, String>) {
      if (Options.v().verbose()) {
         logger.debug("[${b.getMethod().getName()}] Rewrite IdentityStmt region ...");
      }

      val var10000: UnitPatchingChain = b.getUnits();
      val units: PatchingChain = var10000 as PatchingChain;
      if (!(var10000 as PatchingChain).isEmpty()) {
         if (!b.getMethod().isStatic() || b.getMethod().getParameterCount() != 0) {
            val var24: AnalysisCache.G = AnalysisCache.G.INSTANCE;
            val var10001: SootMethod = b.getMethod();
            val var25: SootMethodExtend = var24.sootHost2decl(var10001 as Host) as SootMethodExtend;
            if (var25 != null) {
               val method: SootMethodExtend = var25;
               val var26: CallableDeclaration = var25.getDecl();
               if (var26 != null) {
                  val decl: CallableDeclaration = var26;
                  val var27: java.util.Iterator = units.iterator();
                  val var7: java.util.Iterator = var27;

                  while (var7.hasNext()) {
                     val unit: Unit = var7.next() as Unit;
                     if (unit is IdentityStmt) {
                        val rop: Value = (unit as IdentityStmt).getRightOp();
                        if (rop is ThisRef) {
                           var var33: IdentityStmt2MethodParamRegion = this;
                           val var34: Host = unit as Host;
                           val var10002: NodeWithRange = method.getNameDecl();
                           val var35: Region;
                           if (var10002 != null) {
                              val var23: Region = Region.Companion.invoke(var10002);
                              var33 = this;
                              var35 = var23;
                           } else {
                              var35 = null;
                           }

                           var33.addTag(var34, var35);
                        } else if (rop is ParameterRef) {
                           val var28: NodeList = decl.getParameters();
                           val var29: Parameter = CollectionsKt.getOrNull(var28 as java.util.List, (rop as ParameterRef).getIndex()) as Parameter;
                           if (var29 != null) {
                              val var30: SimpleName = var29.getName();
                              if (var30 != null) {
                                 val var31: Region = Region.Companion.invoke(var30 as NodeWithRange<?>);
                                 if (var31 != null) {
                                    val var32: Region = if (var31.startLine >= 0) var31 else null;
                                    if ((if (var31.startLine >= 0) var31 else null) != null) {
                                       this.addTag(unit as Host, var32);
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
         if (Options.v().keep_line_number()) {
            `$this$addTag`.removeTag("SourceLnPosTag");
            `$this$addTag`.removeTag("SourceLnPosTag");
            `$this$addTag`.addTag((new SourceLnPosTag(region.startLine, region.getEndLine(), region.startColumn, region.getEndColumn())) as Tag);
         }

         return;
      }
   }

   @JvmStatic
   fun {
      val var10000: Logger = LoggerFactory.getLogger(IdentityStmt2MethodParamRegion.class);
      logger = var10000;
   }

   public companion object {
      public const val phase: String
      private final val logger: Logger
   }
}
