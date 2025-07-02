package cn.sast.api.config

import com.feysh.corax.config.api.CheckType
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nCheckerInfo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfoGenResult\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1611#2,9:50\n1863#2:59\n1864#2:61\n1620#2:62\n1#3:60\n*S KotlinDebug\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfoGenResult\n*L\n48#1:50,9\n48#1:59\n48#1:61\n48#1:62\n48#1:60\n*E\n"])
public data class CheckerInfoGenResult(checkerInfoList: LinkedHashSet<CheckerInfo>,
   existsCheckTypes: LinkedHashSet<CheckType>,
   existsCheckerIds: LinkedHashSet<String>,
   checkerIdInCsv: LinkedHashSet<String>
) {
   public final val checkerInfoList: LinkedHashSet<CheckerInfo>
   public final val existsCheckTypes: LinkedHashSet<CheckType>
   public final val existsCheckerIds: LinkedHashSet<String>
   public final val checkerIdInCsv: LinkedHashSet<String>

   public final val chapters: List<ChapterFlat>
      public final get() {
         val `$this$mapNotNull$iv`: java.lang.Iterable = this.checkerInfoList;
         val `destination$iv$iv`: java.util.Collection = new ArrayList();

         for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            val var10000: ChapterFlat = (`element$iv$iv$iv` as CheckerInfo).getChapterFlat();
            if (var10000 != null) {
               `destination$iv$iv`.add(var10000);
            }
         }

         return `destination$iv$iv` as MutableList<ChapterFlat>;
      }


   init {
      this.checkerInfoList = checkerInfoList;
      this.existsCheckTypes = existsCheckTypes;
      this.existsCheckerIds = existsCheckerIds;
      this.checkerIdInCsv = checkerIdInCsv;
   }

   public operator fun component1(): LinkedHashSet<CheckerInfo> {
      return this.checkerInfoList;
   }

   public operator fun component2(): LinkedHashSet<CheckType> {
      return this.existsCheckTypes;
   }

   public operator fun component3(): LinkedHashSet<String> {
      return this.existsCheckerIds;
   }

   public operator fun component4(): LinkedHashSet<String> {
      return this.checkerIdInCsv;
   }

   public fun copy(
      checkerInfoList: LinkedHashSet<CheckerInfo> = this.checkerInfoList,
      existsCheckTypes: LinkedHashSet<CheckType> = this.existsCheckTypes,
      existsCheckerIds: LinkedHashSet<String> = this.existsCheckerIds,
      checkerIdInCsv: LinkedHashSet<String> = this.checkerIdInCsv
   ): CheckerInfoGenResult {
      return new CheckerInfoGenResult(checkerInfoList, existsCheckTypes, existsCheckerIds, checkerIdInCsv);
   }

   public override fun toString(): String {
      return "CheckerInfoGenResult(checkerInfoList=${this.checkerInfoList}, existsCheckTypes=${this.existsCheckTypes}, existsCheckerIds=${this.existsCheckerIds}, checkerIdInCsv=${this.checkerIdInCsv})";
   }

   public override fun hashCode(): Int {
      return ((this.checkerInfoList.hashCode() * 31 + this.existsCheckTypes.hashCode()) * 31 + this.existsCheckerIds.hashCode()) * 31
         + this.checkerIdInCsv.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CheckerInfoGenResult) {
         return false;
      } else {
         val var2: CheckerInfoGenResult = other as CheckerInfoGenResult;
         if (!(this.checkerInfoList == (other as CheckerInfoGenResult).checkerInfoList)) {
            return false;
         } else if (!(this.existsCheckTypes == var2.existsCheckTypes)) {
            return false;
         } else if (!(this.existsCheckerIds == var2.existsCheckerIds)) {
            return false;
         } else {
            return this.checkerIdInCsv == var2.checkerIdInCsv;
         }
      }
   }
}
