package cn.sast.framework.entries.utils

import mu.KLogger
import mu.KotlinLogging
import soot.ArrayType
import soot.Body
import soot.Local
import soot.LocalGenerator
import soot.PrimType
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.Value
import soot.jimple.Jimple
import utils.BaseBodyGenerator
import utils.INewUnits
import utils.NewUnitsAtLastImmediately

public class PhantomValueForType(dummyClassName: String = "dummyMainClass") {
   private final val dummyClassName: String
   private final val logger: KLogger

   private final val summaryClass: SootClass
      private final get() {
         var var10000: SootClass = Scene.v().getSootClassUnsafe(this.dummyClassName, false);
         if (var10000 == null) {
            val var1: SootClass = Scene.v().makeSootClass(this.dummyClassName);
            var1.setResolvingLevel(3);
            var1.setApplicationClass();
            var10000 = var1;
         }

         return var10000;
      }


   init {
      this.dummyClassName = dummyClassName;
      this.logger = KotlinLogging.INSTANCE.logger(PhantomValueForType::logger$lambda$0);
   }

   public fun getName(tp: Type): String? {
      var var10000: java.lang.String;
      if (tp is PrimType) {
         var10000 = tp.toString();
      } else if (tp is RefType) {
         var10000 = (tp as RefType).getClassName();
         var10000 = StringsKt.substringAfterLast$default(var10000, ".", null, 2, null);
      } else {
         if (tp !is ArrayType) {
            this.logger.warn("Unsupported parameter type: {}", tp.toString());
            return null;
         }

         val var10001: Type = (tp as ArrayType).getElementType();
         var10000 = "${this.getName(var10001)}Array";
      }

      return var10000;
   }

   public fun getOrMakeSootMethodForType(tp: Type, name: String, number: Int = 1): SootMethod {
      var var10000: SootMethod = this.getSummaryClass().getMethodByNameUnsafe(name);
      if (var10000 == null) {
         val var6: SootMethod = Scene.v().makeSootMethod(name, CollectionsKt.emptyList(), tp, 8);
         this.getSummaryClass().addMethod(var6);
         var6.setPhantom(true);
         var10000 = var6;
      }

      if (var10000.getReturnType() == tp) {
         var10000 = var10000;
      } else {
         var10000 = this.getOrMakeSootMethodForType(tp, "$name$number", number + 1);
      }

      return var10000;
   }

   public fun getValueForType(units: INewUnits, generator: LocalGenerator, tp: Type): Local? {
      val var10000: java.lang.String = this.getName(tp);
      if (var10000 == null) {
         return null;
      } else {
         val getter: SootMethod = getOrMakeSootMethodForType$default(this, tp, "get$var10000", 0, 4, null);
         val local: Local = generator.generateLocal(tp);
         units.add(Jimple.v().newAssignStmt(local as Value, Jimple.v().newStaticInvokeExpr(getter.makeRef()) as Value) as Unit);
         return local;
      }
   }

   public fun getValueForType(body: Body, generator: LocalGenerator, tp: Type): Local? {
      return this.getValueForType((new NewUnitsAtLastImmediately(body.getUnits())) as INewUnits, generator, tp);
   }

   public fun getValueForType(body: BaseBodyGenerator, tp: Type): Local? {
      val var10001: INewUnits = (new NewUnitsAtLastImmediately(body.getUnits())) as INewUnits;
      val var10002: LocalGenerator = body.getGenerator();
      return this.getValueForType(var10001, var10002, tp);
   }

   public fun getValueForType(newUnits: INewUnits, body: BaseBodyGenerator, tp: Type): Local? {
      val var10002: LocalGenerator = body.getGenerator();
      return this.getValueForType(newUnits, var10002, tp);
   }

   @JvmStatic
   fun `logger$lambda$0`(): kotlin.Unit {
      return kotlin.Unit.INSTANCE;
   }

   fun PhantomValueForType() {
      this(null, 1, null);
   }
}
