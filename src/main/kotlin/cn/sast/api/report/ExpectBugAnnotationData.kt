package cn.sast.api.report

import cn.sast.common.IResFile

/**
 * 测试/基准用：代码里的 `@ExpectBug` 注解信息。
 *
 * @param file   源文件
 * @param line   行号（1-based）
 * @param column 列号（1-based）
 * @param bug    预期的 Bug 对象或类型
 * @param kind   类型（期望出现 / 允许逃逸）
 */
data class ExpectBugAnnotationData<BugT>(
    val file: IResFile,
    val line: Int,
    val column: Int,
    val bug: BugT,
    val kind: Kind
) {
    enum class Kind { Expect, Escape }
}
