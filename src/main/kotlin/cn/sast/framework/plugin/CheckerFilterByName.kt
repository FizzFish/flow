package cn.sast.framework.plugin

public class CheckerFilterByName(enables: Set<String>, renameMap: Map<String, String>) {
   public final val enables: Set<String>
   public final val renameMap: Map<String, String>

   init {
      this.enables = enables;
      this.renameMap = renameMap;
   }
}
