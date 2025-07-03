package cn.sast.api.util

import cn.sast.api.report.ProjectMetrics

public interface IMonitor {
    public val projectMetrics: ProjectMetrics

    public abstract fun timer(phase: String): Timer
}