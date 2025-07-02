package cn.sast.framework.incremental

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.util.SootUtilsKt
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.function.Consumer
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.patch.FileHeader
import soot.Scene
import soot.SootClass

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,395:1\n1317#2,2:396\n1#3:398\n1557#4:399\n1628#4,3:400\n381#5,7:403\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles\n*L\n264#1:396,2\n299#1:399\n299#1:400,3\n337#1:403,7\n*E\n"])
public class IncrementalAnalyzeImplByChangeFiles(mainConfig: MainConfig,
      mappingDiffInArchive: Boolean = true,
      factory: ModifyInfoFactory = (new ModifyInfoFactoryImpl()) as ModifyInfoFactory,
      simpleDeclAnalysisDependsGraph: SimpleDeclAnalysisDependsGraph = factory.createSimpleDeclAnalysisDependsGraph(),
      interProceduralAnalysisDependsGraph: InterProceduralAnalysisDependsGraph = factory.createInterProceduralAnalysisDependsGraph()
   ) :
   IncrementalAnalyzeByChangeFiles {
   private final val mainConfig: MainConfig
   private final val mappingDiffInArchive: Boolean
   private final val factory: ModifyInfoFactory
   public open val simpleDeclAnalysisDependsGraph: SimpleDeclAnalysisDependsGraph
   public open val interProceduralAnalysisDependsGraph: InterProceduralAnalysisDependsGraph
   private final val modifyFiles: LinkedHashSet<String>
   private final val oldPath2Header: LinkedHashMap<String, DiffEntry>
   private final val newPath2Header: LinkedHashMap<String, FileHeader>
   private final val pathsInPatch: LinkedHashSet<String>
   private final val name2Path: MutableMap<String, MutableSet<String>>
   private final var ignoreCase: Boolean

   init {
      this.mainConfig = mainConfig;
      this.mappingDiffInArchive = mappingDiffInArchive;
      this.factory = factory;
      this.simpleDeclAnalysisDependsGraph = simpleDeclAnalysisDependsGraph;
      this.interProceduralAnalysisDependsGraph = interProceduralAnalysisDependsGraph;
      this.modifyFiles = new LinkedHashSet<>();
      this.oldPath2Header = new LinkedHashMap<>();
      this.newPath2Header = new LinkedHashMap<>();
      this.pathsInPatch = new LinkedHashSet<>();
      this.name2Path = new HashMap<>();
   }

   private fun visitChangedDecl(target: Any, diffPath: String, diff: DiffEntry) {
      if (!this.pathsInPatch.contains(diffPath)) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         val changed: java.util.Collection = this.factory.getPatchedDeclsByDiff(target, diff);
         this.getSimpleDeclAnalysisDependsGraph().visitChangedDecl(diffPath, changed);
         this.getInterProceduralAnalysisDependsGraph().visitChangedDecl(diffPath, changed);
      }
   }

   public fun update(scene: Scene, locator: ProjectFileLocator?) {
      var var10000: java.util.Iterator = scene.getClasses().snapshotIterator();
      var var3: java.util.Iterator = var10000;

      while (var3.hasNext()) {
         val p: SootClass = var3.next() as SootClass;
         val `$this$forEach$iv`: Pair = this.getChangeTypeOfClass(p);
         val `$i$f$forEach`: DiffEntry = `$this$forEach$iv`.component1() as DiffEntry;
         val var7: DiffEntry = `$this$forEach$iv`.component2() as DiffEntry;
         if (`$i$f$forEach` != null) {
            val var10002: java.lang.String = `$i$f$forEach`.getOldPath();
            this.visitChangedDecl(p, var10002, `$i$f$forEach`);
         }

         if (var7 != null) {
            val var23: java.lang.String = var7.getNewPath();
            this.visitChangedDecl(p, var23, var7);
         }
      }

      if (locator != null) {
         var10000 = this.pathsInPatch.iterator();
         var3 = var10000;

         while (var3.hasNext()) {
            var10000 = (java.util.Iterator)var3.next();
            val var15: java.lang.String = var10000 as java.lang.String;

            val var17: Sequence;
            for (Object element$iv : var17) {
               val it: IResFile = `element$iv` as IResFile;
               if (this.mappingDiffInArchive || !MainConfigKt.skipResourceInArchive(this.mainConfig, `element$iv` as IResFile)) {
                  val oldx: DiffEntry = this.oldPath2Header.get(var15);
                  val newx: FileHeader = this.newPath2Header.get(var15);
                  if (oldx != null) {
                     if (!(oldx.getOldPath() == var15)) {
                        throw new IllegalStateException("Check failed.".toString());
                     }

                     this.visitChangedDecl(it, var15, oldx);
                  }

                  if (newx != null) {
                     if (!(newx.getNewPath() == var15)) {
                        throw new IllegalStateException("Check failed.".toString());
                     }

                     this.visitChangedDecl(it, var15, newx as DiffEntry);
                  }
               }
            }
         }
      }
   }

   private fun getChangeType(possibleSourceFiles: Collection<String>, mode: CompareMode): Pair<DiffEntry?, DiffEntry?> {
      val indexer: <unrepresentable> = new AbstractFileIndexer<java.lang.String>(this) {
         {
            this.this$0 = `$receiver`;
         }

         public java.util.List<java.lang.String> getNames(java.lang.String path, AbstractFileIndexer.CompareMode mode) {
            return StringsKt.split$default(StringsKt.removeSuffix(path, "/"), new java.lang.String[]{"/"}, false, 0, 6, null);
         }

         @Override
         public java.util.Collection<java.lang.String> getPathsByName(java.lang.String name) {
            val var10000: java.util.Set = IncrementalAnalyzeImplByChangeFiles.access$getName2Path$p(this.this$0).get(name) as java.util.Set;
            return (java.util.Collection<java.lang.String>)(if (var10000 != null) var10000 else CollectionsKt.emptyList());
         }
      };
      val var24: java.util.Collection;
      if (this.ignoreCase) {
         val it: java.lang.Iterable = possibleSourceFiles;
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(possibleSourceFiles, 10));

         for (Object item$iv$iv : $this$map$iv) {
            val itx: java.lang.String = `item$iv$iv` as java.lang.String;
            val var10000: Locale = Locale.getDefault();
            val var23: java.lang.String = itx.toLowerCase(var10000);
            `destination$iv$iv`.add(var23);
         }

         var24 = `destination$iv$iv` as java.util.List;
      } else {
         var24 = possibleSourceFiles;
      }

      val var19: java.util.Iterator = indexer.findFiles(var24, mode).iterator();

      while (true) {
         if (var19.hasNext()) {
            val var20: java.lang.String = var19.next() as java.lang.String;
            val var21: Pair = TuplesKt.to(this.oldPath2Header.get(var20), this.newPath2Header.get(var20));
            if (var21 == null) {
               continue;
            }

            var25 = var21;
            break;
         }

         var25 = null;
         break;
      }

      if (var25 == null) {
         var25 = new Pair(null, null);
      }

      return var25;
   }

   public override fun getChangeTypeOfClass(cls: SootClass): Pair<DiffEntry?, DiffEntry?> {
      return this.getChangeType(SootUtilsKt.getPossibleSourceFiles(cls), AbstractFileIndexer.Companion.getDefaultClassCompareMode());
   }

   public override fun getChangeTypeOfFile(file: String): Pair<DiffEntry?, DiffEntry?> {
      return this.getChangeType(CollectionsKt.listOf(file), AbstractFileIndexer.CompareMode.Path);
   }

   public override fun parseIncrementBaseFile(base: IResource) {
      if (!base.getExists()) {
         throw new IllegalStateException(("The incremental base file: `$base` not exists").toString());
      } else if (!base.isFile()) {
         throw new IllegalStateException(("The incremental base file: `$base` is not a file").toString());
      } else {
         val file: IResFile = base.toFile();
         val var3: java.lang.String = file.getExtension();
         switch (var3.hashCode()) {
            case 115312:
               if (!var3.equals("txt")) {
                  throw new IllegalStateException(
                     ("The incremental base file: `$base` with a unsupported extension. Now only support .diff/.patch/.txt").toString()
                  );
               }

               this.parseChangeFiles(file);
               return;
            case 3083269:
               if (!var3.equals("diff")) {
                  throw new IllegalStateException(
                     ("The incremental base file: `$base` with a unsupported extension. Now only support .diff/.patch/.txt").toString()
                  );
               }
               break;
            case 106438728:
               if (!var3.equals("patch")) {
                  throw new IllegalStateException(
                     ("The incremental base file: `$base` with a unsupported extension. Now only support .diff/.patch/.txt").toString()
                  );
               }
               break;
            default:
               throw new IllegalStateException(
                  ("The incremental base file: `$base` with a unsupported extension. Now only support .diff/.patch/.txt").toString()
               );
         }

         this.parseGitDiff(file);
      }
   }

   private fun addOnePath(p: String) {
      if (StringsKt.indexOf$default(p, "\\", 0, false, 6, null) != -1) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         this.pathsInPatch.add(p);
         val name: java.lang.String = StringsKt.substringAfterLast$default(StringsKt.removeSuffix(p, "/"), "/", null, 2, null);
         val `$this$getOrPut$iv`: java.util.Map = this.name2Path;
         val `value$iv`: Any = this.name2Path.get(name);
         val var10000: Any;
         if (`value$iv` == null) {
            val var8: Any = new LinkedHashSet();
            `$this$getOrPut$iv`.put(name, var8);
            var10000 = var8;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.Set).add(p);
      }
   }

   private fun normalizePath(p: String): String {
      val it: java.lang.String = StringsKt.removeSuffix(
         StringsKt.removePrefix(StringsKt.replace$default(StringsKt.replace$default(p, "\\", "/", false, 4, null), "//", "/", false, 4, null), "/"), "\r"
      );
      val var5: java.lang.String;
      if (this.ignoreCase) {
         val var10000: Locale = Locale.getDefault();
         var5 = it.toLowerCase(var10000);
      } else {
         var5 = it;
      }

      return var5;
   }

   @Throws(java/io/IOException::class)
   private fun parseChangeFiles(diffFilePath: IResFile) {
      label24: {
         val var10002: Path = diffFilePath.getPath();
         val var10003: Array<OpenOption> = new OpenOption[0];
         val var14: InputStream = Files.newInputStream(var10002, Arrays.copyOf(var10003, var10003.length));
         val reader: Reader = new InputStreamReader(var14, Charsets.UTF_8);
         val var2: Closeable = if (reader is BufferedReader) reader as BufferedReader else new BufferedReader(reader, 8192);
         var var3: java.lang.Throwable = null;

         try {
            try {
               (var2 as BufferedReader).lines().forEach(new Consumer(IncrementalAnalyzeImplByChangeFiles::parseChangeFiles$lambda$7$lambda$6) {
                  {
                     this.function = function;
                  }
               });
            } catch (var7: java.lang.Throwable) {
               var3 = var7;
               throw var7;
            }
         } catch (var8: java.lang.Throwable) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @Throws(java/io/IOException::class)
   private fun parseGitDiff(diffFilePath: IResFile) {
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
      // 000: new org/eclipse/jgit/patch/Patch
      // 003: dup
      // 004: invokespecial org/eclipse/jgit/patch/Patch.<init> ()V
      // 007: astore 2
      // 008: aload 1
      // 009: invokeinterface cn/sast/common/IResFile.getPath ()Ljava/nio/file/Path; 1
      // 00e: bipush 0
      // 00f: anewarray 486
      // 012: dup
      // 013: arraylength
      // 014: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 017: checkcast [Ljava/nio/file/OpenOption;
      // 01a: invokestatic java/nio/file/Files.newInputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
      // 01d: dup
      // 01e: ldc_w "newInputStream(...)"
      // 021: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 024: checkcast java/io/Closeable
      // 027: astore 3
      // 028: aconst_null
      // 029: astore 4
      // 02b: nop
      // 02c: aload 3
      // 02d: checkcast java/io/InputStream
      // 030: astore 5
      // 032: bipush 0
      // 033: istore 6
      // 035: aload 2
      // 036: aload 5
      // 038: invokevirtual org/eclipse/jgit/patch/Patch.parse (Ljava/io/InputStream;)V
      // 03b: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 03e: astore 5
      // 040: aload 3
      // 041: aload 4
      // 043: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 046: goto 05d
      // 049: astore 5
      // 04b: aload 5
      // 04d: astore 4
      // 04f: aload 5
      // 051: athrow
      // 052: astore 5
      // 054: aload 3
      // 055: aload 4
      // 057: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 05a: aload 5
      // 05c: athrow
      // 05d: aload 2
      // 05e: invokevirtual org/eclipse/jgit/patch/Patch.getFiles ()Ljava/util/List;
      // 061: invokeinterface java/util/List.isEmpty ()Z 1
      // 066: ifeq 078
      // 069: getstatic cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.logger Lmu/KLogger;
      // 06c: aload 1
      // 06d: invokedynamic invoke (Lcn/sast/common/IResFile;)Lkotlin/jvm/functions/Function0; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ ()Ljava/lang/Object;, cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.parseGitDiff$lambda$9 (Lcn/sast/common/IResFile;)Ljava/lang/Object;, ()Ljava/lang/Object; ]
      // 072: invokeinterface mu/KLogger.error (Lkotlin/jvm/functions/Function0;)V 2
      // 077: return
      // 078: aload 2
      // 079: invokevirtual org/eclipse/jgit/patch/Patch.getFiles ()Ljava/util/List;
      // 07c: invokeinterface java/util/List.iterator ()Ljava/util/Iterator; 1
      // 081: astore 3
      // 082: aload 3
      // 083: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 088: ifeq 125
      // 08b: aload 3
      // 08c: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 091: checkcast org/eclipse/jgit/patch/FileHeader
      // 094: astore 4
      // 096: aload 4
      // 098: invokevirtual org/eclipse/jgit/patch/FileHeader.getOldPath ()Ljava/lang/String;
      // 09b: dup
      // 09c: ifnull 0da
      // 09f: astore 7
      // 0a1: bipush 0
      // 0a2: istore 8
      // 0a4: aload 0
      // 0a5: aload 7
      // 0a7: invokespecial cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.normalizePath (Ljava/lang/String;)Ljava/lang/String;
      // 0aa: astore 9
      // 0ac: aload 0
      // 0ad: getfield cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.oldPath2Header Ljava/util/LinkedHashMap;
      // 0b0: checkcast java/util/Map
      // 0b3: astore 10
      // 0b5: aload 9
      // 0b7: aload 4
      // 0b9: invokestatic kotlin/TuplesKt.to (Ljava/lang/Object;Ljava/lang/Object;)Lkotlin/Pair;
      // 0bc: astore 11
      // 0be: aload 10
      // 0c0: aload 11
      // 0c2: invokevirtual kotlin/Pair.getFirst ()Ljava/lang/Object;
      // 0c5: aload 11
      // 0c7: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
      // 0ca: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 0cf: pop
      // 0d0: aload 0
      // 0d1: aload 9
      // 0d3: invokespecial cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.addOnePath (Ljava/lang/String;)V
      // 0d6: nop
      // 0d7: goto 0dc
      // 0da: pop
      // 0db: nop
      // 0dc: aload 4
      // 0de: invokevirtual org/eclipse/jgit/patch/FileHeader.getNewPath ()Ljava/lang/String;
      // 0e1: dup
      // 0e2: ifnull 120
      // 0e5: astore 7
      // 0e7: bipush 0
      // 0e8: istore 8
      // 0ea: aload 0
      // 0eb: aload 7
      // 0ed: invokespecial cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.normalizePath (Ljava/lang/String;)Ljava/lang/String;
      // 0f0: astore 9
      // 0f2: aload 0
      // 0f3: getfield cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.newPath2Header Ljava/util/LinkedHashMap;
      // 0f6: checkcast java/util/Map
      // 0f9: astore 10
      // 0fb: aload 9
      // 0fd: aload 4
      // 0ff: invokestatic kotlin/TuplesKt.to (Ljava/lang/Object;Ljava/lang/Object;)Lkotlin/Pair;
      // 102: astore 11
      // 104: aload 10
      // 106: aload 11
      // 108: invokevirtual kotlin/Pair.getFirst ()Ljava/lang/Object;
      // 10b: aload 11
      // 10d: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
      // 110: invokeinterface java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; 3
      // 115: pop
      // 116: aload 0
      // 117: aload 9
      // 119: invokespecial cn/sast/framework/incremental/IncrementalAnalyzeImplByChangeFiles.addOnePath (Ljava/lang/String;)V
      // 11c: nop
      // 11d: goto 082
      // 120: pop
      // 121: nop
      // 122: goto 082
      // 125: return
   }

   @JvmStatic
   fun `parseChangeFiles$lambda$7$lambda$6`(`this$0`: IncrementalAnalyzeImplByChangeFiles, ln: java.lang.String): Unit {
      val file: java.lang.String = `this$0`.normalizePath(StringsKt.removeSuffix(ln, "\n"));
      if (file.length() == 0) {
         return Unit.INSTANCE;
      } else {
         `this$0`.modifyFiles.add(file);
         `this$0`.addOnePath(file);
         val var3: java.util.Map = `this$0`.oldPath2Header;
         val var4: Pair = TuplesKt.to(file, new DiffEntry(file) {
            {
               this.oldPath = `$file`;
            }
         });
         var3.put(var4.getFirst(), var4.getSecond());
         return Unit.INSTANCE;
      }
   }

   @JvmStatic
   fun `parseGitDiff$lambda$9`(`$diffFilePath`: IResFile): Any {
      return "The patch file $`$diffFilePath` has no any change files";
   }

   @JvmStatic
   fun `logger$lambda$12`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
