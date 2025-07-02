package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.graph.HashMutableDirectedGraph
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import soot.toolkits.graph.MutableDirectedGraph

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/DependsGraph\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,395:1\n381#2,7:396\n1317#3,2:403\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/DependsGraph\n*L\n157#1:396,7\n168#1:403,2\n*E\n"])
public class DependsGraph(factory: ModifyInfoFactory) : IncrementalAnalyzeByChangeFiles.IDependsGraph {
   public open val factory: ModifyInfoFactory
   private final val dependenceGraph: MutableDirectedGraph<XDecl>
   private final val patchRelateAnalysisTargets: MutableMap<String, MutableSet<XDecl>>
   private final val patchRelateObjects: MutableSet<XDecl>
   private final val patchRelateChangedWalk: MutableSet<XDecl>
   private final var isOld: Boolean

   init {
      this.factory = factory;
      this.dependenceGraph = new HashMutableDirectedGraph<>();
      this.patchRelateAnalysisTargets = new LinkedHashMap<>();
      this.patchRelateObjects = new LinkedHashSet<>();
      this.patchRelateChangedWalk = new LinkedHashSet<>();
   }

   public override infix fun Collection<XDecl>.dependsOn(deps: Collection<XDecl>) {
      for (XDecl n : $this$dependsOn) {
         for (XDecl d : deps) {
            this.dependsOn(n, d);
         }
      }
   }

   public override fun visitChangedDecl(diffPath: String, changed: Collection<XDecl>) {
      for (XDecl e : changed) {
         val `$this$getOrPut$iv`: java.util.Map = this.patchRelateAnalysisTargets;
         val `value$iv`: Any = this.patchRelateAnalysisTargets.get(diffPath);
         val var10000: Any;
         if (`value$iv` == null) {
            val var9: Any = new LinkedHashSet();
            `$this$getOrPut$iv`.put(diffPath, var9);
            var10000 = var9;
         } else {
            var10000 = `value$iv`;
         }

         (var10000 as java.util.Set).add(e);
         this.patchRelateObjects.add(e);
      }

      this.patchRelateChangedWalk.clear();
      this.isOld = true;
   }

   private fun walkALl() {
      if (this.isOld) {
         label60: {
            synchronized (this){} // $VF: monitorenter 

            label31: {
               try {
                  if (!this.isOld) {
                     break label31;
                  }

                  val `$this$forEach$iv`: Sequence;
                  for (Object element$iv : $this$forEach$iv) {
                     this.patchRelateChangedWalk.add(`element$iv` as XDecl);
                  }

                  this.isOld = false;
               } catch (var9: java.lang.Throwable) {
                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }

            // $VF: monitorexit
         }
      }
   }

   public override infix fun XDecl.dependsOn(dep: XDecl) {
      this.dependenceGraph.addEdge(`$this$dependsOn`, dep);
      this.isOld = true;
   }

   private fun walk(node: Collection<XDecl?>, forward: Boolean): Sequence<XDecl> {
      return SequencesKt.sequence(
         (
            new Function2<SequenceScope<? super XDecl>, Continuation<? super Unit>, Object>(node, forward, this, null)// $VF: Couldn't be decompiled
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

   public override fun targetsRelate(targets: Collection<XDecl>): Sequence<XDecl> {
      return SequencesKt.plus(this.walk(targets, true), this.walk(targets, false));
   }

   public override fun toDecl(target: Any): XDecl {
      return this.getFactory().toDecl(target);
   }

   public override fun targetRelate(target: XDecl): Sequence<XDecl> {
      return this.targetsRelate(CollectionsKt.listOf(target));
   }

   public override fun shouldReAnalyzeDecl(target: XDecl): ScanAction {
      this.walkALl();
      val actionByFactory: ProcessRule.ScanAction = this.getFactory().getScanAction(target);
      if (actionByFactory != ProcessRule.ScanAction.Keep) {
         return actionByFactory;
      } else {
         return if (this.patchRelateChangedWalk.contains(target)) ProcessRule.ScanAction.Process else ProcessRule.ScanAction.Skip;
      }
   }

   public override fun shouldReAnalyzeTarget(target: Any): ScanAction {
      return this.shouldReAnalyzeDecl(this.getFactory().toDecl(target));
   }
}
