package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.AbstractHeapFactory
import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.analysis.JOperatorV
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl.EvalCall
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementSet
import com.feysh.corax.config.api.IIexConst
import java.util.ArrayList
import java.util.HashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.PrimType
import soot.RefLikeType
import soot.SootMethod
import soot.jimple.AnyNewExpr
import soot.jimple.BinopExpr
import soot.jimple.Constant
import soot.jimple.NegExpr

@SourceDebugExtension(["SMAP\nOperatorPathFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorPathFactory.kt\ncn/sast/dataflow/interprocedural/check/OperatorPathFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,201:1\n1628#2,3:202\n1454#2,2:205\n1368#2:207\n1557#2:208\n1628#2,3:209\n1456#2,3:212\n*S KotlinDebug\n*F\n+ 1 OperatorPathFactory.kt\ncn/sast/dataflow/interprocedural/check/OperatorPathFactory\n*L\n73#1:202,3\n77#1:205,2\n77#1:207\n77#1:208\n77#1:209,3\n77#1:212,3\n*E\n"])
internal data class OperatorPathFactory(heapFactory: AbstractHeapFactory<IValue>, env: HeapValuesEnv, alloc: IValue, path: IPath? = null) : JOperatorV<IValue> {
   public final val heapFactory: AbstractHeapFactory<IValue>
   public final val env: HeapValuesEnv
   private final val alloc: IValue
   public final val path: IPath?

   init {
      this.heapFactory = heapFactory;
      this.env = env;
      this.alloc = alloc;
      this.path = path;
   }

   public override fun pop(): CompanionV<IValue> {
      val var10000: CompanionValueImpl1 = new CompanionValueImpl1;
      var var10003: IPath = this.path;
      if (this.path == null) {
         var10003 = UnknownPath.Companion.v(this.env);
      }

      var10000./* $VF: Unable to resugar constructor */<init>(this.alloc, var10003);
      return var10000;
   }

   public override fun popHV(): IHeapValues<IValue> {
      return this.heapFactory.empty().plus(this.pop());
   }

   public override fun markOfConstant(c: Constant, info: Any?): JOperatorV<IValue> {
      return copy$default(this, null, null, null, LiteralPath.Companion.v(this.env, c, info), 7, null);
   }

   public override fun markOfConstant(c: IIexConst): JOperatorV<IValue> {
      return copy$default(this, null, null, null, LiteralPath.Companion.v(this.env, c), 7, null);
   }

   public override fun markOfNewExpr(expr: AnyNewExpr): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markSummaryValueFromArrayGet(array: CompanionV<IValue>, info: Any?): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (array as PathCompanionV).getPath(), 7, null);
   }

   public override fun markSummaryValueInCaughtExceptionRhs(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markSummaryReturnValueFailedInHook(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markSummaryReturnValueInCalleeSite(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfCantCalcAbstractResultValue(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfEntryMethodParam(entryPoint: SootMethod): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markSootBinOp(expr: BinopExpr, clop: CompanionV<IValue>, crop: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(
         this, null, null, null, MergePath.Companion.v(this.env, (clop as PathCompanionV).getPath(), (crop as PathCompanionV).getPath()), 7, null
      );
   }

   public override fun markOfOp(op: Any, op1: CompanionV<IValue>, vararg ops: CompanionV<IValue>): JOperatorV<IValue> {
      val var10004: MergePath.Companion = MergePath.Companion;
      val var10005: HeapValuesEnv = this.env;
      val `$this$mapTo$iv`: java.lang.Iterable = CollectionsKt.plus(CollectionsKt.listOf(op1), ops);
      val `destination$iv`: java.util.Collection = new HashSet(8);

      for (Object item$iv : $this$mapTo$iv) {
         val c: CompanionV = `item$iv` as CompanionV;
         `destination$iv`.add((c as PathCompanionV).getPath());
      }

      return copy$default(this, null, null, null, var10004.v(var10005, `destination$iv` as MutableSet<IPath>), 7, null);
   }

   public override fun markOfOp(op: Any, op1: ImmutableElementSet<Any>, vararg ops: ImmutableElementSet<Any>): JOperatorV<IValue> {
      val var10004: MergePath.Companion = MergePath.Companion;
      val var10005: HeapValuesEnv = this.env;
      val `$this$flatMapTo$iv`: java.lang.Iterable = CollectionsKt.plus(CollectionsKt.listOf(op1), ops);
      val `destination$iv`: java.util.Collection = new HashSet(8);

      for (Object element$iv : $this$flatMapTo$iv) {
         val `$this$flatMap$iv`: java.lang.Iterable = (`element$iv` as ImmutableElementSet).getMap().values();
         val `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$flatMap$iv) {
            val `$this$map$iv`: java.lang.Iterable = (`element$iv$iv` as IHeapValues).getValuesCompanion() as java.lang.Iterable;
            val `destination$iv$ivx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

            for (Object item$iv$iv : $this$map$iv) {
               val it: CompanionV = `item$iv$iv` as CompanionV;
               `destination$iv$ivx`.add((it as PathCompanionV).getPath());
            }

            CollectionsKt.addAll(`destination$iv$iv`, `destination$iv$ivx` as java.util.List);
         }

         CollectionsKt.addAll(`destination$iv`, `destination$iv$iv` as java.util.List);
      }

      return copy$default(this, null, null, null, var10004.v(var10005, `destination$iv` as MutableSet<IPath>), 7, null);
   }

   public override fun markOfNegExpr(expr: NegExpr, cop: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (cop as PathCompanionV).getPath(), 7, null);
   }

   public override fun markOfCastTo(toPrimType: PrimType): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfCastTo(toRefType: RefLikeType): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfInstanceOf(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfArrayContentEqualsBoolResult(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfParseString(hint: String, str: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (str as PathCompanionV).getPath(), 7, null);
   }

   public override fun markSummaryReturnValueFailedGetKeyFromKey(src: CompanionV<IValue>, mt: Any, key: Any?): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun dataGetElementFromSequence(sourceSequence: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (sourceSequence as PathCompanionV).getPath(), 7, null);
   }

   public override fun markSummaryArraySize(allocSite: IHeapValues<IValue>): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfGetClass(cop: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (cop as PathCompanionV).getPath(), 7, null);
   }

   public override fun markOfObjectEqualsResult(th1s: CompanionV<IValue>, that: CompanionV<IValue>): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfReturnValueOfMethod(ctx: EvalCall): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun dataSequenceToSeq(sourceSequence: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (sourceSequence as PathCompanionV).getPath(), 7, null);
   }

   public override fun markArraySizeOf(array: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (array as PathCompanionV).getPath(), 7, null);
   }

   public override fun markOfTaint(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public override fun markOfStringLatin1Hash(byteArray: CompanionV<IValue>): JOperatorV<IValue> {
      return copy$default(this, null, null, null, (byteArray as PathCompanionV).getPath(), 7, null);
   }

   public override fun markOfWideningSummary(): JOperatorV<IValue> {
      return this as JOperatorV<IValue>;
   }

   public operator fun component1(): AbstractHeapFactory<IValue> {
      return this.heapFactory;
   }

   public operator fun component2(): HeapValuesEnv {
      return this.env;
   }

   private operator fun component3(): IValue {
      return this.alloc;
   }

   public operator fun component4(): IPath? {
      return this.path;
   }

   public fun copy(
      heapFactory: AbstractHeapFactory<IValue> = this.heapFactory,
      env: HeapValuesEnv = this.env,
      alloc: IValue = this.alloc,
      path: IPath? = this.path
   ): OperatorPathFactory {
      return new OperatorPathFactory(heapFactory, env, alloc, path);
   }

   public override fun toString(): String {
      return "OperatorPathFactory(heapFactory=${this.heapFactory}, env=${this.env}, alloc=${this.alloc}, path=${this.path})";
   }

   public override fun hashCode(): Int {
      return ((this.heapFactory.hashCode() * 31 + this.env.hashCode()) * 31 + this.alloc.hashCode()) * 31
         + (if (this.path == null) 0 else this.path.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is OperatorPathFactory) {
         return false;
      } else {
         val var2: OperatorPathFactory = other as OperatorPathFactory;
         if (!(this.heapFactory == (other as OperatorPathFactory).heapFactory)) {
            return false;
         } else if (!(this.env == var2.env)) {
            return false;
         } else if (!(this.alloc == var2.alloc)) {
            return false;
         } else {
            return this.path == var2.path;
         }
      }
   }
}
