package cn.sast.dataflow.interprocedural.analysis

import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.InterproceduralCFG
import mu.KLogger
import mu.KotlinLogging
import soot.*
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.spark.pag.PAG
import soot.jimple.spark.sets.PointsToSetInternal
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * **按需指针分析** 基类：在运行前先把关联对象与 PTS 缓存好，加速后续查询。
 *
 * @param pta 传入的全程序指针分析结果（通常是 Spark[PAG]）
 */
abstract class DemandPTA<
        V,
        CTX : Context<SootMethod, Unit, IFact<V>>
        >(
   val pta: PointsToAnalysis,
   hf: AbstractHeapFactory<V>,
   icfg: InterproceduralCFG
) : AJimpleInterProceduralAnalysis<V, CTX>(hf, icfg) {

   /** 含 *field* 访问的 PTS 并集 */
   var associationPTS: PointsToSetInternal? = null
      protected set

   /** 仅 base/实例本身的 PTS 并集 */
   var associationInstance: PointsToSetInternal? = null
      protected set

   /** 出现关联访问的语句列表 */
   val associationStmt: MutableSet<Unit> = LinkedHashSet()

   /* ------------------------------------------------------------------ */
   /*  子类需提供：所有待关注 (Unit?, AccessPath)                      */
   /* ------------------------------------------------------------------ */
   abstract fun getLocals(): Set<Pair<Unit?, AccessPath>>

   /* ------------------------------------------------------------------ */
   /*  预处理：收集 PTS 并调用父类分析                                   */
   /* ------------------------------------------------------------------ */
   override fun doAnalysis(
      entries: Collection<SootMethod>,
      methodsMustAnalyze: Collection<SootMethod>
   ) {
      if (pta is PAG) {
         val pag = pta
         val factory = pag.setFactory

         val instSet: PointsToSetInternal = factory.newSet(null, pag)
         val ptsSet: PointsToSetInternal  = factory.newSet(null, pag)

         for ((u, ap) in getLocals()) {
            u?.let { associationStmt += it }

            // base 对象 PTS
            pag.reachingObjects(ap.plainValue)
               .takeIf { it is PointsToSetInternal }
               ?.let { instSet.addAll(it as PointsToSetInternal, null) }

            // base.field() PTS
            val fieldPts: PointsToSet = if (ap.firstFragment != null)
               pag.reachingObjects(ap.plainValue, ap.firstFragment.field)
            else
               pag.reachingObjects(ap.plainValue)

            (fieldPts as? PointsToSetInternal)
               ?.let { ptsSet.addAll(it, null) }
         }

         associationInstance = instSet
         associationPTS = ptsSet
      } else {
         logger.error { "error pta type: ${pta::class.java}" }
      }

      // 调用父类继续做数据流分析
      super.doAnalysis(entries, methodsMustAnalyze)
   }

   /* ------------------------------------------------------------------ */
   /*  查询工具                                                           */
   /* ------------------------------------------------------------------ */

   /** `l` 的 PTS 与关联字段 PTS 是否有交集 */
   fun isAssociation(l: Local): Boolean =
      associationPTS == null ||
              associationPTS!!.hasNonEmptyIntersection(pta.reachingObjects(l))

   /** `l` 的 PTS 与关联 **实例** PTS 是否有交集 */
   fun isAssociationInstance(l: Local): Boolean =
      associationInstance == null ||
              associationInstance!!.hasNonEmptyIntersection(pta.reachingObjects(l))

   /* ------------------------------------------------------------------ */
   /*  normalFlowFunction 继续交给具体分析实现                           */
   /* ------------------------------------------------------------------ */

   abstract override suspend fun normalFlowFunction(
      context: CTX,
      node: Unit,
      succ: Unit,
      inValue: IFact<V>,
      isNegativeBranch: AtomicBoolean
   ): IFact<V>

   /* ------------------------------------------------------------------ */
   /*  日志                                                               */
   /* ------------------------------------------------------------------ */
   companion object {
      private val logger: KLogger = KotlinLogging.logger {}
   }
}
