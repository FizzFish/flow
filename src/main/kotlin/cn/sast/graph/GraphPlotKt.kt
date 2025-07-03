package cn.sast.graph

import cn.sast.common.IResFile
import java.nio.file.Files
import kotlin.io.path.outputStream
import soot.util.dot.DotGraph

/** 将 DOT 图渲染到 [output] 文件（覆盖写） */
fun DotGraph.dump(output: IResFile) {
   Files.createDirectories(output.path.parent)
   output.path.outputStream().use { render(it, 0) }
}
