package cn.sast.dataflow.infoflow.provider

import com.feysh.corax.config.api.AttributeName
import com.feysh.corax.config.api.BuiltInField
import com.feysh.corax.config.api.ClassField
import com.feysh.corax.config.api.IClassField
import com.feysh.corax.config.api.SubFields
import java.util.ArrayList
import java.util.LinkedList
import java.util.Queue
import kotlin.jvm.internal.SourceDebugExtension
import mu.KLogger
import soot.SootField

@SourceDebugExtension(["SMAP\nFieldFinder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FieldFinder.kt\ncn/sast/dataflow/infoflow/provider/FieldFinder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,82:1\n1#2:83\n*E\n"])
public class FieldFinder(baseTypes: Set<String>?, acc: List<IClassField>) {
   private final val baseTypes: Set<String>?
   private final val acc: List<IClassField>

   init {
      this.baseTypes = baseTypes;
      this.acc = acc;
   }

   public constructor(baseTypes: String?, acc: List<IClassField>)  {
      var var10000: FieldFinder = this;
      val var10001: java.util.Set;
      if (baseTypes != null) {
         var10001 = SetsKt.setOf(baseTypes);
         var10000 = this;
      } else {
         var10001 = null;
      }

      var10000./* $VF: Unable to resugar constructor */<init>(var10001, acc);
   }

   public fun sootFields(): List<cn.sast.dataflow.infoflow.provider.FieldFinder.AccessPath> {
      return this.find();
   }

   public fun find(): List<cn.sast.dataflow.infoflow.provider.FieldFinder.AccessPath> {
      if (this.acc.isEmpty()) {
         return CollectionsKt.listOf(new FieldFinder.AccessPath(CollectionsKt.emptyList(), false));
      } else {
         val res: java.util.List = new ArrayList();
         val workList: Queue = new LinkedList();
         workList.add(new FieldFinder.Task(CollectionsKt.emptyList(), this.acc));

         while (!workList.isEmpty()) {
            val cur: FieldFinder.Task = workList.poll() as FieldFinder.Task;
            val field: IClassField = CollectionsKt.first(cur.getRight()) as IClassField;
            if (field is ClassField) {
               val var10000: SootField = FieldFinderKt.getSootField(field as ClassField);
               if (var10000 != null) {
                  val var6: java.util.List = CollectionsKt.drop(cur.getRight(), 1);
                  if (var6.isEmpty()) {
                     res.add(new FieldFinder.AccessPath(CollectionsKt.plus(cur.getLeft(), var10000), false));
                  } else {
                     workList.add(new FieldFinder.Task(CollectionsKt.plus(cur.getLeft(), var10000), var6));
                  }
               }
            } else if (field is SubFields) {
               if (cur.getRight().size() != 1) {
                  throw new IllegalArgumentException(("oops: $cur").toString());
               }

               res.add(new FieldFinder.AccessPath(cur.getLeft(), true));
            } else if (field is BuiltInField) {
               res.add(new FieldFinder.AccessPath(cur.getLeft(), false));
            } else if (field is AttributeName) {
               res.add(new FieldFinder.AccessPath(cur.getLeft(), false));
            }
         }

         return res;
      }
   }

   @JvmStatic
   fun `logger$lambda$2`(): Unit {
      return Unit.INSTANCE;
   }

   public data class AccessPath(sootField: List<SootField>, subFields: Boolean) {
      public final val sootField: List<SootField>
      public final val subFields: Boolean

      init {
         this.sootField = sootField;
         this.subFields = subFields;
      }

      public operator fun component1(): List<SootField> {
         return this.sootField;
      }

      public operator fun component2(): Boolean {
         return this.subFields;
      }

      public fun copy(sootField: List<SootField> = this.sootField, subFields: Boolean = this.subFields): cn.sast.dataflow.infoflow.provider.FieldFinder.AccessPath {
         return new FieldFinder.AccessPath(sootField, subFields);
      }

      public override fun toString(): String {
         return "AccessPath(sootField=${this.sootField}, subFields=${this.subFields})";
      }

      public override fun hashCode(): Int {
         return this.sootField.hashCode() * 31 + java.lang.Boolean.hashCode(this.subFields);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is FieldFinder.AccessPath) {
            return false;
         } else {
            val var2: FieldFinder.AccessPath = other as FieldFinder.AccessPath;
            if (!(this.sootField == (other as FieldFinder.AccessPath).sootField)) {
               return false;
            } else {
               return this.subFields == var2.subFields;
            }
         }
      }
   }

   public companion object {
      private final val logger: KLogger
   }

   private data class Task(left: List<SootField>, right: List<IClassField>) {
      public final val left: List<SootField>
      public final val right: List<IClassField>

      init {
         this.left = left;
         this.right = right;
      }

      public operator fun component1(): List<SootField> {
         return this.left;
      }

      public operator fun component2(): List<IClassField> {
         return this.right;
      }

      public fun copy(left: List<SootField> = this.left, right: List<IClassField> = this.right): cn.sast.dataflow.infoflow.provider.FieldFinder.Task {
         return new FieldFinder.Task(left, right);
      }

      public override fun toString(): String {
         return "Task(left=${this.left}, right=${this.right})";
      }

      public override fun hashCode(): Int {
         return this.left.hashCode() * 31 + this.right.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is FieldFinder.Task) {
            return false;
         } else {
            val var2: FieldFinder.Task = other as FieldFinder.Task;
            if (!(this.left == (other as FieldFinder.Task).left)) {
               return false;
            } else {
               return this.right == var2.right;
            }
         }
      }
   }
}
