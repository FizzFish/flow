package cn.sast.api

import cn.sast.common.IResFile
import cn.sast.common.Resource.fileOf
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

object AnalyzerEnv {
    var shouldV3r14y: Boolean = true
        internal set

    var bvs1n3ss: AtomicInteger = AtomicInteger(0)
        internal set

    val lastLogFile: IResFile =
        fileOf("${System.getProperty("user.home")}${File.separator}logs${File.separator}corax${File.separator}last.log")
}