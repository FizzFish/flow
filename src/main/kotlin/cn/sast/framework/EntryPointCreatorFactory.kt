package cn.sast.framework

import cn.sast.api.util.OthersKt
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.feysh.corax.config.api.IMethodMatch
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import mu.KotlinLogging
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.SourceLocator

@SourceDebugExtension(["SMAP\nEntryPointCreatorFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EntryPointCreatorFactory.kt\ncn/sast/framework/EntryPointCreatorFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Strings.kt\nkotlin/text/StringsKt__StringsKt\n*L\n1#1,73:1\n1863#2:74\n1864#2:98\n108#3:75\n80#3,22:76\n*S KotlinDebug\n*F\n+ 1 EntryPointCreatorFactory.kt\ncn/sast/framework/EntryPointCreatorFactory\n*L\n32#1:74\n32#1:98\n52#1:75\n52#1:76,22\n*E\n"])
object EntryPointCreatorFactory {
    private val logger: KLogger = KotlinLogging.logger {}

    private fun lookFromDir(res: MutableSet<SootClass>, direction: IResource) {
        val scene = Scene.v()

        for (cl in SourceLocator.v().getClassesUnder(direction.getAbsolutePath())) {
            val sootClass = scene.loadClass(cl, 2)
            res.add(sootClass)
        }
    }

    private fun loadClass(className: String) {
        Scene.v().forceResolve(className, 3)
        Scene.v().loadClassAndSupport(className)
    }

    fun getEntryPointFromArgs(args: List<String>): () -> Set<SootMethod> {
        return { getEntryPointFromArgsImpl(args) }
    }

    private fun getEntryPointFromArgsImpl(args: List<String>): Set<SootMethod> {
        val mSet = LinkedHashSet<SootMethod>()

        for (arg in args) {
            val match = OthersKt.methodSignatureToMatcher(arg)
            if (match != null) {
                val scene = Scene.v()
                val matchedMethods = match.matched(scene)
                if (matchedMethods.isEmpty()) {
                    throw IllegalStateException("method: $match not exists")
                }
                mSet.addAll(matchedMethods)
            } else {
                val res = Resource.INSTANCE.of(arg)
                if (!res.exists) {
                    throw IllegalStateException("invalidate $arg")
                }

                if (res.isFile) {
                    val path = res.path
                    val charset = Charsets.UTF_8
                    val options = emptyArray<OpenOption>()
                    val inputStream = Files.newInputStream(path, *options)
                    val reader = BufferedReader(InputStreamReader(inputStream, charset))

                    reader.use { r ->
                        while (true) {
                            val line = r.readLine() ?: break
                            val trimmedLine = line.trim()
                            if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("-")) {
                                val className = Scene.signatureToClass(trimmedLine)
                                loadClass(className)
                                val method = Scene.v().grabMethod(trimmedLine)
                                    ?: throw IllegalStateException("soot method: $trimmedLine not exists")
                                mSet.add(method)
                            }
                        }
                    }
                }
            }
        }

        return mSet
    }
}