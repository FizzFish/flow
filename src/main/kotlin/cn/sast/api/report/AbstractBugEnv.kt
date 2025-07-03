package cn.sast.api.report

import cn.sast.common.Resource
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.nio.file.Path
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nReport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Report.kt\ncn/sast/api/report/AbstractBugEnv\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,451:1\n1#2:452\n*E\n"])
public abstract class AbstractBugEnv : BugMessage.Env {
    public final val appendEvents: MutableList<(BugPathEventEnvironment) -> BugPathEvent?> = ArrayList()

    public override fun appendPathEvent(message: Map<Language, String>, loc: Path, region: Region) {
        this.appendEvents.add(::appendPathEvent$lambda$0)
    }

    public override fun appendPathEvent(message: Map<Language, String>, loc: Host, region: Region?) {
        this.appendEvents.add(::appendPathEvent$lambda$2)
    }

    @JvmStatic
    private fun appendPathEvent$lambda$0(message: Map<Language, String>, loc: Path, region: Region, var3: BugPathEventEnvironment): BugPathEvent {
        return BugPathEvent(message, FileResInfo(Resource.INSTANCE.fileOf(loc)), region, null, 8, null)
    }

    @JvmStatic
    private fun appendPathEvent$lambda$2(region: Region?, message: Map<Language, String>, loc: Host, this$ret: BugPathEventEnvironment): BugPathEvent {
        var finalRegion: Region = region ?: run {
            val sootInfoCache = this$ret.getSootInfoCache()
            sootInfoCache?.let { Region.invoke(it, loc) } ?: Region.ERROR
        }

        return BugPathEvent(message, ClassResInfo.of(loc), finalRegion, null, 8, null)
    }
}