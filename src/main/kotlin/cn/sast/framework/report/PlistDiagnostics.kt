package cn.sast.framework.report

import cn.sast.api.config.MainConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.BugPathPosition
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.IReportHashCalculator
import cn.sast.api.report.Report
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.ResourceKt
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.metadata.AnalysisMetadata
import cn.sast.framework.result.OutputType
import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSNumber
import com.dd.plist.NSObject
import com.dd.plist.NSString
import com.dd.plist.XMLPropertyListWriter
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.analysis.SootLineToMethodMapFactory
import com.feysh.corax.cache.analysis.SootMethodAndRange
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import com.github.javaparser.Range
import com.github.javaparser.ast.body.BodyDeclaration
import java.io.IOException
import java.nio.file.attribute.FileTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.Locale
import java.util.Optional
import java.util.Map.Entry
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.SootClass
import soot.SootMethod

public class PlistDiagnostics(mainConfig: MainConfig, info: SootInfoCache?, outputDir: IResDirectory) : ReportConsumer(OutputType.PLIST, outputDir),
   IFileReportConsumer,
   IMetadataVisitor {
   public final val mainConfig: MainConfig
   public final val info: SootInfoCache?

   public open val metadata: MetaData
      public open get() {
         return new ReportConsumer.MetaData("CoraxJava plist report", "1.0", "CoraxJava");
      }


   public final val hashCalculator: IReportHashCalculator

   init {
      this.mainConfig = mainConfig;
      this.info = info;
      this.hashCalculator = new IReportHashCalculator(this) {
         {
            this.this$0 = `$receiver`;
         }

         @Override
         public java.lang.String from(SootClass clazz) {
            val var10000: java.lang.String = clazz.getName();
            return var10000;
         }

         @Override
         public java.lang.String from(SootMethod method) {
            val var10000: java.lang.String = method.getSignature();
            return var10000;
         }

         @Override
         public java.lang.String fromAbsPath(IResource absolutePath) {
            return if (this.this$0.getMainConfig().getHashAbspathInPlist())
               absolutePath.toString()
               else
               this.this$0.getMainConfig().tryGetRelativePathFromAbsolutePath(absolutePath).getRelativePath();
         }

         @Override
         public java.lang.String fromPath(IResource path) {
            return IReportHashCalculator.DefaultImpls.fromPath(this, path);
         }
      };
   }

   public override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
      var `$continuation`: Continuation;
      label37: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label37;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.flush(null, null, null, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var12: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var fullPath: IResource;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            fullPath = this.getOutputDir().resolve(this.getReportFileName(filename));
            val var10000: NSDictionary = new PlistDiagnostics.PlistDiagnosticImpl(this, this.getMetadata(), locator).getRoot(reports);
            if (var10000 == null) {
               return Unit.INSTANCE;
            }

            val it: NSDictionary = var10000;
            val var15: CoroutineContext = Dispatchers.getIO() as CoroutineContext;
            val var10001: Function2 = (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(it, fullPath, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.$it = `$it`;
                  this.$fullPath = `$fullPath`;
               }

               public final Object invokeSuspend(Object $result) {
                  IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        XMLPropertyListWriter.write(this.$it as NSObject, this.$fullPath.getPath());
                        return Unit.INSTANCE;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  return (new <anonymous constructor>(this.$it, this.$fullPath, `$completion`)) as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }) as Function2;
            `$continuation`.L$0 = fullPath;
            `$continuation`.label = 1;
            if (BuildersKt.withContext(var15, var10001, `$continuation`) === var12) {
               return var12;
            }
            break;
         case 1:
            fullPath = `$continuation`.L$0 as IResource;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         ;
      } catch (var13: IOException) {
      }

      logger.trace(PlistDiagnostics::flush$lambda$1$lambda$0);
      return Unit.INSTANCE;
   }

   public override fun visit(analysisMetadata: AnalysisMetadata) {
      ResourceKt.writeText$default(this.getOutputDir().resolve("metadata.json").toFile(), analysisMetadata.toJson(), null, 2, null);
   }

   private fun getReportFileName(fileName: String): String {
      val var2: java.lang.String = this.getMetadata().getAnalyzerName();
      val var10001: Locale = Locale.getDefault();
      val var3: java.lang.String = var2.toLowerCase(var10001);
      return "$fileName_$var3.plist";
   }

   public override fun close() {
   }

   @JvmStatic
   fun `flush$lambda$1$lambda$0`(`$fullPath`: IResource): Any {
      return "Create/modify plist file: '$`$fullPath`'";
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val hardcodeModifiedTime: FileTime
      private final val logger: KLogger
   }

   @SourceDebugExtension(["SMAP\nPlistDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PlistDiagnostics.kt\ncn/sast/framework/report/PlistDiagnostics$PlistDiagnosticImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 7 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,237:1\n1#2:238\n1#2:247\n1#2:251\n1#2:264\n1#2:281\n381#3,7:239\n59#4:246\n57#4:248\n1619#5:249\n1863#5:250\n1864#5:252\n1620#5:253\n1611#5,9:254\n1863#5:263\n1864#5:265\n1620#5:266\n1611#5,9:271\n1863#5:280\n1864#5:282\n1620#5:283\n37#6,2:267\n37#6,2:269\n37#6,2:284\n216#7,2:286\n*S KotlinDebug\n*F\n+ 1 PlistDiagnostics.kt\ncn/sast/framework/report/PlistDiagnostics$PlistDiagnosticImpl\n*L\n65#1:247\n138#1:251\n158#1:264\n182#1:281\n54#1:239,7\n65#1:246\n65#1:248\n138#1:249\n138#1:250\n138#1:252\n138#1:253\n158#1:254,9\n158#1:263\n158#1:265\n158#1:266\n182#1:271,9\n182#1:280\n182#1:282\n182#1:283\n173#1:267,2\n174#1:269,2\n186#1:284,2\n188#1:286,2\n*E\n"])
   public inner class PlistDiagnosticImpl(metadata: MetaData, classToFileName: IProjectFileLocator) {
      private final val metadata: MetaData
      private final val classToFileName: IProjectFileLocator
      private final var fileToIndex: MutableMap<String, Int>

      init {
         this.this$0 = `this$0`;
         this.metadata = metadata;
         this.classToFileName = classToFileName;
         this.fileToIndex = new LinkedHashMap<>();
      }

      private fun classToFileIndex(classInfo: IBugResInfo): Int? {
         var var10000: IResFile = this.classToFileName.get(classInfo, EmptyWrapperFileGenerator.INSTANCE);
         val var13: Int;
         if (var10000 != null) {
            val `$this$getOrPut$iv`: java.util.Map = this.fileToIndex;
            val `key$iv`: java.lang.String = var10000.expandRes(this.this$0.getOutputDir()).getAbsolute().getNormalize().getPath().toString();
            val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`);
            if (`value$iv` == null) {
               val var11: Any = this.fileToIndex.size();
               `$this$getOrPut$iv`.put(`key$iv`, var11);
               var10000 = (IResFile)var11;
            } else {
               var10000 = (IResFile)`value$iv`;
            }

            var13 = (var10000 as java.lang.Number).intValue();
         } else {
            var13 = null;
         }

         return var13;
      }

      private fun getLocation(classInfo: IBugResInfo, line: Int, column: Int): NSDictionary? {
         val var10000: Int = this.classToFileIndex(classInfo);
         val var9: NSDictionary;
         if (var10000 != null) {
            val it: Int = var10000.intValue();
            val var6: NSDictionary = new NSDictionary();
            (var6 as java.util.Map).put("line", new NSNumber(line));
            (var6 as java.util.Map).put("col", new NSNumber(column));
            (var6 as java.util.Map).put("file", new NSNumber(it));
            var9 = var6;
         } else {
            var9 = null;
         }

         return var9;
      }

      private fun getRange(classInfo: IBugResInfo, region: Region): NSArray? {
         val var10000: Region = if (region.startLine >= 0) region else null;
         val var16: NSArray;
         if ((if (region.startLine >= 0) region else null) != null) {
            val var14: NSDictionary = this.getLocation(classInfo, var10000.startLine, var10000.startColumn);
            if (var14 != null) {
               val var15: NSDictionary = this.getLocation(classInfo, var10000.getEndLine(), var10000.getEndColumn());
               var16 = if (var15 != null) new NSArray(new NSObject[]{var14, var15}) else null;
            } else {
               var16 = null;
            }
         } else {
            var16 = null;
         }

         return var16;
      }

      private fun getFuncRange(classInfo: IBugResInfo, line: Int): NSArray? {
         if (classInfo is ClassResInfo) {
            val var10000: BodyDeclaration = if (this.this$0.getInfo() != null)
               this.this$0.getInfo().getMemberAtLine((classInfo as ClassResInfo).getSc(), line)
               else
               null;
            if (var10000 != null) {
               label31: {
                  val var11: Optional = var10000.getRange();
                  val var14: Optional = if (var11.isPresent()) var11 else null;
                  if (var14 != null) {
                     val var15: Range = var14.get() as Range;
                     if (var15 != null) {
                        var16 = TuplesKt.to(var15.begin.line, var15.end.line);
                        break label31;
                     }
                  }

                  var16 = null;
               }

               if (var16 != null) {
                  return getFuncRange$toRange(var16, this, classInfo);
               }
            }

            val var10: SootMethodAndRange = SootLineToMethodMapFactory.getSootMethodAtLine$default(
               SootLineToMethodMapFactory.INSTANCE, (classInfo as ClassResInfo).getSc(), line, false, 4, null
            );
            if (var10 != null) {
               val var12: Pair = var10.getRange();
               return getFuncRange$toRange(
                  TuplesKt.to((var12.getFirst() as java.lang.Number).intValue() - 1, (var12.getSecond() as java.lang.Number).intValue() + 1), this, classInfo
               );
            }
         }

         return null;
      }

      private fun getNote(event: BugPathEvent): NSDictionary? {
         var var10000: NSDictionary = this.getLocation(event.getClassname(), event.getRegion().startLine, event.getRegion().startColumn);
         if (var10000 != null) {
            val var4: NSDictionary = new NSDictionary();
            (var4 as java.util.Map).put("kind", new NSString("event"));
            (var4 as java.util.Map).put("location", var10000);
            (var4 as java.util.Map).put("depth", new NSNumber(0));
            (var4 as java.util.Map).put("message", new NSString(event.getMessage().get(Language.EN)));
            (var4 as java.util.Map).put("message_zh", new NSString(event.getMessage().get(Language.ZH)));
            val var19: NSArray = this.getFuncRange(event.getClassname(), event.getRegion().startLine);
            if (var19 != null) {
               (var4 as java.util.Map).put("ranges_of_func_source", new NSArray(new NSObject[]{var19}));
            }

            val var20: NSArray = this.getRange(event.getClassname(), event.getRegion());
            if (var20 != null) {
               (var4 as java.util.Map).put("ranges", new NSArray(new NSObject[]{var20}));
            }

            var10000 = var4;
         } else {
            var10000 = null;
         }

         return var10000;
      }

      private fun getControlEdge(fromClass: IBugResInfo, fromRange: Region, toClass: IBugResInfo, toRange: Region): NSDictionary? {
         var var10000: NSArray = this.getRange(fromClass, fromRange);
         val var24: NSDictionary;
         if (var10000 != null) {
            var10000 = this.getRange(toClass, toRange);
            if (var10000 != null) {
               val var9: NSDictionary = new NSDictionary();
               (var9 as java.util.Map).put("kind", new NSString("control"));
               val var12: java.util.Map = var9 as java.util.Map;
               val var14: Array<NSObject> = new NSObject[1];
               val var15: NSDictionary = new NSDictionary();
               (var15 as java.util.Map).put("start", var10000);
               (var15 as java.util.Map).put("end", var10000);
               var14[0] = var15;
               var12.put("edges", new NSArray(var14));
               var24 = var9;
            } else {
               var24 = null;
            }
         } else {
            var24 = null;
         }

         return var24;
      }

      private fun getBugPathEventRange(event: BugPathEvent): Region {
         return event.getRegion();
      }

      private fun getDiagnostic(hashCalculator: IReportHashCalculator, report: Report): NSDictionary? {
         val notes: java.lang.Iterable = report.getPathEvents();
         val location: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : notes) {
            val var10000: NSDictionary = this.getNote(`$this$forEach$iv$iv$iv` as BugPathEvent);
            if (var10000 != null) {
               location.add(var10000);
            }
         }

         val path: java.util.List = location as java.util.List;
         if (!report.getBug_path_positions().isEmpty()) {
            var var20: Int = 0;

            for (int var23 = report.getBug_path_positions().size() - 1; i < var23; i++) {
               val var27: BugPathPosition = report.getBug_path_positions().get(var20);
               val `$this$mapNotNullTo$iv$iv`: BugPathPosition = report.getBug_path_positions().get(var20 + 1);
               if (var27.getRegion() != null && `$this$mapNotNullTo$iv$iv`.getRegion() != null) {
                  val var10001: IBugResInfo = var27.getClassname();
                  val var10002: Region = var27.getRegion();
                  val var10003: IBugResInfo = `$this$mapNotNullTo$iv$iv`.getClassname();
                  val var10004: Region = `$this$mapNotNullTo$iv$iv`.getRegion();
                  val var60: NSDictionary = this.getControlEdge(var10001, var10002, var10003, var10004);
                  if (var60 != null) {
                     path.add(var60);
                  }
               }
            }
         } else if (report.getPathEvents().size() > 1) {
            var var21: Int = 0;

            for (int var24 = report.getPathEvents().size() - 1; i < var24; i++) {
               val var28: BugPathEvent = report.getPathEvents().get(var21);
               val var30: Region = this.getBugPathEventRange(var28);
               val var32: BugPathEvent = report.getPathEvents().get(var21 + 1);
               val var34: Region = this.getBugPathEventRange(var32);
               if (!(var30 == var34)) {
                  val var61: NSDictionary = this.getControlEdge(var28.getClassname(), var30, var32.getClassname(), var34);
                  if (var61 != null) {
                     path.add(var61);
                  }
               }
            }
         }

         val var25: java.lang.Iterable = report.getNotes();
         val var33: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            val var62: NSDictionary = this.getNote(var53 as BugPathEvent);
            if (var62 != null) {
               var33.add(var62);
            }
         }

         val var22: java.util.List = var33 as java.util.List;
         val var63: NSDictionary = this.getLocation(report.getBugResFile(), report.getRegion().startLine, report.getRegion().startColumn);
         if (var63 == null) {
            return null;
         } else {
            val var31: NSDictionary = new NSDictionary();
            (var31 as java.util.Map).put("location", var63);
            (var31 as java.util.Map)
               .put("issue_hash_content_of_line_in_context", new NSString(report.reportHash(hashCalculator, Report.HashType.DIAGNOSTIC_MESSAGE)));
            (var31 as java.util.Map).put("issue_hash_location_bug_type", new NSString(report.reportHash(hashCalculator, Report.HashType.CONTEXT_FREE)));
            (var31 as java.util.Map).put("check_name", new NSString(report.getCheck_name()));
            (var31 as java.util.Map).put("detector_name", new NSString(report.getDetector_name()));
            val var64: java.util.Map = var31 as java.util.Map;
            var var68: Any = report.getMessage().get(Language.EN);
            var64.put("description", new NSString(var68 as java.lang.String));
            val var65: java.util.Map = var31 as java.util.Map;
            var68 = report.getMessage().get(Language.ZH);
            var65.put("description_zh", new NSString(var68 as java.lang.String));
            var var38: java.util.Map = var31 as java.util.Map;
            val var66: NSString = new NSString;
            var var67: java.lang.String = report.getCategory();
            if (var67 == null) {
               var67 = "unknown";
            }

            var66./* $VF: Unable to resugar constructor */<init>(var67);
            var38.put("category", var66);
            (var31 as java.util.Map).put("type", new NSString(report.getType()));
            var38 = var31 as java.util.Map;
            var var48: Array<NSDictionary> = var22.toArray(new NSDictionary[0]);
            var38.put("notes", new NSArray(Arrays.copyOf(var48, var48.length)));
            var38 = var31 as java.util.Map;
            var48 = path.toArray(new NSDictionary[0]);
            var38.put("path", new NSArray(Arrays.copyOf(var48, var48.length)));
            return var31;
         }
      }

      public fun getRoot(reports: List<Report>): NSDictionary? {
         val var2: NSDictionary = new NSDictionary();
         val var3: PlistDiagnostics = this.this$0;
         var var6: java.util.Map = var2 as java.util.Map;
         val `$this$getRoot_u24lambda_u2435_u24lambda_u2434`: java.lang.Iterable = reports;
         val `$i$f$forEach`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            val it: Report = file as Report;
            val var21: NSDictionary = this.getDiagnostic(var3.getHashCalculator(), file as Report);
            if (var21 == null) {
               PlistDiagnostics.access$getLogger$cp().warn(PlistDiagnostics.PlistDiagnosticImpl::getRoot$lambda$35$lambda$28$lambda$27$lambda$26);
            }

            if (var21 != null) {
               `$i$f$forEach`.add(var21);
            }
         }

         val var32: java.util.Collection = `$i$f$forEach` as java.util.List;
         if ((`$i$f$forEach` as java.util.List).isEmpty()) {
            return null;
         } else {
            val var26: Array<NSDictionary> = var32.toArray(new NSDictionary[0]);
            var6.put("diagnostics", new NSArray(Arrays.copyOf(var26, var26.length)));
            var6 = var2 as java.util.Map;
            val var51: NSArray = new NSArray(this.fileToIndex.size());
            val var33: NSArray = var51;

            for (Entry element$iv : this.fileToIndex.entrySet()) {
               var33.setValue(
                  (`$this$getRoot_u24lambda_u2435_u24lambda_u2434_u24lambda_u2433`.getValue() as java.lang.Number).intValue(),
                  `$this$getRoot_u24lambda_u2435_u24lambda_u2434_u24lambda_u2433`.getKey() as java.lang.String
               );
            }

            var6.put("files", var51);
            var6 = var2 as java.util.Map;
            val var52: NSDictionary = new NSDictionary();
            var var38: java.util.Map = var52 as java.util.Map;
            var var44: NSDictionary = new NSDictionary();
            (var44 as java.util.Map).put("name", new NSString(this.metadata.getToolName()));
            (var44 as java.util.Map).put("version", new NSString(this.metadata.getToolVersion()));
            var38.put("generated_by", var44);
            var38 = var52 as java.util.Map;
            var44 = new NSDictionary();
            (var44 as java.util.Map).put("name", new NSString(this.metadata.getAnalyzerName()));
            var38.put("analyzer", var44);
            var6.put("metadata", var52);
            return var2;
         }
      }

      @JvmStatic
      fun Pair<Integer, Integer>.`getFuncRange$toRange`(`this$0`: PlistDiagnostics.PlistDiagnosticImpl, `$classInfo`: IBugResInfo): NSArray {
         val startLn: Int = (`$this$getFuncRange_u24toRange`.component1() as java.lang.Number).intValue();
         val endLn: Int = (`$this$getFuncRange_u24toRange`.component2() as java.lang.Number).intValue();
         if (startLn >= 0 && endLn >= 0) {
            val var10000: NSDictionary = `this$0`.getLocation(`$classInfo`, startLn, -1);
            val var11: NSArray;
            if (var10000 != null) {
               val var10: NSDictionary = `this$0`.getLocation(`$classInfo`, endLn, -1);
               var11 = if (var10 != null) new NSArray(new NSObject[]{var10000, var10}) else null;
            } else {
               var11 = null;
            }

            return var11;
         } else {
            return null;
         }
      }

      @JvmStatic
      fun `getRoot$lambda$35$lambda$28$lambda$27$lambda$26`(`$it`: Report): Any {
         return "Failed create plist report for: `$`$it``";
      }
   }
}
