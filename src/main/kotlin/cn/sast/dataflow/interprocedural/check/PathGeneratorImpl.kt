package cn.sast.dataflow.interprocedural.check

import cn.sast.api.report.BugPathEvent
import cn.sast.dataflow.interprocedural.check.printer.PathDiagnosticsGenerator
import cn.sast.graph.HashMutableDirectedGraph
import cn.sast.idfa.analysis.InterproceduralCFG
import com.feysh.corax.cache.analysis.SootInfoCache
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentList
import mu.KLogger
import soot.toolkits.graph.DirectedGraph

public class PathGeneratorImpl {
   public companion object {
      public final val dataFlowResultPathOnlyStmt: Boolean

      public fun getPathGenerator(): PathGenerator<IPath> {
         return new PathGenerator<IPath>() {
            public boolean getShouldExplore(IPath $this$shouldExplore) {
               return `$this$shouldExplore` !is UnknownPath;
            }

            public java.util.Collection<IPath> getPreds(IPath $this$preds) {
               val var10000: java.util.Collection;
               if (`$this$preds` is ModelBind) {
                  var10000 = CollectionsKt.listOf((`$this$preds` as ModelBind).getPrev());
               } else if (`$this$preds` is MergePath) {
                  var10000 = (`$this$preds` as MergePath).getAll() as java.util.Collection;
               } else if (`$this$preds` is AssignLocalPath) {
                  var10000 = CollectionsKt.listOf((`$this$preds` as AssignLocalPath).getRhsValePath());
               } else if (`$this$preds` is EntryPath) {
                  var10000 = CollectionsKt.emptyList();
               } else if (`$this$preds` is UnknownPath) {
                  var10000 = CollectionsKt.emptyList();
               } else if (`$this$preds` is LiteralPath) {
                  var10000 = CollectionsKt.emptyList();
               } else if (`$this$preds` is GetEdgePath) {
                  var10000 = CollectionsKt.listOf((`$this$preds` as GetEdgePath).getValuePath());
               } else if (`$this$preds` is SetEdgePath) {
                  var10000 = CollectionsKt.listOf((`$this$preds` as SetEdgePath).getValuePath());
               } else {
                  if (`$this$preds` !is InvokeEdgePath) {
                     throw new NoWhenBranchMatchedException();
                  }

                  var10000 = (`$this$preds` as InvokeEdgePath).getInterproceduralPathMap().keySet();
               }

               return var10000;
            }
         };
      }

      public fun generateEvents(info: SootInfoCache?, icfg: InterproceduralCFG, events: List<IPath>): Sequence<List<BugPathEvent>> {
         return SequencesKt.map(new PathGeneratorImpl.Companion.EventGenerator(info, icfg).gen(0, events), PathGeneratorImpl.Companion::generateEvents$lambda$0);
      }

      @JvmStatic
      fun `generateEvents$lambda$0`(it: java.util.List): java.util.List {
         return PathGeneratorKt.getRemoveAdjacentDuplicates(it);
      }

      @SourceDebugExtension(["SMAP\nPathGenerator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorImpl$Companion$EventGenerator\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,213:1\n381#2,3:214\n384#2,4:219\n1317#3,2:217\n*S KotlinDebug\n*F\n+ 1 PathGenerator.kt\ncn/sast/dataflow/interprocedural/check/PathGeneratorImpl$Companion$EventGenerator\n*L\n172#1:214,3\n172#1:219,4\n174#1:217,2\n*E\n"])
      public class EventGenerator(info: SootInfoCache?, icfg: InterproceduralCFG) {
         public final val info: SootInfoCache?
         public final val icfg: InterproceduralCFG

         public final var invokeCount: Int
            internal set

         public final val cache: MutableMap<
            cn.sast.dataflow.interprocedural.check.PathGeneratorImpl.Companion.EventGenerator.CacheKey,
            Set<List<BugPathEvent>>
         >

         init {
            this.info = info;
            this.icfg = icfg;
            this.cache = new LinkedHashMap<>();
         }

         public fun gen(deep: Int, events: List<IPath>): Sequence<List<BugPathEvent>> {
            return SequencesKt.sequence(
               (
                  new Function2<SequenceScope<? super java.util.List<? extends BugPathEvent>>, Continuation<? super Unit>, Object>(events, this, deep, null) {
                     Object L$1;
                     Object L$2;
                     int label;

                     {
                        super(2, `$completionx`);
                        this.$events = `$events`;
                        this.this$0 = `$receiver`;
                        this.$deep = `$deep`;
                     }

                     // $VF: Irreducible bytecode was duplicated to produce valid code
                     public final Object invokeSuspend(Object $result) {
                        val var18: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        var `$this$sequence`: SequenceScope;
                        var normalizedEvents: java.util.List;
                        var worklist: ArrayDeque;
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              `$this$sequence` = this.L$0 as SequenceScope;
                              val var19: java.lang.Iterable = this.$events;
                              val idx: java.util.Collection = new ArrayList();

                              for (Object element$iv$iv : $this$filter$iv) {
                                 if ((curDiagnostic as IPath) !is EntryPath) {
                                    idx.add(curDiagnostic);
                                 }
                              }

                              normalizedEvents = PathGeneratorKt.getRemoveAdjacentDuplicates(idx as java.util.List);
                              if (normalizedEvents.isEmpty()) {
                                 return Unit.INSTANCE;
                              }

                              worklist = new ArrayDeque();
                              worklist.add(TuplesKt.to(ExtensionsKt.persistentListOf(), Boxing.boxInt(0)));
                              break;
                           case 1:
                              worklist = this.L$2 as ArrayDeque;
                              normalizedEvents = this.L$1 as java.util.List;
                              `$this$sequence` = this.L$0 as SequenceScope;
                              ResultKt.throwOnFailure(`$result`);
                              break;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        while (true) {
                           val var10000: Pair = worklist.removeLastOrNull() as Pair;
                           if (var10000 == null) {
                              return Unit.INSTANCE;
                           }

                           val pathEvents: PersistentList = var10000.component1() as PersistentList;
                           val var21: Int = (var10000.component2() as java.lang.Number).intValue();
                           if (var21 >= normalizedEvents.size()) {
                              val var25: java.util.List = PathGeneratorKt.getRemoveAdjacentDuplicates(
                                 new PathDiagnosticsGenerator(this.this$0.getInfo(), this.this$0.getIcfg(), this.$deep).inlineEvents(pathEvents)
                              );
                              if (!var25.isEmpty()) {
                                 val var10002: Continuation = this as Continuation;
                                 this.L$0 = `$this$sequence`;
                                 this.L$1 = normalizedEvents;
                                 this.L$2 = worklist;
                                 this.label = 1;
                                 if (`$this$sequence`.yield(var25, var10002) === var18) {
                                    return var18;
                                 }
                              }
                           } else {
                              val var22: Int = var21 + 1;
                              val var24: IPath = normalizedEvents.get(var21) as IPath;
                              val var26: PersistentList = pathEvents.add(var24);
                              if (var24 is InvokeEdgePath) {
                                 val var27: ExitInvoke = new ExitInvoke(var24 as InvokeEdgePath);
                                 val var29: java.util.Set = PathGeneratorImpl.Companion.EventGenerator.access$diagnosticsCached(
                                    this.this$0, var24 as InvokeEdgePath, this.$deep + 1
                                 );
                                 if (var29.isEmpty()) {
                                    Boxing.boxBoolean(worklist.add(TuplesKt.to(pathEvents, Boxing.boxInt(var22))));
                                 } else {
                                    for (java.util.List calleeEvents : callees) {
                                       if (calleeEvents.isEmpty()) {
                                          throw new IllegalStateException("Check failed.".toString());
                                       }

                                       worklist.add(
                                          TuplesKt.to(
                                             if (var22 == normalizedEvents.size())
                                                ExtensionsKt.plus(var26, calleeEvents)
                                                else
                                                ExtensionsKt.plus(var26, calleeEvents).add(var27),
                                             Boxing.boxInt(var22)
                                          )
                                       );
                                    }
                                 }
                              } else {
                                 Boxing.boxBoolean(worklist.add(TuplesKt.to(var26, Boxing.boxInt(var22))));
                              }
                           }
                        }
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        val var3: Function2 = new <anonymous constructor>(this.$events, this.this$0, this.$deep, `$completion`);
                        var3.L$0 = value;
                        return var3 as Continuation<Unit>;
                     }

                     public final Object invoke(SequenceScope<? super java.util.List<BugPathEvent>> p1, Continuation<? super Unit> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }
                  }
               ) as Function2
            );
         }

         private fun InvokeEdgePath.diagnosticsCached(deep: Int): Set<List<BugPathEvent>> {
            val k: PathGeneratorImpl.Companion.EventGenerator.CacheKey = new PathGeneratorImpl.Companion.EventGenerator.CacheKey(
               `$this$diagnosticsCached`, deep
            );
            val `$this$getOrPut$iv`: java.util.Map = this.cache;
            val `value$iv`: Any = this.cache.get(k);
            val var10000: Any;
            if (`value$iv` == null) {
               val set: java.util.Set = new LinkedHashSet();

               val `$this$forEach$iv`: Sequence;
               for (Object element$iv : $this$forEach$iv) {
                  val it: java.util.List = `element$iv` as java.util.List;
                  if (!(`element$iv` as java.util.List).isEmpty()) {
                     set.add(it);
                  }
               }

               `$this$getOrPut$iv`.put(k, set);
               var10000 = set;
            } else {
               var10000 = `value$iv`;
            }

            return var10000 as MutableSet<MutableList<BugPathEvent>>;
         }

         private fun InvokeEdgePath.diagnostics(deep: Int): Sequence<List<BugPathEvent>> {
            return SequencesKt.sequence(
               (
                  new Function2<SequenceScope<? super java.util.List<? extends BugPathEvent>>, Continuation<? super Unit>, Object>(
                     this, `$this$diagnostics`, deep, null
                  ) {
                     Object L$1;
                     int label;

                     {
                        super(2, `$completionx`);
                        this.this$0 = `$receiver`;
                        this.$this_diagnostics = `$receiver`;
                        this.$deep = `$deep`;
                     }

                     // $VF: Irreducible bytecode was duplicated to produce valid code
                     public final Object invokeSuspend(Object $result) {
                        val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        var `$this$sequence`: SequenceScope;
                        var var7: java.util.Iterator;
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              `$this$sequence` = this.L$0 as SequenceScope;
                              if (this.this$0.getInvokeCount() > 500) {
                                 PathGeneratorImpl.Companion.EventGenerator.Companion.getLogger().error(<unrepresentable>::invokeSuspend$lambda$0);
                                 return Unit.INSTANCE;
                              }

                              val generator: PathGenerator = PathGeneratorImpl.Companion.getPathGenerator();
                              val directGraph: HashMutableDirectedGraph = new HashMutableDirectedGraph();
                              var7 = CollectionsKt.toSet(
                                    generator.flush(directGraph as DirectedGraph<IPath>, generator.getHeads(this.$this_diagnostics.getPath(), directGraph))
                                       .values()
                                 )
                                 .iterator();
                              break;
                           case 1:
                              var7 = this.L$1 as java.util.Iterator;
                              `$this$sequence` = this.L$0 as SequenceScope;
                              ResultKt.throwOnFailure(`$result`);
                              break;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        while (var7.hasNext()) {
                           val events: java.util.List = var7.next() as java.util.List;
                           if (CollectionsKt.first(events) !is LiteralPath) {
                              val var10001: Sequence = this.this$0.gen(this.$deep, events);
                              val var10002: Continuation = this as Continuation;
                              this.L$0 = `$this$sequence`;
                              this.L$1 = var7;
                              this.label = 1;
                              if (`$this$sequence`.yieldAll(var10001, var10002) === var9) {
                                 return var9;
                              }
                           }
                        }

                        return Unit.INSTANCE;
                     }

                     public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                        val var3: Function2 = new <anonymous constructor>(this.this$0, this.$this_diagnostics, this.$deep, `$completion`);
                        var3.L$0 = value;
                        return var3 as Continuation<Unit>;
                     }

                     public final Object invoke(SequenceScope<? super java.util.List<BugPathEvent>> p1, Continuation<? super Unit> p2) {
                        return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                     }

                     private static final Object invokeSuspend$lambda$0() {
                        return "too much call edge of report path. limited!";
                     }
                  }
               ) as Function2
            );
         }

         @JvmStatic
         fun `diagnosticsCached$lambda$2$lambda$0`(`$set`: java.util.Set, it: java.util.List): Boolean {
            return `$set`.size() < 20;
         }

         @JvmStatic
         fun `logger$lambda$3`(): Unit {
            return Unit.INSTANCE;
         }

         public data class CacheKey(callee: InvokeEdgePath, deep: Int) {
            public final val callee: InvokeEdgePath
            public final val deep: Int

            init {
               this.callee = callee;
               this.deep = deep;
            }

            public operator fun component1(): InvokeEdgePath {
               return this.callee;
            }

            public operator fun component2(): Int {
               return this.deep;
            }

            public fun copy(callee: InvokeEdgePath = this.callee, deep: Int = this.deep): cn.sast.dataflow.interprocedural.check.PathGeneratorImpl.Companion.EventGenerator.CacheKey {
               return new PathGeneratorImpl.Companion.EventGenerator.CacheKey(callee, deep);
            }

            public override fun toString(): String {
               return "CacheKey(callee=${this.callee}, deep=${this.deep})";
            }

            public override fun hashCode(): Int {
               return this.callee.hashCode() * 31 + Integer.hashCode(this.deep);
            }

            public override operator fun equals(other: Any?): Boolean {
               if (this === other) {
                  return true;
               } else if (other !is PathGeneratorImpl.Companion.EventGenerator.CacheKey) {
                  return false;
               } else {
                  val var2: PathGeneratorImpl.Companion.EventGenerator.CacheKey = other as PathGeneratorImpl.Companion.EventGenerator.CacheKey;
                  if (!(this.callee == (other as PathGeneratorImpl.Companion.EventGenerator.CacheKey).callee)) {
                     return false;
                  } else {
                     return this.deep == var2.deep;
                  }
               }
            }
         }

         public companion object {
            public final val logger: KLogger
         }
      }
   }
}
