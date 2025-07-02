package cn.sast.dataflow.interprocedural.analysis

import kotlin.enums.EnumEntries
import soot.RefType
import soot.Type

public interface IValue {
   public val type: Type

   public abstract fun typeIsConcrete(): Boolean {
   }

   public abstract fun isNullConstant(): Boolean {
   }

   public abstract fun getKind(): cn.sast.dataflow.interprocedural.analysis.IValue.Kind {
   }

   public open fun isNormal(): Boolean {
   }

   public open fun objectEqual(b: IValue): Boolean? {
   }

   public open fun clone(): IValue {
   }

   public open fun copy(type: Type): IValue {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun isNormal(`$this`: IValue): Boolean {
         return `$this`.getKind() === IValue.Kind.Normal;
      }

      @JvmStatic
      fun objectEqual(`$this`: IValue, b: IValue): java.lang.Boolean? {
         return if (`$this`.getType() is RefType && `$this` === b) true else null;
      }

      @JvmStatic
      fun clone(`$this`: IValue): IValue {
         return `$this`;
      }

      @JvmStatic
      fun copy(`$this`: IValue, type: Type): IValue {
         return `$this`;
      }
   }

   public enum class Kind {
      Normal,
      Entry,
      Summary
      @JvmStatic
      fun getEntries(): EnumEntries<IValue.Kind> {
         return $ENTRIES;
      }
   }
}
