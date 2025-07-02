package cn.sast.dataflow.interprocedural.analysis

import soot.Local
import soot.PointsToAnalysis
import soot.PointsToSet
import soot.Scene
import soot.Value
import soot.jimple.ArrayRef
import soot.jimple.InstanceFieldRef
import soot.jimple.StaticFieldRef
import soot.jimple.infoflow.data.AccessPath

private fun getPointsToSet(accessPath: AccessPath): PointsToSet? {
   val var10000: PointsToSet;
   if (accessPath.isLocal()) {
      var10000 = Scene.v().getPointsToAnalysis().reachingObjects(accessPath.getPlainValue());
   } else if (accessPath.isInstanceFieldRef()) {
      var10000 = Scene.v().getPointsToAnalysis().reachingObjects(accessPath.getPlainValue(), accessPath.getFirstField());
   } else {
      if (!accessPath.isStaticFieldRef()) {
         throw new RuntimeException("Unexepected access path type");
      }

      var10000 = Scene.v().getPointsToAnalysis().reachingObjects(accessPath.getFirstField());
   }

   return var10000;
}

private fun getPointsToSet(pta: PointsToAnalysis, targetValue: Value): PointsToSet? {
   val ptax: PointsToAnalysis = Scene.v().getPointsToAnalysis();
   synchronized (ptax) {
      val var10000: PointsToSet;
      if (targetValue is Local) {
         var10000 = ptax.reachingObjects(targetValue as Local);
      } else if (targetValue is InstanceFieldRef) {
         val var10001: Value = (targetValue as InstanceFieldRef).getBase();
         var10000 = ptax.reachingObjects(var10001 as Local, (targetValue as InstanceFieldRef).getField());
      } else if (targetValue is StaticFieldRef) {
         var10000 = ptax.reachingObjects((targetValue as StaticFieldRef).getField());
      } else if (targetValue is ArrayRef) {
         val var7: Value = (targetValue as ArrayRef).getBase();
         var10000 = ptax.reachingObjects(var7 as Local);
      } else {
         var10000 = null;
      }

      return var10000;
   }
}

private fun isAliasedAtStmt(pta: PointsToAnalysis, ptsTaint: PointsToSet?, v: Value): Boolean {
   if (ptsTaint == null) {
      return false;
   } else {
      val ptsRight: PointsToSet = getPointsToSet(pta, v);
      return ptsRight != null && ptsTaint.hasNonEmptyIntersection(ptsRight);
   }
}
