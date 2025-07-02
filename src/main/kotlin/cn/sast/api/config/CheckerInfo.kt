package cn.sast.api.config

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
@SourceDebugExtension(["SMAP\nCheckerInfo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CheckerInfo.kt\ncn/sast/api/config/CheckerInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,49:1\n1#2:50\n*E\n"])
public data class CheckerInfo(type: String,
   format_version: String,
   analyzer: String,
   language: String,
   checker_id: String,
   severity: String,
   category: Map<String, String>,
   name: Map<String, String>,
   abstract: Map<String, String>,
   description: MutableMap<String, String>,
   tags: List<Tag>,
   impact: String? = null,
   likelihood: String? = null,
   precision: String? = null,
   reCall: String? = null,
   impl: String? = null,
   implemented: Boolean? = null
) {
   public final val type: String
   public final val format_version: String
   public final val analyzer: String
   public final val language: String
   public final val checker_id: String
   public final val severity: String
   public final val category: Map<String, String>
   public final val name: Map<String, String>
   public final val abstract: Map<String, String>
   public final val description: MutableMap<String, String>
   public final val tags: List<Tag>

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val impact: String?

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val likelihood: String?

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val precision: String?

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val reCall: String?

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val impl: String?

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val implemented: Boolean?

   public final val chapterFlat: ChapterFlat?
      public final get() {
         val var10000: java.lang.String = this.category.get("zh-CN");
         return if (var10000 != null) new ChapterFlat(var10000, this.severity, this.checker_id) else null;
      }


   init {
      this.type = type;
      this.format_version = format_version;
      this.analyzer = analyzer;
      this.language = language;
      this.checker_id = checker_id;
      this.severity = severity;
      this.category = category;
      this.name = name;
      this.abstract = var9;
      this.description = description;
      this.tags = tags;
      this.impact = impact;
      this.likelihood = likelihood;
      this.precision = precision;
      this.reCall = reCall;
      this.impl = impl;
      this.implemented = implemented;
   }

   public operator fun component1(): String {
      return this.type;
   }

   public operator fun component2(): String {
      return this.format_version;
   }

   public operator fun component3(): String {
      return this.analyzer;
   }

   public operator fun component4(): String {
      return this.language;
   }

   public operator fun component5(): String {
      return this.checker_id;
   }

   public operator fun component6(): String {
      return this.severity;
   }

   public operator fun component7(): Map<String, String> {
      return this.category;
   }

   public operator fun component8(): Map<String, String> {
      return this.name;
   }

   public operator fun component9(): Map<String, String> {
      return this.abstract;
   }

   public operator fun component10(): MutableMap<String, String> {
      return this.description;
   }

   public operator fun component11(): List<Tag> {
      return this.tags;
   }

   public operator fun component12(): String? {
      return this.impact;
   }

   public operator fun component13(): String? {
      return this.likelihood;
   }

   public operator fun component14(): String? {
      return this.precision;
   }

   public operator fun component15(): String? {
      return this.reCall;
   }

   public operator fun component16(): String? {
      return this.impl;
   }

   public operator fun component17(): Boolean? {
      return this.implemented;
   }

   public fun copy(
      type: String = this.type,
      format_version: String = this.format_version,
      analyzer: String = this.analyzer,
      language: String = this.language,
      checker_id: String = this.checker_id,
      severity: String = this.severity,
      category: Map<String, String> = this.category,
      name: Map<String, String> = this.name,
      abstract: Map<String, String> = this.abstract,
      description: MutableMap<String, String> = this.description,
      tags: List<Tag> = this.tags,
      impact: String? = this.impact,
      likelihood: String? = this.likelihood,
      precision: String? = this.precision,
      reCall: String? = this.reCall,
      impl: String? = this.impl,
      implemented: Boolean? = this.implemented
   ): CheckerInfo {
      return new CheckerInfo(
         type,
         format_version,
         analyzer,
         language,
         checker_id,
         severity,
         category,
         name,
         var9,
         description,
         tags,
         impact,
         likelihood,
         precision,
         reCall,
         impl,
         implemented
      );
   }

   public override fun toString(): String {
      return "CheckerInfo(type=${this.type}, format_version=${this.format_version}, analyzer=${this.analyzer}, language=${this.language}, checker_id=${this.checker_id}, severity=${this.severity}, category=${this.category}, name=${this.name}, abstract=${this.abstract}, description=${this.description}, tags=${this.tags}, impact=${this.impact}, likelihood=${this.likelihood}, precision=${this.precision}, reCall=${this.reCall}, impl=${this.impl}, implemented=${this.implemented})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             this.type
                                                                                                                                                      .hashCode()
                                                                                                                                                   * 31
                                                                                                                                                + this.format_version
                                                                                                                                                   .hashCode()
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + this.analyzer
                                                                                                                                          .hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + this.language.hashCode()
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.checker_id.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.severity.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.category.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.name.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + this.abstract.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.description.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.tags.hashCode()
                                                         )
                                                         * 31
                                                      + (if (this.impact == null) 0 else this.impact.hashCode())
                                                )
                                                * 31
                                             + (if (this.likelihood == null) 0 else this.likelihood.hashCode())
                                       )
                                       * 31
                                    + (if (this.precision == null) 0 else this.precision.hashCode())
                              )
                              * 31
                           + (if (this.reCall == null) 0 else this.reCall.hashCode())
                     )
                     * 31
                  + (if (this.impl == null) 0 else this.impl.hashCode())
            )
            * 31
         + (if (this.implemented == null) 0 else this.implemented.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is CheckerInfo) {
         return false;
      } else {
         val var2: CheckerInfo = other as CheckerInfo;
         if (!(this.type == (other as CheckerInfo).type)) {
            return false;
         } else if (!(this.format_version == var2.format_version)) {
            return false;
         } else if (!(this.analyzer == var2.analyzer)) {
            return false;
         } else if (!(this.language == var2.language)) {
            return false;
         } else if (!(this.checker_id == var2.checker_id)) {
            return false;
         } else if (!(this.severity == var2.severity)) {
            return false;
         } else if (!(this.category == var2.category)) {
            return false;
         } else if (!(this.name == var2.name)) {
            return false;
         } else if (!(this.abstract == var2.abstract)) {
            return false;
         } else if (!(this.description == var2.description)) {
            return false;
         } else if (!(this.tags == var2.tags)) {
            return false;
         } else if (!(this.impact == var2.impact)) {
            return false;
         } else if (!(this.likelihood == var2.likelihood)) {
            return false;
         } else if (!(this.precision == var2.precision)) {
            return false;
         } else if (!(this.reCall == var2.reCall)) {
            return false;
         } else if (!(this.impl == var2.impl)) {
            return false;
         } else {
            return this.implemented == var2.implemented;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<CheckerInfo> {
         return CheckerInfo.$serializer.INSTANCE as KSerializer<CheckerInfo>;
      }
   }
}
