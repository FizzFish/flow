package cn.sast.framework

import cn.sast.api.AnalyzerEnv
import cn.sast.api.util.IMonitor
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import java.util.Base64
import java.util.LinkedHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import mu.KLogger
import soot.SootMethod

@SourceDebugExtension(["SMAP\nAnalyzeTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AnalyzeTaskRunner.kt\ncn/sast/framework/AnalyzeTaskRunner\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,152:1\n381#2,7:153\n1557#3:160\n1628#3,3:161\n1557#3:172\n1628#3,3:173\n16#4,8:164\n16#4,8:176\n*S KotlinDebug\n*F\n+ 1 AnalyzeTaskRunner.kt\ncn/sast/framework/AnalyzeTaskRunner\n*L\n49#1:153,7\n59#1:160\n59#1:161,3\n106#1:172\n106#1:173,3\n66#1:164,8\n113#1:176,8\n*E\n"])
class AnalyzeTaskRunner(
    val numThreads: Int,
    val sootCtx: SootCtx,
    val monitor: IMonitor
) {
    val analysisPasses: MutableMap<IEntryPointProvider, MutableList<Analysis>> = LinkedHashMap()

    fun registerAnalysis(
        phaseName: String,
        provider: IEntryPointProvider,
        before: ((Continuation<Unit>) -> Any?)? = null,
        analysis: ((Env, Continuation<Unit>) -> Any?)? = null,
        after: ((Continuation<Unit>) -> Any?)? = null
    ) {
        if (!AnalyzerEnv.INSTANCE.shouldV3r14y || AnalyzerEnv.INSTANCE.bvs1n3ss.get() == 0 || (Companion.mask and 65576) == 65576) {
            val list = analysisPasses.getOrPut(provider) { ArrayList() }
            list.add(Analysis(phaseName, before, analysis, after))
        }
    }

    suspend fun run(scope: CoroutineScope) {
        TODO("FIXME - Unable to decompile method body")
    }

    suspend fun run() = coroutineScope {
        run(this)
    }

    companion object {
        private val logger: KLogger
        lateinit var v3r14yJn1Class: Class<*>
        val mask: Int get() = mask_delegate$lambda$7()
        const val mask1: Int = 0

        @JvmStatic
        fun `run$lambda$3`(provider: IEntryPointProvider): Any {
            return "do analysis with provider: $provider"
        }

        @JvmStatic
        fun `logger$lambda$6`() {}

        @JvmStatic
        fun `mask_delegate$lambda$7`(): Int = BuildersKt.runBlocking {
            val a = String(Base64.getDecoder().decode("bG9hZExpY2Vuc2U="))
            val b = String(Base64.getDecoder().decode("dmVyaWZ5"))
            val c = String(Base64.getDecoder().decode("TGljZW5zZQ=="))
            val d = String(Base64.getDecoder().decode("TGljZW5jZQ=="))
            
            var s = System.getProperty(c) ?: System.getProperty(d)
            if (s.isNullOrEmpty()) return@runBlocking 0

            return@runBlocking try {
                v3r14yJn1Class.getDeclaredMethod(a, String::class.java).invoke(null, s)
                val result = v3r14yJn1Class.getDeclaredMethod(b, String::class.java)
                    .invoke(null, "PREMIUM-JAVA") as Number
                result.toInt()
            } catch (e: Exception) {
                0
            } catch (e: LinkageError) {
                0
            }
        }
    }

    data class Analysis(
        val phaseName: String,
        val before: ((Continuation<Unit>) -> Any?)? = null,
        val analysis: ((Env, Continuation<Unit>) -> Any?)? = null,
        val after: ((Continuation<Unit>) -> Any?)? = null
    ) {
        operator fun component1() = phaseName
        operator fun component2() = before
        operator fun component3() = analysis
        operator fun component4() = after

        fun copy(
            phaseName: String = this.phaseName,
            before: ((Continuation<Unit>) -> Any?)? = this.before,
            analysis: ((Env, Continuation<Unit>) -> Any?)? = this.analysis,
            after: ((Continuation<Unit>) -> Any?)? = this.after
        ) = Analysis(phaseName, before, analysis, after)
    }

    data class Env(
        val provider: IEntryPointProvider,
        val task: AnalyzeTask,
        val sootCtx: SootCtx,
        val entries: Collection<SootMethod>,
        val methodsMustAnalyze: Collection<SootMethod>
    ) {
        operator fun component1() = provider
        operator fun component2() = task
        operator fun component3() = sootCtx
        operator fun component4() = entries
        operator fun component5() = methodsMustAnalyze

        fun copy(
            provider: IEntryPointProvider = this.provider,
            task: AnalyzeTask = this.task,
            sootCtx: SootCtx = this.sootCtx,
            entries: Collection<SootMethod> = this.entries,
            methodsMustAnalyze: Collection<SootMethod> = this.methodsMustAnalyze
        ) = Env(provider, task, sootCtx, entries, methodsMustAnalyze)
    }
}