package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import java.util.LinkedHashSet
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod

public class KillEntry(public val method: SootMethod, public val env: HeapValuesEnv) {
    public val entries: MutableSet<EntryPath> = LinkedHashSet()
    public val factory: IReNew<IValue> = EntryReplace()

    internal inner class EntryReplace(
        private val special: PersistentList<ReferenceContext> = TODO("FIXME — initialize special properly")
    ) : IReNew<IValue> {
        override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue> {
            val p = EntryPath.Companion.v(special, this@KillEntry.method, this@KillEntry.env)
            this@KillEntry.entries.add(p)
            return when (c) {
                is CompanionValueOfConst -> CompanionValueOfConst(c.getValue(), p, c.getAttr())
                is CompanionValueImpl1 -> CompanionValueImpl1(c.getValue(), p)
                else -> throw NotImplementedError()
            }
        }

        override fun context(value: Any): IReNew<IValue> {
            return EntryReplace(special.add(value as ReferenceContext))
        }

        override fun checkNeedReplace(old: IValue): IValue? {
            return IReNew.DefaultImpls.checkNeedReplace(this, old)
        }
    }
}