package cn.sast.dataflow.interprocedural.analysis

import cn.sast.dataflow.interprocedural.analysis.heapimpl.IArrayHeapKV
import cn.sast.dataflow.interprocedural.check.BuiltInModelT
import kotlinx.collections.immutable.ImmutableSet
import mu.KLogger
import mu.KotlinLogging
import soot.Local
import soot.RefLikeType
import soot.SootMethod
import soot.Type
import soot.Value
import soot.jimple.Constant

public interface IIFact<V> {
   public val hf: AbstractHeapFactory<Any>
   public val callStack: CallStackContext

   public abstract fun getValueData(v: Any, mt: Any): IData<Any>? {
   }

   public open fun getTargets(slot: Any): IHeapValues<Any> {
   }

   public abstract fun getTargetsUnsafe(slot: Any): IHeapValues<Any>? {
   }

   public abstract fun getSlots(): Set<Any> {
   }

   public abstract fun getCalledMethods(): ImmutableSet<SootMethod> {
   }

   public abstract fun isBottom(): Boolean {
   }

   public abstract fun isTop(): Boolean {
   }

   public open fun isValid(): Boolean {
   }

   public abstract fun getOfSlot(env: HeapValuesEnv, slot: Any): IHeapValues<Any>? {
   }

   public open fun getArrayLength(array: Any): IHeapValues<Any>? {
   }

   public open fun getArray(array: Any): IArrayHeapKV<Int, Any>? {
   }

   public open fun getOfSootValue(env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<Any> {
   }

   public companion object {
      public final val logger: KLogger = KotlinLogging.INSTANCE.logger(IIFact.Companion::logger$lambda$0)

      @JvmStatic
      fun `logger$lambda$0`(): Unit {
         return Unit.INSTANCE;
      }
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V> getTargets(`$this`: IIFact<V>, slot: Any): IHeapValues<V> {
         var var10000: IHeapValues = `$this`.getTargetsUnsafe(slot);
         if (var10000 == null) {
            var10000 = `$this`.getHf().empty();
         }

         return var10000;
      }

      @JvmStatic
      fun <V> isValid(`$this`: IIFact<V>): Boolean {
         return !`$this`.isTop() && !`$this`.isBottom();
      }

      @JvmStatic
      fun <V> getArrayLength(`$this`: IIFact<V>, array: V): IHeapValues<V>? {
         val var10000: IArrayHeapKV = `$this`.getArray(array);
         return if (var10000 != null) var10000.getArrayLength() else null;
      }

      @JvmStatic
      fun <V> getArray(`$this`: IIFact<V>, array: V): IArrayHeapKV<Integer, V>? {
         val var2: IData = `$this`.getValueData(array, BuiltInModelT.Array);
         return var2 as? IArrayHeapKV;
      }

      @JvmStatic
      fun <V> getOfSootValue(`$this`: IIFact<V>, env: HeapValuesEnv, value: Value, valueType: Type): IHeapValues<V> {
         val var6: IHeapValues;
         if (value is Constant) {
            val type: Type = if ((value as Constant).getType() is RefLikeType) (value as Constant).getType() else valueType;
            val var10000: AbstractHeapFactory = `$this`.getHf();
            val var10002: AbstractHeapFactory = `$this`.getHf();
            val var10003: Constant = value as Constant;
            var6 = JOperatorV.DefaultImpls.markOfConstant$default(var10000.push(env, var10002.newConstVal(var10003, type)), value as Constant, null, 2, null)
               .popHV();
         } else {
            if (value !is Local) {
               throw new IllegalStateException((IIFact.DefaultImpls::getOfSootValue$lambda$0).toString());
            }

            var6 = `$this`.getTargets(value);
         }

         return var6;
      }

      @JvmStatic
      fun `getOfSootValue$lambda$0`(`$value`: Value): java.lang.String {
         return "error soot.Value: $`$value`";
      }
   }
}
