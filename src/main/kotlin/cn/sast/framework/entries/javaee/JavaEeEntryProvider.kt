package cn.sast.framework.entries.javaee

import analysis.Config
import analysis.CreateEdge
import analysis.Implement
import cn.sast.common.IResFile
import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask
import cn.sast.framework.entries.java.UnReachableEntryProvider
import cn.sast.framework.entries.utils.PhantomValueForType
import java.time.LocalDateTime
import java.util.LinkedHashSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.FlowKt
import mock.MockObject
import mock.MockObjectImpl
import mu.KLogger
import org.utbot.common.LoggerWithLogMethod
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe
import soot.Body
import soot.Local
import soot.RefType
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.Value
import soot.jimple.JimpleBody
import utils.BaseBodyGenerator
import utils.BaseBodyGeneratorFactory
import utils.INewUnits

public class JavaEeEntryProvider(ctx: SootCtx, beanXmls: MutableSet<IResFile> = (new LinkedHashSet()) as java.util.Set) : IEntryPointProvider {
   private final val ctx: SootCtx
   public final val beanXmls: MutableSet<IResFile>
   public open val iterator: Flow<AnalyzeTask>

   init {
      this.ctx = ctx;
      this.beanXmls = beanXmls;
      this.iterator = FlowKt.flow(
         (
            new Function2<FlowCollector<? super IEntryPointProvider.AnalyzeTask>, Continuation<? super Unit>, Object>(this, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
               }

               // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               public final Object invokeSuspend(Object $result) {
                  label54: {
                     val var24: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(`$result`);
                           val `$this$flow`: FlowCollector = this.L$0 as FlowCollector;
                           val unReachableEntryProvider: LoggerWithLogMethod = LoggingKt.info(JavaEeEntryProvider.access$getLogger$cp());
                           val `msg$iv`: java.lang.String = "construct Java EE component";
                           val var7: JavaEeEntryProvider = this.this$0;
                           unReachableEntryProvider.getLogMethod()
                              .invoke(new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$1("construct Java EE component"));
                           val `startTime$iv`: LocalDateTime = LocalDateTime.now();
                           var `alreadyLogged$iv`: Boolean = false;
                           val `res$iv`: ObjectRef = new ObjectRef();
                           `res$iv`.element = Maybe.Companion.empty();

                           var var12: Any;
                           try {
                              try {
                                 val var17: <unknown>;
                                 val `destination$iv`: <unknown>;
                                 while (var17.hasNext()) {
                                    `destination$iv`.add(
                                       (var17.next() as IResFile)
                                          .getNormalize()
                                          .expandRes(JavaEeEntryProvider.access$getCtx$p(var7).getMainConfig().getOutput_dir())
                                          .toString()
                                    );
                                 }

                                 val var15: Any;
                                 val var22: <unknown>;
                                 var22.element = new Maybe(
                                    JavaEeEntryProvider.access$createDummyMain((JavaEeEntryProvider)var15, `destination$iv` as java.util.Set)
                                 );
                                 var12 = (`res$iv`.element as Maybe).getOrThrow();
                              } catch (var25: java.lang.Throwable) {
                                 unReachableEntryProvider.getLogMethod()
                                    .invoke(new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$4(`startTime$iv`, `msg$iv`, var25));
                                 `alreadyLogged$iv` = true;
                                 throw var25;
                              }
                           } catch (var26: java.lang.Throwable) {
                              if (!`alreadyLogged$iv`) {
                                 if ((`res$iv`.element as Maybe).getHasValue()) {
                                    unReachableEntryProvider.getLogMethod()
                                       .invoke(
                                          new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$5(
                                             `startTime$iv`, "construct Java EE component", `res$iv`
                                          )
                                       );
                                 } else {
                                    unReachableEntryProvider.getLogMethod()
                                       .invoke(
                                          new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$6(
                                             `startTime$iv`, "construct Java EE component"
                                          )
                                       );
                                 }
                              }
                           }

                           if ((`res$iv`.element as Maybe).getHasValue()) {
                              unReachableEntryProvider.getLogMethod()
                                 .invoke(
                                    new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$2(
                                       `startTime$iv`, "construct Java EE component", `res$iv`
                                    )
                                 );
                           } else {
                              unReachableEntryProvider.getLogMethod()
                                 .invoke(
                                    new JavaEeEntryProvider$iterator$1$invokeSuspend$$inlined$bracket$default$3(`startTime$iv`, "construct Java EE component")
                                 );
                           }

                           val dummyMain: SootMethod = var12 as SootMethod;
                           val var30: UnReachableEntryProvider = new UnReachableEntryProvider(JavaEeEntryProvider.access$getCtx$p(this.this$0), null, 2, null);
                           if (dummyMain != null) {
                              val var10000: java.util.Set = var30.getExclude();
                              val var10001: java.lang.String = dummyMain.getSignature();
                              var10000.add(var10001);
                           }

                           val var33: Flow = var30.getIterator();
                           val var10002: Continuation = this as Continuation;
                           this.label = 1;
                           if (FlowKt.emitAll(`$this$flow`, var33, var10002) === var24) {
                              return var24;
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
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  val var3: Function2 = new <anonymous constructor>(this.this$0, `$completion`);
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               public final Object invoke(FlowCollector<? super IEntryPointProvider.AnalyzeTask> p1, Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2
      );
   }

   private fun createDummyMain(beanXmlPaths: Set<String>): SootMethod? {
      label36: {
         val p: PhantomValueForType = new PhantomValueForType(null, 1, null);
         BaseBodyGeneratorFactory.instance = new BaseBodyGeneratorFactory(p) {
            {
               this.$p = `$p`;
            }

            public BaseBodyGenerator create(Body body) {
               return new JavaEeEntryProvider.SummaryTypeValueBaseBodyGenerator(this.$p, body);
            }
         };
         Implement.mockObject = (new MockObjectImpl(p) {
            {
               this.$p = `$p`;
            }

            public Local mockBean(JimpleBody body, BaseBodyGenerator units, SootClass sootClass, SootMethod toCall) {
               val var10000: PhantomValueForType = this.$p;
               val var10002: RefType = sootClass.getType();
               var var5: Local = var10000.getValueForType(units, var10002 as Type);
               if (var5 == null) {
                  var5 = super.mockBean(body, units, sootClass, toCall);
               }

               return var5;
            }
         }) as MockObject;
         val edge: CreateEdge = new CreateEdge();

         var config: SootMethod;
         label37: {
            try {
               try {
                  val var10: Config = new Config();
                  Config.linkMainAndController = false;
                  Config.linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods = false;
                  var10.bean_xml_paths = beanXmlPaths;
                  edge.initCallGraph(var10);
                  val e: SootMethod = edge.projectMainMethod;
                  logger.info(JavaEeEntryProvider::createDummyMain$lambda$0);
                  config = e;
                  break label37;
               } catch (var6: Exception) {
                  logger.error("create JavaEE dummy main failed!", var6);
                  config = null;
               }
            } catch (var7: java.lang.Throwable) {
               edge.clear();
               BaseBodyGeneratorFactory.instance = null;
            }

            edge.clear();
            BaseBodyGeneratorFactory.instance = null;
            return config;
         }

         edge.clear();
         BaseBodyGeneratorFactory.instance = null;
         return config;
      }
   }

   override fun startAnalyse() {
      IEntryPointProvider.DefaultImpls.startAnalyse(this);
   }

   override fun endAnalyse() {
      IEntryPointProvider.DefaultImpls.endAnalyse(this);
   }

   @JvmStatic
   fun `createDummyMain$lambda$0`(`$dummy`: SootMethod): Any {
      return "JavaEE dummy main is $`$dummy`";
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   public class SummaryTypeValueBaseBodyGenerator(p: PhantomValueForType, body: Body) : BaseBodyGenerator(body) {
      private final val p: PhantomValueForType

      init {
         this.p = p;
      }

      protected open fun getValueForType(
         newUnits: INewUnits,
         tp: Type,
         constructionStack: MutableSet<SootClass>,
         parentClasses: Set<SootClass>,
         generatedLocals: MutableSet<Local>?
      ): Value? {
         return this.p.getValueForType(newUnits, this, tp) as Value;
      }
   }
}
