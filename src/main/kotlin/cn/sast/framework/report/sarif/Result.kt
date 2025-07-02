package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class Result(ruleId: String,
   ruleIndex: Int,
   message: IndexedMessage = new IndexedMessage(null, 1, null),
   locations: List<Location>,
   codeFlows: List<CodeFlow> = CollectionsKt.emptyList()
) {
   public final val ruleId: String
   public final val ruleIndex: Int

   @EncodeDefault(
      mode = Mode.ALWAYS
   )
   public final val message: IndexedMessage

   public final val locations: List<Location>
   public final val codeFlows: List<CodeFlow>

   init {
      this.ruleId = ruleId;
      this.ruleIndex = ruleIndex;
      this.message = message;
      this.locations = locations;
      this.codeFlows = codeFlows;
   }

   public operator fun component1(): String {
      return this.ruleId;
   }

   public operator fun component2(): Int {
      return this.ruleIndex;
   }

   public operator fun component3(): IndexedMessage {
      return this.message;
   }

   public operator fun component4(): List<Location> {
      return this.locations;
   }

   public operator fun component5(): List<CodeFlow> {
      return this.codeFlows;
   }

   public fun copy(
      ruleId: String = this.ruleId,
      ruleIndex: Int = this.ruleIndex,
      message: IndexedMessage = this.message,
      locations: List<Location> = this.locations,
      codeFlows: List<CodeFlow> = this.codeFlows
   ): Result {
      return new Result(ruleId, ruleIndex, message, locations, codeFlows);
   }

   public override fun toString(): String {
      return "Result(ruleId=${this.ruleId}, ruleIndex=${this.ruleIndex}, message=${this.message}, locations=${this.locations}, codeFlows=${this.codeFlows})";
   }

   public override fun hashCode(): Int {
      return (((this.ruleId.hashCode() * 31 + Integer.hashCode(this.ruleIndex)) * 31 + this.message.hashCode()) * 31 + this.locations.hashCode()) * 31
         + this.codeFlows.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Result) {
         return false;
      } else {
         val var2: Result = other as Result;
         if (!(this.ruleId == (other as Result).ruleId)) {
            return false;
         } else if (this.ruleIndex != var2.ruleIndex) {
            return false;
         } else if (!(this.message == var2.message)) {
            return false;
         } else if (!(this.locations == var2.locations)) {
            return false;
         } else {
            return this.codeFlows == var2.codeFlows;
         }
      }
   }

   public companion object {
      public fun serializer(): KSerializer<Result> {
         return Result.$serializer.INSTANCE as KSerializer<Result>;
      }
   }
}
