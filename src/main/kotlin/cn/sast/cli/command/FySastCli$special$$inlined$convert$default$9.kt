package cn.sast.cli.command

import com.github.ajalt.clikt.core.Context
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda

internal class `FySastCli$special$$inlined$convert$default$9` : Lambda, Function1<Context, java.lang.String> {
   @JvmStatic
   public FySastCli$special$$inlined$convert$default$9 INSTANCE = new FySastCli$special$$inlined$convert$default$9();

   fun `FySastCli$special$$inlined$convert$default$9`() {
      super(1);
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> Context.`<anonymous>`(): String {
      return `$this$null`.getLocalization().defaultMetavar();
   }
}
