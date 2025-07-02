package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.ConstVal
import cn.sast.dataflow.interprocedural.analysis.IValue

internal class CompanionValueOfConst(value: IValue, path: IPath, attr: IValue) : CompanionV(value), PathCompanionV {
   public open val path: IPath
   public final val attr: IValue

   init {
      this.path = path;
      this.attr = attr;
      if (value !is ConstVal) {
         throw new IllegalStateException(java.lang.String.valueOf(value).toString());
      }
   }

   public open fun copy(updateValue: IValue): CompanionV<IValue> {
      return new CompanionValueOfConst(updateValue, this.getPath(), this.attr);
   }

   public override fun union(other: CompanionV<IValue>): CompanionV<IValue> {
      if (!(this.getValue() == other.getValue())) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         return new CompanionValueOfConst(
            this.getValue(), MergePath.Companion.v(new HeapValuesEnvImpl(this.getPath()), this.getPath(), (other as PathCompanionV).getPath()), this.attr
         );
      }
   }

   public override fun toString(): String {
      return "<v=${this.getValue()}, attr=${this.attr}>";
   }

   public override operator fun equals(other: Any?): Boolean {
      if (!super.equals(other)) {
         return false;
      } else {
         return other is CompanionValueOfConst && this.attr == (other as CompanionValueOfConst).attr;
      }
   }

   public override fun computeHash(): Int {
      return 31 * super.computeHash() + this.attr.hashCode();
   }

   public override fun hashCode(): Int {
      return super.hashCode();
   }
}
