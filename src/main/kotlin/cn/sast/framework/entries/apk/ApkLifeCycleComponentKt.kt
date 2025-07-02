package cn.sast.framework.entries.apk

import soot.jimple.infoflow.android.InfoflowAndroidConfiguration.CallbackAnalyzer

public final val convert: CallbackAnalyzerType
   public final get() {
      var var10000: CallbackAnalyzerType;
      switch (ApkLifeCycleComponentKt.WhenMappings.$EnumSwitchMapping$0[$this$convert.ordinal()]) {
         case 1:
            var10000 = CallbackAnalyzerType.Default;
            break;
         case 2:
            var10000 = CallbackAnalyzerType.Fast;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }


public final val convert: CallbackAnalyzer
   public final get() {
      var var10000: CallbackAnalyzer;
      switch (ApkLifeCycleComponentKt.WhenMappings.$EnumSwitchMapping$1[$this$convert.ordinal()]) {
         case 1:
            var10000 = CallbackAnalyzer.Default;
            break;
         case 2:
            var10000 = CallbackAnalyzer.Fast;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

// $VF: Class flags could not be determined
@JvmSynthetic
internal class WhenMappings {
   @JvmStatic
   fun {
      var var0: IntArray = new int[CallbackAnalyzer.values().length];

      try {
         var0[CallbackAnalyzer.Default.ordinal()] = 1;
      } catch (var5: NoSuchFieldError) {
      }

      try {
         var0[CallbackAnalyzer.Fast.ordinal()] = 2;
      } catch (var4: NoSuchFieldError) {
      }

      $EnumSwitchMapping$0 = var0;
      var0 = new int[CallbackAnalyzerType.values().length];

      try {
         var0[CallbackAnalyzerType.Default.ordinal()] = 1;
      } catch (var3: NoSuchFieldError) {
      }

      try {
         var0[CallbackAnalyzerType.Fast.ordinal()] = 2;
      } catch (var2: NoSuchFieldError) {
      }

      $EnumSwitchMapping$1 = var0;
   }
}
