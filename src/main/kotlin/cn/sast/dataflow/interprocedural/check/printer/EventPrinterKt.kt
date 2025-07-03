package cn.sast.dataflow.interprocedural.check.printer

import com.feysh.corax.config.api.utils.UtilsKt

internal val Any.pname: String
    get() {
        var var10000: String = UtilsKt.getTypename(this)
        if (var10000 == null) {
            var10000 = this.toString()
            return var10000
        } else {
            return StringsKt.removePrefix(var10000, "java.lang.")
        }
    }