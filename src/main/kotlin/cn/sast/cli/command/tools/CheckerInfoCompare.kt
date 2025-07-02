package cn.sast.cli.command.tools

import cn.sast.api.config.CheckerInfo
import cn.sast.common.IResFile
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import mu.KLogger

@SourceDebugExtension(["SMAP\nCheckerInfoCompare.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfoCompare.kt\ncn/sast/cli/command/tools/CheckerInfoCompare\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,59:1\n1#2:60\n774#3:61\n865#3,2:62\n1557#3:64\n1628#3,3:65\n774#3:68\n865#3,2:69\n774#3:71\n865#3,2:72\n1557#3:74\n1628#3,3:75\n1187#3,2:78\n1261#3,4:80\n1628#3,3:84\n774#3:87\n865#3,2:88\n*S KotlinDebug\n*F\n+ 1 CheckerInfoCompare.kt\ncn/sast/cli/command/tools/CheckerInfoCompare\n*L\n28#1:61\n28#1:62,2\n28#1:64\n28#1:65,3\n29#1:68\n29#1:69,2\n31#1:71\n31#1:72,2\n34#1:74\n34#1:75,3\n44#1:78,2\n44#1:80,4\n47#1:84,3\n48#1:87\n48#1:88,2\n*E\n"])
public class CheckerInfoCompare {
   public fun compareWith(output: Path, left: IResFile, right: IResFile) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:276)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc "output"
      // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 006: aload 2
      // 007: ldc "left"
      // 009: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 00c: aload 3
      // 00d: ldc "right"
      // 00f: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 012: aload 3
      // 013: invokeinterface cn/sast/common/IResFile.getPath ()Ljava/nio/file/Path; 1
      // 018: bipush 0
      // 019: anewarray 35
      // 01c: dup
      // 01d: arraylength
      // 01e: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 021: checkcast [Ljava/nio/file/OpenOption;
      // 024: invokestatic java/nio/file/Files.newInputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
      // 027: dup
      // 028: ldc "newInputStream(...)"
      // 02a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 02d: checkcast java/io/Closeable
      // 030: astore 5
      // 032: aconst_null
      // 033: astore 6
      // 035: nop
      // 036: aload 5
      // 038: checkcast java/io/InputStream
      // 03b: astore 7
      // 03d: bipush 0
      // 03e: istore 8
      // 040: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 043: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.infoSerializer Lkotlinx/serialization/KSerializer;
      // 046: checkcast kotlinx/serialization/DeserializationStrategy
      // 049: aload 7
      // 04b: invokestatic kotlinx/serialization/json/JvmStreamsKt.decodeFromStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/DeserializationStrategy;Ljava/io/InputStream;)Ljava/lang/Object;
      // 04e: checkcast java/util/List
      // 051: astore 7
      // 053: aload 5
      // 055: aload 6
      // 057: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 05a: aload 7
      // 05c: goto 074
      // 05f: astore 7
      // 061: aload 7
      // 063: astore 6
      // 065: aload 7
      // 067: athrow
      // 068: astore 7
      // 06a: aload 5
      // 06c: aload 6
      // 06e: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 071: aload 7
      // 073: athrow
      // 074: astore 4
      // 076: aload 2
      // 077: invokeinterface cn/sast/common/IResFile.getPath ()Ljava/nio/file/Path; 1
      // 07c: bipush 0
      // 07d: anewarray 35
      // 080: dup
      // 081: arraylength
      // 082: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 085: checkcast [Ljava/nio/file/OpenOption;
      // 088: invokestatic java/nio/file/Files.newInputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
      // 08b: dup
      // 08c: ldc "newInputStream(...)"
      // 08e: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 091: checkcast java/io/Closeable
      // 094: astore 6
      // 096: aconst_null
      // 097: astore 7
      // 099: nop
      // 09a: aload 6
      // 09c: checkcast java/io/InputStream
      // 09f: astore 8
      // 0a1: bipush 0
      // 0a2: istore 9
      // 0a4: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 0a7: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.infoSerializer Lkotlinx/serialization/KSerializer;
      // 0aa: checkcast kotlinx/serialization/DeserializationStrategy
      // 0ad: aload 8
      // 0af: invokestatic kotlinx/serialization/json/JvmStreamsKt.decodeFromStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/DeserializationStrategy;Ljava/io/InputStream;)Ljava/lang/Object;
      // 0b2: checkcast java/util/List
      // 0b5: astore 8
      // 0b7: aload 6
      // 0b9: aload 7
      // 0bb: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 0be: aload 8
      // 0c0: goto 0d8
      // 0c3: astore 8
      // 0c5: aload 8
      // 0c7: astore 7
      // 0c9: aload 8
      // 0cb: athrow
      // 0cc: astore 8
      // 0ce: aload 6
      // 0d0: aload 7
      // 0d2: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 0d5: aload 8
      // 0d7: athrow
      // 0d8: astore 5
      // 0da: aload 1
      // 0db: aload 2
      // 0dc: invokeinterface cn/sast/common/IResFile.getName ()Ljava/lang/String; 1
      // 0e1: ldc "."
      // 0e3: aconst_null
      // 0e4: bipush 2
      // 0e5: aconst_null
      // 0e6: invokestatic kotlin/text/StringsKt.substringBeforeLast$default (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/Object;)Ljava/lang/String;
      // 0e9: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "compare-\u0001" ]
      // 0ee: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 0f3: astore 6
      // 0f5: aload 6
      // 0f7: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0fa: aload 6
      // 0fc: bipush 0
      // 0fd: anewarray 118
      // 100: dup
      // 101: arraylength
      // 102: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 105: checkcast [Ljava/nio/file/LinkOption;
      // 108: invokestatic java/nio/file/Files.exists (Ljava/nio/file/Path;[Ljava/nio/file/LinkOption;)Z
      // 10b: ifne 118
      // 10e: aload 6
      // 110: bipush 0
      // 111: anewarray 126
      // 114: invokestatic java/nio/file/Files.createDirectories (Ljava/nio/file/Path;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;
      // 117: pop
      // 118: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.logger Lmu/KLogger;
      // 11b: aload 2
      // 11c: aload 3
      // 11d: aload 6
      // 11f: invokedynamic invoke (Lcn/sast/common/IResFile;Lcn/sast/common/IResFile;Ljava/nio/file/Path;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/cli/command/tools/CheckerInfoCompare.compareWith$lambda$2 (Lcn/sast/common/IResFile;Lcn/sast/common/IResFile;Ljava/nio/file/Path;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 124: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 129: aload 4
      // 12b: checkcast java/lang/Iterable
      // 12e: astore 8
      // 130: bipush 0
      // 131: istore 9
      // 133: aload 8
      // 135: astore 10
      // 137: new java/util/ArrayList
      // 13a: dup
      // 13b: invokespecial java/util/ArrayList.<init> ()V
      // 13e: checkcast java/util/Collection
      // 141: astore 11
      // 143: bipush 0
      // 144: istore 12
      // 146: aload 10
      // 148: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 14d: astore 13
      // 14f: aload 13
      // 151: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 156: ifeq 186
      // 159: aload 13
      // 15b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 160: astore 14
      // 162: aload 14
      // 164: checkcast cn/sast/api/config/CheckerInfo
      // 167: astore 15
      // 169: bipush 0
      // 16a: istore 16
      // 16c: aload 15
      // 16e: invokevirtual cn/sast/api/config/CheckerInfo.getAnalyzer ()Ljava/lang/String;
      // 171: ldc "Java(canary)"
      // 173: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 176: ifeq 14f
      // 179: aload 11
      // 17b: aload 14
      // 17d: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 182: pop
      // 183: goto 14f
      // 186: aload 11
      // 188: checkcast java/util/List
      // 18b: nop
      // 18c: checkcast java/lang/Iterable
      // 18f: astore 8
      // 191: nop
      // 192: bipush 0
      // 193: istore 9
      // 195: aload 8
      // 197: astore 10
      // 199: new java/util/ArrayList
      // 19c: dup
      // 19d: aload 8
      // 19f: bipush 10
      // 1a1: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 1a4: invokespecial java/util/ArrayList.<init> (I)V
      // 1a7: checkcast java/util/Collection
      // 1aa: astore 11
      // 1ac: bipush 0
      // 1ad: istore 12
      // 1af: aload 10
      // 1b1: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 1b6: astore 13
      // 1b8: aload 13
      // 1ba: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 1bf: ifeq 1ea
      // 1c2: aload 13
      // 1c4: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 1c9: astore 14
      // 1cb: aload 11
      // 1cd: aload 14
      // 1cf: checkcast cn/sast/api/config/CheckerInfo
      // 1d2: astore 15
      // 1d4: astore 28
      // 1d6: bipush 0
      // 1d7: istore 16
      // 1d9: aload 15
      // 1db: invokevirtual cn/sast/api/config/CheckerInfo.getChecker_id ()Ljava/lang/String;
      // 1de: aload 28
      // 1e0: swap
      // 1e1: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 1e6: pop
      // 1e7: goto 1b8
      // 1ea: aload 11
      // 1ec: checkcast java/util/List
      // 1ef: nop
      // 1f0: astore 7
      // 1f2: aload 5
      // 1f4: checkcast java/lang/Iterable
      // 1f7: astore 9
      // 1f9: bipush 0
      // 1fa: istore 10
      // 1fc: aload 9
      // 1fe: astore 11
      // 200: new java/util/ArrayList
      // 203: dup
      // 204: invokespecial java/util/ArrayList.<init> ()V
      // 207: checkcast java/util/Collection
      // 20a: astore 12
      // 20c: bipush 0
      // 20d: istore 13
      // 20f: aload 11
      // 211: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 216: astore 14
      // 218: aload 14
      // 21a: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 21f: ifeq 24f
      // 222: aload 14
      // 224: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 229: astore 15
      // 22b: aload 15
      // 22d: checkcast cn/sast/api/config/CheckerInfo
      // 230: astore 16
      // 232: bipush 0
      // 233: istore 17
      // 235: aload 16
      // 237: invokevirtual cn/sast/api/config/CheckerInfo.getAnalyzer ()Ljava/lang/String;
      // 23a: ldc "Java(canary)"
      // 23c: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 23f: ifeq 218
      // 242: aload 12
      // 244: aload 15
      // 246: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 24b: pop
      // 24c: goto 218
      // 24f: aload 12
      // 251: checkcast java/util/List
      // 254: nop
      // 255: astore 8
      // 257: aload 8
      // 259: checkcast java/lang/Iterable
      // 25c: astore 10
      // 25e: bipush 0
      // 25f: istore 11
      // 261: aload 10
      // 263: astore 12
      // 265: new java/util/ArrayList
      // 268: dup
      // 269: invokespecial java/util/ArrayList.<init> ()V
      // 26c: checkcast java/util/Collection
      // 26f: astore 13
      // 271: bipush 0
      // 272: istore 14
      // 274: aload 12
      // 276: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 27b: astore 15
      // 27d: aload 15
      // 27f: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 284: ifeq 2be
      // 287: aload 15
      // 289: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 28e: astore 16
      // 290: aload 16
      // 292: checkcast cn/sast/api/config/CheckerInfo
      // 295: astore 17
      // 297: bipush 0
      // 298: istore 18
      // 29a: aload 7
      // 29c: aload 17
      // 29e: invokevirtual cn/sast/api/config/CheckerInfo.getChecker_id ()Ljava/lang/String;
      // 2a1: invokeinterface java/util/List.contains (Ljava/lang/Object;)Z 2
      // 2a6: ifne 2ad
      // 2a9: bipush 1
      // 2aa: goto 2ae
      // 2ad: bipush 0
      // 2ae: ifeq 27d
      // 2b1: aload 13
      // 2b3: aload 16
      // 2b5: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 2ba: pop
      // 2bb: goto 27d
      // 2be: aload 13
      // 2c0: checkcast java/util/List
      // 2c3: nop
      // 2c4: astore 9
      // 2c6: aload 6
      // 2c8: ldc "deleted-checker-ids.json"
      // 2ca: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 2cf: dup
      // 2d0: ldc "resolve(...)"
      // 2d2: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 2d5: astore 10
      // 2d7: bipush 0
      // 2d8: anewarray 35
      // 2db: astore 11
      // 2dd: aload 10
      // 2df: aload 11
      // 2e1: aload 11
      // 2e3: arraylength
      // 2e4: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 2e7: checkcast [Ljava/nio/file/OpenOption;
      // 2ea: invokestatic java/nio/file/Files.newOutputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
      // 2ed: dup
      // 2ee: ldc "newOutputStream(...)"
      // 2f0: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 2f3: checkcast java/io/Closeable
      // 2f6: astore 10
      // 2f8: aconst_null
      // 2f9: astore 11
      // 2fb: nop
      // 2fc: aload 10
      // 2fe: checkcast java/io/OutputStream
      // 301: astore 12
      // 303: bipush 0
      // 304: istore 13
      // 306: aload 9
      // 308: checkcast java/lang/Iterable
      // 30b: astore 14
      // 30d: bipush 0
      // 30e: istore 15
      // 310: aload 14
      // 312: astore 16
      // 314: new java/util/ArrayList
      // 317: dup
      // 318: aload 14
      // 31a: bipush 10
      // 31c: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 31f: invokespecial java/util/ArrayList.<init> (I)V
      // 322: checkcast java/util/Collection
      // 325: astore 17
      // 327: bipush 0
      // 328: istore 18
      // 32a: aload 16
      // 32c: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 331: astore 19
      // 333: aload 19
      // 335: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 33a: ifeq 365
      // 33d: aload 19
      // 33f: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 344: astore 20
      // 346: aload 17
      // 348: aload 20
      // 34a: checkcast cn/sast/api/config/CheckerInfo
      // 34d: astore 21
      // 34f: astore 22
      // 351: bipush 0
      // 352: istore 23
      // 354: aload 21
      // 356: invokevirtual cn/sast/api/config/CheckerInfo.getChecker_id ()Ljava/lang/String;
      // 359: aload 22
      // 35b: swap
      // 35c: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 361: pop
      // 362: goto 333
      // 365: aload 17
      // 367: checkcast java/util/List
      // 36a: nop
      // 36b: astore 24
      // 36d: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.logger Lmu/KLogger;
      // 370: aload 24
      // 372: invokedynamic invoke (Ljava/util/List;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/cli/command/tools/CheckerInfoCompare.compareWith$lambda$9$lambda$8 (Ljava/util/List;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 377: invokeinterface mu/KLogger.warn (Lkotlin/jvm/functions/Function0;)V 2
      // 37c: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 37f: getstatic kotlin/jvm/internal/StringCompanionObject.INSTANCE Lkotlin/jvm/internal/StringCompanionObject;
      // 382: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.serializer (Lkotlin/jvm/internal/StringCompanionObject;)Lkotlinx/serialization/KSerializer;
      // 385: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.ListSerializer (Lkotlinx/serialization/KSerializer;)Lkotlinx/serialization/KSerializer;
      // 388: checkcast kotlinx/serialization/SerializationStrategy
      // 38b: aload 24
      // 38d: aload 12
      // 38f: invokestatic kotlinx/serialization/json/JvmStreamsKt.encodeToStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/SerializationStrategy;Ljava/lang/Object;Ljava/io/OutputStream;)V
      // 392: nop
      // 393: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 396: astore 12
      // 398: aload 10
      // 39a: aload 11
      // 39c: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 39f: goto 3b7
      // 3a2: astore 12
      // 3a4: aload 12
      // 3a6: astore 11
      // 3a8: aload 12
      // 3aa: athrow
      // 3ab: astore 12
      // 3ad: aload 10
      // 3af: aload 11
      // 3b1: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 3b4: aload 12
      // 3b6: athrow
      // 3b7: aload 6
      // 3b9: ldc_w "deleted.json"
      // 3bc: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 3c1: dup
      // 3c2: ldc "resolve(...)"
      // 3c4: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 3c7: astore 10
      // 3c9: bipush 0
      // 3ca: anewarray 35
      // 3cd: astore 11
      // 3cf: aload 10
      // 3d1: aload 11
      // 3d3: aload 11
      // 3d5: arraylength
      // 3d6: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 3d9: checkcast [Ljava/nio/file/OpenOption;
      // 3dc: invokestatic java/nio/file/Files.newOutputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
      // 3df: dup
      // 3e0: ldc "newOutputStream(...)"
      // 3e2: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 3e5: checkcast java/io/Closeable
      // 3e8: astore 10
      // 3ea: aconst_null
      // 3eb: astore 11
      // 3ed: nop
      // 3ee: aload 10
      // 3f0: checkcast java/io/OutputStream
      // 3f3: astore 12
      // 3f5: bipush 0
      // 3f6: istore 13
      // 3f8: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 3fb: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.infoSerializer Lkotlinx/serialization/KSerializer;
      // 3fe: checkcast kotlinx/serialization/SerializationStrategy
      // 401: aload 9
      // 403: aload 12
      // 405: invokestatic kotlinx/serialization/json/JvmStreamsKt.encodeToStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/SerializationStrategy;Ljava/lang/Object;Ljava/io/OutputStream;)V
      // 408: nop
      // 409: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 40c: astore 12
      // 40e: aload 10
      // 410: aload 11
      // 412: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 415: goto 42d
      // 418: astore 12
      // 41a: aload 12
      // 41c: astore 11
      // 41e: aload 12
      // 420: athrow
      // 421: astore 12
      // 423: aload 10
      // 425: aload 11
      // 427: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 42a: aload 12
      // 42c: athrow
      // 42d: aload 6
      // 42f: ldc_w "checker_name_mapping.json"
      // 432: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 437: dup
      // 438: ldc "resolve(...)"
      // 43a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 43d: astore 10
      // 43f: bipush 0
      // 440: anewarray 35
      // 443: astore 11
      // 445: aload 10
      // 447: aload 11
      // 449: aload 11
      // 44b: arraylength
      // 44c: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 44f: checkcast [Ljava/nio/file/OpenOption;
      // 452: invokestatic java/nio/file/Files.newOutputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
      // 455: dup
      // 456: ldc "newOutputStream(...)"
      // 458: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 45b: checkcast java/io/Closeable
      // 45e: astore 10
      // 460: aconst_null
      // 461: astore 11
      // 463: nop
      // 464: aload 10
      // 466: checkcast java/io/OutputStream
      // 469: astore 12
      // 46b: bipush 0
      // 46c: istore 13
      // 46e: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 471: getstatic kotlin/jvm/internal/StringCompanionObject.INSTANCE Lkotlin/jvm/internal/StringCompanionObject;
      // 474: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.serializer (Lkotlin/jvm/internal/StringCompanionObject;)Lkotlinx/serialization/KSerializer;
      // 477: getstatic kotlin/jvm/internal/StringCompanionObject.INSTANCE Lkotlin/jvm/internal/StringCompanionObject;
      // 47a: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.serializer (Lkotlin/jvm/internal/StringCompanionObject;)Lkotlinx/serialization/KSerializer;
      // 47d: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.MapSerializer (Lkotlinx/serialization/KSerializer;Lkotlinx/serialization/KSerializer;)Lkotlinx/serialization/KSerializer;
      // 480: checkcast kotlinx/serialization/SerializationStrategy
      // 483: aload 9
      // 485: checkcast java/lang/Iterable
      // 488: astore 14
      // 48a: astore 15
      // 48c: astore 16
      // 48e: bipush 0
      // 48f: istore 17
      // 491: aload 14
      // 493: bipush 10
      // 495: invokestatic kotlin/collections/CollectionsKt.collectionSizeOrDefault (Ljava/lang/Iterable;I)I
      // 498: invokestatic kotlin/collections/MapsKt.mapCapacity (I)I
      // 49b: bipush 16
      // 49d: invokestatic kotlin/ranges/RangesKt.coerceAtLeast (II)I
      // 4a0: istore 18
      // 4a2: aload 14
      // 4a4: astore 19
      // 4a6: new java/util/LinkedHashMap
      // 4a9: dup
      // 4aa: iload 18
      // 4ac: invokespecial java/util/LinkedHashMap.<init> (I)V
      // 4af: checkcast java/util/Map
      // 4b2: astore 20
      // 4b4: bipush 0
      // 4b5: istore 21
      // 4b7: aload 19
      // 4b9: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 4be: astore 22
      // 4c0: aload 22
      // 4c2: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 4c7: ifeq 503
      // 4ca: aload 22
      // 4cc: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 4d1: astore 23
      // 4d3: aload 20
      // 4d5: astore 24
      // 4d7: aload 23
      // 4d9: checkcast cn/sast/api/config/CheckerInfo
      // 4dc: astore 25
      // 4de: bipush 0
      // 4df: istore 26
      // 4e1: aload 25
      // 4e3: invokevirtual cn/sast/api/config/CheckerInfo.getChecker_id ()Ljava/lang/String;
      // 4e6: ldc_w ""
      // 4e9: invokestatic kotlin/TuplesKt.to (Ljava/lang/Object;Ljava/lang/Object;)Lkotlin/Pair;
      // 4ec: astore 25
      // 4ee: aload 24
      // 4f0: aload 25
      // 4f2: invokevirtual kotlin/Pair.getFirst ()Ljava/lang/Object;
      // 4f5: aload 25
      // 4f7: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
      // 4fa: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 4ff: pop
      // 500: goto 4c0
      // 503: aload 20
      // 505: nop
      // 506: astore 27
      // 508: aload 16
      // 50a: aload 15
      // 50c: aload 27
      // 50e: aload 12
      // 510: invokestatic kotlinx/serialization/json/JvmStreamsKt.encodeToStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/SerializationStrategy;Ljava/lang/Object;Ljava/io/OutputStream;)V
      // 513: nop
      // 514: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 517: astore 12
      // 519: aload 10
      // 51b: aload 11
      // 51d: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 520: goto 538
      // 523: astore 12
      // 525: aload 12
      // 527: astore 11
      // 529: aload 12
      // 52b: athrow
      // 52c: astore 12
      // 52e: aload 10
      // 530: aload 11
      // 532: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 535: aload 12
      // 537: athrow
      // 538: aload 8
      // 53a: checkcast java/lang/Iterable
      // 53d: astore 11
      // 53f: new java/util/LinkedHashSet
      // 542: dup
      // 543: invokespecial java/util/LinkedHashSet.<init> ()V
      // 546: checkcast java/util/Set
      // 549: checkcast java/util/Collection
      // 54c: astore 12
      // 54e: nop
      // 54f: bipush 0
      // 550: istore 13
      // 552: aload 11
      // 554: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 559: astore 14
      // 55b: aload 14
      // 55d: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 562: ifeq 58d
      // 565: aload 14
      // 567: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 56c: astore 15
      // 56e: aload 12
      // 570: aload 15
      // 572: checkcast cn/sast/api/config/CheckerInfo
      // 575: astore 16
      // 577: astore 28
      // 579: bipush 0
      // 57a: istore 17
      // 57c: aload 16
      // 57e: invokevirtual cn/sast/api/config/CheckerInfo.getChecker_id ()Ljava/lang/String;
      // 581: aload 28
      // 583: swap
      // 584: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 589: pop
      // 58a: goto 55b
      // 58d: aload 12
      // 58f: checkcast java/util/Set
      // 592: astore 10
      // 594: aload 7
      // 596: checkcast java/lang/Iterable
      // 599: astore 12
      // 59b: bipush 0
      // 59c: istore 13
      // 59e: aload 12
      // 5a0: astore 14
      // 5a2: new java/util/ArrayList
      // 5a5: dup
      // 5a6: invokespecial java/util/ArrayList.<init> ()V
      // 5a9: checkcast java/util/Collection
      // 5ac: astore 15
      // 5ae: bipush 0
      // 5af: istore 16
      // 5b1: aload 14
      // 5b3: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 5b8: astore 17
      // 5ba: aload 17
      // 5bc: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 5c1: ifeq 5f8
      // 5c4: aload 17
      // 5c6: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 5cb: astore 18
      // 5cd: aload 18
      // 5cf: checkcast java/lang/String
      // 5d2: astore 19
      // 5d4: bipush 0
      // 5d5: istore 20
      // 5d7: aload 10
      // 5d9: aload 19
      // 5db: invokeinterface java/util/Set.contains (Ljava/lang/Object;)Z 2
      // 5e0: ifne 5e7
      // 5e3: bipush 1
      // 5e4: goto 5e8
      // 5e7: bipush 0
      // 5e8: ifeq 5ba
      // 5eb: aload 15
      // 5ed: aload 18
      // 5ef: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 5f4: pop
      // 5f5: goto 5ba
      // 5f8: aload 15
      // 5fa: checkcast java/util/List
      // 5fd: nop
      // 5fe: astore 11
      // 600: aload 6
      // 602: ldc_w "new.json"
      // 605: invokeinterface java/nio/file/Path.resolve (Ljava/lang/String;)Ljava/nio/file/Path; 2
      // 60a: dup
      // 60b: ldc "resolve(...)"
      // 60d: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 610: astore 12
      // 612: bipush 0
      // 613: anewarray 35
      // 616: astore 13
      // 618: aload 12
      // 61a: aload 13
      // 61c: aload 13
      // 61e: arraylength
      // 61f: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 622: checkcast [Ljava/nio/file/OpenOption;
      // 625: invokestatic java/nio/file/Files.newOutputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
      // 628: dup
      // 629: ldc "newOutputStream(...)"
      // 62b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 62e: checkcast java/io/Closeable
      // 631: astore 12
      // 633: aconst_null
      // 634: astore 13
      // 636: nop
      // 637: aload 12
      // 639: checkcast java/io/OutputStream
      // 63c: astore 14
      // 63e: bipush 0
      // 63f: istore 15
      // 641: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.logger Lmu/KLogger;
      // 644: aload 11
      // 646: invokedynamic invoke (Ljava/util/List;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/cli/command/tools/CheckerInfoCompare.compareWith$lambda$16$lambda$15 (Ljava/util/List;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 64b: invokeinterface mu/KLogger.info (Lkotlin/jvm/functions/Function0;)V 2
      // 650: getstatic cn/sast/cli/command/tools/CheckerInfoCompare.jsonFormat Lkotlinx/serialization/json/Json;
      // 653: getstatic kotlin/jvm/internal/StringCompanionObject.INSTANCE Lkotlin/jvm/internal/StringCompanionObject;
      // 656: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.serializer (Lkotlin/jvm/internal/StringCompanionObject;)Lkotlinx/serialization/KSerializer;
      // 659: invokestatic kotlinx/serialization/builtins/BuiltinSerializersKt.ListSerializer (Lkotlinx/serialization/KSerializer;)Lkotlinx/serialization/KSerializer;
      // 65c: checkcast kotlinx/serialization/SerializationStrategy
      // 65f: aload 11
      // 661: aload 14
      // 663: invokestatic kotlinx/serialization/json/JvmStreamsKt.encodeToStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/SerializationStrategy;Ljava/lang/Object;Ljava/io/OutputStream;)V
      // 666: nop
      // 667: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 66a: astore 14
      // 66c: aload 12
      // 66e: aload 13
      // 670: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 673: goto 68b
      // 676: astore 14
      // 678: aload 14
      // 67a: astore 13
      // 67c: aload 14
      // 67e: athrow
      // 67f: astore 14
      // 681: aload 12
      // 683: aload 13
      // 685: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 688: aload 14
      // 68a: athrow
      // 68b: return
   }

   @JvmStatic
   fun `compareWith$lambda$2`(`$left`: IResFile, `$right`: IResFile, `$out`: Path): Any {
      return "The computed diff between '$`$left`' and '$`$right`' yields the following result: $`$out`";
   }

   @JvmStatic
   fun `compareWith$lambda$9$lambda$8`(`$deletedIds`: java.util.List): Any {
      return "[-] Deleted ${`$deletedIds`.size()} checker ids. ${CollectionsKt.joinToString$default(
         `$deletedIds`, "\n\t", "[\n\t", "\n]", 0, null, null, 56, null
      )}";
   }

   @JvmStatic
   fun `compareWith$lambda$16$lambda$15`(`$new`: java.util.List): Any {
      return "[+] New ${`$new`.size()} checker ids: ${CollectionsKt.joinToString$default(`$new`, "\n\t", "[\n\t", "\n]", 0, null, null, 56, null)}";
   }

   @JvmStatic
   fun `logger$lambda$17`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
      private final val jsonFormat: Json
      private final val infoSerializer: KSerializer<List<CheckerInfo>>
   }
}
