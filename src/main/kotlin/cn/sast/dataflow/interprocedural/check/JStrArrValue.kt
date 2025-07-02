package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IDiff
import cn.sast.dataflow.interprocedural.analysis.IDiffAble
import cn.sast.dataflow.interprocedural.analysis.IHeapKVData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IHeapValuesFactory
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.ArrayType
import soot.ByteType
import soot.G
import soot.Type
import soot.Unit
import soot.jimple.Constant
import soot.jimple.IntConstant

@SourceDebugExtension(["SMAP\nHeapFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/JStrArrValue\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,1430:1\n13409#2,2:1431\n13409#2,2:1433\n*S KotlinDebug\n*F\n+ 1 HeapFactory.kt\ncn/sast/dataflow/interprocedural/check/JStrArrValue\n*L\n92#1:1431,2\n164#1:1433,2\n*E\n"])
public class JStrArrValue(node: Unit, hf: AbstractHeapFactory<IValue>, byteArray: ByteArray) : IArrayHeapKV<Integer, IValue> {
   public final val node: Unit
   public final val byteArray: ByteArray
   public open val type: ArrayType
   private final val arrayLength: IHeapValues<IValue>
   private final val byteArrayConstVal: Array<CompanionV<IValue>>

   init {
      this.node = node;
      this.byteArray = byteArray;
      this.type = hf.getVg().getBYTE_ARRAY_TYPE();
      val it: IntConstant = IntConstant.v(this.byteArray.length);
      val var10001: HeapValuesEnv = hf.env(this.node);
      this.arrayLength = JOperatorV.DefaultImpls.markOfConstant$default(
            hf.push(var10001, hf.newConstVal(it as Constant, this.getType() as Type)), it as Constant, null, 2, null
         )
         .popHV();
      var var11: Int = 0;
      val var12: Int = this.byteArray.length;

      val var13: Array<CompanionV>;
      for (var13 = new CompanionV[this.byteArray.length]; var11 < var12; var11++) {
         val v: IntConstant = IntConstant.v(this.byteArray[var11]);
         val var10003: Constant = v as Constant;
         val var10004: ByteType = G.v().soot_ByteType();
         var13[var11] = JOperatorV.DefaultImpls.markOfConstant$default(
               hf.push(hf.env(this.node), hf.newConstVal(var10003, var10004 as Type) as IValue), v as Constant, null, 2, null
            )
            .pop();
      }

      this.byteArrayConstVal = var13;
   }

   public open fun get(hf: IHeapValuesFactory<IValue>, key: Int?): IHeapValues<IValue>? {
      if (key != null) {
         return if (key >= 0 && key < this.byteArray.length) hf.single(this.byteArrayConstVal[key]) else null;
      } else {
         val b: IHeapValues.Builder = hf.emptyBuilder();

         val `$this$forEach$iv`: Any;
         for (Object element$iv : $this$forEach$iv) {
            b.add((CompanionV)`element$iv`);
         }

         return b.build();
      }
   }

   public override fun toString(): String {
      return "ImByteArray_${new java.lang.String(this.byteArray, Charsets.UTF_8)}";
   }

   public override fun builder(): IHeapKVData.Builder<Int, IValue> {
      return new IHeapKVData.Builder<Integer, IValue>(this) {
         {
            this.this$0 = `$receiver`;
         }

         public void set(IHeapValuesFactory<IValue> hf, HeapValuesEnv env, Integer key, IHeapValues<IValue> update, boolean append) {
            JStrArrValue.Companion.getLogger().error(<unrepresentable>::set$lambda$0);
         }

         @Override
         public void union(AbstractHeapFactory<IValue> hf, IData<IValue> that) {
            JStrArrValue.Companion.getLogger().error(<unrepresentable>::union$lambda$1);
         }

         @Override
         public void cloneAndReNewObjects(IReNew<IValue> re) {
         }

         @Override
         public IData<IValue> build() {
            return this.this$0;
         }

         private static final Object set$lambda$0(HeapValuesEnv $env, <unrepresentable> this$0) {
            return "$`$env` ${`this$0`.getClass().getSimpleName()} is immutable!!!";
         }

         private static final Object union$lambda$1(<unrepresentable> this$0) {
            return "${`this$0`.getClass().getSimpleName()} is immutable!!! has no union";
         }
      };
   }

   public override fun reference(res: MutableCollection<IValue>) {
   }

   public override fun computeHash(): Int {
      return 1138 + Arrays.hashCode(this.byteArray);
   }

   public override fun diff(cmp: IDiff<IValue>, that: IDiffAble<out Any?>) {
   }

   public override fun hashCode(): Int {
      return this.computeHash();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is JStrArrValue) {
         return false;
      } else {
         return this.hashCode() == (other as JStrArrValue).hashCode() && Arrays.equals(this.byteArray, (other as JStrArrValue).byteArray);
      }
   }

   public override fun cloneAndReNewObjects(re: IReNew<IValue>): IData<IValue> {
      return this as IData<IValue>;
   }

   public override fun getArrayLength(): IHeapValues<IValue> {
      return this.arrayLength;
   }

   public override fun getElement(hf: AbstractHeapFactory<IValue>): IHeapValues<IValue> {
      val b: IHeapValues.Builder = hf.emptyBuilder();

      val `$this$forEach$iv`: Any;
      for (Object element$iv : $this$forEach$iv) {
         b.add((CompanionV)`element$iv`);
      }

      return b.build();
   }

   public open fun getArray(hf: IHeapValuesFactory<IValue>): Array<IValue> {
      var var2: Int = 0;
      val var3: Int = this.byteArrayConstVal.length;

      val var4: Array<IValue>;
      for (var4 = new IValue[this.byteArrayConstVal.length]; var2 < var3; var2++) {
         var4[var2] = this.byteArrayConstVal[var2].getValue();
      }

      return var4;
   }

   public override fun getByteArray(hf: IHeapValuesFactory<IValue>): ByteArray {
      return this.byteArray;
   }

   @JvmStatic
   fun `logger$lambda$4`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger
   }
}
