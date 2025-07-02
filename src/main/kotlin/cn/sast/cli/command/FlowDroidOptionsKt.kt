package cn.sast.cli.command

import soot.jimple.infoflow.InfoflowConfiguration.StaticFieldTrackingMode

public final val cvt: StaticFieldTrackingMode
   public final get() {
      var var10000: StaticFieldTrackingMode;
      switch (FlowDroidOptionsKt.WhenMappings.$EnumSwitchMapping$0[$this$cvt.ordinal()]) {
         case 1:
            var10000 = StaticFieldTrackingMode.ContextFlowSensitive;
            break;
         case 2:
            var10000 = StaticFieldTrackingMode.ContextFlowInsensitive;
            break;
         case 3:
            var10000 = StaticFieldTrackingMode.None;
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
      val var0: IntArray = new int[cn.sast.api.config.StaticFieldTrackingMode.values().length];

      try {
         var0[cn.sast.api.config.StaticFieldTrackingMode.ContextFlowSensitive.ordinal()] = 1;
      } catch (var4: NoSuchFieldError) {
      }

      try {
         var0[cn.sast.api.config.StaticFieldTrackingMode.ContextFlowInsensitive.ordinal()] = 2;
      } catch (var3: NoSuchFieldError) {
      }

      try {
         var0[cn.sast.api.config.StaticFieldTrackingMode.None.ordinal()] = 3;
      } catch (var2: NoSuchFieldError) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
