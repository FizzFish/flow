package cn.sast.coroutines

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.atomicfu.AtomicArray
import kotlinx.atomicfu.AtomicFU
import kotlinx.atomicfu.AtomicFU_commonKt
import kotlinx.atomicfu.AtomicInt

@SourceDebugExtension(["SMAP\nOnDemandAllocatingPool.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OnDemandAllocatingPool.kt\ncn/sast/coroutines/OnDemandAllocatingPool\n+ 2 AtomicFU.common.kt\nkotlinx/atomicfu/AtomicFU_commonKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 OnDemandAllocatingPool.kt\ncn/sast/coroutines/OnDemandAllocatingPoolKt\n*L\n1#1,108:1\n43#1:111\n43#1:114\n36#1:115\n37#1,7:118\n43#1:135\n350#2,2:109\n350#2,2:112\n350#2,2:116\n1557#3:125\n1628#3,2:126\n1630#3:130\n1557#3:131\n1628#3,3:132\n103#4,2:128\n*S KotlinDebug\n*F\n+ 1 OnDemandAllocatingPool.kt\ncn/sast/coroutines/OnDemandAllocatingPool\n*L\n37#1:111\n56#1:114\n78#1:115\n78#1:118,7\n94#1:135\n36#1:109,2\n55#1:112,2\n78#1:116,2\n79#1:125\n79#1:126,2\n79#1:130\n93#1:131\n93#1:132,3\n81#1:128,2\n*E\n"])
public class OnDemandAllocatingPool<T>(maxCapacity: Int, create: (Int) -> Any) {
   private final val maxCapacity: Int
   private final val create: (Int) -> Any
   private final val controlState: AtomicInt
   private final val elements: AtomicArray<Any?>

   init {
      this.maxCapacity = maxCapacity;
      this.create = create;
      this.controlState = AtomicFU.atomic(0);
      this.elements = AtomicFU_commonKt.atomicArrayOfNulls(this.maxCapacity);
   }

   private inline fun tryForbidNewElements(): Int {
      val `$this$loop$iv`: AtomicInt = this.controlState;

      val it: Int;
      do {
         it = `$this$loop$iv`.getValue();
         if ((it and Integer.MIN_VALUE) != 0) {
            return 0;
         }
      } while (!this.controlState.compareAndSet(it, it | -2147483648));

      return it;
   }

   private inline fun Int.isClosed(): Boolean {
      return (`$this$isClosed` and Integer.MIN_VALUE) != 0;
   }

   public fun allocate(): Boolean {
      val `$this$loop$iv`: AtomicInt = this.controlState;

      val ctl: Int;
      do {
         ctl = `$this$loop$iv`.getValue();
         if ((ctl and Integer.MIN_VALUE) != 0) {
            return false;
         }

         if (ctl >= this.maxCapacity) {
            return true;
         }
      } while (!this.controlState.compareAndSet(ctl, ctl + 1));

      this.elements.get(ctl).setValue(this.create.invoke(ctl));
      return true;
   }

   public fun close(): List<Any> {
      val `$this$map$iv`: OnDemandAllocatingPool = this;
      val `$this$mapTo$iv$iv`: AtomicInt = this.controlState;

      var var10000: Int;
      while (true) {
         val `$i$f$mapTo`: Int = `$this$mapTo$iv$iv`.getValue();
         if ((`$i$f$mapTo` and Integer.MIN_VALUE) != 0) {
            var10000 = 0;
            break;
         }

         if (`$this$map$iv`.controlState.compareAndSet(`$i$f$mapTo`, `$i$f$mapTo` or Integer.MIN_VALUE)) {
            var10000 = `$i$f$mapTo`;
            break;
         }
      }

      val var15: java.lang.Iterable = RangesKt.until(0, var10000) as java.lang.Iterable;
      val var17: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var15, 10));
      val var19: java.util.Iterator = var15.iterator();

      while (var19.hasNext()) {
         val i: Int = (var19 as IntIterator).nextInt();

         val element: Any;
         do {
            element = this.elements.get(i).getAndSet(null);
         } while (element == null);

         var17.add(element);
      }

      return var17 as MutableList<T>;
   }

   internal fun stateRepresentation(): String {
      val ctl: Int = this.controlState.getValue();
      val closedStr: java.lang.Iterable = RangesKt.until(0, ctl and Integer.MAX_VALUE) as java.lang.Iterable;
      val `$i$f$isClosed`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(closedStr, 10));
      val var8: java.util.Iterator = closedStr.iterator();

      while (var8.hasNext()) {
         `$i$f$isClosed`.add(this.elements.get((var8 as IntIterator).nextInt()).getValue());
      }

      return "${(`$i$f$isClosed` as java.util.List).toString()}${if ((ctl and Integer.MIN_VALUE) != 0) "[closed]" else ""}";
   }

   public override fun toString(): String {
      return "OnDemandAllocatingPool(${this.stateRepresentation$corax_api()})";
   }
}
