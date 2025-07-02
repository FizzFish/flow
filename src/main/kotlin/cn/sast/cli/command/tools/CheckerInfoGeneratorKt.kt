package cn.sast.cli.command.tools

import cn.sast.api.report.CheckType2StringKind

internal final val id: String
   internal final get() {
      return CheckType2StringKind.Companion.getCheckType2StringKind().getConvert().invoke(`$this$id`) as java.lang.String;
   }

