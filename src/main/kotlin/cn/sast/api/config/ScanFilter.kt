package cn.sast.api.config

import cn.sast.common.IResDirectory
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.IMatchItem
import com.feysh.corax.config.api.rules.ProcessRule.IMatchTarget
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import com.feysh.corax.config.api.rules.ProcessRule.FileMatch.MatchTarget
import java.io.Closeable
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.Collections
import java.util.TreeMap
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JvmStreamsKt
import soot.SootClass
import soot.SootField
import soot.SootMethod

@SourceDebugExtension(["SMAP\nScanFilter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ScanFilter.kt\ncn/sast/api/config/ScanFilter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n*L\n1#1,106:1\n1#2:107\n1#2:110\n72#3,2:108\n*S KotlinDebug\n*F\n+ 1 ScanFilter.kt\ncn/sast/api/config/ScanFilter\n*L\n96#1:110\n96#1:108,2\n*E\n"])
public class ScanFilter(mainConfig: MainConfig, matchContentProvider: MatchContentProvider = (new MatchContentProviderImpl(mainConfig)) as MatchContentProvider) :
   MatchContentProvider {
   public final val mainConfig: MainConfig
   private final val matchContentProvider: MatchContentProvider

   public final lateinit var processRegex: ProcessRegex
      internal set

   private final val show: String
      private final get() {
         if (`$this$show` != null) {
            val var10000: java.lang.String = `$this$show`.toString();
            if (var10000 != null) {
               return var10000;
            }
         }

         return "{no matched rule}";
      }


   private final val filterDiagnostics: ConcurrentHashMap<String, MutableSet<String>>

   init {
      this.mainConfig = mainConfig;
      this.matchContentProvider = matchContentProvider;
      this.filterDiagnostics = new ConcurrentHashMap<>();
   }

   public fun update(value: ProjectConfig) {
      this.setProcessRegex(value.getProcessRegex());
   }

   public fun getActionOf(rule: List<IMatchItem>, origAction: String?, matchTarget: IMatchTarget, prefix: String? = null): ScanAction {
      val var5: Pair = ProcessRule.INSTANCE.matches(rule, matchTarget);
      val rulex: ProcessRule.IMatchItem = var5.component1() as ProcessRule.IMatchItem;
      val finalAction: ProcessRule.ScanAction = var5.component2() as ProcessRule.ScanAction;
      if (origAction != null) {
         var var10001: java.lang.String;
         var var10002: Any;
         label17: {
            var10001 = "$origAction -> $finalAction. rule= ${this.getShow(rulex)}";
            if (rulex != null) {
               var10002 = rulex.getOp();
               if (var10002 != null) {
                  break label17;
               }
            }

            var10002 = "Keep";
         }

         var var10003: java.lang.String = prefix;
         if (prefix == null) {
            var10003 = "";
         }

         this.add(var10001, "$var10002: $var10003 $matchTarget");
      }

      return var5.getSecond() as ProcessRule.ScanAction;
   }

   public fun getActionOfClassPath(origAction: String?, classpath: Path, prefix: String? = null): ScanAction {
      return this.getActionOf(this.getProcessRegex().getClasspathRules(), origAction, this.getClassPath(classpath), prefix);
   }

   public fun getActionOf(origAction: String?, file: Path, prefix: String? = null): ScanAction {
      return this.getActionOf(this.getProcessRegex().getFileRules(), origAction, this.get(file), prefix);
   }

   public fun getActionOf(origAction: String?, sc: SootClass, prefix: String? = null): ScanAction {
      return this.getActionOf(this.getProcessRegex().getClazzRules(), origAction, this.get(sc), prefix);
   }

   public fun getActionOf(origAction: String?, sm: SootMethod, prefix: String? = null): ScanAction {
      return this.getActionOf(this.getProcessRegex().getClazzRules(), origAction, this.get(sm), prefix);
   }

   public fun getActionOf(origAction: String?, sf: SootField, prefix: String? = null): ScanAction {
      return this.getActionOf(this.getProcessRegex().getClazzRules(), origAction, this.get(sf), prefix);
   }

   public fun add(key: String, item: String) {
      val `$this$getOrPut$iv`: ConcurrentMap = this.filterDiagnostics;
      var var10000: Any = this.filterDiagnostics.get(key);
      if (var10000 == null) {
         val `default$iv`: Any = Collections.synchronizedSet(new TreeSet());
         var10000 = `$this$getOrPut$iv`.putIfAbsent(key, `default$iv`);
         if (var10000 == null) {
            var10000 = `default$iv`;
         }
      }

      (var10000 as java.util.Set).add(item);
   }

   public fun dump(dir: IResDirectory) {
      label19: {
         dir.mkdirs();
         val var10000: Path = dir.resolve("scan-classifier-info.json").getPath();
         val var10001: Array<OpenOption> = new OpenOption[0];
         val var11: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
         val var2: Closeable = var11;
         var var3: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  ScanFilter.ClassFilerRecord.Companion.serializer() as SerializationStrategy,
                  new ScanFilter.ClassFilerRecord(new TreeMap<>(this.filterDiagnostics)),
                  var2 as OutputStream
               );
            } catch (var6: java.lang.Throwable) {
               var3 = var6;
               throw var6;
            }
         } catch (var7: java.lang.Throwable) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   public override fun get(file: Path): MatchTarget {
      return this.matchContentProvider.get(file);
   }

   public override fun get(sf: SootField): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      return this.matchContentProvider.get(sf);
   }

   public override fun get(sm: SootMethod): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      return this.matchContentProvider.get(sm);
   }

   public override fun get(sc: SootClass): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      return this.matchContentProvider.get(sc);
   }

   public override fun getClassPath(classpath: Path): com.feysh.corax.config.api.rules.ProcessRule.ClassPathMatch.MatchTarget {
      return this.matchContentProvider.getClassPath(classpath);
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$3`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      return Unit.INSTANCE;
   }

   @Serializable
   private class ClassFilerRecord(filterDiagnostics: Map<String, MutableSet<String>>) {
      private final val filterDiagnostics: Map<String, MutableSet<String>>

      init {
         this.filterDiagnostics = filterDiagnostics;
      }

      public companion object {
         public fun serializer(): KSerializer<cn.sast.api.config.ScanFilter.ClassFilerRecord> {
            return ScanFilter.ClassFilerRecord.$serializer.INSTANCE as KSerializer<ScanFilter.ClassFilerRecord>;
         }
      }
   }

   public companion object {
      private final val jsonFormat: Json
   }
}
