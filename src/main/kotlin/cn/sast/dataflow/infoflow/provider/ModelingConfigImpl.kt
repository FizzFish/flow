package cn.sast.dataflow.infoflow.provider

import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.AttributeName
import com.feysh.corax.config.api.BinOp
import com.feysh.corax.config.api.BuiltInField
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.Elements
import com.feysh.corax.config.api.IBoolExpr
import com.feysh.corax.config.api.IClassDecl
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.IExpr
import com.feysh.corax.config.api.IFieldDecl
import com.feysh.corax.config.api.IIexConst
import com.feysh.corax.config.api.IIexGetField
import com.feysh.corax.config.api.IIexLoad
import com.feysh.corax.config.api.IIstSetField
import com.feysh.corax.config.api.IIstStoreLocal
import com.feysh.corax.config.api.IJDecl
import com.feysh.corax.config.api.ILocalVarDecl
import com.feysh.corax.config.api.IMethodDecl
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.IUnOpExpr
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import com.feysh.corax.config.api.MapKeys
import com.feysh.corax.config.api.MapValues
import com.feysh.corax.config.api.MethodConfig
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.SubFields
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.UnOp
import com.feysh.corax.config.api.ViaProperty
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import com.feysh.corax.config.api.baseimpl.BinOpExpr
import com.feysh.corax.config.api.utils.UtilsKt
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import mu.KLogger
import soot.SootField
import soot.SootMethod
import soot.Type
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowClear
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSink
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSource
import soot.jimple.infoflow.methodSummary.data.summary.ClassMethodSummaries
import soot.jimple.infoflow.methodSummary.data.summary.MethodClear
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries
import soot.jimple.infoflow.methodSummary.data.summary.SourceSinkType
import soot.jimple.infoflow.methodSummary.taintWrappers.AccessPathFragment

@SourceDebugExtension(["SMAP\nMethodSummaryProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MethodSummaryProvider.kt\ncn/sast/dataflow/infoflow/provider/ModelingConfigImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,449:1\n1863#2,2:450\n*S KotlinDebug\n*F\n+ 1 MethodSummaryProvider.kt\ncn/sast/dataflow/infoflow/provider/ModelingConfigImpl\n*L\n420#1:450,2\n*E\n"])
public class ModelingConfigImpl(provider: MethodSummaryProvider, preAnalysis: PreAnalysisApi) : AIAnalysisBaseImpl {
   public final val provider: MethodSummaryProvider
   public open val preAnalysis: PreAnalysisApi

   public open val fastCache: FastCache
      public open get() {
         return FastCacheImpl.INSTANCE;
      }


   public open val scope: CoroutineScope
      public open get() {
         return GlobalScope.INSTANCE as CoroutineScope;
      }


   public open val error: ConfigInfoLogger

   init {
      this.provider = provider;
      this.preAnalysis = preAnalysis;
      this.error = new ConfigInfoLogger();
   }

   private fun createSource(matchStrict: Boolean, loc: MLocal, baseType: String?, fields: List<String>, fieldTypes: List<String>): FlowSource? {
      val acp: AccessPathFragment = if (fields.isEmpty())
         null
         else
         new AccessPathFragment(CollectionsKt.toMutableList(fields), CollectionsKt.toMutableList(fieldTypes));
      if (loc is MReturn) {
         return null;
      } else if (loc is MParameter && (loc as MParameter).getIndex() >= 0) {
         return new FlowSource(SourceSinkType.Parameter, (loc as MParameter).getIndex(), baseType, acp, null, matchStrict);
      } else {
         return if (loc is MParameter && (loc as MParameter).getIndex() == -1) new FlowSource(SourceSinkType.Field, baseType, acp, null, matchStrict) else null;
      }
   }

   private fun createSink(matchStrict: Boolean, taintSubFields: Boolean, loc: MLocal, baseType: String?, fields: List<String>, fieldTypes: List<String>): FlowSink? {
      val acp: AccessPathFragment = if (fields.isEmpty())
         null
         else
         new AccessPathFragment(CollectionsKt.toMutableList(fields), CollectionsKt.toMutableList(fieldTypes));
      if (loc is MReturn) {
         return new FlowSink(SourceSinkType.Return, baseType, acp, taintSubFields, null, matchStrict);
      } else if (loc is MParameter && (loc as MParameter).getIndex() >= 0) {
         return new FlowSink(SourceSinkType.Parameter, (loc as MParameter).getIndex(), baseType, acp, taintSubFields, null, matchStrict);
      } else {
         return if (loc is MParameter && (loc as MParameter).getIndex() == -1)
            new FlowSink(SourceSinkType.Field, baseType, acp, taintSubFields, null, matchStrict)
            else
            null;
      }
   }

   private fun createClear(loc: MLocal, baseType: String?, fields: List<String>, fieldTypes: List<String>): FlowClear? {
      val acp: AccessPathFragment = if (fields.isEmpty())
         null
         else
         new AccessPathFragment(CollectionsKt.toMutableList(fields), CollectionsKt.toMutableList(fieldTypes));
      if (loc !is MReturn) {
         if (loc is MParameter && (loc as MParameter).getIndex() >= 0) {
            return new FlowClear(SourceSinkType.Parameter, (loc as MParameter).getIndex(), baseType, acp, null);
         }

         if (loc is MParameter && (loc as MParameter).getIndex() >= -1) {
            return new FlowClear(SourceSinkType.Field, baseType, acp, null);
         }
      }

      return null;
   }

   public fun IExpr.isEmptySetValue(): Boolean? {
      label16:
      if (`$this$isEmptySetValue` is IIexConst) {
         val var3: Any = (`$this$isEmptySetValue` as IIexConst).getConst();
         return if ((var3 as? java.util.Set) == null) false else (var3 as? java.util.Set).isEmpty();
      } else {
         return null;
      }
   }

   private fun addSummaryFromStmt(method: SootMethod, stmt: IStmt) {
      val summaries: ClassMethodSummaries = new ClassMethodSummaries(method.getDeclaringClass().getName());
      val var10000: MethodSummaries = summaries.getMethodSummaries();
      val summary: MethodSummaries = var10000;
      val from: java.util.List = new ArrayList();
      val to: java.util.List = new ArrayList();
      val subSignature: java.lang.String = method.getSubSignature();
      val typeChecking: ObjectRef = new ObjectRef();
      typeChecking.element = false;
      val ignoreTypes: ObjectRef = new ObjectRef();
      val cutSubfields: ObjectRef = new ObjectRef();
      val matchStrict: BooleanRef = new BooleanRef();
      val taintSubFields: Boolean = false;
      stmt.accept(
         new IModelStmtVisitor<Object>(this, from, summary, method, matchStrict, to, subSignature, typeChecking, ignoreTypes, cutSubfields, taintSubFields) {
            {
               this.this$0 = `$receiver`;
               this.$from = `$from`;
               this.$summary = `$summary`;
               this.$method = `$method`;
               this.$matchStrict = `$matchStrict`;
               this.$to = `$to`;
               this.$subSignature = `$subSignature`;
               this.$typeChecking = `$typeChecking`;
               this.$ignoreTypes = `$ignoreTypes`;
               this.$cutSubfields = `$cutSubfields`;
               this.$taintSubFields = `$taintSubFields`;
            }

            @Override
            public Object default(IStmt stmt) {
               return Unit.INSTANCE;
            }

            public final void initFlowSource(java.util.List<FlowSource> from, MethodSummaries summary, SootMethod method, IExpr expr, boolean matchStrict) {
               ModelingConfigImpl.Companion.getAccessPath(method, CollectionsKt.emptyList(), expr, <unrepresentable>::initFlowSource$lambda$2);
            }

            public void visit(IIstSetField stmt) {
               val field: IClassField = stmt.getField();
               if (field !is ClassField
                  && !(field == TaintProperty.INSTANCE)
                  && !(field == MapKeys.INSTANCE)
                  && !(field == MapValues.INSTANCE)
                  && !(field == Elements.INSTANCE)) {
                  if (field is BuiltInField) {
                  }
               } else {
                  this.initFlowSource(this.$from, this.$summary, this.$method, stmt.getValue(), this.$matchStrict.element);
                  ModelingConfigImpl.Companion
                     .getAccessPath(this.$method, CollectionsKt.listOf(stmt.getField()), stmt.getBase(), <unrepresentable>::visit$lambda$5);
                  val isAlias: Boolean = field !is TaintProperty;

                  for (FlowSource fromE : this.$from) {
                     for (FlowSink toE : this.$to) {
                        this.$summary
                           .addFlow(
                              new MethodFlow(
                                 this.$subSignature,
                                 fromE,
                                 toE,
                                 isAlias,
                                 this.$typeChecking.element as java.lang.Boolean,
                                 this.$ignoreTypes.element as java.lang.Boolean,
                                 this.$cutSubfields.element as java.lang.Boolean
                              )
                           );
                     }
                  }
               }
            }

            @Override
            public Object visit(IIstStoreLocal stmt) {
               this.initFlowSource(this.$from, this.$summary, this.$method, stmt.getValue(), this.$matchStrict.element);
               val toLocal: MLocal = stmt.getLocal();
               val var10000: Type = SourceSinkProviderKt.baseType(this.$method, toLocal);
               val var15: FlowSink = ModelingConfigImpl.access$createSink(
                  this.this$0,
                  this.$matchStrict.element,
                  this.$taintSubFields,
                  toLocal,
                  if (var10000 != null) var10000.toString() else null,
                  CollectionsKt.emptyList(),
                  CollectionsKt.emptyList()
               );
               if (var15 != null) {
                  this.$to.add(var15);
               }

               val isAlias: Boolean = true;

               for (FlowSource fromE : this.$from) {
                  for (FlowSink toE : this.$to) {
                     this.$summary
                        .addFlow(
                           new MethodFlow(
                              this.$subSignature,
                              var13,
                              var14,
                              isAlias,
                              this.$typeChecking.element as java.lang.Boolean,
                              this.$ignoreTypes.element as java.lang.Boolean,
                              this.$cutSubfields.element as java.lang.Boolean
                           )
                        );
                  }
               }

               return Unit.INSTANCE;
            }

            private static final Unit initFlowSource$lambda$2$lambda$1(
               ModelingConfigImpl this$0,
               boolean $matchStrict,
               MLocal $op,
               java.lang.String $baseType,
               IExpr $expr,
               SootMethod $method,
               MethodSummaries $summary,
               java.util.List $from,
               java.util.List fieldSigs,
               java.util.List fieldTypes,
               boolean subFields
            ) {
               val var10000: FlowSource = ModelingConfigImpl.access$createSource(`this$0`, `$matchStrict`, `$op`, `$baseType`, fieldSigs, fieldTypes);
               if (var10000 != null) {
                  `$from`.add(var10000);
               }

               if (`this$0`.isEmptySetValue(`$expr`) == true) {
                  `$summary`.addClear(
                     new MethodClear(`$method`.getSubSignature(), ModelingConfigImpl.access$createClear(`this$0`, `$op`, `$baseType`, fieldSigs, fieldTypes))
                  );
               }

               return Unit.INSTANCE;
            }

            private static final Unit initFlowSource$lambda$2(
               ModelingConfigImpl this$0,
               boolean $matchStrict,
               IExpr $expr,
               SootMethod $method,
               MethodSummaries $summary,
               java.util.List $from,
               MLocal op,
               java.lang.String baseType,
               java.util.List fields
            ) {
               ModelingConfigImpl.Companion.transFields(baseType, fields, <unrepresentable>::initFlowSource$lambda$2$lambda$1);
               return Unit.INSTANCE;
            }

            private static final Unit visit$lambda$5$lambda$4(
               ModelingConfigImpl this$0,
               BooleanRef $matchStrict,
               MLocal $op,
               java.lang.String $baseType,
               java.util.List $to,
               java.util.List fieldSigs,
               java.util.List fieldTypes,
               boolean subFields
            ) {
               val var10000: FlowSink = ModelingConfigImpl.access$createSink(
                  `this$0`, `$matchStrict`.element, subFields, `$op`, `$baseType`, fieldSigs, fieldTypes
               );
               if (var10000 != null) {
                  `$to`.add(var10000);
               }

               return Unit.INSTANCE;
            }

            private static final Unit visit$lambda$5(
               ModelingConfigImpl this$0, BooleanRef $matchStrict, java.util.List $to, MLocal op, java.lang.String baseType, java.util.List fields
            ) {
               ModelingConfigImpl.Companion.transFields(baseType, fields, <unrepresentable>::visit$lambda$5$lambda$4);
               return Unit.INSTANCE;
            }
         }
      );
      this.provider.addMethodSummaries(summaries);
   }

   public override fun addStmt(decl: IJDecl, config: (MethodConfig) -> Unit, stmt: IStmt) {
      if (decl is IMethodDecl) {
         (decl as IMethodDecl).getMatch();

         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            for (SootMethod impl : SetsKt.plus(MethodSummaryProviderKt.findAllOverrideMethodsOfMethod((SootMethod)element$iv), (SootMethod)element$iv)) {
               this.addSummaryFromStmt(impl, stmt);
            }
         }
      } else if (decl !is IClassDecl && decl !is IFieldDecl && decl !is ILocalVarDecl) {
         throw new NoWhenBranchMatchedException();
      }
   }

   public override fun check(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IBoolExpr, checkType: CheckType, env: (Env) -> Unit) {
   }

   public override fun eval(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IExpr, accept: (Any) -> Unit) {
   }

   public override fun validate() {
   }

   @JvmStatic
   fun `logger$lambda$1`(): Unit {
      return Unit.INSTANCE;
   }

   @SourceDebugExtension(["SMAP\nMethodSummaryProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MethodSummaryProvider.kt\ncn/sast/dataflow/infoflow/provider/ModelingConfigImpl$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,449:1\n774#2:450\n865#2,2:451\n1557#2:453\n1628#2,3:454\n1557#2:457\n1628#2,3:458\n*S KotlinDebug\n*F\n+ 1 MethodSummaryProvider.kt\ncn/sast/dataflow/infoflow/provider/ModelingConfigImpl$Companion\n*L\n145#1:450\n145#1:451,2\n147#1:453\n147#1:454,3\n148#1:457\n148#1:458,3\n*E\n"])
   public companion object {
      private final val logger: KLogger

      public fun transFields(baseType: String?, fields: List<IClassField>, res: (List<String>, List<String>, Boolean) -> Unit) {
         if (!fields.isEmpty()) {
            val last: IClassField = CollectionsKt.last(fields) as IClassField;
            val var10000: java.util.List;
            if (last !is ClassField && last !is SubFields) {
               if (!(last == MapValues.INSTANCE)
                  && !(last == MapKeys.INSTANCE)
                  && !(last == Elements.INSTANCE)
                  && last !is AttributeName
                  && last !is ViaProperty
                  && !(last == TaintProperty.INSTANCE)) {
                  throw new NotImplementedError(null, 1, null);
               }

               var10000 = new FieldFinder(baseType, CollectionsKt.dropLast(fields, 1)).sootFields();
            } else {
               var10000 = new FieldFinder(baseType, fields).sootFields();
            }

            for (FieldFinder.AccessPath sf : var10000) {
               var `$this$map$iv`: java.lang.Iterable = sf.getSootField();
               var `destination$iv$iv`: java.util.Collection = new ArrayList();

               for (Object element$iv$iv : $this$filter$iv) {
                  val var37: Type = (`item$iv$iv` as SootField).getType();
                  if (UtilsKt.getTypename(var37) != null) {
                     `destination$iv$iv`.add(`item$iv$iv`);
                  }
               }

               val nonnullTypeNameFields: java.util.List = `destination$iv$iv` as java.util.List;
               `$this$map$iv` = `destination$iv$iv` as java.util.List;
               `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`destination$iv$iv` as java.util.List, 10));

               for (Object item$iv$iv : $this$filter$iv) {
                  `destination$iv$iv`.add((var31 as SootField).getSignature());
               }

               val var10001: java.util.List = `destination$iv$iv` as java.util.List;
               `$this$map$iv` = nonnullTypeNameFields;
               `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(nonnullTypeNameFields, 10));

               for (Object item$iv$iv : $this$filter$iv) {
                  val var38: Type = (var32 as SootField).getType();
                  val var39: java.lang.String = UtilsKt.getTypename(var38);
                  `destination$iv$iv`.add(var39);
               }

               res.invoke(var10001, `destination$iv$iv` as java.util.List, sf.getSubFields());
            }
         } else {
            res.invoke(CollectionsKt.emptyList(), CollectionsKt.emptyList(), true);
         }
      }

      public fun getAccessPath(
         method: SootMethod,
         fields: List<IClassField> = CollectionsKt.emptyList(),
         expr: IExpr,
         cb: (MLocal, String?, List<IClassField>) -> Unit
      ) {
         getAccessPath$resolveExpr(method, cb, expr, fields);
      }

      @JvmStatic
      fun `getAccessPath$add`(
         `$method`: SootMethod, `$cb`: (MLocal?, java.lang.String?, MutableList<IClassField>?) -> Unit, e: IExpr, fields: MutableList<IClassField>
      ) {
         if (e is IIexLoad) {
            val newFields: MLocal = (e as IIexLoad).getOp();
            val var10000: Type = SourceSinkProviderKt.baseType(`$method`, newFields);
            `$cb`.invoke(newFields, if (var10000 != null) var10000.toString() else null, fields);
         } else if (e is IIexGetField) {
            getAccessPath$add(`$method`, `$cb`, (e as IIexGetField).getBase(), CollectionsKt.plus((e as IIexGetField).getAccessPath(), fields));
         }
      }

      @JvmStatic
      fun `getAccessPath$resolveExpr`(
         `$method`: SootMethod, `$cb`: (MLocal?, java.lang.String?, MutableList<IClassField>?) -> Unit, expr: IExpr, fields: MutableList<IClassField>
      ) {
         if (expr is IUnOpExpr && (expr as IUnOpExpr).getOp() === UnOp.GetSet) {
            getAccessPath$add(`$method`, `$cb`, (expr as IUnOpExpr).getOp1(), fields);
         } else if (expr !is BinOpExpr
            || (expr as BinOpExpr).getOp() != BinOp.OrSet
               && (expr as BinOpExpr).getOp() != BinOp.AndSet
               && (expr as BinOpExpr).getOp() != BinOp.RemoveSet
               && (expr as BinOpExpr).getOp() != BinOp.AnyOf) {
            getAccessPath$add(`$method`, `$cb`, expr, fields);
         } else {
            getAccessPath$resolveExpr(`$method`, `$cb`, (expr as BinOpExpr).getOp1(), fields);
            getAccessPath$resolveExpr(`$method`, `$cb`, (expr as BinOpExpr).getOp2(), fields);
         }
      }
   }
}
