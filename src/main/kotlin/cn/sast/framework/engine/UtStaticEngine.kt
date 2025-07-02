package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.result.IUTBotResultCollector
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jdk7.AutoCloseableKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.FileUtil
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import org.utbot.common.PathUtil
import org.utbot.engine.Mocker
import org.utbot.framework.UtSettings
import org.utbot.framework.codegen.CodeGenerator
import org.utbot.framework.codegen.domain.DomainKt
import org.utbot.framework.codegen.domain.ForceStaticMocking
import org.utbot.framework.codegen.domain.NoStaticMocking
import org.utbot.framework.plugin.api.ClassId
import org.utbot.framework.plugin.api.MockStrategyApi
import org.utbot.framework.plugin.api.TestCaseGenerator
import org.utbot.framework.plugin.api.TreatOverflowAsError
import org.utbot.framework.plugin.api.UtMethodTestSet
import org.utbot.framework.plugin.api.util.UtContext
import org.utbot.framework.plugin.services.JdkInfo
import org.utbot.framework.plugin.services.JdkInfoDefaultProvider
import org.utbot.framework.util.EngineUtilsKt
import org.utbot.framework.util.SootUtilsKt
import soot.Scene
import soot.SootMethod
import soot.options.Options

@SourceDebugExtension(["SMAP\nUtStaticEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UtStaticEngine.kt\ncn/sast/framework/engine/UtStaticEngine\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 UtContext.kt\norg/utbot/framework/plugin/api/util/UtContextKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,229:1\n1557#2:230\n1628#2,3:231\n1557#2:234\n1628#2,3:235\n1628#2,3:241\n1557#2:251\n1628#2,3:252\n74#3,3:238\n77#3,5:244\n13409#4,2:249\n37#5,2:255\n*S KotlinDebug\n*F\n+ 1 UtStaticEngine.kt\ncn/sast/framework/engine/UtStaticEngine\n*L\n152#1:230\n152#1:231,3\n161#1:234\n161#1:235,3\n167#1:241,3\n81#1:251\n81#1:252,3\n163#1:238,3\n163#1:244,5\n195#1:249,2\n81#1:255,2\n*E\n"])
public open class UtStaticEngine(mainConfig: MainConfig, utConfig: UtStaticEngineConfiguration) {
   public final val mainConfig: MainConfig
   public final val utConfig: UtStaticEngineConfiguration

   public final var classesToMockAlways: Set<String>
      internal final set(fullyQualifiedNames) {
         for (java.lang.String fullyQualifiedName : fullyQualifiedNames) {
            try {
               Class.forName(fullyQualifiedName, false, this.getClassLoader());
            } catch (var6: ClassNotFoundException) {
               logger.error("", var6);
            }
         }

         this.classesToMockAlways = fullyQualifiedNames;
      }


   public final val classpath: Set<IResource>
      public final get() {
         return this.mainConfig.get_expand_class_path();
      }


   public final val useDefaultJavaClassPath: Boolean
      public final get() {
         return this.mainConfig.getUseDefaultJavaClassPath();
      }


   public final val classLoader: URLClassLoader
      public final get() {
         return this.classLoader$delegate.getValue() as URLClassLoader;
      }


   public final val dependencyPaths: String
      public final get() {
         val var10000: java.lang.String = System.getProperty("java.class.path");
         return var10000;
      }


   init {
      this.mainConfig = mainConfig;
      this.utConfig = utConfig;
      this.classesToMockAlways = this.utConfig.getClassesToMockAlways();
      this.classLoader$delegate = LazyKt.lazy(UtStaticEngine::classLoader_delegate$lambda$1);
   }

   public fun saveToFile(snippet: String, outputPath: String?) {
      if (outputPath != null) {
         Files.write(PathUtil.INSTANCE.toPath(outputPath), CollectionsKt.listOf(snippet));
      }
   }

   private fun initializeCodeGenerator(testFramework: String, classUnderTest: ClassId): CodeGenerator {
      val generateWarningsForStaticMocking: Boolean = this.utConfig.getForceStaticMocking() === ForceStaticMocking.FORCE
         && this.utConfig.getStaticsMocking() is NoStaticMocking;
      return new CodeGenerator(
         classUnderTest,
         null,
         false,
         DomainKt.testFrameworkByName(testFramework),
         null,
         this.utConfig.getStaticsMocking(),
         this.utConfig.getForceStaticMocking(),
         generateWarningsForStaticMocking,
         this.utConfig.getCodegenLanguage(),
         null,
         null,
         null,
         false,
         null,
         15894,
         null
      );
   }

   public fun generateTest(classUnderTest: ClassId, testClassname: String, testCases: List<UtMethodTestSet>): String {
      return this.initializeCodeGenerator(this.utConfig.getTestFramework(), classUnderTest).generateAsString(testCases, testClassname);
   }

   public fun generateTestsForClass(classIdUnderTest: ClassId, testCases: List<UtMethodTestSet>, output: IResource) {
      val testClassName: java.lang.String = "${classIdUnderTest.getSimpleName()}Test";
      val testClassBody: java.lang.String = this.generateTest(classIdUnderTest, testClassName, testCases);
      if (logger.isTraceEnabled()) {
         logger.info(UtStaticEngine::generateTestsForClass$lambda$3);
      }

      val outputArgAsFile: File = output.getFile();
      if (!outputArgAsFile.exists()) {
         outputArgAsFile.mkdirs();
      }

      val outputDir: java.lang.String = "$outputArgAsFile${File.separator}";
      val var10001: java.lang.Iterable = CollectionsKt.dropLast(StringsKt.split$default(classIdUnderTest.getJvmName(), new char[]{'.'}, false, 0, 6, null), 1);
      val var10002: java.lang.String = File.separator;
      val var10: Path = Paths.get("$outputDir${CollectionsKt.joinToString$default(var10001, var10002, null, null, 0, null, null, 62, null)}");
      var10.toFile().mkdirs();
      this.saveToFile(testClassBody, "$var10${File.separator}$testClassName.java");
   }

   protected fun initializeGenerator(): TestCaseGenerator {
      UtSettings.INSTANCE.setTreatOverflowAsError(this.utConfig.getTreatOverflowAsError() === TreatOverflowAsError.AS_ERROR);
      val jdkInfo: JdkInfo = new JdkInfoDefaultProvider().getInfo();
      val `$this$map$iv`: java.lang.Iterable = this.mainConfig.getProcessDir() as java.lang.Iterable;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as IResource).getPath());
      }

      val var10000: java.util.List = `destination$iv$iv` as java.util.List;
      val var10001: java.lang.Iterable = this.getClasspath();
      val var10002: java.lang.String = File.pathSeparator;
      return new TestCaseGenerator(
         var10000,
         CollectionsKt.joinToString$default(var10001, var10002, null, null, 0, null, UtStaticEngine::initializeGenerator$lambda$5, 30, null),
         this.getDependencyPaths(),
         jdkInfo,
         null,
         null,
         false,
         false,
         112,
         null
      );
   }

   public fun runTest(soot: SootCtx, test: TestCaseGenerator, entries: Set<SootMethod>, result: IUTBotResultCollector) {
      label45: {
         val `context$iv`: java.lang.Iterable = entries;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(entries, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add(EngineUtilsKt.getExecutableId(var12 as SootMethod));
         }

         val targetMethods: java.util.List = `destination$iv$iv` as java.util.List;
         val `$this$mapTo$iv$iv`: AutoCloseable = UtContext.Companion.setUtContext(new UtContext(this.getClassLoader()));
         var var33: java.lang.Throwable = null;

         try {
            try {
               try {
                  val var36: MockStrategyApi = MockStrategyApi.NO_MOCKS;
                  val var37: java.lang.Iterable = SetsKt.plus(Mocker.Companion.getDefaultSuperClassesToMockAlwaysNames(), this.classesToMockAlways);
                  val `destination$iv`: java.util.Collection = new LinkedHashSet();

                  for (Object item$iv : var37) {
                     val it: java.lang.String = `item$iv` as java.lang.String;
                     `destination$iv`.add(new ClassId(it, null, false, 6, null));
                  }

                  logger.info(UtStaticEngine::runTest$lambda$9$lambda$8);
               } catch (var25: Exception) {
                  KotlinLogging.INSTANCE.logger("withUtContext").error(new UtStaticEngine$runTest$$inlined$withUtContext$1(var25));
                  throw var25;
               }
            } catch (var26: java.lang.Throwable) {
               var33 = var26;
               throw var26;
            }
         } catch (var27: java.lang.Throwable) {
            AutoCloseableKt.closeFinally(`$this$mapTo$iv$iv`, var33);
         }

         AutoCloseableKt.closeFinally(`$this$mapTo$iv$iv`, null);
      }
   }

   public fun initUt(soot: SootCtx) {
      UtSettings.INSTANCE.setUseFuzzing(false);
      UtSettings.INSTANCE.setUseSandbox(false);
      UtSettings.INSTANCE.setUseConcreteExecution(false);
      UtSettings.INSTANCE.setUseCustomJavaDocTags(false);
      UtSettings.INSTANCE.setEnableSummariesGeneration(false);
      UtSettings.INSTANCE.setCheckNpeInNestedNotPrivateMethods(true);
      UtSettings.INSTANCE.setPreferredCexOption(false);
      UtSettings.INSTANCE.setUseAssembleModelGenerator(false);
      val var10000: MainConfig = this.mainConfig;
      val var10001: PersistentSet = this.mainConfig.getClasspath();
      val var10002: FileUtil = FileUtil.INSTANCE;
      val `$this$forEach$iv`: Array<Class> = SootUtilsKt.getClassesToLoad();
      val var15: java.lang.String = var10002.isolateClassFiles(Arrays.copyOf(`$this$forEach$iv`, `$this$forEach$iv`.length)).getCanonicalPath();
      var10000.setClasspath(var10001.add(var15));
      val scene: Scene = Scene.v();
      scene.setSootClassPath(null);
      val var14: Options = Options.v();
      soot.configureSootClassPath(var14);

      for (Object element$iv : $this$forEach$iv) {
         scene.addBasicClass(`element$iv`.getName(), 3);
         scene.forceResolve(`element$iv`.getName(), 3).setApplicationClass();
      }
   }

   public suspend fun analyzeSuspend(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) {
      UtSettings.INSTANCE.setTreatOverflowAsError(false);
      val test: TestCaseGenerator = this.initializeGenerator();
      this.initUt(soot);
      val var10000: Any = provider.getIterator()
         .collect(
            new FlowCollector(soot, this, test, result) {
               {
                  this.$soot = `$soot`;
                  this.this$0 = `$receiver`;
                  this.$test = `$test`;
                  this.$result = `$result`;
               }

               // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               public final Object emit(IEntryPointProvider.AnalyzeTask task, Continuation<? super Unit> $completion) {
                  label38: {
                     val entries: java.util.Set = CollectionsKt.toMutableSet(task.getEntries());
                     val var10000: java.util.Set = task.getAdditionalEntries();
                     if (var10000 != null) {
                        Boxing.boxBoolean(entries.addAll(var10000));
                     }

                     Scene.v().setEntryPoints(CollectionsKt.toList(entries));
                     this.$soot.constructCallGraph();
                     val `$this$bracket_u24default$iv`: LoggerWithLogMethod = LoggingKt.info(UtStaticEngine.access$getLogger$cp());
                     val `msg$iv`: java.lang.String = "Run symbolic analysis for task: ${task.getName()}";
                     val var25: UtStaticEngine = this.this$0;
                     val var8: SootCtx = this.$soot;
                     val var9: TestCaseGenerator = this.$test;
                     val var10: IUTBotResultCollector = this.$result;
                     `$this$bracket_u24default$iv`.getLogMethod().invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$1(`msg$iv`));
                     val `startTime$iv`: LocalDateTime = LocalDateTime.now();
                     var `alreadyLogged$iv`: Boolean = false;
                     val `res$iv`: ObjectRef = new ObjectRef();
                     `res$iv`.element = Maybe.Companion.empty();

                     try {
                        try {
                           ;
                        } catch (var20: java.lang.Throwable) {
                           `$this$bracket_u24default$iv`.getLogMethod()
                              .invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$4(`startTime$iv`, `msg$iv`, var20));
                           `alreadyLogged$iv` = true;
                           throw var20;
                        }
                     } catch (var21: java.lang.Throwable) {
                        if (!`alreadyLogged$iv`) {
                           if ((`res$iv`.element as Maybe).getHasValue()) {
                              `$this$bracket_u24default$iv`.getLogMethod()
                                 .invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$5(`startTime$iv`, `msg$iv`, `res$iv`));
                           } else {
                              `$this$bracket_u24default$iv`.getLogMethod()
                                 .invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$6(`startTime$iv`, `msg$iv`));
                           }
                        }
                     }

                     if ((`res$iv`.element as Maybe).getHasValue()) {
                        `$this$bracket_u24default$iv`.getLogMethod()
                           .invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$2(`startTime$iv`, `msg$iv`, `res$iv`));
                     } else {
                        `$this$bracket_u24default$iv`.getLogMethod()
                           .invoke(new UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$3(`startTime$iv`, `msg$iv`));
                     }

                     return Unit.INSTANCE;
                  }
               }
            },
            `$completion`
         );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   public fun analyze(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) {
      BuildersKt.runBlocking$default(
         null,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, soot, provider, result, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
         ) as Function2,
         1,
         null
      );
   }

   @JvmStatic
   fun `classLoader_delegate$lambda$1`(`this$0`: UtStaticEngine): URLClassLoader {
      val `$this$toTypedArray$iv`: java.lang.Iterable = SetsKt.plus(`this$0`.getClasspath(), `this$0`.mainConfig.getProcessDir() as java.lang.Iterable);
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$toTypedArray$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as IResource).getUrl());
      }

      val urls: Array<URL> = (`destination$iv$iv` as java.util.List).toArray(new URL[0]);
      return if (`this$0`.getUseDefaultJavaClassPath()) new URLClassLoader(urls, ClassLoader.getSystemClassLoader()) else new URLClassLoader(urls, null);
   }

   @JvmStatic
   fun `generateTestsForClass$lambda$3`(`$testClassBody`: java.lang.String): Any {
      return `$testClassBody`;
   }

   @JvmStatic
   fun `initializeGenerator$lambda$5`(it: IResource): java.lang.CharSequence {
      return it.getAbsolutePath();
   }

   @JvmStatic
   fun `runTest$lambda$9$lambda$8`(`$testCases`: java.util.List): Any {
      return "symbolic result: ${`$testCases`.size()}";
   }

   @JvmStatic
   fun `logger$lambda$12`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$1` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$2` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$3` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$4` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$5` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$6` : Function0<Object> {
   fun `UtStaticEngine$analyze$1$invokeSuspend$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$1` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$2` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$3` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$4` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$5` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$6` : Function0<Object> {
   fun `UtStaticEngine$analyzeSuspend$2$emit$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `UtStaticEngine$runTest$$inlined$withUtContext$1` : Function0<Object> {
   fun `UtStaticEngine$runTest$$inlined$withUtContext$1`(`$e`: Exception) {
      this.$e = `$e`;
   }

   fun invoke(): Any {
      return this.$e;
   }
}
