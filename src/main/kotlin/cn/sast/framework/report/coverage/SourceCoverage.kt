package cn.sast.framework.report.coverage

import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.FileIndexer
import cn.sast.framework.report.FileIndexerBuilder
import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.jacoco.core.analysis.ISourceFileCoverage
import org.jacoco.core.internal.analysis.CounterImpl

@SourceDebugExtension(["SMAP\nCoverage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/SourceCoverage\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,490:1\n1863#2,2:491\n1279#2,2:493\n1293#2,4:495\n*S KotlinDebug\n*F\n+ 1 Coverage.kt\ncn/sast/framework/report/coverage/SourceCoverage\n*L\n59#1:491,2\n63#1:493,2\n63#1:495,4\n*E\n"])
public class SourceCoverage(sourceCoverage: MutableMap<String, cn.sast.framework.report.coverage.SourceCoverage.JavaSourceCoverage>) {
   public final val sourceCoverage: MutableMap<String, cn.sast.framework.report.coverage.SourceCoverage.JavaSourceCoverage>

   init {
      this.sourceCoverage = sourceCoverage;
   }

   public fun calculateCoveredRatio(targetSources: Set<IResFile>): CounterImpl {
      var allLineCount: Int = 0;
      var missedCount: Int = 0;
      val fileIndexerBuilder: FileIndexerBuilder = new FileIndexerBuilder();

      val fileIndexer: java.lang.Iterable;
      for (Object element$iv : fileIndexer) {
         fileIndexerBuilder.addIndexMap(`$i$f$associateWith` as IResFile);
      }

      val var21: FileIndexer = fileIndexerBuilder.build();
      val var23: java.lang.Iterable = MapsKt.toList(this.sourceCoverage);
      val var28: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(var23, 10)), 16));

      for (Object element$iv$iv : $this$associateWith$iv) {
         var28.put(
            `element$iv$iv`,
            SequencesKt.firstOrNull(
               var21.findFromFileIndexMap(
                  StringsKt.split$default((`element$iv$iv` as Pair).getFirst() as java.lang.CharSequence, new java.lang.String[]{"/", "\\"}, false, 0, 6, null),
                  AbstractFileIndexer.Companion.getDefaultClassCompareMode()
               )
            ) as IResFile
         );
      }

      val var22: java.util.Map = var28;
      val var24: java.util.Iterator = var28.entrySet().iterator();

      while (var24.hasNext()) {
         val var31: SourceCoverage.JavaSourceCoverage = ((var24.next() as Entry).getKey() as Pair).component2() as SourceCoverage.JavaSourceCoverage;
         allLineCount += var31.getLineCount();
         val var33: Int = var31.getSourceCoverage().getLineCounter().getMissedCount();
         missedCount += if (var33 <= var31.getLineCount()) var33 else var31.getLineCount();
      }

      for (IResFile source : SetsKt.minus(targetSources, CollectionsKt.toSet(CollectionsKt.filterNotNull(sourceMap.values())))) {
         var var34: Int;
         try {
            var34 = SequencesKt.count(StringsKt.lineSequence(ResourceKt.readText$default(var30, null, 1, null)));
         } catch (var20: Exception) {
            logger.error("File $var30 cannot be read!", var20);
            var34 = 0;
         }

         allLineCount += var34;
         missedCount += var34;
      }

      val var35: CounterImpl = CounterImpl.getInstance(missedCount, allLineCount - missedCount);
      return var35;
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public data class JavaSourceCoverage(lineCount: Int, sourceCoverage: ISourceFileCoverage) {
      public final val lineCount: Int
      public final val sourceCoverage: ISourceFileCoverage

      init {
         this.lineCount = lineCount;
         this.sourceCoverage = sourceCoverage;
         if (this.lineCount < 0) {
            throw new IllegalStateException("Check failed.".toString());
         }
      }

      public operator fun component1(): Int {
         return this.lineCount;
      }

      public operator fun component2(): ISourceFileCoverage {
         return this.sourceCoverage;
      }

      public fun copy(lineCount: Int = this.lineCount, sourceCoverage: ISourceFileCoverage = this.sourceCoverage): cn.sast.framework.report.coverage.SourceCoverage.JavaSourceCoverage {
         return new SourceCoverage.JavaSourceCoverage(lineCount, sourceCoverage);
      }

      public override fun toString(): String {
         return "JavaSourceCoverage(lineCount=${this.lineCount}, sourceCoverage=${this.sourceCoverage})";
      }

      public override fun hashCode(): Int {
         return Integer.hashCode(this.lineCount) * 31 + this.sourceCoverage.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is SourceCoverage.JavaSourceCoverage) {
            return false;
         } else {
            val var2: SourceCoverage.JavaSourceCoverage = other as SourceCoverage.JavaSourceCoverage;
            if (this.lineCount != (other as SourceCoverage.JavaSourceCoverage).lineCount) {
               return false;
            } else {
               return this.sourceCoverage == var2.sourceCoverage;
            }
         }
      }
   }
}
