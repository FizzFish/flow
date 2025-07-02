package cn.sast.dataflow.infoflow.svfa

internal fun ValueLocation.isLeft(): Boolean {
   return `$this$isLeft` === ValueLocation.Left;
}

internal fun ValueLocation.isRight(): Boolean {
   return `$this$isRight` != ValueLocation.Left;
}
