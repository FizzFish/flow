package cn.sast.dataflow.interprocedural.analysis

import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentList
import soot.Type

public interface IFieldManager<V extends IValue> : IValue {
   public abstract fun getPhantomFieldMap(): MutableMap<PersistentList<JFieldType>, PhantomField<Any>> {
   }

   public open fun getPhantomField(field: JFieldType): PhantomField<Any> {
   }

   public open fun getPhantomField(ap: PersistentList<JFieldType>): PhantomField<Any> {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V extends IValue> getPhantomField(`$this`: IFieldManager<V>, field: JFieldType): PhantomField<V> {
         return `$this`.getPhantomField(ExtensionsKt.persistentListOf(new JFieldType[]{field}));
      }

      @JvmStatic
      fun <V extends IValue> getPhantomField(`$this`: IFieldManager<V>, ap: PersistentList<? extends JFieldType>): PhantomField<V> {
         label25: {
            synchronized (`$this`){} // $VF: monitorenter 

            label22: {
               try {
                  val map: java.util.Map = `$this`.getPhantomFieldMap();
                  if (map.get(ap) as PhantomField != null) {
                     break label22;
                  }

                  map.put(ap, new PhantomField(`$this`, ap));
               } catch (var9: java.lang.Throwable) {
                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }

            // $VF: monitorexit
         }
      }

      @JvmStatic
      fun <V extends IValue> isNormal(`$this`: IFieldManager<V>): Boolean {
         return IValue.DefaultImpls.isNormal(`$this`);
      }

      @JvmStatic
      fun <V extends IValue> objectEqual(`$this`: IFieldManager<V>, b: IValue): java.lang.Boolean? {
         return IValue.DefaultImpls.objectEqual(`$this`, b);
      }

      @JvmStatic
      fun <V extends IValue> clone(`$this`: IFieldManager<V>): IValue {
         return IValue.DefaultImpls.clone(`$this`);
      }

      @JvmStatic
      fun <V extends IValue> copy(`$this`: IFieldManager<V>, type: Type): IValue {
         return IValue.DefaultImpls.copy(`$this`, type);
      }
   }
}
