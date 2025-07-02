package cn.sast.dataflow.infoflow.svfa

import soot.Unit

internal interface ILocalDFA {
   public abstract fun getUsesOfAt(ap: AP, stmt: Unit): List<Unit> {
   }

   public abstract fun getDefUsesOfAt(ap: AP, stmt: Unit): List<Unit> {
   }
}
