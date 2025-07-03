package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class Location(
    public val physicalLocation: PhysicalLocation
) {
    public operator fun component1(): PhysicalLocation {
        return this.physicalLocation
    }

    public fun copy(physicalLocation: PhysicalLocation = this.physicalLocation): Location {
        return Location(physicalLocation)
    }

    public override fun toString(): String {
        return "Location(physicalLocation=${this.physicalLocation})"
    }

    public override fun hashCode(): Int {
        return this.physicalLocation.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is Location) {
            return false
        } else {
            return this.physicalLocation == other.physicalLocation
        }
    }

    public companion object {
        public fun serializer(): KSerializer<Location> {
            return Location.$serializer.INSTANCE as KSerializer<Location>
        }
    }
}