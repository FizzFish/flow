package cn.sast.framework.incremental

import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.api.incremental.ModifyInfoFactory
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph
import cn.sast.api.util.OthersKt
import cn.sast.api.util.SootUtilsKt
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.feysh.corax.config.api.XDecl
import com.feysh.corax.config.api.rules.ProcessRule
import com.feysh.corax.config.api.rules.ProcessRule.ScanAction
import java.io.File
import java.nio.file.Path
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import org.eclipse.jgit.diff.DiffEntry
import soot.RefType
import soot.SootClass
import soot.SootField
import soot.SootMethod
import soot.Unit
import soot.Value
import soot.jimple.InstanceInvokeExpr
import soot.jimple.Stmt
import soot.jimple.toolkits.callgraph.CallGraph
import soot.jimple.toolkits.callgraph.Edge
import soot.util.Chain

@SourceDebugExtension(["SMAP\nIncrementalAnalyzeImplByChangeFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/ModifyInfoFactoryImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,395:1\n1628#2,3:396\n1628#2,3:399\n*S KotlinDebug\n*F\n+ 1 IncrementalAnalyzeImplByChangeFiles.kt\ncn/sast/framework/incremental/ModifyInfoFactoryImpl\n*L\n86#1:396,3\n87#1:399,3\n*E\n"])
public class ModifyInfoFactoryImpl : ModifyInfoFactory {
   public override fun toDecl(target: Any): XDecl {
      val var10000: XDecl;
      if (target is SootClass) {
         var10000 = new ModifyInfoFactoryImpl.ClassDecl(target as SootClass);
      } else if (target is SootMethod) {
         var10000 = new ModifyInfoFactoryImpl.MethodDecl(target as SootMethod);
      } else if (target is SootField) {
         var10000 = new ModifyInfoFactoryImpl.FieldDecl(target as SootField);
      } else if (target is Path) {
         var10000 = new ModifyInfoFactoryImpl.FileDecl(target as Path);
      } else if (target is File) {
         val var10002: Path = (target as File).toPath();
         var10000 = new ModifyInfoFactoryImpl.FileDecl(var10002);
      } else {
         if (target !is IResource) {
            throw new IllegalStateException("not support yet".toString());
         }

         var10000 = new ModifyInfoFactoryImpl.FileDecl(target as IResource);
      }

      return var10000;
   }

   public override fun getPatchedDeclsByDiff(target: Any, diff: DiffEntry): Collection<XDecl> {
      return this.getSubDecls(this.toDecl(target));
   }

   public override fun getSubDecls(decl: XDecl): Collection<XDecl> {
      var var10000: java.util.Collection;
      if (decl is ModifyInfoFactoryImpl.FileDecl) {
         var10000 = CollectionsKt.listOf(decl);
      } else if (decl is ModifyInfoFactoryImpl.ClassDecl) {
         val t: SootClass = (decl as ModifyInfoFactoryImpl.ClassDecl).getTarget();
         val var10002: java.util.List = t.getMethods();
         val var23: Int = var10002.size();
         val var10003: Chain = t.getFields();
         val mutableSet: ArrayList = new ArrayList(var23 + (var10003 as java.util.Collection).size() + 1);
         mutableSet.add(decl);

         val `$this$mapTo$iv`: java.lang.Iterable;
         for (Object item$iv : $this$mapTo$iv) {
            var10000 = mutableSet;
            val it: SootMethod = `item$iv` as SootMethod;
            var10000.add(this.toDecl(it));
         }
         for (Object item$iv : $this$mapTo$iv) {
            var10000 = mutableSet;
            val var16: SootField = var15 as SootField;
            var10000.add(this.toDecl(var16));
         }

         var10000 = mutableSet;
      } else if (decl is ModifyInfoFactoryImpl.MethodDecl) {
         var10000 = CollectionsKt.listOf(decl);
      } else {
         if (decl !is ModifyInfoFactoryImpl.FieldDecl) {
            throw new IllegalStateException(("invalid type of $decl").toString());
         }

         var10000 = CollectionsKt.listOf(decl);
      }

      return var10000;
   }

   public override fun createInterProceduralAnalysisDependsGraph(): InterProceduralAnalysisDependsGraph {
      val d: DependsGraph = new DependsGraph(this);
      return new IncrementalAnalyzeByChangeFiles.InterProceduralAnalysisDependsGraph(d) {
         {
            this.$$delegate_0 = `$d`;
         }

         @Override
         public void update(CallGraph cg) {
            val var10000: java.util.Iterator = cg.iterator();
            val var2: java.util.Iterator = var10000;

            while (var2.hasNext()) {
               var src: SootMethod;
               var tgt: SootMethod;
               label38: {
                  val edge: Edge = var2.next() as Edge;
                  src = edge.src();
                  tgt = edge.tgt();
                  val callSite: Unit = edge.srcUnit();
                  val var15: Stmt = callSite as? Stmt;
                  if ((callSite as? Stmt) != null) {
                     val var16: Stmt = if (var15.containsInvokeExpr()) var15 else null;
                     if (var16 != null) {
                        var17 = var16.getInvokeExpr();
                        break label38;
                     }
                  }

                  var17 = null;
               }

               label44: {
                  val var18: InstanceInvokeExpr = var17 as? InstanceInvokeExpr;
                  if ((var17 as? InstanceInvokeExpr) != null) {
                     val var19: Value = var18.getBase();
                     if (var19 != null) {
                        var20 = var19.getType();
                        break label44;
                     }
                  }

                  var20 = null;
               }

               if (!(var20 == RefType.v("java.lang.Object"))) {
                  val subSignature: java.lang.String = tgt.getSubSignature();
                  val var21: SootClass = tgt.getDeclaringClass();

                  for (SootMethod find : SootUtilsKt.findMethodOrNull(var21, subSignature)) {
                     val var10001: ModifyInfoFactory = this.getFactory();
                     this.dependsOn(var10001.toDecl(src), this.getFactory().toDecl(var14));
                  }
               }
            }
         }

         @Override
         public void visitChangedDecl(java.lang.String diffPath, java.util.Collection<? extends XDecl> changed) {
            this.$$delegate_0.visitChangedDecl(diffPath, changed);
         }

         @Override
         public ProcessRule.ScanAction shouldReAnalyzeDecl(XDecl target) {
            return this.$$delegate_0.shouldReAnalyzeDecl(target);
         }

         @Override
         public ProcessRule.ScanAction shouldReAnalyzeTarget(Object target) {
            return this.$$delegate_0.shouldReAnalyzeTarget(target);
         }

         @Override
         public Sequence<XDecl> targetRelate(XDecl target) {
            return this.$$delegate_0.targetRelate(target);
         }

         @Override
         public Sequence<XDecl> targetsRelate(java.util.Collection<? extends XDecl> targets) {
            return this.$$delegate_0.targetsRelate(targets);
         }

         @Override
         public ModifyInfoFactory getFactory() {
            return this.$$delegate_0.getFactory();
         }

         @Override
         public XDecl toDecl(Object target) {
            return this.$$delegate_0.toDecl(target);
         }

         @Override
         public void dependsOn(XDecl $this$dependsOn, XDecl dep) {
            this.$$delegate_0.dependsOn(`$this$dependsOn`, dep);
         }

         @Override
         public void dependsOn(java.util.Collection<? extends XDecl> $this$dependsOn, java.util.Collection<? extends XDecl> deps) {
            this.$$delegate_0.dependsOn(`$this$dependsOn`, deps);
         }
      };
   }

   public override fun getScanAction(target: XDecl): ScanAction {
      return if (target is ModifyInfoFactoryImpl.MethodDecl && OthersKt.isDummy((target as ModifyInfoFactoryImpl.MethodDecl).getTarget()))
         ProcessRule.ScanAction.Skip
         else
         ProcessRule.ScanAction.Keep;
   }

   public override fun createSimpleDeclAnalysisDependsGraph(): SimpleDeclAnalysisDependsGraph {
      val d: DependsGraph = new DependsGraph(this);
      return new IncrementalAnalyzeByChangeFiles.SimpleDeclAnalysisDependsGraph(d) {
         {
            this.$$delegate_0 = `$d`;
         }

         @Override
         public void visitChangedDecl(java.lang.String diffPath, java.util.Collection<? extends XDecl> changed) {
            this.$$delegate_0.visitChangedDecl(diffPath, changed);
         }

         @Override
         public ProcessRule.ScanAction shouldReAnalyzeDecl(XDecl target) {
            return this.$$delegate_0.shouldReAnalyzeDecl(target);
         }

         @Override
         public ProcessRule.ScanAction shouldReAnalyzeTarget(Object target) {
            return this.$$delegate_0.shouldReAnalyzeTarget(target);
         }

         @Override
         public Sequence<XDecl> targetRelate(XDecl target) {
            return this.$$delegate_0.targetRelate(target);
         }

         @Override
         public Sequence<XDecl> targetsRelate(java.util.Collection<? extends XDecl> targets) {
            return this.$$delegate_0.targetsRelate(targets);
         }

         @Override
         public ModifyInfoFactory getFactory() {
            return this.$$delegate_0.getFactory();
         }

         @Override
         public XDecl toDecl(Object target) {
            return this.$$delegate_0.toDecl(target);
         }

         @Override
         public void dependsOn(XDecl $this$dependsOn, XDecl dep) {
            this.$$delegate_0.dependsOn(`$this$dependsOn`, dep);
         }

         @Override
         public void dependsOn(java.util.Collection<? extends XDecl> $this$dependsOn, java.util.Collection<? extends XDecl> deps) {
            this.$$delegate_0.dependsOn(`$this$dependsOn`, deps);
         }
      };
   }

   public data class ClassDecl(target: SootClass) : XDecl {
      public final val target: SootClass

      init {
         this.target = target;
      }

      public operator fun component1(): SootClass {
         return this.target;
      }

      public fun copy(target: SootClass = this.target): cn.sast.framework.incremental.ModifyInfoFactoryImpl.ClassDecl {
         return new ModifyInfoFactoryImpl.ClassDecl(target);
      }

      public override fun toString(): String {
         return "ClassDecl(target=${this.target})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ModifyInfoFactoryImpl.ClassDecl) {
            return false;
         } else {
            return this.target == (other as ModifyInfoFactoryImpl.ClassDecl).target;
         }
      }
   }

   public data class FieldDecl(target: SootField) : XDecl {
      public final val target: SootField

      init {
         this.target = target;
      }

      public operator fun component1(): SootField {
         return this.target;
      }

      public fun copy(target: SootField = this.target): cn.sast.framework.incremental.ModifyInfoFactoryImpl.FieldDecl {
         return new ModifyInfoFactoryImpl.FieldDecl(target);
      }

      public override fun toString(): String {
         return "FieldDecl(target=${this.target})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ModifyInfoFactoryImpl.FieldDecl) {
            return false;
         } else {
            return this.target == (other as ModifyInfoFactoryImpl.FieldDecl).target;
         }
      }
   }

   public data class FileDecl(target: String) : XDecl {
      public final val target: String

      init {
         this.target = target;
      }

      public constructor(path: IResource) : this(path.getAbsolute().getNormalize().toString())
      public constructor(path: Path) : this(Resource.INSTANCE.of(path))
      public operator fun component1(): String {
         return this.target;
      }

      public fun copy(target: String = this.target): cn.sast.framework.incremental.ModifyInfoFactoryImpl.FileDecl {
         return new ModifyInfoFactoryImpl.FileDecl(target);
      }

      public override fun toString(): String {
         return "FileDecl(target=${this.target})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ModifyInfoFactoryImpl.FileDecl) {
            return false;
         } else {
            return this.target == (other as ModifyInfoFactoryImpl.FileDecl).target;
         }
      }
   }

   public data class MethodDecl(target: SootMethod) : XDecl {
      public final val target: SootMethod

      init {
         this.target = target;
      }

      public operator fun component1(): SootMethod {
         return this.target;
      }

      public fun copy(target: SootMethod = this.target): cn.sast.framework.incremental.ModifyInfoFactoryImpl.MethodDecl {
         return new ModifyInfoFactoryImpl.MethodDecl(target);
      }

      public override fun toString(): String {
         return "MethodDecl(target=${this.target})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is ModifyInfoFactoryImpl.MethodDecl) {
            return false;
         } else {
            return this.target == (other as ModifyInfoFactoryImpl.MethodDecl).target;
         }
      }
   }
}
