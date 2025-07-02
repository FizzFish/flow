@file:SourceDebugExtension(["SMAP\nSootUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,512:1\n1557#2:513\n1628#2,3:514\n1557#2:517\n1628#2,3:518\n1368#2:522\n1454#2,5:523\n1368#2:528\n1454#2,5:529\n1454#2,2:536\n1557#2:538\n1628#2,3:539\n1456#2,3:542\n1368#2:545\n1454#2,5:546\n1#3:521\n183#4,2:534\n*S KotlinDebug\n*F\n+ 1 SootUtils.kt\ncn/sast/api/util/SootUtilsKt\n*L\n52#1:513\n52#1:514,3\n55#1:517\n55#1:518,3\n441#1:522\n441#1:523,5\n442#1:528\n442#1:529,5\n506#1:536,2\n507#1:538\n507#1:539,3\n506#1:542,3\n455#1:545\n455#1:546,5\n474#1:534,2\n*E\n"])

package cn.sast.api.util

import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.ResourceKt
import com.feysh.corax.cache.AnalysisCache
import com.feysh.corax.cache.analysis.SootRangeKey
import com.feysh.corax.config.api.utils.UtilsKt
import com.google.common.base.Optional
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.reflect.Constructor
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.CallableReference
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KDeclarationContainer
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.KCallables
import kotlin.reflect.jvm.KClassesJvm
import kotlin.reflect.jvm.ReflectJvmMapping
import soot.Body
import soot.BooleanType
import soot.ByteType
import soot.CharType
import soot.DoubleType
import soot.FloatType
import soot.G
import soot.IntType
import soot.IntegerType
import soot.Local
import soot.LongType
import soot.Printer
import soot.RefLikeType
import soot.Scene
import soot.ShortType
import soot.SootClass
import soot.SootMethod
import soot.SootMethodRef
import soot.SourceLocator
import soot.Type
import soot.Value
import soot.asm.AsmUtil
import soot.jimple.AddExpr
import soot.jimple.AndExpr
import soot.jimple.ArithmeticConstant
import soot.jimple.ClassConstant
import soot.jimple.CmpExpr
import soot.jimple.CmpgExpr
import soot.jimple.CmplExpr
import soot.jimple.Constant
import soot.jimple.DefinitionStmt
import soot.jimple.DivExpr
import soot.jimple.DoubleConstant
import soot.jimple.EqExpr
import soot.jimple.Expr
import soot.jimple.FloatConstant
import soot.jimple.GeExpr
import soot.jimple.GtExpr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.IntConstant
import soot.jimple.InvokeExpr
import soot.jimple.JasminClass
import soot.jimple.LeExpr
import soot.jimple.LongConstant
import soot.jimple.LtExpr
import soot.jimple.MulExpr
import soot.jimple.NeExpr
import soot.jimple.NullConstant
import soot.jimple.NumericConstant
import soot.jimple.OrExpr
import soot.jimple.RealConstant
import soot.jimple.RemExpr
import soot.jimple.ShlExpr
import soot.jimple.ShrExpr
import soot.jimple.SpecialInvokeExpr
import soot.jimple.Stmt
import soot.jimple.StringConstant
import soot.jimple.SubExpr
import soot.jimple.UshrExpr
import soot.jimple.XorExpr
import soot.jimple.toolkits.callgraph.VirtualCalls
import soot.tagkit.SourceFileTag
import soot.tagkit.Tag
import soot.util.Chain
import soot.util.JasminOutputStream
import soot.util.queue.ChunkedQueue
import soot.util.queue.QueueReader

public final val KClass: KClass<*>
   public final get() {
      var var10000: KClass;
      if (`$this$KClass` is CallableReference) {
         val var1: KDeclarationContainer = (`$this$KClass` as CallableReference).getOwner();
         var10000 = var1 as? KClass;
      } else {
         label27: {
            val var3: KParameter = KCallables.getInstanceParameter(`$this$KClass`);
            if (var3 != null) {
               val var4: KType = var3.getType();
               if (var4 != null) {
                  var5 = var4.getClassifier();
                  break label27;
               }
            }

            var5 = null;
         }

         var10000 = var5 as? KClass;
      }

      if (var10000 == null) {
         var10000 = tryConstructor(`$this$KClass`);
         if (var10000 == null) {
            throw new IllegalStateException(("Can't get parent class for $`$this$KClass`").toString());
         }
      }

      return var10000;
   }


public final val paramStringList: List<String>
   public final get() {
      val var20: java.util.List;
      if (`$this$paramStringList` is CallableReference) {
         val var10000: java.lang.String = (`$this$paramStringList` as CallableReference).getSignature();
         val `$this$map$iv`: java.util.List = AsmUtil.toJimpleDesc(StringsKt.substringAfter$default(var10000, "(", null, 2, null), Optional.fromNullable(null));
         `$this$map$iv`.remove(`$this$map$iv`.size() - 1);
         val `$i$f$map`: java.lang.Iterable = `$this$map$iv`;
         val `$i$f$mapTo`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

         for (Object item$iv$iv : $this$map$iv) {
            val var9: Type = it as Type;
            val var19: java.lang.String = UtilsKt.getTypename(var9);
            `$i$f$mapTo`.add(var19);
         }

         var20 = `$i$f$mapTo` as java.util.List;
      } else {
         val var12: java.lang.Iterable = CollectionsKt.drop(
            `$this$paramStringList`.getParameters(), if (KCallables.getInstanceParameter(`$this$paramStringList`) != null) 1 else 0
         );
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var12, 10));

         for (Object item$iv$iv : var12) {
            val var21: java.lang.String = ReflectJvmMapping.getJavaType((var16 as KParameter).getType()).getTypeName();
            `destination$iv$iv`.add(StringsKt.substringBefore$default(var21, '<', null, 2, null));
         }

         var20 = `destination$iv$iv` as java.util.List;
      }

      return var20;
   }


public final val paramSignature: String
   public final get() {
      return CollectionsKt.joinToString$default(getParamStringList(`$this$paramSignature`), ",", null, null, 0, null, null, 62, null);
   }


public final val subSignature: String
   public final get() {
      if (`$this$subSignature` is CallableReference) {
         val var10000: java.lang.String = (`$this$subSignature` as CallableReference).getSignature();
         val sigTypes: java.util.List = AsmUtil.toJimpleDesc(StringsKt.substringAfter$default(var10000, "(", null, 2, null), Optional.fromNullable(null));
         val returnType: Type = sigTypes.remove(sigTypes.size() - 1) as Type;
         return "$returnType ${(`$this$subSignature` as CallableReference).getName()}(${CollectionsKt.joinToString$default(
            sigTypes, ",", null, null, 0, null, SootUtilsKt::_get_subSignature_$lambda$2, 30, null
         )})";
      } else {
         return "${ReflectJvmMapping.getJavaType(`$this$subSignature`.getReturnType()).getTypeName()} ${`$this$subSignature`.getName()}(${getParamSignature(
            `$this$subSignature`
         )})";
      }
   }


public final val returnSootType: Type
   public final get() {
      var var3: java.lang.String;
      if (`$this$returnSootType` is CallableReference) {
         var3 = (`$this$returnSootType` as CallableReference).getSignature();
         val sigTypes: java.util.List = AsmUtil.toJimpleDesc(StringsKt.substringAfter$default(var3, "(", null, 2, null), Optional.fromNullable(null));
         var3 = (sigTypes.remove(sigTypes.size() - 1) as Type).toString();
      } else {
         var3 = ReflectJvmMapping.getJavaType(`$this$returnSootType`.getReturnType()).getTypeName();
      }

      val var4: Type = Scene.v().getTypeUnsafe(var3, true);
      return var4;
   }


public final val sootClassName: String
   public final get() {
      val ks: KClass = getKClass(`$this$sootClassName`);
      val var10000: java.lang.String;
      if (ks is ClassBasedDeclarationContainer) {
         var10000 = (ks as ClassBasedDeclarationContainer).getJClass().getName();
      } else {
         var10000 = ks.getQualifiedName();
      }

      return var10000;
   }


public final val sootSignature: String
   public final get() {
      return "<${getSootClassName(`$this$sootSignature`)}: ${getSubSignature(`$this$sootSignature`)}>";
   }


public final val grabSootMethod: SootMethod?
   public final get() {
      return Scene.v().grabMethod(getSootSignature(`$this$grabSootMethod`));
   }


public final val sootMethod: SootMethod
   public final get() {
      val var10000: SootMethod = Scene.v().getMethod(getSootSignature(`$this$sootMethod`));
      return var10000;
   }


public final val sootClass: SootClass
   public final get() {
      val var10000: SootClass = Scene.v().getSootClass(KClassesJvm.getJvmName(`$this$sootClass`));
      return var10000;
   }


public final val className: String
   public final get() {
      return KClassesJvm.getJvmName(`$this$className`);
   }


public final val sootClassUnsafe: SootClass?
   public final get() {
      return Scene.v().getSootClassUnsafe(KClassesJvm.getJvmName(`$this$sootClassUnsafe`), false);
   }


public final val invokeExprOrNull: InvokeExpr?
   public final inline get() {
      return if ((`$this$invokeExprOrNull` as? Stmt) != null)
         (if ((`$this$invokeExprOrNull` as Stmt).containsInvokeExpr()) (`$this$invokeExprOrNull` as Stmt).getInvokeExpr() else null)
         else
         null;
   }


public final val invokeExprOrNull: InvokeExpr?
   public final inline get() {
      return if (`$this$invokeExprOrNull`.containsInvokeExpr()) `$this$invokeExprOrNull`.getInvokeExpr() else null;
   }


public final val leftOp: Value?
   public final inline get() {
      return if ((`$this$leftOp` as? DefinitionStmt) != null) (`$this$leftOp` as? DefinitionStmt).getLeftOp() else null;
   }


public final val numCode: Int
   public final get() {
      var loc: Int = 0;

      for (SootMethod sm : $this$numCode.getMethods()) {
         if (sm.hasActiveBody()) {
            val var10000: AnalysisCache.G = AnalysisCache.G.INSTANCE;
            val range: Pair = var10000.get(new SootRangeKey(sm));
            if (range != null) {
               loc = Math.max(Math.max(loc, (range.component1() as java.lang.Number).intValue()), (range.component2() as java.lang.Number).intValue());
            }
         }
      }

      return loc;
   }


public final val sourcePath: String?
   public final get() {
      return ClassPathUtilKt.getSourcePathModule(`$this$sourcePath`);
   }


public final val possibleSourceFiles: LinkedHashSet<String>
   public final get() {
      val res: LinkedHashSet = new LinkedHashSet();
      var var10000: java.lang.String = getSourcePath(`$this$possibleSourceFiles`);
      if (var10000 != null) {
         res.add(var10000);
      }

      val list: LinkedHashSet = new LinkedHashSet();
      val var10001: SourceLocator = SourceLocator.v();
      val var10002: java.lang.String = `$this$possibleSourceFiles`.getName();
      list.add(var10001.getSourceForClass(StringsKt.replace$default(var10002, ".", "/", false, 4, null)));
      var10000 = `$this$possibleSourceFiles`.getName();
      if (StringsKt.indexOf$default(var10000, "$", 0, false, 6, null) != -1) {
         val var24: java.lang.String = `$this$possibleSourceFiles`.getName();
         list.add(
            CollectionsKt.joinToString$default(
               StringsKt.split$default(var24, new java.lang.String[]{"."}, false, 0, 6, null), "/", null, null, 0, null, null, 62, null
            )
         );
      }

      val var19: java.lang.Iterable;
      for (Object element$iv : var19) {
         val `list$iv`: java.lang.String = `element$iv` as java.lang.String;
         val `$this$map$iv`: java.lang.Iterable = ResourceKt.getJavaExtensions();
         val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

         for (Object item$iv$iv : $this$map$iv) {
            `destination$iv$iv`.add("$`list$iv`.${`item$iv$iv` as java.lang.String}");
         }

         CollectionsKt.addAll(res, `destination$iv$iv` as java.util.List);
      }

      return res;
   }


public final val activeBodyOrNull: Body?
   public final get() {
      return if (`$this$activeBodyOrNull`.hasActiveBody()) `$this$activeBodyOrNull`.getActiveBody() else null;
   }


private fun <R> tryConstructor(function: KCallable<R>): KClass<out Any>? {
   var var3: Class;
   label21: {
      val var10000: KFunction = function as? KFunction;
      if ((function as? KFunction) != null) {
         val var2: Constructor = ReflectJvmMapping.getJavaConstructor(var10000);
         if (var2 != null) {
            var3 = var2.getDeclaringClass();
            break label21;
         }
      }

      var3 = null;
   }

   return if (var3 != null) JvmClassMappingKt.getKotlinClass(var3) else null;
}

public fun convertParameterTypes(paramTypes: List<CharSequence>): List<Type> {
   val parameterTypes: java.util.List = new ArrayList();

   for (java.lang.CharSequence type : paramTypes) {
      val var10001: Type = Scene.v().getTypeUnsafe(type.toString(), true);
      parameterTypes.add(var10001);
   }

   return parameterTypes;
}

public fun <R> KCallable<R>.sootMethodRef(isStatic: Boolean): SootMethodRef {
   val var10000: SootClass = Scene.v().getSootClass(getSootClassName(`$this$sootMethodRef`));
   val var3: SootMethodRef = Scene.v()
      .makeMethodRef(
         var10000,
         `$this$sootMethodRef`.getName(),
         convertParameterTypes(getParamStringList(`$this$sootMethodRef`)),
         getReturnSootType(`$this$sootMethodRef`),
         isStatic
      );
   return var3;
}

public fun classSplit(cp: SootClass): Pair<String, String> {
   val var10000: java.lang.String = cp.getName();
   return classSplit(var10000);
}

public fun classSplit(cname: String): Pair<String, String> {
   return TuplesKt.to(StringsKt.substringBeforeLast(cname, ".", ""), StringsKt.substringAfterLast$default(cname, ".", null, 2, null));
}

public fun SootClass.getSourcePathFromAnnotation(): String? {
   val fixed: Tag = `$this$getSourcePathFromAnnotation`.getTag("SourceFileTag");
   val var10000: SourceFileTag = fixed as? SourceFileTag;
   if ((fixed as? SourceFileTag) == null) {
      return null;
   } else {
      val source: java.lang.String = var10000.getSourceFile();
      val var5: java.lang.String = StringsKt.substringBeforeLast$default(
         StringsKt.substringBeforeLast$default(StringsKt.substringBeforeLast$default(source, "..", null, 2, null), "/", null, 2, null), "\\", null, 2, null
      );
      if (!ResourceKt.getJavaExtensions().contains(StringsKt.substringAfterLast$default(var5, ".", null, 2, null))) {
         return null;
      } else {
         val var6: java.lang.String = StringsKt.replace$default(
            classSplit(`$this$getSourcePathFromAnnotation`).getFirst() as java.lang.String, ".", "/", false, 4, null
         );
         return if (var6.length() == 0) var5 else "$var6/$var5";
      }
   }
}

public fun NumericConstant.castTo(toType: Type): Constant? {
   if (toType is BooleanType) {
      if (`$this$castTo` is IntConstant) {
         return IntConstant.v(if ((`$this$castTo` as IntConstant).value != 0) 1 else 0) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return IntConstant.v(if ((int)(`$this$castTo` as LongConstant).value != 0) 1 else 0) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return IntConstant.v(if ((int)(`$this$castTo` as FloatConstant).value != 0) 1 else 0) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return IntConstant.v(if ((int)(`$this$castTo` as DoubleConstant).value != 0) 1 else 0) as Constant;
      }
   } else if (toType is ByteType) {
      if (`$this$castTo` is IntConstant) {
         return IntConstant.v((byte)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return IntConstant.v((byte)((int)(`$this$castTo` as LongConstant).value)) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return IntConstant.v((byte)((int)(`$this$castTo` as FloatConstant).value)) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return IntConstant.v((byte)((int)(`$this$castTo` as DoubleConstant).value)) as Constant;
      }
   } else if (toType is CharType) {
      if (`$this$castTo` is IntConstant) {
         return IntConstant.v((char)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return IntConstant.v((char)((int)(`$this$castTo` as LongConstant).value)) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return IntConstant.v((char)((int)(`$this$castTo` as FloatConstant).value)) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return IntConstant.v((char)((int)(`$this$castTo` as DoubleConstant).value)) as Constant;
      }
   } else if (toType is ShortType) {
      if (`$this$castTo` is IntConstant) {
         return IntConstant.v((short)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return IntConstant.v((short)((int)(`$this$castTo` as LongConstant).value)) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return IntConstant.v((short)((int)(`$this$castTo` as FloatConstant).value)) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return IntConstant.v((short)((int)(`$this$castTo` as DoubleConstant).value)) as Constant;
      }
   } else if (toType is IntType) {
      if (`$this$castTo` is IntConstant) {
         return `$this$castTo` as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return IntConstant.v((int)(`$this$castTo` as LongConstant).value) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return IntConstant.v((int)(`$this$castTo` as FloatConstant).value) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return IntConstant.v((int)(`$this$castTo` as DoubleConstant).value) as Constant;
      }
   } else if (toType is LongType) {
      if (`$this$castTo` is IntConstant) {
         return LongConstant.v((long)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return `$this$castTo` as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return LongConstant.v((long)(`$this$castTo` as FloatConstant).value) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return LongConstant.v((long)(`$this$castTo` as DoubleConstant).value) as Constant;
      }
   } else if (toType is FloatType) {
      if (`$this$castTo` is IntConstant) {
         return FloatConstant.v((float)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return FloatConstant.v((float)(`$this$castTo` as LongConstant).value) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return `$this$castTo` as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return FloatConstant.v((float)(`$this$castTo` as DoubleConstant).value) as Constant;
      }
   } else if (toType is DoubleType) {
      if (`$this$castTo` is IntConstant) {
         return DoubleConstant.v((double)(`$this$castTo` as IntConstant).value) as Constant;
      }

      if (`$this$castTo` is LongConstant) {
         return DoubleConstant.v((double)(`$this$castTo` as LongConstant).value) as Constant;
      }

      if (`$this$castTo` is FloatConstant) {
         return DoubleConstant.v((double)(`$this$castTo` as FloatConstant).value) as Constant;
      }

      if (`$this$castTo` is DoubleConstant) {
         return `$this$castTo` as Constant;
      }
   }

   return null;
}

public fun Constant.equalEqual(b: Constant, isEq: Boolean): NumericConstant? {
   val var10000: NumericConstant;
   if (`$this$equalEqual` is NumericConstant) {
      var10000 = if (b !is NumericConstant)
         IntConstant.v(0) as NumericConstant
         else
         (
            if (isEq)
               (`$this$equalEqual` as NumericConstant).equalEqual(b as NumericConstant)
               else
               (`$this$equalEqual` as NumericConstant).notEqual(b as NumericConstant)
         );
   } else if (`$this$equalEqual` !is StringConstant && `$this$equalEqual` !is NullConstant && `$this$equalEqual` !is ClassConstant) {
      var10000 = null;
   } else {
      val equality: Boolean = `$this$equalEqual` == b;
      var10000 = IntConstant.v(if ((if (isEq) equality else !equality)) 1 else 0) as NumericConstant;
   }

   return var10000;
}

@Throws(java/lang/ArithmeticException::class, java/lang/IllegalArgumentException::class)
public fun evalConstantBinop(expr: Expr, c1: Constant, c2: Constant): NumericConstant? {
   return if (expr is AddExpr)
      (c1 as NumericConstant).add(c2 as NumericConstant)
      else
      (
         if (expr is SubExpr)
            (c1 as NumericConstant).subtract(c2 as NumericConstant)
            else
            (
               if (expr is MulExpr)
                  (c1 as NumericConstant).multiply(c2 as NumericConstant)
                  else
                  (
                     if (expr is DivExpr)
                        (c1 as NumericConstant).divide(c2 as NumericConstant)
                        else
                        (
                           if (expr is RemExpr)
                              (c1 as NumericConstant).remainder(c2 as NumericConstant)
                              else
                              (
                                 if (expr is EqExpr)
                                    equalEqual(c1, c2, true)
                                    else
                                    (
                                       if (expr is NeExpr)
                                          equalEqual(c1, c2, false)
                                          else
                                          (
                                             if (expr is GtExpr)
                                                (c1 as NumericConstant).greaterThan(c2 as NumericConstant)
                                                else
                                                (
                                                   if (expr is GeExpr)
                                                      (c1 as NumericConstant).greaterThanOrEqual(c2 as NumericConstant)
                                                      else
                                                      (
                                                         if (expr is LtExpr)
                                                            (c1 as NumericConstant).lessThan(c2 as NumericConstant)
                                                            else
                                                            (
                                                               if (expr is LeExpr)
                                                                  (c1 as NumericConstant).lessThanOrEqual(c2 as NumericConstant)
                                                                  else
                                                                  (
                                                                     if (expr is AndExpr)
                                                                        (c1 as ArithmeticConstant).and(c2 as ArithmeticConstant) as NumericConstant
                                                                        else
                                                                        (
                                                                           if (expr is OrExpr)
                                                                              (c1 as ArithmeticConstant).or(c2 as ArithmeticConstant) as NumericConstant
                                                                              else
                                                                              (
                                                                                 if (expr is XorExpr)
                                                                                    (c1 as ArithmeticConstant).xor(c2 as ArithmeticConstant) as NumericConstant
                                                                                    else
                                                                                    (
                                                                                       if (expr is ShlExpr)
                                                                                          (c1 as ArithmeticConstant).shiftLeft(c2 as ArithmeticConstant) as NumericConstant
                                                                                          else
                                                                                          (
                                                                                             if (expr is ShrExpr)
                                                                                                (c1 as ArithmeticConstant).shiftRight(c2 as ArithmeticConstant) as NumericConstant
                                                                                                else
                                                                                                (
                                                                                                   if (expr is UshrExpr)
                                                                                                      (c1 as ArithmeticConstant)
                                                                                                         .unsignedShiftRight(c2 as ArithmeticConstant) as NumericConstant
                                                                                                      else
                                                                                                      (
                                                                                                         if (expr is CmpExpr)
                                                                                                            (
                                                                                                               if (c1 is LongConstant && c2 is LongConstant)
                                                                                                                  (c1 as LongConstant).cmp(c2 as LongConstant)
                                                                                                                  else
                                                                                                                  null
                                                                                                            ) as NumericConstant
                                                                                                            else
                                                                                                            (
                                                                                                               if (expr !is CmpgExpr && expr !is CmplExpr)
                                                                                                                  null
                                                                                                                  else
                                                                                                                  (
                                                                                                                     if (c1 !is RealConstant
                                                                                                                           || c2 !is RealConstant)
                                                                                                                        null
                                                                                                                        else
                                                                                                                        (
                                                                                                                           if (expr is CmpgExpr)
                                                                                                                              (c1 as RealConstant)
                                                                                                                                 .cmpg(c2 as RealConstant)
                                                                                                                              else
                                                                                                                              (
                                                                                                                                 if (expr is CmplExpr)
                                                                                                                                    (c1 as RealConstant)
                                                                                                                                       .cmpl(c2 as RealConstant)
                                                                                                                                    else
                                                                                                                                    null
                                                                                                                              )
                                                                                                                        )
                                                                                                                  ) as NumericConstant
                                                                                                            )
                                                                                                      )
                                                                                                )
                                                                                          )
                                                                                    )
                                                                              )
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
}

@Throws(java/lang/NumberFormatException::class)
public fun <ToType : Type> StringConstant.cvtNumericConstant(radix: Int, type: ToType): NumericConstant? {
   val var10000: java.lang.String = `$this$cvtNumericConstant`.value;
   return cvtNumericConstant(var10000, radix, type);
}

@Throws(java/lang/NumberFormatException::class)
public fun <ToType : Type> String.cvtNumericConstant(radix: Int, type: ToType): NumericConstant? {
   if (2 > radix || radix >= 37) {
      return null;
   } else {
      return if (type is IntegerType)
         IntConstant.v(Integer.parseInt(`$this$cvtNumericConstant`, CharsKt.checkRadix(radix))) as NumericConstant
         else
         (
            if (type is LongType)
               LongConstant.v(java.lang.Long.parseLong(`$this$cvtNumericConstant`, CharsKt.checkRadix(radix))) as NumericConstant
               else
               (
                  if (type is FloatType)
                     FloatConstant.v(java.lang.Float.parseFloat(`$this$cvtNumericConstant`)) as NumericConstant
                     else
                     (DoubleConstant.v(java.lang.Double.parseDouble(`$this$cvtNumericConstant`)) as? NumericConstant)
               )
         );
   }
}

public inline fun Constant.accurateType(declareType: () -> Type): Type {
   val it: Type = `$this$accurateType`.getType();
   return if (it is RefLikeType) it else declareType.invoke() as Type;
}

public fun printToSootClass(dir: String, sClass: SootClass) {
   val p: File = Paths.get("$dir${File.separator}${SourceLocator.v().getFileNameFor(sClass, 1)}").toFile();
   if (!p.getParentFile().exists()) {
      p.getParentFile().mkdirs();
   }

   val writerOut: PrintWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(p)));
   Printer.v().printTo(sClass, writerOut);
   writerOut.flush();
   writerOut.close();
}

@Throws(java/lang/Exception::class)
public fun sootClass2JasminClass(sClass: SootClass, out: IResDirectory): IResFile {
   label19: {
      val fileName: java.lang.String = SourceLocator.v().getFileNameFor(sClass, 14);
      val outCLass: IResFile = out.resolve(fileName).toFile();
      outCLass.mkdirs();
      val var10002: Path = outCLass.getPath();
      val var10003: Array<OpenOption> = new OpenOption[0];
      val var15: OutputStream = Files.newOutputStream(var10002, Arrays.copyOf(var10003, var10003.length));
      val var4: Closeable = (new JasminOutputStream(var15)) as Closeable;
      var var5: java.lang.Throwable = null;

      try {
         try {
            val writerOut: PrintWriter = new PrintWriter(new OutputStreamWriter((var4 as JasminOutputStream) as OutputStream));
            new JasminClass(sClass).print(writerOut);
            writerOut.flush();
         } catch (var10: java.lang.Throwable) {
            var5 = var10;
            throw var10;
         }
      } catch (var11: java.lang.Throwable) {
         CloseableKt.closeFinally(var4, var5);
      }

      CloseableKt.closeFinally(var4, null);
   }
}

public fun constOf(v: Any): Pair<Constant, Type> {
   val var10000: Pair;
   if (v is Constant) {
      var10000 = TuplesKt.to(v, (v as Constant).getType());
   } else if (v is java.lang.String) {
      var10000 = TuplesKt.to(StringConstant.v(v as java.lang.String), Scene.v().getType("java.lang.String"));
   } else if (v is java.lang.Boolean) {
      var10000 = TuplesKt.to(IntConstant.v(if (v as java.lang.Boolean) 1 else 0), G.v().soot_BooleanType());
   } else {
      if (v !is java.lang.Number) {
         throw new NotImplementedError(null, 1, null);
      }

      if (v is Int) {
         var10000 = TuplesKt.to(IntConstant.v((v as java.lang.Number).intValue()), G.v().soot_IntType());
      } else if (v is java.lang.Long) {
         var10000 = TuplesKt.to(LongConstant.v((v as java.lang.Number).longValue()), G.v().soot_LongType());
      } else if (v is java.lang.Double) {
         var10000 = TuplesKt.to(DoubleConstant.v((v as java.lang.Number).doubleValue()), G.v().soot_DoubleType());
      } else if (v is java.lang.Float) {
         var10000 = TuplesKt.to(FloatConstant.v((v as java.lang.Number).floatValue()), G.v().soot_FloatType());
      } else if (v is java.lang.Byte) {
         var10000 = TuplesKt.to(IntConstant.v((v as java.lang.Number).byteValue()), G.v().soot_ByteType());
      } else {
         if (v !is java.lang.Short) {
            throw new NotImplementedError(null, 1, null);
         }

         var10000 = TuplesKt.to(IntConstant.v((v as java.lang.Number).shortValue()), G.v().soot_ShortType());
      }
   }

   return var10000;
}

public fun getCallTargets(type: Type, container: SootMethod? = null, ie: InvokeExpr, appOnly: Boolean = false): Iterator<SootMethod> {
   val methodRef: SootMethodRef = ie.getMethodRef();
   val virtualCalls: VirtualCalls = VirtualCalls.v();
   val targetsQueue: ChunkedQueue = new ChunkedQueue();
   val var10000: QueueReader = targetsQueue.reader();
   val iter: java.util.Iterator = var10000 as java.util.Iterator;
   if (ie is SpecialInvokeExpr) {
      val var9: SootMethod = virtualCalls.resolveSpecial(methodRef, container, appOnly);
      if (var9 != null) {
         targetsQueue.add(var9);
      }

      return iter;
   } else {
      val var11: Type;
      if (ie is InstanceInvokeExpr) {
         val var10: Value = (ie as InstanceInvokeExpr).getBase();
         var11 = (var10 as Local).getType();
      } else {
         var11 = methodRef.getDeclaringClass().getType() as Type;
      }

      virtualCalls.resolve(type, var11, methodRef, container, targetsQueue, appOnly);
      return iter;
   }
}

@JvmSynthetic
fun `getCallTargets$default`(var0: Type, var1: SootMethod, var2: InvokeExpr, var3: Boolean, var4: Int, var5: Any): java.util.Iterator {
   if ((var4 and 2) != 0) {
      var1 = null;
   }

   if ((var4 and 8) != 0) {
      var3 = false;
   }

   return getCallTargets(var0, var1, var2, var3);
}

public fun SootClass.adjustLevel(level: Int) {
   if (`$this$adjustLevel`.resolvingLevel() < level) {
      `$this$adjustLevel`.setResolvingLevel(level);
   }
}

public fun SootClass.superClassOrNull(): SootClass? {
   return if (`$this$superClassOrNull`.hasSuperclass()) `$this$superClassOrNull`.getSuperclass() else null;
}

private fun findAncestors(sc: SootClass): List<SootClass> {
   val superClasses: java.util.List = new ArrayList();
   val superInterfaces: java.util.List = new ArrayList();
   if (sc.isInterface()) {
      superClasses.add(Scene.v().getObjectType().getSootClass());
      val var5: java.util.Collection = superInterfaces;
      val var10000: java.util.List = Scene.v().getActiveHierarchy().getSuperinterfacesOfIncluding(sc);
      CollectionsKt.addAll(var5, var10000);
   } else {
      var var15: java.util.Collection = superClasses;
      var var28: java.util.List = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(sc);
      CollectionsKt.addAll(var15, var28);
      var15 = superInterfaces;
      var `$this$flatMap$iv`: java.lang.Iterable = superClasses;
      var `destination$iv$iv`: java.util.Collection = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         val var29: Chain = (`element$iv$iv` as SootClass).getInterfaces();
         CollectionsKt.addAll(`destination$iv$iv`, var29 as java.lang.Iterable);
      }

      `$this$flatMap$iv` = `destination$iv$iv` as java.util.List;
      `destination$iv$iv` = new ArrayList();

      for (Object element$iv$iv : $this$flatMap$iv) {
         var28 = Scene.v().getActiveHierarchy().getSuperinterfacesOfIncluding(var23 as SootClass);
         CollectionsKt.addAll(`destination$iv$iv`, var28);
      }

      CollectionsKt.addAll(var15, `destination$iv$iv` as java.util.List);
   }

   return CollectionsKt.plus(superClasses, superInterfaces);
}

public fun SootClass.findMethodOrNull(subSignature: String): Sequence<SootMethod> {
   adjustLevel(`$this$findMethodOrNull`, 2);
   return SequencesKt.filter(
      SequencesKt.flatMapIterable(
         SequencesKt.plus(
            SequencesKt.generateSequence(`$this$findMethodOrNull`, SootUtilsKt::findMethodOrNull$lambda$10),
            SequencesKt.distinct(
               SequencesKt.flatMapIterable(
                  SequencesKt.generateSequence(`$this$findMethodOrNull`, SootUtilsKt::findMethodOrNull$lambda$11), SootUtilsKt::findMethodOrNull$lambda$13
               )
            )
         ),
         SootUtilsKt::findMethodOrNull$lambda$14
      ),
      SootUtilsKt::findMethodOrNull$lambda$15
   );
}

public fun SootClass.findMethodOrNull(subSignature: String, predicate: (SootMethod) -> Boolean): SootMethod? {
   val var5: java.util.Iterator = findMethodOrNull(`$this$findMethodOrNull`, subSignature).iterator();

   var var10000: Any;
   while (true) {
      if (var5.hasNext()) {
         val `element$iv`: Any = var5.next();
         if (!predicate.invoke(`element$iv`) as java.lang.Boolean) {
            continue;
         }

         var10000 = `element$iv`;
         break;
      }

      var10000 = null;
      break;
   }

   return var10000 as SootMethod;
}

fun `_get_subSignature_$lambda$2`(it: Type): java.lang.CharSequence {
   val var10000: java.lang.String = UtilsKt.getTypename(it);
   return var10000;
}

fun `findMethodOrNull$lambda$10`(it: SootClass): SootClass {
   return superClassOrNull(it);
}

fun `findMethodOrNull$lambda$11`(it: SootClass): SootClass {
   return superClassOrNull(it);
}

fun `findMethodOrNull$lambda$13`(sootClass: SootClass): java.lang.Iterable {
   val var10000: Chain = sootClass.getInterfaces();
   val `$this$flatMap$iv`: java.lang.Iterable = var10000 as java.lang.Iterable;
   val `destination$iv$iv`: java.util.Collection = new ArrayList();

   for (Object element$iv$iv : $this$flatMap$iv) {
      val `list$iv$iv`: SootClass = `element$iv$iv` as SootClass;
      CollectionsKt.addAll(`destination$iv$iv`, findAncestors(`list$iv$iv`));
   }

   return `destination$iv$iv` as java.util.List;
}

fun `findMethodOrNull$lambda$14`(it: SootClass): java.lang.Iterable {
   val var10000: java.util.List = it.getMethods();
   return var10000;
}

fun `findMethodOrNull$lambda$15`(`$this_findMethodOrNull`: SootClass, `$params`: java.lang.String, it: SootMethod): Boolean {
   if (!(`$this_findMethodOrNull` == it.getDeclaringClass())) {
      if (it.isStatic() || it.isStaticInitializer()) {
         return false;
      }

      if (it.isPrivate()) {
         return false;
      }
   }

   val var10000: java.lang.String = it.getSubSignature();
   return StringsKt.substringAfter$default(var10000, " ", null, 2, null) == `$params`;
}
