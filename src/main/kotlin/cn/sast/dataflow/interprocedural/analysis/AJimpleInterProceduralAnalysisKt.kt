package cn.sast.dataflow.interprocedural.analysis

import soot.Body
import soot.Unit
import soot.Value
import soot.jimple.IdentityStmt
import soot.jimple.ParameterRef
import soot.jimple.ThisRef

/** 找到 _第 i_ 个参数（或 `this`=-1）所在的 **IdentityStmt** */
fun Body.getParameterUnit(i: Int): Unit? =
   units.find { u ->
      (u as? IdentityStmt)?.rightOp?.let { rop ->
         when (rop) {
            is ParameterRef -> rop.index == i
            is ThisRef      -> i == -1
            else            -> false
         }
      } ?: false
   }
