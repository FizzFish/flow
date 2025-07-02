package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.IValue

internal class CompanionValueImpl1(value: IValue, path: IPath) : CompanionV(value), PathCompanionV {
   public open val path: IPath

   init {
      this.path = path;
   }

   public override fun union(other: CompanionV<IValue>): CompanionV<IValue> {
      if (!(this.getValue() == other.getValue())) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         return new CompanionValueImpl1(
            this.getValue(), MergePath.Companion.v(new HeapValuesEnvImpl(this.getPath()), this.getPath(), (other as PathCompanionV).getPath())
         );
      }
   }

   public override fun toString(): String {
      return "<${this.getValue()}>";
   }

   public open fun copy(updateValue: IValue): CompanionV<IValue> {
      return new CompanionValueImpl1(updateValue, this.getPath());
   }
}
