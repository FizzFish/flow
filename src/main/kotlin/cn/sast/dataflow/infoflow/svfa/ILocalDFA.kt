package cn.sast.dataflow.infoflow.svfa

import soot.Unit

/**
 * 针对单个方法（局部图）的 Data-Flow Analysis 抽象。
 *
 * - **getUsesOfAt**   : 定义 → 使用
 * - **getDefUsesOfAt**: 使用 → 定义
 */
internal interface ILocalDFA {

   fun getUsesOfAt(ap: AP, stmt: Unit): List<Unit>

   fun getDefUsesOfAt(ap: AP, stmt: Unit): List<Unit>
}
