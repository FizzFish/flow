package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.framework.SootCtx
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.result.IPreAnalysisResultCollector
import cn.sast.idfa.analysis.ProcessInfoView
import com.feysh.corax.cache.analysis.SootInfoCache
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger

@SourceDebugExtension(["SMAP\nPreAnalysis.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysis.kt\ncn/sast/framework/engine/PreAnalysis\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,36:1\n49#2,13:37\n62#2,11:54\n1557#3:50\n1628#3,3:51\n*S KotlinDebug\n*F\n+ 1 PreAnalysis.kt\ncn/sast/framework/engine/PreAnalysis\n*L\n28#1:37,13\n28#1:54,11\n31#1:50\n31#1:51,3\n*E\n"])
public class PreAnalysis(mainConfig: MainConfig) {
   public final val mainConfig: MainConfig

   init {
      this.mainConfig = mainConfig;
   }

   public suspend fun analyzeInScene(
      soot: SootCtx,
      locator: IProjectFileLocator,
      info: SootInfoCache,
      resultCollector: IPreAnalysisResultCollector,
      monitor: MetricsMonitor
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.code.cfg.ExceptionRangeCFG.isCircular()" because "range" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.graphToStatement(DomHelper.java:84)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:203)
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:157)
      //
      // Bytecode:
      // 000: aload 6
      // 002: instanceof cn/sast/framework/engine/PreAnalysis$analyzeInScene$1
      // 005: ifeq 029
      // 008: aload 6
      // 00a: checkcast cn/sast/framework/engine/PreAnalysis$analyzeInScene$1
      // 00d: astore 31
      // 00f: aload 31
      // 011: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.label I
      // 014: ldc -2147483648
      // 016: iand
      // 017: ifeq 029
      // 01a: aload 31
      // 01c: dup
      // 01d: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.label I
      // 020: ldc -2147483648
      // 022: isub
      // 023: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.label I
      // 026: goto 035
      // 029: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$1
      // 02c: dup
      // 02d: aload 0
      // 02e: aload 6
      // 030: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.<init> (Lcn/sast/framework/engine/PreAnalysis;Lkotlin/coroutines/Continuation;)V
      // 033: astore 31
      // 035: aload 31
      // 037: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.result Ljava/lang/Object;
      // 03a: astore 30
      // 03c: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03f: astore 33
      // 041: aload 31
      // 043: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.label I
      // 046: tableswitch 687 0 1 22 222
      // 05c: aload 30
      // 05e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 061: invokestatic soot/Scene.v ()Lsoot/Scene;
      // 064: astore 7
      // 066: getstatic cn/sast/framework/engine/PreAnalysis.logger Lmu/KLogger;
      // 069: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/engine/PreAnalysis.analyzeInScene$lambda$0 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 06e: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 073: getstatic cn/sast/framework/engine/PreAnalysis.logger Lmu/KLogger;
      // 076: invokestatic org/utbot/common/LoggingKt.info (Lmu/KLogger;)Lorg/utbot/common/LoggerWithLogMethod;
      // 079: astore 8
      // 07b: ldc "PreAnalysis"
      // 07d: astore 9
      // 07f: bipush 0
      // 080: istore 10
      // 082: aload 8
      // 084: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 087: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$1
      // 08a: dup
      // 08b: aload 9
      // 08d: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$1.<init> (Ljava/lang/String;)V
      // 090: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 095: pop
      // 096: invokestatic java/time/LocalDateTime.now ()Ljava/time/LocalDateTime;
      // 099: astore 11
      // 09b: bipush 0
      // 09c: istore 12
      // 09e: new kotlin/jvm/internal/Ref$ObjectRef
      // 0a1: dup
      // 0a2: invokespecial kotlin/jvm/internal/Ref$ObjectRef.<init> ()V
      // 0a5: astore 13
      // 0a7: aload 13
      // 0a9: getstatic org/utbot/common/Maybe.Companion Lorg/utbot/common/Maybe$Companion;
      // 0ac: invokevirtual org/utbot/common/Maybe$Companion.empty ()Lorg/utbot/common/Maybe;
      // 0af: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 0b2: nop
      // 0b3: aload 13
      // 0b5: astore 29
      // 0b7: bipush 0
      // 0b8: istore 14
      // 0ba: new cn/sast/framework/engine/PreAnalysisImpl
      // 0bd: dup
      // 0be: aload 0
      // 0bf: getfield cn/sast/framework/engine/PreAnalysis.mainConfig Lcn/sast/api/config/MainConfig;
      // 0c2: aload 2
      // 0c3: aload 1
      // 0c4: invokevirtual cn/sast/framework/SootCtx.getSootMethodCallGraph ()Lsoot/jimple/toolkits/callgraph/CallGraph;
      // 0c7: aload 3
      // 0c8: aload 4
      // 0ca: aload 7
      // 0cc: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0cf: aload 7
      // 0d1: invokespecial cn/sast/framework/engine/PreAnalysisImpl.<init> (Lcn/sast/api/config/MainConfig;Lcn/sast/framework/report/IProjectFileLocator;Lsoot/jimple/toolkits/callgraph/CallGraph;Lcom/feysh/corax/cache/analysis/SootInfoCache;Lcn/sast/framework/result/IPreAnalysisResultCollector;Lsoot/Scene;)V
      // 0d4: astore 15
      // 0d6: aload 15
      // 0d8: aload 31
      // 0da: aload 31
      // 0dc: aload 5
      // 0de: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$0 Ljava/lang/Object;
      // 0e1: aload 31
      // 0e3: aload 8
      // 0e5: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$1 Ljava/lang/Object;
      // 0e8: aload 31
      // 0ea: aload 9
      // 0ec: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$2 Ljava/lang/Object;
      // 0ef: aload 31
      // 0f1: aload 11
      // 0f3: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$3 Ljava/lang/Object;
      // 0f6: aload 31
      // 0f8: aload 13
      // 0fa: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$4 Ljava/lang/Object;
      // 0fd: aload 31
      // 0ff: aload 15
      // 101: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$5 Ljava/lang/Object;
      // 104: aload 31
      // 106: aload 29
      // 108: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$6 Ljava/lang/Object;
      // 10b: aload 31
      // 10d: iload 12
      // 10f: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.I$0 I
      // 112: aload 31
      // 114: bipush 1
      // 115: putfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.label I
      // 118: invokevirtual cn/sast/framework/engine/PreAnalysisImpl.processPreAnalysisUnits (Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 11b: dup
      // 11c: aload 33
      // 11e: if_acmpne 17f
      // 121: aload 33
      // 123: areturn
      // 124: bipush 0
      // 125: istore 10
      // 127: bipush 0
      // 128: istore 14
      // 12a: aload 31
      // 12c: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.I$0 I
      // 12f: istore 12
      // 131: aload 31
      // 133: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$6 Ljava/lang/Object;
      // 136: checkcast kotlin/jvm/internal/Ref$ObjectRef
      // 139: astore 29
      // 13b: aload 31
      // 13d: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$5 Ljava/lang/Object;
      // 140: checkcast cn/sast/framework/engine/PreAnalysisImpl
      // 143: astore 15
      // 145: aload 31
      // 147: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$4 Ljava/lang/Object;
      // 14a: checkcast kotlin/jvm/internal/Ref$ObjectRef
      // 14d: astore 13
      // 14f: aload 31
      // 151: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$3 Ljava/lang/Object;
      // 154: checkcast java/time/LocalDateTime
      // 157: astore 11
      // 159: aload 31
      // 15b: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$2 Ljava/lang/Object;
      // 15e: checkcast java/lang/String
      // 161: astore 9
      // 163: aload 31
      // 165: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$1 Ljava/lang/Object;
      // 168: checkcast org/utbot/common/LoggerWithLogMethod
      // 16b: astore 8
      // 16d: aload 31
      // 16f: getfield cn/sast/framework/engine/PreAnalysis$analyzeInScene$1.L$0 Ljava/lang/Object;
      // 172: checkcast cn/sast/framework/metrics/MetricsMonitor
      // 175: astore 5
      // 177: nop
      // 178: aload 30
      // 17a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 17d: aload 30
      // 17f: pop
      // 180: aload 5
      // 182: invokevirtual cn/sast/framework/metrics/MetricsMonitor.getProjectMetrics ()Lcn/sast/api/report/ProjectMetrics;
      // 185: invokevirtual cn/sast/api/report/ProjectMetrics.getAnalyzedClasses ()Ljava/util/Set;
      // 188: checkcast java/util/Collection
      // 18b: astore 16
      // 18d: aload 15
      // 18f: invokevirtual cn/sast/framework/engine/PreAnalysisImpl.getAnalyzedClasses ()Ljava/util/Set;
      // 192: checkcast java/lang/Iterable
      // 195: astore 17
      // 197: bipush 0
      // 198: istore 18
      // 19a: aload 17
      // 19c: astore 19
      // 19e: new java/util/ArrayList
      // 1a1: dup
      // 1a2: aload 17
      // 1a4: bipush 10
      // 1a6: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 1a9: invokespecial java/util/ArrayList.<init> (I)V
      // 1ac: checkcast java/util/Collection
      // 1af: astore 20
      // 1b1: bipush 0
      // 1b2: istore 21
      // 1b4: aload 19
      // 1b6: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 1bb: astore 22
      // 1bd: aload 22
      // 1bf: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 1c4: ifeq 1ef
      // 1c7: aload 22
      // 1c9: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 1ce: astore 23
      // 1d0: aload 20
      // 1d2: aload 23
      // 1d4: checkcast soot/SootClass
      // 1d7: astore 24
      // 1d9: astore 25
      // 1db: bipush 0
      // 1dc: istore 26
      // 1de: aload 24
      // 1e0: invokevirtual soot/SootClass.getName ()Ljava/lang/String;
      // 1e3: aload 25
      // 1e5: swap
      // 1e6: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 1eb: pop
      // 1ec: goto 1bd
      // 1ef: aload 20
      // 1f1: checkcast java/util/List
      // 1f4: nop
      // 1f5: checkcast java/lang/Iterable
      // 1f8: astore 17
      // 1fa: aload 16
      // 1fc: aload 17
      // 1fe: invokestatic kotlin/collections/CollectionsKt.addAll (Ljava/util/Collection;Ljava/lang/Iterable;)Z
      // 201: pop
      // 202: aload 5
      // 204: invokevirtual cn/sast/framework/metrics/MetricsMonitor.getProjectMetrics ()Lcn/sast/api/report/ProjectMetrics;
      // 207: aload 15
      // 209: invokevirtual cn/sast/framework/engine/PreAnalysisImpl.getAnalyzedSourceFiles ()Ljava/util/Set;
      // 20c: checkcast java/util/Collection
      // 20f: invokeinterface java/util/Collection.size ()I 1
      // 214: invokevirtual cn/sast/api/report/ProjectMetrics.setAnalyzedFiles (I)V
      // 217: nop
      // 218: aload 29
      // 21a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 21d: astore 32
      // 21f: new org/utbot/common/Maybe
      // 222: dup
      // 223: aload 32
      // 225: invokespecial org/utbot/common/Maybe.<init> (Ljava/lang/Object;)V
      // 228: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 22b: aload 13
      // 22d: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 230: checkcast org/utbot/common/Maybe
      // 233: invokevirtual org/utbot/common/Maybe.getOrThrow ()Ljava/lang/Object;
      // 236: astore 27
      // 238: nop
      // 239: aload 13
      // 23b: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 23e: checkcast org/utbot/common/Maybe
      // 241: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 244: ifeq 262
      // 247: aload 8
      // 249: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 24c: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$2
      // 24f: dup
      // 250: aload 11
      // 252: aload 9
      // 254: aload 13
      // 256: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$2.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 259: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 25e: pop
      // 25f: goto 278
      // 262: aload 8
      // 264: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 267: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$3
      // 26a: dup
      // 26b: aload 11
      // 26d: aload 9
      // 26f: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$3.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 272: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 277: pop
      // 278: goto 2e4
      // 27b: astore 28
      // 27d: aload 8
      // 27f: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 282: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$4
      // 285: dup
      // 286: aload 11
      // 288: aload 9
      // 28a: aload 28
      // 28c: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$4.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Ljava/lang/Throwable;)V
      // 28f: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 294: pop
      // 295: bipush 1
      // 296: istore 12
      // 298: aload 28
      // 29a: athrow
      // 29b: astore 28
      // 29d: iload 12
      // 29f: ifne 2e1
      // 2a2: aload 13
      // 2a4: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
      // 2a7: checkcast org/utbot/common/Maybe
      // 2aa: invokevirtual org/utbot/common/Maybe.getHasValue ()Z
      // 2ad: ifeq 2cb
      // 2b0: aload 8
      // 2b2: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 2b5: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$5
      // 2b8: dup
      // 2b9: aload 11
      // 2bb: aload 9
      // 2bd: aload 13
      // 2bf: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$5.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;Lkotlin/jvm/internal/Ref$ObjectRef;)V
      // 2c2: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 2c7: pop
      // 2c8: goto 2e1
      // 2cb: aload 8
      // 2cd: invokevirtual org/utbot/common/LoggerWithLogMethod.getLogMethod ()Lkotlin/jvm/functions/Function1;
      // 2d0: new cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$6
      // 2d3: dup
      // 2d4: aload 11
      // 2d6: aload 9
      // 2d8: invokespecial cn/sast/framework/engine/PreAnalysis$analyzeInScene$$inlined$bracket$default$6.<init> (Ljava/time/LocalDateTime;Ljava/lang/String;)V
      // 2db: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 2e0: pop
      // 2e1: aload 28
      // 2e3: athrow
      // 2e4: getstatic cn/sast/framework/engine/PreAnalysis.logger Lmu/KLogger;
      // 2e7: invokedynamic invoke ()Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/engine/PreAnalysis.analyzeInScene$lambda$3 ()Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 2ec: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 2f1: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 2f4: areturn
      // 2f5: new java/lang/IllegalStateException
      // 2f8: dup
      // 2f9: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 2fc: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 2ff: athrow
   }

   @JvmStatic
   fun `analyzeInScene$lambda$0`(): Any {
      return "Before PreAnalysis: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `analyzeInScene$lambda$3`(): Any {
      return "After PreAnalysis: Process information: ${ProcessInfoView.Companion.getGlobalProcessInfo().getProcessInfoText()}";
   }

   @JvmStatic
   fun `logger$lambda$4`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$1`(`$msg`: String): Any {
      return "Started: ${`$msg`}";
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$2`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$3`(
      `$startTime`: LocalDateTime,
      `$msg`: String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$4`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$t`: Throwable
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} :: EXCEPTION :: ";
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$5`(
      `$startTime`: LocalDateTime,
      `$msg`: String,
      `$res`: ObjectRef
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} ";
   }

   @JvmStatic
   fun `PreAnalysis$analyzeInScene$$inlined$bracket$default$6`(
      `$startTime`: LocalDateTime,
      `$msg`: String
   ): Any {
      val var1: LocalDateTime = `$startTime`;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${`$msg`} <Nothing>";
   }

   public companion object {
      private final val logger: KLogger
   }
}
