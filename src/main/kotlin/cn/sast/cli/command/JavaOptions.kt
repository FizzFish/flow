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
    private val customEntryPoint: List<String> by OptionWithValuesKt.help(
        OptionWithValuesKt.multiple(
            OptionWithValuesKt.option(
                this as ParameterHolder, emptyArray(), null, "method signature, signature file", false, null, null, null, null, false, 506, null
            ),
            null,
            false,
            3,
            null
        ),
        "Sets the entry point method(s) for analyze."
    )

    public open val infoFlowConfig: InfoflowConfiguration by lazy { infoFlowConfig_delegate$lambda$0() }

    private val makeComponentDummyMain: Boolean by FlagOptionKt.flag(
        OptionWithValuesKt.option(
            this as ParameterHolder,
            emptyArray(),
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
        emptyArray(),
        false,
        null,
        6,
        null
    )

    private val disableJavaEEComponent: Boolean by FlagOptionKt.flag(
        OptionWithValuesKt.option(
            this as ParameterHolder,
            arrayOf("--disable-javaee-component"),
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
        emptyArray(),
        false,
        null,
        6,
        null
    )

    public override fun getProvider(sootCtx: SootCtx, locator: ProjectFileLocator): IEntryPointProvider {
        val entries = EntryPointCreatorFactory.INSTANCE.getEntryPointFromArgs(customEntryPoint).invoke() as Set<*>
        return if (makeComponentDummyMain) {
            if (entries.isNotEmpty()) HybridCustomThenComponent(sootCtx, entries) else HybridUnReachThenComponent(sootCtx)
        } else {
            if (entries.isNotEmpty()) {
                CustomEntryProvider(entries)
            } else {
                if (disableJavaEEComponent) {
                    UnReachableEntryProvider(sootCtx, null, 2, null)
                } else {
                    BuildersKt.runBlocking<JavaEeEntryProvider>(
                        null,
                        object : Function2<CoroutineScope, Continuation<in JavaEeEntryProvider>, Any?> {
                            override fun invoke(p1: CoroutineScope, p2: Continuation<in JavaEeEntryProvider>): Any? {
                                TODO("FIXME — Couldn't be decompiled")
                            }
                        },
                        1,
                        null
                    ) as IEntryPointProvider
                }
            }
        }
    }

    public override fun configureMainConfig(mainConfig: MainConfig) {
    }

    public override fun initSoot(sootCtx: SootCtx, locator: ProjectFileLocator) {
        sootCtx.configureSoot()
        sootCtx.constructSoot(locator)
    }

    @JvmStatic
    private fun infoFlowConfig_delegate$lambda$0(): InfoflowConfiguration {
        return InfoflowConfiguration()
    }

    @JvmStatic
    private fun logger$lambda$1() {
    }

    public companion object {
        public val logger: KLogger = TODO("Initialize logger")
    }
}