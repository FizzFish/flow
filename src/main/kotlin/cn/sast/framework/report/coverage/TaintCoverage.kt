package cn.sast.framework.report.coverage

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.framework.report.IProjectFileLocator
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nCoverageCompnment.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoverageCompnment.kt\ncn/sast/framework/report/coverage/TaintCoverage\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,72:1\n1#2:73\n*E\n"])
public class TaintCoverage : Coverage {
   private fun copyResource(name: String, to: IResFile) {
      label19: {
         val var10000: Path = to.getPath();
         val var10001: Array<OpenOption> = new OpenOption[0];
         val var13: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
         val var3: Closeable = var13;
         var var4: java.lang.Throwable = null;

         try {
            try {
               val it: OutputStream = var3 as OutputStream;
               val var14: InputStream = TaintCoverage.class.getResourceAsStream(name);
               val var12: Long = ByteStreamsKt.copyTo$default(var14, it, 0, 2, null);
            } catch (var8: java.lang.Throwable) {
               var4 = var8;
               throw var8;
            }
         } catch (var9: java.lang.Throwable) {
            CloseableKt.closeFinally(var3, var4);
         }

         CloseableKt.closeFinally(var3, null);
      }
   }

   public fun changeColor(reportOutputRoot: IResDirectory) {
      this.copyResource("/jacoco/taint-report.css", reportOutputRoot.resolve("jacoco-resources").resolve("report.css").toFile());
      this.copyResource("/jacoco/greenbar.gif", reportOutputRoot.resolve("jacoco-resources").resolve("redbar.gif").toFile());
      this.copyResource("/jacoco/redbar.gif", reportOutputRoot.resolve("jacoco-resources").resolve("greenbar.gif").toFile());
   }

   public override suspend fun flushCoverage(locator: IProjectFileLocator, outputDir: IResDirectory, encoding: Charset) {
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
            Object L$1;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.flushCoverage(null, null, null, this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            `$continuation`.L$0 = this;
            `$continuation`.L$1 = outputDir;
            `$continuation`.label = 1;
            if (super.flushCoverage(locator, outputDir, encoding, `$continuation`) === var9) {
               return var9;
            }
            break;
         case 1:
            outputDir = `$continuation`.L$1 as IResDirectory;
            this = `$continuation`.L$0 as TaintCoverage;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      this.changeColor(outputDir);
      return Unit.INSTANCE;
   }
}
