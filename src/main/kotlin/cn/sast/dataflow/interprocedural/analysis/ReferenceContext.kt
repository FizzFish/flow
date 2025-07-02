package cn.sast.dataflow.interprocedural.analysis

public sealed class ReferenceContext protected constructor() {
   public data object ArrayElement : ReferenceContext() {
      public override fun toString(): String {
         return "ArrayElement";
      }

      public override fun hashCode(): Int {
         return 1172040842;
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else {
            return other is ReferenceContext.ArrayElement;
         }
      }
   }

   public data object ArrayInitialized : ReferenceContext() {
      public override fun toString(): String {
         return "ArrayInitialized";
      }

      public override fun hashCode(): Int {
         return 120126818;
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else {
            return other is ReferenceContext.ArrayInitialized;
         }
      }
   }

   public data object ArraySize : ReferenceContext() {
      public override fun toString(): String {
         return "ArraySize";
      }

      public override fun hashCode(): Int {
         return -1580946125;
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else {
            return other is ReferenceContext.ArraySize;
         }
      }
   }

   public data class KVPosition(key: Any) : ReferenceContext() {
      public final val key: Any

      init {
         this.key = key;
      }

      public operator fun component1(): Any {
         return this.key;
      }

      public fun copy(key: Any = this.key): cn.sast.dataflow.interprocedural.analysis.ReferenceContext.KVPosition {
         return new ReferenceContext.KVPosition(key);
      }

      public override fun toString(): String {
         return "KVPosition(key=${this.key})";
      }

      public override fun hashCode(): Int {
         return this.key.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ReferenceContext.KVPosition) {
            return false;
         } else {
            return this.key == (other as ReferenceContext.KVPosition).key;
         }
      }
   }

   public data object KVUnreferenced : ReferenceContext() {
      public override fun toString(): String {
         return "KVUnreferenced";
      }

      public override fun hashCode(): Int {
         return 523384612;
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else {
            return other is ReferenceContext.KVUnreferenced;
         }
      }
   }

   public data class ObjectValues(value: Any) : ReferenceContext() {
      public final val value: Any

      init {
         this.value = value;
      }

      public operator fun component1(): Any {
         return this.value;
      }

      public fun copy(value: Any = this.value): cn.sast.dataflow.interprocedural.analysis.ReferenceContext.ObjectValues {
         return new ReferenceContext.ObjectValues(value);
      }

      public override fun toString(): String {
         return "ObjectValues(value=${this.value})";
      }

      public override fun hashCode(): Int {
         return this.value.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ReferenceContext.ObjectValues) {
            return false;
         } else {
            return this.value == (other as ReferenceContext.ObjectValues).value;
         }
      }
   }

   public data class PTG(obj: Any, mt: Any) : ReferenceContext() {
      public final val obj: Any
      public final val mt: Any

      init {
         this.obj = obj;
         this.mt = mt;
      }

      public operator fun component1(): Any {
         return this.obj;
      }

      public operator fun component2(): Any {
         return this.mt;
      }

      public fun copy(obj: Any = this.obj, mt: Any = this.mt): cn.sast.dataflow.interprocedural.analysis.ReferenceContext.PTG {
         return new ReferenceContext.PTG(obj, mt);
      }

      public override fun toString(): String {
         return "PTG(obj=${this.obj}, mt=${this.mt})";
      }

      public override fun hashCode(): Int {
         return this.obj.hashCode() * 31 + this.mt.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ReferenceContext.PTG) {
            return false;
         } else {
            val var2: ReferenceContext.PTG = other as ReferenceContext.PTG;
            if (!(this.obj == (other as ReferenceContext.PTG).obj)) {
               return false;
            } else {
               return this.mt == var2.mt;
            }
         }
      }
   }

   public data class Slot(slot: Any) : ReferenceContext() {
      public final val slot: Any

      init {
         this.slot = slot;
      }

      public operator fun component1(): Any {
         return this.slot;
      }

      public fun copy(slot: Any = this.slot): cn.sast.dataflow.interprocedural.analysis.ReferenceContext.Slot {
         return new ReferenceContext.Slot(slot);
      }

      public override fun toString(): String {
         return "Slot(slot=${this.slot})";
      }

      public override fun hashCode(): Int {
         return this.slot.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ReferenceContext.Slot) {
            return false;
         } else {
            return this.slot == (other as ReferenceContext.Slot).slot;
         }
      }
   }
}
