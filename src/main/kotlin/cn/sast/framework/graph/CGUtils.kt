package cn.sast.framework.graph

import cn.sast.api.config.ExtSettings
import cn.sast.api.report.Counter
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import com.github.ajalt.mordant.rendering.Theme
import java.nio.file.Files
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import mu.KotlinLogging
import soot.Body
import soot.MethodOrMethodContext
import soot.PackManager
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.UnitPatchingChain
import soot.VoidType
import soot.jimple.DynamicInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.util.Chain
import soot.util.queue.QueueReader

@SourceDebugExtension(["SMAP\nCGUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CGUtils.kt\ncn/sast/framework/graph/CGUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,223:1\n1#2:224\n*E\n"])
public object CGUtils {
   public final val missClasses: Counter<SootClass> = new Counter()
   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(CGUtils::logger$lambda$8)

   public fun rewriteJimpleBodyAfterCG() {
      val all: java.util.Iterator = Scene.v().getClasses().snapshotIterator();
      BuildersKt.runBlocking(
         Dispatchers.getDefault() as CoroutineContext,
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(all, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.$all = `$all`;
               }

               public final Object invokeSuspend(Object $result) {
                  IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val `$this$runBlocking`: CoroutineScope = this.L$0 as CoroutineScope;
                        val sc: java.util.Iterator = this.$all;
                        val var3x: java.util.Iterator = sc;

                        while (var3x.hasNext()) {
                           val var8: SootClass = var3x.next() as SootClass;
                           if (!var8.isPhantom()) {
                              val var6: java.util.List = var8.getMethods();

                              for (SootMethod sm : CollectionsKt.toList(var6)) {
                                 if (sm.hasActiveBody()) {
                                    BuildersKt.launch$default(
                                       `$this$runBlocking`, null, null, (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(sm, null) {
                                          int label;

                                          {
                                             super(2, `$completionx`);
                                             this.$sm = `$sm`;
                                          }

                                          public final Object invokeSuspend(Object $result) {
                                             IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                             switch (this.label) {
                                                case 0:
                                                   ResultKt.throwOnFailure(`$result`);
                                                   if (!this.$sm.hasActiveBody()) {
                                                      return Unit.INSTANCE;
                                                   } else {
                                                      try {
                                                         val var10000: Body = this.$sm.getActiveBody();
                                                         if (var10000 == null) {
                                                            return Unit.INSTANCE;
                                                         }

                                                         PackManager.v().getTransform("jb.rewriter").apply(var10000);
                                                         PackManager.v().getTransform("jb.identityStmt2MethodParamRegion").apply(var10000);
                                                         this.$sm.setActiveBody(var10000);
                                                      } catch (var3: RuntimeException) {
                                                      }

                                                      return Unit.INSTANCE;
                                                   }
                                                default:
                                                   throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                             }
                                          }

                                          public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                             return (new <anonymous constructor>(this.$sm, `$completion`)) as Continuation<Unit>;
                                          }

                                          public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                             return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                          }
                                       }) as Function2, 3, null
                                    );
                                 }
                              }
                           }
                        }

                        return Unit.INSTANCE;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  val var3: Function2 = new <anonymous constructor>(this.$all, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2
      );
   }

   public fun makeSpuriousMethodFromInvokeExpr() {
      val var10000: Chain = Scene.v().getApplicationClasses();

      for (SootClass appSc : CollectionsKt.toList((java.lang.Iterable)var10000)) {
         if (!appSc.isPhantom()) {
            val var16: java.util.List = appSc.getMethods();

            for (SootMethod sm : CollectionsKt.toList(var16)) {
               if (sm.isConcrete() && sm.getSource() != null) {
                  try {
                     val var17: Body = sm.retrieveActiveBody();
                     if (var17 != null) {
                        val var18: UnitPatchingChain = var17.getUnits();
                        val var19: java.util.Iterator = var18.iterator();
                        val var9: java.util.Iterator = var19;

                        while (var9.hasNext()) {
                           val u: soot.Unit = var9.next() as soot.Unit;
                           val var12: Stmt = u as? Stmt;
                           if ((u as? Stmt) != null) {
                              if (var12.containsInvokeExpr()) {
                                 val var14: SootMethod = var12.getInvokeExpr().getMethod();
                              }

                              if (var12.containsFieldRef()) {
                                 val var15: SootField = var12.getFieldRef().getField();
                              }
                           }
                        }
                     }
                  } catch (var13: RuntimeException) {
                  }
               }
            }
         }
      }
   }

   private fun CallGraph.forceAddCgEdge(src: SootMethod, srcUnit: Stmt, ie: InvokeExpr) {
      val tgt: SootMethod = ie.getMethod();
      if (srcUnit.getInvokeExpr() !is DynamicInvokeExpr) {
         `$this$forceAddCgEdge`.addEdge(new Edge(src as MethodOrMethodContext, srcUnit, tgt as MethodOrMethodContext));
      }
   }

   public fun addCallEdgeForPhantomMethods() {
      val scene: Scene = Scene.v();
      val cg: CallGraph = scene.getCallGraph();
      val reachableMethods: ReachableMethods = scene.getReachableMethods();
      reachableMethods.update();
      val var10000: QueueReader = reachableMethods.listener();
      val listener: java.util.Iterator = var10000 as java.util.Iterator;

      while (listener.hasNext()) {
         val src: SootMethod = (listener.next() as MethodOrMethodContext).method();
         if (src.hasActiveBody()) {
            val var14: UnitPatchingChain = src.getActiveBody().getUnits();
            val var15: java.util.Iterator = var14.iterator();
            val var7: java.util.Iterator = var15;

            while (var7.hasNext()) {
               val u: soot.Unit = var7.next() as soot.Unit;
               val srcUnit: Stmt = u as Stmt;
               if ((u as Stmt).containsInvokeExpr()) {
                  val ie: InvokeExpr = srcUnit.getInvokeExpr();
                  if (!cg.edgesOutOf(u).hasNext()) {
                     val var16: SootClass = ie.getMethodRef().getDeclaringClass();
                     if (var16 != null) {
                        if (var16.isPhantom() && !Scene.v().isExcluded(var16)) {
                           val var17: java.lang.String = var16.getName();
                           if (!StringsKt.startsWith$default(var17, "soot.dummy", false, 2, null)) {
                              missClasses.count(var16);
                           }
                        }
                     }

                     this.forceAddCgEdge(cg, src, srcUnit, ie);
                  }
               }
            }
         }
      }
   }

   public fun flushMissedClasses(outputDir: IResDirectory) {
      val out: IResFile = outputDir.resolve("phantom_dependence_classes.txt").toFile();
      if (missClasses.isNotEmpty()) {
         logger.warn(CGUtils::flushMissedClasses$lambda$1);
         missClasses.writeResults(out);
      } else {
         Files.deleteIfExists(out.getPath());
      }
   }

   public fun removeInvalidMethodBody(scene: Scene) {
      val var10000: java.util.Iterator = scene.getClasses().iterator();
      val var2: java.util.Iterator = var10000;

      while (var2.hasNext()) {
         for (SootMethod sm : ((SootClass)var2.next()).getMethods()) {
            if (sm.hasActiveBody() && sm.getActiveBody().getUnits().isEmpty()) {
               sm.setActiveBody(null);
               sm.setPhantom(true);
            }
         }
      }
   }

   public fun fixInvalidInterface(scene: Scene) {
      var var10000: java.util.Iterator = scene.getClasses().iterator();
      val var2: java.util.Iterator = var10000;

      while (var2.hasNext()) {
         val sc: SootClass = var2.next() as SootClass;
         var10000 = sc.getInterfaces().snapshotIterator();
         val var4: java.util.Iterator = var10000;

         while (var4.hasNext()) {
            val i: SootClass = var4.next() as SootClass;
            if (!i.isInterface()) {
               logger.warn(CGUtils::fixInvalidInterface$lambda$2);

               try {
                  sc.removeInterface(i);
               } catch (var7: Exception) {
                  logger.warn(var7, CGUtils::fixInvalidInterface$lambda$3);
               }
            }
         }
      }
   }

   public fun removeLargeClasses(scene: Scene) {
      val skipClassByMaximumMethods: Int = ExtSettings.INSTANCE.getSkip_large_class_by_maximum_methods();
      val skipClassByMaximumFields: Int = ExtSettings.INSTANCE.getSkip_large_class_by_maximum_fields();
      if (skipClassByMaximumMethods > 0 || skipClassByMaximumFields > 0) {
         val var10000: java.util.Iterator = scene.getClasses().snapshotIterator();
         val var4: java.util.Iterator = var10000;

         while (var4.hasNext()) {
            val sc: SootClass = var4.next() as SootClass;
            var removeIt: Boolean = false;
            if (skipClassByMaximumMethods > 0 && sc.getMethodCount() > skipClassByMaximumMethods) {
               removeIt = true;
               logger.warn(CGUtils::removeLargeClasses$lambda$4);
            }

            if (skipClassByMaximumFields > 0 && sc.getFieldCount() > skipClassByMaximumFields) {
               removeIt = true;
               logger.warn(CGUtils::removeLargeClasses$lambda$5);
            }

            if (removeIt) {
               scene.removeClass(sc);
            }
         }
      }
   }

   public fun fixScene(scene: Scene) {
      this.removeInvalidMethodBody(scene);
      this.fixInvalidInterface(scene);
   }

   public fun createSootMethod(
      name: String,
      argsTypes: List<Type>,
      returnType: Type,
      declaringClass: SootClass,
      graphBody: JimpleBody,
      isStatic: Boolean = true
   ): SootMethod {
      val var7: SootMethod = new SootMethod(name, argsTypes, returnType, if (isStatic) 8 else 0);
      declaringClass.addMethod(var7);
      var7.setActiveBody(graphBody as Body);
      return var7;
   }

   public fun getOrCreateClass(scene: Scene, className: String): SootClass {
      var mainClass: SootClass = scene.getSootClassUnsafe(className, false);
      if (mainClass == null) {
         val var10000: SootClass = scene.makeSootClass(className);
         mainClass = var10000;
         var10000.setResolvingLevel(3);
         scene.addClass(var10000);
      }

      return mainClass;
   }

   public fun createDummyMain(scene: Scene, dummyClassName: String = "dummyMainClass", methodName: String = "fakeMethod"): SootClass {
      val jimple: Jimple = Jimple.v();
      val dummyClass: SootClass = this.getOrCreateClass(scene, dummyClassName);
      dummyClass.setApplicationClass();
      val var7: JimpleBody = jimple.newBody();
      var7.getUnits().add(jimple.newNopStmt() as soot.Unit);
      val var10002: java.util.List = CollectionsKt.emptyList();
      val var10003: VoidType = VoidType.v();
      val var10: Type = var10003 as Type;
      createSootMethod$default(this, methodName, var10002, var10, dummyClass, var7, false, 32, null);
      return dummyClass;
   }

   @JvmStatic
   fun `flushMissedClasses$lambda$1`(`$out`: IResFile): Any {
      return Theme.Companion
         .getDefault()
         .getWarning()
         .invoke("Incomplete analysis! The num of ${missClasses.size()} dependent classes cannot be found here. check: ${`$out`.getAbsolute().getNormalize()}");
   }

   @JvmStatic
   fun `fixInvalidInterface$lambda$2`(`$i`: SootClass, `$sc`: SootClass): Any {
      return "$`$i` is not a interface. but contains in interfaces of $`$sc`";
   }

   @JvmStatic
   fun `fixInvalidInterface$lambda$3`(`$i`: SootClass, `$sc`: SootClass): Any {
      return "remove interface $`$i` from $`$sc` failed";
   }

   @JvmStatic
   fun `removeLargeClasses$lambda$4`(`$sc`: SootClass, `$skipClassByMaximumMethods`: Int): Any {
      return "Remove large class: $`$sc` which is too large. Limit the class methods count should less than $`$skipClassByMaximumMethods`";
   }

   @JvmStatic
   fun `removeLargeClasses$lambda$5`(`$sc`: SootClass, `$skipClassByMaximumFields`: Int): Any {
      return "Remove big class: $`$sc` which is too large. Limit the class fields count should less than $`$skipClassByMaximumFields`";
   }

   @JvmStatic
   fun `logger$lambda$8`(): Unit {
      return Unit.INSTANCE;
   }
}
