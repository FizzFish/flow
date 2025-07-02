package cn.sast.dataflow.callgraph

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import soot.MethodOrMethodContext
import soot.Scene
import soot.SootMethod
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.ReachableMethods
import soot.util.queue.QueueReader

public fun reachableMethodSequence(entryPoints: Collection<MethodOrMethodContext>): Sequence<SootMethod> {
   return SequencesKt.sequence((new Function2<SequenceScope<? super SootMethod>, Continuation<? super Unit>, Object>(entryPoints, null) {
      Object L$1;
      int label;

      {
         super(2, `$completionx`);
         this.$entryPoints = `$entryPoints`;
      }

      // $VF: Irreducible bytecode was duplicated to produce valid code
      public final Object invokeSuspend(Object $result) {
         val var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         var `$this$sequence`: SequenceScope;
         var reachableChangedListener: java.util.Iterator;
         switch (this.label) {
            case 0:
               ResultKt.throwOnFailure(`$result`);
               `$this$sequence` = this.L$0 as SequenceScope;
               val temp: ReachableMethods = new ReachableMethods(Scene.v().getCallGraph(), this.$entryPoints);
               temp.update();
               val m: QueueReader = temp.listener();
               reachableChangedListener = m as java.util.Iterator;
               break;
            case 1:
               reachableChangedListener = this.L$1 as java.util.Iterator;
               `$this$sequence` = this.L$0 as SequenceScope;
               ResultKt.throwOnFailure(`$result`);
               break;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         while (reachableChangedListener.hasNext()) {
            val var10000: SootMethod = (reachableChangedListener.next() as MethodOrMethodContext).method();
            val var10002: Continuation = this as Continuation;
            this.L$0 = `$this$sequence`;
            this.L$1 = reachableChangedListener;
            this.label = 1;
            if (`$this$sequence`.yield(var10000, var10002) === var7) {
               return var7;
            }
         }

         return Unit.INSTANCE;
      }

      public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
         val var3: Function2 = new <anonymous constructor>(this.$entryPoints, `$completion`);
         var3.L$0 = value;
         return var3 as Continuation<Unit>;
      }

      public final Object invoke(SequenceScope<? super SootMethod> p1, Continuation<? super Unit> p2) {
         return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
      }
   }) as Function2);
}

public fun reachableStmtSequence(entryPoints: Collection<MethodOrMethodContext>): Sequence<Stmt> {
   return SequencesKt.sequence(
      (
         new Function2<SequenceScope<? super Stmt>, Continuation<? super Unit>, Object>(entryPoints, null)// $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getVarDefinitions()" because "stat" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1468)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingExprent(VarDefinitionHelper.java:1679)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1496)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.iterateClashingNames(VarDefinitionHelper.java:1545)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.remapClashingNames(VarDefinitionHelper.java:1458)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.rerunClashing(VarProcessor.java:99)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:118)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:352)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:407)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:761)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.wrapOperandString(FunctionExprent.java:727)
      
      ) as Function2
   );
}
