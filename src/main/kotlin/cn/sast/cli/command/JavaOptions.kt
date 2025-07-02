package cn.sast.cli.command

import cn.sast.api.config.MainConfig
import cn.sast.framework.EntryPointCreatorFactory
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.component.HybridUnReachThenComponent
import cn.sast.framework.entries.custom.CustomEntryProvider
import cn.sast.framework.entries.custom.HybridCustomThenComponent
import cn.sast.framework.entries.java.UnReachableEntryProvider
import cn.sast.framework.entries.javaee.JavaEeEntryProvider
import cn.sast.framework.report.ProjectFileLocator
import com.github.ajalt.clikt.core.ParameterHolder
import com.github.ajalt.clikt.parameters.options.FlagOptionKt
import com.github.ajalt.clikt.parameters.options.OptionWithValuesKt
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import mu.KLogger
import soot.jimple.infoflow.InfoflowConfiguration

public class JavaOptions : TargetOptions("Java Target Options"), IClassAnalyzeOptionGroup {
   private final val customEntryPoint: List<String> by OptionWithValuesKt.help(
         OptionWithValuesKt.multiple$default(
            OptionWithValuesKt.option$default(
               this as ParameterHolder, new java.lang.String[0], null, "method signature, signature file", false, null, null, null, null, false, 506, null
            ),
            null,
            false,
            3,
            null
         ),
         "Sets the entry point method(s) for analyze."
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[0])
         private final get() {
         return this.customEntryPoint$delegate.getValue(this, $$delegatedProperties[0]) as MutableList<java.lang.String>;
      }


   public open val infoFlowConfig: InfoflowConfiguration by LazyKt.lazy(JavaOptions::infoFlowConfig_delegate$lambda$0)
      public open get() {
         return this.infoFlowConfig$delegate.getValue() as InfoflowConfiguration;
      }


   private final val makeComponentDummyMain: Boolean by FlagOptionKt.flag$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder,
            new java.lang.String[0],
            "Simple entry point creator that builds a sequential list of method invocations. Each method is invoked only once.",
            null,
            false,
            null,
            null,
            null,
            null,
            false,
            508,
            null
         ),
         new java.lang.String[0],
         false,
         null,
         6,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[1])
         private final get() {
         return this.makeComponentDummyMain$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.Boolean;
      }


   private final val disableJavaEEComponent: Boolean by FlagOptionKt.flag$default(
         OptionWithValuesKt.option$default(
            this as ParameterHolder,
            new java.lang.String[]{"--disable-javaee-component"},
            "disable create the JavaEE lifecycle component methods",
            null,
            false,
            null,
            null,
            null,
            null,
            false,
            508,
            null
         ),
         new java.lang.String[0],
         false,
         null,
         6,
         null
      )
      .provideDelegate(this as ParameterHolder, $$delegatedProperties[2])
         private final get() {
         return this.disableJavaEEComponent$delegate.getValue(this, $$delegatedProperties[2]) as java.lang.Boolean;
      }


   public override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
      val entries: java.util.Set = EntryPointCreatorFactory.INSTANCE.getEntryPointFromArgs(this.getCustomEntryPoint()).invoke() as java.util.Set;
      if (this.getMakeComponentDummyMain()) {
         return if (!entries.isEmpty()) new HybridCustomThenComponent(sootCtx, entries) else new HybridUnReachThenComponent(sootCtx);
      } else {
         return if (!entries.isEmpty())
            new CustomEntryProvider(entries)
            else
            (
               if (this.getDisableJavaEEComponent())
                  new UnReachableEntryProvider(sootCtx, null, 2, null)
                  else
                  BuildersKt.runBlocking$default(
                     null,
                     (
                        new Function2<CoroutineScope, Continuation<? super JavaEeEntryProvider>, Object>(locator, sootCtx, null)// $VF: Couldn't be decompiled
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
               //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
               //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
               //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
               //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
               
                     ) as Function2,
                     1,
                     null
                  ) as IEntryPointProvider
            );
      }
   }

   public override fun configureMainConfig(mainConfig: MainConfig) {
   }

   public override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
      sootCtx.configureSoot();
      sootCtx.constructSoot(locator);
   }

   @JvmStatic
   fun `infoFlowConfig_delegate$lambda$0`(): InfoflowConfiguration {
      return new InfoflowConfiguration();
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger
   }
}
