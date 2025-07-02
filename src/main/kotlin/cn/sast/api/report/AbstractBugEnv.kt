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
   public final val appendEvents: MutableList<(BugPathEventEnvironment) -> BugPathEvent?> = (new ArrayList()) as java.util.List

   public override fun appendPathEvent(message: Map<Language, String>, loc: Path, region: Region) {
      this.appendEvents.add(AbstractBugEnv::appendPathEvent$lambda$0);
   }

   public override fun appendPathEvent(message: Map<Language, String>, loc: Host, region: Region?) {
      this.appendEvents.add(AbstractBugEnv::appendPathEvent$lambda$2);
   }

   @JvmStatic
   fun `appendPathEvent$lambda$0`(`$message`: java.util.Map, `$loc`: Path, `$region`: Region, var3: BugPathEventEnvironment): BugPathEvent {
      return new BugPathEvent(`$message`, new FileResInfo(Resource.INSTANCE.fileOf(`$loc`)), `$region`, null, 8, null);
   }

   @JvmStatic
   fun `appendPathEvent$lambda$2`(`$region`: Region, `$message`: java.util.Map, `$loc`: Host, `$this$ret`: BugPathEventEnvironment): BugPathEvent {
      var var10000: Region = `$region`;
      if (`$region` == null) {
         val var7: SootInfoCache = `$this$ret`.getSootInfoCache();
         var10000 = if (var7 != null) Region.Companion.invoke(var7, `$loc`) else null;
         if (var10000 == null) {
            var10000 = Region.Companion.getERROR();
         }
      }

      return new BugPathEvent(`$message`, ClassResInfo.Companion.of(`$loc`), var10000, null, 8, null);
   }
}
