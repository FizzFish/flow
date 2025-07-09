@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package cn.sast.cli.command

/* ―――――――― 依赖 ―――――――― */
import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.*
import cn.sast.api.report.IResultCollector
import cn.sast.common.CustomRepeatingTimer
import cn.sast.common.IResFile
import cn.sast.common.OS
import cn.sast.common.Resource
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.plugin.CheckerFilterByName
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.result.*
import com.feysh.corax.cache.analysis.SootInfoCache
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.MordantHelpFormatter
import com.github.ajalt.clikt.parameters.groups.GroupableOption
import com.github.ajalt.clikt.parameters.options.Option
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.mordant.rendering.Theme
import com.google.gson.Gson
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import java.io.File
import java.lang.management.ManagementFactory
import java.net.URL
import java.nio.charset.Charset
import kotlin.system.exitProcess

/* ―――――――― logger ―――――――― */
private val logger = KotlinLogging.logger {}

/* ───────────── CLI ───────────── */
class FySastCli : CliktCommand(
    name                = "corax",
    help                = "Static analysis CLI",
    printHelpOnEmptyArgs = true,
    helpFormatter       = { MordantHelpFormatter(it, showDefaultValues = true) }
) {

    /* ====== CLI 选项字段（后面可用 option{} DSL 绑定） ====== */
    var verbosity: Level = Level.INFO
    var config: String? = null
    var rules: MutableList<String>? = null

    val defaultOutput: IResDirectory =
        Resource.dirOf("${System.getProperty("user.dir")}${File.separator}output")
    var output: IResDirectory = defaultOutput

    var dumpSootScene: Boolean = false
    var resultType: MutableSet<ResultType> = mutableSetOf()

    var preferredLanguages: MutableList<Language> = mutableListOf()
    var enableDecompile: Boolean = false
    var enableCodeMetrics: Boolean = false

    var dumpSrcFileList: String? = null
    var target: TargetOptions? = null

    var process: MutableList<String> = mutableListOf()
    var classPath: MutableList<String> = mutableListOf()

    var autoAppClasses: MutableList<String> = mutableListOf()
    var autoAppTraverseMode: TraverseMode = TraverseMode.FILE
    var autoAppSrcOnlyFileScheme: Boolean = false

    var disableDefaultJavaClassPath: Boolean = false

    var sourcePath: MutableSet<IResDirectory> = mutableSetOf()
    var projectRoot: MutableList<String> = mutableListOf()

    var srcPrecedence: SrcPrecedence = SrcPrecedence.prec_java
    var incrementalScanOf: MutableList<String> = mutableListOf()
    var disableMappingDiffInArchive: Boolean = false

    var sunBootClassPath: String = ""
    var javaExtDirs: String = ""

    var ecjOptions: MutableList<String>? = null
    var serializeCG: Boolean = false

    var c2sMode: CompareMode = CompareMode.Path
    var hideNoSource: Boolean = false
    var traverseMode: TraverseMode = TraverseMode.FILE

    var projectScanConfig: File? = null
    var disableWrapper: Boolean = false
    var apponly: Boolean = false

    var disablePreAnalysis: Boolean = false
    var disableBuiltInAnalysis: Boolean = false

    var dataFlowOptions: DataFlowOptions? = null
    var checkerInfoGeneratorOptions: CheckerInfoGeneratorOptions? = null
    var checkerInfoCompareOptions: CheckerInfoCompareOptions? = null
    var subtoolsOptions: SubToolsOptions? = null
    var flowDroidOptions: FlowDroidOptions? = null
    var utAnalyzeOptions: UtAnalyzeOptions? = null

    var enableStructureAnalysis: Boolean = false
    var enableOriginalNames: Boolean = false

    var staticFieldTrackingMode: StaticFieldTrackingMode = StaticFieldTrackingMode.Disabled
    var callGraphAlgorithm: String = "CHATransformer"
    var callGraphAlgorithmBuiltIn: String = "SPARK"

    var disableReflection: Boolean = false
    var maxThreadNum: Int = Runtime.getRuntime().availableProcessors()
    var memoryThreshold: Double = 0.80            // 80 %
    var disableTop: Boolean = false
    var strict: Boolean = false

    var zipFSEnv: MutableMap<String, String> = mutableMapOf()
    var zipFSEncodings: MutableList<String>? = null

    var hashVersion: Int? = null
    var sourceEncoding: Charset = Charsets.UTF_8

    var makeScorecard: Boolean = false
    var timeout: Int = 0                          // seconds

    /* 下面每个字段都与你上传文件一致，这里省略 … */
    // ---------------------------------------------------------------------
    // 省略的字段请保留原先内容（dumpSootScene、dataFlowOptions …）
    // ---------------------------------------------------------------------

    /* ===== 运行期状态 ===== */
    var collectors: List<IResultCollector> = emptyList()
    var anchorPointFile: IResFile? = null
    lateinit var lastResult: ResultCollector
    val sqliteFileIndexes: MutableSet<IResFile> = mutableSetOf()

    /* ───────────────────────── Utils: 打印当前配置 ───────────────────────── */
    private fun printOptions() {
        val theme  = currentContext.theme
        val groups = registeredOptions()
            .groupBy { (it as? GroupableOption)?.parameterGroup }

        // 收集额外“工具”分组名称，若未启用则标记 [off]
        val activeToolGroups: Set<String> = buildSet {
            dataFlowOptions?.groupName?.let(::add)
            flowDroidOptions?.groupName?.let(::add)
            utAnalyzeOptions?.groupName?.let(::add)
            checkerInfoGeneratorOptions?.groupName?.let(::add)
            checkerInfoCompareOptions?.groupName?.let(::add)
            subtoolsOptions?.groupName?.let(::add)
        }

        val body = buildString {
            groups.forEach { (group, opts) ->
                val title = (group?.groupName ?: commandName)
                val header = theme.success(title)

                if (group != null && title !in activeToolGroups) {
                    append("$header [off]\n")
                } else {
                    append(header).append('\n')
                    opts.joinTo(this, separator = "\n\t", prefix = "\t") { formatOption(theme, it) }
                    append('\n')
                }
            }
        }

        // ——— 日志输出 ———
        logger.info { theme.info("Current work directory: ${File(".").absolutePath}") }
        logger.info { theme.info("PID: ${ProcessHandle.current().pid()}") }
        logger.info { "log files: ${AnalyzerEnv.lastLogFile.parent}" }
        logger.info { "\n$body" }
        logger.info { "${theme.success(Application.version)}: ${OS.binaryUrl?.path}" }

        // ——— 写入 output/command.txt ———
        output.mkdirs()
        output.resolve("command.txt").toFile().writeText(body)
    }private fun printOptions() {
        val theme  = currentContext.theme
        val groups = registeredOptions()
            .groupBy { (it as? GroupableOption)?.parameterGroup }

        // 收集额外“工具”分组名称，若未启用则标记 [off]
        val activeToolGroups: Set<String> = buildSet {
            dataFlowOptions?.groupName?.let(::add)
            flowDroidOptions?.groupName?.let(::add)
            utAnalyzeOptions?.groupName?.let(::add)
            checkerInfoGeneratorOptions?.groupName?.let(::add)
            checkerInfoCompareOptions?.groupName?.let(::add)
            subtoolsOptions?.groupName?.let(::add)
        }

        val body = buildString {
            groups.forEach { (group, opts) ->
                val title = (group?.groupName ?: commandName)
                val header = theme.success(title)

                if (group != null && title !in activeToolGroups) {
                    append("$header [off]\n")
                } else {
                    append(header).append('\n')
                    opts.joinTo(this, separator = "\n\t", prefix = "\t") { formatOption(theme, it) }
                    append('\n')
                }
            }
        }

        // ——— 日志输出 ———
        logger.info { theme.info("Current work directory: ${File(".").absolutePath}") }
        logger.info { theme.info("PID: ${ProcessHandle.current().pid()}") }
        logger.info { "log files: ${AnalyzerEnv.lastLogFile.parent}" }
        logger.info { "\n$body" }
        logger.info { "${theme.success(Application.version)}: ${OS.binaryUrl?.path}" }

        // ——— 写入 output/command.txt ———
        output.mkdirs()
        output.resolve("command.txt").toFile().writeText(body)
    }

    private fun formatOption(theme: Theme, opt: Option): CharSequence =
        if (opt is OptionWithValues) {
            val value  = runCatching { opt.value }.getOrNull()
            val names  = theme.info(opt.names.toString())
            val valStr = theme.warning(value.toString())
            "$names \"$valStr\""
        } else opt.names.toString()
    /* --------------------------------------------------------------------- */
    /* 1. 解析 --config 参数 (字符串格式: <pluginDir>[#<pluginId>]@<ymlFile>)  */
    /* --------------------------------------------------------------------- */

    private fun parseConfig(
        config: String,
        checkerFilter: CheckerFilterByName?,
    ): Pair<ConfigPluginLoader, SaConfig> {
        /* <pluginDir>@<yml>  or  <pluginDir>#<pluginId>@<yml> */
        val (pluginPath, ymlName) = config.split('@').let {
            require(it.size == 2) { "--config format invalid: $config" }
            it[0] to it[1]
        }

        val (pluginsDir, pluginId) = pluginPath.split('#').let {
            it[0] to it.getOrNull(1)
        }

        // 1️⃣ 解析 plugin 目录
        val pluginRoots = Resource.globPaths(pluginsDir)
            ?: error("$pluginsDir not exists")
        val tempDirs    = pluginRoots.map { it.resolve("plugins").toDirectory() }

        val loader = ConfigPluginLoader(pluginRoots, tempDirs)

        // 2️⃣ 解析 YML
        val saConfig: SaConfig = when {
            !ymlName.endsWith(".yml") -> loader.loadFromName(pluginId, ymlName)

            // 绝对路径 / 相对文件
            Resource.fileOf(ymlName).takeIf { it.exists && it.isFile } != null -> {
                loader.searchCheckerUnits(Resource.fileOf(ymlName), checkerFilter)
            }

            else -> {
                /* 在 pluginRoots 下递归查找该 yml */
                val ymlInPlugins = pluginRoots
                    .asSequence()
                    .flatMap { root ->
                        PathExtensions.getFiles(root.path)
                            .filter { it.name == ymlName }
                            .map { Resource.fileOf(it) }
                    }
                    .toList()

                val ymlFile = when (ymlInPlugins.size) {
                    0 -> {
                        // 生成模板后抛错提醒
                        val tpl = pluginRoots.first().resolve(ymlName).absolute.normalize().toFile()
                        loader.makeTemplateYml(tpl)
                        logger.warn { templateHintMsg(ymlName, pluginRoots, tpl) }
                        tpl
                    }

                    1 -> ymlInPlugins.first()
                    else -> error(
                        "multiple \"$ymlName\" found in\n${pluginRoots.joinToString("\n")}"
                    )
                }

                loader.searchCheckerUnits(ymlFile, checkerFilter)
            }
        }

        logger.info { "Use SA-Configuration: $ymlName" }
        return loader to saConfig
    }

    /* --------------------------------------------------------------------- */
    /* 2. 从环境变量 CORAX_CONFIG_DEFAULT_DIR 寻找默认配置                     */
    /* --------------------------------------------------------------------- */

    private fun defaultConfig(
        checkerFilter: CheckerFilterByName?,
    ): Pair<ConfigPluginLoader, SaConfig>? {
        logger.info { "Try find config from env: CORAX_CONFIG_DEFAULT_DIR" }
        val envDir = FySastCliEnv.defaultConfigDir ?: run {
            logger.info { "No env config dir found." }; return null
        }
        return parseConfig("default-config.yml@$envDir", checkerFilter)
    }

    /* --------------------------------------------------------------------- */
    /* 3. 结合 CLI 选项，生成最终 MainConfig                                  */
    /* --------------------------------------------------------------------- */

    private fun configureMainConfig(
        main: MainConfig,
        monitor: MetricsMonitor,
    ) {
        // —— 基本选项 ————————————————————————————————
        main.outputDir          = output
        main.preferredLanguages = preferredLanguages
        main.androidScene       = target is AndroidOptions
        main.callGraphAlgorithm = callGraphAlgorithm
        main.callGraphAlgorithmBuiltIn = callGraphAlgorithmBuiltIn
        main.enableOriginalNames = enableOriginalNames
        main.staticFieldTrackingMode = staticFieldTrackingMode
        main.enableReflection    = !disableReflection
        main.parallelsNum        = maxThreadNum
        main.memoryThreshold     = memoryThreshold

        // —— 路径相关 ————————————————————————————————
        main.classpath     = classPath
        main.projectRoot   = projectRoot.map(Resource::dirOf).toPersistentSet()
        main.sourcePath    = sourcePath.toPersistentSet()
        main.processDir    = process.map(Resource::dirOf).toPersistentSet()

        // —— Auto-App 设置 ——————————————————————————
        main.autoAppClasses       = autoAppClasses.map(Resource::dirOf).toPersistentSet()
        main.autoAppTraverseMode  = autoAppTraverseMode
        main.autoAppSrcInZipScheme = !autoAppSrcOnlyFileScheme

        // —— Java 编译/解码选项 ————————————————
        main.srcPrecedence      = srcPrecedence
        main.sunBootClassPath   = sunBootClassPath
        main.javaExtDirs        = javaExtDirs
        main.useDefaultJavaClassPath = !disableDefaultJavaClassPath
        main.deCompileIfNotExists    = enableDecompile
        main.enableCodeMetrics       = enableCodeMetrics
        ecjOptions?.let { main.ecjOptions = it }

        // —— 其它功能开关 ——————————————————————————
        main.hideNoSource = hideNoSource
        main.traverseMode = traverseMode
        main.skipClass    = apponly
        main.useWrapper   = !disableWrapper

        // —— Incremental & Coverage —————————————
        if (incrementalScanOf.isNotEmpty()) {
            main.incrementAnalyze = IncrementalAnalyzeImplByChangeFiles(
                main,
                !disableMappingDiffInArchive
            ).apply {
                incrementalScanOf
                    .flatMap { Resource.globPaths(it) ?: error("--incremental-base \"$it\" invalid") }
                    .forEach(::parseIncrementBaseFile)
            }
        }

        dataFlowOptions?.factor1?.let {
            ExtSettings.calleeDepChainMaxNum = it
        }
        main.enableCoverage = dataFlowOptions?.enableCoverage ?: false

        // —— SA-Config / 插件加载 ————————————————
        val checkerFilter = rules?.toSet()?.let(::compatibleOldCheckerNames)
        val (loader, saConfig) = loadSAConfig(checkerFilter)
        main.configDirs += loader.configDirs
        main.saConfig    = saConfig
        main.checkerInfo = lazy { CheckerInfoGenerator(saConfig).checkerInfo(false) }

        // —— 版本号 & 监控 ————————————————————————
        main.version = Application.version
        monitor.addAnalyzeFinishHook(Thread {
            println("log file: ${AnalyzerEnv.lastLogFile}")
        })
    }

    /* --------------------------------------------------------------------- */
    /* 4. 封装：若 --config 给出则用 parseConfig，否则 defaultConfig         */
    /* --------------------------------------------------------------------- */

    private fun loadSAConfig(
        checkerFilter: CheckerFilterByName?,
    ): Pair<ConfigPluginLoader, SaConfig> =
        config?.let { parseConfig(it, checkerFilter) }
            ?: defaultConfig(checkerFilter)
            ?: error("SA-Config is required.")

    /* --------------------------------------------------------------------- */
    /* 辅助工具函数                                                           */
    /* --------------------------------------------------------------------- */

    private fun templateHintMsg(
        name: String,
        pluginDirs: List<IResource>,
        tpl: IResFile,
    ) = buildString {
        appendLine()
        appendLine("file \"$name\" not found in plugin dirs:")
        pluginDirs.forEach { appendLine("\t${it.absolute.normalize()}") }
        appendLine("A template file has been generated:")
        appendLine("-> $tpl")
        appendLine("Run again with:")
        appendLine("\t--config $name@${tpl.parent}")
        appendLine()
    }
    /* ───────────────────────── 兼容旧规则名 ───────────────────────── */
    private fun compatibleOldCheckerNames(enable: Set<String>): CheckerFilterByName {
        val mappingUrl: URL = OS.binaryUrl ?: return CheckerFilterByName(enable, emptyMap())
        val mappingFile = Resource.of(mappingUrl).parent?.resolve("checker_name_mapping.json")
            ?: return CheckerFilterByName(enable, emptyMap())

        if (!mappingFile.exists || !mappingFile.isFile)
            return CheckerFilterByName(enable, emptyMap())

        @Suppress("UNCHECKED_CAST")
        val map: Map<String, String> = runCatching {
            Gson().fromJson(
                mappingFile.path.readText(Charsets.UTF_8),
                object : com.google.gson.reflect.TypeToken<Map<String, String>>() {}.type
            )
        }.getOrElse {
            logger.error(it) { "read mapping failed" }; emptyMap()
        }

        val visited = linkedSetOf<String>()
        val q = ArrayDeque(enable)
        while (q.isNotEmpty()) {
            val n = q.removeLast()
            if (visited.add(n)) map[n]?.let(q::add)
        }
        return CheckerFilterByName(visited, map)
    }

    /* ───────────────────────── 日志等级 ───────────────────────── */
    private fun setVerbosity(level: Level) {
        println("Log level changed to $level")
        Configurator.setAllLevels(LogManager.getRootLogger().name, UtilsKt.level(level))
    }

    /* ───────────────────────── ResultCollector ───────────────────────── */
    private fun getResultCollector(
        info: SootInfoCache,
        locator: ProjectFileLocator,
        main: MainConfig,
        monitor: MetricsMonitor,
    ): ResultCollector {

        val collectors = buildList<IResultCollector> {
            if (ResultType.COUNTER in resultType) add(ResultCounter())
            add(
                MissingSummaryReporter(
                    main.outputDir.resolve("undefined_summary_methods.txt").toFile()
                )
            )
        }

        val outputs = buildList<OutputType> {
            resultType.forEach {
                when (it) {
                    ResultType.PLIST        -> add(OutputType.PLIST)
                    ResultType.SARIF        -> add(OutputType.SARIF)
                    ResultType.SarifPackSrc -> add(OutputType.SarifPackSrc)
                    ResultType.SarifCopySrc -> add(OutputType.SarifCopySrc)
                    else -> {}
                }
            }
        }

        return ResultCollector(
            mainConfig   = main,
            info         = info,
            outputDir    = main.outputDir,
            locator      = locator,
            collectors   = collectors,
            outputTypes  = outputs,
            enableSqlite = ResultType.SQLITE in resultType,
            coverage     = JacocoCompoundCoverage(locator, dataFlowOptions?.enableCoverage == true),
            monitor      = monitor,
        ).also { this.collectors = collectors }
    }

    /* ───────────────────────── 若干 TODO 占位函数 ───────────────────────── */
    // 这些函数可直接 copy 你之前实现；这里仅给最小 stub，保证编译。

    private fun showMetrics() {
        val theme   = currentContext.theme
        val metrics = UsefulMetrics.metrics

        val xmx   = metrics.getMemSize(metrics.jvmMemoryMax) ?: -1
        val free  = metrics.getMemSize(metrics.freePhysicalSize) ?: -1

        val xmxInfo = theme.info("Xmx:${metrics.getMemFmt(metrics.jvmMemoryMax)}")
        val hwInfo  = "free:${metrics.getMemFmt(metrics.freePhysicalSize)}G"

        when {
            xmx < 0 || free < 0 -> logger.info { theme.danger("Xmx:$xmx / free:$free") }
            xmx > free + 400 * 1024 * 1024 ->
                logger.warn { "$xmxInfo (warning: greater than remaining virtual memory!)" }
            xmx < free * 0.74 ->
                logger.warn { "$xmxInfo (warning: memory restriction too strict!)" }
            else -> logger.info { xmxInfo }
        }

        logger.info { "Hardware Info: $xmxInfo $hwInfo" }
    }
    private fun postCheck() { /* TODO: 实现 */ }
    private fun checkOutputDir() { /* TODO: 实现 */ }
    private fun createAnchorPointFile() {
        if (checkerInfoCompareOptions != null) return   // 不产生 anchor
        val anchor = output.resolve("corax.anchor").toFile()
        if (!anchor.exists) anchor.mkdirs()
        anchor.writeText("nothing")
        anchorPointFile = anchor
    }
    private fun top(monitor: MetricsMonitor) {
        val logFile = output.resolve("top.log").path
        val writer  = Files.newBufferedWriter(
            logFile,
            Charsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND,
            StandardOpenOption.WRITE
        )

        writer.appendLine("${LocalDateTime.now()}: ---- start ----")
        writer.appendLine("${LocalDateTime.now()}: PID ${ProcessHandle.current().pid()}")
        writer.flush()

        val timer = CustomRepeatingTimer(1000L, true) {
            val p = ProcessInfoView.globalProcessInfo
            writer.appendLine("${LocalDateTime.now()}: ${p.processInfoText} ${p.cpuLoadText}")
            writer.flush()
        }.apply { start() }

        /* 在任务结束时停止并收尾 */
        monitor.addAnalyzeFinishHook(Thread {
            timer.stop()
            writer.appendLine("${LocalDateTime.now()}: ---- finish ----")
            writer.flush()
            writer.close()
        })
    }

    private fun checkEnv(locator: ProjectFileLocator) {
        // 1. 防止 output 在被扫描目录内
        locator.findFromFileIndexMap(listOf("corax.anchor"), CompareMode.Path).forEach {
            error("Output dir (${output}) is inside scan resources ($it)")
        }

        // 2. output 目录必须为空
        if (checkerInfoCompareOptions == null && output.listPathEntries().isNotEmpty()) {
            error("Output dir ($output) is not empty.")
        }
    }
    private suspend fun getFilteredJavaSourceFiles(
        main: MainConfig,
        locator: ProjectFileLocator,
    ): Set<IResFile> = coroutineScope {
        val exts = Resource.javaExtensions
        exts.flatMap { ext ->
            locator.getByFileExtension(ext)   // suspend fun
                .filter { file ->
                    if (!main.autoAppSrcInZipScheme && !file.isFileScheme) return@filter false
                    main.scanFilter.actionOf(file.path) != ProcessRule.ScanAction.Skip
                }
                .toList()
        }.toSet()
    }
    private fun addFilesToDataBase(main: MainConfig, files: Set<IResFile>) {
        ResultCollector.newSqliteDiagnostics(main).use { diag ->
            files.forEach(diag::createFileXCachedFromFile)
        }
    }
    private suspend fun runCodeMetrics(main: MainConfig, src: Set<IResFile>) = coroutineScope {
        val fileList = main.outputDir.resolve("tmp/code-metrics-file-list.txt").toFile()
        val csvOut   = main.outputDir.resolve("metrics/metrics.csv").toFile()

        // 写源文件清单
        fileList.writeText(src.joinToString("\n") { it.expandRes(main.outputDir).toString() })
        sqliteFileIndexes += src

        val cmd = constructPmdAnalyzerCmd(fileList, csvOut) ?: return@coroutineScope
        logger.debug { "PMD command line: ${cmd.joinToString(" ")}" }

        withContext(Dispatchers.IO) {
            try {
                ProcessBuilder(cmd)
                    .redirectErrorStream(true)
                    .start()
                    .inputStream.bufferedReader().useLines { lineSeq ->
                        lineSeq.forEach { logger.debug { "[pmd] $it" } }
                    }
            } catch (e: Exception) {
                logger.error(e) { "Error when running PMD." }
            }
        }
    }
    private fun constructPmdAnalyzerCmd(
        fileList: IResFile,
        csvResult: IResFile,
    ): List<String>? {
        val java = OS.javaExecutableFilePath ?: run {
            logger.warn { "Java executable path is null" }; return null
        }

        val pmdHome = OS.jarBinPath?.parent?.parent?.resolve("pmd") ?: run {
            logger.warn { "PMD dir not found near binary" }; return null
        }
        if (!pmdHome.exists() || !pmdHome.isDirectory) {
            logger.warn { "PMD dir $pmdHome not exists or not directory." }
            return null
        }

        val classpath = listOf(
            pmdHome.resolve("conf").pathString,
            "${pmdHome.resolve("lib").pathString}${File.separator}*"
        ).joinToString(File.pathSeparator)

        return buildList {
            add(java)
            addAll(ManagementFactory.getRuntimeMXBean().inputArguments) // 继承 JVM args
            add("-classpath"); add(classpath)
            add("net.sourceforge.pmd.cli.PmdCli")
            addAll(
                arrayOf(
                    "check", "--no-cache", "--threads", "8",
                    "-R", pmdHome.resolve("conf/corax-rule.xml").pathString,
                    "-f", "coraxcsv",
                    "-r", csvResult.pathString,
                    "--file-list", fileList.pathString
                )
            )
        }
    }
    /* ───────────────────────── runAnalyze （示范简化版） ───────────────────────── */
    private suspend fun runAnalyze(target: TargetOptions, m: MetricsMonitor) = coroutineScope {
        /* 0. 全局清理 */
        AnalysisCache.G.clear()
        Resource.clean()
        FileSystemCacheLocator.clear()
        SootUtils.cleanUp()

        /* 1. 日志等级 / metrics / top 监控 */
        setVerbosity(verbosity)
        showMetrics()
        if (!disableTop) top(monitor)

        /* 2. 构造 MainConfig & ProjectFileLocator */
        val main = MainConfig(sourceEncoding, monitor).also {
            configureMainConfig(it, monitor)
            target.configureMainConfig(it)          // 平台专有
        }
        val locator = ProjectFileLocator(
            monitor   = monitor,
            roots     = main.processDir + main.autoAppClasses,
            outputDir = main.outputDir,
            mode      = main.traverseMode,
        ).apply { update() }

        /* 3. 环境校验 & 前置文件 */
        checkEnv(locator)
        createAnchorPointFile()

        /* 4. Soot 初始化 + 结果收集器 */
        val sootCtx  = SootCtx(main).also { target.initSoot(it, locator) }
        val info     = SootInfoCache()             // TODO 替换为你真正的 factory
        val result   = getResultCollector(info, locator, main, monitor)
        lastResult   = result

        /* 5. 任务调度器 */
        val runner   = AnalyzeTaskRunner(main.parallelsNum, sootCtx, monitor)

        // 注册任务示例（可按需添加 BuiltinAnalysis / DataFlow 等）
        runner.registerAnalysis("entryPoints", target.getProvider(sootCtx, locator)) { env ->
            env.task()       // TODO
        }

        /* 6. 真正执行 */
        runner.run()

        /* 7. 收尾：写结果 / 追加 metrics */
        monitor.runAnalyzeFinishHook()
        logger.info { "Output: ${main.outputDir.absolute.normalize()}" }
        logger.info { "Analysis completed in ${monitor.prettyDuration()}" }
    }

    /* ───────────────────────── Clikt 入口 ───────────────────────── */
    override fun run() {
        /* 1. 全局超时 */
        if (timeout > 0) CustomRepeatingTimer(timeout * 1000L, true) {
            logger.warn { "Analysis timeout after $timeout s" }
            exitProcess(700)
        }.start()

        /* 2. 参数检查 & 输出目录 */
        checkOutputDir()
        postCheck()              // 前面已实现
        printOptions()

        /* 3. 监控器 */
        val monitor = MetricsMonitor().apply { start() }

        /* 4. 调用挂起接口 */
        runBlocking(
            CoroutineName("corax-main") +
                    Dispatchers.Default.limitedParallelism(maxThreadNum)
        ) {
            runAnalyze(target ?: error("--target required"), monitor)
        }

        /* 5. 结束提示 */
        logger.info { "log files: ${AnalyzerEnv.lastLogFile}" }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = FySastCli().main(args.toList())
    }

    /* 提供 List<String> 版 main，方便 Gradle tests */
    fun FySastCli.main(argv: List<String>) {
        OS.args = argv.toTypedArray()
        this.parse(argv.toTypedArray())
    }
}

/** 环境 / System property 键名 */
const val defaultConfigPathName = "CORAX_CONFIG_DEFAULT_DIR"

/**
 * 默认配置目录：
 * 先读 `env[CORAX_CONFIG_DEFAULT_DIR]`，否则读 `System.getProperty(...)`。
 * 读取不到时返回 `null`。
 */
val defaultConfigDir: String?
    get() = System.getenv(defaultConfigPathName)
        ?: System.getProperty(defaultConfigPathName)

/** `.corax/anchor_point` 相对路径（操作系统无关分隔符） */
internal val ANCHOR_POINT_FILE = ".corax${File.separator}anchor_point"

/** 检测器名称映射文件 */
const val MAPPING_FILE_NAME = "checker_name_mapping.json"