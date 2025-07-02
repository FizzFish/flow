package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult.Value
import cn.sast.framework.report.sqldelight.coraxframework.DatabaseImplKt

public interface Database : Transacter {
   public val absoluteFilePathQueries: AbsoluteFilePathQueries
   public val analyzeResultFileQueries: AnalyzeResultFileQueries
   public val analyzerStatisticsQueries: AnalyzerStatisticsQueries
   public val controlFlowQueries: ControlFlowQueries
   public val controlFlowPathQueries: ControlFlowPathQueries
   public val diagnosticQueries: DiagnosticQueries
   public val diagnosticExtQueries: DiagnosticExtQueries
   public val fileQueries: FileQueries
   public val macroExpansionQueries: MacroExpansionQueries
   public val noteQueries: NoteQueries
   public val noteExtQueries: NoteExtQueries
   public val notePathQueries: NotePathQueries
   public val regionQueries: RegionQueries
   public val ruleQueries: RuleQueries
   public val ruleMappingQueries: RuleMappingQueries
   public val ruleSetInfoQueries: RuleSetInfoQueries
   public val schemaInfoQueries: SchemaInfoQueries

   public companion object {
      public final val Schema: SqlSchema<Value<Unit>>
         public final get() {
            return DatabaseImplKt.getSchema(Database::class);
         }


      public operator fun invoke(driver: SqlDriver): Database {
         return DatabaseImplKt.newInstance(Database::class, driver);
      }
   }
}
