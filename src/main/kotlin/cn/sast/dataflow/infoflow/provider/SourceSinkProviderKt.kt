package cn.sast.dataflow.infoflow.provider

import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import soot.Scene
import soot.SootMethod
import soot.Type

/**
 * 计算 Loc 在当前方法中的“基类型”。
 *
 * - `this / field`   → 声明类类型
 * - `参数 n (n≥0)`   → 第 n 个参数类型
 * - `返回值`         → 返回类型
 * - `MGlobal`        → java.lang.Object
 */
fun SootMethod.baseType(loc: MLocal): Type? = when (loc) {
   is MParameter -> when (loc.index) {
      -1  -> declaringClass.type               // this / field
      in 0 until parameterCount -> getParameterType(loc.index)
      else -> null
   }
   is MReturn   -> returnType
   MGlobal      -> Scene.v().objectType
   else         -> error("Unrecognized MLocal subtype: $loc")
}
