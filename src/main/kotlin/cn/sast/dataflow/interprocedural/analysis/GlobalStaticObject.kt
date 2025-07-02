package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.PersistentList
import soot.Scene
import soot.Type
import java.util.LinkedHashMap

/**
 * 用于表示“单例全局变量”的抽象值。
 */
open class GlobalStaticObject(
   override val type: Type = Scene.v().objectType
) : IValue, IFieldManager<GlobalStaticObject> {

   private val fieldObjects: MutableMap<PersistentList<JFieldType>, PhantomField<GlobalStaticObject>> =
      LinkedHashMap()

   /* ---------- IFieldManager ---------- */

   override fun getPhantomFieldMap():
           MutableMap<PersistentList<JFieldType>, PhantomField<GlobalStaticObject>> = fieldObjects

   /* ---------- IValue ---------- */

   override fun typeIsConcrete(): Boolean = true
   override fun isNullConstant(): Boolean = false
   override fun getKind(): IValue.Kind = IValue.Kind.Entry

   /* ---------- 基础 ---------- */

   override fun toString(): String = "GlobalStaticObject"

   override fun equals(other: Any?): Boolean = other is GlobalStaticObject
   override fun hashCode(): Int = 30_864
}
