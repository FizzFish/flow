package cn.sast.dataflow.interprocedural.check.checker

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IOpCalculator
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JFieldNameType
import cn.sast.dataflow.interprocedural.analysis.PointsToGraphBuilderAbstract
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMap
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMapBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSetBuilder
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValues
import cn.sast.dataflow.interprocedural.check.heapimpl.ObjectValuesBuilder
import cn.sast.idfa.check.ICallCB
import com.feysh.corax.config.api.AttributeName
import com.feysh.corax.config.api.BuiltInField
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.Elements
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IIexGetField
import com.feysh.corax.config.api.IIexLoad
import com.feysh.corax.config.api.IIstSetField
import com.feysh.corax.config.api.IIstStoreLocal
import com.feysh.corax.config.api.IModelExpressionVisitor
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import com.feysh.corax.config.api.MapKeys
import com.feysh.corax.config.api.MapValues
import com.feysh.corax.config.api.SubFields
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.ViaProperty
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.ArrayType
import soot.Scene
import soot.Type

@SourceDebugExtension(["SMAP\nCheckerModeling.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/FactModeling\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,565:1\n774#2:566\n865#2,2:567\n774#2:569\n865#2,2:570\n46#3:572\n47#3:574\n1#4:573\n*S KotlinDebug\n*F\n+ 1 CheckerModeling.kt\ncn/sast/dataflow/interprocedural/check/checker/FactModeling\n*L\n197#1:566\n197#1:567,2\n200#1:569\n200#1:570,2\n242#1:572\n242#1:574\n242#1:573\n*E\n"])
public class FactModeling(hf: AbstractHeapFactory<IValue>,
   env: HeapValuesEnv,
   summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>,
   builder: Builder<IValue>
) {
   public final val hf: AbstractHeapFactory<IValue>
   public final val env: HeapValuesEnv
   public final val summaryCtxCalleeSite: ICallCB<IHeapValues<IValue>, Builder<IValue>>
   public final val builder: Builder<IValue>

   public final val isArray: Boolean
      public final get() {
         return `$this$isArray` is CompanionV
            && (`$this$isArray` as CompanionV).getValue() is IValue
            && ((`$this$isArray` as CompanionV).getValue() as IValue).getType() is ArrayType;
      }


   init {
      this.hf = hf;
      this.env = env;
      this.summaryCtxCalleeSite = summaryCtxCalleeSite;
      this.builder = builder;
   }

   public fun List<Any>.toHV(): IHeapValues<IValue> {
      val b: IHeapValues.Builder = this.hf.emptyBuilder();

      for (Object e : $this$toHV) {
         if (e is CompanionV) {
            if ((e as CompanionV).getValue() !is IValue) {
               throw new IllegalStateException("Check failed.".toString());
            }

            b.add(e as CompanionV);
         }
      }

      return b.build();
   }

   public fun store(values: List<Any>, dest: MLocal, append: Boolean = false) {
      val var10000: IFact.Builder = this.builder;
      val out: PointsToGraphBuilderAbstract = var10000 as PointsToGraphBuilderAbstract;
      val value: IHeapValues = this.toHV(values);
      if (dest == MReturn.INSTANCE) {
         if (append) {
            this.summaryCtxCalleeSite.setReturn(this.summaryCtxCalleeSite.getReturn().plus(value));
         } else {
            this.summaryCtxCalleeSite.setReturn(value);
         }
      } else if (dest is MParameter) {
         out.assignNewExpr(this.env, this.summaryCtxCalleeSite.argToValue((dest as MParameter).getIndex()), value, append);
      } else if (!(dest == MGlobal.INSTANCE)) {
         throw new NoWhenBranchMatchedException();
      }
   }

   public fun setField(baseExpr: IExpr?, bases: List<Any>, values: List<Any>, field: IClassField, append: Boolean) {
      if (field is BuiltInField) {
         val mt: BuiltInField = field as BuiltInField;
         if (!(field as BuiltInField == TaintProperty.INSTANCE) && !(field as BuiltInField == ViaProperty.INSTANCE)) {
            if (!(mt == MapKeys.INSTANCE) && !(mt == MapValues.INSTANCE) && !(mt == Elements.INSTANCE)) {
               throw new NoWhenBranchMatchedException();
            }

            var value: java.util.List = bases;
            val appendx: BuiltInField = field as BuiltInField;
            val var10000: BuiltInModelT;
            if ((field as BuiltInField) !is Elements) {
               if (appendx is MapKeys) {
                  var10000 = BuiltInModelT.MapKeys;
               } else {
                  if (appendx !is MapValues) {
                     throw new NotImplementedError("An operation is not implemented: unreachable");
                  }

                  var10000 = BuiltInModelT.MapValues;
               }
            } else {
               var base: java.lang.Iterable = bases;
               var var15: java.util.Collection = new ArrayList();

               for (Object element$iv$iv : $this$filter$iv) {
                  if (this.isArray(`element$iv$iv`)) {
                     var15.add(`element$iv$iv`);
                  }
               }

               val `this_$iv`: java.util.List = var15 as java.util.List;
               base = bases;
               var15 = new ArrayList();

               for (Object element$iv$ivx : $this$filter$iv) {
                  if (!this.isArray(`element$iv$ivx`)) {
                     var15.add(`element$iv$ivx`);
                  }
               }

               value = var15 as java.util.List;
               if (!`this_$iv`.isEmpty()) {
                  IFact.Builder.DefaultImpls.assignNewExpr$default(this.builder, this.env, "@arr", this.toHV(`this_$iv`), false, 8, null);
                  this.builder.setArrayValueNew(this.env, "@arr", null, this.toHV(values));
                  this.builder.kill("@arr");
               }

               var10000 = BuiltInModelT.Element;
            }

            val var27: IHeapValues = this.toHV(value);
            this.hf.resolveOp(this.env, var27).resolve(FactModeling::setField$lambda$2);
         } else {
            this.propertyPropagate(baseExpr, bases, values, field, append);
         }
      } else if (field is ClassField) {
         val var21: IHeapValues = this.toHV(bases);
         val var23: IHeapValues = this.toHV(values);
         val var31: FieldUtil = FieldUtil.INSTANCE;
         val var36: ClassField = field as ClassField;
         val var53: java.lang.String = (field as ClassField).getFieldType();
         val var54: Type = if (var53 != null) Scene.v().getTypeUnsafe(var53, true) else null;
         val var55: JFieldNameType = new JFieldNameType;
         val var10002: java.lang.String = var36.getFieldName();
         var var10003: Type = var54;
         if (var54 == null) {
            var10003 = Scene.v().getObjectType() as Type;
         }

         var55./* $VF: Unable to resugar constructor */<init>(var10002, var10003);
         IFact.Builder.DefaultImpls.assignNewExpr$default(this.builder, this.env, "@base", var21, false, 8, null);
         this.builder.setFieldNew(this.env, "@base", var55, var23);
         this.builder.kill("@base");
      } else if (field is AttributeName) {
         val var22: Any = CheckerModelingKt.getKeyAttribute();
         val var24: IHeapValues = this.toHV(values);
         val var28: Boolean = !this.toHV(bases).isSingle();

         for (CompanionV base : this.toHV(bases)) {
            val var41: IData = this.builder.getValueData((IValue)var37.getValue(), var22);
            var var40: ImmutableElementHashMap = var41 as? ImmutableElementHashMap;
            if ((var41 as? ImmutableElementHashMap) == null) {
               var40 = new ImmutableElementHashMap(null, null, 3, null);
            }

            val var42: ImmutableElementHashMapBuilder = var40.builder();
            var42.set(this.hf, this.env, (field as AttributeName).getName(), var24, var28);
            val var45: ImmutableElementHashMap = var42.build();
            if (var37.getValue() as IValue is ConstVal) {
               return;
            }

            this.builder.setValueData(this.env, (IValue)var37.getValue(), var22, var45);
         }
      } else if (field == SubFields.INSTANCE) {
      }
   }

   public fun setConstValue(rvalue: IExpr, newBase: CompanionV<IValue>) {
      if (rvalue is IIexGetField) {
         if ((rvalue as IIexGetField).getAccessPath().isEmpty()) {
            return;
         }

         val acp: java.util.List = CollectionsKt.dropLast((rvalue as IIexGetField).getAccessPath(), 1);
         this.setField(null, SequencesKt.toList(this.hf.resolve(this.env, this.summaryCtxCalleeSite, new IIexGetField(acp, rvalue) {
            {
               this.$acp = `$acp`;
               this.$rvalue = `$rvalue`;
            }

            @Override
            public java.util.List<IClassField> getAccessPath() {
               return this.$acp;
            }

            @Override
            public IExpr getBase() {
               return (this.$rvalue as IIexGetField).getBase();
            }

            @Override
            public <TResult> TResult accept(IModelExpressionVisitor<TResult> visitor) {
               return (TResult)visitor.visit(this);
            }
         })), CollectionsKt.listOf(newBase), CollectionsKt.last((rvalue as IIexGetField).getAccessPath()) as IClassField, true);
      } else if (rvalue is IIexLoad) {
         this.store(CollectionsKt.listOf(newBase), (rvalue as IIexLoad).getOp(), true);
      }
   }

   public fun propertyPropagate(baseExpr: IExpr?, bases: List<Any>, values: List<Any>, field: IClassField, append: Boolean) {
      val base: IHeapValues = this.toHV(bases);
      this.hf.resolveOp(this.env, base).resolve(FactModeling::propertyPropagate$lambda$4);
   }

   public fun getVisitor(): IModelStmtVisitor<Any> {
      return new IModelStmtVisitor<Object>(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public Object default(IStmt stmt) {
            throw new IllegalStateException(stmt.toString());
         }

         @Override
         public Object visit(IIstStoreLocal stmt) {
            FactModeling.store$default(
               this.this$0,
               SequencesKt.toList(this.this$0.getHf().resolve(this.this$0.getEnv(), this.this$0.getSummaryCtxCalleeSite(), stmt.getValue())),
               stmt.getLocal(),
               false,
               4,
               null
            );
            return Unit.INSTANCE;
         }

         public void visit(IIstSetField stmt) {
            this.this$0
               .setField(
                  stmt.getBase(),
                  SequencesKt.toList(this.this$0.getHf().resolve(this.this$0.getEnv(), this.this$0.getSummaryCtxCalleeSite(), stmt.getBase())),
                  SequencesKt.toList(this.this$0.getHf().resolve(this.this$0.getEnv(), this.this$0.getSummaryCtxCalleeSite(), stmt.getValue())),
                  stmt.getField(),
                  false
               );
         }
      };
   }

   @JvmStatic
   fun `setField$lambda$2`(
      `$append`: Boolean,
      `this$0`: FactModeling,
      `$mt`: Any,
      `$values`: java.util.List,
      `$this$solve`: IOpCalculator,
      ret: IHeapValues.Builder,
      var6: Array<CompanionV>
   ): Boolean {
      val base: CompanionV = var6[0];
      if (`$append`) {
         val b: IData = `this$0`.builder.getValueData((IValue)base.getValue(), `$mt`);
         var collection: ObjectValues = b as? ObjectValues;
         if ((b as? ObjectValues) == null) {
            collection = new ObjectValues(`this$0`.hf.empty());
         }

         val var11: ObjectValuesBuilder = collection.builder();
         var11.addAll(`this$0`.toHV(`$values`));
         `this$0`.builder.setValueData(`this$0`.env, (IValue)base.getValue(), `$mt`, var11.build());
      } else {
         `this$0`.builder.setValueData(`this$0`.env, (IValue)base.getValue(), `$mt`, new ObjectValues(`this$0`.toHV(`$values`)));
      }

      return true;
   }

   @JvmStatic
   fun `propertyPropagate$lambda$4`(
      `$append1`: Boolean,
      `this$0`: FactModeling,
      `$field`: IClassField,
      `$values`: java.util.List,
      `$this$solve`: IOpCalculator,
      var5: IHeapValues.Builder,
      var6: Array<CompanionV>
   ): Boolean {
      val base: CompanionV = var6[0];
      var var10000: ImmutableElementSet;
      if (`$append1`) {
         val var9: IData = `this$0`.builder.getValueData((IValue)base.getValue(), `$field`);
         var10000 = var9 as? ImmutableElementSet;
      } else {
         var10000 = null;
      }

      if (var10000 != null && !var10000.isEmpty()) {
         var10000 = null;
      } else if (`$values`.size() == 1) {
         var10000 = (ImmutableElementSet)CollectionsKt.first(`$values`);
         var10000 = var10000;
      } else {
         var10000 = null;
      }

      if (var10000 == null) {
         val setBuilder: ImmutableElementSetBuilder = new ImmutableElementSet(null, null, 3, null).builder();

         for (Object typeValues : $values) {
            var10000 = typeValues as? ImmutableElementSet;
            if ((typeValues as? ImmutableElementSet) != null) {
               val set: ImmutableElementSet = var10000;

               for (Object e : var10000.getElement()) {
                  setBuilder.set(`this$0`.hf, `this$0`.env, e, set.get(`this$0`.hf, e), `$append1`);
               }
            }
         }

         var10000 = setBuilder.build();
      }

      if (base.getValue() as IValue is ConstVal) {
         return false;
      } else {
         `this$0`.builder.setValueData(`this$0`.env, (IValue)base.getValue(), CheckerModelingKt.getKeyTaintProperty(), var10000);
         return true;
      }
   }
}
