package cn.sast.dataflow.infoflow.provider

import cn.sast.api.config.MainConfig
import cn.sast.api.config.PreAnalysisCoroutineScope
import cn.sast.api.config.PreAnalysisCoroutineScopeKt
import cn.sast.common.GLB
import cn.sast.coroutines.caffine.impl.FastCacheImpl
import cn.sast.dataflow.util.ConfigInfoLogger
import com.feysh.corax.cache.coroutines.FastCache
import com.feysh.corax.config.api.BuiltInField
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.Elements
import com.feysh.corax.config.api.IBinOpExpr
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
import com.feysh.corax.config.api.IMethodMatch
import com.feysh.corax.config.api.IModelExpressionVisitor
import com.feysh.corax.config.api.IModelStmtVisitor
import com.feysh.corax.config.api.IQOpExpr
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.ITaintType
import com.feysh.corax.config.api.ITriOpExpr
import com.feysh.corax.config.api.IUnOpExpr
import com.feysh.corax.config.api.MGlobal
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import com.feysh.corax.config.api.MReturn
import com.feysh.corax.config.api.MapKeys
import com.feysh.corax.config.api.MapValues
import com.feysh.corax.config.api.MethodConfig
import com.feysh.corax.config.api.PreAnalysisApi
import com.feysh.corax.config.api.TaintProperty
import com.feysh.corax.config.api.ViaProperty
import com.feysh.corax.config.api.BugMessage.Env
import com.feysh.corax.config.api.baseimpl.AIAnalysisBaseImpl
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.Map.Entry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.ContinuationImpl
import kotlin.jvm.functions.Function4
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import mu.KLogger
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import soot.Scene
import soot.SootMethod
import soot.jimple.infoflow.data.SootMethodAndClass
import soot.jimple.infoflow.sourcesSinks.definitions.AccessPathTuple
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition
import soot.jimple.infoflow.sourcesSinks.definitions.SourceSinkType
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition.CallType

@SourceDebugExtension(["SMAP\nSourceSinkProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,514:1\n1619#2:515\n1863#2:516\n1864#2:518\n1620#2:519\n1454#2,5:520\n1246#2,4:526\n1#3:517\n412#4:525\n216#5,2:530\n*S KotlinDebug\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider\n*L\n453#1:515\n453#1:516\n453#1:518\n453#1:519\n466#1:520,5\n471#1:526,4\n453#1:517\n471#1:525\n483#1:530,2\n*E\n"])
public class SourceSinkProvider(mainConfig: MainConfig, preAnalysisImpl: PreAnalysisCoroutineScope) : ISourceSinkDefinitionProvider {
   public final val mainConfig: MainConfig
   private final val preAnalysisImpl: PreAnalysisCoroutineScope

   public final var sourceDefinitions: ConcurrentHashMap<SootMethod, cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel>
      internal set

   public final var sinkDefinitions: ConcurrentHashMap<SootMethod, cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel>
      internal set

   public final val checkTypesInSink: MutableSet<ITaintType>

   public final var sourceSet: MutableSet<ISourceSinkDefinition>
      internal set

   public final var sinkSet: MutableSet<ISourceSinkDefinition>
      internal set

   public final var allMethods: MutableSet<ISourceSinkDefinition>
      internal set

   private final var missClass: MutableSet<Any>

   init {
      this.mainConfig = mainConfig;
      this.preAnalysisImpl = preAnalysisImpl;
      this.sourceDefinitions = new ConcurrentHashMap<>();
      this.sinkDefinitions = new ConcurrentHashMap<>();
      this.checkTypesInSink = new LinkedHashSet<>();
      this.sourceSet = new LinkedHashSet<>();
      this.sinkSet = new LinkedHashSet<>();
      this.allMethods = new LinkedHashSet<>();
      this.missClass = new LinkedHashSet<>();
   }

   public suspend fun initialize() {
      var `$continuation`: Continuation;
      label38: {
         if (`$completion` is <unrepresentable>) {
            `$continuation` = `$completion` as <unrepresentable>;
            if (((`$completion` as <unrepresentable>).label and Integer.MIN_VALUE) != 0) {
               `$continuation`.label -= Integer.MIN_VALUE;
               break label38;
            }
         }

         `$continuation` = new ContinuationImpl(this, `$completion`) {
            Object L$0;
            int label;

            {
               super(`$completion`);
               this.this$0 = `this$0`;
            }

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = `$result`;
               this.label |= Integer.MIN_VALUE;
               return this.this$0.initialize(this as Continuation<? super Unit>);
            }
         };
      }

      val `$result`: Any = `$continuation`.result;
      val var9: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch ($continuation.label) {
         case 0:
            ResultKt.throwOnFailure(`$result`);
            val aiCheckerImpl: AIAnalysisBaseImpl = new SourceSinkProvider.ModelingConfigImpl(this, this.preAnalysisImpl);
            val var10001: PreAnalysisCoroutineScope = this.preAnalysisImpl;
            `$continuation`.L$0 = this;
            `$continuation`.label = 1;
            if (PreAnalysisCoroutineScopeKt.processAIAnalysisUnits(aiCheckerImpl, var10001, `$continuation`) === var9) {
               return var9;
            }
            break;
         case 1:
            this = `$continuation`.L$0 as SourceSinkProvider;
            ResultKt.throwOnFailure(`$result`);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      val sinkTypes: java.util.Set = new LinkedHashSet();
      var var4: java.util.Iterator = this.sinkDefinitions.entrySet().iterator();

      while (var4.hasNext()) {
         val md: SourceSinkProvider.MethodModel = (var4.next() as Entry).getValue() as SourceSinkProvider.MethodModel;
         val definition: MethodSourceSinkDefinition = initialize$definition(md, null);
         if (!definition.isEmpty()) {
            this.sinkSet.add(definition);
            sinkTypes.addAll(initialize$types$5(md));
         }
      }

      var4 = this.sourceDefinitions.entrySet().iterator();

      while (var4.hasNext()) {
         val var12: MethodSourceSinkDefinition = initialize$definition((var4.next() as Entry).getValue() as SourceSinkProvider.MethodModel, sinkTypes);
         if (!var12.isEmpty()) {
            this.sourceSet.add(var12);
         }
      }

      return Unit.INSTANCE;
   }

   public open fun getSources(): Collection<ISourceSinkDefinition> {
      return this.sourceSet;
   }

   public open fun getSinks(): Collection<ISourceSinkDefinition> {
      return this.sinkSet;
   }

   public open fun getAllMethods(): Collection<ISourceSinkDefinition> {
      return SetsKt.plus(this.sourceSet, this.sinkSet);
   }

   @JvmStatic
   fun MutableSet<SourceSinkProvider.MethodModel.AccPath>.`initialize$apt`(filter: MutableSet<ITaintType>): MutableSet<AccessPathTuple> {
      val `$this$mapNotNullTo$iv`: java.lang.Iterable = `$this$initialize_u24apt`;
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object element$iv$iv : $this$mapNotNullTo$iv) {
         val types: java.util.Set = (`element$iv$iv` as SourceSinkProvider.MethodModel.AccPath).getTypes();
         val var10000: AccessPathTuple = if (types != null && filter != null)
            (if (CollectionsKt.intersect(types, filter).isEmpty()) null else (`element$iv$iv` as SourceSinkProvider.MethodModel.AccPath).getApt())
            else
            (`element$iv$iv` as SourceSinkProvider.MethodModel.AccPath).getApt();
         if (var10000 != null) {
            `destination$iv`.add(var10000);
         }
      }

      return `destination$iv` as MutableSet<AccessPathTuple>;
   }

   @JvmStatic
   fun MutableSet<SourceSinkProvider.MethodModel.AccPath>.`initialize$types`(): MutableSet<ITaintType> {
      val `$this$flatMapTo$iv`: java.lang.Iterable = `$this$initialize_u24types`;
      val `destination$iv`: java.util.Collection = new LinkedHashSet();

      for (Object element$iv : $this$flatMapTo$iv) {
         var var10000: java.util.Set = (`element$iv` as SourceSinkProvider.MethodModel.AccPath).getTypes();
         if (var10000 == null) {
            var10000 = SetsKt.emptySet();
         }

         CollectionsKt.addAll(`destination$iv`, var10000);
      }

      return `destination$iv` as MutableSet<ITaintType>;
   }

   @JvmStatic
   fun SourceSinkProvider.MethodModel.`initialize$definition`(filter: MutableSet<ITaintType>): MethodSourceSinkDefinition {
      val baseObjects: java.util.Set = initialize$apt(`$this$initialize_u24definition`.getBaseObjectsAndTypes(), filter);
      val returnAPs: java.util.Map = `$this$initialize_u24definition`.getParametersMapAndTypes();
      val am: java.util.Map = new LinkedHashMap();

      val `$this$associateByTo$iv$iv`: java.lang.Iterable;
      for (Object element$iv$iv : $this$associateByTo$iv$iv) {
         am.put(
            (`element$iv$iv` as Entry).getKey(),
            initialize$apt((`element$iv$iv` as Entry).getValue() as MutableSet<SourceSinkProvider.MethodModel.AccPath>, filter)
         );
      }

      val parametersMap: java.util.Map = am;
      val var19: java.util.Set = initialize$apt(`$this$initialize_u24definition`.getReturnAPsAndTypes(), filter);
      val var20: SootMethodAndClass = new SootMethodAndClass(`$this$initialize_u24definition`.getMethod());
      var var21: Int = 0;
      val var22: Int = if (am.isEmpty()) 0 else (CollectionsKt.maxOrThrow(am.keySet()) as java.lang.Number).intValue() + 1;

      val var23: Array<java.util.Set>;
      for (var23 = new java.util.Set[var22]; var21 < var22; var21++) {
         var var10002: java.util.Set = parametersMap.get(var21) as java.util.Set;
         if (var10002 == null) {
            var10002 = new LinkedHashSet();
         }

         var23[var21] = var10002;
      }

      return new MethodSourceSinkDefinition(var20, baseObjects, var23, var19, `$this$initialize_u24definition`.getCallType(), null);
   }

   @JvmStatic
   fun SourceSinkProvider.MethodModel.`initialize$types$5`(): MutableSet<ITaintType> {
      val var1: java.util.Set = new LinkedHashSet();
      val it: java.util.Set = var1;
      var1.addAll(initialize$types(`$this$initialize_u24types_u245`.getBaseObjectsAndTypes()));

      for (Entry element$iv : $this$initialize_u24types_u245.getParametersMapAndTypes().entrySet()) {
         it.addAll(initialize$types(`element$iv`.getValue() as MutableSet<SourceSinkProvider.MethodModel.AccPath>));
      }

      it.addAll(initialize$types(`$this$initialize_u24types_u245`.getReturnAPsAndTypes()));
      return var1;
   }

   @JvmStatic
   fun `logger$lambda$6`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   @SourceDebugExtension(["SMAP\nSourceSinkProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider$MethodModel\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,514:1\n12574#2,2:515\n381#3,7:517\n*S KotlinDebug\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider$MethodModel\n*L\n359#1:515,2\n369#1:517,7\n*E\n"])
   public class MethodModel(method: SootMethod, callType: CallType) {
      public final val method: SootMethod
      public final val callType: CallType
      public final val baseObjectsAndTypes: MutableSet<cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel.AccPath>
      public final val parametersMapAndTypes: MutableMap<Int, MutableSet<cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel.AccPath>>
      public final val returnAPsAndTypes: MutableSet<cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel.AccPath>

      init {
         this.method = method;
         this.callType = callType;
         this.baseObjectsAndTypes = new LinkedHashSet<>();
         this.parametersMapAndTypes = new LinkedHashMap<>();
         this.returnAPsAndTypes = new LinkedHashSet<>();
      }

      public fun addParameter(index: Int, atp: AccessPathTuple, types: Set<ITaintType>?) {
         var var10000: Array<java.lang.String> = atp.getFields();
         if (var10000 != null) {
            val `value$iv`: Array<Any> = var10000;
            var var10: Int = 0;
            val var11: Int = var10000.length;

            while (true) {
               if (var10 >= var11) {
                  var22 = false;
                  break;
               }

               val `element$iv`: Any = `value$iv`[var10];
               if (StringsKt.startsWith$default((java.lang.String)`element$iv`, "<", false, 2, null)) {
                  var22 = true;
                  break;
               }

               var10++;
            }

            if (var22) {
               throw new IllegalStateException(java.lang.String.valueOf(atp).toString());
            }
         }

         val ap: SourceSinkProvider.MethodModel.AccPath = new SourceSinkProvider.MethodModel.AccPath(atp, types);
         if (index == -1) {
            this.baseObjectsAndTypes.add(ap);
         } else {
            if (index < 0) {
               throw new IllegalArgumentException("Failed requirement.".toString());
            }

            val `$this$getOrPut$iv`: java.util.Map = this.parametersMapAndTypes;
            val var15: Any = index;
            val var18: Any = `$this$getOrPut$iv`.get(var15);
            if (var18 == null) {
               val var21: Any = new LinkedHashSet();
               `$this$getOrPut$iv`.put(var15, var21);
               var10000 = (java.lang.String[])var21;
            } else {
               var10000 = (java.lang.String[])var18;
            }

            (var10000 as java.util.Set).add(ap);
         }
      }

      public fun addGlobal(atp: AccessPathTuple, types: Set<ITaintType>?) {
      }

      public fun addReturn(atp: AccessPathTuple, types: Set<ITaintType>?) {
         this.returnAPsAndTypes.add(new SourceSinkProvider.MethodModel.AccPath(atp, types));
      }

      public data class AccPath(apt: AccessPathTuple, types: Set<ITaintType>?) {
         public final val apt: AccessPathTuple
         public final val types: Set<ITaintType>?

         init {
            this.apt = apt;
            this.types = types;
         }

         public operator fun component1(): AccessPathTuple {
            return this.apt;
         }

         public operator fun component2(): Set<ITaintType>? {
            return this.types;
         }

         public fun copy(apt: AccessPathTuple = this.apt, types: Set<ITaintType>? = this.types): cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel.AccPath {
            return new SourceSinkProvider.MethodModel.AccPath(apt, types);
         }

         public override fun toString(): String {
            return "AccPath(apt=${this.apt}, types=${this.types})";
         }

         public override fun hashCode(): Int {
            return this.apt.hashCode() * 31 + (if (this.types == null) 0 else this.types.hashCode());
         }

         public override operator fun equals(other: Any?): Boolean {
            if (this === other) {
               return true;
            } else if (other !is SourceSinkProvider.MethodModel.AccPath) {
               return false;
            } else {
               val var2: SourceSinkProvider.MethodModel.AccPath = other as SourceSinkProvider.MethodModel.AccPath;
               if (!(this.apt == (other as SourceSinkProvider.MethodModel.AccPath).apt)) {
                  return false;
               } else {
                  return this.types == var2.types;
               }
            }
         }
      }
   }

   @SourceDebugExtension(["SMAP\nSourceSinkProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider$ModelingConfigImpl\n+ 2 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,514:1\n72#2,2:515\n72#2,2:518\n1#3:517\n1#3:520\n1#3:535\n1#3:548\n1863#4,2:521\n1863#4,2:523\n1611#4,9:525\n1863#4:534\n1864#4:536\n1620#4:537\n1611#4,9:538\n1863#4:547\n1864#4:549\n1620#4:550\n*S KotlinDebug\n*F\n+ 1 SourceSinkProvider.kt\ncn/sast/dataflow/infoflow/provider/SourceSinkProvider$ModelingConfigImpl\n*L\n139#1:515,2\n207#1:518,2\n139#1:517\n207#1:520\n61#1:535\n62#1:548\n300#1:521,2\n323#1:523,2\n61#1:525,9\n61#1:534\n61#1:536\n61#1:537\n62#1:538,9\n62#1:547\n62#1:549\n62#1:550\n*E\n"])
   public inner class ModelingConfigImpl(preAnalysis: PreAnalysisApi) : AIAnalysisBaseImpl {
      public open val preAnalysis: PreAnalysisApi
      public open val error: ConfigInfoLogger

      public open val fastCache: FastCache
         public open get() {
            return FastCacheImpl.INSTANCE;
         }


      public open val scope: CoroutineScope
         public open get() {
            return GlobalScope.INSTANCE as CoroutineScope;
         }


      init {
         this.this$0 = `this$0`;
         this.preAnalysis = preAnalysis;
         this.error = new ConfigInfoLogger();
      }

      private fun setSourceSink(
         method: SootMethod,
         expr: IExpr,
         accessPath: List<IClassField> = CollectionsKt.emptyList(),
         cb: (MLocal, String?, List<String>, List<String>) -> Unit
      ) {
         cn.sast.dataflow.infoflow.provider.ModelingConfigImpl.Companion
            .getAccessPath(method, accessPath, expr, SourceSinkProvider.ModelingConfigImpl::setSourceSink$lambda$2);
      }

      public fun addSource(method: SootMethod, value: IExpr, cb: (cn.sast.dataflow.infoflow.provider.SourceSinkProvider.MethodModel, Set<ITaintType>) -> Unit) {
         var var10000: java.util.Set = value.accept(new IModelExpressionVisitor<java.util.Set<? extends ITaintType>>() {
            public java.util.Set<ITaintType> default(IExpr expr) {
               return null;
            }

            public java.util.Set<ITaintType> visit(IBinOpExpr expr) {
               val op1: java.util.Set = expr.getOp1().accept(this as IModelExpressionVisitor<java.util.Set>);
               val op2: java.util.Set = expr.getOp2().accept(this as IModelExpressionVisitor<java.util.Set>);
               switch (expr.getOp()) {
                  case OrSet:
                     if (op1 == null) {
                        return op2;
                     } else {
                        if (op2 == null) {
                           return op1;
                        }

                        return SetsKt.plus(op1, op2);
                     }
                  case AndSet:
                     if (op1 == null) {
                        return op2;
                     } else {
                        if (op2 == null) {
                           return op1;
                        }

                        return CollectionsKt.intersect(op1, op2);
                     }
                  case RemoveSet:
                  case ContainsSet:
                  case HasIntersectionSet:
                  case Or:
                  case And:
                  case StartsWith:
                  case EndsWith:
                  case Contains:
                  case StringEquals:
                  case AnyOf:
                  case LT:
                  case LE:
                  case EQ:
                  case GE:
                  case GT:
                  case Add:
                  case Sub:
                  case Mul:
                  case Div:
                  case Mod:
                  case Xor:
                  case BvAnd:
                  case BvOr:
                  case BvXor:
                  case BvShr:
                  case BvShl:
                  case BvLShr:
                  case IsInstanceOf:
                     return null;
                  default:
                     throw new NoWhenBranchMatchedException();
               }
            }

            public java.util.Set<ITaintType> visit(IIexConst expr) {
               val var2: Any = expr.getConst();
               return var2 as? java.util.Set;
            }

            public java.util.Set<ITaintType> visit(IIexLoad expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IIexGetField expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IUnOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(ITriOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IQOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }
         });
         if (var10000 != null) {
            if (!var10000.isEmpty()) {
               val `$this$getOrPut$iv`: ConcurrentMap = this.this$0.getSourceDefinitions();
               var10000 = (java.util.Set)`$this$getOrPut$iv`.get(method);
               if (var10000 == null) {
                  val `default$iv`: Any = new SourceSinkProvider.MethodModel(method, CallType.MethodCall);
                  var10000 = (java.util.Set)`$this$getOrPut$iv`.putIfAbsent(method, `default$iv`);
                  if (var10000 == null) {
                     var10000 = (java.util.Set)`default$iv`;
                  }
               }

               val m: SourceSinkProvider.MethodModel = var10000 as SourceSinkProvider.MethodModel;
               cb.invoke(m, var10000);
            }
         }
      }

      private fun findSourceFromStmt(method: SootMethod, stmt: IStmt) {
         stmt.accept(
            new IModelStmtVisitor<Unit>(this, method) {
               {
                  this.this$0 = `$receiver`;
                  this.$method = `$method`;
               }

               public void default(IStmt stmt) {
               }

               public void visit(IIstStoreLocal stmt) {
                  this.this$0.addSource(this.$method, stmt.getValue(), <unrepresentable>::visit$lambda$0);
               }

               public void visit(IIstSetField stmt) {
                  val field: IClassField = stmt.getField();
                  if (field is ClassField || field is TaintProperty) {
                     this.this$0.addSource(this.$method, stmt.getValue(), <unrepresentable>::visit$lambda$2);
                  } else if (field is BuiltInField
                     && !(field as BuiltInField == TaintProperty.INSTANCE)
                     && !(field as BuiltInField == ViaProperty.INSTANCE)
                     && !(field as BuiltInField == MapKeys.INSTANCE)
                     && !(field as BuiltInField == MapValues.INSTANCE)
                     && !(field as BuiltInField == Elements.INSTANCE)) {
                     throw new NoWhenBranchMatchedException();
                  }
               }

               private static final Unit visit$lambda$0(IIstStoreLocal $stmt, SourceSinkProvider.MethodModel m, java.util.Set sources) {
                  val op: MLocal = `$stmt`.getLocal();
                  val sourceSinkType: SourceSinkType = SourceSinkType.Source;
                  val apField: AccessPathTuple = AccessPathTuple.fromPathElements(null, CollectionsKt.emptyList(), CollectionsKt.emptyList(), sourceSinkType);
                  if (op is MReturn) {
                     m.addReturn(apField, sources);
                  } else if (op is MParameter) {
                     val var10001: Int = (op as MParameter).getIndex();
                     m.addParameter(var10001, apField, sources);
                  } else {
                     if (!(op == MGlobal.INSTANCE)) {
                        throw new NoWhenBranchMatchedException();
                     }

                     m.addGlobal(apField, sources);
                  }

                  return Unit.INSTANCE;
               }

               private static final Unit visit$lambda$2$lambda$1(
                  SourceSinkProvider.MethodModel $m,
                  java.util.Set $sources,
                  MLocal op,
                  java.lang.String baseType,
                  java.util.List fields,
                  java.util.List fieldTypes
               ) {
                  val apField: AccessPathTuple = AccessPathTuple.fromPathElements(baseType, fields, fieldTypes, SourceSinkType.Source);
                  if (op is MReturn) {
                     `$m`.addReturn(apField, `$sources`);
                  } else if (op is MParameter) {
                     val var10001: Int = (op as MParameter).getIndex();
                     `$m`.addParameter(var10001, apField, `$sources`);
                  } else {
                     if (!(op == MGlobal.INSTANCE)) {
                        throw new NoWhenBranchMatchedException();
                     }

                     `$m`.addGlobal(apField, `$sources`);
                  }

                  return Unit.INSTANCE;
               }

               private static final Unit visit$lambda$2(
                  IClassField $field,
                  SourceSinkProvider.ModelingConfigImpl this$0,
                  SootMethod $method,
                  IIstSetField $stmt,
                  SourceSinkProvider.MethodModel m,
                  java.util.Set sources
               ) {
                  val var10000: java.util.List;
                  if (`$field` is ClassField) {
                     var10000 = CollectionsKt.listOf(`$field`);
                  } else {
                     if (`$field` !is TaintProperty) {
                        throw new IllegalStateException(("error $`$field`").toString());
                     }

                     var10000 = CollectionsKt.emptyList();
                  }

                  SourceSinkProvider.ModelingConfigImpl.access$setSourceSink(
                     `this$0`, `$method`, `$stmt`.getBase(), var10000, <unrepresentable>::visit$lambda$2$lambda$1
                  );
                  return Unit.INSTANCE;
               }
            }
         );
      }

      private fun addSink(method: SootMethod, expr: IExpr, sinkKinds: Set<ITaintType>?) {
         val `$this$getOrPut$iv`: ConcurrentMap = this.this$0.getSinkDefinitions();
         var var10000: Any = `$this$getOrPut$iv`.get(method);
         if (var10000 == null) {
            val `default$iv`: Any = new SourceSinkProvider.MethodModel(method, CallType.MethodCall);
            var10000 = `$this$getOrPut$iv`.putIfAbsent(method, `default$iv`);
            if (var10000 == null) {
               var10000 = `default$iv`;
            }
         }

         setSourceSink$default(this, method, expr, null, SourceSinkProvider.ModelingConfigImpl::addSink$lambda$5, 4, null);
      }

      private fun findSinkFromStmt(method: SootMethod, expr: IExpr): Set<ITaintType> {
         val var10000: java.util.Set = expr.accept(new IModelExpressionVisitor<java.util.Set<? extends ITaintType>>(this, method) {
            {
               this.this$0 = `$receiver`;
               this.$method = `$method`;
            }

            public java.util.Set<ITaintType> default(IExpr expr) {
               return null;
            }

            public java.util.Set<ITaintType> visit(IBinOpExpr expr) {
               val op1: java.util.Set = expr.getOp1().accept(this as IModelExpressionVisitor<java.util.Set>);
               val op2: java.util.Set = expr.getOp2().accept(this as IModelExpressionVisitor<java.util.Set>);
               var var10000: Any;
               switch (expr.getOp()) {
                  case OrSet:
                     if (op1 == null) {
                        return op2;
                     }

                     if (op2 == null) {
                        return op1;
                     }

                     return SetsKt.plus(op1, op2);
                  case AndSet:
                     if (op1 == null) {
                        return op2;
                     }

                     if (op2 == null) {
                        return op1;
                     }

                     return CollectionsKt.intersect(op1, op2);
                  case RemoveSet:
                     if (op1 != null && op2 != null) {
                        val `$this$filterTo$iv`: java.lang.Iterable = op1;
                        val `destination$iv`: java.util.Collection = new LinkedHashSet();

                        for (Object element$iv : $this$filterTo$iv) {
                           if (!op2.contains(`element$iv` as ITaintType)) {
                              `destination$iv`.add(`element$iv`);
                           }
                        }

                        return `destination$iv` as MutableSet<ITaintType>;
                     }

                     return null;
                  case ContainsSet:
                  case HasIntersectionSet:
                     SourceSinkProvider.ModelingConfigImpl.access$addSink(this.this$0, this.$method, expr.getOp1(), op2);
                     SourceSinkProvider.ModelingConfigImpl.access$addSink(this.this$0, this.$method, expr.getOp2(), op1);
                     if (op1 == null) {
                        return op2;
                     }

                     if (op2 == null) {
                        return op1;
                     }

                     return SetsKt.plus(op1, op2);
                  case AnyOf:
                     var10000 = null;
                     break;
                  case Or:
                     var10000 = null;
                     break;
                  case And:
                     var10000 = null;
                     break;
                  case StartsWith:
                     var10000 = null;
                     break;
                  case EndsWith:
                     var10000 = null;
                     break;
                  case Contains:
                     var10000 = null;
                     break;
                  case StringEquals:
                     var10000 = null;
                     break;
                  case LT:
                     var10000 = null;
                     break;
                  case LE:
                     var10000 = null;
                     break;
                  case EQ:
                     var10000 = null;
                     break;
                  case GE:
                     var10000 = null;
                     break;
                  case GT:
                     var10000 = null;
                     break;
                  case Add:
                     var10000 = null;
                     break;
                  case Sub:
                     var10000 = null;
                     break;
                  case Mul:
                     var10000 = null;
                     break;
                  case Div:
                     var10000 = null;
                     break;
                  case Mod:
                     var10000 = null;
                     break;
                  case Xor:
                     var10000 = null;
                     break;
                  case BvAnd:
                     var10000 = null;
                     break;
                  case BvOr:
                     var10000 = null;
                     break;
                  case BvXor:
                     var10000 = null;
                     break;
                  case BvShr:
                     var10000 = null;
                     break;
                  case BvShl:
                     var10000 = null;
                     break;
                  case BvLShr:
                     var10000 = null;
                     break;
                  case IsInstanceOf:
                     var10000 = null;
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }

               return (java.util.Set<ITaintType>)var10000;
            }

            public java.util.Set<ITaintType> visit(IIexConst expr) {
               val var2: Any = expr.getConst();
               if (var2 is java.util.Set) {
                  val `$this$filterIsInstanceTo$iv`: java.lang.Iterable = var2 as java.lang.Iterable;
                  val `destination$iv`: java.util.Collection = new LinkedHashSet();

                  for (Object element$iv : $this$filterIsInstanceTo$iv) {
                     if (`element$iv` is ITaintType) {
                        `destination$iv`.add(`element$iv`);
                     }
                  }

                  return `destination$iv` as MutableSet<ITaintType>;
               } else {
                  return null;
               }
            }

            public java.util.Set<ITaintType> visit(IIexLoad expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IIexGetField expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IUnOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(ITriOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }

            public java.util.Set<ITaintType> visit(IQOpExpr expr) {
               return IModelExpressionVisitor.DefaultImpls.visit(this, expr);
            }
         });
         return if (var10000 == null) SetsKt.emptySet() else var10000;
      }

      public override fun addStmt(decl: IJDecl, config: (MethodConfig) -> Unit, stmt: IStmt) {
         if (decl is IMethodDecl) {
            (decl as IMethodDecl).getMatch();

            val `$this$forEach$iv`: java.lang.Iterable;
            for (Object element$iv : $this$forEach$iv) {
               this.findSourceFromStmt(`element$iv` as SootMethod, stmt);
            }
         } else if (decl !is IClassDecl && decl !is IFieldDecl && decl !is ILocalVarDecl) {
            throw new NoWhenBranchMatchedException();
         }
      }

      public override fun check(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IBoolExpr, checkType: CheckType, env: (Env) -> Unit) {
         GLB.INSTANCE.plusAssign(checkType);
         if (this.this$0.getMainConfig().isEnable(checkType)) {
            if (decl is IMethodDecl) {
               val var10000: IMethodMatch = (decl as IMethodDecl).getMatch();
               val var10001: Scene = Scene.v();
               val `$this$forEach$iv`: java.lang.Iterable = var10000.matched(var10001);
               val var9: SourceSinkProvider = this.this$0;

               for (Object element$iv : $this$forEach$iv) {
                  val types: java.util.Set = this.findSinkFromStmt(`element$iv` as SootMethod, expr.getExpr());
                  synchronized (var9.getCheckTypesInSink()) {
                     var9.getCheckTypesInSink().addAll(types);
                  }
               }
            } else if (decl !is IClassDecl && decl !is IFieldDecl && decl !is ILocalVarDecl) {
               throw new NoWhenBranchMatchedException();
            }
         }
      }

      public override fun eval(decl: IJDecl, config: (MethodConfig) -> Unit, expr: IExpr, accept: (Any) -> Unit) {
      }

      public override fun validate() {
      }

      @JvmStatic
      fun `setSourceSink$lambda$2`(`$cb`: Function4, op: MLocal, baseType: java.lang.String, fields: java.util.List): Unit {
         val fieldTypes: java.lang.Iterable = fields;
         val `$this$mapNotNullTo$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            val var16: IClassField = `element$iv$iv$iv` as IClassField;
            val var10000: java.lang.String = if ((`element$iv$iv$iv` as IClassField as? ClassField) != null)
               (`element$iv$iv$iv` as IClassField as? ClassField).getFieldName()
               else
               null;
            if (var10000 != null) {
               `$this$mapNotNullTo$iv$iv`.add(var10000);
            }
         }

         val fieldSigs: java.util.List = `$this$mapNotNullTo$iv$iv` as java.util.List;
         val `$this$mapNotNull$ivx`: java.lang.Iterable = fields;
         val `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$ivx : $this$mapNotNull$ivx) {
            val var27: IClassField = `element$iv$iv$ivx` as IClassField;
            val var30: java.lang.String = if ((`element$iv$iv$ivx` as IClassField as? ClassField) != null)
               (`element$iv$iv$ivx` as IClassField as? ClassField).getFieldType()
               else
               null;
            if (var30 != null) {
               `destination$iv$ivx`.add(var30);
            }
         }

         `$cb`.invoke(op, baseType, fieldSigs, `destination$iv$ivx` as java.util.List);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `addSink$lambda$5`(
         `$m`: SourceSinkProvider.MethodModel,
         `$sinkKinds`: java.util.Set,
         op: MLocal,
         baseType: java.lang.String,
         fields: java.util.List,
         fieldTypes: java.util.List
      ): Unit {
         val apField: AccessPathTuple = AccessPathTuple.fromPathElements(baseType, fields, fieldTypes, SourceSinkType.Sink);
         if (op is MReturn) {
            `$m`.addReturn(apField, `$sinkKinds`);
         } else if (op is MParameter) {
            val var10001: Int = (op as MParameter).getIndex();
            `$m`.addParameter(var10001, apField, `$sinkKinds`);
         } else {
            if (!(op == MGlobal.INSTANCE)) {
               throw new NoWhenBranchMatchedException();
            }

            `$m`.addGlobal(apField, `$sinkKinds`);
         }

         return Unit.INSTANCE;
      }
   }
}
