@file:SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactoryKt\n+ 2 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/FieldUtil\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1430:1\n44#2:1431\n1#3:1432\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/HeapFactoryKt\n*L\n44#1:1431\n*E\n"])

package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FieldUtil
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IFact
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JSootFieldType
import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.check.callback.ICallCBImpl
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import soot.SootField

public inline fun <T> flowIt(crossinline c: (FlowCollector<T>, Continuation<Unit>) -> Any?): Flow<T> {
   return FlowKt.flow((new Function2<FlowCollector<? super T>, Continuation<? super Unit>, Object>(c, null) {
      int label;

      {
         super(2, `$completionx`);
         this.$c = `$c`;
      }

      public final Object invokeSuspend(Object $result) {
         val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         switch (this.label) {
            case 0:
               ResultKt.throwOnFailure(`$result`);
               val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
               val var10000: Function2 = this.$c;
               this.label = 1;
               if (var10000.invoke(`$this$flow`, this) === var3x) {
                  return var3x;
               }
               break;
            case 1:
               ResultKt.throwOnFailure(`$result`);
               break;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         return Unit.INSTANCE;
      }

      public final Object invokeSuspend$$forInline(Object $result) {
         this.$c.invoke(this.L$0 as FlowCollector, this);
         return Unit.INSTANCE;
      }

      public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
         val var3: Function2 = new <anonymous constructor>(this.$c, `$completion`);
         var3.L$0 = value;
         return var3 as Continuation<Unit>;
      }

      public final Object invoke(FlowCollector<? super T> p1, Continuation<? super Unit> p2) {
         return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
      }
   }) as Function2);
}

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getValueField(obj: IHeapValues<IValue>, valueField: SootField): IHeapValues<IValue> {
   IFact.Builder.DefaultImpls.assignNewExpr$default(
      `$this$getValueField`.getOut() as IFact.Builder, `$this$getValueField`.getEnv(), "@obj", obj, false, 8, null
   );
   val var10000: IFact.Builder = `$this$getValueField`.getOut() as IFact.Builder;
   val var10001: HeapValuesEnv = `$this$getValueField`.getEnv();
   val r: FieldUtil = FieldUtil.INSTANCE;
   var10000.getField(var10001, "@obj.value", "@obj", new JSootFieldType(valueField), true);
   val var5: IHeapValues = (`$this$getValueField`.getOut() as IFact.Builder).getTargetsUnsafe("@obj.value");
   (`$this$getValueField`.getOut() as IFact.Builder).kill("@obj");
   (`$this$getValueField`.getOut() as IFact.Builder).kill("@obj.value");
   var var6: IHeapValues = var5;
   if (var5 == null) {
      var6 = `$this$getValueField`.getHf().empty();
   }

   return var6;
}

public fun ICallCBImpl<IHeapValues<IValue>, Builder<IValue>>.getConstantValue(obj: IHeapValues<IValue>, valueField: SootField): IHeapValues<IValue> {
   val res: IHeapValues.Builder = `$this$getConstantValue`.getHf().emptyBuilder();
   res.add(getValueField(`$this$getConstantValue`, obj, valueField));

   for (CompanionV o : obj) {
      if (o.getValue() is ConstVal) {
         res.add(o);
      }
   }

   return res.build();
}
