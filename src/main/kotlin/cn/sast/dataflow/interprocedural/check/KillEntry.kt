package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.ReferenceContext
import java.util.LinkedHashSet
import kotlinx.collections.immutable.PersistentList
import soot.SootMethod

public class KillEntry(method: SootMethod, env: HeapValuesEnv) {
   public final val method: SootMethod
   public final val env: HeapValuesEnv
   public final val entries: MutableSet<EntryPath>
   public final val factory: IReNew<IValue>

   init {
      this.method = method;
      this.env = env;
      this.entries = new LinkedHashSet<>();
      this.factory = new KillEntry.EntryReplace(this, null, 1, null);
   }

   internal inner class EntryReplace(special: PersistentList<ReferenceContext> = ...) : IReNew<IValue> {
      public final val special: PersistentList<ReferenceContext>

      init {
         this.this$0 = `this$0`;
         this.special = special;
      }

      public override fun checkNeedReplace(c: CompanionV<IValue>): CompanionV<IValue> {
         val p: EntryPath = EntryPath.Companion.v(this.special, this.this$0.getMethod(), this.this$0.getEnv());
         this.this$0.getEntries().add(p);
         if (c is CompanionValueOfConst) {
            return new CompanionValueOfConst((c as CompanionValueOfConst).getValue(), p, (c as CompanionValueOfConst).getAttr());
         } else if (c is CompanionValueImpl1) {
            return new CompanionValueImpl1((c as CompanionValueImpl1).getValue(), p);
         } else {
            throw new NotImplementedError(null, 1, null);
         }
      }

      public override fun context(value: Any): IReNew<IValue> {
         return this.this$0.new EntryReplace(this.this$0, this.special.add(value as ReferenceContext));
      }

      fun checkNeedReplace(old: IValue): IValue? {
         return IReNew.DefaultImpls.checkNeedReplace(this, old);
      }
   }
}
