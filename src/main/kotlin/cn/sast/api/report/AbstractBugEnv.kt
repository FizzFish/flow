package cn.sast.api.report

import cn.sast.common.Resource
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import soot.tagkit.Host
import java.nio.file.Path
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 提供两种 `appendPathEvent` 默认实现，构造成 [BugPathEvent] 并缓存至 [appendEvents]。
 */
abstract class AbstractBugEnv : BugMessage.Env {

    /** 存放延迟执行的事件构造器 */
    val appendEvents: MutableList<(BugPathEventEnvironment) -> BugPathEvent?> =
        CopyOnWriteArrayList()

    /* ---------- API 实现 ---------- */

    override fun appendPathEvent(
        message: Map<Language, String>,
        loc: Path,
        region: Region
    ) {
        appendEvents += { BugPathEvent(message, FileResInfo(Resource.fileOf(loc)), region) }
    }

    override fun appendPathEvent(
        message: Map<Language, String>,
        loc: Host,
        region: Region?
    ) {
        appendEvents += { env ->
            val finalRegion = region ?: env.sootInfoCache?.let { Region(it, loc) } ?: Region.ERROR
            BugPathEvent(message, ClassResInfo.of(loc), finalRegion)
        }
    }
}
