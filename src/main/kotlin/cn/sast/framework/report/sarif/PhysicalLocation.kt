package cn.sast.framework.report.sarif

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public data class PhysicalLocation(
    public val artifactLocation: ArtifactLocation,
    public val region: Region
) {
    public operator fun component1(): ArtifactLocation {
        return this.artifactLocation
    }

    public operator fun component2(): Region {
        return this.region
    }

    public fun copy(
        artifactLocation: ArtifactLocation = this.artifactLocation,
        region: Region = this.region
    ): PhysicalLocation {
        return PhysicalLocation(artifactLocation, region)
    }

    public override fun toString(): String {
        return "PhysicalLocation(artifactLocation=${this.artifactLocation}, region=${this.region})"
    }

    public override fun hashCode(): Int {
        return this.artifactLocation.hashCode() * 31 + this.region.hashCode()
    }

    public override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is PhysicalLocation) {
            return false
        } else {
            val var2: PhysicalLocation = other
            if (!(this.artifactLocation == other.artifactLocation)) {
                return false
            } else {
                return this.region == var2.region
            }
        }
    }

    public companion object {
        public fun serializer(): KSerializer<PhysicalLocation> {
            return PhysicalLocation.serializer()
        }
    }
}