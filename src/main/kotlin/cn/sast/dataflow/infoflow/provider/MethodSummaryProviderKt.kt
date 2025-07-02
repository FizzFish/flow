package cn.sast.dataflow.infoflow.provider

import soot.Scene
import soot.SootClass
import soot.SootMethod

public fun findAllOverrideMethodsOfMethod(method: SootMethod): Set<SootMethod> {
   val var10000: SootClass = method.getDeclaringClass();
   val var2: java.util.Set = Scene.v().getFastHierarchy().resolveAbstractDispatch(var10000, method);
   return var2;
}
