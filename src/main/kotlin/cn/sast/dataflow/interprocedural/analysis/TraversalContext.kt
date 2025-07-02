package cn.sast.dataflow.interprocedural.analysis

import soot.jimple.*
import soot.jimple.internal.*

/**
 * 语句遍历回调接口，`V` 为值类型，`A` 为用户附带状态类型。
 */
interface TraversalContext<V, A> {

   val voidValue: V

   /* ---------- 各类语句 ---------- */

   fun traverseAssignStmt(current: JAssignStmt)
   fun traverseIdentityStmt(current: JIdentityStmt)
   fun traverseIfStmt(current: JIfStmt)
   fun traverseInvokeStmt(current: JInvokeStmt)
   fun traverseSwitchStmt(current: SwitchStmt)
   fun traverseThrowStmt(current: JThrowStmt)

   /* ---------- 产生结果 ---------- */

   fun processResult(current: MethodResult<V>)
   fun symbolicSuccess(stmt: ReturnStmt): MethodResult<V>

   /* ---------- 状态 ---------- */

   fun offerState(state: A)

   /* ---------- 通用分发 ---------- */

   fun traverseStmt(current: Stmt) {
      when (current) {
         is JAssignStmt       -> traverseAssignStmt(current)
         is JIdentityStmt     -> traverseIdentityStmt(current)
         is JIfStmt           -> traverseIfStmt(current)
         is JInvokeStmt       -> traverseInvokeStmt(current)
         is SwitchStmt        -> traverseSwitchStmt(current)
         is JReturnStmt       -> processResult(symbolicSuccess(current))
         is JReturnVoidStmt   -> processResult(SymbolicSuccess(voidValue))
         is JThrowStmt        -> traverseThrowStmt(current)
         is JRetStmt ->
            error("should be removed by Soot: $current")
         is DefinitionStmt ->
            error("unhandled DefinitionStmt: $current")
         else ->
            error("Unsupported stmt: ${current::class}")
      }
   }
}
