package cn.sast.framework.report

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.enums.EnumEntries
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJavaSourceLocator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/AbstractFileIndexer\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,490:1\n1381#2:491\n1469#2,2:492\n1471#2,3:495\n1#3:494\n*S KotlinDebug\n*F\n+ 1 JavaSourceLocator.kt\ncn/sast/framework/report/AbstractFileIndexer\n*L\n197#1:491\n197#1:492,2\n197#1:495,3\n*E\n"])
public abstract class AbstractFileIndexer<PathType> {
   private final var errorMsgShow: Boolean

   public abstract fun getNames(path: Any, mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode): List<String> {
   }

   public abstract fun getPathsByName(name: String): Collection<Any> {
   }

   private fun List<String>.hasDot(): Boolean {
      val sz: Int = `$this$hasDot`.size();
      if (sz <= 1) {
         return false;
      } else {
         val i: Int = 0;

         for (java.lang.String e : $this$hasDot) {
            if (++i != sz && StringsKt.contains$default(e, ".", false, 2, null)) {
               return true;
            }
         }

         return false;
      }
   }

   private fun List<String>.normalizePathParts(mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode = AbstractFileIndexer.CompareMode.Path): List<
         String
      > {
      if (mode.isClass() && this.hasDot(`$this$normalizePathParts`)) {
         val lastIndex: Int = CollectionsKt.getLastIndex(`$this$normalizePathParts`);
         val ret: ArrayList = new ArrayList(`$this$normalizePathParts`.size() + 2);
         val i: Int = 0;

         for (java.lang.String e : $this$normalizePathParts) {
            if (i++ != lastIndex && StringsKt.contains$default(e, ".", false, 2, null)) {
               CollectionsKt.addAll(ret, StringsKt.split$default(e, new java.lang.String[]{"."}, false, 0, 6, null));
            } else {
               ret.add(e);
            }
         }

         return ret;
      } else {
         return `$this$normalizePathParts`;
      }
   }

   public fun findFromFileIndexMap(
      toFindNames: List<String>,
      mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode = AbstractFileIndexer.CompareMode.Path
   ): Sequence<Any> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super PathType>, Continuation<? super Unit>, Object>(toFindNames, this, mode, null) {
         Object L$1;
         Object L$2;
         Object L$3;
         Object L$4;
         int label;

         {
            super(2, `$completionx`);
            this.$toFindNames = `$toFindNames`;
            this.this$0 = `$receiver`;
            this.$mode = `$mode`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         public final Object invokeSuspend(Object $result) {
            val var14: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var find: java.util.List;
            var var5: AbstractFileIndexer;
            var var6: AbstractFileIndexer.CompareMode;
            var var8: java.util.Iterator;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  if (this.$toFindNames.isEmpty()) {
                     return Unit.INSTANCE;
                  }

                  find = AbstractFileIndexer.access$normalizePathParts(this.this$0, this.$toFindNames, this.$mode);
                  val `$this$forEach$iv`: java.lang.Iterable = this.this$0.getPathsByName(CollectionsKt.last(find) as java.lang.String);
                  var5 = this.this$0;
                  var6 = this.$mode;
                  var8 = `$this$forEach$iv`.iterator();
                  break;
               case 1:
                  var8 = this.L$4 as java.util.Iterator;
                  var6 = this.L$3 as AbstractFileIndexer.CompareMode;
                  var5 = this.L$2 as AbstractFileIndexer;
                  find = this.L$1 as java.util.List;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               case 2:
                  var8 = this.L$4 as java.util.Iterator;
                  var6 = this.L$3 as AbstractFileIndexer.CompareMode;
                  var5 = this.L$2 as AbstractFileIndexer;
                  find = this.L$1 as java.util.List;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while (var8.hasNext()) {
               val `element$iv`: Any = var8.next();
               val checkNames: java.util.List = AbstractFileIndexer.access$normalizePathParts(var5, var5.getNames(`element$iv`, var6), var6);
               if (var6 === AbstractFileIndexer.CompareMode.ClassNoPackageLayout) {
                  this.L$0 = `$this$sequence`;
                  this.L$1 = find;
                  this.L$2 = var5;
                  this.L$3 = var6;
                  this.L$4 = var8;
                  this.label = 1;
                  if (`$this$sequence`.yield(`element$iv`, this) === var14) {
                     return var14;
                  }
               } else {
                  val r: ListSuffixRelation = JavaSourceLocatorKt.listEndsWith(checkNames, find);
                  if (r === ListSuffixRelation.Equals || r === ListSuffixRelation.BIsSuffixOfA) {
                     this.L$0 = `$this$sequence`;
                     this.L$1 = find;
                     this.L$2 = var5;
                     this.L$3 = var6;
                     this.L$4 = var8;
                     this.label = 2;
                     if (`$this$sequence`.yield(`element$iv`, this) === var14) {
                        return var14;
                     }
                  }
               }
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            val var3: Function2 = new <anonymous constructor>(this.$toFindNames, this.this$0, this.$mode, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         public final Object invoke(SequenceScope<? super PathType> p1, Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   public fun findFromFileIndexMap(find: Any, mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode = AbstractFileIndexer.CompareMode.Path): Sequence<
         Any
      > {
      return this.findFromFileIndexMap(this.getNames((PathType)find, mode), mode);
   }

   public fun findFiles(fileNames: Collection<String>, mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode): List<Any> {
      val `$this$flatMap$iv`: java.lang.Iterable = fileNames;
      val `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val `list$iv$iv`: java.lang.String = `element$iv$iv` as java.lang.String;
         if (StringsKt.indexOf$default(`element$iv$iv` as java.lang.String, "\\", 0, false, 6, null) != -1) {
            throw new IllegalArgumentException(("invalid source file name: $`list$iv$iv`").toString());
         }

         CollectionsKt.addAll(
            `destination$iv$iv`, this.findFromFileIndexMap(StringsKt.split$default(`list$iv$iv`, new java.lang.String[]{"/"}, false, 0, 6, null), mode)
         );
      }

      return `destination$iv$iv` as MutableList<PathType>;
   }

   public fun findAnyFile(fileNames: Collection<String>, mode: cn.sast.framework.report.AbstractFileIndexer.CompareMode): Any? {
      val var4: java.util.Iterator = fileNames.iterator();

      while (true) {
         var var14: Pair;
         if (var4.hasNext()) {
            val s: java.lang.String = var4.next() as java.lang.String;
            if (StringsKt.indexOf$default(s, "\\", 0, false, 6, null) != -1) {
               throw new IllegalArgumentException(("invalid source file name: $s in $fileNames").toString());
            }

            var14 = (Pair)SequencesKt.firstOrNull(this.findFromFileIndexMap(StringsKt.split$default(s, new java.lang.String[]{"/"}, false, 0, 6, null), mode));
            var14 = if (var14 != null) TuplesKt.to(s, var14) else null;
            if (var14 == null) {
               continue;
            }

            var14 = var14;
         } else {
            var14 = null;
         }

         return (PathType)(if (var14 != null) var14.getSecond() else null);
      }
   }

   public companion object {
      public final var defaultClassCompareMode: cn.sast.framework.report.AbstractFileIndexer.CompareMode
         internal set
   }

   public enum class CompareMode(isClass: Boolean) {
      Path(false),
      Class(true),
      ClassNoPackageLayout(true)
      public final val isClass: Boolean

      init {
         this.isClass = isClass;
      }

      @JvmStatic
      fun getEntries(): EnumEntries<AbstractFileIndexer.CompareMode> {
         return $ENTRIES;
      }
   }
}
