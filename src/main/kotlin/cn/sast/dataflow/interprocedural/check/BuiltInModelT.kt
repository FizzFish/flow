package cn.sast.dataflow.interprocedural.check

import kotlin.enums.EnumEntries

public enum class BuiltInModelT {
    Field,
    Array,
    Element,
    MapKeys,
    MapValues;

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<BuiltInModelT> {
            return entries
        }
    }
}