package cn.sast.framework

import cn.sast.api.AnalyzerEnv
import cn.sast.api.util.IMonitor
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import java.util.Base64
import java.util.LinkedHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import soot.SootMethod

@SourceDebugExtension(["SMAP\nAnalyzeTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AnalyzeTaskRunner.kt\ncn/sast/framework/AnalyzeTaskRunner\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,152:1\n381#2,7:153\n1557#3:160\n1628#3,3:161\n1557#3:172\n1628#3,3:173\n16#4,8:164\n16#4,8:176\n*S KotlinDebug\n*F\n+ 1 AnalyzeTaskRunner.kt\ncn/sast/framework/AnalyzeTaskRunner\n*L\n49#1:153,7\n59#1:160\n59#1:161,3\n106#1:172\n106#1:173,3\n66#1:164,8\n113#1:176,8\n*E\n"])
public class AnalyzeTaskRunner(numThreads: Int, sootCtx: SootCtx, monitor: IMonitor) {
   public final val numThreads: Int
   public final val sootCtx: SootCtx
   public final val monitor: IMonitor
   public final val analysisPasses: MutableMap<IEntryPointProvider, MutableList<cn.sast.framework.AnalyzeTaskRunner.Analysis>>

   init {
      this.numThreads = numThreads;
      this.sootCtx = sootCtx;
      this.monitor = monitor;
      this.analysisPasses = new LinkedHashMap<>();
   }

   public fun registerAnalysis(
      phaseName: String,
      provider: IEntryPointProvider,
      before: ((Continuation<Unit>) -> Any?)? = null,
      analysis: ((cn.sast.framework.AnalyzeTaskRunner.Env, Continuation<Unit>) -> Any?)? = null,
      after: ((Continuation<Unit>) -> Any?)? = null
   ) {
      if (!AnalyzerEnv.INSTANCE.getShouldV3r14y() || AnalyzerEnv.INSTANCE.getBvs1n3ss().get() == 0 || (Companion.getMask() and 65576) == 65576) {
         val `$this$getOrPut$iv`: java.util.Map = this.analysisPasses;
         val `value$iv`: Any = this.analysisPasses.get(provider);
         val var10000: Any;
         if (`value$iv` == null) {
            val var10: Any = new ArrayList();
            `$this$getOrPut$iv`.put(provider, var10);
            var10000 = var10;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.List).add(new AnalyzeTaskRunner.Analysis(phaseName, before, analysis, after));
      }
   }

   public suspend fun run(scope: CoroutineScope) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:361)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:504)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1064)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:565)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //
      // Bytecode:
      // 000: aload 2
      // 001: instanceof cn/sast/framework/AnalyzeTaskRunner$run$1
      // 004: ifeq 027
      // 007: aload 2
      // 008: checkcast cn/sast/framework/AnalyzeTaskRunner$run$1
      // 00b: astore 23
      // 00d: aload 23
      // 00f: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 012: ldc -2147483648
      // 014: iand
      // 015: ifeq 027
      // 018: aload 23
      // 01a: dup
      // 01b: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 01e: ldc -2147483648
      // 020: isub
      // 021: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 024: goto 032
      // 027: new cn/sast/framework/AnalyzeTaskRunner$run$1
      // 02a: dup
      // 02b: aload 0
      // 02c: aload 2
      // 02d: invokespecial cn/sast/framework/AnalyzeTaskRunner$run$1.<init> (Lcn/sast/framework/AnalyzeTaskRunner;Lkotlin/coroutines/Continuation;)V
      // 030: astore 23
      // 032: aload 23
      // 034: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.result Ljava/lang/Object;
      // 037: astore 22
      // 039: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
      // 03c: astore 24
      // 03e: aload 23
      // 040: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 043: tableswitch 1300 0 5 37 319 470 795 1129 1235
      // 068: aload 22
      // 06a: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 06d: aload 0
      // 06e: getfield cn/sast/framework/AnalyzeTaskRunner.monitor Lcn/sast/api/util/IMonitor;
      // 071: ldc "AnalyzeTaskRunner.analysis.pre"
      // 073: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
      // 078: astore 3
      // 079: aload 0
      // 07a: getfield cn/sast/framework/AnalyzeTaskRunner.monitor Lcn/sast/api/util/IMonitor;
      // 07d: ldc "AnalyzeTaskRunner.analysis.process"
      // 07f: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
      // 084: astore 4
      // 086: aload 0
      // 087: getfield cn/sast/framework/AnalyzeTaskRunner.monitor Lcn/sast/api/util/IMonitor;
      // 08a: ldc "AnalyzeTaskRunner.analysis.after"
      // 08c: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
      // 091: astore 5
      // 093: new java/util/ArrayList
      // 096: dup
      // 097: invokespecial java/util/ArrayList.<init> ()V
      // 09a: checkcast java/util/List
      // 09d: astore 6
      // 09f: aload 0
      // 0a0: getfield cn/sast/framework/AnalyzeTaskRunner.analysisPasses Ljava/util/Map;
      // 0a3: invokeinterface java/util/Map.values ()Ljava/util/Collection; 1
      // 0a8: checkcast java/lang/Iterable
      // 0ab: invokestatic kotlin/collections/CollectionsKt.flatten (Ljava/lang/Iterable;)Ljava/util/List;
      // 0ae: astore 7
      // 0b0: aload 6
      // 0b2: aload 7
      // 0b4: checkcast java/lang/Iterable
      // 0b7: astore 8
      // 0b9: astore 20
      // 0bb: bipush 0
      // 0bc: istore 9
      // 0be: aload 8
      // 0c0: astore 10
      // 0c2: new java/util/ArrayList
      // 0c5: dup
      // 0c6: aload 8
      // 0c8: bipush 10
      // 0ca: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 0cd: invokespecial java/util/ArrayList.<init> (I)V
      // 0d0: checkcast java/util/Collection
      // 0d3: astore 11
      // 0d5: bipush 0
      // 0d6: istore 12
      // 0d8: aload 10
      // 0da: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 0df: astore 13
      // 0e1: aload 13
      // 0e3: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 0e8: ifeq 125
      // 0eb: aload 13
      // 0ed: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0f2: astore 14
      // 0f4: aload 11
      // 0f6: aload 14
      // 0f8: checkcast cn/sast/framework/AnalyzeTaskRunner$Analysis
      // 0fb: astore 15
      // 0fd: astore 21
      // 0ff: bipush 0
      // 100: istore 16
      // 102: aload 1
      // 103: aconst_null
      // 104: aconst_null
      // 105: new cn/sast/framework/AnalyzeTaskRunner$run$2$1
      // 108: dup
      // 109: aload 0
      // 10a: aload 15
      // 10c: aconst_null
      // 10d: invokespecial cn/sast/framework/AnalyzeTaskRunner$run$2$1.<init> (Lcn/sast/framework/AnalyzeTaskRunner;Lcn/sast/framework/AnalyzeTaskRunner$Analysis;Lkotlin/coroutines/Continuation;)V
      // 110: checkcast kotlin/jvm/functions/Function2
      // 113: bipush 3
      // 114: aconst_null
      // 115: invokestatic kotlinx/coroutines/BuildersKt.async$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Deferred;
      // 118: nop
      // 119: aload 21
      // 11b: swap
      // 11c: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 121: pop
      // 122: goto 0e1
      // 125: aload 11
      // 127: checkcast java/util/List
      // 12a: nop
      // 12b: aload 20
      // 12d: swap
      // 12e: checkcast java/util/Collection
      // 131: invokeinterface java/util/List.addAll (Ljava/util/Collection;)Z 2
      // 136: pop
      // 137: aload 3
      // 138: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 13b: astore 8
      // 13d: bipush 0
      // 13e: istore 9
      // 140: aload 8
      // 142: ifnonnull 1c6
      // 145: bipush 0
      // 146: istore 10
      // 148: aload 6
      // 14a: checkcast java/util/Collection
      // 14d: aload 23
      // 14f: aload 23
      // 151: aload 0
      // 152: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 155: aload 23
      // 157: aload 1
      // 158: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 15b: aload 23
      // 15d: aload 4
      // 15f: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 162: aload 23
      // 164: aload 5
      // 166: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 169: aload 23
      // 16b: aload 7
      // 16d: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 170: aload 23
      // 172: bipush 1
      // 173: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 176: invokestatic kotlinx/coroutines/AwaitKt.awaitAll (Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 179: dup
      // 17a: aload 24
      // 17c: if_acmpne 1bf
      // 17f: aload 24
      // 181: areturn
      // 182: bipush 0
      // 183: istore 9
      // 185: bipush 0
      // 186: istore 10
      // 188: aload 23
      // 18a: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 18d: checkcast java/util/List
      // 190: astore 7
      // 192: aload 23
      // 194: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 197: checkcast cn/sast/api/util/Timer
      // 19a: astore 5
      // 19c: aload 23
      // 19e: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 1a1: checkcast cn/sast/api/util/Timer
      // 1a4: astore 4
      // 1a6: aload 23
      // 1a8: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 1ab: checkcast kotlinx/coroutines/CoroutineScope
      // 1ae: astore 1
      // 1af: aload 23
      // 1b1: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 1b4: checkcast cn/sast/framework/AnalyzeTaskRunner
      // 1b7: astore 0
      // 1b8: aload 22
      // 1ba: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 1bd: aload 22
      // 1bf: checkcast java/util/List
      // 1c2: pop
      // 1c3: goto 286
      // 1c6: aload 8
      // 1c8: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 1cb: astore 11
      // 1cd: nop
      // 1ce: bipush 0
      // 1cf: istore 10
      // 1d1: aload 6
      // 1d3: checkcast java/util/Collection
      // 1d6: aload 23
      // 1d8: aload 23
      // 1da: aload 0
      // 1db: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 1de: aload 23
      // 1e0: aload 1
      // 1e1: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 1e4: aload 23
      // 1e6: aload 4
      // 1e8: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 1eb: aload 23
      // 1ed: aload 5
      // 1ef: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 1f2: aload 23
      // 1f4: aload 7
      // 1f6: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 1f9: aload 23
      // 1fb: aload 8
      // 1fd: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 200: aload 23
      // 202: aload 11
      // 204: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 207: aload 23
      // 209: bipush 2
      // 20a: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 20d: invokestatic kotlinx/coroutines/AwaitKt.awaitAll (Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 210: dup
      // 211: aload 24
      // 213: if_acmpne 26b
      // 216: aload 24
      // 218: areturn
      // 219: bipush 0
      // 21a: istore 9
      // 21c: bipush 0
      // 21d: istore 10
      // 21f: aload 23
      // 221: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 224: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
      // 227: astore 11
      // 229: aload 23
      // 22b: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 22e: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 231: astore 8
      // 233: aload 23
      // 235: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 238: checkcast java/util/List
      // 23b: astore 7
      // 23d: aload 23
      // 23f: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 242: checkcast cn/sast/api/util/Timer
      // 245: astore 5
      // 247: aload 23
      // 249: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 24c: checkcast cn/sast/api/util/Timer
      // 24f: astore 4
      // 251: aload 23
      // 253: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 256: checkcast kotlinx/coroutines/CoroutineScope
      // 259: astore 1
      // 25a: aload 23
      // 25c: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 25f: checkcast cn/sast/framework/AnalyzeTaskRunner
      // 262: astore 0
      // 263: nop
      // 264: aload 22
      // 266: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 269: aload 22
      // 26b: checkcast java/util/List
      // 26e: astore 10
      // 270: aload 8
      // 272: aload 11
      // 274: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 277: goto 286
      // 27a: astore 12
      // 27c: aload 8
      // 27e: aload 11
      // 280: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 283: aload 12
      // 285: athrow
      // 286: aload 4
      // 288: invokevirtual cn/sast/api/util/Timer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 28b: astore 8
      // 28d: aload 0
      // 28e: getfield cn/sast/framework/AnalyzeTaskRunner.sootCtx Lcn/sast/framework/SootCtx;
      // 291: invokevirtual cn/sast/framework/SootCtx.getMainConfig ()Lcn/sast/api/config/MainConfig;
      // 294: invokevirtual cn/sast/api/config/MainConfig.getIncrementAnalyze ()Lcn/sast/api/incremental/IncrementalAnalyze;
      // 297: astore 10
      // 299: aload 10
      // 29b: instanceof cn/sast/api/incremental/IncrementalAnalyzeByChangeFiles
      // 29e: ifeq 2a9
      // 2a1: aload 10
      // 2a3: checkcast cn/sast/api/incremental/IncrementalAnalyzeByChangeFiles
      // 2a6: goto 2aa
      // 2a9: aconst_null
      // 2aa: astore 9
      // 2ac: aload 0
      // 2ad: getfield cn/sast/framework/AnalyzeTaskRunner.analysisPasses Ljava/util/Map;
      // 2b0: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 2b5: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 2ba: astore 10
      // 2bc: aload 10
      // 2be: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 2c3: ifeq 3b7
      // 2c6: aload 10
      // 2c8: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 2cd: checkcast java/util/Map$Entry
      // 2d0: astore 11
      // 2d2: aload 11
      // 2d4: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 2d9: checkcast cn/sast/framework/entries/IEntryPointProvider
      // 2dc: astore 12
      // 2de: aload 11
      // 2e0: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 2e5: checkcast java/util/List
      // 2e8: astore 13
      // 2ea: getstatic cn/sast/framework/AnalyzeTaskRunner.logger Lmu/KLogger;
      // 2ed: aload 12
      // 2ef: invokedynamic invoke (Lcn/sast/framework/entries/IEntryPointProvider;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/AnalyzeTaskRunner.run$lambda$3 (Lcn/sast/framework/entries/IEntryPointProvider;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 2f4: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 2f9: aload 12
      // 2fb: invokeinterface cn/sast/framework/entries/IEntryPointProvider.getIterator ()Lkotlinx/coroutines/flow/Flow; 1
      // 300: new cn/sast/framework/AnalyzeTaskRunner$run$5
      // 303: dup
      // 304: aload 0
      // 305: aload 12
      // 307: aload 9
      // 309: aload 13
      // 30b: aload 1
      // 30c: invokespecial cn/sast/framework/AnalyzeTaskRunner$run$5.<init> (Lcn/sast/framework/AnalyzeTaskRunner;Lcn/sast/framework/entries/IEntryPointProvider;Lcn/sast/api/incremental/IncrementalAnalyzeByChangeFiles;Ljava/util/List;Lkotlinx/coroutines/CoroutineScope;)V
      // 30f: checkcast kotlinx/coroutines/flow/FlowCollector
      // 312: aload 23
      // 314: aload 23
      // 316: aload 0
      // 317: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 31a: aload 23
      // 31c: aload 1
      // 31d: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 320: aload 23
      // 322: aload 4
      // 324: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 327: aload 23
      // 329: aload 5
      // 32b: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 32e: aload 23
      // 330: aload 7
      // 332: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 335: aload 23
      // 337: aload 8
      // 339: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 33c: aload 23
      // 33e: aload 9
      // 340: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 343: aload 23
      // 345: aload 10
      // 347: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$7 Ljava/lang/Object;
      // 34a: aload 23
      // 34c: bipush 3
      // 34d: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 350: invokeinterface kotlinx/coroutines/flow/Flow.collect (Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/Continuation;)Ljava/lang/Object; 3
      // 355: dup
      // 356: aload 24
      // 358: if_acmpne 3b3
      // 35b: aload 24
      // 35d: areturn
      // 35e: aload 23
      // 360: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$7 Ljava/lang/Object;
      // 363: checkcast java/util/Iterator
      // 366: astore 10
      // 368: aload 23
      // 36a: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 36d: checkcast cn/sast/api/incremental/IncrementalAnalyzeByChangeFiles
      // 370: astore 9
      // 372: aload 23
      // 374: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 377: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
      // 37a: astore 8
      // 37c: aload 23
      // 37e: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 381: checkcast java/util/List
      // 384: astore 7
      // 386: aload 23
      // 388: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 38b: checkcast cn/sast/api/util/Timer
      // 38e: astore 5
      // 390: aload 23
      // 392: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 395: checkcast cn/sast/api/util/Timer
      // 398: astore 4
      // 39a: aload 23
      // 39c: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 39f: checkcast kotlinx/coroutines/CoroutineScope
      // 3a2: astore 1
      // 3a3: aload 23
      // 3a5: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 3a8: checkcast cn/sast/framework/AnalyzeTaskRunner
      // 3ab: astore 0
      // 3ac: aload 22
      // 3ae: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 3b1: aload 22
      // 3b3: pop
      // 3b4: goto 2bc
      // 3b7: aload 4
      // 3b9: aload 8
      // 3bb: invokevirtual cn/sast/api/util/Timer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 3be: new java/util/ArrayList
      // 3c1: dup
      // 3c2: invokespecial java/util/ArrayList.<init> ()V
      // 3c5: checkcast java/util/List
      // 3c8: astore 10
      // 3ca: aload 10
      // 3cc: aload 7
      // 3ce: checkcast java/lang/Iterable
      // 3d1: astore 11
      // 3d3: astore 20
      // 3d5: bipush 0
      // 3d6: istore 12
      // 3d8: aload 11
      // 3da: astore 13
      // 3dc: new java/util/ArrayList
      // 3df: dup
      // 3e0: aload 11
      // 3e2: bipush 10
      // 3e4: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 3e7: invokespecial java/util/ArrayList.<init> (I)V
      // 3ea: checkcast java/util/Collection
      // 3ed: astore 14
      // 3ef: bipush 0
      // 3f0: istore 15
      // 3f2: aload 13
      // 3f4: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 3f9: astore 16
      // 3fb: aload 16
      // 3fd: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 402: ifeq 43f
      // 405: aload 16
      // 407: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 40c: astore 17
      // 40e: aload 14
      // 410: aload 17
      // 412: checkcast cn/sast/framework/AnalyzeTaskRunner$Analysis
      // 415: astore 18
      // 417: astore 21
      // 419: bipush 0
      // 41a: istore 19
      // 41c: aload 1
      // 41d: aconst_null
      // 41e: aconst_null
      // 41f: new cn/sast/framework/AnalyzeTaskRunner$run$6$1
      // 422: dup
      // 423: aload 0
      // 424: aload 18
      // 426: aconst_null
      // 427: invokespecial cn/sast/framework/AnalyzeTaskRunner$run$6$1.<init> (Lcn/sast/framework/AnalyzeTaskRunner;Lcn/sast/framework/AnalyzeTaskRunner$Analysis;Lkotlin/coroutines/Continuation;)V
      // 42a: checkcast kotlin/jvm/functions/Function2
      // 42d: bipush 3
      // 42e: aconst_null
      // 42f: invokestatic kotlinx/coroutines/BuildersKt.async$default (Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;ILjava/lang/Object;)Lkotlinx/coroutines/Deferred;
      // 432: nop
      // 433: aload 21
      // 435: swap
      // 436: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 43b: pop
      // 43c: goto 3fb
      // 43f: aload 14
      // 441: checkcast java/util/List
      // 444: nop
      // 445: aload 20
      // 447: swap
      // 448: checkcast java/util/Collection
      // 44b: invokeinterface java/util/List.addAll (Ljava/util/Collection;)Z 2
      // 450: pop
      // 451: aload 5
      // 453: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 456: astore 11
      // 458: bipush 0
      // 459: istore 12
      // 45b: aload 11
      // 45d: ifnonnull 4c0
      // 460: bipush 0
      // 461: istore 13
      // 463: aload 10
      // 465: checkcast java/util/Collection
      // 468: aload 23
      // 46a: aload 23
      // 46c: aconst_null
      // 46d: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 470: aload 23
      // 472: aconst_null
      // 473: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 476: aload 23
      // 478: aconst_null
      // 479: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 47c: aload 23
      // 47e: aconst_null
      // 47f: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 482: aload 23
      // 484: aconst_null
      // 485: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 488: aload 23
      // 48a: aconst_null
      // 48b: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 48e: aload 23
      // 490: aconst_null
      // 491: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 494: aload 23
      // 496: aconst_null
      // 497: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$7 Ljava/lang/Object;
      // 49a: aload 23
      // 49c: bipush 4
      // 49d: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 4a0: invokestatic kotlinx/coroutines/AwaitKt.awaitAll (Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 4a3: dup
      // 4a4: aload 24
      // 4a6: if_acmpne 4b9
      // 4a9: aload 24
      // 4ab: areturn
      // 4ac: bipush 0
      // 4ad: istore 12
      // 4af: bipush 0
      // 4b0: istore 13
      // 4b2: aload 22
      // 4b4: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 4b7: aload 22
      // 4b9: checkcast java/util/List
      // 4bc: pop
      // 4bd: goto 553
      // 4c0: aload 11
      // 4c2: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
      // 4c5: astore 14
      // 4c7: nop
      // 4c8: bipush 0
      // 4c9: istore 13
      // 4cb: aload 10
      // 4cd: checkcast java/util/Collection
      // 4d0: aload 23
      // 4d2: aload 23
      // 4d4: aload 11
      // 4d6: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 4d9: aload 23
      // 4db: aload 14
      // 4dd: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 4e0: aload 23
      // 4e2: aconst_null
      // 4e3: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$2 Ljava/lang/Object;
      // 4e6: aload 23
      // 4e8: aconst_null
      // 4e9: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$3 Ljava/lang/Object;
      // 4ec: aload 23
      // 4ee: aconst_null
      // 4ef: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$4 Ljava/lang/Object;
      // 4f2: aload 23
      // 4f4: aconst_null
      // 4f5: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$5 Ljava/lang/Object;
      // 4f8: aload 23
      // 4fa: aconst_null
      // 4fb: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$6 Ljava/lang/Object;
      // 4fe: aload 23
      // 500: aconst_null
      // 501: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$7 Ljava/lang/Object;
      // 504: aload 23
      // 506: bipush 5
      // 507: putfield cn/sast/framework/AnalyzeTaskRunner$run$1.label I
      // 50a: invokestatic kotlinx/coroutines/AwaitKt.awaitAll (Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
      // 50d: dup
      // 50e: aload 24
      // 510: if_acmpne 538
      // 513: aload 24
      // 515: areturn
      // 516: bipush 0
      // 517: istore 12
      // 519: bipush 0
      // 51a: istore 13
      // 51c: aload 23
      // 51e: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$1 Ljava/lang/Object;
      // 521: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
      // 524: astore 14
      // 526: aload 23
      // 528: getfield cn/sast/framework/AnalyzeTaskRunner$run$1.L$0 Ljava/lang/Object;
      // 52b: checkcast cn/sast/api/util/PhaseIntervalTimer
      // 52e: astore 11
      // 530: nop
      // 531: aload 22
      // 533: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
      // 536: aload 22
      // 538: checkcast java/util/List
      // 53b: astore 13
      // 53d: aload 11
      // 53f: aload 14
      // 541: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 544: goto 553
      // 547: astore 15
      // 549: aload 11
      // 54b: aload 14
      // 54d: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
      // 550: aload 15
      // 552: athrow
      // 553: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 556: areturn
      // 557: new java/lang/IllegalStateException
      // 55a: dup
      // 55b: ldc_w "call to 'resume' before 'invoke' with coroutine"
      // 55e: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 561: athrow
   }

   public suspend fun run() {
      val var10000: Any = CoroutineScopeKt.coroutineScope((new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null) {
         int label;

         {
            super(2, `$completionx`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var3x: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val `$this$coroutineScope`: CoroutineScope = this.L$0 as CoroutineScope;
                  val var10000: AnalyzeTaskRunner = this.this$0;
                  val var10002: Continuation = this as Continuation;
                  this.label = 1;
                  if (var10000.run(`$this$coroutineScope`, var10002) === var3x) {
                     return var3x;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            val var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @JvmStatic
   fun `run$lambda$3`(`$provider`: IEntryPointProvider): Any {
      return "do analysis with provider: $`$provider`";
   }

   @JvmStatic
   fun `logger$lambda$6`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `mask_delegate$lambda$7`(): Int {
      return (BuildersKt.runBlocking$default(
            null,
            (
               new Function2<CoroutineScope, Continuation<? super Integer>, Object>(null) {
                  int label;

                  {
                     super(2, `$completion`);
                  }

                  public final Object invokeSuspend(Object $result) {
                     IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           var var10000: ByteArray = Base64.getDecoder().decode("bG9hZExpY2Vuc2U=");
                           val a: java.lang.String = StringsKt.decodeToString(var10000);
                           var10000 = Base64.getDecoder().decode("dmVyaWZ5");
                           val b: java.lang.String = StringsKt.decodeToString(var10000);
                           var10000 = Base64.getDecoder().decode("TGljZW5zZQ==");
                           val c: java.lang.String = StringsKt.decodeToString(var10000);
                           var10000 = Base64.getDecoder().decode("TGljZW5jZQ==");
                           val d: java.lang.String = StringsKt.decodeToString(var10000);
                           var s: java.lang.String = System.getProperty(c);
                           if (s == null || s.length() == 0) {
                              s = System.getProperty(d);
                           }

                           if (s != null && s.length() != 0) {
                              var var7: Int;
                              try {
                                 AnalyzeTaskRunner.Companion.getV3r14yJn1Class().getDeclaredMethod(a, java.lang.String.class).invoke(null, s);
                                 var10000 = (byte[])AnalyzeTaskRunner.Companion
                                    .getV3r14yJn1Class()
                                    .getDeclaredMethod(b, java.lang.String.class)
                                    .invoke(null, "PREMIUM-JAVA");
                                 var7 = (var10000 as java.lang.Number).intValue();
                              } catch (var9: Exception) {
                                 var7 = 0;
                              } catch (var10: LinkageError) {
                                 var7 = 0;
                              }

                              return Boxing.boxInt(var7);
                           } else {
                              return Boxing.boxInt(0);
                           }
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }
                  }

                  public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                     return (new <anonymous constructor>(`$completion`)) as Continuation<Unit>;
                  }

                  public final Object invoke(CoroutineScope p1, Continuation<? super Integer> p2) {
                     return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                  }
               }
            ) as Function2,
            1,
            null
         ) as java.lang.Number)
         .intValue();
   }

   public data class Analysis(phaseName: String,
      before: ((Continuation<Unit>) -> Any?)? = null,
      analysis: ((cn.sast.framework.AnalyzeTaskRunner.Env, Continuation<Unit>) -> Any?)? = null,
      after: ((Continuation<Unit>) -> Any?)? = null
   ) {
      public final val phaseName: String
      public final val before: ((Continuation<Unit>) -> Any?)?
      public final val analysis: ((cn.sast.framework.AnalyzeTaskRunner.Env, Continuation<Unit>) -> Any?)?
      public final val after: ((Continuation<Unit>) -> Any?)?

      init {
         this.phaseName = phaseName;
         this.before = before;
         this.analysis = analysis;
         this.after = after;
      }

      public operator fun component1(): String {
         return this.phaseName;
      }

      public operator fun component2(): ((Continuation<Unit>) -> Any?)? {
         return this.before;
      }

      public operator fun component3(): ((cn.sast.framework.AnalyzeTaskRunner.Env, Continuation<Unit>) -> Any?)? {
         return this.analysis;
      }

      public operator fun component4(): ((Continuation<Unit>) -> Any?)? {
         return this.after;
      }

      public fun copy(
         phaseName: String = this.phaseName,
         before: ((Continuation<Unit>) -> Any?)? = this.before,
         analysis: ((cn.sast.framework.AnalyzeTaskRunner.Env, Continuation<Unit>) -> Any?)? = this.analysis,
         after: ((Continuation<Unit>) -> Any?)? = this.after
      ): cn.sast.framework.AnalyzeTaskRunner.Analysis {
         return new AnalyzeTaskRunner.Analysis(phaseName, before, analysis, after);
      }

      public override fun toString(): String {
         return "Analysis(phaseName=${this.phaseName}, before=${this.before}, analysis=${this.analysis}, after=${this.after})";
      }

      public override fun hashCode(): Int {
         return (
                  (this.phaseName.hashCode() * 31 + (if (this.before == null) 0 else this.before.hashCode())) * 31
                     + (if (this.analysis == null) 0 else this.analysis.hashCode())
               )
               * 31
            + (if (this.after == null) 0 else this.after.hashCode());
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is AnalyzeTaskRunner.Analysis) {
            return false;
         } else {
            val var2: AnalyzeTaskRunner.Analysis = other as AnalyzeTaskRunner.Analysis;
            if (!(this.phaseName == (other as AnalyzeTaskRunner.Analysis).phaseName)) {
               return false;
            } else if (!(this.before == var2.before)) {
               return false;
            } else if (!(this.analysis == var2.analysis)) {
               return false;
            } else {
               return this.after == var2.after;
            }
         }
      }
   }

   public companion object {
      private final val logger: KLogger

      public final lateinit var v3r14yJn1Class: Class<*>
         internal set

      public final val mask: Int
         public final get() {
            return (AnalyzeTaskRunner.access$getMask$delegate$cp().getValue() as java.lang.Number).intValue();
         }


      public const val mask1: Int
   }

   public data class Env(provider: IEntryPointProvider,
      task: AnalyzeTask,
      sootCtx: SootCtx,
      entries: Collection<SootMethod>,
      methodsMustAnalyze: Collection<SootMethod>
   ) {
      public final val provider: IEntryPointProvider
      public final val task: AnalyzeTask
      public final val sootCtx: SootCtx
      public final val entries: Collection<SootMethod>
      public final val methodsMustAnalyze: Collection<SootMethod>

      init {
         this.provider = provider;
         this.task = task;
         this.sootCtx = sootCtx;
         this.entries = entries;
         this.methodsMustAnalyze = methodsMustAnalyze;
      }

      public operator fun component1(): IEntryPointProvider {
         return this.provider;
      }

      public operator fun component2(): AnalyzeTask {
         return this.task;
      }

      public operator fun component3(): SootCtx {
         return this.sootCtx;
      }

      public operator fun component4(): Collection<SootMethod> {
         return this.entries;
      }

      public operator fun component5(): Collection<SootMethod> {
         return this.methodsMustAnalyze;
      }

      public fun copy(
         provider: IEntryPointProvider = this.provider,
         task: AnalyzeTask = this.task,
         sootCtx: SootCtx = this.sootCtx,
         entries: Collection<SootMethod> = this.entries,
         methodsMustAnalyze: Collection<SootMethod> = this.methodsMustAnalyze
      ): cn.sast.framework.AnalyzeTaskRunner.Env {
         return new AnalyzeTaskRunner.Env(provider, task, sootCtx, entries, methodsMustAnalyze);
      }

      public override fun toString(): String {
         return "Env(provider=${this.provider}, task=${this.task}, sootCtx=${this.sootCtx}, entries=${this.entries}, methodsMustAnalyze=${this.methodsMustAnalyze})";
      }

      public override fun hashCode(): Int {
         return (((this.provider.hashCode() * 31 + this.task.hashCode()) * 31 + this.sootCtx.hashCode()) * 31 + this.entries.hashCode()) * 31
            + this.methodsMustAnalyze.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is AnalyzeTaskRunner.Env) {
            return false;
         } else {
            val var2: AnalyzeTaskRunner.Env = other as AnalyzeTaskRunner.Env;
            if (!(this.provider == (other as AnalyzeTaskRunner.Env).provider)) {
               return false;
            } else if (!(this.task == var2.task)) {
               return false;
            } else if (!(this.sootCtx == var2.sootCtx)) {
               return false;
            } else if (!(this.entries == var2.entries)) {
               return false;
            } else {
               return this.methodsMustAnalyze == var2.methodsMustAnalyze;
            }
         }
      }
   }
}
