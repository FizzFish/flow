package cn.sast.dataflow.interprocedural.analysis

public interface IDiff<V> {
   public abstract fun diff(left: CompanionV<Any>, right: CompanionV<out Any?>) {
   }
}
