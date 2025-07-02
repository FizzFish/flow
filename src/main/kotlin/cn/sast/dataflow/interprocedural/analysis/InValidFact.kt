package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.FixPointStatus
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.ImmutableSet
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value

@SourceDebugExtension(["SMAP\nIFact.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/InValidFact\n+ 2 FixPointStatus.kt\ncn/sast/idfa/analysis/FixPointStatus$Companion\n*L\n1#1,507:1\n10#2:508\n*S KotlinDebug\n*F\n+ 1 IFact.kt\ncn/sast/dataflow/interprocedural/analysis/InValidFact\n*L\n230#1:508\n*E\n"])
public abstract class InValidFact<V> : IFact<V> {
   public open val callStack: CallStackContext
      public open get() {
         throw new NotImplementedError("An operation is not implemented: Not yet implemented");
      }


   public override fun hasChange(context: Context<SootMethod, Unit, IFact<Any>>, new: IProblemIteratorTerminal<Any>): FixPointStatus {
      val `this_$iv`: FixPointStatus.Companion = FixPointStatus.Companion;
      return if (!(this == var2)) FixPointStatus.HasChange else FixPointStatus.Fixpoint;
   }

   public override fun getSlots(): Set<Any> {
      return SetsKt.emptySet();
   }

   public override fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? {
      return null;
   }

   public override fun getCalledMethods(): ImmutableSet<SootMethod> {
      return ExtensionsKt.persistentHashSetOf() as ImmutableSet<SootMethod>;
   }

   public override fun getValueData(v: Any, mt: Any): IData<Any>? {
      return null;
   }

   override fun getTargets(slot: Any): IHeapValues<V> {
      return IFact.DefaultImpls.getTargets(this, slot);
   }

   override fun isValid(): Boolean {
      return IFact.DefaultImpls.isValid(this);
   }

   override fun getArrayLength(array: V): IHeapValues<V>? {
      return IFact.DefaultImpls.getArrayLength(this, (V)array);
   }

   override fun getArray(array: V): IArrayHeapKV<Integer, V>? {
      return IFact.DefaultImpls.getArray(this, (V)array);
   }

   override fun getOfSootValue(env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
      return IFact.DefaultImpls.getOfSootValue(this, env, value, valueType);
   }
}
