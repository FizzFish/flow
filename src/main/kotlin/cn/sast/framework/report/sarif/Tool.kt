package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Tool(public val driver: ToolComponent) {
    public operator fun component1(): ToolComponent {
        return this.driver
    }

    public fun copy(driver: ToolComponent = this.driver): Tool {
        return Tool(driver)
    }

    public override fun toString(): String {
        return "Tool(driver=${this.driver})"
    }

    public override fun hashCode(): Int {
        return this.driver.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Tool) {
            return false
        } else {
            return this.driver == other.driver
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Tool> {
            return Tool.serializer()
        }
    }
}