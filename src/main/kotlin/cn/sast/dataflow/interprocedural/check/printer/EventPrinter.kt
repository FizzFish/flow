package cn.sast.dataflow.interprocedural.check.printer

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.EntryParam
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.PhantomField
import cn.sast.dataflow.interprocedural.check.AssignLocalPath
import cn.sast.dataflow.interprocedural.check.ExitInvoke
import cn.sast.dataflow.interprocedural.check.GetEdgePath
import cn.sast.dataflow.interprocedural.check.IPath
import cn.sast.dataflow.interprocedural.check.InvokeEdgePath
import cn.sast.dataflow.interprocedural.check.LiteralPath
import cn.sast.dataflow.interprocedural.check.MergePath
import cn.sast.dataflow.interprocedural.check.ModelBind
import cn.sast.dataflow.interprocedural.check.PrevCallStmtInfo
import cn.sast.dataflow.interprocedural.check.SetEdgePath
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.baseimpl.IexConst
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.Constant
import soot.jimple.DefinitionStmt
import soot.jimple.IdentityStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.tagkit.ParamNamesTag

@SourceDebugExtension(["SMAP\nEventPrinter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/EventPrinter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,245:1\n1#2:246\n1#2:249\n1#2:251\n1#2:253\n310#3:247\n303#3:248\n303#3:250\n303#3:252\n808#4,11:254\n*S KotlinDebug\n*F\n+ 1 EventPrinter.kt\ncn/sast/dataflow/interprocedural/check/printer/EventPrinter\n*L\n101#1:249\n129#1:251\n156#1:253\n100#1:247\n101#1:248\n129#1:250\n156#1:252\n225#1:254,11\n*E\n"])
internal class EventPrinter(prefix: String) {
   private final val prefix: String
   private final var outputEn: StringBuffer
   private final var outputZh: StringBuffer

   public final var region: Region?
      internal set

   public final val message: Map<Language, String>?
      public final get() {
         return if (this.outputEn.length() == 0)
            null
            else
            MapsKt.mapOf(new Pair[]{TuplesKt.to(Language.EN, "${this.prefix} ${this.outputEn}"), TuplesKt.to(Language.ZH, "${this.prefix} ${this.outputZh}")});
      }


   init {
      this.prefix = prefix;
      this.outputEn = new StringBuffer();
      this.outputZh = new StringBuffer();
   }

   public operator fun StringBuffer.plusAssign(string: String) {
      `$this$plusAssign`.append(string);
   }

   public fun default(pathNode: IPath, unit: Unit) {
      val keyPath: java.lang.String = SimpleUnitPrinter.Companion.getStringOf(unit);
      this.plusAssign(this.outputEn, "key path: $keyPath");
      this.plusAssign(this.outputZh, "关键路径: $keyPath");
   }

   private fun printModelingBinding(v: ModelBind, unit: Unit, sootMethod: SootMethod) {
      if (v.getMt() is TaintProperty) {
         val call: IValue = v.getObj();
         if (call !is EntryParam && call !is PhantomField) {
            this.plusAssign(this.outputZh, "污点传递");
            this.plusAssign(this.outputEn, "Taint propagate");
         } else {
            this.plusAssign(this.outputZh, "污点源 Source 点: ");
            this.plusAssign(this.outputEn, "Taint source: ");
            val msg: IValue = v.getObj();
            if (msg is EntryParam && this.printParameterNameByIndex((msg as EntryParam).getMethod(), (msg as EntryParam).getParamIndex())) {
               return;
            }
         }

         val var11: java.lang.String = SimpleUnitPrinter.Companion.getStringOf(unit);
         var var12: Boolean = false;
         if (unit !is IdentityStmt) {
            this.write(": $var11");
            var12 = true;
         }

         if (v.getInfo() != null) {
            val params: java.util.List = v.getInfo().getParameterNamesInUnitDefUse(unit);
            if (!params.isEmpty()) {
               val paramsStr: java.lang.String = CollectionsKt.joinToString$default(params, ", ", null, null, 0, null, null, 62, null);
               this.plusAssign(this.outputZh, ", 关键位置: `$paramsStr`");
               this.plusAssign(this.outputEn, ", key scope: `$paramsStr`");
               if (v.getInfo() is PrevCallStmtInfo) {
                  var var10000: EventPrinter = this;
                  val var10001: Int = (v.getInfo() as PrevCallStmtInfo).getFirstParamIndex();
                  val var13: Region;
                  if (var10001 != null) {
                     var13 = Region.Companion.getParamRegion(sootMethod, var10001.intValue());
                     var10000 = this;
                  } else {
                     var13 = null;
                  }

                  var10000.region = var13;
               }

               var12 = true;
            }
         }

         if (var12) {
            return;
         }

         this.clean();
      }

      this.default(v, unit);
   }

   public fun printModeling(pathEvent: ModelBind, unit: Unit, sootMethod: SootMethod): EventPrinter {
      this.printModelingBinding(pathEvent, unit, sootMethod);
      return this;
   }

   public fun normalPrint(node: ExitInvoke): EventPrinter {
      val var10000: SimpleUnitPrinter.Companion = SimpleUnitPrinter.Companion;
      val var10001: SootMethod = node.getInvoke().getCallee();
      var `$this$invokeExprOrNull$iv`: Unit = node.getInvoke().getNode();
      val var10002: Value = if ((`$this$invokeExprOrNull$iv` as? DefinitionStmt) != null)
         (`$this$invokeExprOrNull$iv` as? DefinitionStmt).getLeftOp()
         else
         null;
      `$this$invokeExprOrNull$iv` = node.getInvoke().getNode();
      val call: java.lang.String = SimpleUnitPrinter.Companion.getStringOf$default(
         var10000,
         var10001,
         var10002,
         if ((`$this$invokeExprOrNull$iv` as? Stmt) != null)
            (if ((`$this$invokeExprOrNull$iv` as Stmt).containsInvokeExpr()) (`$this$invokeExprOrNull$iv` as Stmt).getInvokeExpr() else null)
            else
            null,
         false,
         8,
         null
      );
      this.plusAssign(this.outputZh, "离开被调用方法: `$call`");
      this.plusAssign(this.outputEn, "return from calling: `$call`");
      return this;
   }

   public fun write(s: String) {
      this.plusAssign(this.outputZh, s);
      this.plusAssign(this.outputEn, s);
   }

   private fun clean() {
      this.outputZh = new StringBuffer();
      this.outputEn = new StringBuffer();
   }

   public fun calleeBeginPrint(invoke: InvokeEdgePath): EventPrinter {
      val container: SootMethod = invoke.getContainer();
      val call: java.lang.String = "${container.getDeclaringClass().getName()}#${container.getName()}";
      this.plusAssign(this.outputZh, "从 `$call` 进入调用");
      this.plusAssign(this.outputEn, "calling from: `$call`");
      return this;
   }

   public fun normalPrint(nodeI: IndexedValue<IPath>, unit: Unit, sootMethod: SootMethod): EventPrinter {
      val node: IPath = nodeI.getValue() as IPath;
      if (node is InvokeEdgePath) {
         val var5: java.lang.String = SimpleUnitPrinter.Companion.getStringOf$default(
            SimpleUnitPrinter.Companion,
            (node as InvokeEdgePath).getCallee(),
            null,
            if ((unit as? Stmt) != null) (if ((unit as Stmt).containsInvokeExpr()) (unit as Stmt).getInvokeExpr() else null) else null,
            false,
            8,
            null
         );
         this.plusAssign(this.outputZh, "进入被调用方法: `$var5`");
         this.plusAssign(this.outputEn, "calling: `$var5`");
      } else if (node !is MergePath) {
         if (node is AssignLocalPath) {
            this.default(node, unit);
         } else if (node is ModelBind) {
            this.printModelingBinding(node as ModelBind, unit, sootMethod);
         } else if (node is LiteralPath) {
            val var13: Any = (node as LiteralPath).getConst();
            if (var13 is IexConst) {
               switch (EventPrinter.WhenMappings.$EnumSwitchMapping$0[((IexConst)const).getType().ordinal()]) {
                  case 1:
                     val invokeExpr: InvokeExpr = if ((unit as? Stmt) != null)
                        (if ((unit as Stmt).containsInvokeExpr()) (unit as Stmt).getInvokeExpr() else null)
                        else
                        null;
                     if (invokeExpr == null || nodeI.getIndex() != 0) {
                        this.clean();
                        return this;
                     }

                     this.plusAssign(this.outputZh, "污点源 Source 点: ");
                     this.plusAssign(this.outputEn, "Taint source: ");
                     this.write(SimpleUnitPrinter.Companion.getStringOf$default(SimpleUnitPrinter.Companion, null, null, invokeExpr, false, 8, null));
                     break;
                  case 2:
                     this.clean();
                     return this;
                  default:
                     this.plusAssign(this.outputZh, "${(var13 as IexConst).getType()} 类型的常量: ");
                     this.plusAssign(this.outputEn, "${(var13 as IexConst).getType()} type constant: ");
                     this.write("`${(var13 as IexConst).getConst()}`");
               }
            } else if (var13 is Constant) {
               val var16: Type = (var13 as Constant).getType();
               val ty: java.lang.String = EventPrinterKt.getPname(var16);
               this.plusAssign(this.outputZh, "$ty 类型的常量: ");
               this.plusAssign(this.outputEn, "$ty type constant: ");
               this.write(java.lang.String.valueOf(var13));
            }
         } else if (node is GetEdgePath) {
            this.default(node, unit);
         } else if (node is SetEdgePath) {
            this.default(node, unit);
         } else {
            this.default(node, unit);
         }
      }

      return this;
   }

   private fun printParameterNameByIndex(sootMethod: SootMethod, index: Int): Boolean {
      val var10000: Boolean;
      if (index == -1) {
         val names: java.lang.String = sootMethod.getDeclaringClass().getName();
         this.plusAssign(this.outputZh, "$names类型的参数: this");
         this.plusAssign(this.outputEn, "$names type parameter: this");
         this.region = Region.Companion.getParamRegion(sootMethod, index);
         var10000 = true;
      } else if (index >= 0 && index < sootMethod.getParameterCount()) {
         val var19: java.util.List = sootMethod.getTags();
         val name: java.lang.Iterable = var19;
         val `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filterIsInstance$iv) {
            if (var11 is ParamNamesTag) {
               `destination$iv$iv`.add(var11);
            }
         }

         label53: {
            val var20: ParamNamesTag = CollectionsKt.firstOrNull(`destination$iv$iv` as java.util.List) as ParamNamesTag;
            if (var20 != null) {
               var21 = var20.getNames();
               if (var21 != null) {
                  break label53;
               }
            }

            var21 = CollectionsKt.emptyList();
         }

         val ty: Type = sootMethod.getParameterType(index);
         var var22: java.lang.String = CollectionsKt.getOrNull(var21, index) as java.lang.String;
         if (var22 == null) {
            var var17: java.lang.String;
            try {
               label44: {
                  val var23: Body = SootUtilsKt.getActiveBodyOrNull(sootMethod);
                  if (var23 != null) {
                     val var24: Local = var23.getParameterLocal(index);
                     if (var24 != null) {
                        var25 = "local var $var24";
                        break label44;
                     }
                  }

                  var25 = null;
               }

               var17 = var25;
            } catch (var14: RuntimeException) {
               var17 = null;
            }

            var22 = if (var17 != null) ": `$var17`" else null;
            if (var22 == null) {
               var22 = "";
            }
         }

         this.plusAssign(this.outputZh, "$ty类型的第${index + 1}个参数$var22`");
         this.plusAssign(this.outputEn, "$ty type parameter${index + 1}$var22}");
         this.region = Region.Companion.getParamRegion(sootMethod, index);
         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }
}
