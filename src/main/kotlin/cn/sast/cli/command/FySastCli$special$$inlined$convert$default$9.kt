package cn.sast.cli.command

import com.github.ajalt.clikt.core.Context
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda

internal class `FySastCli$special$$inlined$convert$default$9` : Lambda(), Function1<Context, String> {
    @JvmStatic
    val INSTANCE: `FySastCli$special$$inlined$convert$default$9` = `FySastCli$special$$inlined$convert$default$9`()

    constructor() : super(1)

    internal fun <InT : Any, ValueT : Any> Context.`<anonymous>`(): String {
        return this.getLocalization().defaultMetavar()
    }
}