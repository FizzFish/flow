package cn.sast.framework.validator

import cn.sast.api.config.MainConfig
import cn.sast.api.config.ScanFilter
import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ExpectBugAnnotationData
import cn.sast.api.report.Report
import cn.sast.api.report.ReportKt
import cn.sast.common.FileSystemLocator
import cn.sast.common.GLB
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.common.ResourceKt
import cn.sast.framework.report.IProjectFileLocator
import cn.sast.framework.report.NullWrapperFileGenerator
import cn.sast.framework.report.ProjectFileLocator
import cn.sast.framework.result.ResultCollector
import com.feysh.corax.config.api.AIAnalysisApiKt
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.IChecker
import com.feysh.corax.config.api.IRule
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.FileMatch
import com.github.doyaaaaaken.kotlincsv.client.CsvFileWriter
import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import com.github.doyaaaaaken.kotlincsv.dsl.CsvWriterDslKt
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Writer
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlinx.coroutines.AwaitKt
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.SerializersKt
import mu.KLogger
import mu.KotlinLogging
import java.util.Comparator
import kotlin.comparisons.compareValues

@SourceDebugExtension(["SMAP\nPrecisionMeasurement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrecisionMeasurement.kt\ncn/sast/framework/validator/AccuracyValidator\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ProcessRule.kt\ncom/feysh/corax/config/api/rules/ProcessRule\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,423:1\n1557#2:424\n1628#2,3:425\n1053#2:429\n24#3:428\n1#4:430\n*S KotlinDebug\n*F\n+ 1 PrecisionMeasurement.kt\ncn/sast/framework/validator/AccuracyValidator\n*L\n86#1:424\n86#1:425,3\n81#1:429\n86#1:428\n*E\n"])
class AccuracyValidator(val mainConfig: MainConfig) {
    private val logger: KLogger = KotlinLogging.logger { "AccuracyValidator" }
    private val extensions: List<String> = ResourceKt.getJavaExtensions() + listOf("yml", "txt", "gradle", "kts", "cnf", "conf", "config", "xml", "properties")

    private object AnnotationLineComparator : Comparator<ExpectBugAnnotationData> {
        override fun compare(a: ExpectBugAnnotationData, b: ExpectBugAnnotationData): Int {
            return compareValues(a.line, b.line)
        }
    }

    private object RowTypeComparator : Comparator<RowType> {
        override fun compare(a: RowType, b: RowType): Int {
            return compareValues(a.toString(), b.toString())
        }
    }

    val str: String
        get() = "\"${
            CollectionsKt.joinToString(
                CollectionsKt.sortedWith(str, AnnotationLineComparator),
                "\n",
                null,
                null,
                0,
                null,
                { bugAnnotationData -> "file: ${mainConfig.tryGetRelativePath(bugAnnotationData.file).relativePath}:${bugAnnotationData.line}:${bugAnnotationData.column} kind: ${bugAnnotationData.kind} a bug: ${bugAnnotationData.bug}" },
                30,
                null
            )}\""

    private val rules: List<FileMatch>
    private val pattern: Pattern

    init {
        val var17 = ProcessRule.INSTANCE
        val var18 = listOf("build", "out", "target", ".idea", ".git")
        val mappedRules = var18.map { "(-)path=/$it/" }
            .map { line ->
                ProcessRule.InlineRuleStringSerialize.INSTANCE
                    .deserializeMatchFromLineString(SerializersKt.serializer(FileMatch::class), line)
            }

        this.rules = mappedRules.toMutableList()
        this.pattern = Pattern.compile("(?<escape>(!?))\\$ *(?<name>((`([^(`\\r\\n)])+`)|([a-zA-Z$_]+[a-zA-Z0-9$_.-]*)))", 8)
    }

    suspend fun makeScore(result: ResultCollector, locator: IProjectFileLocator): Result {
        return BuildersKt.withContext(
            Dispatchers.IO,
            Function2<CoroutineScope, Continuation<Result>, Any> { scope, continuation ->
                object : kotlin.coroutines.jvm.internal.RestrictedSuspendLambda(scope, continuation) {
                    private var L$0: Any? = null
                    private var L$1: Any? = null
                    private var L$2: Any? = null
                    private var L$3: Any? = null
                    private var L$4: Any? = null
                    private var label: Int = 0

                    override fun invokeSuspend(result: Any): Any {
                        when (label) {
                            0 -> {
                                ResultKt.throwOnFailure(result)
                                val projectFileLocator = ProjectFileLocator(
                                    mainConfig.monitor,
                                    mainConfig.sourcePath + mainConfig.processDir + mainConfig.autoAppClasses,
                                    null,
                                    FileSystemLocator.TraverseMode.Default,
                                    false
                                )
                                projectFileLocator.update()
                                val expectedResults = LinkedHashSet<ExpectBugAnnotationData>()
                                val outputDir = extensions
                                val FP = this@AccuracyValidator
                                val TN = ArrayList<Any>()
                                val typeNoAnnotated = outputDir.iterator()
                                label = 1
                                L$0 = projectFileLocator
                                L$1 = expectedResults
                                L$2 = FP
                                L$3 = TN
                                L$4 = typeNoAnnotated
                                val res = projectFileLocator.getByFileExtension(typeNoAnnotated.next(), this)
                                if (res == IntrinsicsKt.getCOROUTINE_SUSPENDED()) return res
                                CollectionsKt.addAll(TN, SequencesKt.map(res as Sequence<*>) { TODO() })
                            }
                            1 -> {
                                val typeNoAnnotated = L$4 as Iterator<*>
                                TN = L$3 as MutableCollection<*>
                                FP = L$2 as AccuracyValidator
                                expectedResults = L$1 as MutableSet<*>
                                projectFileLocator = L$0 as ProjectFileLocator
                                ResultKt.throwOnFailure(result)
                                CollectionsKt.addAll(TN, SequencesKt.map(result as Sequence<*>) { TODO() })
                            }
                            2 -> {
                                expectedResults = L$0 as MutableSet<*>
                                ResultKt.throwOnFailure(result)
                                // Rest of the implementation...
                            }
                            else -> throw IllegalStateException("call to 'resume' before 'invoke' with coroutine")
                        }
                        TODO("Complete implementation")
                    }
                }
            }
        )
    }

    private fun parseFile(file: IResFile): Set<ExpectBugAnnotationData<String>> {
        val res = try {
            String(ResourceKt.readAllBytes(file), Charsets.UTF_8)
        } catch (e: IOException) {
            logger.error("read config file $file failed")
            null
        }

        if (res == null) {
            return emptySet()
        }

        val text = res
        val matcher = pattern.matcher(res)
        val result = LinkedHashSet<ExpectBugAnnotationData<String>>()

        while (matcher.find()) {
            val escape = matcher.group("escape").isNotEmpty()
            val name = StringsKt.removeSuffix(StringsKt.removeSurrounding(matcher.group("name"), "`", "--")
            val startIndex = matcher.start()
            val (start, line, col) = getLineAndColumn(text, startIndex)
            val lineText = text.substring(start)
            val linePrefix = StringsKt.substringBefore(lineText, "\n").substring(0, startIndex - start)

            if (linePrefix.contains("//") || linePrefix.contains("<!--") || file.extension == "properties") {
                val absoluteFile = file.absolute
                val lowerName = name.toLowerCase(Locale.getDefault())
                val annotation = ExpectBugAnnotationData(
                    absoluteFile, line, col, lowerName,
                    if (escape) ExpectBugAnnotationData.Kind.Escape else ExpectBugAnnotationData.Kind.Expect
                )
                logger.trace { annotation }
                result.add(annotation)
            }
        }

        return result
    }

    private fun getLineAndColumn(text: String, index: Int): Triple<Int, Int, Int> {
        var line = 1
        var col = 0
        var start = 0

        for (i in 0 until index) {
            if (text[i] == '\n') {
                line++
                start = i + 1
                col = 0
            } else {
                col++
            }
        }

        return Triple(start, line, col)
    }

    data class Result(
        val TP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
        val FN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
        val TN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
        val FP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>>,
        val allTp: Int,
        val allFn: Int,
        val allTn: Int,
        val allFp: Int,
        val allTotal: Int,
        val allTpr: Float,
        val allFpr: Float,
        val score: Float
    ) {
        override fun toString(): String {
            return "TP: $allTp, FN: $allFn, TN: $allTn, FP: $allFp, allTotal: $allTotal, TPR:${PrecisionMeasurementKt.getFm(allTpr)}, FPR:${PrecisionMeasurementKt.getFm(allFpr)}, Score: ${PrecisionMeasurementKt.getFm(score)}"
        }

        operator fun component1() = TP
        operator fun component2() = FN
        operator fun component3() = TN
        operator fun component4() = FP
        operator fun component5() = allTp
        operator fun component6() = allFn
        operator fun component7() = allTn
        operator fun component8() = allFp
        operator fun component9() = allTotal
        operator fun component10() = allTpr
        operator fun component11() = allFpr
        operator fun component12() = score

        fun copy(
            TP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.TP,
            FN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.FN,
            TN: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.TN,
            FP: Map<RowType, MutableSet<ExpectBugAnnotationData<String>>> = this.FP,
            allTp: Int = this.allTp,
            allFn: Int = this.allFn,
            allTn: Int = this.allTn,
            allFp: Int = this.allFp,
            allTotal: Int = this.allTotal,
            allTpr: Float = this.allTpr,
            allFpr: Float = this.allFpr,
            score: Float = this.score
        ) = Result(TP, FN, TN, FP, allTp, allFn, allTn, allFp, allTotal, allTpr, allFpr, score)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Result) return false

            if (TP != other.TP) return false
            if (FN != other.FN) return false
            if (TN != other.TN) return false
            if (FP != other.FP) return false
            if (allTp != other.allTp) return false
            if (allFn != other.allFn) return false
            if (allTn != other.allTn) return false
            if (allFp != other.allFp) return false
            if (allTotal != other.allTotal) return false
            if (allTpr != other.allTpr) return false
            if (allFpr != other.allFpr) return false
            if (score != other.score) return false

            return true
        }

        override fun hashCode(): Int {
            var result = TP.hashCode()
            result = 31 * result + FN.hashCode()
            result = 31 * result + TN.hashCode()
            result = 31 * result + FP.hashCode()
            result = 31 * result + allTp
            result = 31 * result + allFn
            result = 31 * result + allTn
            result = 31 * result + allFp
            result = 31 * result + allTotal
            result = 31 * result + allTpr.hashCode()
            result = 31 * result + allFpr.hashCode()
            result = 31 * result + score.hashCode()
            return result
        }
    }
}