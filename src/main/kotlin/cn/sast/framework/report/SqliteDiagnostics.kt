package cn.sast.framework.report

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.TransactionWithoutReturn
import cn.sast.api.config.CheckerInfoGenResult
import cn.sast.api.config.ExtSettings
import cn.sast.api.config.MainConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.CheckType2StringKind
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.api.report.Report
import cn.sast.api.util.PhaseIntervalTimerKt
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import cn.sast.framework.metrics.MetricsMonitor
import cn.sast.framework.metrics.MetricsMonitorKt
import cn.sast.framework.report.FileX.ID
import cn.sast.framework.report.ReportConsumer.MetaData
import cn.sast.framework.report.metadata.AnalysisMetadata
import cn.sast.framework.report.metadata.Analyzer
import cn.sast.framework.report.metadata.AnalyzerStatistics
import cn.sast.framework.report.metadata.Tool
import cn.sast.framework.report.sqldelight.AnalyzerResultFile
import cn.sast.framework.report.sqldelight.ControlFlow
import cn.sast.framework.report.sqldelight.ControlFlowPath
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.Diagnostic
import cn.sast.framework.report.sqldelight.File
import cn.sast.framework.report.sqldelight.Note
import cn.sast.framework.report.sqldelight.NotePath
import cn.sast.framework.report.sqldelight.Rule
import cn.sast.framework.result.OutputType
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.cache.analysis.SootLineToMethodMapFactory
import com.feysh.corax.cache.analysis.SootMethodAndRange
import com.feysh.corax.commons.NullableLateinit
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.github.javaparser.ast.body.BodyDeclaration
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import java.util.Map.Entry
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.optionals.OptionalsKt
import kotlin.time.Duration
import kotlin.time.DurationKt
import kotlin.time.DurationUnit
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExecutorsKt
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import mu.KLogger
import org.utbot.common.StringUtilKt

@SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,813:1\n381#2,7:814\n381#2,7:821\n1#3:828\n1#3:830\n1#3:834\n59#4:829\n57#4:831\n60#4:832\n59#4:833\n57#4,2:835\n1279#5,2:837\n1293#5,4:839\n1863#5,2:843\n1279#5,2:845\n1293#5,4:847\n1863#5,2:851\n1863#5:853\n1863#5,2:854\n1864#5:856\n1863#5,2:857\n1863#5,2:859\n1628#5,3:861\n1863#5,2:864\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics\n*L\n365#1:814,7\n379#1:821,7\n465#1:830\n474#1:834\n465#1:829\n465#1:831\n474#1:832\n474#1:833\n474#1:835,2\n573#1:837,2\n573#1:839,4\n593#1:843,2\n600#1:845,2\n600#1:847,4\n624#1:851,2\n708#1:853\n709#1:854,2\n708#1:856\n755#1:857,2\n762#1:859,2\n770#1:861,3\n738#1:864,2\n*E\n"])
open class SqliteDiagnostics(
    val mainConfig: MainConfig,
    val info: SootInfoCache?,
    outputDir: IResDirectory,
    val monitor: MetricsMonitor?,
    type: OutputType = OutputType.SQLITE
) : ReportConsumer(type, outputDir), IFileReportConsumer, IMetadataVisitor {
    private val _sdb = NullableLateinit<SQLiteDB>("SQLiteDB is not initialized yet")
    private var sqLiteDB: SQLiteDB by _sdb

    private val database: Database
        get() = sqLiteDB.database

    private val sqliteReportDb: IResFile = mainConfig.sqlite_report_db

    val metadata: MetaData
        get() = MetaData("CoraxJava sqlite report", "1.0", "CoraxJava")

    private val writeDispatcher: ExecutorCoroutineDispatcher = ExecutorsKt.from(Executors.newSingleThreadExecutor())
    private var ruleAndRuleMapping: RuleAndRuleMapping? = null
    private val fileIdMap: MutableMap<IResFile, FileID> = Collections.synchronizedMap(LinkedHashMap())
    private val fileCache: LoadingCache<IResFile, Optional<FileX>> = Caffeine.newBuilder()
        .initialCapacity(1000)
        .softValues()
        .build(object : CacheLoader<IResFile, Optional<FileX>> {
            override fun load(absFile: IResFile): Optional<FileX> {
                return try {
                    Optional.ofNullable(createFile(absFile))
                } catch (e: Exception) {
                    logger.warn { "Failed to read file: $absFile, e: ${e.message}" }
                    logger.debug(e) { "Failed to read file: $absFile, e: ${e.message}" }
                    Optional.empty()
                }
            }
        })

    private val noteHashIdAutoIncrement: MutableMap<String, Long> = HashMap(1000)
    private val ctrlFlowHashIdAutoIncrement: MutableMap<String, Long> = HashMap(1000)

    open val sourceEncoding: Charset
        get() = Charsets.UTF_8

    private val CheckType.id: String
        get() = CheckType2StringKind.checkType2StringKind.convert(this)

    private val Report.associateChecker: Rule?
        get() = ruleAndRuleMapping?.id2checkerMap?.get(check_name)

    override suspend fun init() {
        // TODO: Implement suspend init logic
    }

    fun open(journalMode: String = ExtSettings.INSTANCE.sqliteJournalMode) {
        sqliteReportDb.mkdirs()
        sqLiteDB = Companion.openDataBase(sqliteReportDb.pathString, journalMode)
        sqLiteDB.createSchema()
    }

    private fun createRuleAndRuleMapping() {
        val ruleSortYaml = mainConfig.rule_sort_yaml
        val checkerInfo = mainConfig.checkerInfo?.value as? CheckerInfoGenResult

        if (ruleSortYaml != null && checkerInfo != null) {
            ruleAndRuleMapping = RuleAndRuleMapping(checkerInfo, ruleSortYaml.path).also {
                it.insert(database)
            }
        } else {
            logger.warn { "rule_sort.yaml is not exists" }
        }
    }

    private fun ExecutableQuery<*>.verify(name: String) {
        val it = executeAsList()
        if (it.isNotEmpty()) {
            logger.error { "reference of $name: $it is not exists in the parent table" }
        }
    }

    fun verify() {
        database.run {
            ruleMappingQueries.verify_rule_name().verify("RuleMapping.rule_name")
            diagnosticQueries.verify_rule_name().verify("diagnostic.rule_name")
            diagnosticQueries.verify_file().verify("diagnostic.__file_id")
            diagnosticQueries.verify_note_path().verify("diagnostic.__note_array_hash_id")
            diagnosticQueries.verify_control_flow_path().verify("diagnostic.__control_flow_array_hash_id")
            diagnosticQueries.verify_macro().verify("diagnostic.__macro_note_set_hash_id")
            notePathQueries.verify_note().verify("NotePath.__note_id")
            controlFlowPathQueries.verify_control_flow().verify("ControlFlowPath.__control_flow_id")
            macroExpansionQueries.verify_note().verify("MacroExpansion.__macro_note_id")
            controlFlowQueries.verify_file().verify("ControlFlow.__file_id")
            noteQueries.verify_file().verify("Note.__file_id")
            analyzeResultFileQueries.verify_file().verify("AnalyzeResultFile.__file_id")
            absoluteFilePathQueries.verify_absolute_file_path().verify("AbsoluteFilePath.__file_id")
        }
    }

    override fun close() {
        verify()
        sqLiteDB.close()
        _sdb.uninitialized()
        noteHashIdAutoIncrement.clear()
        ctrlFlowHashIdAutoIncrement.clear()
        fileCache.cleanUp()
        fileIdMap.clear()
    }

    override suspend fun flush(reports: List<Report>, filename: String, locator: IProjectFileLocator) {
        // TODO: Implement suspend flush logic
    }

    override fun visit(analysisMetadata: AnalysisMetadata) {
        if (monitor != null) {
            database.transaction {
                createAnalyzerStatistics(analysisMetadata, monitor)
            }
        }
    }

    private suspend fun serializeReportsToDb(reports: List<Report>, locator: IProjectFileLocator, filename: String) {
        BuildersKt.withContext(writeDispatcher) {
            database.transaction {
                reports.forEach { report ->
                    createDiagnostic(locator, report)
                }
            }
        }
    }

    private fun createFileCached(locator: IProjectFileLocator, file: IBugResInfo): FileID? {
        val absFile = locator.get(file, EmptyWrapperFileGenerator.INSTANCE)?.absolute?.normalize ?: return null
        
        return fileIdMap.getOrPut(absFile) {
            fileCache.get(absFile).orNull?.let { fileX ->
                FileID(fileX.insert(database).id, fileX.associateAbsFile)
            } ?: return null
        }
    }

    private fun createFileXCached(locator: IProjectFileLocator, file: IBugResInfo): ID? {
        val absFile = locator.get(file, EmptyWrapperFileGenerator.INSTANCE)?.absolute?.normalize ?: return null
        return createFileXCachedFromAbsFile(absFile)
    }

    private fun createFileXCachedFromAbsFile(absFile: IResFile): ID? {
        val fileX = fileCache.get(absFile).orNull ?: return null
        return fileIdMap.getOrPut(absFile) {
            FileID(fileX.insert(database).id, fileX.associateAbsFile)
        }.withId(fileIdMap[absFile]?.id ?: return null)
    }

    fun createFileXCachedFromFile(file: IResFile): ID? {
        return createFileXCachedFromAbsFile(file.absolute.normalize)
    }

    private fun createFile(absFile: IResFile): FileX {
        val bytesContent = ResourceKt.readAllBytes(absFile)
        val hash = ResourceImplKt.calculate(bytesContent, "sha256")
        val encoding = getSourceEncoding(absFile)
        val lines = String(bytesContent, encoding).lines()
        val relativePath = mainConfig.tryGetRelativePathFromAbsolutePath(Resource.originFileFromExpandAbsPath(absFile))
        val relativePathStr = relativePath.relativePath.removePrefix("/")
        
        return FileX(
            File(
                id = -1L,
                hash = hash,
                relative_path = relativePathStr,
                line_count = lines.size.toLong(),
                encoding = encoding.name.lowercase(Locale.getDefault()),
                byte_count = bytesContent.size.toLong(),
                content = bytesContent
            ),
            relativePath,
            absFile,
            lines
        )
    }

    private fun FileX.lineContent(line: Int): String? {
        return lines.getOrNull(line - 1)?.takeIf { it.length <= 384 }?.substring(0..384)
    }

    private fun createRegion(locator: IProjectFileLocator, res: IBugResInfo, region: Region): ValueWithId<cn.sast.framework.report.sqldelight.Region>? {
        val fileId = createFileCached(locator, res) ?: return null
        
        val regionObj = cn.sast.framework.report.sqldelight.Region(
            id = 0L,
            __file_id = fileId.id,
            start_line = region.startLine,
            start_column = region.startColumn.toLong(),
            end_line = region.endLine.toLong(),
            end_column = region.endColumn.toLong()
        )
        
        database.regionQueries.insert(
            regionObj.__file_id,
            regionObj.start_line,
            regionObj.start_column,
            regionObj.end_line,
            regionObj.end_column
        )
        
        val id = database.regionQueries.id(
            regionObj.__file_id,
            regionObj.start_line,
            regionObj.start_column,
            regionObj.end_line,
            regionObj.end_column
        ).executeAsList().first().toLong()
        
        return ValueWithId(id, regionObj)
    }

    private fun getFuncRange(classInfo: IBugResInfo, line: Int): Region? {
        if (classInfo is ClassResInfo) {
            info?.getMemberAtLine(classInfo.sc, line)?.range?.let { range ->
                return Region.Companion.invoke_Optional_Range(range)
            }

            SootLineToMethodMapFactory.getSootMethodAtLine(classInfo.sc, line, false)?.range?.let { (start, end) ->
                return Region(start.toInt() - 1, 0, end.toInt() + 1, 0).takeIf { it.startLine >= 0 }
            }
        }
        return null
    }

    private fun createNote(locator: IProjectFileLocator, event: BugPathEvent): ValueWithId<Note>? {
        val fileId = createFileCached(locator, event.classname) ?: return null
        
        val region = event.region
        val funcRange = if (region.startColumn >= 0 && region.endColumn >= 0 && region.startLine >= 0) region else null
        val noticesRegion = funcRange?.let { createRegion(locator, event.classname, it) }
        val funcRegion = getFuncRange(event.classname, event.region.startLine)?.let { createRegion(locator, event.classname, it) }
        
        val note = Note(
            id = 0L,
            kind = "event",
            display_hint = "Below",
            __file_id = fileId.id,
            file_abs_path = fileId.fileAbsPath,
            line = event.region.startLine.toLong(),
            column = event.region.startColumn.toLong(),
            message_en = event.message[Language.EN] ?: "",
            message_zh = event.message[Language.ZH] ?: "",
            __notices_region_id = noticesRegion?.id,
            __func_region_id = funcRegion?.id
        )
        
        database.noteQueries.insert(
            note.kind,
            note.display_hint,
            note.__file_id,
            note.file_abs_path,
            note.line,
            note.column,
            note.message_en,
            note.message_zh,
            note.__notices_region_id,
            note.__func_region_id
        )
        
        val id = database.noteQueries.id(
            note.kind,
            note.display_hint,
            note.__file_id,
            note.file_abs_path,
            note.line,
            note.column,
            note.message_en,
            note.message_zh,
            note.__notices_region_id,
            note.__func_region_id
        ).executeAsList().first().toLong()
        
        return ValueWithId(id, note)
    }

    private fun createControlFlow(locator: IProjectFileLocator, event: BugPathEvent): ValueWithId<ControlFlow>? {
        return null
    }

    private fun createControlFlowPath(locator: IProjectFileLocator, path: MutableList<BugPathEvent>): Long? {
        val path2controlFlow = path.associateWith { createControlFlow(locator, it) }
        val arrayHashIdG = ArrayHashIdGenerator(ctrlFlowHashIdAutoIncrement)
        var sequence = 0L
        val array = mutableListOf<ControlFlowPath>()
        
        path2controlFlow.values.forEach { controlFlow ->
            controlFlow?.let {
                val controlFlowPath = ControlFlowPath(
                    id = -1L,
                    control_flow_sequence = sequence++,
                    __control_flow_id = it.id
                )
                array.add(controlFlowPath)
                arrayHashIdG.array.apply {
                    add(controlFlowPath.__control_flow_id.toString())
                    add(controlFlowPath.control_flow_sequence.toString())
                }
            }
        }
        
        return arrayHashIdG.arrayId?.also { arrayId ->
            array.forEach {
                database.controlFlowPathQueries.insert(it.copy(id = arrayId))
            }
        }
    }

    private fun createNotePath(locator: IProjectFileLocator, pathEvents: MutableList<BugPathEvent>): Long? {
        val event2note = pathEvents.associateWith { createNote(locator, it) }
        val arrayHashIdG = ArrayHashIdGenerator(noteHashIdAutoIncrement)
        var sequence = 0L
        val array = mutableListOf<NotePath>()
        
        event2note.forEach { (event, note) ->
            note?.let {
                val notePath = NotePath(
                    id = -1L,
                    note_sequence = sequence++,
                    note_stack_depth = event.stackDepth?.toLong(),
                    note_is_key_event = 0L,
                    __note_id = it.id
                )
                array.add(notePath)
                arrayHashIdG.array.apply {
                    add(notePath.note_sequence.toString())
                    add(notePath.note_stack_depth?.toString() ?: "")
                    add(notePath.note_is_key_event.toString())
                    add(notePath.__note_id.toString())
                }
            }
        }
        
        return arrayHashIdG.arrayId?.also { arrayId ->
            array.forEach {
                database.notePathQueries.insert(it.copy(id = arrayId))
            }
        }
    }

    private fun createDiagnostic(locator: IProjectFileLocator, report: Report): ValueWithId<Diagnostic>? {
        val fileId = createFileXCached(locator, report.bugResFile) ?: return null
        
        if (report.pathEvents.isEmpty()) {
            logger.error { "Report.pathEvents is empty! report: $report" }
            return null
        }
        
        val lastPathEvents = report.pathEvents.last()
        val noteArrayHashId = createNotePath(locator, report.pathEvents) ?: run {
            logger.error { "invalid report: $report" }
            return null
        }
        
        val controlFlowArrayHashId = createControlFlowPath(locator, report.notes)
        val line = lastPathEvents.region.startLine
        
        val diagnostic = Diagnostic(
            id = 0L,
            rule_name = report.check_name,
            _rule_short_description_zh = associateChecker?.short_description_zh,
            __file_id = fileId.id,
            file_abs_path = fileId.file.fileAbsPath,
            line = line.toLong(),
            column = lastPathEvents.region.startColumn.toLong(),
            message_en = report.message[Language.EN] ?: "",
            message_zh = report.message[Language.ZH] ?: "",
            severity = report.severity ?: associateChecker?.severity ?: "None",
            precision = null,
            likelihood = null,
            impact = null,
            technique = null,
            analysis_scope = null,
            line_content = lineContent(fileId.file, line),
            __note_array_hash_id = noteArrayHashId,
            __control_flow_array_hash_id = controlFlowArrayHashId,
            __macro_note_set_hash_id = null
        )
        
        database.diagnosticQueries.insert(
            diagnostic.rule_name,
            diagnostic._rule_short_description_zh,
            diagnostic.__file_id,
            diagnostic.file_abs_path,
            diagnostic.line,
            diagnostic.column,
            diagnostic.message_en,
            diagnostic.message_zh,
            diagnostic.severity,
            diagnostic.precision,
            diagnostic.likelihood,
            diagnostic.impact,
            diagnostic.technique,
            diagnostic.analysis_scope,
            diagnostic.line_content,
            diagnostic.__note_array_hash_id,
            diagnostic.__control_flow_array_hash_id,
            diagnostic.__macro_note_set_hash_id
        )
        
        val id = database.diagnosticQueries.id(
            diagnostic.rule_name,
            diagnostic._rule_short_description_zh,
            diagnostic.__file_id,
            diagnostic.file_abs_path,
            diagnostic.severity,
            diagnostic.precision,
            diagnostic.likelihood,
            diagnostic.impact,
            diagnostic.technique,
            diagnostic.analysis_scope,
            diagnostic.line_content,
            diagnostic.__note_array_hash_id,
            diagnostic.__control_flow_array_hash_id,
            diagnostic.__macro_note_set_hash_id
        ).executeAsList().first().toLong()
        
        return ValueWithId(id, diagnostic)
    }

    private fun getAnalyzerStatisticsSet(tools: List<Tool>): MutableSet<AnalyzerStatistics> {
        return tools.flatMap { it.analyzers }.map { it.analyzerStatistics }.toMutableSet()
    }

    private fun createAnalyzerResultFile(file: IResFile, fileName: String? = null): AnalyzerResultFile? {
        val absFile = file.absolute.normalize
        val fileId = createFileXCachedFromAbsFile(absFile) ?: return null
        
        val analyzerResultFile = AnalyzerResultFile(
            fileName ?: mainConfig.tryGetRelativePathFromAbsolutePath(absFile).relativePath.removePrefix("/"),
            absFile.pathString,
            fileId.id
        )
        
        database.analyzeResultFileQueries.insert(analyzerResultFile)
        return analyzerResultFile
    }

    fun writeAnalyzerResultFiles() {
        database.transaction {
            mainConfig.output_dir.walk().forEach { path ->
                if (path is IResFile && path.exists()) {
                    createAnalyzerResultFile(path)
                }
            }
        }
    }

    private fun createAnalyzerStatistics(data: AnalysisMetadata, monitor: MetricsMonitor): cn.sast.framework.report.sqldelight.AnalyzerStatistics {
        val tools = data.tools
        val analyzerStatisticsSet = getAnalyzerStatisticsSet(tools)
        
        var failed = 0
        var successful = 0
        val failedSources = LinkedHashSet<String>()
        val successfulSources = LinkedHashSet<String>()
        
        analyzerStatisticsSet.forEach { stats ->
            failed += stats.failed
            failedSources.addAll(stats.failedSources)
            successful += stats.successful
            successfulSources.addAll(stats.successfulSources)
        }
        
        val commands = tools.flatMap { it.command }
        val toolWorkingDirs = tools.joinToString(",") { it.workingDirectory }
        val toolOutputPaths = tools.joinToString(",") { it.outputPath }
        val toolProjectRoots = tools.joinToString(",") { it.projectRoot }
        
        val beginMillis = monitor.beginMillis
        val endTimestamp = System.currentTimeMillis()
        val elapsed = MetricsMonitorKt.timeSub(PhaseIntervalTimerKt.currentNanoTime(), monitor.beginNanoTime)
        
        val enableCheckTypes = mainConfig.saConfig?.enableCheckTypes?.map { it.id }?.toSet() ?: emptySet()
        
        val checkerInfo = mainConfig.checkerInfo?.value as? CheckerInfoGenResult
        val existsCheckerIds = checkerInfo?.existsCheckerIds ?: emptyList()
        
        val analyzerStats = cn.sast.framework.report.sqldelight.AnalyzerStatistics(
            analyzer_name = "Corax",
            analyzer_version = "",
            analyzer_version_full = mainConfig.version ?: "None",
            begin_time = MetricsMonitorKt.getDateStringFromMillis(beginMillis),
            begin_timestamp = beginMillis,
            elapsed_seconds = PhaseIntervalTimerKt.nanoTimeInSeconds(elapsed),
            elapsed_time = elapsed?.let { DurationKt.toDuration(it, DurationUnit.NANOSECONDS).toString() } ?: "",
            end_time = MetricsMonitorKt.getDateStringFromMillis(endTimestamp),
            end_timestamp = endTimestamp,
            file_count = data.fileCount,
            line_count = data.lineCount,
            covered = data.codeCoverage.covered.toLong(),
            missed = data.codeCoverage.missed.toLong(),
            report_dir_count = data.numOfReportDir.toLong(),
            source_paths = data.sourcePaths.joinToString(","),
            os_name = data.osName,
            commands = Json.encodeToString(ArrayListSerializer(StringSerializer.INSTANCE), commands),
            working_dirs = toolWorkingDirs,
            output_paths = toolOutputPaths,
            project_roots = toolProjectRoots,
            compiler = "",
            enabled_check_types = enableCheckTypes.joinToString(","),
            disabled_check_types = (existsCheckerIds - enableCheckTypes).joinToString(","),
            failed_sources = failedSources.joinToString(","),
            failed_count = failed.toLong(),
            successful_sources = successfulSources.joinToString(","),
            successful_count = successful.toLong(),
            note = "",
            id = 1L
        )
        
        database.analyzerStatisticsQueries.insert(analyzerStats)
        return analyzerStats
    }

    @SourceDebugExtension(["SMAP\nSqliteDiagnostics.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics$ArrayHashIdGenerator\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,813:1\n381#2,7:814\n*S KotlinDebug\n*F\n+ 1 SqliteDiagnostics.kt\ncn/sast/framework/report/SqliteDiagnostics$ArrayHashIdGenerator\n*L\n566#1:814,7\n*E\n"])
    class ArrayHashIdGenerator(private val hashIdAutoIncrement: MutableMap<String, Long>) {
        val array = mutableListOf<String>()

        private val md5: String
            get() {
                val digest = StringUtilKt.getMd5()
                val bytes = array.joinToString(",").toByteArray(Charsets.UTF_8)
                return BigInteger(1, digest.digest(bytes)).toString(16)
            }

        val arrayId: Long?
            get() = synchronized(hashIdAutoIncrement) {
                if (array.isEmpty()) {
                    null
                } else {
                    val id = hashIdAutoIncrement.size + 1L
                    hashIdAutoIncrement.getOrPut(md5) { id }
                }
            }
    }

    companion object {
        private val logger: KLogger = TODO("Initialize logger")
        private val jsonFormat = Json {
            useArrayPolymorphism = true
            prettyPrint = true
            encodeDefaults = false
        }

        fun openDataBase(fullPath: String, journalMode: String = ExtSettings.INSTANCE.sqliteJournalMode): SQLiteDB {
            return SQLiteDB.Companion.openDataBase(fullPath, journalMode)
        }
    }
}