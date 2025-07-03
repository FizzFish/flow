package cn.sast.dataflow.interprocedural.check

import kotlin.enums.EnumEntries

public enum class OverrideModel {
    HashMap,
    ArrayList;

    companion object {
        @JvmStatic
        fun getEntries(): EnumEntries<OverrideModel> {
            return entries
        }
    }
}