package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.common.IResource
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.apk.ApkLifeCycleComponent
import cn.sast.framework.report.ProjectFileLocator
import java.io.File
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.PersistentSet
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.AnalysisFileConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.IccConfiguration
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.SourceSinkConfiguration

@SourceDebugExtension(["SMAP\nAndroidOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AndroidOptions.kt\ncn/sast/cli/command/AndroidOptions\n+ 2 Validate.kt\ncom/github/ajalt/clikt/parameters/options/ValidateKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,94:1\n65#2,5:95\n25#2:100\n1#3:101\n*S KotlinDebug\n*F\n+ 1 AndroidOptions.kt\ncn/sast/cli/command/AndroidOptions\n*L\n20#1:95,5\n20#1:100\n*E\n"])
public class AndroidOptions : TargetOptions("Android Target Options"), IClassAnalyzeOptionGroup {
   private final val androidPlatformDir: File
      private final get() {
         return this.androidPlatformDir$delegate.getValue(this, $$delegatedProperties[0]) as File;
      }


   private final val oneComponentAtATime: Boolean
      private final get() {
         return this.oneComponentAtATime$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.Boolean;
      }


   public open val infoFlowConfig: InfoflowAndroidConfiguration
      public open get() {
         return this.infoFlowConfig$delegate.getValue() as InfoflowAndroidConfiguration;
      }


   private final val targetApkFile: String
      private final get() {
         val it: PersistentSet = `$this$targetApkFile`.getProcessDir();
         if (it.size() != 1) {
            throw new IllegalStateException(("process: ${`$this$targetApkFile`.getProcessDir()} must be a single apk file").toString());
         } else {
            return (CollectionsKt.first(it as java.lang.Iterable) as IResource).getAbsolutePath();
         }
      }


   public override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
      val mainConfig: MainConfig = sootCtx.getMainConfig();
      return new ApkLifeCycleComponent(this.getInfoFlowConfig(), mainConfig, this.getTargetApkFile(mainConfig));
   }

   public override fun configureMainConfig(mainConfig: MainConfig) {
      mainConfig.setAndroidPlatformDir(this.getAndroidPlatformDir().getAbsolutePath());
   }

   public override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
      val mainConfig: MainConfig = sootCtx.getMainConfig();
      val targetApkFile: java.lang.String = this.getTargetApkFile(mainConfig);
      val var10000: java.lang.String = mainConfig.getAndroidJarClasspath(targetApkFile);
      if (var10000 == null) {
         throw new IllegalStateException(
            ("could not find android jar in androidPlatformDir: ${mainConfig.getAndroidPlatformDir()} with apk: $targetApkFile").toString()
         );
      } else {
         mainConfig.setClasspath(mainConfig.getClasspath().add(var10000));
         sootCtx.configureSoot();
         sootCtx.constructSoot(locator);
      }
   }

   @JvmStatic
   fun `infoFlowConfig_delegate$lambda$5`(`this$0`: AndroidOptions): InfoflowAndroidConfiguration {
      val infoConfig: InfoflowAndroidConfiguration = new InfoflowAndroidConfiguration();
      val `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`: AnalysisFileConfiguration = infoConfig.getAnalysisFileConfig();
      `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`.setTargetAPKFile("unused");
      `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`.setSourceSinkFile("unused");
      `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`.setAndroidPlatformDir("unused");
      `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`.setAdditionalClasspath("unused");
      `$this$infoFlowConfig_delegate_u24lambda_u245_u24lambda_u244`.setOutputFile("unused");
      val var4: CallbackConfiguration = infoConfig.getCallbackConfig();
      var4.setEnableCallbacks(true);
      var4.setCallbackAnalyzer(CallbackAnalyzer.Default);
      var4.setFilterThreadCallbacks(true);
      var4.setMaxCallbacksPerComponent(100);
      var4.setCallbackAnalysisTimeout(0);
      var4.setMaxAnalysisCallbackDepth(-1);
      var4.setSerializeCallbacks(false);
      var4.setCallbacksFile("");
      val var5: SourceSinkConfiguration = infoConfig.getSourceSinkConfig();
      val var6: IccConfiguration = infoConfig.getIccConfig();
      var6.setIccModel(null);
      var6.setIccResultsPurify(true);
      infoConfig.setOneComponentAtATime(`this$0`.getOneComponentAtATime());
      return infoConfig;
   }
}
