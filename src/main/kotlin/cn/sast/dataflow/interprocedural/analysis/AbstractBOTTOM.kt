package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.IFact.Builder
import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import kotlinx.collections.immutable.ImmutableSet
import soot.ArrayType
import soot.Context
import soot.Local
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value

public abstract class AbstractBOTTOM<V> : InValidFact<V> {
   public open val hf: AbstractHeapFactory<Any>
      public open get() {
         throw new NotImplementedError("An operation is not implemented: Not yet implemented");
      }


   public override fun isBottom(): Boolean {
      return true;
   }

   public override fun isTop(): Boolean {
      return false;
   }

   public override fun isValid(): Boolean {
      return false;
   }

   public override fun toString(): String {
      return "IFact: BOTTOM";
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is IFact) {
         return false;
      } else {
         return this.isBottom() && (other as IFact).isBottom();
      }
   }

   public override fun hashCode(): Int {
      return 2;
   }

   public override fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any> {
      throw new NotImplementedError("An operation is not implemented: Not yet implemented");
   }

   public override fun diff(cmp: IDiff<Any>, that: IFact<Any>) {
   }

   public override fun builder(): Builder<Any> {
      return new IFact.Builder<V>(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public CallStackContext getCallStack() {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void assignLocal(HeapValuesEnv env, Object lhs, Object rhs) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void assignNewExpr(HeapValuesEnv env, Object lhs, IHeapValues<V> allocSite, boolean append) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void setField(HeapValuesEnv env, Object lhs, JFieldType field, Object rhs, boolean append) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void setFieldNew(HeapValuesEnv env, Object lhs, JFieldType field, IHeapValues<V> allocSite) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void getField(HeapValuesEnv env, Object lhs, Object rhs, JFieldType field, boolean newSummaryField) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void summarizeTargetFields(Object lhs) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void union(IFact<V> that) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IHeapValues<V> updateIntraEdge(HeapValuesEnv env, Context ctx, Context calleeCtx, IFact<V> callEdgeValue, boolean hasReturnValue) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void kill(Object slot) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public java.util.Set<Local> getSlots() {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IFact<V> build() {
            return this.this$0;
         }

         @Override
         public void addCalledMethod(SootMethod sm) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public ImmutableSet<SootMethod> getCalledMethods() {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IHeapValues<V> getTargets(Object slot) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public boolean isBottom() {
            return true;
         }

         @Override
         public boolean isTop() {
            return false;
         }

         @Override
         public boolean isValid() {
            return false;
         }

         @Override
         public void setArraySootValue(HeapValuesEnv env, Object lhs, Value index, Value rhs, Type valueType) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void setArrayValueNew(HeapValuesEnv env, Object lhs, Value index, IHeapValues<V> allocSite) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public boolean getArrayValue(HeapValuesEnv env, Object lhs, Object rhs, Value index) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public boolean getArrayValue(HeapValuesEnv env, Object lhs, Value rhs, Value index) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void gc() {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void callEntryFlowFunction(cn.sast.idfa.analysis.Context<SootMethod, Unit, IFact<V>> context, SootMethod callee, Unit node, Unit succ) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void assignNewArray(HeapValuesEnv env, Object lhs, IHeapValues<V> allocSite, ArrayType type, Value size) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IData<V> getValueData(V v, Object mt) {
            return null;
         }

         @Override
         public IHeapValues<V> getOfSlot(HeapValuesEnv env, Object slot) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void setValueData(HeapValuesEnv env, V v, Object mt, IData<V> data) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IHeapValues<V> getArrayLength(V array) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void assignLocalSootValue(HeapValuesEnv env, Object lhs, Value rhs, Type valueType) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void assignFieldSootValue(HeapValuesEnv env, Object lhs, JFieldType field, Value rhs, Type valueType, boolean append) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IHeapValues<V> getOfSootValue(HeapValuesEnv env, Value value, Type valueType) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public AbstractHeapFactory<V> getHf() {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public void copyValueData(V from, V to) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IHeapValues<V> getTargetsUnsafe(Object slot) {
            throw new NotImplementedError("An operation is not implemented: Not yet implemented");
         }

         @Override
         public IArrayHeapKV<Integer, V> getArray(V array) {
            return IFact.Builder.DefaultImpls.getArray(this, (V)array);
         }
      };
   }
}
