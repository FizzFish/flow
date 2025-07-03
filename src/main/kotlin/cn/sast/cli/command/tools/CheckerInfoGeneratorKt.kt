package cn.sast.cli.command.tools

import cn.sast.api.report.CheckType2StringKind

internal val id: String
    get() = CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(`$this$id`) as String