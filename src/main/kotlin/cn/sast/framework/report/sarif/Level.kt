package cn.sast.framework.report.sarif

enum class Level(val value: String) {
    None("none"),
    Note("note"),
    Warning("warning"),
    Error("error");

}