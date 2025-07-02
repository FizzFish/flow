package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IValue
import soot.Unit

public class SetEdgePath private constructor(node: Unit,
   heapObject: IValue,
   heapObjectPath: IPath,
   mt: Any,
   key: Any?,
   value: IValue,
   valuePath: IPath,
   info: Any?
) : IPath() {
   public open val node: Unit
   public final val heapObject: IValue
   public final val heapObjectPath: IPath
   public final val mt: Any
   public final val key: Any?
   public final val value: IValue
   public final val valuePath: IPath
   public final val info: Any?

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.heapObject = heapObject;
      this.heapObjectPath = heapObjectPath;
      this.mt = mt;
      this.key = key;
      this.value = value;
      this.valuePath = valuePath;
      this.info = info;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SetEdgePath) {
         return false;
      } else if (this.equivHashCode() != (other as SetEdgePath).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as SetEdgePath).getNode()) {
         return false;
      } else if (!(this.heapObject == (other as SetEdgePath).heapObject)) {
         return false;
      } else if (this.heapObjectPath != (other as SetEdgePath).heapObjectPath) {
         return false;
      } else if (!(this.mt == (other as SetEdgePath).mt)) {
         return false;
      } else if (!(this.key == (other as SetEdgePath).key)) {
         return false;
      } else if (!(this.value == (other as SetEdgePath).value)) {
         return false;
      } else if (this.valuePath != (other as SetEdgePath).valuePath) {
         return false;
      } else {
         return this.info == (other as SetEdgePath).info;
      }
   }

   public override fun equivHashCode(): Int {
      var result: Int = this.hash;
      if (this.hash == null) {
         result = 31
               * Integer.valueOf(
                  31
                        * Integer.valueOf(
                           31
                                 * Integer.valueOf(
                                    31
                                          * Integer.valueOf(
                                             31
                                                   * Integer.valueOf(
                                                      31
                                                            * Integer.valueOf(
                                                               31 * Integer.valueOf(System.identityHashCode(this.getNode())) + this.heapObject.hashCode()
                                                            )
                                                         + this.heapObjectPath.hashCode()
                                                   )
                                                + this.mt.hashCode()
                                          )
                                       + (if (this.key != null) this.key.hashCode() else 0)
                                 )
                              + this.value.hashCode()
                        )
                     + this.valuePath.hashCode()
               )
            + (if (this.info != null) this.info.hashCode() else 0);
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, heapObject: IValue, heapObjectPath: IPath, mt: Any, key: Any?, v: IValue, value: IPath, info: Any? = null): SetEdgePath {
         return IPath.Companion.getInterner(new SetEdgePath(env.getNode(), heapObject, heapObjectPath, mt, key, v, value, info, null));
      }
   }
}
