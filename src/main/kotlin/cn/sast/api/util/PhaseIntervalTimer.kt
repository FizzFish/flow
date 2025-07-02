package cn.sast.api.util

import java.util.Arrays
import java.util.LinkedList
import java.util.TreeMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.atomicfu.AtomicFU
import kotlinx.atomicfu.AtomicInt
import mu.KLogger

@SourceDebugExtension(["SMAP\nPhaseIntervalTimer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PhaseIntervalTimer.kt\ncn/sast/api/util/PhaseIntervalTimer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,124:1\n1#2:125\n*E\n"])
public open class PhaseIntervalTimer {
   private final val id: AtomicInt = AtomicFU.atomic(0)

   public final var startTime: Long?
      private set

   public final var endTime: Long?
      private set

   private final var _elapsedTime: Long?

   public open val prefix: String
      public open get() {
         return "";
      }


   public final var elapsedTime: Long?
      public final get() {
         if (this.phaseTimerActiveCount.getValue() != 0) {
            logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$0);
         }

         if (this.ranges.size() != 0) {
            logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$1);
         }

         if (this.queue.size() != 0) {
            logger.error(PhaseIntervalTimer::_get_elapsedTime_$lambda$2);
         }

         return this._elapsedTime;
      }

      private set

   private final var phaseTimerActiveCount: AtomicInt = AtomicFU.atomic(0)

   public final var phaseStartCount: AtomicInt = AtomicFU.atomic(0)
      private set

   public final val phaseAverageElapsedTime: Double?
      public final get() {
         val var1: Int = this.phaseStartCount.getValue();
         var c: Int = var1.intValue();
         val var10000: Int = if (c > 0) var1 else null;
         val var14: Any;
         if ((if (c > 0) var1 else null) != null) {
            c = var10000.intValue();
            val var12: java.lang.Long = this.getElapsedTime();
            if (var12 != null) {
               val var4: java.lang.Double = (double)var12.longValue();
               val itx: Double = var4.doubleValue();
               val var13: java.lang.Double = if (!java.lang.Double.isInfinite(itx) && !java.lang.Double.isNaN(itx)) var4 else null;
               if (var13 != null) {
                  return var13 / (double)c;
               }
            }

            var14 = null;
         } else {
            var14 = null;
         }

         return (java.lang.Double)var14;
      }


   private final var ranges: MutableList<TimeRange> = (new LinkedList()) as java.util.List
   private final var queue: TreeMap<Int, cn.sast.api.util.PhaseIntervalTimer.Snapshot> = new TreeMap()

   public fun start(): cn.sast.api.util.PhaseIntervalTimer.Snapshot {
      val result: PhaseIntervalTimer.Snapshot = new PhaseIntervalTimer.Snapshot(PhaseIntervalTimerKt.currentNanoTime(), this.id.getAndIncrement());
      if (this.startTime == null) {
         this.startTime = result.getStartTime();
      }

      synchronized (this.queue) {
         this.queue.put(result.getId$corax_api(), result);
         val var5: Int = this.phaseTimerActiveCount.getAndIncrement();
      }

      this.phaseStartCount.getAndIncrement();
      return result;
   }

   public fun stop(snapshot: cn.sast.api.util.PhaseIntervalTimer.Snapshot) {
      val timeRange: TimeRange = new TimeRange(snapshot.getStartTime(), PhaseIntervalTimerKt.currentNanoTime());
      this.endTime = Math.max(if (this.endTime != null) this.endTime else timeRange.getMax(), timeRange.getMax());
      synchronized (this.queue) {
         val cur: Int = this.phaseTimerActiveCount.decrementAndGet();
         this.ranges.add(timeRange);
         this.ranges = TimeRange.Companion.sortAndMerge(this.ranges);
         if (cur >= 0) {
            if (this.queue.lowerKey(snapshot.getId$corax_api()) == null) {
               var i: Int = 0;
               var elapsedTime: Long = if (this._elapsedTime != null) this._elapsedTime else 0L;
               val higher: Entry = this.queue.higherEntry(snapshot.getId$corax_api());

               for (long noSnapshotBefore = higher == null
                     ? ((TimeRange)CollectionsKt.last(this.ranges)).getMax()
                     : (timeRange.getMax() <= ((PhaseIntervalTimer.Snapshot)higher.getValue()).getStartTime() ? timeRange.getMax() : timeRange.getMin());
                  i < this.ranges.size() && this.ranges.get(i).getMax() <= noSnapshotBefore;
                  i++
               ) {
                  val e: TimeRange = this.ranges.remove(i--);
                  elapsedTime += e.getMax() - e.getMin();
               }

               this._elapsedTime = elapsedTime;
            }

            this.queue.remove(snapshot.getId$corax_api());
         } else {
            logger.error(PhaseIntervalTimer::stop$lambda$8$lambda$7);
         }
      }
   }

   public override fun toString(): String {
      val var2: Array<Any> = new Object[]{PhaseIntervalTimerKt.nanoTimeInSeconds$default(this._elapsedTime, 0, 1, null)};
      val var10000: java.lang.String = java.lang.String.format("elapsed time: %.2fs", Arrays.copyOf(var2, var2.length));
      return var10000;
   }

   @JvmStatic
   fun `_get_elapsedTime_$lambda$0`(`this$0`: PhaseIntervalTimer): Any {
      return "${`this$0`.getPrefix()}internal error: phaseTimerCount is not zero";
   }

   @JvmStatic
   fun `_get_elapsedTime_$lambda$1`(`this$0`: PhaseIntervalTimer): Any {
      return "${`this$0`.getPrefix()}internal error: ranges is not empty";
   }

   @JvmStatic
   fun `_get_elapsedTime_$lambda$2`(`this$0`: PhaseIntervalTimer): Any {
      return "${`this$0`.getPrefix()}internal error: queue is not empty";
   }

   @JvmStatic
   fun `stop$lambda$8$lambda$7`(): Any {
      return "internal error: phaseTimerCount is negative";
   }

   @JvmStatic
   fun `logger$lambda$9`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public data class Snapshot internal constructor(startTime: Long, id: Int) {
      public final val startTime: Long
      internal final val id: Int

      init {
         this.startTime = startTime;
         this.id = id;
      }

      public operator fun component1(): Long {
         return this.startTime;
      }

      internal operator fun component2(): Int {
         return this.id;
      }

      public fun copy(startTime: Long = this.startTime, id: Int = this.id): cn.sast.api.util.PhaseIntervalTimer.Snapshot {
         return new PhaseIntervalTimer.Snapshot(startTime, id);
      }

      public override fun toString(): String {
         return "Snapshot(startTime=${this.startTime}, id=${this.id})";
      }

      public override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.startTime) * 31 + Integer.hashCode(this.id);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is PhaseIntervalTimer.Snapshot) {
            return false;
         } else {
            val var2: PhaseIntervalTimer.Snapshot = other as PhaseIntervalTimer.Snapshot;
            if (this.startTime != (other as PhaseIntervalTimer.Snapshot).startTime) {
               return false;
            } else {
               return this.id == var2.id;
            }
         }
      }
   }
}
