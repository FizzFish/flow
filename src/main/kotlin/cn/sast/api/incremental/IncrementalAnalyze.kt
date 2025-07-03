package cn.sast.api.incremental

import cn.sast.common.IResource

public interface IncrementalAnalyze {
    public fun parseIncrementBaseFile(base: IResource) {
    }
}