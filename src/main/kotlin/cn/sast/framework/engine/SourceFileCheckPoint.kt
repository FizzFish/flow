package cn.sast.framework.engine

import cn.sast.api.config.MainConfig
import cn.sast.api.config.MainConfig.RelativePath
import cn.sast.api.report.DefaultEnv
import cn.sast.api.report.FileResInfo
import cn.sast.api.report.IBugResInfo
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import com.feysh.corax.config.api.ISourceFileCheckPoint
import com.feysh.corax.config.api.report.Region
import java.io.Closeable
import java.io.IOException
import java.net.URI
import java.nio.file.Path
import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.BuildersKt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nPreAnalysisImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/SourceFileCheckPoint\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,760:1\n1#2:761\n1557#3:762\n1628#3,3:763\n*S KotlinDebug\n*F\n+ 1 PreAnalysisImpl.kt\ncn/sast/framework/engine/SourceFileCheckPoint\n*L\n747#1:762\n747#1:763,3\n*E\n"])
public class SourceFileCheckPoint(sFile: IResFile, mainConfig: MainConfig) : CheckPoint, ISourceFileCheckPoint, Closeable {
   private final val sFile: IResFile
   public final val mainConfig: MainConfig
   public open val path: Path

   public open val relativePath: RelativePath
      public open get() {
         return this.mainConfig.tryGetRelativePath(this.sFile);
      }


   public open val uri: URI
      public open get() {
         return this.sFile.getUri();
      }


   public open val archiveFile: Path?
      public open get() {
         return this.archiveFile$delegate.getValue() as Path;
      }


   public open val file: IBugResInfo

   internal open val env: DefaultEnv
      internal open get() {
         return new DefaultEnv(Region.Companion.getERROR().getMutable(), null, null, null, null, null, null, null, null, 510, null);
      }


   private final var bytes: ByteArray?
   private final var text: String?
   private final var lines: List<IndexedValue<String>>?

   init {
      this.sFile = sFile;
      this.mainConfig = mainConfig;
      this.path = this.sFile.getPath();
      this.archiveFile$delegate = LazyKt.lazy(SourceFileCheckPoint::archiveFile_delegate$lambda$0);
      this.file = new FileResInfo(this.sFile);
   }

   public override suspend fun readAllBytes(): ByteArray {
      val var2: ByteArray = this.bytes;
      return if ((if (this.bytes != null) this.bytes else null) == null)
         BuildersKt.withContext(Dispatchers.getIO() as CoroutineContext, (new Function2<CoroutineScope, Continuation<byte[]>, Object>(this, null) {
            int label;

            {
               super(2, `$completionx`);
               this.this$0 = `$receiver`;
            }

            public final Object invokeSuspend(Object $result) {
               IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch (this.label) {
                  case 0:
                     ResultKt.throwOnFailure(`$result`);
                     val var2: ByteArray = ResourceKt.readAllBytes(SourceFileCheckPoint.access$getSFile$p(this.this$0));
                     SourceFileCheckPoint.access$setBytes$p(this.this$0, var2);
                     return var2;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }
            }

            public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
               return (new <anonymous constructor>(this.this$0, `$completion`)) as Continuation<Unit>;
            }

            public final Object invoke(CoroutineScope p1, Continuation<byte[]> p2) {
               return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
            }
         }) as Function2, `$completion`)
         else
         (if (this.bytes != null) this.bytes else null);
   }

   public override suspend fun text(): String? {
      var `$continuation`: Continuation;
      label59: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label59;
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
               return this.this$0.text(this as Continuation<? super java.lang.String>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val var13: java.lang.String = this.text;
            var10000 = if (this.text != null) this.text else null;
            if ((if (this.text != null) this.text else null) != null) {
               return var10000;
            }

            try {
               `$continuation`.L$0 = this;
               `$continuation`.label = 1;
               var10000 = this.readAllBytes(`$continuation`);
            } catch (var11: IOException) {
               logger.error("read config file ${this.getPath()} failed");
               return null;
            }

            if (var10000 === var9) {
               return var9;
            }
            break;
         case 1:
            this = `$continuation`.L$0 as SourceFileCheckPoint;

            try {
               ResultKt.throwOnFailure(`$result`);
               var10000 = `$result`;
               break;
            } catch (var12: IOException) {
               logger.error("read config file ${(`$continuation`.L$0 as SourceFileCheckPoint).getPath()} failed");
               return null;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      var var2: java.lang.String;
      try {
         var2 = new java.lang.String(var10000 as ByteArray, Charsets.UTF_8);
         this.text = var2;
         var2 = var2;
      } catch (var10: IOException) {
         logger.error("read config file ${this.getPath()} failed");
         var2 = null;
      }

      return var2;
   }

   public override suspend fun lines(): List<IndexedValue<String>> {
      var `$continuation`: Continuation;
      label57: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label57;
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
               return this.this$0.lines(this as Continuation<? super java.util.List<IndexedValue<java.lang.String>>>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var18: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      var var10000: Any;
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val var19: java.util.List = this.lines;
            var10000 = if (this.lines != null) this.lines else null;
            if ((if (this.lines != null) this.lines else null) != null) {
               return var10000;
            }

            `$continuation`.L$0 = this;
            `$continuation`.label = 1;
            var10000 = this.text(`$continuation`);
            if (var10000 === var18) {
               return var18;
            }
            break;
         case 1:
            this = `$continuation`.L$0 as SourceFileCheckPoint;
            ResultKt.throwOnFailure(`$result`);
            var10000 = `$result`;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      label44: {
         var10000 = var10000 as java.lang.String;
         if (var10000 as java.lang.String != null) {
            var10000 = StringsKt.split$default(var10000 as java.lang.CharSequence, new java.lang.String[]{"\n"}, false, 0, 6, null);
            if (var10000 != null) {
               var10000 = CollectionsKt.withIndex(var10000 as java.lang.Iterable);
               if (var10000 != null) {
                  val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((java.lang.Iterable)var10000, 10));

                  val `$this$map$iv`: java.lang.Iterable;
                  for (Object item$iv$iv : $this$map$iv) {
                     `destination$iv$iv`.add(new IndexedValue((`item$iv$iv` as IndexedValue).getIndex() + 1, (`item$iv$iv` as IndexedValue).getValue()));
                  }

                  var10000 = `destination$iv$iv` as java.util.List;
                  break label44;
               }
            }
         }

         var10000 = CollectionsKt.emptyList();
      }

      this.lines = (java.util.List<IndexedValue<java.lang.String>>)var10000;
      return var10000;
   }

   public override fun close() {
      this.text = null;
      this.lines = null;
      this.bytes = null;
   }

   override fun getFilename(): java.lang.String {
      return ISourceFileCheckPoint.DefaultImpls.getFilename(this);
   }

   @JvmStatic
   fun `archiveFile_delegate$lambda$0`(`this$0`: SourceFileCheckPoint): Path {
      val var10000: Path;
      if (`this$0`.sFile.isJarScheme()) {
         var10000 = `this$0`.sFile.getSchemePath().toAbsolutePath();
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @JvmStatic
   fun `logger$lambda$7`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }
}
