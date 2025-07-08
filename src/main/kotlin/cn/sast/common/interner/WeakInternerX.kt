package cn.sast.common.interner

import com.google.common.base.Equivalence
import com.google.common.collect.Interners
import com.google.common.collect.Interner

/**
 * Kotlin 封装版：基于 Guava WeakInterner。
 * 用于对值对象做弱引用去重。
 */
class WeakInternerX<E : InternerEquiv>(
   keyEquivalence: Equivalence<InternerEquiv> = Equivalence.equals() as Equivalence<InternerEquiv>
) {

   /** 实际的弱引用 interner */
   private val interner: Interner<InternerEquiv> = Interners.newWeakInterner()

   /**
    * 对外暴露：对输入的值做 Intern，返回去重后共享的实例。
    */
   fun intern(value: E): E {
      @Suppress("UNCHECKED_CAST")
      return interner.intern(value) as E
   }

   /** 可选：暴露 Equivalence（如果需要） */
   val equivalence: Equivalence<InternerEquiv> = keyEquivalence
}
