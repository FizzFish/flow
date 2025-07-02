package cn.sast.api.util

import java.util.ArrayList
import java.util.LinkedList
import java.util.Objects
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nTimeRange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TimeRange.kt\ncn/sast/api/util/TimeRange\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,331:1\n1#2:332\n*E\n"])
public class TimeRange(min: Long, max: Long) : java.lang.Comparable<TimeRange> {
   public final var min: Long
      private set

   public final var max: Long
      private set

   public final var leftClose: Boolean = true
      internal set

   public final var rightClose: Boolean = true
      internal set

   init {
      this.set(min, max);
   }

   public open operator fun compareTo(r: TimeRange?): Int {
      if (r == null) {
         throw new NullPointerException("The input cannot be null!");
      } else {
         return if (this.min > r.min) 1 else (if (this.min < r.min) -1 else (if (this.max > r.max) 1 else (if (this.max < r.max) -1 else 0)));
      }
   }

   public fun setMin(min: Long) {
      if (min < 0L || min > this.max) {
         throw new IllegalArgumentException("Invalid input!".toString());
      } else {
         this.min = min;
      }
   }

   public fun setMax(max: Long) {
      if (max < 0L || max < this.min) {
         throw new IllegalArgumentException("Invalid input!".toString());
      } else {
         this.max = max;
      }
   }

   public fun contains(r: TimeRange): Boolean {
      return this.min <= r.min && this.max >= r.max;
   }

   public fun contains(min: Long, max: Long): Boolean {
      return if (this.leftClose && this.rightClose)
         this.min <= min && this.max >= max
         else
         (
            if (this.leftClose)
               this.min <= min && this.max > max
               else
               (if (this.rightClose) this.min < min && this.max >= max else this.min < min && this.max > max)
         );
   }

   public fun contains(time: Long): Boolean {
      return if (this.leftClose && this.rightClose)
         time >= this.min && time <= this.max
         else
         (
            if (this.leftClose)
               time >= this.min && time < this.max
               else
               (if (this.rightClose) time > this.min && time <= this.max else time > this.min && time < this.max)
         );
   }

   public fun set(min: Long, max: Long) {
      if (min > max) {
         throw new IllegalArgumentException(("min:$min should not be larger than max: $max").toString());
      } else {
         this.min = min;
         this.max = max;
      }
   }

   public fun intersects(r: TimeRange): Boolean {
      return (this.leftClose && r.rightClose || r.max >= this.min)
         && (this.leftClose || r.rightClose || r.max > this.min)
         && (!this.leftClose || !r.rightClose || r.max > this.min - 2)
         && (this.rightClose && r.leftClose || r.min <= this.max)
         && (this.rightClose || r.leftClose || r.min < this.max)
         && (!this.rightClose || !r.leftClose || r.min < this.max + 2);
   }

   public override operator fun equals(o: Any?): Boolean {
      if (this === o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         return this.min == (o as TimeRange).min && this.max == (o as TimeRange).max;
      } else {
         return false;
      }
   }

   public override fun hashCode(): Int {
      return Objects.hash(this.min, this.max);
   }

   public fun overlaps(rhs: TimeRange): Boolean {
      return (this.leftClose && rhs.rightClose || rhs.max > this.min)
         && (this.leftClose || rhs.rightClose || rhs.max > this.min + 1L)
         && (!this.leftClose || !rhs.rightClose || rhs.max >= this.min)
         && (this.rightClose && rhs.leftClose || rhs.min < this.max)
         && (this.rightClose || rhs.leftClose || rhs.min + 1L < this.max)
         && (!this.rightClose || !rhs.leftClose || rhs.min <= this.max);
   }

   public override fun toString(): String {
      val res: StringBuilder = new StringBuilder();
      if (this.leftClose) {
         res.append("[ ");
      } else {
         res.append("( ");
      }

      res.append(this.min).append(" : ").append(this.max);
      if (this.rightClose) {
         res.append(" ]");
      } else {
         res.append(" )");
      }

      val var10000: java.lang.String = res.toString();
      return var10000;
   }

   public fun merge(rhs: TimeRange) {
      this.set(RangesKt.coerceAtMost(this.min, rhs.min), RangesKt.coerceAtLeast(this.max, rhs.max));
   }

   public fun getRemains(timeRangesPrev: List<TimeRange>): List<TimeRange> {
      val remains: java.util.List = new ArrayList();

      for (TimeRange prev : timeRangesPrev) {
         if (prev.min >= this.max + 2) {
            break;
         }

         if (this.intersects(prev)) {
            if (prev.contains(this)) {
               return remains;
            }

            if (this.contains(prev)) {
               if (prev.min > this.min && prev.max == this.max) {
                  this.setMax(prev.min);
                  this.rightClose = false;
                  remains.add(this);
                  return remains;
               }

               if (prev.min == this.min) {
                  this.min = prev.max;
                  this.leftClose = false;
               } else {
                  val r: TimeRange = new TimeRange(this.min, prev.min);
                  r.leftClose = this.leftClose;
                  r.rightClose = false;
                  remains.add(r);
                  this.min = prev.max;
                  this.leftClose = false;
               }
            } else {
               if (prev.min >= this.min) {
                  this.setMax(prev.min);
                  this.rightClose = false;
                  remains.add(this);
                  return remains;
               }

               this.min = prev.max;
               this.leftClose = false;
            }
         }
      }

      remains.add(this);
      return remains;
   }

   public companion object {
      public fun sortAndMerge(unionCandidates: MutableList<TimeRange>): MutableList<TimeRange> {
         CollectionsKt.sort(unionCandidates);
         val unionResult: LinkedList = new LinkedList();
         val iterator: java.util.Iterator = unionCandidates.iterator();
         if (!iterator.hasNext()) {
            return unionResult;
         } else {
            var var6: TimeRange = iterator.next() as TimeRange;

            while (iterator.hasNext()) {
               val rangeNext: TimeRange = iterator.next() as TimeRange;
               if (var6.intersects(rangeNext)) {
                  var6.merge(rangeNext);
               } else {
                  unionResult.add(var6);
                  var6 = rangeNext;
               }
            }

            unionResult.add(var6);
            return unionResult;
         }
      }
   }
}
