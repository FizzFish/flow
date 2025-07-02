package cn.sast.cli.command

import com.github.ajalt.clikt.core.Context
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Lambda

internal class `DataFlowOptions$special$$inlined$convert$default$1` : Lambda, Function1<Context, java.lang.String> {
   fun `DataFlowOptions$special$$inlined$convert$default$1`(`$metavar`: java.lang.String) {
      super(1);
      this.$metavar = `$metavar`;
   }

   // QF: local property
internal fun <InT : Any, ValueT : Any> Context.`<anonymous>`(): String {
      return this.$metavar;
   }
}
