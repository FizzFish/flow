package cn.sast.framework.entries.javaee

import analysis.Config
import analysis.CreateEdge
import analysis.Implement
import cn.sast.common.IResFile
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.java.UnReachableEntryProvider
import cn.sast.framework.entries.utils.PhantomValueForType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import mu.KLogger
import mu.KotlinLogging
import org.utbot.common.Maybe
import soot.Body
import soot.Local
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.Value
import soot.jimple.JimpleBody
import utils.BaseBodyGenerator
import utils.BaseBodyGeneratorFactory
import utils.INewUnits
import java.time.LocalDateTime

class JavaEeEntryProvider(
    private val ctx: SootCtx,
    val beanXmls: MutableSet<IResFile> = mutableSetOf()
) : IEntryPointProvider {

    private val logger: KLogger = KotlinLogging.logger {}

    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        logger.info { "construct Java EE component" }
        val start = LocalDateTime.now()
        val maybeDummyMain: Maybe<SootMethod> = Maybe.ofNullable(kotlin.runCatching {
            val paths = beanXmls.map { it.normalize().expandRes(ctx.mainConfig.output_dir).toString() }.toSet()
            createDummyMain(paths)
        }.getOrNull())

        val unreachable = UnReachableEntryProvider(ctx).apply {
            maybeDummyMain.ifPresent { exclude += it.signature }
        }
        emitAll(unreachable.iterator)
        logger.info { "Java EE entry provider finished; elapsed = ${java.time.Duration.between(start, LocalDateTime.now()).toMillis()} ms" }
    }

    /** Build a synthetic `main` that bootstraps Spring / JavaEE context so Soot can start from it. */
    private fun createDummyMain(beanXmlPaths: Set<String>): SootMethod? {
        val p = PhantomValueForType()
        BaseBodyGeneratorFactory.instance = object : BaseBodyGeneratorFactory(p) {
            override fun create(body: Body): BaseBodyGenerator = SummaryTypeValueBaseBodyGenerator(p, body)
        }
        Implement.mockObject = object : mock.MockObjectImpl(p) {
            override fun mockBean(
                body: JimpleBody,
                units: BaseBodyGenerator,
                sootClass: SootClass,
                toCall: SootMethod
            ): Local = p.getValueForType(units, sootClass.type) ?: super.mockBean(body, units, sootClass, toCall)
        }
        val edge = CreateEdge()
        return try {
            val cfg = Config().apply {
                linkMainAndController = false
                linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods = false
                bean_xml_paths = beanXmlPaths
            }
            edge.initCallGraph(cfg)
            edge.projectMainMethod.also { logger.info { "JavaEE dummy main is $it" } }
        } catch (e: Exception) {
            logger.error(e) { "create JavaEE dummy main failed!" }
            null
        } finally {
            edge.clear()
            BaseBodyGeneratorFactory.instance = null
        }
    }

    // ------------------------------------------------------------------------- helper generator --
    inner class SummaryTypeValueBaseBodyGenerator(private val p: PhantomValueForType, body: Body) : BaseBodyGenerator(body) {
        override fun getValueForType(
            newUnits: INewUnits,
            tp: Type,
            constructionStack: MutableSet<SootClass>,
            parentClasses: Set<SootClass>,
            generatedLocals: MutableSet<Local>?
        ): Value? = p.getValueForType(newUnits, this, tp)
    }
}