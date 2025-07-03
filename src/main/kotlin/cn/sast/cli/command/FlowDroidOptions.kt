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
class FlowDroidOptions(isHidden: Boolean = false) : OptionGroup("FlowDroid Options", null, 2) {
    val isHidden: Boolean = isHidden

    val enableFlowDroid: Boolean
        get() = enableFlowDroid$delegate.getValue(this, $$delegatedProperties[0]) as Boolean

    private val baseDirectory: String
        get() = baseDirectory$delegate.getValue(this, $$delegatedProperties[1]) as String

    private val noPathAgnosticResults: Boolean
        get() = noPathAgnosticResults$delegate.getValue(this, $$delegatedProperties[2]) as Boolean

    private val oneResultPerAccessPath: Boolean
        get() = oneResultPerAccessPath$delegate.getValue(this, $$delegatedProperties[3]) as Boolean

    private val mergeNeighbors: Boolean
        get() = mergeNeighbors$delegate.getValue(this, $$delegatedProperties[4]) as Boolean

    private val stopAfterFirstKFlows: Int
        get() = (stopAfterFirstKFlows$delegate.getValue(this, $$delegatedProperties[5]) as Number).toInt()

    private val inspectSources: Boolean
        get() = inspectSources$delegate.getValue(this, $$delegatedProperties[6]) as Boolean

    private val inspectSinks: Boolean
        get() = inspectSinks$delegate.getValue(this, $$delegatedProperties[7]) as Boolean

    private val implicitFlowMode: ImplicitFlowMode
        get() = implicitFlowMode$delegate.getValue(this, $$delegatedProperties[8]) as ImplicitFlowMode

    private val sootIntegrationMode: SootIntegrationMode
        get() = sootIntegrationMode$delegate.getValue(this, $$delegatedProperties[9]) as SootIntegrationMode

    private val disableFlowSensitiveAliasing: Boolean
        get() = disableFlowSensitiveAliasing$delegate.getValue(this, $$delegatedProperties[10]) as Boolean

    private val disableExceptionTracking: Boolean
        get() = disableExceptionTracking$delegate.getValue(this, $$delegatedProperties[11]) as Boolean

    private val disableArrayTracking: Boolean
        get() = disableArrayTracking$delegate.getValue(this, $$delegatedProperties[12]) as Boolean

    private val disableArraySizeTainting: Boolean
        get() = disableArraySizeTainting$delegate.getValue(this, $$delegatedProperties[13]) as Boolean

    private val disableTypeChecking: Boolean
        get() = disableTypeChecking$delegate.getValue(this, $$delegatedProperties[14]) as Boolean

    private val callgraphAlgorithm: CallgraphAlgorithm
        get() = callgraphAlgorithm$delegate.getValue(this, $$delegatedProperties[15]) as CallgraphAlgorithm

    private val aliasingAlgorithm: AliasingAlgorithm
        get() = aliasingAlgorithm$delegate.getValue(this, $$delegatedProperties[16]) as AliasingAlgorithm

    private val dataFlowDirection: DataFlowDirection
        get() = dataFlowDirection$delegate.getValue(this, $$delegatedProperties[17]) as DataFlowDirection

    private val ignoreFlowsInSystemPackages: Boolean
        get() = ignoreFlowsInSystemPackages$delegate.getValue(this, $$delegatedProperties[18]) as Boolean

    private val writeOutputFiles: Boolean
        get() = writeOutputFiles$delegate.getValue(this, $$delegatedProperties[19]) as Boolean

    private val codeEliminationMode: CodeEliminationMode
        get() = codeEliminationMode$delegate.getValue(this, $$delegatedProperties[20]) as CodeEliminationMode

    private val disableLogSourcesAndSinks: Boolean
        get() = disableLogSourcesAndSinks$delegate.getValue(this, $$delegatedProperties[21]) as Boolean

    private val enableReflection: Boolean
        get() = enableReflection$delegate.getValue(this, $$delegatedProperties[22]) as Boolean

    private val disableLineNumbers: Boolean
        get() = disableLineNumbers$delegate.getValue(this, $$delegatedProperties[23]) as Boolean

    private val disableTaintAnalysis: Boolean
        get() = disableTaintAnalysis$delegate.getValue(this, $$delegatedProperties[24]) as Boolean

    private val incrementalResultReporting: Boolean
        get() = incrementalResultReporting$delegate.getValue(this, $$delegatedProperties[25]) as Boolean

    private val dataFlowTimeout: Long
        get() = (dataFlowTimeout$delegate.getValue(this, $$delegatedProperties[26]) as Number).toLong()

    private val oneSourceAtATime: Boolean
        get() = oneSourceAtATime$delegate.getValue(this, $$delegatedProperties[27]) as Boolean

    private val sequentialPathProcessing: Boolean
        get() = sequentialPathProcessing$delegate.getValue(this, $$delegatedProperties[28]) as Boolean

    private val pathReconstructionMode: PathReconstructionMode
        get() = pathReconstructionMode$delegate.getValue(this, $$delegatedProperties[29]) as PathReconstructionMode

    private val pathBuildingAlgorithm: PathBuildingAlgorithm
        get() = pathBuildingAlgorithm$delegate.getValue(this, $$delegatedProperties[30]) as PathBuildingAlgorithm

    private val maxCallStackSize: Int
        get() = (maxCallStackSize$delegate.getValue(this, $$delegatedProperties[31]) as Number).toInt()

    private val maxPathLength: Int
        get() = (maxPathLength$delegate.getValue(this, $$delegatedProperties[32]) as Number).toInt()

    private val maxPathsPerAbstraction: Int
        get() = (maxPathsPerAbstraction$delegate.getValue(this, $$delegatedProperties[33]) as Number).toInt()

    private val pathReconstructionTimeout: Long
        get() = (pathReconstructionTimeout$delegate.getValue(this, $$delegatedProperties[34]) as Number).toLong()

    private val pathReconstructionBatchSize: Int
        get() = (pathReconstructionBatchSize$delegate.getValue(this, $$delegatedProperties[35]) as Number).toInt()

    private val accessPathLength: Int
        get() = (accessPathLength$delegate.getValue(this, $$delegatedProperties[36]) as Number).toInt()

    private val useRecursiveAccessPaths: Boolean
        get() = useRecursiveAccessPaths$delegate.getValue(this, $$delegatedProperties[37]) as Boolean

    private val useThisChainReduction: Boolean
        get() = useThisChainReduction$delegate.getValue(this, $$delegatedProperties[38]) as Boolean

    private val useSameFieldReduction: Boolean
        get() = useSameFieldReduction$delegate.getValue(this, $$delegatedProperties[39]) as Boolean

    private val disableSparseOpt: Boolean
        get() = disableSparseOpt$delegate.getValue(this, $$delegatedProperties[40]) as Boolean

    private val maxJoinPointAbstractions: Int
        get() = (maxJoinPointAbstractions$delegate.getValue(this, $$delegatedProperties[41]) as Number).toInt()

    private val maxAbstractionPathLength: Int
        get() = (maxAbstractionPathLength$delegate.getValue(this, $$delegatedProperties[42]) as Number).toInt()

    private val maxCalleesPerCallSite: Int
        get() = (maxCalleesPerCallSite$delegate.getValue(this, $$delegatedProperties[43]) as Number).toInt()

    private val dataFlowSolver: DataFlowSolver
        get() = dataFlowSolver$delegate.getValue(this, $$delegatedProperties[44]) as DataFlowSolver

    private val enableFlowDroid$delegate = OptionWithValuesKt.help(
        OptionWithValuesKt.required(
            DefaultImpls.copy$default(
                OptionWithValuesKt.option$default(
                    this as ParameterHolder, emptyArray(), null, null, false, null, null, null, null, false, 510, null
                ),
                { "BOOL" },
                OptionWithValuesKt.defaultEachProcessor(),
                OptionWithValuesKt.defaultAllProcessor(),
                OptionWithValuesKt.defaultValidator(),
                null,
                { "BOOL" },
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                false,
                253904,
                null
            )
        ),
        "Set if the FlowDroid engine shall be enabled"
    ).provideDelegate(this as ParameterHolder, $$delegatedProperties[0])

    private val baseDirectory$delegate = OptionWithValuesKt.default$default(
        OptionWithValuesKt.option$default(
            this as ParameterHolder, emptyArray(), null, null, isHidden, null, null, null, null, false, 502, null
        ),
        "",
        null,
        2,
        null
    ).provideDelegate(this as ParameterHolder, $$delegatedProperties[1])

    // ... (rest of the delegate initializations follow the same pattern as above)

    private fun initializeGlobalStaticCommandLineOptions() {
        InfoflowConfiguration.setBaseDirectory(baseDirectory)
        InfoflowConfiguration.setOneResultPerAccessPath(oneResultPerAccessPath)
        InfoflowConfiguration.setMergeNeighbors(mergeNeighbors)
    }

    fun configureInfoFlowConfig(infoFlowConfig: InfoflowConfiguration, mainConfig: MainConfig) {
        initializeGlobalStaticCommandLineOptions()
        infoFlowConfig.apply {
            setOneSourceAtATime(oneSourceAtATime)
            setStopAfterFirstKFlows(stopAfterFirstKFlows)
            setInspectSources(inspectSources)
            setInspectSinks(inspectSinks)
            setImplicitFlowMode(implicitFlowMode)
            setStaticFieldTrackingMode(FlowDroidOptionsKt.getCvt(mainConfig.staticFieldTrackingMode))
            setSootIntegrationMode(sootIntegrationMode)
            setFlowSensitiveAliasing(!disableFlowSensitiveAliasing)
            setEnableExceptionTracking(!disableExceptionTracking)
            setEnableArrayTracking(!disableArrayTracking)
            setEnableArraySizeTainting(!disableArraySizeTainting)
            setCallgraphAlgorithm(callgraphAlgorithm)
            setAliasingAlgorithm(aliasingAlgorithm)
            setDataFlowDirection(dataFlowDirection)
            setEnableTypeChecking(!disableTypeChecking)
            setIgnoreFlowsInSystemPackages(ignoreFlowsInSystemPackages)
            setExcludeSootLibraryClasses(mainConfig.apponly)
            setMaxThreadNum(mainConfig.parallelsNum)
            setWriteOutputFiles(writeOutputFiles)
            setCodeEliminationMode(codeEliminationMode)
            setLogSourcesAndSinks(!disableLogSourcesAndSinks)
            setEnableReflection(enableReflection)
            setEnableLineNumbers(!disableLineNumbers)
            setEnableOriginalNames(true)
            setTaintAnalysisEnabled(!disableTaintAnalysis)
            setIncrementalResultReporting(incrementalResultReporting)
            setDataFlowTimeout(dataFlowTimeout)
            setMemoryThreshold(mainConfig.memoryThreshold)
            setPathAgnosticResults(!noPathAgnosticResults)
        }

        infoFlowConfig.pathConfiguration.apply {
            setSequentialPathProcessing(sequentialPathProcessing)
            setPathReconstructionMode(pathReconstructionMode)
            setPathBuildingAlgorithm(pathBuildingAlgorithm)
            setMaxCallStackSize(maxCallStackSize)
            setMaxPathLength(maxPathLength)
            setMaxPathsPerAbstraction(maxPathsPerAbstraction)
            setPathReconstructionTimeout(pathReconstructionTimeout)
            setPathReconstructionBatchSize(pathReconstructionBatchSize)
        }

        infoFlowConfig.accessPathConfiguration.apply {
            setAccessPathLength(accessPathLength)
            setUseRecursiveAccessPaths(useRecursiveAccessPaths)
            setUseThisChainReduction(useThisChainReduction)
            setUseSameFieldReduction(useSameFieldReduction)
        }

        infoFlowConfig.solverConfiguration.apply {
            setMaxJoinPointAbstractions(maxJoinPointAbstractions)
            setMaxAbstractionPathLength(maxAbstractionPathLength)
            setMaxCalleesPerCallSite(maxCalleesPerCallSite)
            setDataFlowSolver(dataFlowSolver)
        }
    }

    fun getExtInfoFlowConfig(): InfoflowConfigurationExt {
        return InfoflowConfigurationExt(false, null, 3, null).apply {
            setUseSparseOpt(!disableSparseOpt)
        }
    }

    constructor() : this(false)
}