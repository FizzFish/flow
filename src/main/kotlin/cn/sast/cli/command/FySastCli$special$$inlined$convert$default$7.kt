package cn.sast.cli.command

import com.github.ajalt.clikt.core.Context
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda

internal class `FySastCli$special$$inlined$convert$default$7`(
    private val `$metavar`: String
) : Lambda(), Function1<Context, String> {
    override fun invoke(p1: Context): String {
        return this.`$metavar`
    }
}