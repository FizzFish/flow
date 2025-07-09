package cn.sast.framework

import cn.sast.api.util.OthersKt.methodSignatureToMatcher
import cn.sast.common.IResource
import cn.sast.common.Resource
import mu.KotlinLogging
import soot.Scene
import soot.SootMethod
import soot.SourceLocator
import java.io.BufferedReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.readLines

/**
 * 依据“方法签名 / 路径文件 / 目录”动态生成入口点集合。
 */
object EntryPointCreatorFactory {
    private val logger = KotlinLogging.logger {}

    /**
     * 将命令行参数转换为 *惰性* 入口点提供函数。
     *
     * 支持三种形式：
     * 1. 完整方法签名：`<Ljava/lang/String: void main(java.lang.String[])>`
     * 2. 文件：每行一个方法签名
     * 3. 目录：递归读取所有 `*.class` 并抓取 `<init>` 之外的所有公有方法
     */
    fun getEntryPointFromArgs(args: List<String>): () -> Set<SootMethod> = {
        val result = linkedSetOf<SootMethod>()
        val scene  = Scene.v()

        args.forEach { arg ->
            // 1) 方法签名
            methodSignatureToMatcher(arg)?.let { matcher ->
                val matched = matcher.matched(scene)
                require(matched.isNotEmpty()) { "Method $matcher not found in loaded classes" }
                result += matched
                return@forEach
            }

            // 2) 资源（文件 / 目录）
            val res: IResource = Resource.of(arg)
            require(res.exists) { "Invalid path: $arg" }

            when {
                res.isFile -> {                       // 每行一个签名
                    Files.newBufferedReader(res.path, StandardCharsets.UTF_8).useLines { lines ->
                        lines.map(String::trim)
                            .filter { it.isNotEmpty() && !it.startsWith("#") && !it.startsWith("-") }
                            .forEach { sig ->
                                scene.forceResolve(SourceLocator.signatureToClass(sig), SootClass.BODIES)
                                result += scene.grabMethod(sig)
                            }
                    }
                }

                res.isDirectory -> {                  // 递归目录 → 类 → 公有方法
                    SourceLocator.v()
                        .getClassesUnder(res.absolutePath)
                        .forEach { clsName ->
                            scene.loadClass(clsName, SootClass.BODIES)
                                .methods
                                .filter { it.isPublic && !it.isConstructor }
                                .forEach(result::add)
                        }
                }
            }
        }

        result
    }
}
