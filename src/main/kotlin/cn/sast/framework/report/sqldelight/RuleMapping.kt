package cn.sast.framework.report.sqldelight

public data class RuleMapping(rule_name: String, standard_name: String?, standard_rule: String?) {
   public final val rule_name: String
   public final val standard_name: String?
   public final val standard_rule: String?

   init {
      this.rule_name = rule_name;
      this.standard_name = standard_name;
      this.standard_rule = standard_rule;
   }

   public operator fun component1(): String {
      return this.rule_name;
   }

   public operator fun component2(): String? {
      return this.standard_name;
   }

   public operator fun component3(): String? {
      return this.standard_rule;
   }

   public fun copy(rule_name: String = this.rule_name, standard_name: String? = this.standard_name, standard_rule: String? = this.standard_rule): RuleMapping {
      return new RuleMapping(rule_name, standard_name, standard_rule);
   }

   public override fun toString(): String {
      return "RuleMapping(rule_name=${this.rule_name}, standard_name=${this.standard_name}, standard_rule=${this.standard_rule})";
   }

   public override fun hashCode(): Int {
      return (this.rule_name.hashCode() * 31 + (if (this.standard_name == null) 0 else this.standard_name.hashCode())) * 31
         + (if (this.standard_rule == null) 0 else this.standard_rule.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is RuleMapping) {
         return false;
      } else {
         val var2: RuleMapping = other as RuleMapping;
         if (!(this.rule_name == (other as RuleMapping).rule_name)) {
            return false;
         } else if (!(this.standard_name == var2.standard_name)) {
            return false;
         } else {
            return this.standard_rule == var2.standard_rule;
         }
      }
   }
}
