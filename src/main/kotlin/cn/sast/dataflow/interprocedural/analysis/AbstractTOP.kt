package cn.sast.dataflow.interprocedural.analysis

/**
 * “常量” TOP；与 BOTTOM 相对。
 */
abstract class AbstractTOP<V>(
   override val hf: AbstractHeapFactory<V>
) : InValidFact<V>() {

   override fun isBottom() = false
   override fun isTop()    = true
   override fun isValid()  = false

   override fun toString() = "IFact: TOP"

   override fun equals(other: Any?): Boolean =
      other is IFact<*> && other.isTop()

   override fun hashCode(): Int = 1

   /* 其余操作默认返回空值 / 不执行逻辑 */
   override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any> =
      hf.empty()

   override fun diff(cmp: IDiff<Any>, that: IFact<Any>) = Unit
}
