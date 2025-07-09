package cn.sast.framework.plugin

class CheckerFilterByName(enables: Set<String>, renameMap: Map<String, String>) {
    val enables: Set<String>
    val renameMap: Map<String, String>

    init {
        this.enables = enables;
        this.renameMap = renameMap;
    }
}