package cn.sast.framework.compiler

import cn.sast.common.IResource
import java.io.Closeable
import java.io.File
import java.io.PrintWriter
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.collections.immutable.PersistentSet
import mu.KLogger
import org.eclipse.jdt.core.compiler.CompilationProgress
import org.eclipse.jdt.internal.compiler.batch.Main
import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

@SourceDebugExtension(["SMAP\nEcjCompiler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EcjCompiler.kt\ncn/sast/framework/compiler/EcjCompiler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Logging.kt\norg/utbot/common/LoggingKt\n+ 5 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,150:1\n1279#2,2:151\n1293#2,4:153\n1557#2:157\n1628#2,3:158\n1863#2,2:161\n1863#2,2:163\n1#3:165\n49#4,13:166\n62#4,11:181\n37#5,2:179\n*S KotlinDebug\n*F\n+ 1 EcjCompiler.kt\ncn/sast/framework/compiler/EcjCompiler\n*L\n113#1:151,2\n113#1:153,4\n118#1:157\n118#1:158,3\n121#1:161,2\n130#1:163,2\n143#1:166,13\n143#1:181,11\n144#1:179,2\n*E\n"])
class EcjCompiler(
    val sourcePath: PersistentSet<IResource>,
    val classpath: PersistentSet<String>,
    val class_opt: IResource,
    val customOptions: List<String>,
    val useDefaultJava: Boolean,
    outWriter: PrintWriter = PrintWriter(System.out),
    errWriter: PrintWriter = PrintWriter(System.err),
    systemExitWhenFinished: Boolean = false,
    customDefaultOptions: MutableMap<String, String>? = null,
    compilationProgress: CompilationProgress? = null
) : Main(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress) {
    val collectClassPath: MutableSet<String> = LinkedHashSet()
    private val currentClasspathNameHack: MutableSet<String> = LinkedHashSet()

    private fun getDefaultClasspath(): ArrayList<Classpath> {
        return super.handleClasspath(null, null)
    }

    protected open fun addNewEntry(
        paths: ArrayList<Classpath?>?,
        currentClasspathName: String,
        currentRuleSpecs: ArrayList<String?>?,
        customEncoding: String?,
        destPath: String?,
        isSourceOnly: Boolean,
        rejectDestinationPathOnJars: Boolean
    ) {
        currentClasspathNameHack.add(currentClasspathName)
        super.addNewEntry(paths, currentClasspathName, currentRuleSpecs, customEncoding, destPath, isSourceOnly, rejectDestinationPathOnJars)
    }

    private fun getPathsFrom(path: String): ArrayList<Classpath>? {
        currentClasspathNameHack.clear()
        val paths = ArrayList<Classpath>()

        try {
            processPathEntries(4, paths, path, null, false, false)
            return paths
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    private fun replace(ecjClasspathName: String): List<String> {
        val path = getPathsFrom(ecjClasspathName)
        if (currentClasspathNameHack.size == 1 && path != null && path.size == 1) {
            val origClassPathFileName = currentClasspathNameHack.first()
            val res = ArrayList<String>()
            if (origClassPathFileName.isNotEmpty()) {
                val cpf = File(origClassPathFileName)
                if (!cpf.exists()) {
                    val parentPath = cpf.parentFile.toPath()
                    val name = cpf.name
                    Files.newDirectoryStream(parentPath, name).use { stream ->
                        for (subClasspathName in stream) {
                            res.add(ecjClasspathName.replace(origClassPathFileName, subClasspathName.toString(), false))
                            collectClassPath.add(subClasspathName.toString())
                        }
                    }
                }
            }

            collectClassPath.add(ecjClasspathName)
            return listOf(ecjClasspathName)
        } else {
            collectClassPath.add(ecjClasspathName)
            return listOf(ecjClasspathName)
        }
    }

    fun compile(): Boolean {
        val args = ArrayList<String>()
        val result: Boolean
        if (customOptions.isEmpty()) {
            val classpathSet = classpath.toMutableSet()
            args.addAll(listOf("-source", "11", "-target", "11", "-proceedOnError", "-warn:none", "-g:lines,vars,source", "-preserveAllLocals"))
            if (classpathSet.isNotEmpty()) {
                val classpathMap = LinkedHashMap<String, List<String>>(
                    maxOf(16, classpathSet.size.coerceAtLeast(10))
                )

                for (t in classpathSet) {
                    classpathMap[t] = replace(t)
                }

                val classpathList = classpathMap.values.flatten().toMutableList()
                if (useDefaultJava) {
                    val defaultClasspath = getDefaultClasspath().map { it.path }
                    classpathList.addAll(defaultClasspath)
                }

                for (element in classpathList) {
                    args.add("-classpath")
                    args.add(element)
                }
            }

            args.addAll(listOf("-d", class_opt.toString()))
            if (sourcePath.isNotEmpty()) {
                for (resource in sourcePath) {
                    args.add(resource.file.path)
                }
            }

            kLogger.info { "ecj cmd args:\n[ ${args.joinToString("\n")} ]\n" }
        } else {
            if (sourcePath.isNotEmpty()) {
                throw IllegalStateException("sourcePath: $sourcePath must be empty while use customOptions: $customOptions")
            }

            if (classpath.isNotEmpty()) {
                throw IllegalStateException("classpath: $classpath must be empty while use customOptions: $customOptions")
            }

            args.addAll(customOptions)
        }

        val logger = LoggingKt.info(kLogger)
        val msg = "compile java source"
        logger.logMethod.invoke { "Started: $msg" }
        val startTime = LocalDateTime.now()
        var caughtException: Throwable? = null
        val res = ObjectRef<Maybe<*>>()
        res.element = Maybe.empty()

        try {
            result = super.compile(args.toTypedArray(), null, null)
            res.element = Maybe.just(result)
        } catch (t: Throwable) {
            logger.logMethod.invoke { 
                "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): $msg :: EXCEPTION :: "
            }
            caughtException = t
            throw t
        } finally {
            if (caughtException == null) {
                if (res.element?.hasValue == true) {
                    logger.logMethod.invoke { 
                        "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): $msg"
                    }
                } else {
                    logger.logMethod.invoke { 
                        "Finished (in ${LoggingKt.elapsedSecFrom(startTime)}): $msg <Nothing>"
                    }
                }
            }
        }

        return result
    }

    companion object {
        val kLogger: KLogger
    }
}

internal class EcjCompiler$compile$$inlined$bracket$default$1(
    private val $msg: String
) : () -> Any {
    override fun invoke(): Any {
        return "Started: $msg"
    }
}

@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class EcjCompiler$compile$$inlined$bracket$default$2(
    private val $startTime: LocalDateTime,
    private val $msg: String,
    private val $res: ObjectRef
) : () -> Any {
    override fun invoke(): Any {
        val elapsed = LoggingKt.elapsedSecFrom($startTime)
        val result = ($res.element as Maybe<*>).getOrThrow()
        return "Finished (in $elapsed): $msg"
    }
}

internal class EcjCompiler$compile$$inlined$bracket$default$3(
    private val $startTime: LocalDateTime,
    private val $msg: String
) : () -> Any {
    override fun invoke(): Any {
        val elapsed = LoggingKt.elapsedSecFrom($startTime)
        return "Finished (in $elapsed): $msg <Nothing>"
    }
}

@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class EcjCompiler$compile$$inlined$bracket$default$4(
    private val $startTime: LocalDateTime,
    private val $msg: String,
    private val $t: Throwable
) : () -> Any {
    override fun invoke(): Any {
        val elapsed = LoggingKt.elapsedSecFrom($startTime)
        return "Finished (in $elapsed): $msg :: EXCEPTION :: "
    }
}

@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class EcjCompiler$compile$$inlined$bracket$default$5(
    private val $startTime: LocalDateTime,
    private val $msg: String,
    private val $res: ObjectRef
) : () -> Any {
    override fun invoke(): Any {
        val elapsed = LoggingKt.elapsedSecFrom($startTime)
        val result = ($res.element as Maybe<*>).getOrThrow()
        return "Finished (in $elapsed): $msg"
    }
}

internal class EcjCompiler$compile$$inlined$bracket$default$6(
    private val $startTime: LocalDateTime,
    private val $msg: String
) : () -> Any {
    override fun invoke(): Any {
        val elapsed = LoggingKt.elapsedSecFrom($startTime)
        return "Finished (in $elapsed): $msg <Nothing>"
    }
}