package cn.sast.dataflow.interprocedural.check.printer

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.ClassResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.dataflow.interprocedural.check.ExitInvoke
import cn.sast.dataflow.interprocedural.check.IPath
import cn.sast.dataflow.interprocedural.check.InvokeEdgePath
import cn.sast.dataflow.interprocedural.check.ModelBind
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.Language
import com.feysh.corax.config.api.report.Region
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentList
import soot.Local
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.Unit
import soot.Value
import soot.jimple.AssignStmt
import soot.jimple.InvokeExpr
import soot.jimple.Stmt
import soot.tagkit.AbstractHost
import soot.tagkit.Host

@SourceDebugExtension(["SMAP\nPathDiagnosticsGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathDiagnosticsGenerator.kt\ncn/sast/dataflow/interprocedural/check/printer/PathDiagnosticsGenerator\n+ 2 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Region.kt\ncom/feysh/corax/config/api/report/Region\n*L\n1#1,211:1\n303#2:212\n1#3:213\n1#3:214\n1#3:216\n59#4:215\n57#4:217\n*S KotlinDebug\n*F\n+ 1 PathDiagnosticsGenerator.kt\ncn/sast/dataflow/interprocedural/check/printer/PathDiagnosticsGenerator\n*L\n40#1:212\n40#1:213\n85#1:216\n85#1:215\n85#1:217\n*E\n"])
public class PathDiagnosticsGenerator(info: SootInfoCache?, icfg: InterproceduralCFG, deep: Int) {
   public final val info: SootInfoCache?
   public final val icfg: InterproceduralCFG
   public final val deep: Int
   private final val result: ArrayDeque<BugPathEvent>
   private final val prefix: String
   private final val calleePrefix: String

   init {
      this.info = info;
      this.icfg = icfg;
      this.deep = deep;
      this.result = new ArrayDeque();
      this.prefix = "[${this.deep}]";
      this.calleePrefix = "[${this.deep + 1}]";
   }

   internal fun emit(node: IPath, message: EventPrinter): List<BugPathEvent> {
      return this.emit(node.getNode(), message.getMessage(), message.getRegion());
   }

   public fun emit(node: Unit, message: Map<Language, String>?, region: Region?): List<BugPathEvent> {
      if (message == null) {
         return CollectionsKt.emptyList();
      } else {
         val clazz: SootClass = this.icfg.getMethodOf(node).getDeclaringClass();
         val var10000: InvokeExpr = if ((node as? Stmt) != null) (if ((node as Stmt).containsInvokeExpr()) (node as Stmt).getInvokeExpr() else null) else null;
         val methodRef: SootMethodRef = if (var10000 != null) var10000.getMethodRef() else null;
         if (methodRef != null
            && (methodRef !is SootMethod || (methodRef as SootMethod).isDeclared())
            && methodRef.getDeclaringClass().getName() == "java.lang.String") {
            val var18: java.lang.String = methodRef.getName();
            if (StringsKt.contains(var18, "equals", true)) {
               return CollectionsKt.emptyList();
            }
         }

         if (clazz.isJavaLibraryClass()) {
            return CollectionsKt.emptyList();
         } else {
            var var19: java.util.Map = message;
            val var10001: ClassResInfo.Companion = ClassResInfo.Companion;
            val var20: IBugResInfo = var10001.of(clazz);
            var var10002: Region = region;
            if (region == null) {
               if (this.info != null) {
                  val var12: Region = Region.Companion.invoke(this.info, node as Host);
                  var19 = message;
                  var10002 = var12;
               } else {
                  var10002 = null;
               }

               if (var10002 == null) {
                  var10002 = Region.Companion.invoke(node);
                  if (var10002 == null) {
                     var10002 = Region.Companion.getERROR();
                  }
               }
            }

            return CollectionsKt.listOf(new BugPathEvent(var19, var20, var10002, this.deep));
         }
      }
   }

   internal fun emit(method: SootMethod, message: EventPrinter?): List<BugPathEvent> {
      return if (message == null) CollectionsKt.emptyList() else this.emit(method, message.getMessage(), message.getRegion());
   }

   public fun emit(method: SootMethod, message: Map<Language, String>?, region: Region?): List<BugPathEvent> {
      if (message == null) {
         return CollectionsKt.emptyList();
      } else {
         val clazz: SootClass = method.getDeclaringClass();
         if (method.isDeclared() && method.getDeclaringClass().getName() == "java.lang.String") {
            val var10000: java.lang.String = method.getName();
            if (StringsKt.contains(var10000, "equals", true)) {
               return CollectionsKt.emptyList();
            }
         }

         if (clazz.isJavaLibraryClass()) {
            return CollectionsKt.emptyList();
         } else {
            var var20: java.util.Map = message;
            val var10001: ClassResInfo.Companion = ClassResInfo.Companion;
            val var21: IBugResInfo = var10001.of(clazz);
            var var10002: Region = region;
            if (region == null) {
               if (this.info != null) {
                  val var14: Region = Region.Companion.invoke(this.info, method as AbstractHost);
                  var20 = message;
                  var10002 = var14;
               } else {
                  var10002 = null;
               }

               if (var10002 == null) {
                  val `this_$iv`: Region = new Region(method.getJavaSourceStartLineNumber(), method.getJavaSourceStartColumnNumber(), -1, -1);
                  var10002 = if (`this_$iv`.startLine >= 0) `this_$iv` else null;
                  if ((if (`this_$iv`.startLine >= 0) `this_$iv` else null) == null) {
                     var10002 = Region.Companion.getERROR();
                  }
               }
            }

            return CollectionsKt.listOf(new BugPathEvent(var20, var21, var10002, this.deep));
         }
      }
   }

   public fun inlineAssignment(pathEvents: List<IndexedValue<IPath>>, index: Int, inlined: MutableSet<Int>, type: KClass<out IPath>): Unit {
      val pathEvent: IPath = (pathEvents.get(index) as IndexedValue).getValue() as IPath;
      return (new GrimpInline(index, pathEvents, pathEvent, type, inlined) {
            private java.util.Set<Value> visited;

            {
               this.$index = `$index`;
               this.$pathEvents = `$pathEvents`;
               this.$pathEvent = `$pathEvent`;
               this.$type = `$type`;
               this.$inlined = `$inlined`;
               this.visited = new LinkedHashSet<>();
            }

            public final java.util.Set<Value> getVisited() {
               return this.visited;
            }

            public final void setVisited(java.util.Set<Value> var1) {
               this.visited = var1;
            }

            @Override
            public Value newExpr(Value value) {
               var result: Value = null;
               if (!this.visited.add(value)) {
                  return value;
               } else {
                  if (value is Local) {
                     val reverseIndex: Int = this.$index;

                     while (--reverseIndex >= 0) {
                        val defUnitPathNode: IPath = this.$pathEvents.get(reverseIndex).getValue() as IPath;
                        val defUnit: Unit = defUnitPathNode.getNode();
                        if (!(defUnit == this.$pathEvent.getNode())
                           && this.$type.isInstance(defUnitPathNode)
                           && defUnit is AssignStmt
                           && (defUnit as AssignStmt).getJavaSourceStartLineNumber() == this.$pathEvent.getNode().getJavaSourceStartLineNumber()
                           && (defUnit as AssignStmt).getJavaSourceStartLineNumber() > 0) {
                           val var10000: Value = (defUnit as AssignStmt).getLeftOp();
                           if (var10000 as Local == value) {
                              result = (defUnit as AssignStmt).getRightOp();
                              this.$inlined.add(reverseIndex);
                              break;
                           }
                        }
                     }
                  }

                  return if (result != null) super.newExpr(result) else super.newExpr(value);
               }
            }
         })
         .inline(pathEvent.getNode());
   }

   private fun inlinePathEvents(pathEvents: List<IndexedValue<IPath>>, index: Int, inlined: MutableSet<Int>): List<BugPathEvent> {
      val pathEventI: IndexedValue = pathEvents.get(index) as IndexedValue;
      val pathEvent: IPath = pathEventI.getValue() as IPath;
      val lineUnit: Unit = this.inlineAssignment(pathEvents, index, inlined, pathEvent.getClass()::class);
      val sootMethod: SootMethod = this.icfg.getMethodOf(pathEvent.getNode());
      val var10000: java.util.List;
      if (pathEvent is ModelBind) {
         var10000 = this.emit$corax_data_flow(pathEvent, new EventPrinter(this.prefix).printModeling(pathEvent as ModelBind, lineUnit, sootMethod));
      } else {
         val n: java.util.List = this.emit$corax_data_flow(pathEvent, new EventPrinter(this.prefix).normalPrint(pathEventI, lineUnit, sootMethod));
         var10000 = if (pathEvent is InvokeEdgePath)
            CollectionsKt.plus(
               n,
               this.emit$corax_data_flow(
                  (pathEvent as InvokeEdgePath).getCallee(), new EventPrinter(this.calleePrefix).calleeBeginPrint(pathEvent as InvokeEdgePath)
               )
            )
            else
            n;
      }

      return var10000;
   }

   public fun inlineBugPathEvents(pathEvents: List<BugPathEvent>) {
      CollectionsKt.addAll(this.result as java.util.Collection, pathEvents);
   }

   public fun inlinePathEvents(pathEvents: List<IndexedValue<IPath>>) {
      val inlined: java.util.Set = new LinkedHashSet();
      val insertIndex: Int = this.result.size();
      val index: Int = pathEvents.size();

      while (--index >= 0) {
         if (!inlined.contains(index)) {
            this.result.addAll(insertIndex, this.inlinePathEvents(pathEvents, index, inlined));
         }
      }
   }

   public fun inlineEvents(pathEvents: PersistentList<Any>): List<BugPathEvent> {
      val curEvents: java.util.List = new ArrayList();
      val curPathEvents: java.util.List = new ArrayList();
      var i: Int = 0;

      for (Object event : pathEvents) {
         if (event is BugPathEvent) {
            if (!curPathEvents.isEmpty()) {
               this.inlinePathEvents(curPathEvents);
               curPathEvents.clear();
            }

            curEvents.add(event);
         } else if (event is IPath) {
            if (!curEvents.isEmpty()) {
               this.inlineBugPathEvents(curEvents);
               curEvents.clear();
            }

            curPathEvents.add(new IndexedValue(i, event));
         } else {
            if (event !is ExitInvoke) {
               throw new IllegalStateException("internal error".toString());
            }

            if (!curPathEvents.isEmpty()) {
               this.inlinePathEvents(curPathEvents);
               curPathEvents.clear();
            }

            if (!curEvents.isEmpty()) {
               this.inlineBugPathEvents(curEvents);
               curEvents.clear();
            }

            CollectionsKt.addAll(
               this.result as java.util.Collection,
               this.emit$corax_data_flow((event as ExitInvoke).getInvoke(), new EventPrinter(this.prefix).normalPrint(event as ExitInvoke))
            );
         }

         i++;
      }

      if (!curEvents.isEmpty()) {
         this.inlineBugPathEvents(curEvents);
      }

      if (!curPathEvents.isEmpty()) {
         this.inlinePathEvents(curPathEvents);
      }

      return this.result as MutableList<BugPathEvent>;
   }
}
