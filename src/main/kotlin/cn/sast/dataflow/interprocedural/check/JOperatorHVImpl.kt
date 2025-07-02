package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorHV

internal data class JOperatorHVImpl(heapFactory: AbstractHeapFactory<IValue>, env: HeapValuesEnv, value: IHeapValues<IValue>) : JOperatorHV<IValue> {
   public final val heapFactory: AbstractHeapFactory<IValue>
   public final val env: HeapValuesEnv
   private final val value: IHeapValues<IValue>
   public final val pf: PathFactory<IValue>

   init {
      this.heapFactory = heapFactory;
      this.env = env;
      this.value = value;
      this.pf = this.heapFactory.getPathFactory();
   }

   public override fun pop(): IHeapValues<IValue> {
      return this.value;
   }

   public override fun <K> setKVValue(mt: Any, lhs: CompanionV<IValue>, key: K?): JOperatorHV<IValue> {
      return copy$default(this, null, null, this.pf.updatePath(this.env, this.value, JOperatorHVImpl::setKVValue$lambda$0), 3, null);
   }

   public override fun <K> getKVValue(mt: Any, rhs: CompanionV<IValue>, key: K?): JOperatorHV<IValue> {
      return copy$default(this, null, null, this.pf.updatePath(this.env, this.value, JOperatorHVImpl::getKVValue$lambda$1), 3, null);
   }

   public override fun assignLocal(lhs: Any, rhsValue: IHeapValues<IValue>): JOperatorHV<IValue> {
      return copy$default(this, null, null, this.pf.updatePath(this.env, this.value, JOperatorHVImpl::assignLocal$lambda$2), 3, null);
   }

   public override fun markOfArrayLength(rhs: CompanionV<IValue>): JOperatorHV<IValue> {
      return this as JOperatorHV<IValue>;
   }

   public override fun dataElementCopyToSequenceElement(sourceElement: IHeapValues<IValue>): JOperatorHV<IValue> {
      return this as JOperatorHV<IValue>;
   }

   public operator fun component1(): AbstractHeapFactory<IValue> {
      return this.heapFactory;
   }

   public operator fun component2(): HeapValuesEnv {
      return this.env;
   }

   private operator fun component3(): IHeapValues<IValue> {
      return this.value;
   }

   public fun copy(heapFactory: AbstractHeapFactory<IValue> = this.heapFactory, env: HeapValuesEnv = this.env, value: IHeapValues<IValue> = this.value): JOperatorHVImpl {
      return new JOperatorHVImpl(heapFactory, env, value);
   }

   public override fun toString(): String {
      return "JOperatorHVImpl(heapFactory=${this.heapFactory}, env=${this.env}, value=${this.value})";
   }

   public override fun hashCode(): Int {
      return (this.heapFactory.hashCode() * 31 + this.env.hashCode()) * 31 + this.value.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is JOperatorHVImpl) {
         return false;
      } else {
         val var2: JOperatorHVImpl = other as JOperatorHVImpl;
         if (!(this.heapFactory == (other as JOperatorHVImpl).heapFactory)) {
            return false;
         } else if (!(this.env == var2.env)) {
            return false;
         } else {
            return this.value == var2.value;
         }
      }
   }

   @JvmStatic
   fun `setKVValue$lambda$0`(`this$0`: JOperatorHVImpl, `$lhs`: CompanionV, `$mt`: Any, `$key`: Any, v: IValue, valuePath: IPath): IPath {
      val var10000: SetEdgePath.Companion = SetEdgePath.Companion;
      val var10001: HeapValuesEnv = `this$0`.env;
      val var10002: IValue = `$lhs`.getValue() as IValue;
      return SetEdgePath.Companion.v$default(var10000, var10001, var10002, (`$lhs` as PathCompanionV).getPath(), `$mt`, `$key`, v, valuePath, null, 128, null);
   }

   @JvmStatic
   fun `getKVValue$lambda$1`(`this$0`: JOperatorHVImpl, `$rhs`: CompanionV, `$mt`: Any, `$key`: Any, v: IValue, valuePath: IPath): IPath {
      val var10000: GetEdgePath.Companion = GetEdgePath.Companion;
      val var10001: HeapValuesEnv = `this$0`.env;
      val var10002: IValue = `$rhs`.getValue() as IValue;
      return GetEdgePath.Companion.v$default(var10000, var10001, var10002, (`$rhs` as PathCompanionV).getPath(), `$mt`, `$key`, v, valuePath, null, 128, null);
   }

   @JvmStatic
   fun `assignLocal$lambda$2`(`this$0`: JOperatorHVImpl, `$lhs`: Any, v: IValue, valuePath: IPath): IPath {
      return AssignLocalPath.Companion.v(`this$0`.env, `$lhs`, v, valuePath);
   }
}
