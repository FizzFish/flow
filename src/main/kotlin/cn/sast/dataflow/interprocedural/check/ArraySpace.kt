package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.FactValuesKt
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.heapimpl.ArrayHeapKV
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentMap
import soot.ArrayType
import soot.Type
import soot.jimple.Constant

@SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,612:1\n1#2:613\n1863#3,2:614\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace\n*L\n225#1:614,2\n*E\n"])
public open class ArraySpace internal constructor(element: PersistentMap<Int, IHeapValues<IValue>>,
   unreferenced: IHeapValues<IValue>?,
   type: ArrayType,
   allSize: IHeapValues<IValue>,
   size: Int?,
   initializedValue: CompanionV<IValue>?
) : ArrayHeapKV(element, unreferenced, allSize, type, size, initializedValue) {
   public open fun builder(): ArraySpaceBuilder {
      val var10003: kotlinx.collections.immutable.PersistentMap.Builder = this.getMap().builder();
      val var10004: IHeapValues = this.getUnreferenced();
      return new ArraySpaceBuilder(this, var10003, if (var10004 != null) var10004.builder() else null);
   }

   public override fun getElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
      val b: IHeapValues.Builder = hf.emptyBuilder();
      val var10000: IHeapValues = this.getUnreferenced();
      if (var10000 != null) {
         b.add(var10000);
      }

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         b.add(var10 as IHeapValues);
      }

      return b.build();
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
      val b: ArraySpaceBuilder = this.builder();
      b.cloneAndReNewObjects(re);
      return b.build();
   }

   public open fun getArray(hf: IHeapValuesFactory<IValue>): Array<IValue>? {
      if (this.getSize() == null) {
         return null;
      } else {
         var var2: Int = 0;
         val var3: Int = this.getSize();

         val var4: Array<IValue>;
         for (var4 = new IValue[var3]; var2 < var3; var2++) {
            val var10002: IHeapValues = this.get(hf, var2);
            if (var10002 == null) {
               return null;
            }

            if (!var10002.isSingle()) {
               return null;
            }

            var4[var2] = (IValue)var10002.getSingle().getValue();
         }

         return var4;
      }
   }

   public override fun getByteArray(hf: IHeapValuesFactory<IValue>): ByteArray? {
      val var10000: Array<IValue> = this.getArray(hf);
      if (var10000 == null) {
         return null;
      } else {
         val arr: Array<IValue> = var10000;
         var var3: Int = 0;
         val var4: Int = var10000.length;
         val var5: ByteArray = new byte[var10000.length];

         while (true) {
            if (var3 >= var4) {
               return var5;
            }

            val var7: IValue = arr[var3];
            val var10002: ConstVal = arr[var3] as? ConstVal;
            if ((var7 as? ConstVal) == null) {
               break;
            }

            val var8: java.lang.Byte = FactValuesKt.getByteValue(var10002, true);
            if (var8 == null) {
               break;
            }

            var5[var3] = var8;
            var3++;
         }

         return null;
      }
   }

   @SourceDebugExtension(["SMAP\nPointsToGraph.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,612:1\n13474#2,3:613\n*S KotlinDebug\n*F\n+ 1 PointsToGraph.kt\ncn/sast/dataflow/interprocedural/check/ArraySpace$Companion\n*L\n211#1:613,3\n*E\n"])
   public companion object {
      public fun v(
         hf: AbstractHeapFactory<IValue>,
         env: HeapValuesEnv,
         element: PersistentMap<Int, IHeapValues<IValue>>,
         unreferenced: IHeapValues<IValue>,
         type: ArrayType,
         allSize: IHeapValues<IValue>
      ): ArraySpace {
         val size: Int = allSize.getMaxInt(true);
         val var8: Pair = hf.getVg().zeroValue(type);
         val v: Constant = var8.component1() as Constant;
         return new ArraySpace(
            element, unreferenced, type, allSize, size, hf.push(env, hf.newConstVal(v, var8.component2() as Type)).markOfConstant(v, "array init value").pop()
         );
      }

      public fun <T : Number> v(
         hf: AbstractHeapFactory<IValue>,
         env: HeapValuesEnv,
         value: CompanionV<IValue>,
         array: Array<T>,
         type: ArrayType,
         allSize: IHeapValues<IValue>
      ): ArraySpace {
         val element: kotlinx.collections.immutable.PersistentMap.Builder = ExtensionsKt.persistentHashMapOf().builder();
         var `index$iv`: Int = 0;

         for (Object item$iv : array) {
            val index: Int = `index$iv`++;
            val var17: java.util.Map = element as java.util.Map;
            val var18: Int = index;
            val var10003: Constant = SootUtilsKt.constOf(`item$iv`).getFirst() as Constant;
            val var10004: Type = type.getElementType();
            var17.put(var18, hf.push(env, hf.newConstVal(var10003, var10004)).dataGetElementFromSequence(value).popHV());
         }

         return new ArraySpace(element.build(), hf.empty(), type, allSize, element.size(), null);
      }
   }
}
