package cn.sast.framework.plugin

public class CheckerFilterByName(
    public val enables: Set<String>,
    public val renameMap: Map<String, String>
)