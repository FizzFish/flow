package cn.sast.dataflow.interprocedural.analysis

import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries
import soot.RefType
import soot.Type

/**
 * 抽象语义值接口。
 */
interface IValue {

   /** 该值的 Soot 类型 */
   val type: Type

   /** `type` 是否为具体类型（而非 `T`, `?`, `*` 之类的抽象） */
   fun typeIsConcrete(): Boolean

   /** 是否显式为 `null` 常量 */
   fun isNullConstant(): Boolean

   /** 语义种类 */
   fun getKind(): Kind

   /* ---------- 默认实现 ---------- */

   /** `Kind.Normal` → `true`；其余 `false` */
   fun isNormal(): Boolean = getKind() == Kind.Normal

   /**
    * 当且仅当
    * 1. `type` 是 [RefType]；且
    * 2. 与 `b` 引用相同实例
    * 返回 `true`，否则 `null`（表示未知）。
    */
   fun objectEqual(b: IValue): Boolean? =
      if (type is RefType && this === b) true else null

   /** 默认返回自身（不可变值） */
   fun clone(): IValue = this

   /** 若未覆写，复制时仅替换类型信息 */
   fun copy(type: Type): IValue = this

   /* ---------- 辅助：兼容旧 Java 调用 ---------- */
   object DefaultImpls {
      @JvmStatic fun isNormal(self: IValue): Boolean = self.isNormal()
      @JvmStatic fun objectEqual(self: IValue, b: IValue): Boolean? = self.objectEqual(b)
      @JvmStatic fun clone(self: IValue): IValue = self.clone()
      @JvmStatic fun copy(self: IValue, type: Type): IValue = self.copy(type)
   }

   /* ---------- 枚举 ---------- */
   enum class Kind {
      Normal,
      Entry,
      Summary;

      /** 与 Kotlin 生成的 `ENTRIES` 常量等价，方便 Java 访问 */
      companion object {
         @JvmStatic
         fun getEntries(): EnumEntries<Kind> = enumEntries()
      }
   }
}
