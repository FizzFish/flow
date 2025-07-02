package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.SootMethod
import soot.tagkit.ParamNamesTag

@SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PrevCallStmtInfo\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,994:1\n808#2,11:995\n1#3:1006\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PrevCallStmtInfo\n*L\n472#1:995,11\n*E\n"])
public class PrevCallStmtInfo(stmt: IStmt, method: SootMethod) : ModelingStmtInfo(stmt) {
   public final val method: SootMethod

   init {
      this.method = method;
   }

   public override fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any? {
      var var7: java.lang.String;
      if (index is MParameter) {
         var7 = this.getParameterNameByIndex((index as MParameter).getIndex());
         if (var7 != null) {
            if (this.getFirstParamIndex() == null) {
               this.setFirstParamIndex((index as MParameter).getIndex());
            }

            var7 = if (filter.invoke(var7)) var7 else null;
         } else {
            var7 = null;
         }
      } else {
         var7 = java.lang.String.valueOf(if (filter.invoke(index)) index else null);
      }

      return var7;
   }

   public open fun getParameterNameByIndex(index: Int): String? {
      if (index == -1) {
         return "${this.method.getDeclaringClass().getShortName()}.this";
      } else if (index >= 0 && index < this.method.getParameterCount()) {
         var var10000: java.util.List = this.method.getTags();
         val `$this$filterIsInstance$iv`: java.lang.Iterable = var10000;
         val it: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filterIsInstance$iv) {
            if (`element$iv$iv` is ParamNamesTag) {
               it.add(`element$iv$iv`);
            }
         }

         label44: {
            val var14: ParamNamesTag = CollectionsKt.firstOrNull(it as java.util.List) as ParamNamesTag;
            if (var14 != null) {
               var10000 = var14.getNames();
               if (var10000 != null) {
                  break label44;
               }
            }

            var10000 = CollectionsKt.emptyList();
         }

         var var16: java.lang.String = CollectionsKt.getOrNull(var10000, index) as java.lang.String;
         if (var16 == null) {
            try {
               label34: {
                  val var17: Body = SootUtilsKt.getActiveBodyOrNull(this.method);
                  if (var17 != null) {
                     val var18: Local = var17.getParameterLocal(index);
                     if (var18 != null) {
                        var19 = "local var $var18";
                        break label34;
                     }
                  }

                  var19 = null;
               }

               var11 = var19;
            } catch (var10: RuntimeException) {
               var11 = null;
            }

            var16 = var11;
         }

         return var16;
      } else {
         return null;
      }
   }
}
