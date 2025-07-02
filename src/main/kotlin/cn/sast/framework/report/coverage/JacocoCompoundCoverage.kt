package cn.sast.framework.report.coverage

import cn.sast.api.report.CoverData
import cn.sast.api.report.CoverInst
import cn.sast.api.report.CoverTaint
import cn.sast.api.report.ICoverageCollector
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import kotlinx.coroutines.Deferred
import org.jacoco.core.analysis.ICounter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

public class JacocoCompoundCoverage(locator: IProjectFileLocator,
      taintCoverage: Coverage = (new TaintCoverage()) as Coverage,
      executionCoverage: Coverage = new Coverage(),
      enableCoveredTaint: Boolean = false
   ) :
   ICoverageCollector {
   private final val locator: IProjectFileLocator
   private final val taintCoverage: Coverage
   private final val executionCoverage: Coverage
   public open val enableCoveredTaint: Boolean

   init {
      this.locator = locator;
      this.taintCoverage = taintCoverage;
      this.executionCoverage = executionCoverage;
      this.enableCoveredTaint = enableCoveredTaint;
   }

   public override fun cover(coverInfo: CoverData) {
      if (coverInfo is CoverTaint) {
         this.taintCoverage.coverByQueue((coverInfo as CoverTaint).getClassName(), (coverInfo as CoverTaint).getLineNumber());
      } else {
         if (coverInfo !is CoverInst) {
            throw new NoWhenBranchMatchedException();
         }

         this.executionCoverage.coverByQueue((coverInfo as CoverInst).getClassName(), (coverInfo as CoverInst).getLineNumber());
      }
   }

   public override suspend fun flush(output: IResDirectory, sourceEncoding: Charset) {
      val var10000: Any = CoroutineScopeKt.coroutineScope(
         (
            new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, output, sourceEncoding, null) {
               int label;

               {
                  super(2, `$completionx`);
                  this.this$0 = `$receiver`;
                  this.$output = `$output`;
                  this.$sourceEncoding = `$sourceEncoding`;
               }

               public final Object invokeSuspend(Object $result) {
                  val var5: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  var execTask: Deferred;
                  switch (this.label) {
                     case 0:
                        ResultKt.throwOnFailure(`$result`);
                        val `$this$coroutineScope`: CoroutineScope = this.L$0 as CoroutineScope;
                        val taintTask: Deferred = BuildersKt.async$default(
                           this.L$0 as CoroutineScope,
                           null,
                           null,
                           (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.this$0, this.$output, this.$sourceEncoding, null) {
                              int label;

                              {
                                 super(2, `$completionx`);
                                 this.this$0 = `$receiver`;
                                 this.$output = `$output`;
                                 this.$sourceEncoding = `$sourceEncoding`;
                              }

                              public final Object invokeSuspend(Object $result) {
                                 val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                 switch (this.label) {
                                    case 0:
                                       ResultKt.throwOnFailure(`$result`);
                                       val var10000: JacocoCompoundCoverage = this.this$0;
                                       val var10001: Coverage = JacocoCompoundCoverage.access$getTaintCoverage$p(this.this$0);
                                       val var10002: IResDirectory = this.$output.resolve("taint-coverage").toDirectory();
                                       val var10003: Charset = this.$sourceEncoding;
                                       val var10004: Continuation = this as Continuation;
                                       this.label = 1;
                                       if (var10000.flush(var10001, var10002, var10003, var10004) === var2) {
                                          return var2;
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
                                 return (new <anonymous constructor>(this.this$0, this.$output, this.$sourceEncoding, `$completion`)) as Continuation<Unit>;
                              }

                              public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                 return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                              }
                           }) as Function2,
                           3,
                           null
                        );
                        execTask = BuildersKt.async$default(
                           `$this$coroutineScope`,
                           null,
                           null,
                           (new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.this$0, this.$output, this.$sourceEncoding, null) {
                              int label;

                              {
                                 super(2, `$completionx`);
                                 this.this$0 = `$receiver`;
                                 this.$output = `$output`;
                                 this.$sourceEncoding = `$sourceEncoding`;
                              }

                              public final Object invokeSuspend(Object $result) {
                                 val var2: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                 switch (this.label) {
                                    case 0:
                                       ResultKt.throwOnFailure(`$result`);
                                       val var10000: JacocoCompoundCoverage = this.this$0;
                                       val var10001: Coverage = JacocoCompoundCoverage.access$getExecutionCoverage$p(this.this$0);
                                       val var10002: IResDirectory = this.$output.resolve("code-coverage").toDirectory();
                                       val var10003: Charset = this.$sourceEncoding;
                                       val var10004: Continuation = this as Continuation;
                                       this.label = 1;
                                       if (var10000.flush(var10001, var10002, var10003, var10004) === var2) {
                                          return var2;
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
                                 return (new <anonymous constructor>(this.this$0, this.$output, this.$sourceEncoding, `$completion`)) as Continuation<Unit>;
                              }

                              public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
                                 return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
                              }
                           }) as Function2,
                           3,
                           null
                        );
                        val var10001: Continuation = this as Continuation;
                        this.L$0 = execTask;
                        this.label = 1;
                        if (taintTask.await(var10001) === var5) {
                           return var5;
                        }
                        break;
                     case 1:
                        execTask = this.L$0 as Deferred;
                        ResultKt.throwOnFailure(`$result`);
                        break;
                     case 2:
                        ResultKt.throwOnFailure(`$result`);
                        return Unit.INSTANCE;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  val var6: Continuation = this as Continuation;
                  this.L$0 = null;
                  this.label = 2;
                  return if (execTask.await(var6) === var5) var5 else Unit.INSTANCE;
               }

               public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
                  val var3: Function2 = new <anonymous constructor>(this.this$0, this.$output, this.$sourceEncoding, `$completion`);
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

   public override suspend fun getCoveredLineCounter(allSourceFiles: Set<IResFile>, encoding: Charset): ICounter {
      var `$continuation`: Continuation;
      label20: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.getCoveredLineCounter(null, null, this as Continuation<? super ICounter>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var6: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            var10000 = this.executionCoverage;
            val var10001: IProjectFileLocator = this.locator;
            `$continuation`.L$0 = allSourceFiles;
            `$continuation`.label = 1;
            var10000 = (Coverage)var10000.calculateSourceCoverage(var10001, encoding, `$continuation`);
            if (var10000 === var6) {
               return var6;
            }
            break;
         case 1:
            allSourceFiles = `$continuation`.L$0 as java.util.Set;
            ResultKt.throwOnFailure(`$result`);
            var10000 = (Coverage)`$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return (var10000 as SourceCoverage).calculateCoveredRatio(allSourceFiles);
   }

   public suspend fun Coverage.flush(out: IResDirectory, sourceEncoding: Charset) {
      val var10000: Any = `$this$flush`.flushCoverage(this.locator, out, sourceEncoding, `$completion`);
      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   public fun copy(
      locator: IProjectFileLocator = this.locator,
      taintCoverage: Coverage = this.taintCoverage,
      executionCoverage: Coverage = this.executionCoverage,
      enableCoveredTaint: Boolean = this.getEnableCoveredTaint()
   ): JacocoCompoundCoverage {
      return new JacocoCompoundCoverage(locator, taintCoverage, executionCoverage, enableCoveredTaint);
   }
}
