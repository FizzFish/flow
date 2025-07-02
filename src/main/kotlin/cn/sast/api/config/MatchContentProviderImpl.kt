package cn.sast.api.config

import cn.sast.common.Resource
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ClassPathMatch.MatchTarget
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.SootClass
import soot.SootField
import soot.SootMethod

@SourceDebugExtension(["SMAP\nMatchContentProviderImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MatchContentProviderImpl.kt\ncn/sast/api/config/MatchContentProviderImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,80:1\n1#2:81\n*E\n"])
public class MatchContentProviderImpl(mainConfig: MainConfig) : MatchContentProvider {
   public final val mainConfig: MainConfig

   init {
      this.mainConfig = mainConfig;
   }

   public fun getRelativePath(path: Path): String? {
      var relativePath: java.lang.String;
      try {
         val var10000: Resource = Resource.INSTANCE;
         var var10001: Path = path.toAbsolutePath();
         var10001 = var10001.normalize();
         relativePath = var10000.of(var10001).toString();
      } catch (var7: Exception) {
         logger.warn(MatchContentProviderImpl::getRelativePath$lambda$0);
         return null;
      }

      val var8: MainConfig.RelativePath = this.mainConfig.tryGetRelativePathFromAbsolutePath(relativePath);
      return if (var8.getPrefix().length() > 0) var8.getRelativePath() else null;
   }

   private fun getSourceOfClassMember(declaringClass: SootClass): String? {
      val var10000: Path = AnalysisCache.G.INSTANCE.class2SourceFile(declaringClass);
      return if (var10000 != null) this.getRelativePath(var10000) else null;
   }

   public override fun getClassPath(classpath: Path): MatchTarget {
      return new ProcessRule.ClassPathMatch.MatchTarget(this.getRelativePath(classpath));
   }

   public override fun get(file: Path): com.feysh.corax.config.api.rules.ProcessRule.FileMatch.MatchTarget {
      return new ProcessRule.FileMatch.MatchTarget(this.getRelativePath(file));
   }

   public override fun get(sc: SootClass): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      return new ProcessRule.ClassMemberMatch.MatchTarget(sc.getName(), this.getSourceOfClassMember(sc), null, null, null, null);
   }

   public override fun get(sm: SootMethod): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      val var10002: java.lang.String = sm.getDeclaringClass().getName();
      val var10004: SootClass = sm.getDeclaringClass();
      return new ProcessRule.ClassMemberMatch.MatchTarget(var10002, this.getSourceOfClassMember(var10004), sm.getSignature(), sm.getName(), null, null);
   }

   public override fun get(sf: SootField): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget {
      val var10002: java.lang.String = sf.getDeclaringClass().getName();
      val var10004: SootClass = sf.getDeclaringClass();
      return new ProcessRule.ClassMemberMatch.MatchTarget(var10002, this.getSourceOfClassMember(var10004), null, null, sf.getSignature(), sf.getName());
   }

   @JvmStatic
   fun `getRelativePath$lambda$0`(`$path`: Path, `$e`: Exception): Any {
      return "Invalid path: [$`$path`], e: ${`$e`.getMessage()}";
   }

   @JvmStatic
   fun `logger$lambda$3`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
