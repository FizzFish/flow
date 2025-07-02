package cn.sast.dataflow.infoflow.provider

import soot.Scene
import soot.SootClass
import soot.SootMethod

fun findAllOverrideMethodsOfMethod(method: SootMethod): Set<SootMethod> {
   val clazz: SootClass = method.declaringClass
   return Scene.v().fastHierarchy.resolveAbstractDispatch(clazz, method)
}
