package cn.sast.framework.report.sqldelight

public data class RuleSetInfo(name: String,
   language: String,
   description: String?,
   prefix: String?,
   id_pattern: String?,
   section_level: Long?,
   version: String,
   revision: String
) {
   public final val name: String
   public final val language: String
   public final val description: String?
   public final val prefix: String?
   public final val id_pattern: String?
   public final val section_level: Long?
   public final val version: String
   public final val revision: String

   init {
      this.name = name;
      this.language = language;
      this.description = description;
      this.prefix = prefix;
      this.id_pattern = id_pattern;
      this.section_level = section_level;
      this.version = version;
      this.revision = revision;
   }

   public operator fun component1(): String {
      return this.name;
   }

   public operator fun component2(): String {
      return this.language;
   }

   public operator fun component3(): String? {
      return this.description;
   }

   public operator fun component4(): String? {
      return this.prefix;
   }

   public operator fun component5(): String? {
      return this.id_pattern;
   }

   public operator fun component6(): Long? {
      return this.section_level;
   }

   public operator fun component7(): String {
      return this.version;
   }

   public operator fun component8(): String {
      return this.revision;
   }

   public fun copy(
      name: String = this.name,
      language: String = this.language,
      description: String? = this.description,
      prefix: String? = this.prefix,
      id_pattern: String? = this.id_pattern,
      section_level: Long? = this.section_level,
      version: String = this.version,
      revision: String = this.revision
   ): RuleSetInfo {
      return new RuleSetInfo(name, language, description, prefix, id_pattern, section_level, version, revision);
   }

   public override fun toString(): String {
      return "RuleSetInfo(name=${this.name}, language=${this.language}, description=${this.description}, prefix=${this.prefix}, id_pattern=${this.id_pattern}, section_level=${this.section_level}, version=${this.version}, revision=${this.revision})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (this.name.hashCode() * 31 + this.language.hashCode()) * 31
                                                      + (if (this.description == null) 0 else this.description.hashCode())
                                                )
                                                * 31
                                             + (if (this.prefix == null) 0 else this.prefix.hashCode())
                                       )
                                       * 31
                                    + (if (this.id_pattern == null) 0 else this.id_pattern.hashCode())
                              )
                              * 31
                           + (if (this.section_level == null) 0 else this.section_level.hashCode())
                     )
                     * 31
                  + this.version.hashCode()
            )
            * 31
         + this.revision.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is RuleSetInfo) {
         return false;
      } else {
         val var2: RuleSetInfo = other as RuleSetInfo;
         if (!(this.name == (other as RuleSetInfo).name)) {
            return false;
         } else if (!(this.language == var2.language)) {
            return false;
         } else if (!(this.description == var2.description)) {
            return false;
         } else if (!(this.prefix == var2.prefix)) {
            return false;
         } else if (!(this.id_pattern == var2.id_pattern)) {
            return false;
         } else if (!(this.section_level == var2.section_level)) {
            return false;
         } else if (!(this.version == var2.version)) {
            return false;
         } else {
            return this.revision == var2.revision;
         }
      }
   }
}
