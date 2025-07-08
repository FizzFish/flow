package cn.sast.framework.engine

import mu.KLogger
import org.utbot.framework.codegen.domain.ForceStaticMocking
import org.utbot.framework.codegen.domain.StaticsMocking
import org.utbot.framework.plugin.api.CodegenLanguage
import org.utbot.framework.plugin.api.MockStrategyApi
import org.utbot.framework.plugin.api.TreatOverflowAsError

data class UtStaticEngineConfiguration(
    var codegenLanguage: CodegenLanguage = CodegenLanguage.defaultItem,
    var mockStrategy: MockStrategyApi = MockStrategyApi.NO_MOCKS,
    var testFramework: String = "junit4",
    var staticsMocking: StaticsMocking = StaticsMocking.defaultItem,
    var forceStaticMocking: ForceStaticMocking = ForceStaticMocking.defaultItem,
    var treatOverflowAsError: TreatOverflowAsError = TreatOverflowAsError.defaultItem
) {
    var classesToMockAlways: Set<String> = emptySet()
}