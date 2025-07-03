package cn.sast.dataflow.interprocedural.override.lang

/** 判断码点是否可 LATIN1 编码 */
fun canEncode(cp: Int): Boolean = (cp ushr 8) == 0
