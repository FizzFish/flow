package cn.sast.framework.entries.java

import cn.sast.dataflow.callgraph.TargetReachableMethods
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mu.KLogger
import soot.EntryPoints
import soot.MethodOrMethodContext
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Sources
import soot.util.Chain
import soot.util.queue.QueueReader

@SourceDebugExtension(["SMAP\nUnReachableEntryProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UnReachableEntryProvider.kt\ncn/sast/framework/entries/java/UnReachableEntryProvider\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,79:1\n1368#2:80\n1454#2,5:81\n774#2:86\n865#2,2:87\n*S KotlinDebug\n*F\n+ 1 UnReachableEntryProvider.kt\ncn/sast/framework/entries/java/UnReachableEntryProvider\n*L\n59#1:80\n59#1:81,5\n59#1:86\n59#1:87,2\n*E\n"])
public open class UnReachableEntryProvider(ctx: SootCtx, exclude: MutableSet<String> = (new LinkedHashSet()) as java.util.Set) : IEntryPointProvider {
   private final val ctx: SootCtx
   public final val exclude: MutableSet<String>
   public open val iterator: Flow<AnalyzeTask>

   init {
      this.ctx = ctx;
      this.exclude = exclude;
      this.iterator = FlowKt.flow((new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(this, null) {
         int label;

         {
            super(2, `$completionx`);
            this.this$0 = `$receiver`;
         }

         public final Object invokeSuspend(Object $result) {
            val var12: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
                  val `$this$filterTo$iv`: java.lang.Iterable = this.this$0.getEntryMethods();
                  val `destination$iv`: java.util.Collection = new LinkedHashSet();
                  val var6: UnReachableEntryProvider = this.this$0;

                  for (Object element$iv : $this$filterTo$iv) {
                     if (!var6.getExclude().contains((`element$iv` as SootMethod).getSignature())) {
                        `destination$iv`.add(`element$iv`);
                     }
                  }

                  val methods: java.util.Set = `destination$iv` as java.util.Set;
                  val var10001: IEntryPointProvider.AnalyzeTask = new IEntryPointProvider.AnalyzeTask(methods, this.this$0) {
                     private final java.util.Set<SootMethod> entries;
                     private final java.util.Set<SootClass> components;
                     private final java.lang.String name;

                     {
                        this.this$0 = `$receiver`;
                        this.entries = `$methods`;
                        this.name = "(entries size: ${`$methods`.size()})";
                     }

                     @Override
                     public java.util.Set<SootMethod> getEntries() {
                        return this.entries;
                     }

                     @Override
                     public java.util.Set<SootMethod> getMethodsMustAnalyze() {
                        val `$this$filterTo$ivx`: java.lang.Iterable = IEntryPointProvider.AnalyzeTask.DefaultImpls.getMethodsMustAnalyze(this);
                        val `destination$ivx`: java.util.Collection = new LinkedHashSet();
                        val var3x: UnReachableEntryProvider = this.this$0;

                        for (Object element$ivx : $this$filterTo$ivx) {
                           if (!var3x.getExclude().contains((`element$ivx` as SootMethod).getSignature())) {
                              `destination$ivx`.add(`element$ivx`);
                           }
                        }

                        return `destination$ivx` as MutableSet<SootMethod>;
                     }

                     @Override
                     public java.util.Set<SootClass> getComponents() {
                        return this.components;
                     }

                     @Override
                     public java.lang.String getName() {
                        return this.name;
                     }

                     @Override
                     public void needConstructCallGraph(SootCtx sootCtx) {
                     }

                     @Override
                     public java.util.Set<SootMethod> getAdditionalEntries() {
                        return IEntryPointProvider.AnalyzeTask.DefaultImpls.getAdditionalEntries(this);
                     }
                  };
                  val var10002: Continuation = this as Continuation;
                  this.label = 1;
                  if (`$this$flow`.emit(var10001, var10002) === var12) {
                     return var12;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            val var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   public open fun getEntryMethods(): Set<SootMethod> {
      val scene: Scene = Scene.v();
      val reachClasses: Chain = scene.getApplicationClasses();
      logger.info(UnReachableEntryProvider::getEntryMethods$lambda$0);
      var `$this$filter$iv`: java.lang.Iterable = reachClasses as java.lang.Iterable;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val var10000: java.util.List = (`element$iv$iv` as SootClass).getMethods();
         CollectionsKt.addAll(`destination$iv$iv`, var10000);
      }

      `$this$filter$iv` = `destination$iv$iv` as java.util.List;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         if (!scene.isExcluded((var18 as SootMethod).getDeclaringClass().getName()) || scene.isIncluded((var18 as SootMethod).getDeclaringClass().getName())) {
            `destination$iv$iv`.add(var18);
         }
      }

      return Companion.getEntryPoints(this.ctx, `destination$iv$iv` as MutableList<SootMethod>);
   }

   override fun startAnalyse() {
      IEntryPointProvider.DefaultImpls.startAnalyse(this);
   }

   override fun endAnalyse() {
      IEntryPointProvider.DefaultImpls.endAnalyse(this);
   }

   @JvmStatic
   fun `getEntryMethods$lambda$0`(`$reachClasses`: Chain): Any {
      return "reach classes num: ${`$reachClasses`.size()}";
   }

   @JvmStatic
   fun `logger$lambda$3`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger

      public fun getEntryPoints(ctx: SootCtx, methodsToFind: List<SootMethod>): MutableSet<SootMethod> {
         ctx.releaseCallGraph();
         UnReachableEntryProvider.access$getLogger$cp().info(UnReachableEntryProvider.Companion::getEntryPoints$lambda$0);
         UnReachableEntryProvider.access$getLogger$cp().info(UnReachableEntryProvider.Companion::getEntryPoints$lambda$1);
         val scene: Scene = Scene.v();
         val var10000: java.util.List = EntryPoints.v().all();
         scene.setEntryPoints(methodsToFind);
         ctx.constructCallGraph();
         val cg: CallGraph = ctx.getSootMethodCallGraph();
         val reachable: TargetReachableMethods = new TargetReachableMethods(cg, methodsToFind);
         reachable.update();
         val var8: java.util.Set = new LinkedHashSet();
         val it: java.util.Set = var8;
         val var14: QueueReader = reachable.listener();
         val iter: java.util.Iterator = var14 as java.util.Iterator;

         while (iter.hasNext()) {
            val cur: MethodOrMethodContext = iter.next() as MethodOrMethodContext;
            if (!new Sources(cg.edgesInto(cur)).hasNext() && cur.method().isConcrete()) {
               val var10001: SootMethod = cur.method();
               it.add(var10001);
            }
         }

         it.addAll(var10000);
         UnReachableEntryProvider.access$getLogger$cp().info(UnReachableEntryProvider.Companion::getEntryPoints$lambda$3$lambda$2);
         if (var8.isEmpty()) {
            UnReachableEntryProvider.access$getLogger$cp().warn("no entry points");
         }

         return var8;
      }

      @JvmStatic
      fun `getEntryPoints$lambda$0`(): Any {
         return "auto make the entry points by UnReachableMethodsFinder.";
      }

      @JvmStatic
      fun `getEntryPoints$lambda$1`(`$methodsToFind`: java.util.List): Any {
         return "reach methods num: ${`$methodsToFind`.size()}";
      }

      @JvmStatic
      fun `getEntryPoints$lambda$3$lambda$2`(`$it`: java.util.Set): Any {
         val var2: Array<Any> = new Object[]{`$it`.size()};
         val var10000: java.lang.String = java.lang.String.format("unreachable entry methods num :%d", Arrays.copyOf(var2, var2.length));
         return var10000;
      }
   }
}
