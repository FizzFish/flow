package cn.sast.framework.engine

import mu.KLogger
import org.utbot.framework.codegen.domain.ForceStaticMocking
import org.utbot.framework.codegen.domain.StaticsMocking
import org.utbot.framework.plugin.api.CodegenLanguage
import org.utbot.framework.plugin.api.MockStrategyApi
import org.utbot.framework.plugin.api.TreatOverflowAsError

public data class UtStaticEngineConfiguration(codegenLanguage: CodegenLanguage = CodegenLanguage.Companion.getDefaultItem(),
   mockStrategy: MockStrategyApi = MockStrategyApi.NO_MOCKS,
   testFramework: String = "junit4",
   staticsMocking: StaticsMocking = StaticsMocking.Companion.getDefaultItem(),
   forceStaticMocking: ForceStaticMocking = ForceStaticMocking.Companion.getDefaultItem(),
   treatOverflowAsError: TreatOverflowAsError = TreatOverflowAsError.Companion.getDefaultItem()
) {
   public final var codegenLanguage: CodegenLanguage
      internal set

   public final var mockStrategy: MockStrategyApi
      internal set

   public final var testFramework: String
      internal set

   public final var staticsMocking: StaticsMocking
      internal set

   public final var forceStaticMocking: ForceStaticMocking
      internal set

   public final var treatOverflowAsError: TreatOverflowAsError
      internal set

   public final var classesToMockAlways: Set<String>
      internal set

   init {
      this.codegenLanguage = codegenLanguage;
      this.mockStrategy = mockStrategy;
      this.testFramework = testFramework;
      this.staticsMocking = staticsMocking;
      this.forceStaticMocking = forceStaticMocking;
      this.treatOverflowAsError = treatOverflowAsError;
      this.classesToMockAlways = SetsKt.emptySet();
   }

   public operator fun component1(): CodegenLanguage {
      return this.codegenLanguage;
   }

   public operator fun component2(): MockStrategyApi {
      return this.mockStrategy;
   }

   public operator fun component3(): String {
      return this.testFramework;
   }

   public operator fun component4(): StaticsMocking {
      return this.staticsMocking;
   }

   public operator fun component5(): ForceStaticMocking {
      return this.forceStaticMocking;
   }

   public operator fun component6(): TreatOverflowAsError {
      return this.treatOverflowAsError;
   }

   public fun copy(
      codegenLanguage: CodegenLanguage = this.codegenLanguage,
      mockStrategy: MockStrategyApi = this.mockStrategy,
      testFramework: String = this.testFramework,
      staticsMocking: StaticsMocking = this.staticsMocking,
      forceStaticMocking: ForceStaticMocking = this.forceStaticMocking,
      treatOverflowAsError: TreatOverflowAsError = this.treatOverflowAsError
   ): UtStaticEngineConfiguration {
      return new UtStaticEngineConfiguration(codegenLanguage, mockStrategy, testFramework, staticsMocking, forceStaticMocking, treatOverflowAsError);
   }

   public override fun toString(): String {
      return "UtStaticEngineConfiguration(codegenLanguage=${this.codegenLanguage}, mockStrategy=${this.mockStrategy}, testFramework=${this.testFramework}, staticsMocking=${this.staticsMocking}, forceStaticMocking=${this.forceStaticMocking}, treatOverflowAsError=${this.treatOverflowAsError})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        ((this.codegenLanguage.hashCode() * 31 + this.mockStrategy.hashCode()) * 31 + this.testFramework.hashCode()) * 31
                           + this.staticsMocking.hashCode()
                     )
                     * 31
                  + this.forceStaticMocking.hashCode()
            )
            * 31
         + this.treatOverflowAsError.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is UtStaticEngineConfiguration) {
         return false;
      } else {
         val var2: UtStaticEngineConfiguration = other as UtStaticEngineConfiguration;
         if (this.codegenLanguage != (other as UtStaticEngineConfiguration).codegenLanguage) {
            return false;
         } else if (this.mockStrategy != var2.mockStrategy) {
            return false;
         } else if (!(this.testFramework == var2.testFramework)) {
            return false;
         } else if (!(this.staticsMocking == var2.staticsMocking)) {
            return false;
         } else if (this.forceStaticMocking != var2.forceStaticMocking) {
            return false;
         } else {
            return this.treatOverflowAsError === var2.treatOverflowAsError;
         }
      }
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   fun UtStaticEngineConfiguration() {
      this(null, null, null, null, null, null, 63, null);
   }

   public companion object {
      private final val logger: KLogger
   }
}
