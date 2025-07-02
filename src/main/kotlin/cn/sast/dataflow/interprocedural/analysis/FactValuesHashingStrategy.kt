package cn.sast.dataflow.interprocedural.analysis

import gnu.trove.strategy.HashingStrategy
import java.util.Objects

public open class FactValuesHashingStrategy : HashingStrategy<IValue> {
   public open fun computeHashCode(v: IValue): Int {
      val var10000: Int;
      if (v is AnyNewValue) {
         var10000 = Objects.hash((v as AnyNewValue).getNewExpr());
      } else if (v is ConstVal) {
         var10000 = Objects.hash((v as ConstVal).getV());
      } else if (v is SummaryValue) {
         var10000 = Objects.hash((v as SummaryValue).getType(), (v as SummaryValue).getUnit(), (v as SummaryValue).getSpecial());
      } else if (v is EntryParam) {
         var10000 = Objects.hash((v as EntryParam).getMethod(), (v as EntryParam).getParamIndex());
      } else if (v is GlobalStaticObject) {
         var10000 = (v as GlobalStaticObject).hashCode();
      } else {
         if (v !is PhantomField) {
            throw new IllegalStateException(("error type of ${v.getClass()}: $v").toString());
         }

         var10000 = Objects.hash(this.computeHashCode((v as PhantomField).getBase()), (v as PhantomField).getAccessPath());
      }

      return var10000;
   }

   public open operator fun equals(a: IValue, b: IValue): Boolean {
      if (a === b) {
         return true;
      } else if (this.computeHashCode(a) != this.computeHashCode(b)) {
         return false;
      } else if (a is AnyNewValue && b is AnyNewValue) {
         return (a as AnyNewValue).getNewExpr() == (b as AnyNewValue).getNewExpr();
      } else if (a is ConstVal && b is ConstVal) {
         return (a as ConstVal).getV() == (b as ConstVal).getV();
      } else if (a is SummaryValue && b is SummaryValue) {
         return (a as SummaryValue).getType() == (b as SummaryValue).getType()
            && (a as SummaryValue).getUnit() == (b as SummaryValue).getUnit()
            && (a as SummaryValue).getSpecial() == (b as SummaryValue).getSpecial();
      } else if (a is EntryParam && b is EntryParam) {
         return (a as EntryParam).getMethod() == (b as EntryParam).getMethod() && (a as EntryParam).getParamIndex() == (b as EntryParam).getParamIndex();
      } else if (a is GlobalStaticObject && b is GlobalStaticObject) {
         return a == b;
      } else if (a is PhantomField && b is PhantomField) {
         return this.equals((a as PhantomField).getBase(), (b as PhantomField).getBase())
            && (a as PhantomField).getAccessPath() == (b as PhantomField).getAccessPath();
      } else {
         throw new IllegalStateException(("error type of a: \n${a.getClass()}: $a    b:\n${b.getClass()}: $b").toString());
      }
   }

   public companion object {
      public final val INSTANCE: FactValuesHashingStrategy
   }
}
