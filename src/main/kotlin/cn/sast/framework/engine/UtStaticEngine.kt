package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.result.IUTBotResultCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.FileUtil
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
import org.utbot.framework.plugin.services.JdkInfoDefaultProvider
import org.utbot.framework.util.EngineUtilsKt
import org.utbot.framework.util.SootUtilsKt
import soot.Scene
import soot.SootMethod
import soot.options.Options
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 * Static‑analysis wrapper around UTBot that generates and runs symbolic tests.
 */
open class UtStaticEngine(
    val mainConfig: MainConfig,
    val utConfig: UtStaticEngineConfiguration
) {
    var classesToMockAlways: Set<String> = utConfig.classesToMockAlways
        internal set(value) {
            // Pre‑load classes so that UTBot does not fail later with CNFE.
            value.forEach { fqName ->
                try {
                    Class.forName(fqName, false, classLoader)
                } catch (e: ClassNotFoundException) {
                    logger.error(e) { "Unable to pre‑load $fqName" }
                }
            }
            field = value
        }

    val classpath: Set<IResource>
        get() = mainConfig.expand_class_path

    private val useDefaultJavaClassPath: Boolean
        get() = mainConfig.useDefaultJavaClassPath

    val classLoader by lazy {
        val urls = (classpath + mainConfig.processDir).map { it.url }.toTypedArray()
        if (useDefaultJavaClassPath) URLClassLoader(urls, ClassLoader.getSystemClassLoader())
        else URLClassLoader(urls, null)
    }

    val dependencyPaths: String
        get() = System.getProperty("java.class.path")

    fun saveToFile(snippet: String, outputPath: String?) {
        outputPath?.let { Files.write(PathUtil.INSTANCE.toPath(it), listOf(snippet)) }
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
            0,
            null
        )
    }

    fun generateTest(classUnderTest: ClassId, testClassname: String, testCases: List<UtMethodTestSet>): String =
        initializeCodeGenerator(utConfig.testFramework, classUnderTest)
            .generateAsString(testCases, testClassname)

    fun generateTestsForClass(classIdUnderTest: ClassId, testCases: List<UtMethodTestSet>, output: IResource) {
        val testClassName = "${classIdUnderTest.simpleName}Test"
        val testClassBody = generateTest(classIdUnderTest, testClassName, testCases)

        if (logger.isTraceEnabled) logger.info { testClassBody }

        val outputDir = output.file.apply { if (!exists()) mkdirs() }.absolutePath + File.separator
        val packagePath = classIdUnderTest.jvmName.substringBeforeLast('.').replace('.', File.separatorChar)
        val outputPath = Paths.get(outputDir + packagePath).apply { toFile().mkdirs() }
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
            0,
            null
        )
    }

    fun runTest(soot: SootCtx, test: TestCaseGenerator, entries: Set<SootMethod>, result: IUTBotResultCollector) {
        val targetMethods = entries.map { EngineUtilsKt.getExecutableId(it) }

        UtContext.setUtContext(UtContext(classLoader)).use {
            try {
                val mockStrategy = MockStrategyApi.NO_MOCKS
                val classesToMock = (Mocker.defaultSuperClassesToMockAlwaysNames + classesToMockAlways)
                    .map { ClassId(it, null, false) }
                    .toSet()

                logger.info { "symbolic result: ${targetMethods.size}" }
                // TODO: connect UTBot engine and feed result collector
            } catch (e: Exception) {
                logger.error(e) { "UTBot execution failed" }
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
            scene.addBasicClass(clazz.name, Scene.BODIES)
            scene.forceResolve(clazz.name, Scene.BODIES).setApplicationClass()
        }
    }

    suspend fun analyzeSuspend(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) {
        UtSettings.INSTANCE.treatOverflowAsError = false
        val testGenerator = initializeGenerator()
        initUt(soot)

        provider.getIterator().collect { task ->
            val entries = task.entries.toMutableSet().apply {
                task.additionalEntries?.let(::addAll)
            }
            Scene.v().entryPoints = entries.toList()
            soot.constructCallGraph()

            val startTime = LocalDateTime.now()
            var ok = false
            try {
                // TODO: plug UTBot symbolic execution here
                ok = true
            } catch (t: Throwable) {
                logger.error(t) { "Finished (in ${org.utbot.common.LoggingKt.elapsedSecFrom(startTime)}): ${task.name} :: EXCEPTION ::" }
                throw t
            } finally {
                val msg = if (ok) "<Success>" else "<Nothing>"
                logger.info { "Finished (in ${org.utbot.common.LoggingKt.elapsedSecFrom(startTime)}): ${task.name} $msg" }
            }
        }
    }

    fun analyze(soot: SootCtx, provider: IEntryPointProvider, result: IUTBotResultCollector) =
        runBlocking { analyzeSuspend(soot, provider, result) }

    companion object {
        private val logger: KLogger = KotlinLogging.logger(UtStaticEngine::class.java.name)
    }
}
