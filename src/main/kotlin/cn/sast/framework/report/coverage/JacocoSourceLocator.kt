package cn.sast.framework.report.coverage

import cn.sast.api.report.ClassResInfo
import cn.sast.common.IResFile
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.IProjectFileLocator
import org.apache.commons.io.FilenameUtils
import org.jacoco.report.InputStreamSourceFileLocator
import soot.Scene
import java.io.InputStream
import java.nio.file.Files
import kotlin.io.path.inputStream

/**
 * Jacoco HTML/CSV 报告用 “源码查找器”，支持：
 * * 直接凭相对路径定位
 * * 按类名反查 Soot Scene → 源文件
 */
class JacocoSourceLocator(
    private val sourceLocator: IProjectFileLocator,
    encoding: String = "utf-8",
    tabWidth: Int = 4
) : InputStreamSourceFileLocator(encoding, tabWidth) {

    override fun getSourceStream(path: String): InputStream? {
        // ① 直接按文件名查找
        sourceLocator.findFromFileIndexMap(
            path.split('/', '\\'),
            AbstractFileIndexer.defaultClassCompareMode
        ).firstOrNull()?.let { return it.path.inputStream() }

        // ② 按类名 → 源文件
        val ext = FilenameUtils.getExtension(path)
        if (ext in cn.sast.common.ResourceKt.javaExtensions) {
            val className = path.removeSuffix(".$ext")
                .replace('/', '.')
                .replace('\\', '.')
            Scene.v().getSootClassUnsafe(className, false)?.let { sc ->
                sourceLocator.get(ClassResInfo.of(sc))?.let { src ->
                    return src.path.inputStream()
                }
            }
        }
        return null
    }
}
