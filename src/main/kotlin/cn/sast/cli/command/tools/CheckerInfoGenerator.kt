package cn.sast.cli.command.tools

import cn.sast.api.config.ChapterFlat
import cn.sast.api.config.CheckerInfo
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.CheckerPriorityConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.Tag
import cn.sast.common.IResDirectory
import cn.sast.common.Resource
import cn.sast.framework.plugin.ConfigPluginLoader
import cn.sast.framework.plugin.PluginDefinitions
import cn.sast.framework.report.RuleAndRuleMapping
import cn.sast.framework.report.SQLiteDB
import cn.sast.framework.report.SqliteDiagnostics
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.github.ajalt.mordant.rendering.Theme
import com.github.doyaaaaaken.kotlincsv.client.CsvFileReader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Closeable
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.SortedMap
import java.util.Map.Entry
import java.util.regex.Pattern
import kotlin.enums.EnumEntries
import kotlin.io.path.PathsKt
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.StringCompanionObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.BuiltinSerializersKt
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.LinkedHashMapSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JvmStreamsKt
import mu.KLogger

@SourceDebugExtension(["SMAP\nCheckerInfoGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfoGenerator.kt\ncn/sast/cli/command/tools/CheckerInfoGenerator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 6 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,476:1\n1863#2:477\n1864#2:479\n1202#2,2:480\n1230#2,4:482\n1279#2,2:486\n1293#2,4:488\n1628#2,3:505\n1557#2:515\n1628#2,3:516\n1557#2:519\n1628#2,3:520\n774#2:523\n865#2,2:524\n774#2:526\n865#2,2:527\n1557#2:529\n1628#2,3:530\n1557#2:533\n1628#2,3:534\n1863#2,2:537\n1187#2,2:539\n1261#2,4:541\n1279#2,2:566\n1293#2,4:568\n1279#2,2:572\n1293#2,4:574\n1279#2,2:578\n1293#2,4:580\n1279#2,2:584\n1293#2,4:586\n1611#2,9:593\n1863#2:602\n1864#2:604\n1620#2:605\n1557#2:606\n1628#2,3:607\n1557#2:614\n1628#2,3:615\n1053#2:618\n1628#2,3:619\n1#3:478\n1#3:603\n77#4:492\n97#4,5:493\n126#4:552\n153#4,3:553\n77#4:590\n97#4,2:591\n99#4,3:610\n535#5:498\n520#5,6:499\n535#5:508\n520#5,6:509\n535#5:545\n520#5,6:546\n381#5,7:557\n1317#6:556\n1318#6:564\n1317#6:565\n1318#6:613\n*S KotlinDebug\n*F\n+ 1 CheckerInfoGenerator.kt\ncn/sast/cli/command/tools/CheckerInfoGenerator\n*L\n77#1:477\n77#1:479\n115#1:480,2\n115#1:482,4\n117#1:486,2\n117#1:488,4\n121#1:505,3\n129#1:515\n129#1:516,3\n142#1:519\n142#1:520,3\n285#1:523\n285#1:524,2\n286#1:526\n286#1:527,2\n287#1:529\n287#1:530,3\n288#1:533\n288#1:534,3\n315#1:537,2\n352#1:539,2\n352#1:541,4\n191#1:566,2\n191#1:568,4\n200#1:572,2\n200#1:574,4\n206#1:578,2\n206#1:580,4\n216#1:584,2\n216#1:586,4\n246#1:593,9\n246#1:602\n246#1:604\n246#1:605\n246#1:606\n246#1:607,3\n389#1:614\n389#1:615,3\n389#1:618\n393#1:619,3\n246#1:603\n118#1:492\n118#1:493,5\n439#1:552\n439#1:553,3\n226#1:590\n226#1:591,2\n226#1:610,3\n120#1:498\n120#1:499,6\n122#1:508\n122#1:509,6\n434#1:545\n434#1:546,6\n168#1:557,7\n166#1:556\n166#1:564\n180#1:565\n180#1:613\n*E\n"])
public class CheckerInfoGenerator(language: List<String>, output: Path, checkerInfoResRoot: Path, pluginDefinitions: PluginDefinitions) {
   private final val language: List<String>
   private final val output: Path
   private final val checkerInfoResRoot: Path
   private final val pluginDefinitions: PluginDefinitions

   private final val checkerInfoJsonOutPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("checker_info.json").normalize();
      }


   private final val checkerInfoSqliteDBOutPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("checker_info.db").normalize();
      }


   private final val ruleChaptersYamlOutPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("rule_chapters.yaml").normalize();
      }


   private final val ruleChaptersSortNumberYamlOutPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("rule_chapters_sort_number.yaml").normalize();
      }


   private final val descriptionsPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("descriptions");
      }


   private final val checkerInfoCsvPath: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("checker_info.csv");
      }


   private final val categoryLanguageMapCsv: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("category.translation.csv");
      }


   private final val standardNameMappingJson: Path
      private final get() {
         return this.checkerInfoResRoot.resolve("checker_info_ruleset_to_server_standard_name_mapping.json");
      }


   public final val ruleSortPath: Path
      public final get() {
         val var10000: Path = this.checkerInfoResRoot.resolve("rule_sort.yaml").normalize();
         return var10000;
      }


   private final val errors: MutableList<Any>
   private final val warnings: MutableList<Any>

   init {
      this.language = language;
      this.output = output;
      this.checkerInfoResRoot = checkerInfoResRoot;
      this.pluginDefinitions = pluginDefinitions;
      this.errors = new ArrayList<>();
      this.warnings = new ArrayList<>();
   }

   private fun getSuffixOfLang(lang: String): String {
      return ".$lang";
   }

   private fun getMarkdown(checkerId: String, lang: String): Path {
      val var10000: Path = this.getDescriptionsPath().resolve("$checkerId${this.getSuffixOfLang(lang)}.md");
      return var10000;
   }

   private fun getAbstraction(checkerId: String, lang: String): Path {
      val var10000: Path = this.getDescriptionsPath().resolve("$checkerId${this.getSuffixOfLang(lang)}.txt");
      return var10000;
   }

   private fun dumpExistsCheckerIds(existsCheckerIds: List<String>) {
      label19: {
         val var10000: Path = this.output.resolve("exists-checker-id.json");
         val var3: Array<OpenOption> = new OpenOption[0];
         val var13: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var3, var3.length));
         val var10: Closeable = var13;
         var var11: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.ListSerializer(BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)) as SerializationStrategy,
                  existsCheckerIds,
                  var10 as OutputStream
               );
            } catch (var6: java.lang.Throwable) {
               var11 = var6;
               throw var6;
            }
         } catch (var7: java.lang.Throwable) {
            CloseableKt.closeFinally(var10, var11);
         }

         CloseableKt.closeFinally(var10, null);
      }
   }

   private fun validateBugMessageLanguage(checkTypes: List<CheckType>) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val it: CheckType = `element$iv` as CheckType;
         if (!(`element$iv` as CheckType).getBugMessage().containsKey(Language.EN) || !(`element$iv` as CheckType).getBugMessage().containsKey(Language.ZH)) {
            throw new IllegalStateException(("Missing language ZH or EN: $it").toString());
         }
      }
   }

   private fun getMessage(lang: String, msg: cn.sast.cli.command.tools.CheckerInfoGenerator.HintEnum): String {
      var var10000: java.lang.String;
      switch (CheckerInfoGenerator.WhenMappings.$EnumSwitchMapping$0[msg.ordinal()]) {
         case 1:
            var10000 = if (lang == "zh-CN") "请添加此规则对应的描述或修复建议到" else "Please add a description or remediation suggestion for this rule at";
            break;
         case 2:
            var10000 = if (lang == "zh-CN") "请添加此规则对应的规则的摘要" else "Please add a abstract short-description of the rule";
            break;
         case 3:
            var10000 = if (lang == "zh-CN") "规则的markdown格式的漏洞描述文档" else "Description & Fix Suggestions for Vulnerabilities in Markdown Format";
            break;
         case 4:
            var10000 = if (lang == "zh-CN") "规则的摘要描述" else "Abstract Short-Description of Rule";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   private fun validateDescription(
      lang: String,
      suffix: String,
      existsCheckerIds: List<CheckType>,
      kind: cn.sast.cli.command.tools.CheckerInfoGenerator.HintEnum,
      phantom: cn.sast.cli.command.tools.CheckerInfoGenerator.HintEnum
   ) {
      label140: {
         val markdown: Path = this.getDescriptionsPath();
         val checkTypeToAllPossibleIds: java.lang.Iterable = PathsKt.listDirectoryEntries(markdown, "*$suffix");
         val lack: java.util.Map = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(checkTypeToAllPossibleIds, 10)), 16)
         );

         for (Object element$iv$iv : $this$associateBy$iv) {
            lack.put(PathsKt.getName(`$this$map$iv` as Path), `$this$map$iv`);
         }

         val existsMarkdowns: SortedMap = MapsKt.toSortedMap(lack);
         val var52: java.lang.Iterable = existsCheckerIds;
         val existsCheckerIdFromDescFiles: LinkedHashMap = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(existsCheckerIds, 10)), 16)
         );

         for (Object element$iv$iv : $this$associateWith$iv) {
            existsCheckerIdFromDescFiles.put(var75, CollectionsKt.listOf(CheckerInfoGeneratorKt.getId(var75 as CheckType)));
         }

         val var51: java.util.Map = existsCheckerIdFromDescFiles;
         var `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Entry element$iv$iv : checkTypeToAllPossibleIds.entrySet()) {
            CollectionsKt.addAll(`destination$iv$ivx`, var85.getValue() as java.lang.Iterable);
         }

         val var53: java.util.List = `destination$iv$ivx` as java.util.List;
         val var57: java.util.Map = existsMarkdowns;
         val `destination$iv$ivxx`: java.util.Map = new LinkedHashMap();

         for (Entry element$iv$iv : $this$filter$iv.entrySet()) {
            val var10001: Any = var98.getKey();
            if (!var53.contains(StringsKt.removeSuffix(var10001 as java.lang.String, suffix))) {
               `destination$iv$ivxx`.put(var98.getKey(), var98.getValue());
            }
         }

         val var55: java.util.Set = `destination$iv$ivxx`.keySet();
         val var125: java.util.Set = existsMarkdowns.keySet();
         val var60: java.lang.Iterable = var125;
         `destination$iv$ivx` = new LinkedHashSet();

         for (Object item$iv : var60) {
            val var99: java.lang.String = var87 as java.lang.String;
            `destination$iv$ivx`.add(StringsKt.removeSuffix(var99, suffix));
         }

         val var58: java.util.Set = `destination$iv$ivx` as java.util.Set;
         val `destination$iv$ivxxx`: java.util.Map = new LinkedHashMap();

         val it: Entry;
         for (Entry element$iv$ivx : checkTypeToAllPossibleIds.entrySet()) {
            it = `element$iv$ivx`;
            if (CollectionsKt.intersect(`element$iv$ivx`.getValue() as java.lang.Iterable, var58).isEmpty()) {
               `destination$iv$ivxxx`.put(`element$iv$ivx`.getKey(), `element$iv$ivx`.getValue());
            }
         }

         val var61: java.util.Set = CollectionsKt.toSet(`destination$iv$ivxxx`.keySet());
         val var71: Path = this.output.resolve("descriptions");
         var var132: Array<LinkOption> = new LinkOption[0];
         if (!Files.exists(var71, Arrays.copyOf(var132, var132.length))) {
            Files.createDirectories(var71);
         }

         val var65: Path = var71;
         val var126: Path = var71.resolve("redundancy_md.json");
         val var79: Array<OpenOption> = new OpenOption[0];
         val var127: OutputStream = Files.newOutputStream(var126, Arrays.copyOf(var79, var79.length));
         val var73: Closeable = var127;
         var var80: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.SetSerializer(BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)) as SerializationStrategy,
                  var55,
                  var73 as OutputStream
               );
            } catch (var31: java.lang.Throwable) {
               var80 = var31;
               throw var31;
            }
         } catch (var32: java.lang.Throwable) {
            CloseableKt.closeFinally(var73, var80);
         }

         CloseableKt.closeFinally(var73, null);

         val `destination$iv$ivxxxx`: java.util.Collection;
         while (it.hasNext()) {
            `destination$iv$ivxxxx`.add("${CheckerInfoGeneratorKt.getId(it.next() as CheckType)}$suffix");
         }

         val var74: java.util.List = `destination$iv$ivxxxx` as java.util.List;
         val var128: Path = var71.resolve("lack_md.json");
         val var92: Array<OpenOption> = new OpenOption[0];
         val var129: OutputStream = Files.newOutputStream(var128, Arrays.copyOf(var92, var92.length));
         val var82: Closeable = var129;
         var var93: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.ListSerializer(BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)) as SerializationStrategy,
                  var74,
                  var82 as OutputStream
               );
            } catch (var29: java.lang.Throwable) {
               var93 = var29;
               throw var29;
            }
         } catch (var30: java.lang.Throwable) {
            CloseableKt.closeFinally(var82, var93);
         }

         CloseableKt.closeFinally(var82, null);

         while (var82.hasNext()) {
            val var94: java.lang.String = var82.next() as java.lang.String;
            val var130: Path = this.getDescriptionsPath().resolve(var94);
            val var131: Path = var130.toAbsolutePath();
            val var104: Path = var131.normalize();
            this.errors.add("Required: The necessary file for \"${this.getMessage(lang, kind)}\" do not exist: $var104");
            val var108: Path = var65.resolve(var94);
            var132 = new LinkOption[0];
            if (!Files.exists(var108, Arrays.copyOf(var132, var132.length))) {
               val var113: Charset = Charsets.UTF_8;
               val var117: Array<OpenOption> = new OpenOption[0];
               val var111: Closeable = new OutputStreamWriter(Files.newOutputStream(var108, Arrays.copyOf(var117, var117.length)), var113);
               var var114: java.lang.Throwable = null;

               try {
                  try {
                     (var111 as OutputStreamWriter).write("${this.getMessage(lang, phantom)}\n\nRequired: $var104\n");
                  } catch (var27: java.lang.Throwable) {
                     var114 = var27;
                     throw var27;
                  }
               } catch (var28: java.lang.Throwable) {
                  CloseableKt.closeFinally(var111, var114);
               }

               CloseableKt.closeFinally(var111, null);
            }
         }

         val var83: java.lang.Iterable = var55;
         val var124: java.util.List = this.warnings;
         `destination$iv$ivxxxx` = new ArrayList(CollectionsKt.collectionSizeOrDefault(var83, 10));

         for (Object item$iv$iv : var83) {
            `destination$iv$ivxxxx`.add("Redundant \"${this.getMessage(lang, kind)}\": ${var120 as java.lang.String}");
         }

         var124.addAll(`destination$iv$ivxxxx` as java.util.List);
      }
   }

   private fun isValid(row: Map<String, String>, key: String): Boolean {
      val it: java.lang.String = row.get(key) as java.lang.String;
      return it != null && it.length() != 0 && StringsKt.trim(it).toString().length() > 0;
   }

   private fun getValueFromRow(file: Any, row: Map<String, String>, key: String): String {
      if (!this.isValid(row, key)) {
         throw new IllegalStateException(("failed to get a value of attribute '$key' in $row at $file").toString());
      } else {
         val var10000: Any = row.get(key);
         return var10000 as java.lang.String;
      }
   }

   private fun getValueFromRowOrNull(file: Any, row: Map<String, String>, key: String, defaultValue: (() -> String?)? = null): String? {
      if (!this.isValid(row, key)) {
         if (defaultValue != null) {
            return defaultValue.invoke() as java.lang.String;
         } else {
            this.warnings.add("failed to get a value of attribute '$key' in $row at $file");
            return null;
         }
      } else {
         val var10000: Any = row.get(key);
         return var10000 as java.lang.String;
      }
   }

   public fun getCheckerInfoList(existsCheckerIds: LinkedHashSet<String>): Pair<LinkedHashSet<CheckerInfo>, LinkedHashSet<String>> {
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
      // 001: ldc_w "existsCheckerIds"
      // 004: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 007: new java/util/LinkedHashMap
      // 00a: dup
      // 00b: invokespecial java/util/LinkedHashMap.<init> ()V
      // 00e: checkcast java/util/Map
      // 011: astore 2
      // 012: aconst_null
      // 013: bipush 1
      // 014: aconst_null
      // 015: invokestatic com/github/doyaaaaaken/kotlincsv/dsl/CsvReaderDslKt.csvReader$default (Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)Lcom/github/doyaaaaaken/kotlincsv/client/CsvReader;
      // 018: aload 0
      // 019: invokespecial cn/sast/cli/command/tools/CheckerInfoGenerator.getCategoryLanguageMapCsv ()Ljava/nio/file/Path;
      // 01c: invokeinterface java/nio/file/Path.toFile ()Ljava/io/File; 1
      // 021: dup
      // 022: ldc_w "toFile(...)"
      // 025: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 028: aload 0
      // 029: aload 2
      // 02a: invokedynamic invoke (Lcn/sast/cli/command/tools/CheckerInfoGenerator;Ljava/util/Map;)Lkotlin/jvm/functions/Function1; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, cn/sast/cli/command/tools/CheckerInfoGenerator.getCheckerInfoList$lambda$18 (Lcn/sast/cli/command/tools/CheckerInfoGenerator;Ljava/util/Map;Lcom/github/doyaaaaaken/kotlincsv/client/CsvFileReader;)Lkotlin/Unit;, (Lcom/github/doyaaaaaken/kotlincsv/client/CsvFileReader;)Lkotlin/Unit; ]
      // 02f: invokevirtual com/github/doyaaaaaken/kotlincsv/client/CsvReader.open (Ljava/io/File;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;
      // 032: pop
      // 033: aload 0
      // 034: invokespecial cn/sast/cli/command/tools/CheckerInfoGenerator.getStandardNameMappingJson ()Ljava/nio/file/Path;
      // 037: dup
      // 038: ldc_w "<get-standardNameMappingJson>(...)"
      // 03b: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 03e: astore 4
      // 040: bipush 0
      // 041: anewarray 134
      // 044: astore 5
      // 046: aload 4
      // 048: aload 5
      // 04a: aload 5
      // 04c: arraylength
      // 04d: invokestatic java/util/Arrays.copyOf ([Ljava/lang/Object;I)[Ljava/lang/Object;
      // 050: checkcast [Ljava/nio/file/OpenOption;
      // 053: invokestatic java/nio/file/Files.newInputStream (Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
      // 056: dup
      // 057: ldc_w "newInputStream(...)"
      // 05a: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 05d: checkcast java/io/Closeable
      // 060: astore 4
      // 062: aconst_null
      // 063: astore 5
      // 065: nop
      // 066: aload 4
      // 068: checkcast java/io/InputStream
      // 06b: astore 6
      // 06d: bipush 0
      // 06e: istore 7
      // 070: getstatic cn/sast/cli/command/tools/CheckerInfoGenerator.jsonFormat Lkotlinx/serialization/json/Json;
      // 073: new kotlinx/serialization/internal/LinkedHashMapSerializer
      // 076: dup
      // 077: getstatic kotlinx/serialization/internal/StringSerializer.INSTANCE Lkotlinx/serialization/internal/StringSerializer;
      // 07a: getstatic kotlinx/serialization/internal/StringSerializer.INSTANCE Lkotlinx/serialization/internal/StringSerializer;
      // 07d: invokespecial kotlinx/serialization/internal/LinkedHashMapSerializer.<init> (Lkotlinx/serialization/KSerializer;Lkotlinx/serialization/KSerializer;)V
      // 080: checkcast kotlinx/serialization/DeserializationStrategy
      // 083: aload 6
      // 085: invokestatic kotlinx/serialization/json/JvmStreamsKt.decodeFromStream (Lkotlinx/serialization/json/Json;Lkotlinx/serialization/DeserializationStrategy;Ljava/io/InputStream;)Ljava/lang/Object;
      // 088: checkcast java/util/Map
      // 08b: astore 6
      // 08d: aload 4
      // 08f: aload 5
      // 091: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 094: aload 6
      // 096: goto 0ae
      // 099: astore 6
      // 09b: aload 6
      // 09d: astore 5
      // 09f: aload 6
      // 0a1: athrow
      // 0a2: astore 6
      // 0a4: aload 4
      // 0a6: aload 5
      // 0a8: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 0ab: aload 6
      // 0ad: athrow
      // 0ae: astore 3
      // 0af: new java/util/LinkedHashSet
      // 0b2: dup
      // 0b3: invokespecial java/util/LinkedHashSet.<init> ()V
      // 0b6: astore 4
      // 0b8: new java/util/LinkedHashSet
      // 0bb: dup
      // 0bc: invokespecial java/util/LinkedHashSet.<init> ()V
      // 0bf: astore 5
      // 0c1: new java/util/LinkedHashSet
      // 0c4: dup
      // 0c5: invokespecial java/util/LinkedHashSet.<init> ()V
      // 0c8: checkcast java/util/Set
      // 0cb: astore 6
      // 0cd: aconst_null
      // 0ce: bipush 1
      // 0cf: aconst_null
      // 0d0: invokestatic com/github/doyaaaaaken/kotlincsv/dsl/CsvReaderDslKt.csvReader$default (Lkotlin/jvm/functions/Function1;ILjava/lang/Object;)Lcom/github/doyaaaaaken/kotlincsv/client/CsvReader;
      // 0d3: aload 0
      // 0d4: invokespecial cn/sast/cli/command/tools/CheckerInfoGenerator.getCheckerInfoCsvPath ()Ljava/nio/file/Path;
      // 0d7: invokeinterface java/nio/file/Path.toFile ()Ljava/io/File; 1
      // 0dc: dup
      // 0dd: ldc_w "toFile(...)"
      // 0e0: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 0e3: aload 6
      // 0e5: aload 0
      // 0e6: aload 4
      // 0e8: aload 1
      // 0e9: aload 3
      // 0ea: aload 5
      // 0ec: aload 2
      // 0ed: invokedynamic invoke (Ljava/util/Set;Lcn/sast/cli/command/tools/CheckerInfoGenerator;Ljava/util/LinkedHashSet;Ljava/util/LinkedHashSet;Ljava/util/Map;Ljava/util/LinkedHashSet;Ljava/util/Map;)Lkotlin/jvm/functions/Function1; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Ljava/lang/Object;, cn/sast/cli/command/tools/CheckerInfoGenerator.getCheckerInfoList$lambda$33 (Ljava/util/Set;Lcn/sast/cli/command/tools/CheckerInfoGenerator;Ljava/util/LinkedHashSet;Ljava/util/LinkedHashSet;Ljava/util/Map;Ljava/util/LinkedHashSet;Ljava/util/Map;Lcom/github/doyaaaaaken/kotlincsv/client/CsvFileReader;)Lkotlin/Unit;, (Lcom/github/doyaaaaaken/kotlincsv/client/CsvFileReader;)Lkotlin/Unit; ]
      // 0f2: invokevirtual com/github/doyaaaaaken/kotlincsv/client/CsvReader.open (Ljava/io/File;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;
      // 0f5: pop
      // 0f6: aload 3
      // 0f7: invokeinterface java/util/Map.keySet ()Ljava/util/Set; 1
      // 0fc: aload 6
      // 0fe: checkcast java/lang/Iterable
      // 101: invokestatic kotlin/collections/SetsKt.minus (Ljava/util/Set;Ljava/lang/Iterable;)Ljava/util/Set;
      // 104: astore 7
      // 106: aload 7
      // 108: invokeinterface java/util/Set.isEmpty ()Z 1
      // 10d: ifne 131
      // 110: bipush 0
      // 111: istore 8
      // 113: aload 0
      // 114: invokespecial cn/sast/cli/command/tools/CheckerInfoGenerator.getStandardNameMappingJson ()Ljava/nio/file/Path;
      // 117: aload 7
      // 119: aload 0
      // 11a: invokespecial cn/sast/cli/command/tools/CheckerInfoGenerator.getCheckerInfoCsvPath ()Ljava/nio/file/Path;
      // 11d: invokedynamic makeConcatWithConstants (Ljava/nio/file/Path;Ljava/util/Set;Ljava/nio/file/Path;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "The rule set names in JSON file '\u0001': \u0001 are missing from the '\u0001' and need to be added." ]
      // 122: astore 8
      // 124: new java/lang/IllegalStateException
      // 127: dup
      // 128: aload 8
      // 12a: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 12d: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 130: athrow
      // 131: aload 5
      // 133: aload 4
      // 135: invokestatic kotlin/TuplesKt.to (Ljava/lang/Object;Ljava/lang/Object;)Lkotlin/Pair;
      // 138: areturn
   }

   private fun dumpCheckerInfoJsonFile(checkerIdInCsv: Set<String>, existsCheckerIds: LinkedHashSet<String>) {
      label82: {
         val lackIdInCsv: java.lang.Iterable = checkerIdInCsv;
         val outputStream: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filter$iv) {
            if (!existsCheckerIds.contains(`element$iv$iv` as java.lang.String)) {
               outputStream.add(`element$iv$iv`);
            }
         }

         val redundancyIdInCsv: java.util.List = outputStream as java.util.List;
         var `$this$filter$ivx`: java.lang.Iterable = existsCheckerIds;
         var `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Object element$iv$ivx : $this$filter$ivx) {
            if (!checkerIdInCsv.contains(`element$iv$ivx` as java.lang.String)) {
               `destination$iv$ivx`.add(`element$iv$ivx`);
            }
         }

         val var28: java.util.List = `destination$iv$ivx` as java.util.List;
         `$this$filter$ivx` = redundancyIdInCsv;
         var var14: java.util.List = this.warnings;
         `destination$iv$ivx` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$filter$ivx`, 10));

         for (Object item$iv$iv : $this$filter$ivx) {
            `destination$iv$ivx`.add("Redundant rule '${var54 as java.lang.String}' in checker_info.csv that not define in the plugin");
         }

         var14.addAll(`destination$iv$ivx` as java.util.List);
         `$this$filter$ivx` = var28;
         var14 = this.errors;
         `destination$iv$ivx` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$filter$ivx`, 10));

         for (Object item$iv$iv : $this$filter$ivx) {
            `destination$iv$ivx`.add("Rule '${var55 as java.lang.String}' define in the plugin that not declare in the checker_info.csv");
         }

         var14.addAll(`destination$iv$ivx` as java.util.List);
         val var10000: Path = this.output.resolve("redundancy_checker_csv_row.json");
         val var36: Array<OpenOption> = new OpenOption[0];
         val var62: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var36, var36.length));
         val var33: Closeable = var62;
         var var37: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.ListSerializer(BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)) as SerializationStrategy,
                  redundancyIdInCsv,
                  var33 as OutputStream
               );
            } catch (var18: java.lang.Throwable) {
               var37 = var18;
               throw var18;
            }
         } catch (var19: java.lang.Throwable) {
            CloseableKt.closeFinally(var33, var37);
         }

         CloseableKt.closeFinally(var33, null);

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.ListSerializer(BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)) as SerializationStrategy,
                  var28,
                  var33 as OutputStream
               );
            } catch (var16: java.lang.Throwable) {
               var37 = var16;
               throw var16;
            }
         } catch (var17: java.lang.Throwable) {
            CloseableKt.closeFinally(var33, var37);
         }

         CloseableKt.closeFinally(var33, null);
      }
   }

   private fun toBoolean(value: String?): Boolean? {
      val var10000: Any;
      if (value == null) {
         var10000 = null;
      } else {
         if (!(value == "无")) {
            var var2: java.lang.String = value.toUpperCase(Locale.ROOT);
            if (!(var2 == "NONE")) {
               if (!(value == "否")) {
                  var2 = value.toUpperCase(Locale.ROOT);
                  if (!(var2 == "FALSE")) {
                     if (!(value == "是")) {
                        var2 = value.toUpperCase(Locale.ROOT);
                        if (!(var2 == "TRUE")) {
                           return null;
                        }
                     }

                     return true;
                  }
               }

               return false;
            }
         }

         var10000 = null;
      }

      return (java.lang.Boolean)var10000;
   }

   private fun validateLanguageMapToContext(map: Map<String, String>, checkerId: String, key: String) {
      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$iv : $this$forEach$iv) {
         val lang: java.lang.String = (`element$iv` as Entry).getKey() as java.lang.String;
         val context: java.lang.String = (`element$iv` as Entry).getValue() as java.lang.String;
         if ("en-US" == lang && this.containsChinese(context)) {
            this.warnings.add("The $key of checker with id: $checkerId is invalid in en-US context!");
         }

         if ("zh-CN" == lang && !this.containsChinese(context)) {
            this.warnings.add("The $key of checker with id: $checkerId is invalid in zh-CN context!");
         }
      }
   }

   private fun containsChinese(context: String): Boolean {
      return Pattern.compile("[\\u4e00-\\u9fa5]").matcher(context).find();
   }

   private fun checkAndAbort(abortOnError: Boolean) {
      if (abortOnError) {
         if (!this.warnings.isEmpty()) {
            logger.warn(CheckerInfoGenerator::checkAndAbort$lambda$43);
         }

         if (!this.errors.isEmpty()) {
            logger.error(CheckerInfoGenerator::checkAndAbort$lambda$45);
            System.exit(5);
            throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
         } else {
            this.warnings.clear();
            this.errors.clear();
         }
      }
   }

   public fun dumpRuleChaptersYaml(checkerInfoGenResult: CheckerInfoGenResult) {
      label41: {
         val checkerPriorityConfig: CheckerPriorityConfig = CheckerPriorityConfig.Companion.deserialize(Resource.INSTANCE.fileOf(this.getRuleSortPath()));
         val chapters: java.util.List = checkerInfoGenResult.getChapters();
         val chapterTree: java.util.Map = checkerPriorityConfig.getSortTree(chapters);
         val `$this$associate$iv`: java.lang.Iterable = checkerPriorityConfig.getRuleWithSortNumber(chapters);
         val `destination$iv$iv`: java.util.Map = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associate$iv`, 10)), 16)
         );

         for (Object element$iv$iv : $this$associate$iv) {
            val var38: Pair = TuplesKt.to(
               (`element$iv$iv` as IndexedValue).getIndex(), ((`element$iv$iv` as IndexedValue).getValue() as ChapterFlat).toString()
            );
            `destination$iv$iv`.put(var38.getFirst(), var38.getSecond());
         }

         val chaptersWithSortNumber: java.util.Map = `destination$iv$iv`;
         val var10000: Path = this.getRuleChaptersYamlOutPath();
         val var31: Array<OpenOption> = new OpenOption[0];
         val var39: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var31, var31.length));
         val var30: Closeable = var39;
         var var32: java.lang.Throwable = null;

         try {
            try {
               Yaml.encodeToStream$default(
                  yamlFormat,
                  (
                     new LinkedHashMapSerializer(
                        StringSerializer.INSTANCE, new LinkedHashMapSerializer(StringSerializer.INSTANCE, new ArrayListSerializer(StringSerializer.INSTANCE))
                     )
                  ) as SerializationStrategy,
                  chapterTree,
                  var30 as OutputStream,
                  null,
                  8,
                  null
               );
            } catch (var19: java.lang.Throwable) {
               var32 = var19;
               throw var19;
            }
         } catch (var20: java.lang.Throwable) {
            CloseableKt.closeFinally(var30, var32);
         }

         CloseableKt.closeFinally(var30, null);

         try {
            try {
               Yaml.encodeToStream$default(
                  yamlFormat,
                  (new LinkedHashMapSerializer(IntSerializer.INSTANCE, StringSerializer.INSTANCE)) as SerializationStrategy,
                  chaptersWithSortNumber,
                  var30 as OutputStream,
                  null,
                  8,
                  null
               );
            } catch (var17: java.lang.Throwable) {
               var32 = var17;
               throw var17;
            }
         } catch (var18: java.lang.Throwable) {
            CloseableKt.closeFinally(var30, var32);
         }

         CloseableKt.closeFinally(var30, null);
      }
   }

   public fun dumpCheckerInfoJson(checkerInfo: CheckerInfoGenResult, abortOnError: Boolean = true) {
      label19: {
         this.dumpExistsCheckerIds(CollectionsKt.toList(checkerInfo.getExistsCheckerIds()));
         this.dumpCheckerInfoJsonFile(checkerInfo.getCheckerIdInCsv(), checkerInfo.getExistsCheckerIds());
         val var10000: Path = this.getCheckerInfoJsonOutPath();
         val var4: Array<OpenOption> = new OpenOption[0];
         val var14: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var4, var4.length));
         val var11: Closeable = var14;
         var var12: java.lang.Throwable = null;

         try {
            try {
               JvmStreamsKt.encodeToStream(
                  jsonFormat, infoSerializer as SerializationStrategy, CollectionsKt.toList(checkerInfo.getCheckerInfoList()), var11 as OutputStream
               );
            } catch (var7: java.lang.Throwable) {
               var12 = var7;
               throw var7;
            }
         } catch (var8: java.lang.Throwable) {
            CloseableKt.closeFinally(var11, var12);
         }

         CloseableKt.closeFinally(var11, null);
      }
   }

   public fun dumpRuleAndRuleMappingDB(checkerInfo: CheckerInfoGenResult, rules: Set<String>?) {
      label19: {
         val dbPath: Path = this.getCheckerInfoSqliteDBOutPath();
         Files.deleteIfExists(dbPath);
         val ruleAndRuleMapping: RuleAndRuleMapping = new RuleAndRuleMapping(checkerInfo, this.getRuleSortPath());
         val var5: Closeable = SqliteDiagnostics.Companion.openDataBase$default(SqliteDiagnostics.Companion, dbPath.toString(), null, 2, null);
         var var6: java.lang.Throwable = null;

         try {
            try {
               val it: SQLiteDB = var5 as SQLiteDB;
               (var5 as SQLiteDB).createSchema();
               ruleAndRuleMapping.insert(it.getDatabase(), rules);
            } catch (var9: java.lang.Throwable) {
               var6 = var9;
               throw var9;
            }
         } catch (var10: java.lang.Throwable) {
            CloseableKt.closeFinally(var5, var6);
         }

         CloseableKt.closeFinally(var5, null);
      }
   }

   public fun getCheckerInfo(abortOnError: Boolean = true): CheckerInfoGenResult {
      val `checkTypes$delegate`: Lazy = LazyKt.lazy(CheckerInfoGenerator::getCheckerInfo$lambda$56);
      val `existsCheckerIds$delegate`: Lazy = LazyKt.lazy(CheckerInfoGenerator::getCheckerInfo$lambda$61);
      this.validateBugMessageLanguage(getCheckerInfo$lambda$57(`checkTypes$delegate`));

      for (java.lang.String lang : this.language) {
         this.validateDescription(
            checkerInfoList,
            "${this.getSuffixOfLang(checkerInfoList)}.md",
            getCheckerInfo$lambda$57(`checkTypes$delegate`),
            CheckerInfoGenerator.HintEnum.MARKDOWN,
            CheckerInfoGenerator.HintEnum.PHANTOM_MARKDOWN
         );
         this.validateDescription(
            checkerInfoList,
            "${this.getSuffixOfLang(checkerInfoList)}.txt",
            getCheckerInfo$lambda$57(`checkTypes$delegate`),
            CheckerInfoGenerator.HintEnum.ABSTRACT,
            CheckerInfoGenerator.HintEnum.PHANTOM_ABSTRACT
         );
      }

      this.checkAndAbort(abortOnError);
      val var7: Pair = this.getCheckerInfoList(getCheckerInfo$lambda$62(`existsCheckerIds$delegate`));
      val var8: LinkedHashSet = var7.component1() as LinkedHashSet;
      val checkerIdInCsv: LinkedHashSet = var7.component2() as LinkedHashSet;
      this.checkAndAbort(abortOnError);
      this.validateCheckerIdAliasName(getCheckerInfo$lambda$62(`existsCheckerIds$delegate`));
      this.checkAndAbort(abortOnError);
      return new CheckerInfoGenResult(
         var8, new LinkedHashSet<>(getCheckerInfo$lambda$57(`checkTypes$delegate`)), getCheckerInfo$lambda$62(`existsCheckerIds$delegate`), checkerIdInCsv
      );
   }

   private fun validateCheckerIdAliasName(existsCheckerIds: Set<String>) {
      label44: {
         val var10000: Gson = new Gson();
         val var10001: Path = this.checkerInfoResRoot.resolve("checker_name_mapping.json");
         var var26: Any = var10000.fromJson(
            PathsKt.readText(var10001, Charsets.UTF_8), (new TypeToken<java.util.Map<java.lang.String, ? extends java.lang.String>>() {}).getType()
         );
         val renameMap: java.util.Map = var26 as java.util.Map;
         val `destination$iv$iv`: java.util.Map = new LinkedHashMap();

         for (Entry element$iv$iv : renameMap.entrySet()) {
            if (!existsCheckerIds.contains(`item$iv$iv`.getValue())) {
               `destination$iv$iv`.put(`item$iv$iv`.getKey(), `item$iv$iv`.getValue());
            }
         }

         val missing: java.util.Map = `destination$iv$iv`;
         var26 = this.output.resolve("${StringsKt.substringBeforeLast$default("checker_name_mapping.json", ".", null, 2, null)}.no-such-alias-names.json");
         val var20: Array<OpenOption> = new OpenOption[0];
         var26 = Files.newOutputStream((Path)var26, Arrays.copyOf(var20, var20.length));
         val var19: Closeable = var26 as Closeable;
         var var21: java.lang.Throwable = null;

         try {
            try {
               val `$this$mapTo$iv$iv`: OutputStream = var19 as OutputStream;
               var23 = 0;
               JvmStreamsKt.encodeToStream(
                  jsonFormat,
                  BuiltinSerializersKt.MapSerializer(
                     BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE), BuiltinSerializersKt.serializer(StringCompanionObject.INSTANCE)
                  ) as SerializationStrategy,
                  missing,
                  `$this$mapTo$iv$iv`
               );
            } catch (var15: java.lang.Throwable) {
               var21 = var15;
               throw var15;
            }
         } catch (var16: java.lang.Throwable) {
            CloseableKt.closeFinally(var19, var21);
         }

         CloseableKt.closeFinally(var19, null);

         val var9: <unknown>;
         while (var9.hasNext()) {
            val var24: Entry = var9.next() as Entry;
            var23.add(
               "checker_id alias ${var24.getKey()} -> ${var24.getValue()}. the target ${var24.getValue()} is not exists. check: checker_name_mapping.json"
            );
         }

         val var13: <unknown>;
         var13.addAll(var23 as java.util.List);
      }
   }

   @JvmStatic
   fun `getCheckerInfoList$lambda$18`(`this$0`: CheckerInfoGenerator, `$categoryLanguageMap`: java.util.Map, `$this$open`: CsvFileReader): Unit {
      val `$this$forEach$iv`: Sequence;
      for (Object element$iv : $this$forEach$iv) {
         val row: java.util.Map = `element$iv` as java.util.Map;
         val var10001: Path = `this$0`.getCategoryLanguageMapCsv();
         val lang: java.lang.String = `this$0`.getValueFromRow(var10001, row, "lang");
         val `value$iv`: Any = `$categoryLanguageMap`.get(lang);
         val var10000: Any;
         if (`value$iv` == null) {
            val var14: Any = new LinkedHashMap();
            `$categoryLanguageMap`.put(lang, var14);
            var10000 = var14;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.Map).putAll(row);
      }

      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `getCheckerInfoList$lambda$33$lambda$32$lambda$30`(): java.lang.String {
      return null;
   }

   @JvmStatic
   fun `getCheckerInfoList$lambda$33$lambda$32$lambda$31`(): java.lang.String {
      return null;
   }

   @JvmStatic
   fun `getCheckerInfoList$lambda$33`(
      `$standardCheckExists`: java.util.Set,
      `this$0`: CheckerInfoGenerator,
      `$checkerIdInCsv`: LinkedHashSet,
      `$existsCheckerIds`: LinkedHashSet,
      `$standardNames`: java.util.Map,
      `$checkerInfos`: LinkedHashSet,
      `$categoryLanguageMap`: java.util.Map,
      `$this$open`: CsvFileReader
   ): Unit {
      val `$this$forEach$iv`: Sequence;
      for (Object element$iv : $this$forEach$iv) {
         val row: java.util.Map = `element$iv` as java.util.Map;
         CollectionsKt.addAll(`$standardCheckExists`, (`element$iv` as java.util.Map).keySet());
         var var10001: Path = `this$0`.getCheckerInfoCsvPath();
         val checkerId: java.lang.String = `this$0`.getValueFromRow(var10001, row, "checker_id");
         if (!`$checkerIdInCsv`.add(checkerId)) {
            `this$0`.errors.add("Duplication rule $checkerId is already in csv");
         }

         if (`$existsCheckerIds`.contains(checkerId)) {
            val name: java.lang.Iterable = `this$0`.language;
            val description: LinkedHashMap = new LinkedHashMap(
               RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`this$0`.language, 10)), 16)
            );

            for (Object element$iv$iv : $this$associateWith$iv) {
               var var10000: java.util.Map = description;
               val `destination$iv$iv`: java.lang.String = `$this$flatMapTo$iv$iv` as java.lang.String;
               var10000 = `$categoryLanguageMap`.get(`$this$flatMapTo$iv$iv` as java.lang.String) as java.util.Map;
               if (var10000 == null) {
                  throw new IllegalStateException(("'$`destination$iv$iv`' is not exists in ${`this$0`.getCategoryLanguageMapCsv()}").toString());
               }

               var10001 = `this$0`.getCheckerInfoCsvPath();
               val `element$iv$ivx`: java.lang.String = `this$0`.getValueFromRow(var10001, row, "category");
               val var107: java.lang.String = var10000.get(`element$iv$ivx`) as java.lang.String;
               if (var107 == null) {
                  throw new IllegalStateException(
                     ("$`destination$iv$iv` category for '$`element$iv$ivx`' is not exists in ${`this$0`.getCategoryLanguageMapCsv()}").toString()
                  );
               }

               if (StringsKt.trim(var107).toString().length() <= 0) {
                  throw new IllegalStateException(
                     ("invalid translation of category: $`element$iv$ivx` and it is empty in ${`this$0`.getCategoryLanguageMapCsv()}").toString()
                  );
               }

               var10000.put(`$this$flatMapTo$iv$iv`, var107);
            }

            val categoryx: java.util.Map = description;
            `this$0`.validateLanguageMapToContext(description, checkerId, "category");
            val `$this$associateWith$ivx`: java.lang.Iterable = `this$0`.language;
            val `result$ivx`: LinkedHashMap = new LinkedHashMap(
               RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`this$0`.language, 10)), 16)
            );

            for (Object element$iv$iv : $this$associateWith$ivx) {
               val var108: java.util.Map = `result$ivx`;
               val var85: java.lang.String = "name-${StringsKt.substringBefore$default(var70 as java.lang.String, "-", null, 2, null)}";
               var10001 = `this$0`.getCheckerInfoCsvPath();
               var108.put(var70, `this$0`.getValueFromRow(var10001, row, var85));
            }

            val var54: java.util.Map = `result$ivx`;
            `this$0`.validateLanguageMapToContext(`result$ivx`, checkerId, "name");
            val `$this$associateWith$ivxx`: java.lang.Iterable = `this$0`.language;
            val `result$ivxx`: LinkedHashMap = new LinkedHashMap(
               RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`this$0`.language, 10)), 16)
            );

            for (Object element$iv$iv : $this$associateWith$ivxx) {
               val var109: java.util.Map = `result$ivxx`;
               val var89: Path = `this$0`.getAbstraction(checkerId, var78 as java.lang.String);
               val var121: Array<LinkOption> = new LinkOption[0];
               val var110: java.lang.String;
               if (!Files.exists(var89, Arrays.copyOf(var121, var121.length))) {
                  `this$0`.errors.add("Required: A short rule description file: '$var89' is not exists.");
                  var110 = "error: no such abstract file!";
               } else {
                  var110 = StringsKt.replace$default(PathsKt.readText(var89, Charsets.UTF_8), "\r\n", "\n", false, 4, null);
               }

               var109.put(var78, var110);
            }

            val var56: java.util.Map = `result$ivxx`;
            `this$0`.validateLanguageMapToContext(`result$ivxx`, checkerId, "abstract");
            val `$this$associateWith$ivxxx`: java.lang.Iterable = `this$0`.language;
            val `result$ivxxx`: LinkedHashMap = new LinkedHashMap(
               RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`this$0`.language, 10)), 16)
            );

            for (Object element$iv$iv : $this$associateWith$ivxxx) {
               val var111: java.util.Map = `result$ivxxx`;
               val var29: Path = `this$0`.getMarkdown(checkerId, var83 as java.lang.String);
               val var122: Array<LinkOption> = new LinkOption[0];
               val var112: java.lang.String;
               if (!Files.exists(var29, Arrays.copyOf(var122, var122.length))) {
                  `this$0`.errors.add("Required: A markdown file that detailing rule descriptions & defect repair recommendations: '$var29' is not exists.");
                  var112 = "error: no such abstract file!";
               } else {
                  var112 = StringsKt.replace$default(PathsKt.readText(var29, Charsets.UTF_8), "\r\n", "\n", false, 4, null);
               }

               var111.put(var83, var112);
            }

            val var59: java.util.Map = `result$ivxxx`;
            `this$0`.validateLanguageMapToContext(`result$ivxxx`, checkerId, "description");
            val var73: java.util.Collection = new ArrayList();

            for (Entry element$iv$iv : $standardNames.entrySet()) {
               val var93: java.lang.String = var88.getKey() as java.lang.String;
               val var95: java.lang.String = var88.getValue() as java.lang.String;
               var var114: java.lang.Iterable;
               if (!`this$0`.isValid(row, var93)) {
                  var114 = CollectionsKt.emptyList();
               } else {
                  var10001 = `this$0`.getCheckerInfoCsvPath();
                  val rule: java.lang.String = StringsKt.trim(`this$0`.getValueFromRow(var10001, row, var93)).toString();
                  if (rule.length() == 0) {
                     var114 = CollectionsKt.emptyList();
                  } else {
                     label172: {
                        if (!(rule == "无")) {
                           val var113: java.lang.String = rule.toUpperCase(Locale.ROOT);
                           if (!(var113 == "NONE")) {
                              if (!(rule == "否")) {
                                 val var115: java.lang.String = rule.toUpperCase(Locale.ROOT);
                                 if (!(var115 == "FALSE")) {
                                    if (!(rule == "是")) {
                                       val var116: java.lang.String = rule.toUpperCase(Locale.ROOT);
                                       if (!(var116 == "TRUE")) {
                                          var var98: java.lang.Iterable = StringsKt.split$default(rule, new java.lang.String[]{","}, false, 0, 6, null);
                                          var `destination$iv$ivx`: java.util.Collection = new ArrayList();

                                          for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
                                             val var48: java.lang.String = StringsKt.trim(var43 as java.lang.String).toString();
                                             val var118: java.lang.String = if (!StringsKt.isBlank(var48)) var48 else null;
                                             if (var118 != null) {
                                                `destination$iv$ivx`.add(var118);
                                             }
                                          }

                                          var98 = `destination$iv$ivx` as java.util.List;
                                          `destination$iv$ivx` = new ArrayList(
                                             CollectionsKt.collectionSizeOrDefault(`destination$iv$ivx` as java.util.List, 10)
                                          );

                                          for (Object item$iv$iv : $this$mapNotNull$iv) {
                                             `destination$iv$ivx`.add(new Tag(var95, var103 as java.lang.String));
                                          }

                                          var114 = `destination$iv$ivx` as java.util.List;
                                          break label172;
                                       }
                                    }

                                    var114 = CollectionsKt.listOf(new Tag(var95, ""));
                                    break label172;
                                 }
                              }

                              var114 = CollectionsKt.emptyList();
                              break label172;
                           }
                        }

                        var114 = CollectionsKt.emptyList();
                     }
                  }
               }

               CollectionsKt.addAll(var73, var114);
            }

            val var62: java.util.List = var73 as java.util.List;
            val var10009: Path = `this$0`.getCheckerInfoCsvPath();
            val var10008: java.lang.String = `this$0`.getValueFromRow(var10009, row, "severity");
            val var10012: java.util.Map = MapsKt.toMutableMap(var59);
            val var10015: Path = `this$0`.getCheckerInfoCsvPath();
            val var10014: java.lang.String = `this$0`.getValueFromRowOrNull(
               var10015, row, "影响程度", CheckerInfoGenerator::getCheckerInfoList$lambda$33$lambda$32$lambda$30
            );
            val var10016: Path = `this$0`.getCheckerInfoCsvPath();
            val var124: java.lang.String = `this$0`.getValueFromRowOrNull(
               var10016, row, "可利用性", CheckerInfoGenerator::getCheckerInfoList$lambda$33$lambda$32$lambda$31
            );
            val var10017: Path = `this$0`.getCheckerInfoCsvPath();
            val var125: java.lang.String = getValueFromRowOrNull$default(`this$0`, var10017, row, "精确率", null, 8, null);
            val var10018: Path = `this$0`.getCheckerInfoCsvPath();
            val var126: java.lang.String = getValueFromRowOrNull$default(`this$0`, var10018, row, "召回率", null, 8, null);
            val var10019: Path = `this$0`.getCheckerInfoCsvPath();
            val var127: java.lang.String = getValueFromRowOrNull$default(`this$0`, var10019, row, "impl", null, 8, null);
            val var10021: Path = `this$0`.getCheckerInfoCsvPath();
            `$checkerInfos`.add(
               new CheckerInfo(
                  "Corax Checker",
                  "1",
                  "Java(canary)",
                  "Java",
                  checkerId,
                  var10008,
                  categoryx,
                  var54,
                  var56,
                  var10012,
                  var62,
                  var10014,
                  var124,
                  var125,
                  var126,
                  var127,
                  `this$0`.toBoolean(getValueFromRowOrNull$default(`this$0`, var10021, row, "是否有实现", null, 8, null))
               )
            );
         }
      }

      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `checkAndAbort$lambda$43$lambda$42`(it: Any): java.lang.CharSequence {
      return Theme.Companion.getDefault().getWarning().invoke("W: $it");
   }

   @JvmStatic
   fun `checkAndAbort$lambda$43`(`this$0`: CheckerInfoGenerator): Any {
      return "发现 ${Theme.Companion.getDefault().getWarning().invoke(java.lang.String.valueOf(`this$0`.warnings.size()))} 个问题, \n\t${CollectionsKt.joinToString$default(
         `this$0`.warnings, "\n\t", null, null, 0, null, CheckerInfoGenerator::checkAndAbort$lambda$43$lambda$42, 30, null
      )}";
   }

   @JvmStatic
   fun `checkAndAbort$lambda$45$lambda$44`(it: Any): java.lang.CharSequence {
      return Theme.Companion.getDefault().getDanger().invoke("E: $it");
   }

   @JvmStatic
   fun `checkAndAbort$lambda$45`(`this$0`: CheckerInfoGenerator): Any {
      return "发现 ${Theme.Companion.getDefault().getDanger().invoke(java.lang.String.valueOf(`this$0`.errors.size()))} 个严重错误, \n\t${CollectionsKt.joinToString$default(
         `this$0`.errors, "\n\t", null, null, 0, null, CheckerInfoGenerator::checkAndAbort$lambda$45$lambda$44, 30, null
      )}";
   }

   @JvmStatic
   fun `dumpRuleChaptersYaml$lambda$49`(`this$0`: CheckerInfoGenerator): Any {
      return "Successfully! File: ${`this$0`.getRuleChaptersYamlOutPath()} has been generated.";
   }

   @JvmStatic
   fun `dumpCheckerInfoJson$lambda$51`(`this$0`: CheckerInfoGenerator): Any {
      return "Successfully! File: ${`this$0`.getCheckerInfoJsonOutPath()} has been generated.";
   }

   @JvmStatic
   fun `dumpRuleAndRuleMappingDB$lambda$53`(`$dbPath`: Path): Any {
      return "Successfully! File: $`$dbPath` has been generated.";
   }

   @JvmStatic
   fun `getCheckerInfo$lambda$56`(`this$0`: CheckerInfoGenerator): java.util.List {
      val `$this$sortedBy$iv`: java.lang.Iterable = `this$0`.pluginDefinitions.getCheckTypeDefinition(CheckType.class);
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$sortedBy$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as PluginDefinitions.CheckTypeDefinition).getSingleton());
      }

      return CollectionsKt.sortedWith(`destination$iv$iv` as java.util.List, new CheckerInfoGenerator$getCheckerInfo$lambda$56$$inlined$sortedBy$1());
   }

   @JvmStatic
   fun `getCheckerInfo$lambda$57`(`$checkTypes$delegate`: Lazy<? extends java.utilList<? extends CheckType>>): MutableList<CheckType> {
      return `$checkTypes$delegate`.getValue() as MutableList<CheckType>;
   }

   @JvmStatic
   fun `getCheckerInfo$lambda$61$lambda$60`(`$existsCheckerIds`: LinkedHashSet): Any {
      return "Found ${`$existsCheckerIds`.size()} CheckTypes";
   }

   @JvmStatic
   fun `getCheckerInfo$lambda$61`(`$checkTypes$delegate`: Lazy): LinkedHashSet {
      val `$this$mapTo$iv`: java.lang.Iterable = getCheckerInfo$lambda$57(`$checkTypes$delegate`);
      val var3: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         var3.add(CheckerInfoGeneratorKt.getId(`item$iv` as CheckType));
      }

      val existsCheckerIds: LinkedHashSet = var3 as LinkedHashSet;
      if ((var3 as LinkedHashSet).isEmpty()) {
         throw new IllegalStateException("Internal error! Failed to find any CheckType".toString());
      } else {
         logger.info(CheckerInfoGenerator::getCheckerInfo$lambda$61$lambda$60);
         return existsCheckerIds;
      }
   }

   @JvmStatic
   fun `getCheckerInfo$lambda$62`(`$existsCheckerIds$delegate`: Lazy<? extends LinkedHashSet<java.lang.String>>): LinkedHashSet<java.lang.String> {
      return `$existsCheckerIds$delegate`.getValue() as LinkedHashSet<java.lang.String>;
   }

   @JvmStatic
   fun `logger$lambda$66`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun JsonBuilder.`jsonFormat$lambda$67`(): Unit {
      `$this$Json`.setUseArrayPolymorphism(true);
      `$this$Json`.setPrettyPrint(true);
      return Unit.INSTANCE;
   }

   @SourceDebugExtension(["SMAP\nCheckerInfoGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfoGenerator.kt\ncn/sast/cli/command/tools/CheckerInfoGenerator$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,476:1\n1#2:477\n*E\n"])
   public companion object {
      private final val logger: KLogger
      public final val jsonFormat: Json
      public final val yamlFormat: Yaml
      public final val infoSerializer: KSerializer<List<CheckerInfo>>

      public fun createCheckerInfoGenerator(
         pl: ConfigPluginLoader,
         language: List<String> = CollectionsKt.listOf(new java.lang.String[]{"zh-CN", "en-US"}),
         stopOnError: Boolean = true
      ): CheckerInfoGenerator? {
         val var10000: IResDirectory = MainConfigKt.checkerInfoDir(pl.getConfigDirs(), stopOnError);
         if (var10000 != null) {
            val var16: Path = var10000.getPath();
            if (var16 != null) {
               var pluginDefinitions: Path = var16.resolve("checker_info_generator_logs");
               val var10001: Array<LinkOption> = new LinkOption[0];
               if (!Files.exists(pluginDefinitions, Arrays.copyOf(var10001, var10001.length))) {
                  Files.createDirectories(pluginDefinitions);
               }

               pluginDefinitions = pluginDefinitions.normalize();
               CheckerInfoGenerator.access$getLogger$cp().info(CheckerInfoGenerator.Companion::createCheckerInfoGenerator$lambda$4$lambda$3);
               val var12: PluginDefinitions = PluginDefinitions.Companion.load(pl.getPluginManager());
               return new CheckerInfoGenerator(language, pluginDefinitions, var16, var12);
            }
         }

         val var8: CheckerInfoGenerator.Companion = this;
         if (stopOnError) {
            throw new IllegalStateException("get checkerInfoDir return null".toString());
         } else {
            return null;
         }
      }

      @JvmStatic
      fun `createCheckerInfoGenerator$lambda$4$lambda$3`(`$it`: Path): Any {
         return "The tools output path is: $`$it`";
      }
   }

   public enum class HintEnum {
      PHANTOM_MARKDOWN,
      PHANTOM_ABSTRACT,
      MARKDOWN,
      ABSTRACT
      @JvmStatic
      fun getEntries(): EnumEntries<CheckerInfoGenerator.HintEnum> {
         return $ENTRIES;
      }
   }
}
