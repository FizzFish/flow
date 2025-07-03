package cn.sast.framework.report.coverage

import cn.sast.api.report.ClassResInfo
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import cn.sast.framework.report.AbstractFileIndexer
import cn.sast.framework.report.IProjectFileLocator
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.commons.io.FilenameUtils
import org.jacoco.report.InputStreamSourceFileLocator
import soot.Scene
import soot.SootClass

@SourceDebugExtension(["SMAP\nJacocoSourceLoator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JacocoSourceLoator.kt\ncn/sast/framework/report/coverage/JacocoSourceLocator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,38:1\n1#2:39\n*E\n"])
class JacocoSourceLocator(
    private val sourceLocator: IProjectFileLocator,
    encoding: String = "utf-8",
    tabWidth: Int = 4
) : InputStreamSourceFileLocator(encoding, tabWidth) {

    protected open fun getSourceStream(path: String): InputStream? {
        val ext = SequencesKt.firstOrNull(
            sourceLocator.findFromFileIndexMap(
                StringsKt.split$default(path, charArrayOf('/', '\\'), false, 0, 6, null),
                AbstractFileIndexer.Companion.getDefaultClassCompareMode()
            )
        ) as? IResFile
        
        if (ext != null) {
            val filePath: Path = ext.getPath()
            val options: Array<OpenOption> = emptyArray()
            return Files.newInputStream(filePath, Arrays.copyOf(options, options.size))
        } else {
            val extension = FilenameUtils.getExtension(path)
            if (ResourceKt.getJavaExtensions().contains(extension)) {
                val sc = Scene.v().getSootClassUnsafe(
                    StringsKt.replace$default(
                        StringsKt.replace$default(
                            StringsKt.removeSuffix(StringsKt.removeSuffix(path, extension), "."),
                            "/",
                            ".",
                            false,
                            4,
                            null
                        ),
                        "\\",
                        ".",
                        false,
                        4,
                        null
                    ),
                    false
                )
                if (sc != null) {
                    val src = IProjectFileLocator.DefaultImpls.get$default(
                        sourceLocator,
                        ClassResInfo.Companion.of(sc),
                        null,
                        2,
                        null
                    )
                    if (src != null) {
                        val srcPath = src.getPath()
                        val srcOptions: Array<OpenOption> = emptyArray()
                        return Files.newInputStream(srcPath, Arrays.copyOf(srcOptions, srcOptions.size))
                    }
                }
            }
            return null
        }
    }
}