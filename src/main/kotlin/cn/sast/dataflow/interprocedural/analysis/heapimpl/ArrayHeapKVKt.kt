package cn.sast.dataflow.interprocedural.analysis.heapimpl

internal fun isValidKey(key: Int?, size: Int?): Boolean? {
   if (key != null) {
      if (size != null) {
         return if (key >= size) false else key >= 0;
      }

      if (key < 0) {
         return false;
      }
   }

   return null;
}
