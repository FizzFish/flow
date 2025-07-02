package cn.sast.dataflow.infoflow

import cn.sast.dataflow.infoflow.svfa.SparseInfoFlowSolver
import java.io.File
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import mu.KotlinLogging
import soot.SootMethod
import soot.jimple.infoflow.AbstractInfoflow
import soot.jimple.infoflow.BackwardsInfoflow
import soot.jimple.infoflow.Infoflow
import soot.jimple.infoflow.InfoflowConfiguration.DataFlowDirection
import soot.jimple.infoflow.InfoflowConfiguration.DataFlowSolver
import soot.jimple.infoflow.InfoflowConfiguration.SolverConfiguration
import soot.jimple.infoflow.cfg.BiDirICFGFactory
import soot.jimple.infoflow.data.Abstraction
import soot.jimple.infoflow.problems.AbstractInfoflowProblem
import soot.jimple.infoflow.problems.BackwardsInfoflowProblem
import soot.jimple.infoflow.problems.InfoflowProblem
import soot.jimple.infoflow.problems.TaintPropagationResults
import soot.jimple.infoflow.problems.TaintPropagationResults.OnTaintPropagationResultAdded
import soot.jimple.infoflow.solver.IInfoflowSolver
import soot.jimple.infoflow.solver.executors.InterruptableExecutor

@SourceDebugExtension(["SMAP\nFlowDroidFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FlowDroidFactory.kt\ncn/sast/dataflow/infoflow/FlowDroidFactory\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,136:1\n1#2:137\n*E\n"])
public object FlowDroidFactory {
   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(FlowDroidFactory::logger$lambda$3)

   public fun createInfoFlow(
      dataFlowDirection: DataFlowDirection,
      androidPlatformDir: String?,
      forceAndroidJar: Boolean?,
      lifecycleMethods: Collection<SootMethod>?,
      cfgFactory: BiDirICFGFactory?,
      useSparseOpt: Boolean,
      resultAddedHandlers: Set<OnTaintPropagationResultAdded>
   ): AbstractInfoflow {
      if (androidPlatformDir == null && forceAndroidJar != null) {
         throw new IllegalArgumentException("androidPlatformDir not special".toString());
      } else if (androidPlatformDir != null && forceAndroidJar == null) {
         throw new IllegalArgumentException("forceAndroidJar not special".toString());
      } else if (androidPlatformDir != null
         && forceAndroidJar != null
         && (!StringsKt.endsWith$default(androidPlatformDir, ".jar", false, 2, null) || !forceAndroidJar)
         && (!new File(androidPlatformDir).isDirectory() || forceAndroidJar)) {
         throw new IllegalArgumentException(("error androidPlatformDir: $androidPlatformDir and forceAndroidJar: $forceAndroidJar").toString());
      } else {
         return if (dataFlowDirection === DataFlowDirection.Forwards)
            (
               new Infoflow(lifecycleMethods, androidPlatformDir, cfgFactory, resultAddedHandlers, useSparseOpt, forceAndroidJar != null && forceAndroidJar) {
                  {
                     super(`$androidPlatformDir`, `$super_call_param$1`, `$cfgFactory`);
                     this.$resultAddedHandlers = `$resultAddedHandlers`;
                     this.$useSparseOpt = `$useSparseOpt`;
                     this.additionalEntryPointMethods = `$lifecycleMethods`;
                  }

                  protected InfoflowProblem createInfoflowProblem(Abstraction zeroValue) {
                     val var10000: InfoflowProblem = super.createInfoflowProblem(zeroValue);
                     val var10: TaintPropagationResults = var10000.getResults();
                     val propagationResults: TaintPropagationResults = var10;

                     val `$this$forEach$iv`: java.lang.Iterable;
                     for (Object element$iv : $this$forEach$iv) {
                        propagationResults.addResultAvailableHandler(`element$iv` as OnTaintPropagationResultAdded);
                     }

                     return var10000;
                  }

                  protected IInfoflowSolver createDataFlowSolver(
                     InterruptableExecutor executor, AbstractInfoflowProblem problem, SolverConfiguration solverConfig
                  ) {
                     if (!this.$useSparseOpt) {
                        FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$1);
                        val var6: IInfoflowSolver = super.createDataFlowSolver(executor, problem, solverConfig);
                        return var6;
                     } else {
                        val var10000: DataFlowSolver = solverConfig.getDataFlowSolver();
                        switch (var10000 == null ? -1 : WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
                           case 1:
                              FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$2);
                              return (new SparseInfoFlowSolver(problem, executor)) as IInfoflowSolver;
                           case 2:
                              FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$3);
                              val var5: cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver = new cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver(
                                 problem, executor
                              );
                              this.solverPeerGroup.addSolver(var5 as IInfoflowSolver);
                              var5.setPeerGroup(this.solverPeerGroup);
                              return var5 as IInfoflowSolver;
                           default:
                              throw new NotImplementedError(
                                 "An operation is not implemented: Sparse opt not support the ${solverConfig.getDataFlowSolver()} solver yet"
                              );
                        }
                     }
                  }

                  private static final Object createDataFlowSolver$lambda$1() {
                     return "Using forward solver with no sparse opt";
                  }

                  private static final Object createDataFlowSolver$lambda$2() {
                     return "Using context- and flow-sensitive solver with sparse opt";
                  }

                  private static final Object createDataFlowSolver$lambda$3() {
                     return "Using garbage-collecting solver with sparse opt";
                  }
               }
            ) as AbstractInfoflow
            else
            (
               new BackwardsInfoflow(
                  lifecycleMethods, androidPlatformDir, cfgFactory, resultAddedHandlers, useSparseOpt, forceAndroidJar != null && forceAndroidJar
               ) {
                  {
                     super(`$androidPlatformDir`, `$super_call_param$1`, `$cfgFactory`);
                     this.$resultAddedHandlers = `$resultAddedHandlers`;
                     this.$useSparseOpt = `$useSparseOpt`;
                     this.additionalEntryPointMethods = `$lifecycleMethods`;
                  }

                  protected BackwardsInfoflowProblem createInfoflowProblem(Abstraction zeroValue) {
                     val var10000: BackwardsInfoflowProblem = super.createInfoflowProblem(zeroValue);
                     val var10: TaintPropagationResults = var10000.getResults();
                     val propagationResults: TaintPropagationResults = var10;

                     val `$this$forEach$iv`: java.lang.Iterable;
                     for (Object element$iv : $this$forEach$iv) {
                        propagationResults.addResultAvailableHandler(`element$iv` as OnTaintPropagationResultAdded);
                     }

                     return var10000;
                  }

                  protected IInfoflowSolver createDataFlowSolver(
                     InterruptableExecutor executor, AbstractInfoflowProblem problem, SolverConfiguration solverConfig
                  ) {
                     if (!this.$useSparseOpt) {
                        FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$1);
                        val var6: IInfoflowSolver = super.createDataFlowSolver(executor, problem, solverConfig);
                        return var6;
                     } else {
                        val var10000: DataFlowSolver = solverConfig.getDataFlowSolver();
                        switch (var10000 == null ? -1 : WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
                           case 1:
                              FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$2);
                              return (new SparseInfoFlowSolver(problem, executor)) as IInfoflowSolver;
                           case 2:
                              FlowDroidFactory.access$getLogger$p().info(<unrepresentable>::createDataFlowSolver$lambda$3);
                              val var5: cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver = new cn.sast.dataflow.infoflow.svfa.gcSolver.SparseInfoFlowSolver(
                                 problem, executor
                              );
                              this.solverPeerGroup.addSolver(var5 as IInfoflowSolver);
                              var5.setPeerGroup(this.solverPeerGroup);
                              return var5 as IInfoflowSolver;
                           default:
                              throw new NotImplementedError(
                                 "An operation is not implemented: Sparse opt not support the ${solverConfig.getDataFlowSolver()} solver yet"
                              );
                        }
                     }
                  }

                  private static final Object createDataFlowSolver$lambda$1() {
                     return "Using backward solver with no sparse opt";
                  }

                  private static final Object createDataFlowSolver$lambda$2() {
                     return "Using context- and flow-sensitive solver with sparse opt";
                  }

                  private static final Object createDataFlowSolver$lambda$3() {
                     return "Using garbage-collecting solver with sparse opt";
                  }
               }
            ) as AbstractInfoflow;
      }
   }

   @JvmStatic
   fun `logger$lambda$3`(): Unit {
      return Unit.INSTANCE;
   }
}
