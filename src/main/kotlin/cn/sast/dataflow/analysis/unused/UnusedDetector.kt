package cn.sast.dataflow.analysis.unused

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.MainConfig
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.dataflow.analysis.IBugReporter
import cn.sast.dataflow.infoflow.provider.MethodSummaryProviderKt
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.utils.UtilsKt
import com.feysh.corax.config.builtin.checkers.DefineUnusedChecker
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.SemaphoreKt
import mu.KLogger
import soot.MethodOrMethodContext
import soot.Scene
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.Value
import soot.ValueBox
import soot.jimple.FieldRef
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.tagkit.AbstractHost
import soot.tagkit.VisibilityAnnotationTag
import soot.util.Chain

@SourceDebugExtension(["SMAP\nUnusedDetector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UnusedDetector.kt\ncn/sast/dataflow/analysis/unused/UnusedDetector\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,225:1\n1557#2:226\n1628#2,3:227\n865#2,2:230\n1863#2,2:232\n1755#2,3:234\n1755#2,3:237\n1053#2:241\n1053#2:242\n1619#2:243\n1863#2:244\n1864#2:246\n1620#2:247\n1469#2,5:248\n1#3:240\n1#3:245\n*S KotlinDebug\n*F\n+ 1 UnusedDetector.kt\ncn/sast/dataflow/analysis/unused/UnusedDetector\n*L\n92#1:226\n92#1:227,3\n47#1:230,2\n53#1:232,2\n97#1:234,3\n102#1:237,3\n183#1:241\n199#1:242\n65#1:243\n65#1:244\n65#1:246\n65#1:247\n66#1:248,5\n65#1:245\n*E\n"])
public class UnusedDetector(mainConfig: MainConfig, builtinAnalysisConfig: BuiltinAnalysisConfig, cg: CallGraph, reporter: IBugReporter) {
   public final val mainConfig: MainConfig
   private final val builtinAnalysisConfig: BuiltinAnalysisConfig
   private final val cg: CallGraph
   private final val reporter: IBugReporter

   private final val calleeAndSuperMethods: MutableSet<SootMethod>
      private final get() {
         return this.calleeAndSuperMethods$delegate.getValue() as MutableSet<SootMethod>;
      }


   private final val allBlackMethods: MutableSet<SootMethod>
      private final get() {
         return this.allBlackMethods$delegate.getValue() as MutableSet<SootMethod>;
      }


   private final val blackMethodPatterns: List<Regex>

   private final val isSkipped: Boolean
      private final get() {
         val sig: java.lang.String = `$this$isSkipped`.getSignature();
         if (!this.getAllBlackMethods().contains(`$this$isSkipped`)) {
            val `$this$any$iv`: java.lang.Iterable = this.blackMethodPatterns;
            var var10000: Boolean;
            if (this.blackMethodPatterns is java.util.Collection && this.blackMethodPatterns.isEmpty()) {
               var10000 = false;
            } else {
               val var5: java.util.Iterator = `$this$any$iv`.iterator();

               while (true) {
                  if (!var5.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  val it: Regex = var5.next() as Regex;
                  if (it.matches(sig)) {
                     var10000 = true;
                     break;
                  }
               }
            }

            if (!var10000) {
               return false;
            }
         }

         return true;
      }


   private final val isSkipped: Boolean
      private final get() {
         val sig: java.lang.String = `$this$isSkipped`.getSignature();
         val `$this$any$iv`: java.lang.Iterable = this.blackMethodPatterns;
         var var10000: Boolean;
         if (this.blackMethodPatterns is java.util.Collection && this.blackMethodPatterns.isEmpty()) {
            var10000 = false;
         } else {
            val var5: java.util.Iterator = `$this$any$iv`.iterator();

            while (true) {
               if (!var5.hasNext()) {
                  var10000 = false;
                  break;
               }

               val it: Regex = var5.next() as Regex;
               if (it.matches(sig)) {
                  var10000 = true;
                  break;
               }
            }
         }

         return var10000;
      }


   private final val enableUnusedMethod: Boolean
   private final val enableUrfUnreadField: Boolean

   init {
      this.mainConfig = mainConfig;
      this.builtinAnalysisConfig = builtinAnalysisConfig;
      this.cg = cg;
      this.reporter = reporter;
      this.calleeAndSuperMethods$delegate = LazyKt.lazy(UnusedDetector::calleeAndSuperMethods_delegate$lambda$6);
      this.allBlackMethods$delegate = LazyKt.lazy(UnusedDetector::allBlackMethods_delegate$lambda$8);
      val `$this$map$iv`: java.lang.Iterable = this.builtinAnalysisConfig.getUnusedDetectorSootSigPatternBlackList();
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(new Regex(`item$iv$iv` as java.lang.String));
      }

      this.blackMethodPatterns = `destination$iv$iv` as MutableList<Regex>;
      this.enableUnusedMethod = this.mainConfig.isEnable(DefineUnusedChecker.UnusedMethod.INSTANCE);
      this.enableUrfUnreadField = this.mainConfig.isEnable(DefineUnusedChecker.UrfUnreadField.INSTANCE);
   }

   private fun isAnnotated(abstractHost: AbstractHost): Boolean {
      return abstractHost.getTag("VisibilityAnnotationTag") as VisibilityAnnotationTag != null;
   }

   private fun findUnusedField(appClass: SootClass): Set<SootField> {
      val var10000: Chain = appClass.getFields();
      val `$this$filterTo$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
      val sootMethod: java.util.Collection = new LinkedHashSet();

      for (Object element$iv : $this$filterTo$iv) {
         label52: {
            val `$i$f$forEach`: SootField = `$this$forEach$iv` as SootField;
            if ((`$this$forEach$iv` as SootField).isPrivate() && !(`$this$forEach$iv` as SootField).isFinal()) {
               if (!this.isAnnotated(`$i$f$forEach` as AbstractHost)) {
                  var21 = true;
                  break label52;
               }
            }

            var21 = false;
         }

         if (var21) {
            sootMethod.add(`$this$forEach$iv`);
         }
      }

      val unused: java.util.Set = sootMethod as java.util.Set;
      val var22: java.util.List = appClass.getMethods();

      for (SootMethod sootMethodx : CollectionsKt.toSet(var22)) {
         if (!sootMethodx.hasActiveBody()) {
            return SetsKt.emptySet();
         }

         val var23: java.util.Iterator = sootMethodx.getActiveBody().getUnits().iterator();
         val var16: java.util.Iterator = var23;

         while (var16.hasNext()) {
            val var18: java.lang.Iterable;
            for (Object element$iv : var18) {
               val v: Value = (`element$iv` as ValueBox).getValue();
               if (v is FieldRef) {
                  unused.remove((v as FieldRef).getField());
               }
            }
         }
      }

      return unused;
   }

   private fun isUnused(sootMethod: SootMethod): Boolean {
      if (!sootMethod.isStaticInitializer() && !this.isAnnotated(sootMethod as AbstractHost)) {
         if (sootMethod.isConstructor()) {
            if (sootMethod.getSubSignature() == "void <init>()") {
               return false;
            }

            if (sootMethod.getParameterCount() == 1) {
               val var10000: java.lang.String = sootMethod.getDeclaringClass().getName();
               val var10001: Type = sootMethod.getParameterType(0);
               val var13: java.lang.String = UtilsKt.getTypename(var10001);
               val var10002: java.lang.String = sootMethod.getDeclaringClass().getShortJavaStyleName();
               if (var10000 == "$var13\$${StringsKt.substringAfterLast$default(var10002, "$", null, 2, null)}") {
                  return false;
               }
            }
         }

         var var8: java.lang.String = sootMethod.getName();
         if (!StringsKt.contains$default(var8, "$", false, 2, null)) {
            var8 = sootMethod.getName();
            if (!StringsKt.contains(var8, "lambda", true)) {
               var8 = sootMethod.getSubSignature();
               if (StringsKt.endsWith$default(var8, "valueOf(java.lang.String)", false, 2, null)) {
                  return false;
               }

               var8 = sootMethod.getSubSignature();
               if (StringsKt.endsWith$default(var8, "[] values()", false, 2, null)) {
                  return false;
               }

               if (!sootMethod.isStatic()) {
                  val var3: java.lang.String = sootMethod.getName();
                  if ((
                        if (StringsKt.startsWith$default(var3, "get", false, 2, null)
                              || StringsKt.startsWith$default(var3, "set", false, 2, null)
                              || StringsKt.startsWith$default(var3, "is", false, 2, null))
                           var3
                           else
                           null
                     )
                     != null) {
                     return false;
                  }
               }

               if (sootMethod.isMain()) {
                  return false;
               }

               if (this.cg.edgesInto(sootMethod as MethodOrMethodContext).hasNext()) {
                  return false;
               }

               val var12: SootClass = sootMethod.getDeclaringClass();
               if (SootUtilsKt.findMethodOrNull(var12, sootMethod.getSubSignature().toString(), UnusedDetector::isUnused$lambda$14) != null) {
                  return false;
               }

               if (this.getCalleeAndSuperMethods().contains(sootMethod)) {
                  return false;
               }

               if (!sootMethod.hasActiveBody()) {
                  return false;
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public fun analyze(clazz: SootClass) {
      if (this.enableUnusedMethod) {
         val count: Int = 1;
         val maximumUnusedFieldReportsEachClass: Int = this.builtinAnalysisConfig.getMaximumUnusedMethodReportsEachClass();
         val var10000: java.util.List = clazz.getMethods();

         for (SootMethod method : CollectionsKt.sortedWith(CollectionsKt.toSet(var10000), new UnusedDetector$analyze$$inlined$sortedBy$1())) {
            if (this.isUnused(var10) && !this.isSkipped(var10)) {
               if (count++ > maximumUnusedFieldReportsEachClass) {
                  break;
               }

               IBugReporter.DefaultImpls.report$default(this.reporter, DefineUnusedChecker.UnusedMethod.INSTANCE, var10, null, 4, null);
            }
         }
      }

      if (this.enableUrfUnreadField) {
         val var7: Int = 1;
         val var8: Int = this.builtinAnalysisConfig.getMaximumUnusedFieldReportsEachClass();

         for (SootField field : CollectionsKt.sortedWith(this.findUnusedField(clazz), new UnusedDetector$analyze$$inlined$sortedBy$2())) {
            if (!this.isSkipped(var14)) {
               if (var7++ > var8) {
                  break;
               }

               IBugReporter.DefaultImpls.report$default(this.reporter, DefineUnusedChecker.UrfUnreadField.INSTANCE, var14, null, 4, null);
            }
         }
      }
   }

   public suspend fun analyze(classes: Collection<SootClass>) {
      val semaphore: Semaphore = SemaphoreKt.Semaphore$default(this.mainConfig.getParallelsNum() * 2, 0, 2, null);
      val var10000: Any = CoroutineScopeKt.coroutineScope(
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(classes, semaphore, this, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.$classes = `$classes`;
                  this.$semaphore = `$semaphore`;
                  this.this$0 = `$receiver`;
               }

               public final Object invokeSuspend(Object $result) {
                  IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val `$this$coroutineScope`: CoroutineScope = this.L$0 as CoroutineScope;

                        for (SootClass clazz : this.$classes) {
                           BuildersKt.launch$default(
                              `$this$coroutineScope`,
                              null,
                              null,
                              (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.$semaphore, this.this$0, clazz, null) {
                                 Object L$0;
                                 Object L$1;
                                 Object L$2;
                                 int label;

                                 {
                                    super(2, `$completionx`);
                                    this.$semaphore = `$semaphore`;
                                    this.this$0 = `$receiver`;
                                    this.$clazz = `$clazz`;
                                 }

                                 public final Object invokeSuspend(Object $result) {
                                    label23: {
                                       val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                       var `$this$withPermit$iv`: Semaphore;
                                       var var3: UnusedDetector;
                                       var var4: SootClass;
                                       switch (this.label) {
                                          case 0:
                                             ResultKt.throwOnFailure(`$result`);
                                             `$this$withPermit$iv` = this.$semaphore;
                                             var3 = this.this$0;
                                             var4 = this.$clazz;
                                             val var10001: Continuation = this as Continuation;
                                             this.L$0 = this.$semaphore;
                                             this.L$1 = var3;
                                             this.L$2 = var4;
                                             this.label = 1;
                                             if (`$this$withPermit$iv`.acquire(var10001) === var9) {
                                                return var9;
                                             }
                                             break;
                                          case 1:
                                             var4 = this.L$2 as SootClass;
                                             var3 = this.L$1 as UnusedDetector;
                                             `$this$withPermit$iv` = this.L$0 as Semaphore;
                                             ResultKt.throwOnFailure(`$result`);
                                             break;
                                          default:
                                             throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                       }

                                       try {
                                          var3.analyze(var4);
                                       } catch (var10: java.lang.Throwable) {
                                          `$this$withPermit$iv`.release();
                                       }

                                       `$this$withPermit$iv`.release();
                                    }
                                 }

                                 public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                                    return (new <anonymous constructor>(this.$semaphore, this.this$0, this.$clazz, `$completion`)) as Continuation<Unit>;
                                 }

                                 public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                    return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                                 }
                              }) as Function2,
                              3,
                              null
                           );
                        }

                        return Unit.INSTANCE;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  val var3: Function2 = new <anonymous constructor>(this.$classes, this.$semaphore, this.this$0, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2,
         `$completion`
      );
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   @JvmStatic
   fun `calleeAndSuperMethods_delegate$lambda$6$lambda$4$lambda$3`(it: SootMethod): Boolean {
      return !it.isConstructor() && !it.isPrivate();
   }

   @JvmStatic
   fun `calleeAndSuperMethods_delegate$lambda$6`(`this$0`: UnusedDetector): java.util.Set {
      var `$this$flatMapTo$iv`: java.lang.Iterable = CollectionsKt.toCollection(`this$0`.cg as java.lang.Iterable, new LinkedHashSet());
      var it: java.util.Collection = new LinkedHashSet();

      for (Object element$iv$iv : $this$flatMapTo$iv) {
         val var10000: SootMethod = (var8 as Edge).tgt();
         if (var10000 != null) {
            it.add(var10000);
         }
      }

      val res: java.util.Set = it as java.util.Set;
      `$this$flatMapTo$iv` = it as java.util.Set;
      it = new LinkedHashSet();

      for (Object element$iv : $this$flatMapTo$iv) {
         val var21: SootMethod = var20 as SootMethod;
         val var24: SootClass = (var20 as SootMethod).getDeclaringClass();
         CollectionsKt.addAll(
            it,
            SequencesKt.filter(
               SootUtilsKt.findMethodOrNull(var24, var21.getSubSignature().toString()),
               UnusedDetector::calleeAndSuperMethods_delegate$lambda$6$lambda$4$lambda$3
            )
         );
      }

      res.addAll(it as java.util.Set);
      return res;
   }

   @JvmStatic
   fun `allBlackMethods_delegate$lambda$8$lambda$7`(`$blackMethodSig`: java.lang.String): java.lang.String {
      return "`$`$blackMethodSig`` is not a valid method signature";
   }

   @JvmStatic
   fun `allBlackMethods_delegate$lambda$8`(`this$0`: UnusedDetector): java.util.Set {
      val scene: Scene = Scene.v();
      val allBlackMethods: java.util.Set = new LinkedHashSet();

      for (java.lang.String blackMethodSig : this$0.builtinAnalysisConfig.getUnusedDetectorMethodSigBlackList()) {
         val var10000: IMethodMatch = OthersKt.methodSignatureToMatcher(blackMethodSig);
         if (var10000 == null) {
            throw new IllegalStateException((UnusedDetector::allBlackMethods_delegate$lambda$8$lambda$7).toString());
         }
         for (SootMethod mBlack : var10000.matched(scene)) {
            allBlackMethods.add(mBlack);
            allBlackMethods.addAll(MethodSummaryProviderKt.findAllOverrideMethodsOfMethod(mBlack));
         }
      }

      return allBlackMethods;
   }

   @JvmStatic
   fun `isUnused$lambda$14`(`$sootMethod`: SootMethod, it: SootMethod): Boolean {
      return !(it == `$sootMethod`) && !it.isConstructor() && !it.isPrivate();
   }

   @JvmStatic
   fun `logger$lambda$17`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      public final val logger: KLogger

      public fun isEnable(mainConfig: MainConfig): Boolean {
         return mainConfig.isAnyEnable(DefineUnusedChecker.UnusedMethod.INSTANCE, DefineUnusedChecker.UrfUnreadField.INSTANCE);
      }
   }
}
