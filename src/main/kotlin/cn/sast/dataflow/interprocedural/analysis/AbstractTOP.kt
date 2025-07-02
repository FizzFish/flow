package cn.sast.dataflow.interprocedural.analysis

public abstract class AbstractTOP<V> : InValidFact<V> {
   public open val hf: AbstractHeapFactory<Any>

   open fun AbstractTOP(hf: AbstractHeapFactory<V>) {
      this.hf = hf;
   }

   public override fun isBottom(): Boolean {
      return false;
   }

   public override fun isTop(): Boolean {
      return true;
   }

   public override fun isValid(): Boolean {
      return false;
   }

   public override fun toString(): String {
      return "IFact: TOP";
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is IFact) {
         return false;
      } else {
         return this.isTop() && (other as IFact).isTop();
      }
   }

   public override fun hashCode(): Int {
      return 1;
   }

   public override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any> {
      return this.getHf().empty();
   }

   public override fun diff(cmp: IDiff<Any>, that: IFact<Any>) {
   }
}
