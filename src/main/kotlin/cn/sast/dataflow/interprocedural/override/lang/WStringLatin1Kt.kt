package cn.sast.dataflow.interprocedural.override.lang

public fun canEncode(cp: Int): Boolean {
   return cp ushr 8 == 0;
}
