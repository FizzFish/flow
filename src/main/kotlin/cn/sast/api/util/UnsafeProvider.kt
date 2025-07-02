package cn.sast.api.util

import sun.misc.Unsafe

public object UnsafeProvider {
   public final val unsafe: Unsafe = INSTANCE.getUnsafeInternal()

   private final val unsafeInternal: Unsafe
      private final get() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.NullPointerException: Cannot read field "bytecode" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInstance()" is null
         //   at org.vineflower.kotlin.pass.KMergePass.matchForEach(KMergePass.java:165)
         //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoop(KMergePass.java:52)
         //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:39)
         //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
         //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
         //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
         //   at org.vineflower.kotlin.pass.KMergePass.run(KMergePass.java:23)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
         //   at org.jetbrains.java.decompiler.api.plugin.pass.MainPassBuilder$CompiledPass.run(MainPassBuilder.java:34)
         //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:160)
         //
         // Bytecode:
         // 00: nop
         // 01: invokestatic sun/misc/Unsafe.getUnsafe ()Lsun/misc/Unsafe;
         // 04: dup
         // 05: ldc "getUnsafe(...)"
         // 07: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
         // 0a: areturn
         // 0b: astore 1
         // 0c: ldc sun/misc/Unsafe
         // 0e: invokevirtual java/lang/Class.getDeclaredFields ()[Ljava/lang/reflect/Field;
         // 11: invokestatic kotlin/jvm/internal/ArrayIteratorKt.iterator ([Ljava/lang/Object;)Ljava/util/Iterator;
         // 14: astore 2
         // 15: aload 2
         // 16: invokeinterface java/util/Iterator.hasNext ()Z 1
         // 1b: ifeq 61
         // 1e: aload 2
         // 1f: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
         // 24: checkcast java/lang/reflect/Field
         // 27: astore 3
         // 28: aload 3
         // 29: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
         // 2c: ldc sun/misc/Unsafe
         // 2e: invokestatic kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
         // 31: ifeq 15
         // 34: aload 3
         // 35: bipush 1
         // 36: invokevirtual java/lang/reflect/Field.setAccessible (Z)V
         // 39: nop
         // 3a: aload 3
         // 3b: aconst_null
         // 3c: invokevirtual java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
         // 3f: dup
         // 40: ldc "null cannot be cast to non-null type sun.misc.Unsafe"
         // 42: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
         // 45: checkcast sun/misc/Unsafe
         // 48: astore 4
         // 4a: goto 5e
         // 4d: astore 5
         // 4f: new java/lang/IllegalStateException
         // 52: dup
         // 53: ldc "Failed to access Unsafe member on Unsafe class"
         // 55: aload 5
         // 57: checkcast java/lang/Throwable
         // 5a: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
         // 5d: athrow
         // 5e: aload 4
         // 60: areturn
         // 61: new java/lang/IllegalStateException
         // 64: dup
         // 65: getstatic kotlin/jvm/internal/StringCompanionObject.INSTANCE Lkotlin/jvm/internal/StringCompanionObject;
         // 68: pop
         // 69: ldc sun/misc/Unsafe
         // 6b: invokevirtual java/lang/Class.getDeclaredFields ()[Ljava/lang/reflect/Field;
         // 6e: astore 3
         // 6f: aload 3
         // 70: invokestatic kotlin/collections/ArraysKt.contentDeepToString ([Ljava/lang/Object;)Ljava/lang/String;
         // 73: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Failed to find Unsafe member on Unsafe class, have: \u0001" ]
         // 78: astore 3
         // 79: bipush 0
         // 7a: anewarray 4
         // 7d: astore 4
         // 7f: aload 3
         // 80: aload 4
         // 82: aload 4
         // 84: arraylength
         // 85: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
         // 88: invokestatic java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
         // 8b: dup
         // 8c: ldc "format(...)"
         // 8e: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
         // 91: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
         // 94: athrow
      }

}
