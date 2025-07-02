package cn.sast.api.config

import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.util.IMonitor
import cn.sast.common.FileSystemLocator
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.OS
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.FileSystemLocator.TraverseMode
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IRelativePath
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.rules.ProcessRule
import java.io.File
import java.nio.charset.Charset
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.LinkedHashSet
import java.util.Locale
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import mu.KLogger
import soot.Scene

@SourceDebugExtension(["SMAP\nMainConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,463:1\n1611#2,9:464\n1863#2:473\n1864#2:475\n1620#2:476\n774#2:477\n865#2,2:478\n1557#2:480\n1628#2,3:481\n1619#2:484\n1863#2:485\n1864#2:488\n1620#2:489\n1454#2,5:490\n1628#2,3:495\n1557#2:499\n1628#2,3:500\n1755#2,3:503\n1755#2,3:506\n1755#2,3:509\n774#2:514\n865#2,2:515\n774#2:517\n865#2,2:518\n1#3:474\n1#3:486\n1#3:487\n1#3:498\n12574#4,2:512\n*S KotlinDebug\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfig\n*L\n182#1:464,9\n182#1:473\n182#1:475\n182#1:476\n197#1:477\n197#1:478,2\n197#1:480\n197#1:481,3\n224#1:484\n224#1:485\n224#1:488\n224#1:489\n262#1:490,5\n270#1:495,3\n303#1:499\n303#1:500,3\n342#1:503,3\n343#1:506,3\n344#1:509,3\n405#1:514\n405#1:515,2\n412#1:517\n412#1:518,2\n182#1:474\n224#1:487\n352#1:512,2\n*E\n"])
public class MainConfig(sourceEncoding: Charset = Charsets.UTF_8,
   monitor: IMonitor? = null,
   saConfig: SaConfig? = null,
   output_dir: IResDirectory = Resource.INSTANCE.dirOf("out/test-out"),
   dumpSootScene: Boolean = false,
   androidPlatformDir: String? = null,
   use_wrapper: Boolean = true,
   hideNoSource: Boolean = false,
   traverseMode: TraverseMode = FileSystemLocator.TraverseMode.RecursivelyIndexArchive,
   useDefaultJavaClassPath: Boolean = true,
   processDir: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
   classpath: PersistentSet<String> = ExtensionsKt.persistentSetOf(),
   sourcePath: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
   projectRoot: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
   autoAppClasses: PersistentSet<IResource> = ExtensionsKt.persistentSetOf(),
   autoAppTraverseMode: TraverseMode = FileSystemLocator.TraverseMode.RecursivelyIndexArchive,
   autoAppSrcInZipScheme: Boolean = true,
   skipClass: Boolean = false,
   incrementAnalyze: IncrementalAnalyze? = null,
   enableLineNumbers: Boolean = true,
   enableOriginalNames: Boolean = true,
   output_format: Int = 14,
   throw_analysis: Int = 3,
   process_multiple_dex: Boolean = true,
   appClasses: Set<String> = SetsKt.emptySet(),
   src_precedence: SrcPrecedence = SrcPrecedence.prec_apk_class_jimple,
   ecj_options: List<String> = CollectionsKt.emptyList(),
   sunBootClassPath: String? = System.getProperty("sun.boot.class.path"),
   javaExtDirs: String? = System.getProperty("java.ext.dirs"),
   hashAbspathInPlist: Boolean = false,
   deCompileIfNotExists: Boolean = true,
   enableCodeMetrics: Boolean = true,
   prepend_classpath: Boolean = false,
   whole_program: Boolean = true,
   no_bodies_for_excluded: Boolean = true,
   allow_phantom_refs: Boolean = true,
   enableReflection: Boolean = true,
   staticFieldTrackingMode: StaticFieldTrackingMode = StaticFieldTrackingMode.ContextFlowSensitive,
   callGraphAlgorithm: String = "insens",
   callGraphAlgorithmBuiltIn: String = "cha",
   memoryThreshold: Double = 0.9
) {
   public final val sourceEncoding: Charset

   public final var monitor: IMonitor?
      internal set

   public final var saConfig: SaConfig?
      internal set

   public final var output_dir: IResDirectory
      internal set

   public final var dumpSootScene: Boolean
      internal set

   public final var use_wrapper: Boolean
      internal set

   public final var hideNoSource: Boolean
      internal set

   public final var traverseMode: TraverseMode
      internal set

   public final var useDefaultJavaClassPath: Boolean
      internal set

   public final var processDir: PersistentSet<IResource>
      internal set

   public final var classpath: PersistentSet<String>
      internal set

   public final var projectRoot: PersistentSet<IResource>
      internal set

   public final var autoAppClasses: PersistentSet<IResource>
      internal set

   public final var autoAppTraverseMode: TraverseMode
      internal set

   public final var autoAppSrcInZipScheme: Boolean
      internal set

   public final var skipClass: Boolean
      internal set

   public final var incrementAnalyze: IncrementalAnalyze?
      internal set

   public final var enableLineNumbers: Boolean
      internal set

   public final var enableOriginalNames: Boolean
      internal set

   public final var output_format: Int
      internal set

   public final var throw_analysis: Int
      internal set

   public final var process_multiple_dex: Boolean
      internal set

   public final var appClasses: Set<String>
      internal set

   public final var src_precedence: SrcPrecedence
      internal set

   public final var ecj_options: List<String>
      internal set

   public final var sunBootClassPath: String?
      internal set

   public final var javaExtDirs: String?
      internal set

   public final var hashAbspathInPlist: Boolean
      internal set

   public final var deCompileIfNotExists: Boolean
      internal set

   public final var enableCodeMetrics: Boolean
      internal set

   public final var prepend_classpath: Boolean
      internal set

   public final var whole_program: Boolean
      internal set

   public final var no_bodies_for_excluded: Boolean
      internal set

   public final var allow_phantom_refs: Boolean
      internal set

   public final var enableReflection: Boolean
      internal set

   public final var staticFieldTrackingMode: StaticFieldTrackingMode
      internal set

   public final var memoryThreshold: Double
      internal set

   public final var version: String?
      internal set

   public final var checkerInfo: Lazy<CheckerInfoGenResult?>?
      internal set

   public final val configDirs: MutableList<IResource>

   public final val checkerInfoDir: IResDirectory?
      public final get() {
         return MainConfigKt.checkerInfoDir(this.configDirs, false);
      }


   public final val checker_info_csv: IResFile?
      public final get() {
         val var10000: IResDirectory = this.getCheckerInfoDir();
         if (var10000 != null) {
            val var1: IResource = var10000.resolve("checker_info.csv");
            if (var1 != null) {
               return var1.toFile();
            }
         }

         return null;
      }


   public final val rule_sort_yaml: IResFile?
      public final get() {
         val var10000: IResDirectory = this.getCheckerInfoDir();
         if (var10000 != null) {
            val var1: IResource = var10000.resolve("rule_sort.yaml");
            if (var1 != null) {
               return var1.toFile();
            }
         }

         return null;
      }


   public final var apponly: Boolean
      internal set

   public final var sourcePath: PersistentSet<IResource>
      internal final set(value) {
         this.sourcePath = value;
         val `$this$mapNotNull$iv`: java.lang.Iterable = value as java.lang.Iterable;
         val `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            val it: IResource = `element$iv$iv$iv` as IResource;
            var var10000: FileSystem;
            if (!(`element$iv$iv$iv` as IResource).getZipLike()) {
               var10000 = null;
            } else {
               label33: {
                  var var15: FileSystem;
                  try {
                     var15 = Resource.INSTANCE.getZipFileSystem(it.getPath());
                  } catch (var20: Exception) {
                     var10000 = null;
                     break label33;
                  }

                  var10000 = var15;
               }
            }

            if (var10000 != null) {
               `destination$iv$iv`.add(var10000);
            }
         }

         this.sourcePathZFS = ExtensionsKt.toPersistentSet(`destination$iv$iv` as java.util.List);
      }


   public final var sourcePathZFS: PersistentSet<FileSystem>
      private set

   public final var rootPathsForConvertRelativePath: List<IResource>
      internal final set(value) {
         var `$this$map$iv`: java.lang.Iterable = value;
         var `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$map$iv) {
            if ((`item$iv$iv` as IResource).isDirectory() || (`item$iv$iv` as IResource).getZipLike()) {
               `destination$iv$iv`.add(`item$iv$iv`);
            }
         }

         `$this$map$iv` = `destination$iv$iv` as java.util.List;
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add((var19 as IResource).getAbsolute().getNormalize().toString());
         }

         this.allResourcePathNormalized = CollectionsKt.toList(new LinkedHashSet(`destination$iv$iv` as java.util.List));
         this.rootPathsForConvertRelativePath = value;
      }


   public final var allResourcePathNormalized: List<String>
      internal set

   public final val sqlite_report_db: IResFile
      public final get() {
         return this.output_dir.resolve("sqlite").resolve("sqlite_report_coraxjava.db").toFile();
      }


   public final var callGraphAlgorithm: String
      internal final set(value) {
         val var10001: Locale = Locale.getDefault();
         val var3: java.lang.String = value.toLowerCase(var10001);
         this.callGraphAlgorithm = var3;
      }


   public final var callGraphAlgorithmBuiltIn: String
      internal final set(value) {
         val var10001: Locale = Locale.getDefault();
         val var3: java.lang.String = value.toLowerCase(var10001);
         this.callGraphAlgorithmBuiltIn = var3;
      }


   public final var isAndroidScene: Boolean?
      internal set

   public final var parallelsNum: Int
      public final get() {
         return if (this.parallelsNum <= 0) OS.INSTANCE.getMaxThreadNum() else this.parallelsNum;
      }

      public final set(value) {
         if (value > 0) {
            this.parallelsNum = value;
         }
      }


   public final val soot_process_dir: Set<String>
      public final get() {
         val `$this$map$iv`: java.lang.Iterable = SetsKt.plus(this.processDir as java.util.Set, this.autoAppClasses as java.lang.Iterable);
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add((`item$iv$iv` as IResource).expandRes(this.output_dir).getAbsolutePath());
         }

         return CollectionsKt.toSet(this.skipInvalidClassPaths(`destination$iv$iv`));
      }


   public final val soot_output_dir: IResource
      public final get() {
         return this.output_dir.resolve("sootOutPut");
      }


   public open val forceAndroidJar: Boolean?
      public open get() {
         return this.forceAndroidJar$delegate.getValue() as java.lang.Boolean;
      }


   public final var androidPlatformDir: String?
      public final get() {
         return if (this.androidPlatformDir == "ANDROID_JARS") System.getenv("ANDROID_JARS") else this.androidPlatformDir;
      }

      public final set(value) {
         this.androidPlatformDir = value;
         if (value != null && value.length() <= 0) {
            throw new IllegalStateException("Check failed.".toString());
         }
      }


   public final var scanFilter: ScanFilter
      internal set

   public final var projectConfig: ProjectConfig
      internal final set(value) {
         this.projectConfig = value;
         this.scanFilter.update(value);
      }


   init {
      this.sourceEncoding = sourceEncoding;
      this.monitor = monitor;
      this.saConfig = saConfig;
      this.output_dir = output_dir;
      this.dumpSootScene = dumpSootScene;
      this.use_wrapper = use_wrapper;
      this.hideNoSource = hideNoSource;
      this.traverseMode = traverseMode;
      this.useDefaultJavaClassPath = useDefaultJavaClassPath;
      this.processDir = processDir;
      this.classpath = classpath;
      this.projectRoot = projectRoot;
      this.autoAppClasses = autoAppClasses;
      this.autoAppTraverseMode = autoAppTraverseMode;
      this.autoAppSrcInZipScheme = autoAppSrcInZipScheme;
      this.skipClass = skipClass;
      this.incrementAnalyze = incrementAnalyze;
      this.enableLineNumbers = enableLineNumbers;
      this.enableOriginalNames = enableOriginalNames;
      this.output_format = output_format;
      this.throw_analysis = throw_analysis;
      this.process_multiple_dex = process_multiple_dex;
      this.appClasses = appClasses;
      this.src_precedence = src_precedence;
      this.ecj_options = ecj_options;
      this.sunBootClassPath = sunBootClassPath;
      this.javaExtDirs = javaExtDirs;
      this.hashAbspathInPlist = hashAbspathInPlist;
      this.deCompileIfNotExists = deCompileIfNotExists;
      this.enableCodeMetrics = enableCodeMetrics;
      this.prepend_classpath = prepend_classpath;
      this.whole_program = whole_program;
      this.no_bodies_for_excluded = no_bodies_for_excluded;
      this.allow_phantom_refs = allow_phantom_refs;
      this.enableReflection = enableReflection;
      this.staticFieldTrackingMode = staticFieldTrackingMode;
      this.memoryThreshold = memoryThreshold;
      this.configDirs = new ArrayList<>();
      this.sourcePath = ExtensionsKt.persistentSetOf();
      this.sourcePathZFS = ExtensionsKt.persistentSetOf();
      this.rootPathsForConvertRelativePath = CollectionsKt.emptyList();
      this.allResourcePathNormalized = CollectionsKt.emptyList();
      var var10001: Locale = Locale.getDefault();
      val var44: java.lang.String = callGraphAlgorithm.toLowerCase(var10001);
      this.callGraphAlgorithm = var44;
      var10001 = Locale.getDefault();
      val var46: java.lang.String = callGraphAlgorithmBuiltIn.toLowerCase(var10001);
      this.callGraphAlgorithmBuiltIn = var46;
      this.isAndroidScene = false;
      this.parallelsNum = Math.max(OS.INSTANCE.getMaxThreadNum() - 1, 1);
      this.forceAndroidJar$delegate = LazyKt.lazy(MainConfig::forceAndroidJar_delegate$lambda$12);
      this.androidPlatformDir = androidPlatformDir;
      this.scanFilter = new ScanFilter(this, null, 2, null);
      this.projectConfig = new ProjectConfig(null, 1, null);
      this.setSourcePath(sourcePath);
      this.scanFilter.update(this.projectConfig);
   }

   public fun skipInvalidClassPaths(paths: Collection<String>): Set<String> {
      val `$this$mapNotNullTo$iv`: java.lang.Iterable = paths;
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object element$iv$iv : $this$mapNotNullTo$iv) {
         val path: java.lang.String = `element$iv$iv` as java.lang.String;
         var var10000: java.lang.String;
         if (this.isSkipClassSource(`element$iv$iv` as java.lang.String)) {
            logger.info("Exclude class path: $path");
            var10000 = null;
         } else {
            label56: {
               var var22: IResource;
               try {
                  var22 = Resource.INSTANCE.of(path);
                  var22 = if (var22.isFile() && var22.getZipLike()) var22 else null;
               } catch (var21: Exception) {
                  var10000 = path;
                  break label56;
               }

               if (var22 == null) {
                  var10000 = path;
               } else {
                  val zipLikeFile: IResource = var22;

                  try {
                     Resource.INSTANCE.getEntriesUnsafe(zipLikeFile.getPath());
                     var10000 = path;
                  } catch (var20: Exception) {
                     logger.error("skip the invalid archive file: $var22 e: ${var20.getMessage()}");
                     var10000 = null;
                  }
               }
            }
         }

         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as MutableSet<java.lang.String>;
   }

   public fun get_expand_class_path(): Set<IResource> {
      val var10: java.util.Set;
      if (!this.apponly) {
         val `$this$flatMapTo$iv`: java.lang.Iterable = this.classpath as java.lang.Iterable;
         val `destination$iv`: java.util.Collection = new LinkedHashSet();

         for (Object element$iv : $this$flatMapTo$iv) {
            val `list$iv`: java.lang.String = `element$iv` as java.lang.String;
            val var10000: java.util.List = ResourceImplKt.globPaths(`element$iv` as java.lang.String);
            if (var10000 == null) {
               throw new IllegalStateException(("classpath option: \"$`list$iv`\" is invalid or target not exists").toString());
            }

            CollectionsKt.addAll(`destination$iv`, var10000);
         }

         var10 = `destination$iv` as java.util.Set;
      } else {
         var10 = SetsKt.emptySet();
      }

      return var10;
   }

   public fun get_soot_classpath(): Set<String> {
      val javaClasspath: java.lang.Iterable = this.get_expand_class_path();
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : javaClasspath) {
         `destination$iv`.add((`item$iv` as IResource).expandRes(this.output_dir).getAbsolutePath());
      }

      val cps: java.util.Set = `destination$iv` as java.util.Set;
      val var10: java.lang.String = Scene.defaultJavaClassPath();
      if (this.useDefaultJavaClassPath) {
         if (var10.length() > 0) {
            cps.addAll(StringsKt.split$default(var10, new char[]{File.pathSeparatorChar}, false, 0, 6, null));
         }
      }

      return this.skipInvalidClassPaths(cps);
   }

   public fun getAndroidJarClasspath(targetAPKFile: String): String? {
      val var10000: java.lang.Boolean = this.getForceAndroidJar();
      val var7: java.lang.String;
      if (var10000 != null) {
         val androidJar: java.lang.String = if (var10000)
            this.getAndroidPlatformDir()
            else
            Scene.v().getAndroidJarPath(this.getAndroidPlatformDir(), targetAPKFile);
         if (androidJar == null || androidJar.length() == 0) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         }

         var7 = androidJar;
      } else {
         var7 = null;
      }

      return var7;
   }

   public fun isSkipClassSource(path: Path): Boolean {
      return this.scanFilter.getActionOfClassPath("Process", path, null) === ProcessRule.ScanAction.Skip;
   }

   public fun isSkipClassSource(path: String): Boolean {
      var var3: Path;
      try {
         val ignore: Path = Path.of(path);
         val var10001: Array<LinkOption> = new LinkOption[0];
         val var10000: Path = if (Files.exists(ignore, Arrays.copyOf(var10001, var10001.length))) ignore else null;
         if (var10000 == null) {
            return false;
         }

         var3 = var10000;
      } catch (var7: Exception) {
         return false;
      }

      return this.isSkipClassSource(var3);
   }

   public fun validate() {
      var `$this$any$iv`: java.lang.Iterable = this.classpath as java.lang.Iterable;
      var var10000: Boolean;
      if (this.classpath as java.lang.Iterable is java.util.Collection && ((this.classpath as java.lang.Iterable) as java.util.Collection).isEmpty()) {
         var10000 = false;
      } else {
         label125: {
            for (Object element$iv : $this$any$iv) {
               if ((`element$iv` as java.lang.String).length() == 0) {
                  var10000 = true;
                  break label125;
               }
            }

            var10000 = false;
         }
      }

      if (var10000) {
         throw new IllegalStateException("classpath has empty string".toString());
      } else {
         `$this$any$iv` = this.processDir as java.lang.Iterable;
         if (this.processDir as java.lang.Iterable is java.util.Collection && ((this.processDir as java.lang.Iterable) as java.util.Collection).isEmpty()) {
            var10000 = false;
         } else {
            label126: {
               for (Object element$ivx : $this$any$iv) {
                  if ((`element$ivx` as IResource).toString().length() == 0) {
                     var10000 = true;
                     break label126;
                  }
               }

               var10000 = false;
            }
         }

         if (var10000) {
            throw new IllegalStateException("processDir has empty string".toString());
         } else {
            `$this$any$iv` = this.sourcePath as java.lang.Iterable;
            if (this.sourcePath as java.lang.Iterable is java.util.Collection && ((this.sourcePath as java.lang.Iterable) as java.util.Collection).isEmpty()) {
               var10000 = false;
            } else {
               label127: {
                  for (Object element$ivxx : $this$any$iv) {
                     if ((`element$ivxx` as IResource).toString().length() == 0) {
                        var10000 = true;
                        break label127;
                     }
                  }

                  var10000 = false;
               }
            }

            if (var10000) {
               throw new IllegalStateException("sourcePath has empty string".toString());
            }
         }
      }
   }

   public fun isEnable(type: CheckType): Boolean {
      return this.saConfig == null || this.saConfig.isEnable(type);
   }

   public fun isAnyEnable(vararg type: CheckType): Boolean {
      val `$this$any$iv`: Array<Any> = type;
      var var4: Int = 0;
      val var5: Int = type.length;

      var var10000: Boolean;
      while (true) {
         if (var4 >= var5) {
            var10000 = false;
            break;
         }

         if (this.isEnable((CheckType)`$this$any$iv`[var4])) {
            var10000 = true;
            break;
         }

         var4++;
      }

      return var10000;
   }

   private fun getRelative(src: String, path: String): String {
      var var10000: java.lang.String = path.substring(src.length());
      var var4: java.lang.String = StringsKt.replace$default(StringsKt.replace$default(var10000, "\\", "/", false, 4, null), "//", "/", false, 4, null);
      if (!StringsKt.startsWith$default(var4, "/", false, 2, null)) {
         if (StringsKt.startsWith$default(var4, "!/", false, 2, null)) {
            var10000 = var4.substring(1);
         } else {
            var10000 = "/$var4";
         }

         var4 = var10000;
      }

      return var4;
   }

   public fun tryGetRelativePathFromAbsolutePath(abs: String): cn.sast.api.config.MainConfig.RelativePath {
      for (java.lang.String src : this.allResourcePathNormalized) {
         if (StringsKt.startsWith$default(abs, src, false, 2, null)) {
            return new MainConfig.RelativePath(
               StringsKt.removeSuffix(StringsKt.replace$default(StringsKt.replace$default(src, "\\", "/", false, 4, null), "//", "/", false, 4, null), "/"),
               this.getRelative(src, abs)
            );
         }
      }

      return new MainConfig.RelativePath("", StringsKt.replace$default(StringsKt.replace$default(abs, "\\", "/", false, 4, null), "//", "/", false, 4, null));
   }

   public fun tryGetRelativePathFromAbsolutePath(abs: IResource): cn.sast.api.config.MainConfig.RelativePath {
      return this.tryGetRelativePathFromAbsolutePath(abs.toString());
   }

   public fun tryGetRelativePath(p: IResource): cn.sast.api.config.MainConfig.RelativePath {
      return this.tryGetRelativePathFromAbsolutePath(p.getAbsolute().getNormalize());
   }

   public fun tryGetRelativePath(p: Path): cn.sast.api.config.MainConfig.RelativePath {
      return this.tryGetRelativePath(Resource.INSTANCE.of(p));
   }

   public fun <E : Any> simpleDeclIncrementalAnalysisFilter(targets: Collection<E>): Collection<E> {
      val changeFileBasedIncAnalysis: IncrementalAnalyzeByChangeFiles = this.incrementAnalyze as? IncrementalAnalyzeByChangeFiles;
      if ((this.incrementAnalyze as? IncrementalAnalyzeByChangeFiles) != null) {
         val var10000: IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph = changeFileBasedIncAnalysis.getSimpleDeclAnalysisDependsGraph();
         if (var10000 != null) {
            val var13: IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph = var10000;
            val `$this$filter$iv`: java.lang.Iterable = targets;
            val `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               if (var13.shouldReAnalyzeTarget(`element$iv$iv`) != ProcessRule.ScanAction.Skip) {
                  `destination$iv$iv`.add(`element$iv$iv`);
               }
            }

            return `destination$iv$iv`;
         }
      }

      return targets;
   }

   public fun <E : Any> InterProceduralIncrementalAnalysisFilter(targets: Collection<E>): Collection<E> {
      val changeFileBasedIncAnalysis: IncrementalAnalyzeByChangeFiles = this.incrementAnalyze as? IncrementalAnalyzeByChangeFiles;
      if ((this.incrementAnalyze as? IncrementalAnalyzeByChangeFiles) != null) {
         val var10000: IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph = changeFileBasedIncAnalysis.getInterProceduralAnalysisDependsGraph();
         if (var10000 != null) {
            val var13: IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph = var10000;
            val `$this$filter$iv`: java.lang.Iterable = targets;
            val `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               if (var13.shouldReAnalyzeTarget(`element$iv$iv`) != ProcessRule.ScanAction.Skip) {
                  `destination$iv$iv`.add(`element$iv$iv`);
               }
            }

            return `destination$iv$iv`;
         }
      }

      return targets;
   }

   @JvmStatic
   fun `forceAndroidJar_delegate$lambda$12`(`this$0`: MainConfig): java.lang.Boolean {
      val var10000: java.lang.String = `this$0`.getAndroidPlatformDir();
      if (var10000 == null) {
         return null;
      } else if (var10000.length() == 0) {
         throw new RuntimeException("Android platform directory is empty");
      } else {
         val f: File = new File(var10000);
         if (!f.exists()) {
            throw new IllegalArgumentException("androidPlatformDir not exist".toString());
         } else {
            return f.isFile();
         }
      }
   }

   @JvmStatic
   fun `logger$lambda$22`(): Unit {
      return Unit.INSTANCE;
   }

   fun MainConfig() {
      this(
         null,
         null,
         null,
         null,
         false,
         null,
         false,
         false,
         null,
         false,
         null,
         null,
         null,
         null,
         null,
         null,
         false,
         false,
         null,
         false,
         false,
         0,
         0,
         false,
         null,
         null,
         null,
         null,
         null,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         null,
         null,
         null,
         0.0,
         -1,
         511,
         null
      );
   }

   @SourceDebugExtension(["SMAP\nMainConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfig$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,463:1\n1#2:464\n*E\n"])
   public companion object {
      public const val CHECKER_INFO_CSV_NAME: String
      public const val RULE_SORT_YAML: String

      public final var preferredLanguages: List<Language>
         internal final set(value) {
            MainConfig.access$setPreferredLanguages$cp(
               (if (value.isEmpty()) CollectionsKt.listOf(new Language[]{Language.ZH, Language.EN}) else value) as java.util.List
            );
         }


      private final val logger: KLogger
      public final val excludeFiles: Set<String>
   }

   public data class RelativePath(prefix: String, relativePath: String) : IRelativePath {
      public open val prefix: String
      public open val relativePath: String

      public final val identifier: String
         public final get() {
            return "${Companion.short(this.getPrefix())}${this.getRelativePath()}";
         }


      public final val absoluteNormalizePath: String
         public final get() {
            return "${this.getPrefix()}${this.getRelativePath()}";
         }


      init {
         this.prefix = prefix;
         this.relativePath = relativePath;
         if (this.getPrefix().length() > 0) {
            prefixes.add(this.getPrefix());
         }
      }

      public operator fun component1(): String {
         return this.prefix;
      }

      public operator fun component2(): String {
         return this.relativePath;
      }

      public fun copy(prefix: String = this.prefix, relativePath: String = this.relativePath): cn.sast.api.config.MainConfig.RelativePath {
         return new MainConfig.RelativePath(prefix, relativePath);
      }

      public override fun toString(): String {
         return "RelativePath(prefix=${this.prefix}, relativePath=${this.relativePath})";
      }

      public override fun hashCode(): Int {
         return this.prefix.hashCode() * 31 + this.relativePath.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is MainConfig.RelativePath) {
            return false;
         } else {
            val var2: MainConfig.RelativePath = other as MainConfig.RelativePath;
            if (!(this.prefix == (other as MainConfig.RelativePath).prefix)) {
               return false;
            } else {
               return this.relativePath == var2.relativePath;
            }
         }
      }

      @JvmStatic
      fun {
         val var10000: java.util.Set = Collections.synchronizedSet(new LinkedHashSet());
         prefixes = var10000;
      }

      public companion object {
         public final val prefixes: MutableSet<String>

         public fun short(prefix: String): String {
            val i: Int = StringsKt.lastIndexOfAny$default(prefix, CollectionsKt.listOf(new java.lang.String[]{"/", "\\"}), 0, false, 6, null);
            if (i == -1) {
               return prefix;
            } else {
               val var4: java.lang.String = prefix.substring(i + 1);
               return var4;
            }
         }
      }
   }
}
