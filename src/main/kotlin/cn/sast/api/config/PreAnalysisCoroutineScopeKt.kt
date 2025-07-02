@file:SourceDebugExtension(["SMAP\nPreAnalysisCoroutineScope.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisCoroutineScope.kt\ncn/sast/api/config/PreAnalysisCoroutineScopeKt\n+ 2 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,99:1\n16#2,2:100\n16#2,8:102\n19#2,3:110\n16#2,8:113\n23#2:121\n*S KotlinDebug\n*F\n+ 1 PreAnalysisCoroutineScope.kt\ncn/sast/api/config/PreAnalysisCoroutineScopeKt\n*L\n63#1:100,2\n89#1:102,8\n63#1:110,3\n89#1:113,8\n63#1:121\n*E\n"])

package cn.sast.api.config

import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import kotlin.jvm.internal.SourceDebugExtension

public suspend fun AIAnalysisBaseImpl.processAIAnalysisUnits(preAnalysisScope: PreAnalysisCoroutineScope) {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.code.cfg.ExceptionRangeCFG.isCircular()" because "range" is null
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.graphToStatement(DomHelper.java:84)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:203)
   //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.createStatement(DomHelper.java:27)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:157)
   //
   // Bytecode:
   // 000: aload 2
   // 001: instanceof cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1
   // 004: ifeq 027
   // 007: aload 2
   // 008: checkcast cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1
   // 00b: astore 19
   // 00d: aload 19
   // 00f: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 012: ldc -2147483648
   // 014: iand
   // 015: ifeq 027
   // 018: aload 19
   // 01a: dup
   // 01b: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 01e: ldc -2147483648
   // 020: isub
   // 021: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 024: goto 031
   // 027: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1
   // 02a: dup
   // 02b: aload 2
   // 02c: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.<init> (Lkotlin/coroutines/Continuation;)V
   // 02f: astore 19
   // 031: aload 19
   // 033: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.result Ljava/lang/Object;
   // 036: astore 18
   // 038: invokestatic kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
   // 03b: astore 20
   // 03d: aload 19
   // 03f: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 042: tableswitch 1185 0 6 42 232 399 504 695 896 1036
   // 06c: aload 18
   // 06e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 071: aload 0
   // 072: invokevirtual com/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl.getPreAnalysis ()Lcom/feysh/corax/config/api/PreAnalysisApi;
   // 075: aload 1
   // 076: if_acmpne 07d
   // 079: bipush 1
   // 07a: goto 07e
   // 07d: bipush 0
   // 07e: ifne 092
   // 081: ldc "Failed requirement."
   // 083: astore 4
   // 085: new java/lang/IllegalArgumentException
   // 088: dup
   // 089: aload 4
   // 08b: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
   // 08e: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
   // 091: athrow
   // 092: aload 1
   // 093: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.getMainConfig ()Lcn/sast/api/config/MainConfig; 1
   // 098: invokevirtual cn/sast/api/config/MainConfig.getSaConfig ()Lcn/sast/api/config/SaConfig;
   // 09b: dup
   // 09c: ifnull 0a6
   // 09f: invokevirtual cn/sast/api/config/SaConfig.getCheckers ()Ljava/util/Set;
   // 0a2: dup
   // 0a3: ifnonnull 0aa
   // 0a6: pop
   // 0a7: invokestatic kotlin/collections/SetsKt.emptySet ()Ljava/util/Set;
   // 0aa: astore 3
   // 0ab: aload 1
   // 0ac: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.getMainConfig ()Lcn/sast/api/config/MainConfig; 1
   // 0b1: invokevirtual cn/sast/api/config/MainConfig.getMonitor ()Lcn/sast/api/util/IMonitor;
   // 0b4: astore 4
   // 0b6: aload 1
   // 0b7: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.getMainConfig ()Lcn/sast/api/config/MainConfig; 1
   // 0bc: invokevirtual cn/sast/api/config/MainConfig.getParallelsNum ()I
   // 0bf: bipush 0
   // 0c0: bipush 2
   // 0c1: aconst_null
   // 0c2: invokestatic kotlinx/coroutines/sync/SemaphoreKt.Semaphore$default (IIILjava/lang/Object;)Lkotlinx/coroutines/sync/Semaphore;
   // 0c5: astore 5
   // 0c7: aload 4
   // 0c9: dup
   // 0ca: ifnull 0d7
   // 0cd: ldc "AIAnalysis.processAIAnalysisUnits"
   // 0cf: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
   // 0d4: goto 0d9
   // 0d7: pop
   // 0d8: aconst_null
   // 0d9: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 0dc: astore 6
   // 0de: bipush 0
   // 0df: istore 7
   // 0e1: aload 6
   // 0e3: ifnonnull 29f
   // 0e6: bipush 0
   // 0e7: istore 8
   // 0e9: nop
   // 0ea: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$1
   // 0ed: dup
   // 0ee: aload 1
   // 0ef: aload 3
   // 0f0: aload 5
   // 0f2: aload 4
   // 0f4: aload 0
   // 0f5: aconst_null
   // 0f6: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Ljava/util/Set;Lkotlinx/coroutines/sync/Semaphore;Lcn/sast/api/util/IMonitor;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlin/coroutines/Continuation;)V
   // 0f9: checkcast kotlin/jvm/functions/Function2
   // 0fc: aload 19
   // 0fe: aload 19
   // 100: aload 0
   // 101: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 104: aload 19
   // 106: aload 1
   // 107: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 10a: aload 19
   // 10c: aload 4
   // 10e: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 111: aload 19
   // 113: aload 5
   // 115: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 118: aload 19
   // 11a: bipush 1
   // 11b: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 11e: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 121: dup
   // 122: aload 20
   // 124: if_acmpne 15e
   // 127: aload 20
   // 129: areturn
   // 12a: bipush 0
   // 12b: istore 7
   // 12d: bipush 0
   // 12e: istore 8
   // 130: aload 19
   // 132: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 135: checkcast kotlinx/coroutines/sync/Semaphore
   // 138: astore 5
   // 13a: aload 19
   // 13c: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 13f: checkcast cn/sast/api/util/IMonitor
   // 142: astore 4
   // 144: aload 19
   // 146: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 149: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 14c: astore 1
   // 14d: aload 19
   // 14f: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 152: checkcast com/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl
   // 155: astore 0
   // 156: nop
   // 157: aload 18
   // 159: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 15c: aload 18
   // 15e: pop
   // 15f: aload 1
   // 160: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 165: goto 173
   // 168: astore 9
   // 16a: aload 1
   // 16b: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 170: aload 9
   // 172: athrow
   // 173: nop
   // 174: aload 4
   // 176: dup
   // 177: ifnull 184
   // 17a: ldc "AIAnalysis.initializeClassCallBacks"
   // 17c: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
   // 181: goto 186
   // 184: pop
   // 185: aconst_null
   // 186: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 189: astore 9
   // 18b: bipush 0
   // 18c: istore 10
   // 18e: aload 9
   // 190: ifnonnull 1f2
   // 193: bipush 0
   // 194: istore 11
   // 196: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1
   // 199: dup
   // 19a: aload 1
   // 19b: aload 0
   // 19c: aload 5
   // 19e: aconst_null
   // 19f: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlinx/coroutines/sync/Semaphore;Lkotlin/coroutines/Continuation;)V
   // 1a2: checkcast kotlin/jvm/functions/Function2
   // 1a5: aload 19
   // 1a7: aload 19
   // 1a9: aload 1
   // 1aa: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 1ad: aload 19
   // 1af: aconst_null
   // 1b0: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 1b3: aload 19
   // 1b5: aconst_null
   // 1b6: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 1b9: aload 19
   // 1bb: aconst_null
   // 1bc: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 1bf: aload 19
   // 1c1: bipush 2
   // 1c2: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 1c5: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 1c8: dup
   // 1c9: aload 20
   // 1cb: if_acmpne 1ee
   // 1ce: aload 20
   // 1d0: areturn
   // 1d1: bipush 0
   // 1d2: istore 7
   // 1d4: bipush 0
   // 1d5: istore 8
   // 1d7: bipush 0
   // 1d8: istore 10
   // 1da: bipush 0
   // 1db: istore 11
   // 1dd: aload 19
   // 1df: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 1e2: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 1e5: astore 1
   // 1e6: nop
   // 1e7: aload 18
   // 1e9: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 1ec: aload 18
   // 1ee: pop
   // 1ef: goto 287
   // 1f2: aload 9
   // 1f4: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
   // 1f7: astore 12
   // 1f9: nop
   // 1fa: bipush 0
   // 1fb: istore 11
   // 1fd: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1
   // 200: dup
   // 201: aload 1
   // 202: aload 0
   // 203: aload 5
   // 205: aconst_null
   // 206: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlinx/coroutines/sync/Semaphore;Lkotlin/coroutines/Continuation;)V
   // 209: checkcast kotlin/jvm/functions/Function2
   // 20c: aload 19
   // 20e: aload 19
   // 210: aload 1
   // 211: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 214: aload 19
   // 216: aload 9
   // 218: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 21b: aload 19
   // 21d: aload 12
   // 21f: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 222: aload 19
   // 224: aconst_null
   // 225: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 228: aload 19
   // 22a: bipush 3
   // 22b: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 22e: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 231: dup
   // 232: aload 20
   // 234: if_acmpne 26b
   // 237: aload 20
   // 239: areturn
   // 23a: bipush 0
   // 23b: istore 7
   // 23d: bipush 0
   // 23e: istore 8
   // 240: bipush 0
   // 241: istore 10
   // 243: bipush 0
   // 244: istore 11
   // 246: aload 19
   // 248: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 24b: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
   // 24e: astore 12
   // 250: aload 19
   // 252: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 255: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 258: astore 9
   // 25a: aload 19
   // 25c: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 25f: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 262: astore 1
   // 263: nop
   // 264: aload 18
   // 266: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 269: aload 18
   // 26b: pop
   // 26c: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 26f: astore 13
   // 271: aload 9
   // 273: aload 12
   // 275: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 278: goto 287
   // 27b: astore 14
   // 27d: aload 9
   // 27f: aload 12
   // 281: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 284: aload 14
   // 286: athrow
   // 287: aload 1
   // 288: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 28d: goto 29b
   // 290: astore 9
   // 292: aload 1
   // 293: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 298: aload 9
   // 29a: athrow
   // 29b: nop
   // 29c: goto 4df
   // 29f: aload 6
   // 2a1: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
   // 2a4: astore 15
   // 2a6: nop
   // 2a7: bipush 0
   // 2a8: istore 8
   // 2aa: nop
   // 2ab: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$1
   // 2ae: dup
   // 2af: aload 1
   // 2b0: aload 3
   // 2b1: aload 5
   // 2b3: aload 4
   // 2b5: aload 0
   // 2b6: aconst_null
   // 2b7: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Ljava/util/Set;Lkotlinx/coroutines/sync/Semaphore;Lcn/sast/api/util/IMonitor;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlin/coroutines/Continuation;)V
   // 2ba: checkcast kotlin/jvm/functions/Function2
   // 2bd: aload 19
   // 2bf: aload 19
   // 2c1: aload 0
   // 2c2: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 2c5: aload 19
   // 2c7: aload 1
   // 2c8: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 2cb: aload 19
   // 2cd: aload 4
   // 2cf: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 2d2: aload 19
   // 2d4: aload 5
   // 2d6: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 2d9: aload 19
   // 2db: aload 6
   // 2dd: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$4 Ljava/lang/Object;
   // 2e0: aload 19
   // 2e2: aload 15
   // 2e4: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$5 Ljava/lang/Object;
   // 2e7: aload 19
   // 2e9: bipush 4
   // 2ea: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 2ed: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 2f0: dup
   // 2f1: aload 20
   // 2f3: if_acmpne 341
   // 2f6: aload 20
   // 2f8: areturn
   // 2f9: bipush 0
   // 2fa: istore 7
   // 2fc: bipush 0
   // 2fd: istore 8
   // 2ff: aload 19
   // 301: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$5 Ljava/lang/Object;
   // 304: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
   // 307: astore 15
   // 309: aload 19
   // 30b: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$4 Ljava/lang/Object;
   // 30e: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 311: astore 6
   // 313: aload 19
   // 315: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 318: checkcast kotlinx/coroutines/sync/Semaphore
   // 31b: astore 5
   // 31d: aload 19
   // 31f: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 322: checkcast cn/sast/api/util/IMonitor
   // 325: astore 4
   // 327: aload 19
   // 329: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 32c: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 32f: astore 1
   // 330: aload 19
   // 332: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 335: checkcast com/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl
   // 338: astore 0
   // 339: nop
   // 33a: aload 18
   // 33c: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 33f: aload 18
   // 341: pop
   // 342: aload 1
   // 343: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 348: goto 356
   // 34b: astore 9
   // 34d: aload 1
   // 34e: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 353: aload 9
   // 355: athrow
   // 356: nop
   // 357: aload 4
   // 359: dup
   // 35a: ifnull 367
   // 35d: ldc "AIAnalysis.initializeClassCallBacks"
   // 35f: invokeinterface cn/sast/api/util/IMonitor.timer (Ljava/lang/String;)Lcn/sast/api/util/Timer; 2
   // 364: goto 369
   // 367: pop
   // 368: aconst_null
   // 369: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 36c: astore 9
   // 36e: bipush 0
   // 36f: istore 10
   // 371: aload 9
   // 373: ifnonnull 3f7
   // 376: bipush 0
   // 377: istore 11
   // 379: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1
   // 37c: dup
   // 37d: aload 1
   // 37e: aload 0
   // 37f: aload 5
   // 381: aconst_null
   // 382: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlinx/coroutines/sync/Semaphore;Lkotlin/coroutines/Continuation;)V
   // 385: checkcast kotlin/jvm/functions/Function2
   // 388: aload 19
   // 38a: aload 19
   // 38c: aload 1
   // 38d: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 390: aload 19
   // 392: aload 6
   // 394: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 397: aload 19
   // 399: aload 15
   // 39b: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 39e: aload 19
   // 3a0: aconst_null
   // 3a1: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 3a4: aload 19
   // 3a6: aconst_null
   // 3a7: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$4 Ljava/lang/Object;
   // 3aa: aload 19
   // 3ac: aconst_null
   // 3ad: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$5 Ljava/lang/Object;
   // 3b0: aload 19
   // 3b2: bipush 5
   // 3b3: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 3b6: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 3b9: dup
   // 3ba: aload 20
   // 3bc: if_acmpne 3f3
   // 3bf: aload 20
   // 3c1: areturn
   // 3c2: bipush 0
   // 3c3: istore 7
   // 3c5: bipush 0
   // 3c6: istore 8
   // 3c8: bipush 0
   // 3c9: istore 10
   // 3cb: bipush 0
   // 3cc: istore 11
   // 3ce: aload 19
   // 3d0: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 3d3: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
   // 3d6: astore 15
   // 3d8: aload 19
   // 3da: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 3dd: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 3e0: astore 6
   // 3e2: aload 19
   // 3e4: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 3e7: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 3ea: astore 1
   // 3eb: nop
   // 3ec: aload 18
   // 3ee: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 3f1: aload 18
   // 3f3: pop
   // 3f4: goto 4af
   // 3f7: aload 9
   // 3f9: invokevirtual cn/sast/api/util/PhaseIntervalTimer.start ()Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;
   // 3fc: astore 12
   // 3fe: nop
   // 3ff: bipush 0
   // 400: istore 11
   // 402: new cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1
   // 405: dup
   // 406: aload 1
   // 407: aload 0
   // 408: aload 5
   // 40a: aconst_null
   // 40b: invokespecial cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$2$2$1.<init> (Lcn/sast/api/config/PreAnalysisCoroutineScope;Lcom/feysh/corax/config/api/baseimpl/AIAnalysisBaseImpl;Lkotlinx/coroutines/sync/Semaphore;Lkotlin/coroutines/Continuation;)V
   // 40e: checkcast kotlin/jvm/functions/Function2
   // 411: aload 19
   // 413: aload 19
   // 415: aload 1
   // 416: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 419: aload 19
   // 41b: aload 6
   // 41d: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 420: aload 19
   // 422: aload 9
   // 424: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 427: aload 19
   // 429: aload 12
   // 42b: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 42e: aload 19
   // 430: aload 15
   // 432: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$4 Ljava/lang/Object;
   // 435: aload 19
   // 437: aconst_null
   // 438: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$5 Ljava/lang/Object;
   // 43b: aload 19
   // 43d: bipush 6
   // 43f: putfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.label I
   // 442: invokestatic kotlinx/coroutines/CoroutineScopeKt.coroutineScope (Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
   // 445: dup
   // 446: aload 20
   // 448: if_acmpne 493
   // 44b: aload 20
   // 44d: areturn
   // 44e: bipush 0
   // 44f: istore 7
   // 451: bipush 0
   // 452: istore 8
   // 454: bipush 0
   // 455: istore 10
   // 457: bipush 0
   // 458: istore 11
   // 45a: aload 19
   // 45c: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$4 Ljava/lang/Object;
   // 45f: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
   // 462: astore 15
   // 464: aload 19
   // 466: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$3 Ljava/lang/Object;
   // 469: checkcast cn/sast/api/util/PhaseIntervalTimer$Snapshot
   // 46c: astore 12
   // 46e: aload 19
   // 470: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$2 Ljava/lang/Object;
   // 473: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 476: astore 9
   // 478: aload 19
   // 47a: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$1 Ljava/lang/Object;
   // 47d: checkcast cn/sast/api/util/PhaseIntervalTimer
   // 480: astore 6
   // 482: aload 19
   // 484: getfield cn/sast/api/config/PreAnalysisCoroutineScopeKt$processAIAnalysisUnits$1.L$0 Ljava/lang/Object;
   // 487: checkcast cn/sast/api/config/PreAnalysisCoroutineScope
   // 48a: astore 1
   // 48b: nop
   // 48c: aload 18
   // 48e: invokestatic kotlin/ResultKt.throwOnFailure (Ljava/lang/Object;)V
   // 491: aload 18
   // 493: pop
   // 494: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 497: astore 13
   // 499: aload 9
   // 49b: aload 12
   // 49d: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 4a0: goto 4af
   // 4a3: astore 14
   // 4a5: aload 9
   // 4a7: aload 12
   // 4a9: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 4ac: aload 14
   // 4ae: athrow
   // 4af: aload 1
   // 4b0: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 4b5: goto 4c3
   // 4b8: astore 9
   // 4ba: aload 1
   // 4bb: invokeinterface cn/sast/api/config/PreAnalysisCoroutineScope.uninitializedScope ()V 1
   // 4c0: aload 9
   // 4c2: athrow
   // 4c3: nop
   // 4c4: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 4c7: astore 16
   // 4c9: aload 6
   // 4cb: aload 15
   // 4cd: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 4d0: goto 4df
   // 4d3: astore 17
   // 4d5: aload 6
   // 4d7: aload 15
   // 4d9: invokevirtual cn/sast/api/util/PhaseIntervalTimer.stop (Lcn/sast/api/util/PhaseIntervalTimer$Snapshot;)V
   // 4dc: aload 17
   // 4de: athrow
   // 4df: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
   // 4e2: areturn
   // 4e3: new java/lang/IllegalStateException
   // 4e6: dup
   // 4e7: ldc "call to 'resume' before 'invoke' with coroutine"
   // 4e9: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
   // 4ec: athrow
}
