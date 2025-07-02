package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.StmtModelingEnv
import soot.Unit

public class ModelBind private constructor(node: Unit, obj: IValue, mt: Any, data: Any, info: ModelingStmtInfo?, prev: IPath) : IPath() {
   public open val node: Unit
   public final val obj: IValue
   public final val mt: Any
   public final val data: Any
   public final val info: ModelingStmtInfo?
   public final val prev: IPath

   public final var hash: Int?
      internal set

   init {
      this.node = node;
      this.obj = obj;
      this.mt = mt;
      this.data = data;
      this.info = info;
      this.prev = prev;
   }

   public override fun equivTo(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ModelBind) {
         return false;
      } else if (this.equivHashCode() != (other as ModelBind).equivHashCode()) {
         return false;
      } else if (this.getNode() != (other as ModelBind).getNode()) {
         return false;
      } else if (!(this.obj == (other as ModelBind).obj)) {
         return false;
      } else if (!(this.mt == (other as ModelBind).mt)) {
         return false;
      } else if (!(this.data == (other as ModelBind).data)) {
         return false;
      } else if (!(this.info == (other as ModelBind).info)) {
         return false;
      } else {
         return this.prev === (other as ModelBind).prev;
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
                                    31 * Integer.valueOf(31 * Integer.valueOf(System.identityHashCode(this.getNode())) + this.obj.hashCode())
                                       + this.mt.hashCode()
                                 )
                              + this.data.hashCode()
                        )
                     + (if (this.info != null) this.info.hashCode() else 0)
               )
            + this.prev.hashCode();
         this.hash = result;
      }

      return result;
   }

   public companion object {
      public fun v(env: HeapValuesEnv, obj: IValue, mt: Any, data: IData<IValue>, prev: IPath): ModelBind {
         return IPath.Companion
            .getInterner(
               new ModelBind(env.getNode(), obj, mt, mt, if ((env as? StmtModelingEnv) != null) (env as? StmtModelingEnv).getInfo() else null, prev, null)
            );
      }
   }
}
