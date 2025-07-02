package cn.sast.dataflow.interprocedural.analysis

/**
 * 堆模型中 “元素 + 伴随数据” 的抽象基类。
 *
 * @param V 伴随值的实际类型
 * @property value 元素自身的值
 */
abstract class CompanionV<V : Any?>(
   val value: V
) {

   /** 懒缓存的哈希值（不可参与 equals 判断） */
   private var _hashCode: Int? = null

   /* ---------- 扩展点（子类实现） ---------- */

   /** 与 [other] 合并，返回新的 `CompanionV` */
   abstract fun union(other: CompanionV<*>): CompanionV<*>

   /** 复制一个 *value 被替换* 的副本 */
   abstract fun copy(updateValue: V): CompanionV<V>

   /* ---------- 可覆写：计算哈希 ---------- */

   protected open fun computeHash(): Int =
      (value?.hashCode() ?: 0) + 23_342_879

   /* ---------- Object 基础 ---------- */

   override fun toString(): String = value.toString()

   override fun equals(other: Any?): Boolean =
      this === other || (other is CompanionV<*> && value == other.value)

   override fun hashCode(): Int =
      _hashCode ?: computeHash().also { _hashCode = it }
}
