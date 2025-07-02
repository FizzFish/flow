package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import soot.ArrayType
import soot.Context
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value

public interface IFact<V> : IProblemIteratorTerminal<V>, IIFact<V> {
   public abstract fun builder(): cn.sast.dataflow.interprocedural.analysis.IFact.Builder<Any> {
   }

   public abstract fun diff(cmp: IDiff<Any>, that: IFact<Any>) {
   }

   public interface Builder<V> : IIFact<V> {
      public abstract fun copyValueData(from: Any, to: Any) {
      }

      public abstract fun setValueData(env: HeapValuesEnv, v: Any, mt: Any, data: IData<Any>?) {
      }

      public abstract fun assignLocal(env: HeapValuesEnv, lhs: Any, rhs: Any) {
      }

      public abstract fun assignLocalSootValue(env: HeapValuesEnv, lhs: Any, rhs: Value, valueType: Type) {
      }

      public abstract fun assignNewExpr(env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<Any>, append: Boolean = ...) {
      }

      public abstract fun assignNewArray(env: HeapValuesEnv, lhs: Any, allocSite: IHeapValues<Any>, type: ArrayType, size: Value) {
      }

      public abstract fun assignFieldSootValue(env: HeapValuesEnv, lhs: Any, field: JFieldType, rhs: Value, valueType: Type, append: Boolean = ...) {
      }

      public abstract fun setField(env: HeapValuesEnv, lhs: Any, field: JFieldType, rhs: Any, append: Boolean = ...) {
      }

      public abstract fun setFieldNew(env: HeapValuesEnv, lhs: Any, field: JFieldType, allocSite: IHeapValues<Any>) {
      }

      public abstract fun getField(env: HeapValuesEnv, lhs: Any, rhs: Any, field: JFieldType, newSummaryField: Boolean = ...) {
      }

      public abstract fun summarizeTargetFields(lhs: Any) {
      }

      public abstract fun union(that: IFact<Any>) {
      }

      public abstract fun updateIntraEdge(env: HeapValuesEnv, ctx: Context, calleeCtx: Context, callEdgeValue: IFact<Any>, hasReturnValue: Boolean): IHeapValues<
            Any
         >? {
      }

      public abstract fun kill(slot: Any) {
      }

      public abstract fun build(): IFact<Any> {
      }

      public abstract fun addCalledMethod(sm: SootMethod) {
      }

      public abstract fun setArraySootValue(env: HeapValuesEnv, lhs: Any, index: Value, rhs: Value, valueType: Type) {
      }

      public abstract fun setArrayValueNew(env: HeapValuesEnv, lhs: Any, index: Value?, allocSite: IHeapValues<Any>) {
      }

      public abstract fun getArrayValue(env: HeapValuesEnv, lhs: Any, rhs: Any, index: Value?): Boolean {
      }

      public abstract fun getArrayValue(env: HeapValuesEnv, lhs: Any, rhs: Value, index: Value?): Boolean {
      }

      public abstract fun callEntryFlowFunction(
         context: cn.sast.idfa.analysis.Context<SootMethod, Unit, IFact<Any>>,
         callee: SootMethod,
         node: Unit,
         succ: Unit
      ) {
      }

      public abstract fun gc() {
      }

      // $VF: Class flags could not be determined
      internal class DefaultImpls {
         @JvmStatic
         fun <V> getTargets(`$this`: IFactBuilder<V>, slot: Any): IHeapValues<V> {
            return IIFact.DefaultImpls.getTargets(`$this`, slot);
         }

         @JvmStatic
         fun <V> isValid(`$this`: IFactBuilder<V>): Boolean {
            return IIFact.DefaultImpls.isValid(`$this`);
         }

         @JvmStatic
         fun <V> getArrayLength(`$this`: IFactBuilder<V>, array: V): IHeapValues<V>? {
            return IIFact.DefaultImpls.getArrayLength(`$this`, (V)array);
         }

         @JvmStatic
         fun <V> getArray(`$this`: IFactBuilder<V>, array: V): IArrayHeapKV<Integer, V>? {
            return IIFact.DefaultImpls.getArray(`$this`, (V)array);
         }

         @JvmStatic
         fun <V> getOfSootValue(`$this`: IFactBuilder<V>, env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
            return IIFact.DefaultImpls.getOfSootValue(`$this`, env, value, valueType);
         }
      }
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V> getTargets(`$this`: IFact<V>, slot: Any): IHeapValues<V> {
         return IIFact.DefaultImpls.getTargets(`$this`, slot);
      }

      @JvmStatic
      fun <V> isValid(`$this`: IFact<V>): Boolean {
         return IIFact.DefaultImpls.isValid(`$this`);
      }

      @JvmStatic
      fun <V> getArrayLength(`$this`: IFact<V>, array: V): IHeapValues<V>? {
         return IIFact.DefaultImpls.getArrayLength(`$this`, (V)array);
      }

      @JvmStatic
      fun <V> getArray(`$this`: IFact<V>, array: V): IArrayHeapKV<Integer, V>? {
         return IIFact.DefaultImpls.getArray(`$this`, (V)array);
      }

      @JvmStatic
      fun <V> getOfSootValue(`$this`: IFact<V>, env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
         return IIFact.DefaultImpls.getOfSootValue(`$this`, env, value, valueType);
      }
   }
}
