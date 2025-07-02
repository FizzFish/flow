package cn.sast.dataflow.interprocedural.analysis

import soot.*
import soot.jimple.*
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.spark.sets.PointsToSetInternal

/**
 * 根据 [accessPath] 查询 Soot 全局 PTA 的 `PointsToSet`
 */
fun getPointsToSet(accessPath: AccessPath): PointsToSet? {
   val pta = Scene.v().pointsToAnalysis
   return when {
      accessPath.isLocal       -> pta.reachingObjects(accessPath.plainValue)
      accessPath.isInstanceFieldRef -> pta.reachingObjects(
         accessPath.plainValue,
         accessPath.firstField
      )
      accessPath.isStaticFieldRef   -> pta.reachingObjects(accessPath.firstField)
      else -> throw RuntimeException("Unexpected access path type: $accessPath")
   }
}

/**
 * 针对任意 [targetValue]（Local / InstanceFieldRef / StaticFieldRef / ArrayRef）
 * 查询 [pta] 中的 `PointsToSet`
 */
fun getPointsToSet(
   pta: PointsToAnalysis,
   targetValue: Value
): PointsToSet? = synchronized(Scene.v().pointsToAnalysis) {
   when (targetValue) {
      is Local -> pta.reachingObjects(targetValue)
      is InstanceFieldRef -> pta.reachingObjects(
         targetValue.base as Local,
         targetValue.field
      )
      is StaticFieldRef -> pta.reachingObjects(targetValue.field)
      is ArrayRef -> pta.reachingObjects(targetValue.base as Local)
      else -> null
   }
}

/**
 * 判断 [v] 的 PTS 是否与污点集合 [ptsTaint] 有交集
 */
fun isAliasedAtStmt(
   pta: PointsToAnalysis,
   ptsTaint: PointsToSet?,
   v: Value
): Boolean {
   ptsTaint ?: return false
   val ptsRight = getPointsToSet(pta, v) ?: return false
   return ptsTaint.hasNonEmptyIntersection(ptsRight)
}
