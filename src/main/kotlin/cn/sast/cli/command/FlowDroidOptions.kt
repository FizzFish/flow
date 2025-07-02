package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.dataflow.infoflow.InfoflowConfigurationExt
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.FlagOptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import com.github.ajalt.clikt.parameters.options.OptionWithValues.DefaultImpls
import com.github.ajalt.clikt.parameters.types.ChoiceKt
import com.github.ajalt.clikt.parameters.types.IntKt
import com.github.ajalt.clikt.parameters.types.LongKt
import com.github.ajalt.clikt.parameters.types.RangeKt
import java.util.LinkedHashMap
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.AccessPathConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.AliasingAlgorithm
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm
import soot.jimple.infoflow.InfoflowConfiguration.CodeEliminationMode
import soot.jimple.infoflow.InfoflowConfiguration.DataFlowDirection
import soot.jimple.infoflow.InfoflowConfiguration.DataFlowSolver
import soot.jimple.infoflow.InfoflowConfiguration.ImplicitFlowMode
import soot.jimple.infoflow.InfoflowConfiguration.PathBuildingAlgorithm
import soot.jimple.infoflow.InfoflowConfiguration.PathConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.PathReconstructionMode
import soot.jimple.infoflow.InfoflowConfiguration.SolverConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.SootIntegrationMode

@SourceDebugExtension(["SMAP\nFlowDroidOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowDroidOptions.kt\ncn/sast/cli/command/FlowDroidOptions\n+ 2 Convert.kt\ncom/github/ajalt/clikt/parameters/options/OptionWithValuesKt__ConvertKt\n+ 3 enum.kt\ncom/github/ajalt/clikt/parameters/types/EnumKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 enum.kt\ncom/github/ajalt/clikt/parameters/types/EnumKt$enum$3\n*L\n1#1,152:1\n35#2,6:153\n70#2:159\n82#2,4:160\n45#3,5:164\n45#3,5:175\n45#3,5:186\n45#3,5:197\n45#3,5:208\n45#3,5:219\n45#3,5:230\n45#3,5:241\n45#3,5:252\n8541#4,2:169\n8801#4,2:171\n8804#4:174\n8541#4,2:180\n8801#4,2:182\n8804#4:185\n8541#4,2:191\n8801#4,2:193\n8804#4:196\n8541#4,2:202\n8801#4,2:204\n8804#4:207\n8541#4,2:213\n8801#4,2:215\n8804#4:218\n8541#4,2:224\n8801#4,2:226\n8804#4:229\n8541#4,2:235\n8801#4,2:237\n8804#4:240\n8541#4,2:246\n8801#4,2:248\n8804#4:251\n8541#4,2:257\n8801#4,2:259\n8804#4:262\n47#5:173\n47#5:184\n47#5:195\n47#5:206\n47#5:217\n47#5:228\n47#5:239\n47#5:250\n47#5:261\n*S KotlinDebug\n*F\n+ 1 FlowDroidOptions.kt\ncn/sast/cli/command/FlowDroidOptions\n*L\n17#1:153,6\n17#1:159\n17#1:160,4\n28#1:164,5\n30#1:175,5\n37#1:186,5\n39#1:197,5\n41#1:208,5\n44#1:219,5\n56#1:230,5\n58#1:241,5\n77#1:252,5\n28#1:169,2\n28#1:171,2\n28#1:174\n30#1:180,2\n30#1:182,2\n30#1:185\n37#1:191,2\n37#1:193,2\n37#1:196\n39#1:202,2\n39#1:204,2\n39#1:207\n41#1:213,2\n41#1:215,2\n41#1:218\n44#1:224,2\n44#1:226,2\n44#1:229\n56#1:235,2\n56#1:237,2\n56#1:240\n58#1:246,2\n58#1:248,2\n58#1:251\n77#1:257,2\n77#1:259,2\n77#1:262\n28#1:173\n30#1:184\n37#1:195\n39#1:206\n41#1:217\n44#1:228\n56#1:239\n58#1:250\n77#1:261\n*E\n"])
public class FlowDroidOptions(isHidden: Boolean = false) : OptionGroup("FlowDroid Options", null, 2) {
   public final val isHidden: Boolean

   public final val enableFlowDroid: Boolean
      public final get() {
         return this.enableFlowDroid$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Boolean;
      }


   private final val baseDirectory: String
      private final get() {
         return this.baseDirectory$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.String;
      }


   private final val noPathAgnosticResults: Boolean
      private final get() {
         return this.noPathAgnosticResults$delegate.getValue(this, $$delegatedProperties[2]) as java.lang.Boolean;
      }


   private final val oneResultPerAccessPath: Boolean
      private final get() {
         return this.oneResultPerAccessPath$delegate.getValue(this, $$delegatedProperties[3]) as java.lang.Boolean;
      }


   private final val mergeNeighbors: Boolean
      private final get() {
         return this.mergeNeighbors$delegate.getValue(this, $$delegatedProperties[4]) as java.lang.Boolean;
      }


   private final val stopAfterFirstKFlows: Int
      private final get() {
         return (this.stopAfterFirstKFlows$delegate.getValue(this, $$delegatedProperties[5]) as java.lang.Number).intValue();
      }


   private final val inspectSources: Boolean
      private final get() {
         return this.inspectSources$delegate.getValue(this, $$delegatedProperties[6]) as java.lang.Boolean;
      }


   private final val inspectSinks: Boolean
      private final get() {
         return this.inspectSinks$delegate.getValue(this, $$delegatedProperties[7]) as java.lang.Boolean;
      }


   private final val implicitFlowMode: ImplicitFlowMode
      private final get() {
         return this.implicitFlowMode$delegate.getValue(this, $$delegatedProperties[8]) as ImplicitFlowMode;
      }


   private final val sootIntegrationMode: SootIntegrationMode
      private final get() {
         return this.sootIntegrationMode$delegate.getValue(this, $$delegatedProperties[9]) as SootIntegrationMode;
      }


   private final val disableFlowSensitiveAliasing: Boolean
      private final get() {
         return this.disableFlowSensitiveAliasing$delegate.getValue(this, $$delegatedProperties[10]) as java.lang.Boolean;
      }


   private final val disableExceptionTracking: Boolean
      private final get() {
         return this.disableExceptionTracking$delegate.getValue(this, $$delegatedProperties[11]) as java.lang.Boolean;
      }


   private final val disableArrayTracking: Boolean
      private final get() {
         return this.disableArrayTracking$delegate.getValue(this, $$delegatedProperties[12]) as java.lang.Boolean;
      }


   private final val disableArraySizeTainting: Boolean
      private final get() {
         return this.disableArraySizeTainting$delegate.getValue(this, $$delegatedProperties[13]) as java.lang.Boolean;
      }


   private final val disableTypeChecking: Boolean
      private final get() {
         return this.disableTypeChecking$delegate.getValue(this, $$delegatedProperties[14]) as java.lang.Boolean;
      }


   private final val callgraphAlgorithm: CallgraphAlgorithm
      private final get() {
         return this.callgraphAlgorithm$delegate.getValue(this, $$delegatedProperties[15]) as CallgraphAlgorithm;
      }


   private final val aliasingAlgorithm: AliasingAlgorithm
      private final get() {
         return this.aliasingAlgorithm$delegate.getValue(this, $$delegatedProperties[16]) as AliasingAlgorithm;
      }


   private final val dataFlowDirection: DataFlowDirection
      private final get() {
         return this.dataFlowDirection$delegate.getValue(this, $$delegatedProperties[17]) as DataFlowDirection;
      }


   private final val ignoreFlowsInSystemPackages: Boolean
      private final get() {
         return this.ignoreFlowsInSystemPackages$delegate.getValue(this, $$delegatedProperties[18]) as java.lang.Boolean;
      }


   private final val writeOutputFiles: Boolean
      private final get() {
         return this.writeOutputFiles$delegate.getValue(this, $$delegatedProperties[19]) as java.lang.Boolean;
      }


   private final val codeEliminationMode: CodeEliminationMode
      private final get() {
         return this.codeEliminationMode$delegate.getValue(this, $$delegatedProperties[20]) as CodeEliminationMode;
      }


   private final val disableLogSourcesAndSinks: Boolean
      private final get() {
         return this.disableLogSourcesAndSinks$delegate.getValue(this, $$delegatedProperties[21]) as java.lang.Boolean;
      }


   private final val enableReflection: Boolean
      private final get() {
         return this.enableReflection$delegate.getValue(this, $$delegatedProperties[22]) as java.lang.Boolean;
      }


   private final val disableLineNumbers: Boolean
      private final get() {
         return this.disableLineNumbers$delegate.getValue(this, $$delegatedProperties[23]) as java.lang.Boolean;
      }


   private final val disableTaintAnalysis: Boolean
      private final get() {
         return this.disableTaintAnalysis$delegate.getValue(this, $$delegatedProperties[24]) as java.lang.Boolean;
      }


   private final val incrementalResultReporting: Boolean
      private final get() {
         return this.incrementalResultReporting$delegate.getValue(this, $$delegatedProperties[25]) as java.lang.Boolean;
      }


   private final val dataFlowTimeout: Long
      private final get() {
         return (this.dataFlowTimeout$delegate.getValue(this, $$delegatedProperties[26]) as java.lang.Number).longValue();
      }


   private final val oneSourceAtATime: Boolean
      private final get() {
         return this.oneSourceAtATime$delegate.getValue(this, $$delegatedProperties[27]) as java.lang.Boolean;
      }


   private final val sequentialPathProcessing: Boolean
      private final get() {
         return this.sequentialPathProcessing$delegate.getValue(this, $$delegatedProperties[28]) as java.lang.Boolean;
      }


   private final val pathReconstructionMode: PathReconstructionMode
      private final get() {
         return this.pathReconstructionMode$delegate.getValue(this, $$delegatedProperties[29]) as PathReconstructionMode;
      }


   private final val pathBuildingAlgorithm: PathBuildingAlgorithm
      private final get() {
         return this.pathBuildingAlgorithm$delegate.getValue(this, $$delegatedProperties[30]) as PathBuildingAlgorithm;
      }


   private final val maxCallStackSize: Int
      private final get() {
         return (this.maxCallStackSize$delegate.getValue(this, $$delegatedProperties[31]) as java.lang.Number).intValue();
      }


   private final val maxPathLength: Int
      private final get() {
         return (this.maxPathLength$delegate.getValue(this, $$delegatedProperties[32]) as java.lang.Number).intValue();
      }


   private final val maxPathsPerAbstraction: Int
      private final get() {
         return (this.maxPathsPerAbstraction$delegate.getValue(this, $$delegatedProperties[33]) as java.lang.Number).intValue();
      }


   private final val pathReconstructionTimeout: Long
      private final get() {
         return (this.pathReconstructionTimeout$delegate.getValue(this, $$delegatedProperties[34]) as java.lang.Number).longValue();
      }


   private final val pathReconstructionBatchSize: Int
      private final get() {
         return (this.pathReconstructionBatchSize$delegate.getValue(this, $$delegatedProperties[35]) as java.lang.Number).intValue();
      }


   private final val accessPathLength: Int
      private final get() {
         return (this.accessPathLength$delegate.getValue(this, $$delegatedProperties[36]) as java.lang.Number).intValue();
      }


   private final val useRecursiveAccessPaths: Boolean
      private final get() {
         return this.useRecursiveAccessPaths$delegate.getValue(this, $$delegatedProperties[37]) as java.lang.Boolean;
      }


   private final val useThisChainReduction: Boolean
      private final get() {
         return this.useThisChainReduction$delegate.getValue(this, $$delegatedProperties[38]) as java.lang.Boolean;
      }


   private final val useSameFieldReduction: Boolean
      private final get() {
         return this.useSameFieldReduction$delegate.getValue(this, $$delegatedProperties[39]) as java.lang.Boolean;
      }


   private final val disableSparseOpt: Boolean
      private final get() {
         return this.disableSparseOpt$delegate.getValue(this, $$delegatedProperties[40]) as java.lang.Boolean;
      }


   private final val maxJoinPointAbstractions: Int
      private final get() {
         return (this.maxJoinPointAbstractions$delegate.getValue(this, $$delegatedProperties[41]) as java.lang.Number).intValue();
      }


   private final val maxAbstractionPathLength: Int
      private final get() {
         return (this.maxAbstractionPathLength$delegate.getValue(this, $$delegatedProperties[42]) as java.lang.Number).intValue();
      }


   private final val maxCalleesPerCallSite: Int
      private final get() {
         return (this.maxCalleesPerCallSite$delegate.getValue(this, $$delegatedProperties[43]) as java.lang.Number).intValue();
      }


   private final val dataFlowSolver: DataFlowSolver
      private final get() {
         return this.dataFlowSolver$delegate.getValue(this, $$delegatedProperties[44]) as DataFlowSolver;
      }


   init {
      this.isHidden = isHidden;
      var `$this$enum_u24default$iv`: OptionWithValues = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, false, null, null, null, null, false, 510, null
      );
      val `$i$f$associateBy`: Function1 = new FlowDroidOptions$special$$inlined$convert$default$1("BOOL");
      val `$this$associateByTo$iv$iv$iv`: Function2 = new FlowDroidOptions$special$$inlined$convert$default$2(`$this$enum_u24default$iv`);
      val var10003: Function2 = OptionWithValuesKt.defaultEachProcessor();
      val var10004: Function2 = OptionWithValuesKt.defaultAllProcessor();
      val var10005: Function2 = OptionWithValuesKt.defaultValidator();
      var var10007: Function1 = `$this$enum_u24default$iv`.getMetavarGetter();
      if (var10007 == null) {
         var10007 = `$i$f$associateBy`;
      }

      var var10015: CompletionCandidates = `$this$enum_u24default$iv`.getExplicitCompletionCandidates();
      if (var10015 == null) {
         var10015 = null;
      }

      this.enableFlowDroid$delegate = OptionWithValuesKt.help(
            OptionWithValuesKt.required(
               DefaultImpls.copy$default(
                  `$this$enum_u24default$iv`,
                  `$this$associateByTo$iv$iv$iv`,
                  var10003,
                  var10004,
                  var10005,
                  null,
                  var10007,
                  null,
                  null,
                  false,
                  null,
                  null,
                  null,
                  null,
                  var10015,
                  null,
                  false,
                  false,
                  false,
                  253904,
                  null
               )
            ),
            "Set if the FlowDroid engine shall be enabled"
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[0]);
      this.baseDirectory$delegate = OptionWithValuesKt.default$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            "",
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[1]);
      this.noPathAgnosticResults$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[2]);
      this.oneResultPerAccessPath$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[3]);
      this.mergeNeighbors$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[4]);
      this.stopAfterFirstKFlows$delegate = OptionWithValuesKt.default$default(
            IntKt.int$default(
               OptionWithValuesKt.option$default(
                  this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
               ),
               false,
               1,
               null
            ),
            0,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[5]);
      this.inspectSources$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[6]);
      this.inspectSinks$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[7]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var48: Array<ImplicitFlowMode> = ImplicitFlowMode.values();
      var `destination$iv$iv$iv`: java.util.Map = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var48.length), 16));

      for (Object element$iv$iv$iv : var48) {
         `destination$iv$iv$iv`.put((`element$iv$iv$iv` as java.lang.Enum).name(), `element$iv$iv$iv`);
      }

      this.implicitFlowMode$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null), ImplicitFlowMode.ArrayAccesses, null, 2, null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[8]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var49: Array<SootIntegrationMode> = SootIntegrationMode.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var49.length), 16));

      for (Object element$iv$iv$iv : var49) {
         `destination$iv$iv$iv`.put((var116 as java.lang.Enum).name(), var116);
      }

      this.sootIntegrationMode$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null),
            SootIntegrationMode.CreateNewInstance,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[9]);
      this.disableFlowSensitiveAliasing$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[10]);
      this.disableExceptionTracking$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[11]);
      this.disableArrayTracking$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[12]);
      this.disableArraySizeTainting$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[13]);
      this.disableTypeChecking$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[14]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var50: Array<CallgraphAlgorithm> = CallgraphAlgorithm.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var50.length), 16));

      for (Object element$iv$iv$iv : var50) {
         `destination$iv$iv$iv`.put((var117 as java.lang.Enum).name(), var117);
      }

      this.callgraphAlgorithm$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null),
            CallgraphAlgorithm.AutomaticSelection,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[15]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var51: Array<AliasingAlgorithm> = AliasingAlgorithm.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var51.length), 16));

      for (Object element$iv$iv$iv : var51) {
         `destination$iv$iv$iv`.put((var118 as java.lang.Enum).name(), var118);
      }

      this.aliasingAlgorithm$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null), AliasingAlgorithm.FlowSensitive, null, 2, null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[16]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var52: Array<DataFlowDirection> = DataFlowDirection.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var52.length), 16));

      for (Object element$iv$iv$iv : var52) {
         `destination$iv$iv$iv`.put((var119 as java.lang.Enum).name(), var119);
      }

      this.dataFlowDirection$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null), DataFlowDirection.Forwards, null, 2, null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[17]);
      this.ignoreFlowsInSystemPackages$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[18]);
      this.writeOutputFiles$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[19]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var53: Array<CodeEliminationMode> = CodeEliminationMode.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var53.length), 16));

      for (Object element$iv$iv$iv : var53) {
         `destination$iv$iv$iv`.put((var120 as java.lang.Enum).name(), var120);
      }

      this.codeEliminationMode$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null),
            CodeEliminationMode.NoCodeElimination,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[20]);
      this.disableLogSourcesAndSinks$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[21]);
      this.enableReflection$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[22]);
      this.disableLineNumbers$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[23]);
      this.disableTaintAnalysis$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[24]);
      this.incrementalResultReporting$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[25]);
      this.dataFlowTimeout$delegate = OptionWithValuesKt.default$default(
            LongKt.long$default(
               OptionWithValuesKt.option$default(
                  this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
               ),
               false,
               1,
               null
            ),
            0L,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[26]);
      this.oneSourceAtATime$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[27]);
      this.sequentialPathProcessing$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[28]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var54: Array<PathReconstructionMode> = PathReconstructionMode.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var54.length), 16));

      for (Object element$iv$iv$iv : var54) {
         `destination$iv$iv$iv`.put((var121 as java.lang.Enum).name(), var121);
      }

      this.pathReconstructionMode$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null), PathReconstructionMode.Precise, null, 2, null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[29]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var55: Array<PathBuildingAlgorithm> = PathBuildingAlgorithm.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var55.length), 16));

      for (Object element$iv$iv$iv : var55) {
         `destination$iv$iv$iv`.put((var122 as java.lang.Enum).name(), var122);
      }

      this.pathBuildingAlgorithm$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null),
            PathBuildingAlgorithm.ContextSensitive,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[30]);
      this.maxCallStackSize$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            30,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[31]);
      this.maxPathLength$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            75,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[32]);
      this.maxPathsPerAbstraction$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            15,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[33]);
      this.pathReconstructionTimeout$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               LongKt.long$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new LongRange((long)-1, java.lang.Long.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            0L,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[34]);
      this.pathReconstructionBatchSize$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            5,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[35]);
      this.accessPathLength$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            25,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[36]);
      this.useRecursiveAccessPaths$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[37]);
      this.useThisChainReduction$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[38]);
      this.useSameFieldReduction$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[39]);
      this.disableSparseOpt$delegate = FlagOptionKt.flag$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
            ),
            new java.lang.String[0],
            false,
            null,
            6,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[40]);
      this.maxJoinPointAbstractions$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            -1,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[41]);
      this.maxAbstractionPathLength$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            -1,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[42]);
      this.maxCalleesPerCallSite$delegate = OptionWithValuesKt.default$default(
            RangeKt.restrictTo$default(
               IntKt.int$default(
                  OptionWithValuesKt.option$default(
                     this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
                  ),
                  false,
                  1,
                  null
               ),
               (new IntRange(-1, Integer.MAX_VALUE)) as ClosedRange,
               false,
               2,
               null
            ),
            -1,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[43]);
      `$this$enum_u24default$iv` = OptionWithValuesKt.option$default(
         this as ParameterHolder, new java.lang.String[0], null, null, this.isHidden, null, null, null, null, false, 502, null
      );
      val var56: Array<DataFlowSolver> = DataFlowSolver.values();
      `destination$iv$iv$iv` = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(var56.length), 16));

      for (Object element$iv$iv$iv : var56) {
         `destination$iv$iv$iv`.put((var123 as java.lang.Enum).name(), var123);
      }

      this.dataFlowSolver$delegate = OptionWithValuesKt.default$default(
            ChoiceKt.choice$default(`$this$enum_u24default$iv`, `destination$iv$iv$iv`, null, true, 2, null),
            DataFlowSolver.ContextFlowSensitive,
            null,
            2,
            null
         )
         .provideDelegate(this as ParameterHolder, $$delegatedProperties[44]);
   }

   private fun initializeGlobalStaticCommandLineOptions() {
      InfoflowConfiguration.setBaseDirectory(this.getBaseDirectory());
      InfoflowConfiguration.setOneResultPerAccessPath(this.getOneResultPerAccessPath());
      InfoflowConfiguration.setMergeNeighbors(this.getMergeNeighbors());
   }

   public fun configureInfoFlowConfig(infoFlowConfig: InfoflowConfiguration, mainConfig: MainConfig) {
      this.initializeGlobalStaticCommandLineOptions();
      infoFlowConfig.setOneSourceAtATime(this.getOneSourceAtATime());
      infoFlowConfig.setStopAfterFirstKFlows(this.getStopAfterFirstKFlows());
      infoFlowConfig.setInspectSources(this.getInspectSources());
      infoFlowConfig.setInspectSinks(this.getInspectSinks());
      infoFlowConfig.setImplicitFlowMode(this.getImplicitFlowMode());
      infoFlowConfig.setStaticFieldTrackingMode(FlowDroidOptionsKt.getCvt(mainConfig.getStaticFieldTrackingMode()));
      infoFlowConfig.setSootIntegrationMode(this.getSootIntegrationMode());
      infoFlowConfig.setFlowSensitiveAliasing(!this.getDisableFlowSensitiveAliasing());
      infoFlowConfig.setEnableExceptionTracking(!this.getDisableExceptionTracking());
      infoFlowConfig.setEnableArrayTracking(!this.getDisableArrayTracking());
      infoFlowConfig.setEnableArraySizeTainting(!this.getDisableArraySizeTainting());
      infoFlowConfig.setCallgraphAlgorithm(this.getCallgraphAlgorithm());
      infoFlowConfig.setAliasingAlgorithm(this.getAliasingAlgorithm());
      infoFlowConfig.setDataFlowDirection(this.getDataFlowDirection());
      infoFlowConfig.setEnableTypeChecking(!this.getDisableTypeChecking());
      infoFlowConfig.setIgnoreFlowsInSystemPackages(this.getIgnoreFlowsInSystemPackages());
      infoFlowConfig.setExcludeSootLibraryClasses(mainConfig.getApponly());
      infoFlowConfig.setMaxThreadNum(mainConfig.getParallelsNum());
      infoFlowConfig.setWriteOutputFiles(this.getWriteOutputFiles());
      infoFlowConfig.setCodeEliminationMode(this.getCodeEliminationMode());
      infoFlowConfig.setLogSourcesAndSinks(!this.getDisableLogSourcesAndSinks());
      infoFlowConfig.setEnableReflection(this.getEnableReflection());
      infoFlowConfig.setEnableLineNumbers(!this.getDisableLineNumbers());
      infoFlowConfig.setEnableOriginalNames(true);
      infoFlowConfig.setTaintAnalysisEnabled(!this.getDisableTaintAnalysis());
      infoFlowConfig.setIncrementalResultReporting(this.getIncrementalResultReporting());
      infoFlowConfig.setDataFlowTimeout(this.getDataFlowTimeout());
      infoFlowConfig.setMemoryThreshold(mainConfig.getMemoryThreshold());
      infoFlowConfig.setPathAgnosticResults(!this.getNoPathAgnosticResults());
      val pathConfiguration: PathConfiguration = infoFlowConfig.getPathConfiguration();
      pathConfiguration.setSequentialPathProcessing(this.getSequentialPathProcessing());
      pathConfiguration.setPathReconstructionMode(this.getPathReconstructionMode());
      pathConfiguration.setPathBuildingAlgorithm(this.getPathBuildingAlgorithm());
      pathConfiguration.setMaxCallStackSize(this.getMaxCallStackSize());
      pathConfiguration.setMaxPathLength(this.getMaxPathLength());
      pathConfiguration.setMaxPathsPerAbstraction(this.getMaxPathsPerAbstraction());
      pathConfiguration.setPathReconstructionTimeout(this.getPathReconstructionTimeout());
      pathConfiguration.setPathReconstructionBatchSize(this.getPathReconstructionBatchSize());
      val accessPathConfiguration: AccessPathConfiguration = infoFlowConfig.getAccessPathConfiguration();
      accessPathConfiguration.setAccessPathLength(this.getAccessPathLength());
      accessPathConfiguration.setUseRecursiveAccessPaths(this.getUseRecursiveAccessPaths());
      accessPathConfiguration.setUseThisChainReduction(this.getUseThisChainReduction());
      accessPathConfiguration.setUseSameFieldReduction(this.getUseSameFieldReduction());
      val solverConfiguration: SolverConfiguration = infoFlowConfig.getSolverConfiguration();
      solverConfiguration.setMaxJoinPointAbstractions(this.getMaxJoinPointAbstractions());
      solverConfiguration.setMaxAbstractionPathLength(this.getMaxAbstractionPathLength());
      solverConfiguration.setMaxCalleesPerCallSite(this.getMaxCalleesPerCallSite());
      solverConfiguration.setDataFlowSolver(this.getDataFlowSolver());
   }

   public fun getExtInfoFlowConfig(): InfoflowConfigurationExt {
      val var1: InfoflowConfigurationExt = new InfoflowConfigurationExt(false, null, 3, null);
      var1.setUseSparseOpt(!this.getDisableSparseOpt());
      return var1;
   }

   fun FlowDroidOptions() {
      this(false, 1, null);
   }
}
