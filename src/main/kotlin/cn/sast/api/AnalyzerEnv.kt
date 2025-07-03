package cn.sast.api

import cn.sast.common.IResFile
import cn.sast.common.Resource
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

public object AnalyzerEnv {
    public var shouldV3r14y: Boolean = true
        internal set

    public var bvs1n3ss: AtomicInteger = AtomicInteger(0)
        internal set

    public val lastLogFile: IResFile =
        Resource.INSTANCE.fileOf("${System.getProperty("user.home")}${File.separator}logs${File.separator}corax${File.separator}last.log")
}