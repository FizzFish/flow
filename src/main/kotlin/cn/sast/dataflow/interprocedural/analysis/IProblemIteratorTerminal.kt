package cn.sast.dataflow.interprocedural.analysis

import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.FixPointStatus
import soot.SootMethod
import soot.Unit

/**
 * 数据流 *增量迭代器* 的终点节点接口。
 */
interface IProblemIteratorTerminal<V> {

   /**
    * 与 [new] 对比，判断是否发生变化：
    * - 如果对象引用不同 → [FixPointStatus.HasChange]
    * - 否则          → [FixPointStatus.Fixpoint]
    */
   fun hasChange(
      context: Context<SootMethod, Unit, IFact<Any>>,
      new: IProblemIteratorTerminal<Any>
   ): FixPointStatus = if (this === new) FixPointStatus.Fixpoint
   else FixPointStatus.HasChange
}
