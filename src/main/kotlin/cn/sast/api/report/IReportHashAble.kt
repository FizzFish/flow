package cn.sast.api.report

public interface IReportHashAble {
   public abstract fun reportHash(c: IReportHashCalculator): String {
   }
}
