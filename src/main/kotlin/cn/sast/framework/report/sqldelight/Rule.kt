package cn.sast.framework.report.sqldelight

public data class Rule(name: String,
   short_description_en: String,
   short_description_zh: String,
   severity: String?,
   priority: String?,
   language: String,
   precision: String?,
   recall: String?,
   likelihood: String?,
   impact: String?,
   technique: String?,
   analysis_scope: String?,
   performance: String?,
   configurable: Long?,
   implemented: Long?,
   static_analyzability: String?,
   c_allocated_target: String?,
   category_en: String,
   category_zh: String,
   rule_sort_number: Long?,
   chapter_name_1: String?,
   chapter_name_2: String?,
   chapter_name_3: String?,
   chapter_name_4: String?,
   description_en: String,
   description_zh: String?,
   document_en: String,
   document_zh: String
) {
   public final val name: String
   public final val short_description_en: String
   public final val short_description_zh: String
   public final val severity: String?
   public final val priority: String?
   public final val language: String
   public final val precision: String?
   public final val recall: String?
   public final val likelihood: String?
   public final val impact: String?
   public final val technique: String?
   public final val analysis_scope: String?
   public final val performance: String?
   public final val configurable: Long?
   public final val implemented: Long?
   public final val static_analyzability: String?
   public final val c_allocated_target: String?
   public final val category_en: String
   public final val category_zh: String
   public final val rule_sort_number: Long?
   public final val chapter_name_1: String?
   public final val chapter_name_2: String?
   public final val chapter_name_3: String?
   public final val chapter_name_4: String?
   public final val description_en: String
   public final val description_zh: String?
   public final val document_en: String
   public final val document_zh: String

   init {
      this.name = name;
      this.short_description_en = short_description_en;
      this.short_description_zh = short_description_zh;
      this.severity = severity;
      this.priority = priority;
      this.language = language;
      this.precision = precision;
      this.recall = recall;
      this.likelihood = likelihood;
      this.impact = impact;
      this.technique = technique;
      this.analysis_scope = analysis_scope;
      this.performance = performance;
      this.configurable = configurable;
      this.implemented = implemented;
      this.static_analyzability = static_analyzability;
      this.c_allocated_target = c_allocated_target;
      this.category_en = category_en;
      this.category_zh = category_zh;
      this.rule_sort_number = rule_sort_number;
      this.chapter_name_1 = chapter_name_1;
      this.chapter_name_2 = chapter_name_2;
      this.chapter_name_3 = chapter_name_3;
      this.chapter_name_4 = chapter_name_4;
      this.description_en = description_en;
      this.description_zh = description_zh;
      this.document_en = document_en;
      this.document_zh = document_zh;
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): String {
      return this.short_description_en;
   }

   public operator fun component3(): String {
      return this.short_description_zh;
   }

   public operator fun component4(): String? {
      return this.severity;
   }

   public operator fun component5(): String? {
      return this.priority;
   }

   public operator fun component6(): String {
      return this.language;
   }

   public operator fun component7(): String? {
      return this.precision;
   }

   public operator fun component8(): String? {
      return this.recall;
   }

   public operator fun component9(): String? {
      return this.likelihood;
   }

   public operator fun component10(): String? {
      return this.impact;
   }

   public operator fun component11(): String? {
      return this.technique;
   }

   public operator fun component12(): String? {
      return this.analysis_scope;
   }

   public operator fun component13(): String? {
      return this.performance;
   }

   public operator fun component14(): Long? {
      return this.configurable;
   }

   public operator fun component15(): Long? {
      return this.implemented;
   }

   public operator fun component16(): String? {
      return this.static_analyzability;
   }

   public operator fun component17(): String? {
      return this.c_allocated_target;
   }

   public operator fun component18(): String {
      return this.category_en;
   }

   public operator fun component19(): String {
      return this.category_zh;
   }

   public operator fun component20(): Long? {
      return this.rule_sort_number;
   }

   public operator fun component21(): String? {
      return this.chapter_name_1;
   }

   public operator fun component22(): String? {
      return this.chapter_name_2;
   }

   public operator fun component23(): String? {
      return this.chapter_name_3;
   }

   public operator fun component24(): String? {
      return this.chapter_name_4;
   }

   public operator fun component25(): String {
      return this.description_en;
   }

   public operator fun component26(): String? {
      return this.description_zh;
   }

   public operator fun component27(): String {
      return this.document_en;
   }

   public operator fun component28(): String {
      return this.document_zh;
   }

   public fun copy(
      name: String = this.name,
      short_description_en: String = this.short_description_en,
      short_description_zh: String = this.short_description_zh,
      severity: String? = this.severity,
      priority: String? = this.priority,
      language: String = this.language,
      precision: String? = this.precision,
      recall: String? = this.recall,
      likelihood: String? = this.likelihood,
      impact: String? = this.impact,
      technique: String? = this.technique,
      analysis_scope: String? = this.analysis_scope,
      performance: String? = this.performance,
      configurable: Long? = this.configurable,
      implemented: Long? = this.implemented,
      static_analyzability: String? = this.static_analyzability,
      c_allocated_target: String? = this.c_allocated_target,
      category_en: String = this.category_en,
      category_zh: String = this.category_zh,
      rule_sort_number: Long? = this.rule_sort_number,
      chapter_name_1: String? = this.chapter_name_1,
      chapter_name_2: String? = this.chapter_name_2,
      chapter_name_3: String? = this.chapter_name_3,
      chapter_name_4: String? = this.chapter_name_4,
      description_en: String = this.description_en,
      description_zh: String? = this.description_zh,
      document_en: String = this.document_en,
      document_zh: String = this.document_zh
   ): Rule {
      return new Rule(
         name,
         short_description_en,
         short_description_zh,
         severity,
         priority,
         language,
         precision,
         recall,
         likelihood,
         impact,
         technique,
         analysis_scope,
         performance,
         configurable,
         implemented,
         static_analyzability,
         c_allocated_target,
         category_en,
         category_zh,
         rule_sort_number,
         chapter_name_1,
         chapter_name_2,
         chapter_name_3,
         chapter_name_4,
         description_en,
         description_zh,
         document_en,
         document_zh
      );
   }

   public override fun toString(): String {
      return "Rule(name=${this.name}, short_description_en=${this.short_description_en}, short_description_zh=${this.short_description_zh}, severity=${this.severity}, priority=${this.priority}, language=${this.language}, precision=${this.precision}, recall=${this.recall}, likelihood=${this.likelihood}, impact=${this.impact}, technique=${this.technique}, analysis_scope=${this.analysis_scope}, performance=${this.performance}, configurable=${this.configurable}, implemented=${this.implemented}, static_analyzability=${this.static_analyzability}, c_allocated_target=${this.c_allocated_target}, category_en=${this.category_en}, category_zh=${this.category_zh}, rule_sort_number=${this.rule_sort_number}, chapter_name_1=${this.chapter_name_1}, chapter_name_2=${this.chapter_name_2}, chapter_name_3=${this.chapter_name_3}, chapter_name_4=${this.chapter_name_4}, description_en=${this.description_en}, description_zh=${this.description_zh}, document_en=${this.document_en}, document_zh=${this.document_zh})";
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
                                                                                                                                                                                                                                                this.name
                                                                                                                                                                                                                                                         .hashCode()
                                                                                                                                                                                                                                                      * 31
                                                                                                                                                                                                                                                   + this.short_description_en
                                                                                                                                                                                                                                                      .hashCode()
                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                             * 31
                                                                                                                                                                                                                                          + this.short_description_zh
                                                                                                                                                                                                                                             .hashCode()
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                    * 31
                                                                                                                                                                                                                                 + (
                                                                                                                                                                                                                                    if (this.severity
                                                                                                                                                                                                                                          == null)
                                                                                                                                                                                                                                       0
                                                                                                                                                                                                                                       else
                                                                                                                                                                                                                                       this.severity
                                                                                                                                                                                                                                          .hashCode()
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                           * 31
                                                                                                                                                                                                                        + (
                                                                                                                                                                                                                           if (this.priority
                                                                                                                                                                                                                                 == null)
                                                                                                                                                                                                                              0
                                                                                                                                                                                                                              else
                                                                                                                                                                                                                              this.priority
                                                                                                                                                                                                                                 .hashCode()
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                  )
                                                                                                                                                                                                                  * 31
                                                                                                                                                                                                               + this.language
                                                                                                                                                                                                                  .hashCode()
                                                                                                                                                                                                         )
                                                                                                                                                                                                         * 31
                                                                                                                                                                                                      + (
                                                                                                                                                                                                         if (this.precision
                                                                                                                                                                                                               == null)
                                                                                                                                                                                                            0
                                                                                                                                                                                                            else
                                                                                                                                                                                                            this.precision
                                                                                                                                                                                                               .hashCode()
                                                                                                                                                                                                      )
                                                                                                                                                                                                )
                                                                                                                                                                                                * 31
                                                                                                                                                                                             + (
                                                                                                                                                                                                if (this.recall
                                                                                                                                                                                                      == null)
                                                                                                                                                                                                   0
                                                                                                                                                                                                   else
                                                                                                                                                                                                   this.recall
                                                                                                                                                                                                      .hashCode()
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                       * 31
                                                                                                                                                                                    + (
                                                                                                                                                                                       if (this.likelihood
                                                                                                                                                                                             == null)
                                                                                                                                                                                          0
                                                                                                                                                                                          else
                                                                                                                                                                                          this.likelihood
                                                                                                                                                                                             .hashCode()
                                                                                                                                                                                    )
                                                                                                                                                                              )
                                                                                                                                                                              * 31
                                                                                                                                                                           + (
                                                                                                                                                                              if (this.impact
                                                                                                                                                                                    == null)
                                                                                                                                                                                 0
                                                                                                                                                                                 else
                                                                                                                                                                                 this.impact
                                                                                                                                                                                    .hashCode()
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + (
                                                                                                                                                                     if (this.technique
                                                                                                                                                                           == null)
                                                                                                                                                                        0
                                                                                                                                                                        else
                                                                                                                                                                        this.technique
                                                                                                                                                                           .hashCode()
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + (
                                                                                                                                                            if (this.analysis_scope
                                                                                                                                                                  == null)
                                                                                                                                                               0
                                                                                                                                                               else
                                                                                                                                                               this.analysis_scope
                                                                                                                                                                  .hashCode()
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + (
                                                                                                                                                   if (this.performance
                                                                                                                                                         == null)
                                                                                                                                                      0
                                                                                                                                                      else
                                                                                                                                                      this.performance
                                                                                                                                                         .hashCode()
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + (
                                                                                                                                          if (this.configurable
                                                                                                                                                == null)
                                                                                                                                             0
                                                                                                                                             else
                                                                                                                                             this.configurable
                                                                                                                                                .hashCode()
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + (
                                                                                                                                 if (this.implemented == null)
                                                                                                                                    0
                                                                                                                                    else
                                                                                                                                    this.implemented.hashCode()
                                                                                                                              )
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + (
                                                                                                                        if (this.static_analyzability == null)
                                                                                                                           0
                                                                                                                           else
                                                                                                                           this.static_analyzability.hashCode()
                                                                                                                     )
                                                                                                               )
                                                                                                               * 31
                                                                                                            + (
                                                                                                               if (this.c_allocated_target == null)
                                                                                                                  0
                                                                                                                  else
                                                                                                                  this.c_allocated_target.hashCode()
                                                                                                            )
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.category_en.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + this.category_zh.hashCode()
                                                                                    )
                                                                                    * 31
                                                                                 + (if (this.rule_sort_number == null) 0 else this.rule_sort_number.hashCode())
                                                                           )
                                                                           * 31
                                                                        + (if (this.chapter_name_1 == null) 0 else this.chapter_name_1.hashCode())
                                                                  )
                                                                  * 31
                                                               + (if (this.chapter_name_2 == null) 0 else this.chapter_name_2.hashCode())
                                                         )
                                                         * 31
                                                      + (if (this.chapter_name_3 == null) 0 else this.chapter_name_3.hashCode())
                                                )
                                                * 31
                                             + (if (this.chapter_name_4 == null) 0 else this.chapter_name_4.hashCode())
                                       )
                                       * 31
                                    + this.description_en.hashCode()
                              )
                              * 31
                           + (if (this.description_zh == null) 0 else this.description_zh.hashCode())
                     )
                     * 31
                  + this.document_en.hashCode()
            )
            * 31
         + this.document_zh.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Rule) {
         return false;
      } else {
         val var2: Rule = other as Rule;
         if (!(this.name == (other as Rule).name)) {
            return false;
         } else if (!(this.short_description_en == var2.short_description_en)) {
            return false;
         } else if (!(this.short_description_zh == var2.short_description_zh)) {
            return false;
         } else if (!(this.severity == var2.severity)) {
            return false;
         } else if (!(this.priority == var2.priority)) {
            return false;
         } else if (!(this.language == var2.language)) {
            return false;
         } else if (!(this.precision == var2.precision)) {
            return false;
         } else if (!(this.recall == var2.recall)) {
            return false;
         } else if (!(this.likelihood == var2.likelihood)) {
            return false;
         } else if (!(this.impact == var2.impact)) {
            return false;
         } else if (!(this.technique == var2.technique)) {
            return false;
         } else if (!(this.analysis_scope == var2.analysis_scope)) {
            return false;
         } else if (!(this.performance == var2.performance)) {
            return false;
         } else if (!(this.configurable == var2.configurable)) {
            return false;
         } else if (!(this.implemented == var2.implemented)) {
            return false;
         } else if (!(this.static_analyzability == var2.static_analyzability)) {
            return false;
         } else if (!(this.c_allocated_target == var2.c_allocated_target)) {
            return false;
         } else if (!(this.category_en == var2.category_en)) {
            return false;
         } else if (!(this.category_zh == var2.category_zh)) {
            return false;
         } else if (!(this.rule_sort_number == var2.rule_sort_number)) {
            return false;
         } else if (!(this.chapter_name_1 == var2.chapter_name_1)) {
            return false;
         } else if (!(this.chapter_name_2 == var2.chapter_name_2)) {
            return false;
         } else if (!(this.chapter_name_3 == var2.chapter_name_3)) {
            return false;
         } else if (!(this.chapter_name_4 == var2.chapter_name_4)) {
            return false;
         } else if (!(this.description_en == var2.description_en)) {
            return false;
         } else if (!(this.description_zh == var2.description_zh)) {
            return false;
         } else if (!(this.document_en == var2.document_en)) {
            return false;
         } else {
            return this.document_zh == var2.document_zh;
         }
      }
   }
}
