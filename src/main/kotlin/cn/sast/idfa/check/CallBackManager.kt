package cn.sast.idfa.check

import cn.sast.api.util.SootUtilsKt
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.jvm.internal.SourceDebugExtension
import soot.SootClass
import soot.SootMethod
import soot.Unit
import soot.toolkits.graph.UnitGraph

@SourceDebugExtension(["SMAP\nCheckerManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerManager.kt\ncn/sast/idfa/check/CallBackManager\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,170:1\n137#1,5:215\n381#2,7:171\n381#2,7:178\n381#2,7:187\n381#2,7:194\n381#2,7:201\n381#2,7:208\n183#3,2:185\n1628#4,3:220\n*S KotlinDebug\n*F\n+ 1 CheckerManager.kt\ncn/sast/idfa/check/CallBackManager\n*L\n147#1:215,5\n88#1:171,7\n89#1:178,7\n105#1:187,7\n111#1:194,7\n127#1:201,7\n128#1:208,7\n97#1:185,2\n163#1:220,3\n*E\n"])
public open class CallBackManager {
   public final val callBacksOfMethod: MutableMap<Class<out IStmtCB>, MutableMap<SootMethod, MutableList<Any>>> = (new LinkedHashMap()) as java.util.Map
   public final val callBacksOfUnit: MutableMap<Class<out IStmtCB>, MutableMap<Unit, MutableList<Any>>> = (new LinkedHashMap()) as java.util.Map
   private final val jimpleOverride: MutableMap<SootMethod, UnitGraph> = (new LinkedHashMap()) as java.util.Map
   public final val miss: MutableMap<Class<out IStmtCB>, MutableSet<SootMethod>> = (new LinkedHashMap()) as java.util.Map
   public final val hit: MutableMap<Class<out IStmtCB>, MutableSet<Pair<SootMethod, SootMethod>>> = (new LinkedHashMap()) as java.util.Map

   public fun <typeCB> put(x: Class<out IStmtCB>, key: SootMethod, cb: (typeCB, Continuation<kotlin.Unit>) -> Any?) {
      val `$this$getOrPut$iv`: java.util.Map = this.callBacksOfMethod;
      var `value$iv`: Any = this.callBacksOfMethod.get(x);
      var var10000: Any;
      if (`value$iv` == null) {
         val var11: Any = new LinkedHashMap();
         `$this$getOrPut$iv`.put(x, var11);
         var10000 = var11;
      } else {
         var10000 = `value$iv`;
      }

      val map: java.util.Map = var10000 as java.util.Map;
      `value$iv` = (var10000 as java.util.Map).get(key);
      if (`value$iv` == null) {
         val var13: Any = new ArrayList();
         map.put(key, var13);
         var10000 = var13;
      } else {
         var10000 = `value$iv`;
      }

      (var10000 as java.util.List).add(cb);
   }

   public open fun <typeCB> get(x: Class<out IStmtCB>, method: SootMethod): List<(typeCB, Continuation<kotlin.Unit>) -> Any?>? {
      val subSignature: java.lang.String = method.getSubSignature();
      var var10000: SootClass = method.getDeclaringClass();
      val var8: java.util.Iterator = SootUtilsKt.findMethodOrNull(var10000, subSignature).iterator();

      while (true) {
         if (var8.hasNext()) {
            val `$this$getOrPut$iv`: Any = var8.next();
            if (this.getRaw(x, `$this$getOrPut$iv` as SootMethod) == null) {
               continue;
            }

            var10000 = (SootClass)`$this$getOrPut$iv`;
            break;
         }

         var10000 = null;
         break;
      }

      val targetCB: SootMethod = var10000 as SootMethod;
      if (var10000 as SootMethod != null && (var10000 as SootMethod == method || !(var10000 as SootMethod).isConcrete())) {
         val var16: java.util.List = this.getRaw(x, targetCB);
         if (var16 != null) {
            synchronized (this.hit) {
               val var25: java.util.Map = this.hit;
               val var30: Any = this.hit.get(x);
               if (var30 == null) {
                  val var31: Any = new LinkedHashSet();
                  var25.put(x, var31);
                  var10000 = (SootClass)var31;
               } else {
                  var10000 = (SootClass)var30;
               }

               val var23: Boolean = (var10000 as java.util.Set).add(TuplesKt.to(method, targetCB));
               return var16;
            }
         }
      }

      synchronized (this.miss) {
         val var21: java.util.Map = this.miss;
         val var26: Any = this.miss.get(x);
         if (var26 == null) {
            val var29: Any = new LinkedHashSet();
            var21.put(x, var29);
            var10000 = (SootClass)var29;
         } else {
            var10000 = (SootClass)var26;
         }

         val var19: Boolean = (var10000 as java.util.Set).add(method);
         return null;
      }
   }

   public open fun <typeCB> getRaw(x: Class<out IStmtCB>, key: SootMethod): List<(typeCB, Continuation<kotlin.Unit>) -> Any?>? {
      val var10000: java.util.Map = this.callBacksOfMethod.get(x);
      return if (var10000 != null) var10000.get(key) as java.util.List else null;
   }

   public fun <typeCB> put(x: Class<out IStmtCB>, key: Unit, cb: (typeCB) -> kotlin.Unit) {
      val `$this$getOrPut$iv`: java.util.Map = this.callBacksOfUnit;
      var `value$iv`: Any = this.callBacksOfUnit.get(x);
      var var10000: Any;
      if (`value$iv` == null) {
         val var11: Any = new LinkedHashMap();
         `$this$getOrPut$iv`.put(x, var11);
         var10000 = var11;
      } else {
         var10000 = `value$iv`;
      }

      val map: java.util.Map = var10000 as java.util.Map;
      `value$iv` = (var10000 as java.util.Map).get(key);
      if (`value$iv` == null) {
         val var13: Any = new ArrayList();
         map.put(key, var13);
         var10000 = var13;
      } else {
         var10000 = `value$iv`;
      }

      (var10000 as java.util.List).add(cb);
   }

   public open fun <typeCB> get(x: Class<out IStmtCB>, key: Unit): List<(typeCB, Continuation<kotlin.Unit>) -> Any?>? {
      val var10000: java.util.Map = this.callBacksOfUnit.get(x);
      return if (var10000 != null) var10000.get(key) as java.util.List else null;
   }

   public fun putUnitGraphOverride(key: SootMethod, override: UnitGraph): UnitGraph? {
      return this.jimpleOverride.put(key, override);
   }

   public fun getUnitGraphOverride(key: SootMethod): UnitGraph? {
      return this.jimpleOverride.get(key);
   }

   public fun reportMissSummaryMethod(reportMissingMethod: (SootMethod) -> kotlin.Unit) {
      val miss: java.lang.Iterable = CollectionsKt.flatten(this.hit.values());
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : miss) {
         `destination$iv`.add((`item$iv` as Pair).getFirst() as SootMethod);
      }

      for (SootMethod m : SetsKt.minus(CollectionsKt.toSet(CollectionsKt.flatten(this.miss.values())), (java.util.Set)destination$iv)) {
         reportMissingMethod.invoke(var13);
      }
   }
}
