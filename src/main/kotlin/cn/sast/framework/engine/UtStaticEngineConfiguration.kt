package cn.sast.framework.engine

import mu.KLogger
import org.utbot.framework.codegen.domain.ForceStaticMocking
import org.utbot.framework.codegen.domain.StaticsMocking
import org.utbot.framework.plugin.api.CodegenLanguage
import org.utbot.framework.plugin.api.MockStrategyApi
import org.utbot.framework.plugin.api.TreatOverflowAsError

public data class UtStaticEngineConfiguration(
    var codegenLanguage: CodegenLanguage = CodegenLanguage.Companion.getDefaultItem(),
    var mockStrategy: MockStrategyApi = MockStrategyApi.NO_MOCKS,
    var testFramework: String = "junit4",
    var staticsMocking: StaticsMocking = StaticsMocking.Companion.getDefaultItem(),
    var forceStaticMocking: ForceStaticMocking = ForceStaticMocking.Companion.getDefaultItem(),
    var treatOverflowAsError: TreatOverflowAsError = TreatOverflowAsError.Companion.getDefaultItem()
) {
    public var classesToMockAlways: Set<String> = emptySet()
        internal set

    public operator fun component1(): CodegenLanguage = codegenLanguage

    public operator fun component2(): MockStrategyApi = mockStrategy

    public operator fun component3(): String = testFramework

    public operator fun component4(): StaticsMocking = staticsMocking

    public operator fun component5(): ForceStaticMocking = forceStaticMocking

    public operator fun component6(): TreatOverflowAsError = treatOverflowAsError

    public fun copy(
        codegenLanguage: CodegenLanguage = this.codegenLanguage,
        mockStrategy: MockStrategyApi = this.mockStrategy,
        testFramework: String = this.testFramework,
        staticsMocking: StaticsMocking = this.staticsMocking,
        forceStaticMocking: ForceStaticMocking = this.forceStaticMocking,
        treatOverflowAsError: TreatOverflowAsError = this.treatOverflowAsError
    ): UtStaticEngineConfiguration {
        return UtStaticEngineConfiguration(
            codegenLanguage,
            mockStrategy,
            testFramework,
            staticsMocking,
            forceStaticMocking,
            treatOverflowAsError
        )
    }

    override fun toString(): String {
        return "UtStaticEngineConfiguration(codegenLanguage=$codegenLanguage, mockStrategy=$mockStrategy, testFramework=$testFramework, staticsMocking=$staticsMocking, forceStaticMocking=$forceStaticMocking, treatOverflowAsError=$treatOverflowAsError)"
    }

    override fun hashCode(): Int {
        return (
            (
                (
                    (codegenLanguage.hashCode() * 31 + mockStrategy.hashCode()) * 31 + testFramework.hashCode()
                ) * 31 + staticsMocking.hashCode()
            ) * 31 + forceStaticMocking.hashCode()
            ) * 31 + treatOverflowAsError.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UtStaticEngineConfiguration) return false
        
        return codegenLanguage == other.codegenLanguage &&
            mockStrategy == other.mockStrategy &&
            testFramework == other.testFramework &&
            staticsMocking == other.staticsMocking &&
            forceStaticMocking == other.forceStaticMocking &&
            treatOverflowAsError == other.treatOverflowAsError
    }

    @JvmStatic
    fun `logger$lambda$0`() {}

    public companion object {
        private val logger: KLogger
    }
}