package cn.sast.framework.report.sarif

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.EncodeDefault.Mode

@Serializable
public data class Result(
    public val ruleId: String,
    public val ruleIndex: Int,
    @EncodeDefault(mode = Mode.ALWAYS)
    public val message: IndexedMessage = IndexedMessage(null, 1, null),
    public val locations: List<Location>,
    public val codeFlows: List<CodeFlow> = emptyList()
) {
    public operator fun component1(): String = this.ruleId

    public operator fun component2(): Int = this.ruleIndex

    public operator fun component3(): IndexedMessage = this.message

    public operator fun component4(): List<Location> = this.locations

    public operator fun component5(): List<CodeFlow> = this.codeFlows

    public fun copy(
        ruleId: String = this.ruleId,
        ruleIndex: Int = this.ruleIndex,
        message: IndexedMessage = this.message,
        locations: List<Location> = this.locations,
        codeFlows: List<CodeFlow> = this.codeFlows
    ): Result = Result(ruleId, ruleIndex, message, locations, codeFlows)

    public override fun toString(): String =
        "Result(ruleId=${this.ruleId}, ruleIndex=${this.ruleIndex}, message=${this.message}, locations=${this.locations}, codeFlows=${this.codeFlows})"

    public override fun hashCode(): Int =
        (((this.ruleId.hashCode() * 31 + Integer.hashCode(this.ruleIndex)) * 31 + this.message.hashCode()) * 31 + this.locations.hashCode()) * 31 + this.codeFlows.hashCode()

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false
        
        return this.ruleId == other.ruleId &&
            this.ruleIndex == other.ruleIndex &&
            this.message == other.message &&
            this.locations == other.locations &&
            this.codeFlows == other.codeFlows
    }

    public companion object {
        public fun serializer(): KSerializer<Result> = Result.serializer()
    }
}