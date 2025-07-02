package cn.sast.dataflow.interprocedural.analysis

public interface IReNew<V> {
   public open fun checkNeedReplace(old: Any): Any? {
   }

   public open fun checkNeedReplace(c: CompanionV<Any>): CompanionV<Any>? {
   }

   public open fun context(value: Any): IReNew<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V> checkNeedReplace(`$this`: IReNew<V>, old: V): V? {
         return null;
      }

      @JvmStatic
      fun <V> checkNeedReplace(`$this`: IReNew<V>, c: CompanionV<V>): CompanionV<V>? {
         return null;
      }

      @JvmStatic
      fun <V> context(`$this`: IReNew<V>, value: Any): IReNew<V> {
         return `$this`;
      }
   }
}
