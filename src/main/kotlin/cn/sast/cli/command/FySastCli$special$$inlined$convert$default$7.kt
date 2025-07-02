package cn.sast.cli.command

import com.github.ajalt.clikt.core.Context
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda

internal class `FySastCli$special$$inlined$convert$default$7` : Lambda, Function1<Context, java.lang.String> {
   fun `FySastCli$special$$inlined$convert$default$7`(`$metavar`: java.lang.String) {
      super(1);
      this.$metavar = `$metavar`;
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> Context.`<anonymous>`(): String {
      return this.$metavar;
   }
}
