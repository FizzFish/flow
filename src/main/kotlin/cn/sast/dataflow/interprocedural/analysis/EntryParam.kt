package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IValue.Kind
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod
import soot.Type
import java.util.LinkedHashMap

/**
 * 表示“分析入口参数”这一特殊语义值。
 *
 * @param type       参数类型；`-1` 代表 `this`
 * @param method     所属方法
 * @param paramIndex 形参下标，`-1` 表示 `this`
 */
open class EntryParam(
   override val type: Type,
   val method: SootMethod,
   val paramIndex: Int
) : IValue,
   IFieldManager<EntryParam> {

   /** 伴随字段映射（惰性创建） */
   private val fieldObjects: MutableMap<
           PersistentList<JFieldType>,
           PhantomField<EntryParam>
           > = LinkedHashMap()

   /* ---------------- 辅助次构造 ---------------- */

   constructor(method: SootMethod, paramIndex: Int) : this(
      if (paramIndex != -1)
         method.getParameterType(paramIndex)
      else
         method.declaringClass.type,
      method,
      paramIndex
   )

   /* ---------------- IFieldManager ---------------- */

   fun getPhantomFieldMap():
           MutableMap<PersistentList<JFieldType>, PhantomField<EntryParam>> = fieldObjects

   /* ---------------- Object 基础 ---------------- */

   override fun equals(other: Any?): Boolean =
      this === other ||
              (other is EntryParam &&
                      method == other.method &&
                      paramIndex == other.paramIndex &&
                      type == other.type)

   override fun hashCode(): Int =
      31 * (31 * (31 + method.hashCode()) + paramIndex) + type.hashCode()

   override fun toString(): String = "param<$paramIndex>_$type"

   /* ---------------- IValue ---------------- */

   override fun typeIsConcrete(): Boolean = false

   override fun isNullConstant(): Boolean = false

   override fun getKind(): Kind = Kind.Entry

   override fun copy(type: Type): IValue =
      EntryParam(type, method, paramIndex)

   /* ---------- 默认实现转发 ---------- */

   override fun isNormal(): Boolean = IValue.DefaultImpls.isNormal(this)

   override fun objectEqual(b: IValue): Boolean? =
      IValue.DefaultImpls.objectEqual(this, b)

   override fun clone(): IValue = IValue.DefaultImpls.clone(this)

   /* ---------- IFieldManager 默认 ---------- */

   fun getPhantomField(field: JFieldType): PhantomField<EntryParam> =
      IFieldManager.DefaultImpls.getPhantomField(this, field)

   fun getPhantomField(
      ap: PersistentList<out JFieldType>
   ): PhantomField<EntryParam> =
      IFieldManager.DefaultImpls.getPhantomField(this, ap)
}
