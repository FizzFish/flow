package cn.sast.api.report

sealed class IBugResInfo protected constructor() : Comparable<IBugResInfo>, IReportHashAble {
    abstract val reportFileName: String?
    abstract val path: String
}