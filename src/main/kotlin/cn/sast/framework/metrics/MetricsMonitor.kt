package cn.sast.framework.metrics

import cn.sast.api.config.MainConfigKt
import cn.sast.api.report.ProjectMetrics
import cn.sast.api.report.Report
import cn.sast.api.util.IMonitor
import cn.sast.api.util.PhaseIntervalTimerKt
import cn.sast.api.util.Timer
import cn.sast.common.CustomRepeatingTimer
import cn.sast.common.IResDirectory
import cn.sast.framework.result.ResultCollector
import cn.sast.framework.result.ResultCounter
import cn.sast.idfa.analysis.UsefulMetrics
import com.charleskorn.kaml.Yaml
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongUnaryOperator
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.Duration
import kotlin.time.DurationKt
import kotlin.time.DurationUnit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModuleBuilder
import org.eclipse.microprofile.metrics.Gauge

@Serializable
@SourceDebugExtension(["SMAP\nMetricsMonitor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitor\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 6 SerializersModuleBuilders.kt\nkotlinx/serialization/modules/SerializersModuleBuildersKt\n*L\n1#1,249:1\n72#2,2:250\n1#3:252\n1#3:253\n1485#4:254\n1510#4,3:255\n1513#4,3:265\n1246#4,4:270\n1246#4,4:276\n808#4,11:280\n1863#4,2:291\n381#5,7:258\n462#5:268\n412#5:269\n477#5:274\n423#5:275\n31#6,3:293\n*S KotlinDebug\n*F\n+ 1 MetricsMonitor.kt\ncn/sast/framework/metrics/MetricsMonitor\n*L\n92#1:250,2\n92#1:252\n159#1:254\n159#1:255,3\n159#1:265,3\n159#1:270,4\n160#1:276,4\n165#1:280,11\n218#1:291,2\n159#1:258,7\n159#1:268\n159#1:269\n160#1:274\n160#1:275\n228#1:293,3\n*E\n"])
public class MetricsMonitor : IMonitor {
   private final var beginDate: String = ""
   public final val beginMillis: Long = System.currentTimeMillis()
   private final var elapsedSeconds: Double = -1.0
   private final var elapsedTime: String = ""
   private final var endDate: String = ""
   private final var endTime: Long
   private final var jvmMemoryUsedMax: Double = -1.0
   private final val jvmMemoryMax: Double
   public open val projectMetrics: ProjectMetrics
   private final val phaseTimer: MutableList<cn.sast.framework.metrics.MetricsMonitor.PhaseTimer>
   private final val final: MutableMap<String, Any>
   private final val reports: MutableList<ReportKey>
   private final val snapshot: MutableList<cn.sast.framework.metrics.MetricsMonitor.MetricsSnapshot>

   @Transient
   public final val beginNanoTime: Long

   @Transient
   private final val allPhaseTimer: ConcurrentHashMap<String, Timer>

   @Transient
   private final val analyzeFinishHook: MutableList<Thread>

   private final val g: Double
      private final get() {
         return `$this$g` / 1024.0 / 1024.0 / 1024.0;
      }


   private final val g: Double
      private final get() {
         label21:
         if (`$this$g` == null) {
            return -1.0;
         } else {
            val var4: Any = `$this$g`.getValue();
            val var10000: java.lang.Long = (if (var4 as java.lang.Long >= 0L) var4 else null) as java.lang.Long;
            return if (var10000 != null) this.getG(var10000) else -1.0;
         }
      }


   @Transient
   public final val maxUsedMemory: AtomicLong

   @Transient
   private final val timer: CustomRepeatingTimer

   init {
      val var10001: Gauge = UsefulMetrics.Companion.getMetrics().getJvmMemoryMax();
      this.jvmMemoryMax = MetricsMonitorKt.inMemGB$default(if (var10001 != null) this.getG(var10001) else null, 0, 1, null);
      this.projectMetrics = new ProjectMetrics(null, null, 0, 0, 0, 0, 0, 0, 0.0F, 0, 0.0F, 0, 0, 0, 0L, 0L, 0L, 0, 0, 0, 0.0F, 0, 0.0F, 0, 16777215, null);
      this.phaseTimer = new ArrayList<>();
      this.final = new LinkedHashMap<>();
      this.reports = new ArrayList<>();
      this.snapshot = new ArrayList<>();
      this.beginNanoTime = PhaseIntervalTimerKt.currentNanoTime();
      this.allPhaseTimer = new ConcurrentHashMap<>();
      this.analyzeFinishHook = new ArrayList<>();
      this.maxUsedMemory = new AtomicLong(0L);
      val var1: CustomRepeatingTimer = new CustomRepeatingTimer(2000L, MetricsMonitor::timer$lambda$5);
      var1.setRepeats(true);
      this.record();
      this.timer = var1;
   }

   public override fun timer(phase: String): Timer {
      val `$this$getOrPut$iv`: ConcurrentMap = this.allPhaseTimer;
      var var10000: Any = this.allPhaseTimer.get(phase);
      if (var10000 == null) {
         val `default$iv`: Any = new Timer(phase);
         var10000 = `$this$getOrPut$iv`.putIfAbsent(phase, `default$iv`);
         if (var10000 == null) {
            var10000 = `default$iv`;
         }
      }

      return var10000 as Timer;
   }

   public fun record() {
      val m: UsefulMetrics = UsefulMetrics.Companion.getMetrics();
      synchronized (this) {
         val var10000: Gauge = m.getJvmMemoryUsed();
         if (var10000 != null) {
            val var27: java.lang.Long = var10000.getValue() as java.lang.Long;
            if (var27 != null) {
               val usedMemory: Long = var27.longValue();
               this.maxUsedMemory.updateAndGet(new LongUnaryOperator(usedMemory) {
                  {
                     this.$usedMemory = `$usedMemory`;
                  }

                  @Override
                  public final long applyAsLong(long it) {
                     return if (it < this.$usedMemory) this.$usedMemory else it;
                  }
               });
            }
         }

         var var7: java.util.Collection;
         var var29: java.lang.Double;
         var var30: java.lang.Double;
         var var31: java.lang.Double;
         var var33: java.lang.Double;
         var var10002: java.lang.Double;
         label37: {
            var7 = this.snapshot;
            var28 = PhaseIntervalTimerKt.nanoTimeInSeconds$default(
               MetricsMonitorKt.timeSub(PhaseIntervalTimerKt.currentNanoTime(), this.beginNanoTime), 0, 1, null
            );
            val var10001: Gauge = m.getJvmMemoryUsed();
            var29 = MetricsMonitorKt.inMemGB$default(if (var10001 != null) this.getG(var10001) else null, 0, 1, null);
            var10002 = MetricsMonitorKt.inMemGB$default(this.getG(this.maxUsedMemory.get()), 0, 1, null);
            val var10003: Gauge = m.getJvmMemoryCommitted();
            var30 = MetricsMonitorKt.inMemGB$default(if (var10003 != null) this.getG(var10003) else null, 0, 1, null);
            val var10004: Gauge = m.getFreePhysicalSize();
            var31 = MetricsMonitorKt.inMemGB$default(if (var10004 != null) this.getG(var10004) else null, 0, 1, null);
            val var10005: Gauge = m.getCpuSystemCpuLoad();
            if (var10005 != null) {
               var33 = var10005.getValue() as java.lang.Double;
               if (var33 != null) {
                  var33 = PhaseIntervalTimerKt.retainDecimalPlaces$default(var33.doubleValue(), 2, null, 4, null);
                  break label37;
               }
            }

            var33 = null;
         }

         var7.add(new MetricsMonitor.MetricsSnapshot(var28, var29, var10002, var30, var31, var33));
      }
   }

   public fun start() {
      this.timer.start();
   }

   public fun stop() {
      this.timer.stop();
   }

   @JvmName(name = "putNumber")
   public fun <T : Number> put(name: String, value: T) {
      synchronized (this) {
         val var5: java.util.Map = this.final;
         val var6: Pair = TuplesKt.to(name, value);
         var5.put(var6.getFirst(), var6.getSecond());
      }
   }

   public fun put(name: String, value: String) {
      synchronized (this) {
         val var5: java.util.Map = this.final;
         val var6: Pair = TuplesKt.to(name, value);
         var5.put(var6.getFirst(), var6.getSecond());
      }
   }

   public fun take(result: ResultCollector) {
      synchronized (this) {
         this.getProjectMetrics().setSerializedReports(result.getReports().size());
         val counter: java.util.Collection = this.reports;
         var `$this$filterIsInstance$iv`: java.lang.Iterable = result.getReports();
         var `destination$iv$iv`: java.util.Map = new LinkedHashMap();

         for (Object element$iv$iv : $this$groupBy$iv) {
            val it: Any = new ReportKey((`element$iv$iv` as Report).getCategory(), (`element$iv$iv` as Report).getType(), 0, 4, null);
            val itx: Any = `destination$iv$iv`.get(it);
            val var10000: Any;
            if (itx == null) {
               val var56: Any = new ArrayList();
               `destination$iv$iv`.put(it, var56);
               var10000 = var56;
            } else {
               var10000 = itx;
            }

            (var10000 as java.util.List).add(`element$iv$iv`);
         }

         `destination$iv$iv` = new LinkedHashMap(MapsKt.mapCapacity(`destination$iv$iv`.size()));

         val var38: java.lang.Iterable;
         for (Object element$iv$iv$iv : var38) {
            `destination$iv$iv`.put((var47 as Entry).getKey(), ((var47 as Entry).getValue() as java.util.Collection).size());
         }

         `destination$iv$iv` = new LinkedHashMap(MapsKt.mapCapacity(`destination$iv$iv`.size()));

         for (Object element$iv$iv$iv : var38) {
            val var50: Entry = var48 as Entry;
            ((var48 as Entry).getKey() as ReportKey).setSize(((var48 as Entry).getValue() as java.lang.Number).intValue());
            `destination$iv$iv`.put(var50.getKey() as ReportKey, (var48 as Entry).getValue());
         }

         CollectionsKt.addAll(counter, `destination$iv$iv`.keySet());
         `$this$filterIsInstance$iv` = result.getCollectors();
         val `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$groupBy$iv) {
            if (var43 is ResultCounter) {
               `destination$iv$ivx`.add(var43);
            }
         }

         val var25: ResultCounter = CollectionsKt.firstOrNull(`destination$iv$ivx` as java.util.List) as ResultCounter;
         if (var25 != null) {
            this.putNumber("infoflow.results", var25.getInfoflowResCount().get());
            this.putNumber("infoflow.abstraction", var25.getInfoflowAbsAtSinkCount().get());
            this.putNumber("symbolic.execution", var25.getSymbolicUTbotCount().get());
            this.putNumber("PreAnalysis.results", var25.getPreAnalysisResultCount().get());
            this.putNumber("built-in.Analysis.results", var25.getBuiltInAnalysisCount().get());
            this.putNumber("AbstractInterpretationAnalysis.results", var25.getDataFlowCount().get());
         }
      }
   }

   public fun serialize(out: IResDirectory) {
      label45: {
         this.stop();
         val file: Path = out.resolve("metrics.yml").getPath();
         synchronized (this) {
            this.getProjectMetrics().process();
            this.beginDate = MetricsMonitorKt.getDateStringFromMillis(this.beginMillis);

            for (Entry var6 : this.allPhaseTimer.entrySet()) {
               val phaseName: java.lang.String = var6.getKey() as java.lang.String;
               val it: Timer = var6.getValue() as Timer;
               this.phaseTimer
                  .add(
                     new MetricsMonitor.PhaseTimer(
                        phaseName,
                        PhaseIntervalTimerKt.nanoTimeInSeconds$default(MetricsMonitorKt.timeSub(it.getStartTime(), this.beginNanoTime), 0, 1, null),
                        PhaseIntervalTimerKt.nanoTimeInSeconds$default(it.getElapsedTime(), 0, 1, null),
                        it.getPhaseStartCount().getValue(),
                        PhaseIntervalTimerKt.nanoTimeInSeconds(it.getPhaseAverageElapsedTime(), 6),
                        PhaseIntervalTimerKt.nanoTimeInSeconds$default(MetricsMonitorKt.timeSub(it.getEndTime(), this.beginNanoTime), 0, 1, null)
                     )
                  );
            }

            var var10001: java.lang.String;
            label34: {
               CollectionsKt.sortWith(this.phaseTimer, new MetricsMonitor$serialize$lambda$15$$inlined$compareBy$1());
               this.endTime = PhaseIntervalTimerKt.currentNanoTime();
               val var16: java.lang.Long = MetricsMonitorKt.timeSub(this.endTime, this.beginNanoTime);
               this.endDate = MetricsMonitorKt.getDateStringFromMillis(System.currentTimeMillis());
               this.elapsedSeconds = PhaseIntervalTimerKt.nanoTimeInSeconds$default(var16, 0, 1, null);
               if (var16 != null) {
                  var10001 = Duration.toString-impl(DurationKt.toDuration(var16, DurationUnit.NANOSECONDS));
                  if (var10001 != null) {
                     break label34;
                  }
               }

               var10001 = "invalid";
            }

            this.elapsedTime = var10001;
            this.jvmMemoryUsedMax = MetricsMonitorKt.inMemGB$default(this.getG(this.maxUsedMemory.get()), 0, 1, null);
            val var21: Array<OpenOption> = new OpenOption[0];
            val var10000: OutputStream = Files.newOutputStream(file, Arrays.copyOf(var21, var21.length));
            val var17: Closeable = var10000;
            var var18: java.lang.Throwable = null;

            try {
               try {
                  Yaml.encodeToStream$default(yamlFormat, Companion.serializer() as SerializationStrategy, this, var17 as OutputStream, null, 8, null);
               } catch (var10: java.lang.Throwable) {
                  var18 = var10;
                  throw var10;
               }
            } catch (var11: java.lang.Throwable) {
               CloseableKt.closeFinally(var17, var18);
            }

            CloseableKt.closeFinally(var17, null);
         }
      }
   }

   public fun addAnalyzeFinishHook(t: Thread) {
      this.analyzeFinishHook.add(t);
   }

   public fun runAnalyzeFinishHook() {
      synchronized (this.analyzeFinishHook) {
         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val it: Thread = `element$iv` as Thread;
            (`element$iv` as Thread).start();
            it.join();
         }

         this.analyzeFinishHook.clear();
      }
   }

   @JvmStatic
   fun `timer$lambda$5`(`this$0`: MetricsMonitor): Unit {
      `this$0`.record();
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `_init_$lambda$18`(`this$0`: MetricsMonitor): Unit {
      `this$0`.record();
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun {
      val `builder$iv`: SerializersModuleBuilder = new SerializersModuleBuilder();
      `builder$iv`.contextual(Any::class, new DynamicLookupSerializer());
      yamlFormat = new Yaml(`builder$iv`.build(), MainConfigKt.getYamlConfiguration());
   }

   public companion object {
      public final val yamlFormat: Yaml

      public fun serializer(): KSerializer<MetricsMonitor> {
         return MetricsMonitor.$serializer.INSTANCE as KSerializer<MetricsMonitor>;
      }
   }

   @Serializable
   public data class MetricsSnapshot(timeInSecond: Double,
      jvmMemoryUsed: Double?,
      jvmMemoryUsedMax: Double?,
      jvmMemoryCommitted: Double?,
      freePhysicalSize: Double?,
      cpuSystemCpuLoad: Double?
   ) {
      public final val timeInSecond: Double
      public final val jvmMemoryUsed: Double?
      public final val jvmMemoryUsedMax: Double?
      public final val jvmMemoryCommitted: Double?
      public final val freePhysicalSize: Double?
      public final val cpuSystemCpuLoad: Double?

      init {
         this.timeInSecond = timeInSecond;
         this.jvmMemoryUsed = jvmMemoryUsed;
         this.jvmMemoryUsedMax = jvmMemoryUsedMax;
         this.jvmMemoryCommitted = jvmMemoryCommitted;
         this.freePhysicalSize = freePhysicalSize;
         this.cpuSystemCpuLoad = cpuSystemCpuLoad;
      }

      public operator fun component1(): Double {
         return this.timeInSecond;
      }

      public operator fun component2(): Double? {
         return this.jvmMemoryUsed;
      }

      public operator fun component3(): Double? {
         return this.jvmMemoryUsedMax;
      }

      public operator fun component4(): Double? {
         return this.jvmMemoryCommitted;
      }

      public operator fun component5(): Double? {
         return this.freePhysicalSize;
      }

      public operator fun component6(): Double? {
         return this.cpuSystemCpuLoad;
      }

      public fun copy(
         timeInSecond: Double = this.timeInSecond,
         jvmMemoryUsed: Double? = this.jvmMemoryUsed,
         jvmMemoryUsedMax: Double? = this.jvmMemoryUsedMax,
         jvmMemoryCommitted: Double? = this.jvmMemoryCommitted,
         freePhysicalSize: Double? = this.freePhysicalSize,
         cpuSystemCpuLoad: Double? = this.cpuSystemCpuLoad
      ): cn.sast.framework.metrics.MetricsMonitor.MetricsSnapshot {
         return new MetricsMonitor.MetricsSnapshot(timeInSecond, jvmMemoryUsed, jvmMemoryUsedMax, jvmMemoryCommitted, freePhysicalSize, cpuSystemCpuLoad);
      }

      public override fun toString(): String {
         return "MetricsSnapshot(timeInSecond=${this.timeInSecond}, jvmMemoryUsed=${this.jvmMemoryUsed}, jvmMemoryUsedMax=${this.jvmMemoryUsedMax}, jvmMemoryCommitted=${this.jvmMemoryCommitted}, freePhysicalSize=${this.freePhysicalSize}, cpuSystemCpuLoad=${this.cpuSystemCpuLoad})";
      }

      public override fun hashCode(): Int {
         return (
                  (
                           (
                                    (java.lang.Double.hashCode(this.timeInSecond) * 31 + (if (this.jvmMemoryUsed == null) 0 else this.jvmMemoryUsed.hashCode()))
                                          * 31
                                       + (if (this.jvmMemoryUsedMax == null) 0 else this.jvmMemoryUsedMax.hashCode())
                                 )
                                 * 31
                              + (if (this.jvmMemoryCommitted == null) 0 else this.jvmMemoryCommitted.hashCode())
                        )
                        * 31
                     + (if (this.freePhysicalSize == null) 0 else this.freePhysicalSize.hashCode())
               )
               * 31
            + (if (this.cpuSystemCpuLoad == null) 0 else this.cpuSystemCpuLoad.hashCode());
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is MetricsMonitor.MetricsSnapshot) {
            return false;
         } else {
            val var2: MetricsMonitor.MetricsSnapshot = other as MetricsMonitor.MetricsSnapshot;
            if (java.lang.Double.compare(this.timeInSecond, (other as MetricsMonitor.MetricsSnapshot).timeInSecond) != 0) {
               return false;
            } else if (!(this.jvmMemoryUsed == var2.jvmMemoryUsed)) {
               return false;
            } else if (!(this.jvmMemoryUsedMax == var2.jvmMemoryUsedMax)) {
               return false;
            } else if (!(this.jvmMemoryCommitted == var2.jvmMemoryCommitted)) {
               return false;
            } else if (!(this.freePhysicalSize == var2.freePhysicalSize)) {
               return false;
            } else {
               return this.cpuSystemCpuLoad == var2.cpuSystemCpuLoad;
            }
         }
      }

      public companion object {
         public fun serializer(): KSerializer<cn.sast.framework.metrics.MetricsMonitor.MetricsSnapshot> {
            return MetricsMonitor.MetricsSnapshot.$serializer.INSTANCE as KSerializer<MetricsMonitor.MetricsSnapshot>;
         }
      }
   }

   @Serializable
   private data class PhaseTimer(name: String, start: Double, elapsedTime: Double, phaseStartCount: Int, averageElapsedTime: Double, end: Double) {
      public final val name: String
      public final val start: Double
      public final val elapsedTime: Double
      public final val phaseStartCount: Int
      public final val averageElapsedTime: Double
      public final val end: Double

      init {
         this.name = name;
         this.start = start;
         this.elapsedTime = elapsedTime;
         this.phaseStartCount = phaseStartCount;
         this.averageElapsedTime = averageElapsedTime;
         this.end = end;
      }

      public operator fun component1(): String {
         return this.name;
      }

      public operator fun component2(): Double {
         return this.start;
      }

      public operator fun component3(): Double {
         return this.elapsedTime;
      }

      public operator fun component4(): Int {
         return this.phaseStartCount;
      }

      public operator fun component5(): Double {
         return this.averageElapsedTime;
      }

      public operator fun component6(): Double {
         return this.end;
      }

      public fun copy(
         name: String = this.name,
         start: Double = this.start,
         elapsedTime: Double = this.elapsedTime,
         phaseStartCount: Int = this.phaseStartCount,
         averageElapsedTime: Double = this.averageElapsedTime,
         end: Double = this.end
      ): cn.sast.framework.metrics.MetricsMonitor.PhaseTimer {
         return new MetricsMonitor.PhaseTimer(name, start, elapsedTime, phaseStartCount, averageElapsedTime, end);
      }

      public override fun toString(): String {
         return "PhaseTimer(name=${this.name}, start=${this.start}, elapsedTime=${this.elapsedTime}, phaseStartCount=${this.phaseStartCount}, averageElapsedTime=${this.averageElapsedTime}, end=${this.end})";
      }

      public override fun hashCode(): Int {
         return (
                  (
                           ((this.name.hashCode() * 31 + java.lang.Double.hashCode(this.start)) * 31 + java.lang.Double.hashCode(this.elapsedTime)) * 31
                              + Integer.hashCode(this.phaseStartCount)
                        )
                        * 31
                     + java.lang.Double.hashCode(this.averageElapsedTime)
               )
               * 31
            + java.lang.Double.hashCode(this.end);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is MetricsMonitor.PhaseTimer) {
            return false;
         } else {
            val var2: MetricsMonitor.PhaseTimer = other as MetricsMonitor.PhaseTimer;
            if (!(this.name == (other as MetricsMonitor.PhaseTimer).name)) {
               return false;
            } else if (java.lang.Double.compare(this.start, var2.start) != 0) {
               return false;
            } else if (java.lang.Double.compare(this.elapsedTime, var2.elapsedTime) != 0) {
               return false;
            } else if (this.phaseStartCount != var2.phaseStartCount) {
               return false;
            } else if (java.lang.Double.compare(this.averageElapsedTime, var2.averageElapsedTime) != 0) {
               return false;
            } else {
               return java.lang.Double.compare(this.end, var2.end) == 0;
            }
         }
      }

      public companion object {
         public fun serializer(): KSerializer<cn.sast.framework.metrics.MetricsMonitor.PhaseTimer> {
            return MetricsMonitor.PhaseTimer.$serializer.INSTANCE as KSerializer<MetricsMonitor.PhaseTimer>;
         }
      }
   }
}
