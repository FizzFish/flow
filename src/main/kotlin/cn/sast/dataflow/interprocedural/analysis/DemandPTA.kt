package cn.sast.dataflow.interprocedural.analysis

import cn.sast.idfa.analysis.Context
import cn.sast.idfa.analysis.InterproceduralCFG
import java.util.LinkedHashSet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.Local
import soot.PointsToAnalysis
import soot.PointsToSet
import soot.SootMethod
import soot.Unit
import soot.jimple.infoflow.data.AccessPath
import soot.jimple.spark.pag.PAG
import soot.jimple.spark.sets.PointsToSetInternal

@SourceDebugExtension(["SMAP\nDemandPTA.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DemandPTA.kt\ncn/sast/dataflow/interprocedural/analysis/DemandPTA\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,152:1\n1863#2:153\n1864#2:155\n1557#2:156\n1628#2,3:157\n808#2,11:160\n1#3:154\n*S KotlinDebug\n*F\n+ 1 DemandPTA.kt\ncn/sast/dataflow/interprocedural/analysis/DemandPTA\n*L\n70#1:153\n70#1:155\n115#1:156\n115#1:157,3\n115#1:160,11\n*E\n"])
public abstract class DemandPTA<V, CTX extends Context<SootMethod, Unit, IFact<V>>> : AJimpleInterProceduralAnalysis<V, CTX> {
   public final val pta: PointsToAnalysis

   public final var associationPTS: PointsToSetInternal?
      internal set

   public final var associationInstance: PointsToSetInternal?
      internal set

   public final var associationStmt: MutableSet<Unit>
      internal set

   open fun DemandPTA(pta: PointsToAnalysis, hf: AbstractHeapFactory<V>, icfg: InterproceduralCFG) {
      super(hf, icfg);
      this.pta = pta;
      this.associationStmt = new LinkedHashSet<>();
   }

   public abstract fun getLocals(): Set<Pair<Unit?, AccessPath>> {
   }

   public override fun doAnalysis(entries: Collection<SootMethod>) {
      if (this.pta is PAG) {
         val associationInstance: PointsToSetInternal = (this.pta as PAG).getSetFactory().newSet(null, this.pta as PAG);
         val associationPTS: PointsToSetInternal = (this.pta as PAG).getSetFactory().newSet(null, this.pta as PAG);

         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val u: Unit = (`element$iv` as Pair).component1() as Unit;
            val accessPath: AccessPath = (`element$iv` as Pair).component2() as AccessPath;
            if (u != null) {
               this.associationStmt.add(u);
            }

            val var10000: PointsToSet;
            if (accessPath.getFirstFragment() != null) {
               val instance: PointsToSet = (this.pta as PAG).reachingObjects(accessPath.getPlainValue());
               if (instance != null && instance is PointsToSetInternal) {
                  associationInstance.addAll(instance as PointsToSetInternal, null);
               }

               var10000 = (this.pta as PAG).reachingObjects(accessPath.getPlainValue(), accessPath.getFirstFragment().getField());
            } else {
               var10000 = (this.pta as PAG).reachingObjects(accessPath.getPlainValue());
            }

            if (var10000 != null && var10000 is PointsToSetInternal) {
               associationPTS.addAll(var10000 as PointsToSetInternal, null);
            }
         }

         this.associationInstance = associationInstance;
         this.associationPTS = associationPTS;
      } else {
         logger.error(DemandPTA::doAnalysis$lambda$2);
      }

      super.doAnalysis(entries);
   }

   public fun isAssociation(l: Local): Boolean {
      return this.associationPTS == null || this.associationPTS.hasNonEmptyIntersection(this.pta.reachingObjects(l));
   }

   public fun isAssociationInstance(l: Local): Boolean {
      return this.associationInstance == null || this.associationInstance.hasNonEmptyIntersection(this.pta.reachingObjects(l));
   }

   public override suspend fun normalFlowFunction(context: Any, node: Unit, succ: Unit, inValue: IFact<Any>, isNegativeBranch: AtomicBoolean): IFact<Any> {
      return normalFlowFunction$suspendImpl(this, (CTX)context, node, succ, inValue, isNegativeBranch, `$completion`);
   }

   @JvmStatic
   fun `doAnalysis$lambda$2`(`this$0`: DemandPTA): Any {
      return "error pta type: ${`this$0`.pta.getClass()}";
   }

   @JvmStatic
   fun `logger$lambda$4`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   public companion object {
      private final var logger: KLogger
   }
}
