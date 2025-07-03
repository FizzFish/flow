package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.result.IUTBotResultCollector
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jdk7.AutoCloseableKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.PersistentSet
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.FileUtil
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import org.utbot.common.PathUtil
import org.utbot.engine.Mocker
import org.utbot.framework.UtSettings
import org.utbot.framework.codegen.CodeGenerator
import org.utbot.framework.codegen.domain.DomainKt
import org.utbot.framework.codegen.domain.ForceStaticMocking
import org.utbot.framework.codegen.domain.NoStaticMocking
import org.utbot.framework.plugin.api.ClassId
import org.utbot.framework.plugin.api.MockStrategyApi
import org.utbot.framework.plugin.api.TestCaseGenerator
import org.utbot.framework.plugin.api.TreatOverflowAsError
import org.utbot.framework.plugin.api.UtMethodTestSet
import org.utbot.framework.plugin.api.util.UtContext
import org.utbot.framework.plugin.services.JdkInfo
import org.utbot.framework.plugin.services.JdkInfoDefaultProvider
import org.utbot.framework.util.EngineUtilsKt
import org.utbot.framework.util.SootUtilsKt
import soot.Scene
import soot.SootMethod
import soot.options.Options

@SourceDebugExtension(["SMAP\nUtStaticEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UtStaticEngine.kt\ncn/sast/framework/engine/UtStaticEngine\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 UtContext.kt\norg/utbot/framework/plugin/api/util/UtContextKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,229:1\n1557#2:230\n1628#2,3:231\n1557#2:234\n1628#2,3:235\n1628#2,3:241\n1557#2:251\n1628#2,3:252\n74#3,3:238\n77#3,5:244\n13409#4,2:249\n37#5,2:255\n*S KotlinDebug\n*F\n+ 1 UtStaticEngine.kt\ncn/sast/framework/engine/UtStaticEngine\n*L\n152#1:230\n152#1:231,3\n161#1:234\n161#1:235,3\n167#1:241,3\n81#1:251\n81#1:252,3\n163#1:238,3\n163#1:244,5\n195#1:249,2\n81#1:255,2\n*E\n"])
open class UtStaticEngine(
    val mainConfig: MainConfig,
    val utConfig: UtStaticEngineConfiguration
) {
    var classesToMockAlways: Set<String> = utConfig.classesToMockAlways
        internal set(fullyQualifiedNames) {
            for (fullyQualifiedName in fullyQualifiedNames) {
                try {
                    Class.forName(fullyQualifiedName, false, this.classLoader)
                } catch (e: ClassNotFoundException) {
                    logger.error("", e)
                }
            }
            field = fullyQualifiedNames
        }

    val classpath: Set<IResource>
        get() = mainConfig.expand_class_path

    val useDefaultJavaClassPath: Boolean
        get() = mainConfig.useDefaultJavaClassPath

    val classLoader: URLClassLoader by lazy {
        val urls = (classpath + mainConfig.processDir).map { it.url }.toTypedArray()
        if (useDefaultJavaClassPath) {
            URLClassLoader(urls, ClassLoader.getSystemClassLoader())
        } else {
            URLClassLoader(urls, null)
        }
    }

    val dependencyPaths: String
        get() = System.getProperty("java.class.path")

    fun saveToFile(snippet: String, outputPath: String?) {
        outputPath?.let {
            Files.write(PathUtil.INSTANCE.toPath(it), listOf(snippet))
        }
    }

    private fun initializeCodeGenerator(testFramework: String, classUnderTest: ClassId): CodeGenerator {
        val generateWarningsForStaticMocking = utConfig.forceStaticMocking == ForceStaticMocking.FORCE &&
                utConfig.staticsMocking is NoStaticMocking
        return CodeGenerator(
            classUnderTest,
            null,
            false,
            DomainKt.testFrameworkByName(testFramework),
            null,
            utConfig.staticsMocking,
            utConfig.forceStaticMocking,
            generateWarningsForStaticMocking,
            utConfig.codegenLanguage,
            null,
            null,
            null,
            false,
            null,
            15894,
            null
        )
    }

    fun generateTest(classUnderTest: ClassId, testClassname: String, testCases: List<UtMethodTestSet>): String {
        return initializeCodeGenerator(utConfig.testFramework, classUnderTest)
            .generateAsString(testCases, testClassname)
    }

    fun generateTestsForClass(classIdUnderTest: ClassId, testCases: List<UtMethodTestSet>, output: IResource) {
        val testClassName = "${classIdUnderTest.simpleName}Test"
        val testClassBody = generateTest(classIdUnderTest, testClassName, testCases)
        
        if (logger.isTraceEnabled) {
            logger.info(testClassBody)
        }

        val outputArgAsFile = output.file
        if (!outputArgAsFile.exists()) {
            outputArgAsFile.mkdirs()
        }

        val outputDir = "$outputArgAsFile${File.separator}"
        val packagePath = classIdUnderTest.jvmName.split('.').dropLast(1).joinToString(File.separator)
        val outputPath = Paths.get("$outputDir$packagePath")
        outputPath.toFile().mkdirs()
        saveToFile(testClassBody, "$outputPath${File.separator}$testClassName.java")
    }

    protected fun initializeGenerator(): TestCaseGenerator {
        UtSettings.INSTANCE.treatOverflowAsError = utConfig.treatOverflowAsError == TreatOverflowAsError.AS_ERROR
        val jdkInfo = JdkInfoDefaultProvider().info
        val processDirs = mainConfig.processDir.map { it.path }
        
        return TestCaseGenerator(
            processDirs,
            classpath.joinToString(File.pathSeparator) { it.absolutePath },
            dependencyPaths,
            jdkInfo,
            null,
            null,
            false,
            false,
            112,
            null
        )
    }

    fun runTest(soot: SootCtx, test: TestCaseGenerator, entries: Set<SootMethod>, result: IUTBotResultCollector) {
        val targetMethods = entries.map { EngineUtilsKt.getExecutableId(it) }
        
        UtContext.Companion.setUtContext(UtContext(classLoader)).use { _ ->
            try {
                val mockStrategy = MockStrategyApi.NO_MOCKS
                val classesToMock = (Mocker.Companion.defaultSuperClassesToMockAlwaysNames + classesToMockAlways)
                    .map { ClassId(it, null, false, 6, null) }
                    .toSet()
                
                logger.info { "symbolic result: ${targetMethods.size}" }
            } catch (e: Exception) {
                KotlinLogging.logger("withUtContext").error(e)
                throw e
            }
        }
    }

    fun initUt(soot: SootCtx) {
        UtSettings.INSTANCE.apply {
            useFuzzing = false
            useSandbox = false
            useConcreteExecution = false
            useCustomJavaDocTags = false
            enableSummariesGeneration = false
            checkNpeInNestedNotPrivateMethods = true
            preferredCexOption = false
            useAssembleModelGenerator = false
        }

        val isolatedClassFiles = FileUtil.INSTANCE.isolateClassFiles(
            SootUtilsKt.getClassesToLoad().copyOf()
        ).canonicalPath
        mainConfig.classpath = mainConfig.classpath.add(isolatedClassFiles)

        val scene = Scene.v()
        scene.sootClassPath = null
        val options = Options.v()
        soot.configureSootClassPath(options)

        SootUtilsKt.getClassesToLoad().forEach { clazz ->
            scene.addBasicClass(clazz.name, 3)
            scene.forceResolve(clazz.name, 3).setApplicationClass()
        }
    }

    suspend fun analyzeSuspend(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) {
        UtSettings.INSTANCE.treatOverflowAsError = false
        val test = initializeGenerator()
        initUt(soot)
        
        provider.getIterator().collect(object : FlowCollector<IEntryPointProvider.AnalyzeTask> {
            override suspend fun emit(task: IEntryPointProvider.AnalyzeTask, continuation: Continuation<Unit>): Any? {
                val entries = task.entries.toMutableSet()
                task.additionalEntries?.let { entries.addAll(it) }
                
                Scene.v().entryPoints = entries.toList()
                soot.constructCallGraph()
                
                val startTime = LocalDateTime.now()
                val res = ObjectRef<Maybe<*>>()
                res.element = Maybe.empty()
                
                LoggingKt.info(logger).use { log ->
                    try {
                        log.logMethod.invoke("Started: ${task.name}")
                        // TODO: Add actual analysis logic here
                    } catch (t: Throwable) {
                        log.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): ${task.name} :: EXCEPTION :: ")
                        throw t
                    } finally {
                        if (res.element?.hasValue == true) {
                            log.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): ${task.name}")
                        } else {
                            log.logMethod.invoke("Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): ${task.name} <Nothing>")
                        }
                    }
                }
                return Unit
            }
        })
    }

    fun analyze(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) {
        BuildersKt.runBlocking(null, object : Function2<CoroutineScope, Continuation<Unit>, Any?> {
            override fun invoke(p1: CoroutineScope, p2: Continuation<Unit>): Any? {
                TODO("FIXME - Could not decompile this function")
            }
        })
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger(UtStaticEngine::class.java.name)
    }
}