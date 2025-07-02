package cn.sast.dataflow.infoflow.svfa

internal fun ValueLocation.isLeft(): Boolean  = this == ValueLocation.Left
internal fun ValueLocation.isRight(): Boolean = this != ValueLocation.Left
