package cn.sast.framework.engine

internal class BuiltinAnalysisAllMethodsAnalyzeInSceneDefault(
    private val msg: String
) : () -> String {
    override fun invoke(): String = "Started: $msg"
}