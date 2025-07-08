package cn.sast.api.incremental

import cn.sast.common.IResource

interface IncrementalAnalyze {
    fun parseIncrementBaseFile(base: IResource)
}