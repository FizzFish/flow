package cn.sast.idfa.analysis

import kotlin.enums.EnumEntries

// 移除冗余的 public 修饰符
enum class FixPointStatus {
   HasChange,
   Fixpoint,
   // 每个枚举项后添加逗号，移除无效的 @JvmStatic 注解
   NeedWideningOperators;

   // 移除错误的 Companion 对象初始化语句
   // 移除无效的 @JvmStatic 注解，因为它只能用于命名对象和伴生对象中的成员
   companion object {
      // 移除冗余的 public 修饰符和 inline 关键字
      fun of(hasChange: Boolean): FixPointStatus {
         // 移除冗余的限定符名称和分号
         return if (hasChange) HasChange else Fixpoint
      }

      fun getEntries(): EnumEntries<FixPointStatus> = entries
   }
}
