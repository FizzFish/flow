package cn.sast.dataflow.interprocedural.analysis

import cn.sast.api.config.StaticFieldTrackingMode
import cn.sast.dataflow.util.SootUtilsKt
import soot.ArrayType
import soot.DoubleType
import soot.FloatType
import soot.G
import soot.IntegerType
import soot.Local
import soot.LongType
import soot.NullType
import soot.RefLikeType
import soot.RefType
import soot.Scene
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.jimple.Constant
import soot.jimple.DoubleConstant
import soot.jimple.FloatConstant
import soot.jimple.IntConstant
import soot.jimple.Jimple
import soot.jimple.LongConstant
import soot.jimple.NopStmt
import soot.jimple.NullConstant
import soot.jimple.internal.JimpleLocal

public class IVGlobal(GLOBAL_SITE: IValue = (new GlobalStaticObject()) as IValue) {
   public final val GLOBAL_SITE: IValue
   public final val GLOBAL_LOCAL: String
   public final val RETURN_LOCAL: String
   public final val SUMMARY_ETURN_LOCAL: Local
   public final val STRING_TYPE: RefType
   public final val OBJECT_TYPE: RefType
   public final val BYTE_ARRAY_TYPE: ArrayType
   public final val OBJ_ARRAY_TYPE: ArrayType
   public final val CLASS_TYPE: RefType
   public final val NOP: Unit
   public final val NEW_Env: AnyNewExprEnv
   public final val NULL_CONST: NullConstant
   public final val INT_ZERO_CONST: IntConstant
   public final val DOUBLE_ZERO_CONST: DoubleConstant
   public final val Float_ZERO_CONST: FloatConstant
   public final val Long_ZERO_CONST: LongConstant

   public open var staticFieldTrackingMode: StaticFieldTrackingMode
      internal final set

   init {
      this.GLOBAL_SITE = GLOBAL_SITE;
      this.GLOBAL_LOCAL = "@global";
      this.RETURN_LOCAL = "@return";
      this.SUMMARY_ETURN_LOCAL = (new JimpleLocal("@SummaryReturn", Scene.v().getObjectType() as Type)) as Local;
      var var10001: RefType = RefType.v("java.lang.String");
      this.STRING_TYPE = var10001;
      var10001 = Scene.v().getObjectType();
      this.OBJECT_TYPE = var10001;
      val var3: ArrayType = ArrayType.v(G.v().soot_ByteType() as Type, 1);
      this.BYTE_ARRAY_TYPE = var3;
      val var4: ArrayType = ArrayType.v(this.OBJECT_TYPE as Type, 1);
      this.OBJ_ARRAY_TYPE = var4;
      var10001 = RefType.v("java.lang.Class");
      this.CLASS_TYPE = var10001;
      val var6: NopStmt = Jimple.v().newNopStmt();
      this.NOP = var6 as Unit;
      val var10003: SootMethod = SootUtilsKt.sootSignatureToRef("<java.lang.String: void <clinit>()>", true).resolve();
      this.NEW_Env = new AnyNewExprEnv(var10003, this.NOP);
      this.NULL_CONST = NullConstant.v();
      this.INT_ZERO_CONST = IntConstant.v(0);
      this.DOUBLE_ZERO_CONST = DoubleConstant.v(0.0);
      this.Float_ZERO_CONST = FloatConstant.v(0.0F);
      this.Long_ZERO_CONST = LongConstant.v(0L);
      this.staticFieldTrackingMode = StaticFieldTrackingMode.ContextFlowSensitive;
   }

   public fun zeroValue(array: ArrayType): Pair<Constant, Type> {
      val var10001: Type = array.getElementType();
      return this.defaultValue(var10001);
   }

   public fun defaultValue(type: Type): Pair<Constant, Type> {
      val var10000: Pair;
      if (type is RefLikeType) {
         var10000 = TuplesKt.to(this.NULL_CONST, NullType.v());
      } else if (type is IntegerType) {
         var10000 = TuplesKt.to(this.INT_ZERO_CONST, type);
      } else if (type is DoubleType) {
         var10000 = TuplesKt.to(this.DOUBLE_ZERO_CONST, type);
      } else if (type is FloatType) {
         var10000 = TuplesKt.to(this.Float_ZERO_CONST, type);
      } else {
         if (type !is LongType) {
            throw new NotImplementedError("An operation is not implemented: error type of $this");
         }

         var10000 = TuplesKt.to(this.Long_ZERO_CONST, type);
      }

      return var10000;
   }

   fun IVGlobal() {
      this(null, 1, null);
   }
}
