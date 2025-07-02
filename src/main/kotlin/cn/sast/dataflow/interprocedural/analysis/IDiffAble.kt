package cn.sast.dataflow.interprocedural.analysis

public interface IDiffAble<V> {
   public abstract fun diff(cmp: IDiff<Any>, that: IDiffAble<out Any?>) {
   }
}
