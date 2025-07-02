package cn.sast.framework.entries.component

import cn.sast.framework.entries.utils.PhantomValueForType
import soot.Body
import soot.Local
import soot.LocalGenerator
import soot.SootClass
import soot.Type
import soot.Value
import soot.jimple.infoflow.entryPointCreators.SequentialEntryPointCreator

public class ComponentEntryPointCreator(entry: Collection<String>) : SequentialEntryPointCreator(entry) {
   private final val p: PhantomValueForType = new PhantomValueForType(null, 1, null)

   protected open fun getValueForType(
      tp: Type,
      constructionStack: MutableSet<SootClass>?,
      parentClasses: Set<SootClass>?,
      generatedLocals: MutableSet<Local>?,
      ignoreExcludes: Boolean
   ): Value? {
      val var10000: PhantomValueForType = this.p;
      val var10001: Body = this.body;
      val var10002: LocalGenerator = this.generator;
      return var10000.getValueForType(var10001, var10002, tp) as Value;
   }
}
