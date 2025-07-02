package cn.sast.dataflow.interprocedural.analysis

/**
 * 克隆-重映射过程中的“引用场景”标记。
 */
sealed class ReferenceContext {

   /* ---------- 数组 ---------- */

   data object ArrayElement : ReferenceContext()
   data object ArrayInitialized : ReferenceContext()
   data object ArraySize : ReferenceContext()

   /* ---------- KV ---------- */

   data class KVPosition(val key: Any) : ReferenceContext()
   data object KVUnreferenced : ReferenceContext()

   /* ---------- 对象 ---------- */

   data class ObjectValues(val value: Any) : ReferenceContext()

   /* ---------- PTG ---------- */

   data class PTG(val obj: Any, val mt: Any) : ReferenceContext()

   /* ---------- Slot ---------- */

   data class Slot(val slot: Any) : ReferenceContext()
}
