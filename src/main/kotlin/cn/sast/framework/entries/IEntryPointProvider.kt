package cn.sast.framework.entries

import cn.sast.framework.SootCtx
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.util.Chain

public interface IEntryPointProvider {
   public val iterator: Flow<cn.sast.framework.entries.IEntryPointProvider.AnalyzeTask>

   public open fun startAnalyse() {
   }

   public open fun endAnalyse() {
   }

   public interface AnalyzeTask {
      public val name: String
      public val entries: Set<SootMethod>

      public open val methodsMustAnalyze: Set<SootMethod>
         public open get() {
         }


      public open val additionalEntries: Set<SootMethod>?
         public open get() {
         }


      public open val components: Set<SootClass>?
         public open get() {
         }


      public abstract fun needConstructCallGraph(sootCtx: SootCtx) {
      }

      // $VF: Class flags could not be determined
      @SourceDebugExtension(["SMAP\nIEntryPointProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IEntryPointProvider.kt\ncn/sast/framework/entries/IEntryPointProvider$AnalyzeTask$DefaultImpls\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,25:1\n774#2:26\n865#2,2:27\n1454#2,5:29\n*S KotlinDebug\n*F\n+ 1 IEntryPointProvider.kt\ncn/sast/framework/entries/IEntryPointProvider$AnalyzeTask$DefaultImpls\n*L\n15#1:26\n15#1:27,2\n15#1:29,5\n*E\n"])
      internal class DefaultImpls {
         @JvmStatic
         fun getMethodsMustAnalyze(`$this`: IEntryPointProvider.AnalyzeTask): MutableSet<SootMethod> {
            val var10000: Chain = Scene.v().getApplicationClasses();
            var `$this$flatMapTo$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
            val `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               if ((var7 as SootClass).isInScene()) {
                  `destination$iv$iv`.add(var7);
               }
            }

            `$this$flatMapTo$iv` = `destination$iv$iv` as java.util.List;
            val var11: java.util.Collection = new LinkedHashSet();

            for (Object element$iv : $this$filter$iv) {
               val var17: java.util.List = (var13 as SootClass).getMethods();
               CollectionsKt.addAll(var11, var17);
            }

            return var11 as MutableSet<SootMethod>;
         }

         @JvmStatic
         fun getAdditionalEntries(`$this`: IEntryPointProvider.AnalyzeTask): MutableSet<SootMethod>? {
            return null;
         }

         @JvmStatic
         fun getComponents(`$this`: IEntryPointProvider.AnalyzeTask): MutableSet<SootClass>? {
            return null;
         }
      }
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun startAnalyse(`$this`: IEntryPointProvider) {
      }

      @JvmStatic
      fun endAnalyse(`$this`: IEntryPointProvider) {
      }
   }
}
