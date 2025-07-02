package cn.sast.dataflow.callgraph

import kotlin.sequences.Sequence
import kotlin.sequences.sequence

import soot.MethodOrMethodContext
import soot.Scene
import soot.SootMethod
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.util.queue.QueueReader

/**
 * Returns a lazy [Sequence] of reachable [SootMethod]s starting from the given entry points.
 */
fun reachableMethodSequence(entryPoints: Collection<MethodOrMethodContext>): Sequence<SootMethod> = sequence {
   val scene = Scene.v()
   val reachableMethods = ReachableMethods(scene.callGraph, entryPoints)
   reachableMethods.update()
   val listener: QueueReader<MethodOrMethodContext> = reachableMethods.listener()

   for (methodOrContext in listener) {
      yield(methodOrContext.method())
   }
}

/**
 * Returns a lazy [Sequence] of reachable [Stmt]s starting from the given entry points.
 */
fun reachableStmtSequence(entryPoints: Collection<MethodOrMethodContext>): Sequence<Stmt> = sequence {
   val scene = Scene.v()
   val reachableMethods = ReachableMethods(scene.callGraph, entryPoints)
   reachableMethods.update()
   val listener: QueueReader<MethodOrMethodContext> = reachableMethods.listener()

   for (methodOrContext in listener) {
      val method = methodOrContext.method()
      if (!method.hasActiveBody()) continue
      for (unit in method.activeBody.units) {
         if (unit is Stmt) yield(unit)
      }
   }
}
