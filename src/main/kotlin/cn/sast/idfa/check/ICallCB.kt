package cn.sast.idfa.check

import soot.SootMethod
import soot.jimple.InvokeExpr
import soot.jimple.Stmt

/**
 * 函数调用相关的公共回调上下文
 *
 * @param V 全局/静态环境对象类型
 * @param R 返回值类型
 */
interface ICallCB<V, R> {

   /** 可跨调用链共享的全局数据 */
   val global: V

   /** 函数返回值（实现类在合适时机写入） */
   var `return`: R

   /** 调用点所在的 `this`（若为静态方法可为空） */
   val `this`: Any?

   /** 调用结束后写回的 out 变量（常见于 JNI / C 形参 out-param） */
   var out: Any?

   /** 取第 `argIndex` 个参数的 **Value**（如 Local / Constant 等） */
   fun arg(argIndex: Int): Any?

   /** 将第 `argIndex` 个参数解析成实际 **运行期对象** */
   fun argToValue(argIndex: Int): Any?
}

/**
 * 侧重 **被调用者** 视角的回调
 */
interface ICalleeCB<V, R> : IStmtCB, ICallCB<V, R> {

   /** 被调用的方法 */
   val callee: SootMethod

   /* ---------- 扩展细分 ---------- */

   interface IEvalCall<V, R> : ICalleeCB<V, R>, IEvalCallCB<V, R>
   interface IPostCall<V, R> : ICalleeCB<V, R>, IPostCallCB<V, R>
   interface IPrevCall<V, R> : ICalleeCB<V, R>, IPrevCB
}

/**
 * 侧重 **调用点** 视角的回调
 */
interface ICallerSiteCB<V, R> : IStmtCB, ICallCB<V, R> {

   /** 调用点所在的方法 */
   val caller: SootMethod

   /* ---------- 扩展细分 ---------- */

   interface IEvalCall<V, R> : ICallerSiteCB<V, R>, IEvalCallCB<V, R>
   interface IPostCall<V, R> : ICallerSiteCB<V, R>, IPostCallCB<V, R>
   interface IPrevCall<V, R> : ICallerSiteCB<V, R>, IPrevCB
}

/** 所有 “求值” 回调的共同父接口（占位符） */
interface IEvalCB : IStmtCB

/**
 * “求值” 阶段专用回调
 */
interface IEvalCallCB<V, R> : IEvalCB, ICallCB<V, R> {
   var isEvalAble: Boolean
}


/**
 * 针对 `invoke` 语句（包含表达式）的回调
 */
interface IInvokeStmtCB<V, R> : ICallerSiteCB<V, R> {

   /** 当前语句携带的 `InvokeExpr` */
   val invokeExpr: InvokeExpr
      get() = stmt.invokeExpr   // `stmt` 由上层 IStmtCB 暴露

   companion object {
      /** 与反射时的 “静态工具” 行为一致 */
      @JvmStatic
      fun <V, R> getInvokeExpr(receiver: IInvokeStmtCB<V, R>): InvokeExpr =
         receiver.invokeExpr
   }
}

interface IPostCB : IStmtCB

interface IPostCallCB<V, R> : IPostCB, ICallCB<V, R>

interface IPrevCB : IStmtCB

interface IStmtCB {
   val stmt: Stmt
}

