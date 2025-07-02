package cn.sast.api.report

public sealed class IBugResInfo protected constructor() : java.lang.Comparable<IBugResInfo>, IReportHashAble {
   public abstract val reportFileName: String?
   public abstract val path: String
}
