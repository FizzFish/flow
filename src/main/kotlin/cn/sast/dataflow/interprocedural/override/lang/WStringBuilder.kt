package cn.sast.dataflow.interprocedural.override.lang

import cn.sast.dataflow.interprocedural.analysis.ACheckCallAnalysis
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.SummaryHandlePackage
import cn.sast.dataflow.util.SootUtilsKt
import com.feysh.corax.config.api.utils.UtilsKt
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KCallable
import soot.ArrayType
import soot.G
import soot.RefType
import soot.SootField
import soot.Type
import soot.jimple.Jimple
import soot.jimple.NewExpr

public class WStringBuilder : SummaryHandlePackage<IValue> {
   public override fun ACheckCallAnalysis.register() {
      val appendInt: Function2 = <unrepresentable>.INSTANCE;
      val appendString: Function2 = <unrepresentable>.INSTANCE;
      val appendBoolean: Function2 = <unrepresentable>.INSTANCE;
      val appendChar: Function2 = <unrepresentable>.INSTANCE;
      val appendLong: Function2 = <unrepresentable>.INSTANCE;
      val appendShort: Function2 = <unrepresentable>.INSTANCE;
      val appendIntSignature: java.lang.String = UtilsKt.getSootSignature((appendInt as CallableReference) as KCallable<?>);
      val appendStringSignature: java.lang.String = UtilsKt.getSootSignature((appendString as CallableReference) as KCallable<?>);
      val appendBooleanSignature: java.lang.String = UtilsKt.getSootSignature((appendBoolean as CallableReference) as KCallable<?>);
      val appendCharSignature: java.lang.String = UtilsKt.getSootSignature((appendChar as CallableReference) as KCallable<?>);
      val appendLongSignature: java.lang.String = UtilsKt.getSootSignature((appendLong as CallableReference) as KCallable<?>);
      val appendShortSignature: java.lang.String = UtilsKt.getSootSignature((appendShort as CallableReference) as KCallable<?>);
      val newInteger: NewExpr = Jimple.v().newNewExpr(RefType.v("java.lang.Integer"));
      val var10002: ArrayType = ArrayType.v(G.v().soot_ByteType() as Type, 1);
      val valueField: SootField = SootUtilsKt.getOrMakeField("java.lang.AbstractStringBuilder", "value", var10002 as Type);
   }

   public companion object {
      public fun v(): WStringBuilder {
         return new WStringBuilder();
      }
   }
}
