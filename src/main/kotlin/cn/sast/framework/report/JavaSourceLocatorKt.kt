package cn.sast.framework.report

public fun listEndsWith(array1: List<String>, array2: List<String>): ListSuffixRelation {
   val minSize: Int = Math.min(array1.size(), array2.size());
   if (minSize == 0) {
      return ListSuffixRelation.NeitherSuffix;
   } else {
      for (int i = 0; i < minSize; i++) {
         if (!(array1.get(array1.size() - minSize + i) == array2.get(array2.size() - minSize + i))) {
            return ListSuffixRelation.NeitherSuffix;
         }
      }

      return if (array1.size() < array2.size())
         ListSuffixRelation.AIsSuffixOfB
         else
         (if (array1.size() > array2.size()) ListSuffixRelation.BIsSuffixOfA else ListSuffixRelation.Equals);
   }
}
