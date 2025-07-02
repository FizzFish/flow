package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.Transacter
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.TransactionWithReturn
import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.Transacter.DefaultImpls
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import cn.sast.framework.report.sqldelight.diagnostic.Verify_file
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function19
import kotlin.jvm.functions.Function2

public class DiagnosticQueries(driver: SqlDriver) : TransacterImpl(driver) {
   public fun id(
      rule_name: String,
      _rule_short_description_zh: String?,
      __file_id: Long?,
      _file_abs_path: String,
      severity: String?,
      precision: String?,
      likelihood: String?,
      impact: String?,
      technique: String?,
      analysis_scope: String?,
      line_content: String?,
      __note_array_hash_id: Long,
      __control_flow_array_hash_id: Long?,
      __macro_note_set_hash_id: Long?
   ): ExecutableQuery<Long> {
      return new DiagnosticQueries.IdQuery<>(
         this,
         rule_name,
         _rule_short_description_zh,
         __file_id,
         _file_abs_path,
         severity,
         precision,
         likelihood,
         impact,
         technique,
         analysis_scope,
         (long)line_content,
         __note_array_hash_id,
         __control_flow_array_hash_id,
         __macro_note_set_hash_id,
         DiagnosticQueries::id$lambda$0
      );
   }

   public fun <T : Any> verify_rule_name(mapper: (Long, String) -> T): ExecutableQuery<T> {
      return new DiagnosticQueries.Verify_rule_nameQuery(this, DiagnosticQueries::verify_rule_name$lambda$1);
   }

   public fun verify_rule_name(): ExecutableQuery<Verify_rule_name> {
      return this.verify_rule_name(DiagnosticQueries::verify_rule_name$lambda$2);
   }

   public fun <T : Any> verify_file(mapper: (Long, Long?) -> T): ExecutableQuery<T> {
      return new DiagnosticQueries.Verify_fileQuery(this, DiagnosticQueries::verify_file$lambda$3);
   }

   public fun verify_file(): ExecutableQuery<Verify_file> {
      return this.verify_file(DiagnosticQueries::verify_file$lambda$4);
   }

   public fun <T : Any> verify_note_path(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new DiagnosticQueries.Verify_note_pathQuery(this, DiagnosticQueries::verify_note_path$lambda$5);
   }

   public fun verify_note_path(): ExecutableQuery<Verify_note_path> {
      return this.verify_note_path(DiagnosticQueries::verify_note_path$lambda$6);
   }

   public fun <T : Any> verify_control_flow_path(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new DiagnosticQueries.Verify_control_flow_pathQuery(this, DiagnosticQueries::verify_control_flow_path$lambda$7);
   }

   public fun verify_control_flow_path(): ExecutableQuery<Verify_control_flow_path> {
      return this.verify_control_flow_path(DiagnosticQueries::verify_control_flow_path$lambda$8);
   }

   public fun <T : Any> verify_macro(mapper: (Long, Long) -> T): ExecutableQuery<T> {
      return new DiagnosticQueries.Verify_macroQuery(this, DiagnosticQueries::verify_macro$lambda$9);
   }

   public fun verify_macro(): ExecutableQuery<Verify_macro> {
      return this.verify_macro(DiagnosticQueries::verify_macro$lambda$10);
   }

   public fun <T : Any> selectAll(
      mapper: (
               Long,
               String,
               String?,
               Long?,
               String,
               Long,
               Long,
               String,
               String,
               String?,
               String?,
               String?,
               String?,
               String?,
               String?,
               String?,
               Long,
               Long?,
               Long?
            ) -> T
   ): Query<T> {
      return QueryKt.Query(
         2051098827,
         new java.lang.String[]{"Diagnostic"},
         this.getDriver(),
         "Diagnostic.sq",
         "selectAll",
         "SELECT Diagnostic.id, Diagnostic.rule_name, Diagnostic._rule_short_description_zh, Diagnostic.__file_id, Diagnostic._file_abs_path, Diagnostic._line, Diagnostic._column, Diagnostic._message_en, Diagnostic._message_zh, Diagnostic.severity, Diagnostic.precision, Diagnostic.likelihood, Diagnostic.impact, Diagnostic.technique, Diagnostic.analysis_scope, Diagnostic.line_content, Diagnostic.__note_array_hash_id, Diagnostic.__control_flow_array_hash_id, Diagnostic.__macro_note_set_hash_id\nFROM Diagnostic",
         DiagnosticQueries::selectAll$lambda$11
      );
   }

   public fun selectAll(): Query<Diagnostic> {
      return this.selectAll(DiagnosticQueries::selectAll$lambda$12);
   }

   public fun insert(
      rule_name: String,
      _rule_short_description_zh: String?,
      __file_id: Long?,
      _file_abs_path: String,
      _line: Long,
      _column: Long,
      _message_en: String,
      _message_zh: String,
      severity: String?,
      precision: String?,
      likelihood: String?,
      impact: String?,
      technique: String?,
      analysis_scope: String?,
      line_content: String?,
      __note_array_hash_id: Long,
      __control_flow_array_hash_id: Long?,
      __macro_note_set_hash_id: Long?
   ) {
      DefaultImpls.transaction$default(this as Transacter, false, DiagnosticQueries::insert$lambda$14, 1, null);
      this.notifyQueries(949328563, DiagnosticQueries::insert$lambda$15);
   }

   @JvmStatic
   fun `id$lambda$0`(cursor: SqlCursor): Long {
      val var10000: java.lang.Long = cursor.getLong(0);
      return var10000;
   }

   @JvmStatic
   fun `verify_rule_name$lambda$1`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_rule_name$lambda$2`(id: Long, rule_name: java.lang.String): Verify_rule_name {
      return new Verify_rule_name(id, rule_name);
   }

   @JvmStatic
   fun `verify_file$lambda$3`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      return `$mapper`.invoke(var10001, cursor.getLong(1));
   }

   @JvmStatic
   fun `verify_file$lambda$4`(id: Long, __file_id: java.lang.Long): Verify_file {
      return new Verify_file(id, __file_id);
   }

   @JvmStatic
   fun `verify_note_path$lambda$5`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_note_path$lambda$6`(id: Long, __note_array_hash_id: Long): Verify_note_path {
      return new Verify_note_path(id, __note_array_hash_id);
   }

   @JvmStatic
   fun `verify_control_flow_path$lambda$7`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_control_flow_path$lambda$8`(id: Long, __control_flow_array_hash_id: Long): Verify_control_flow_path {
      return new Verify_control_flow_path(id, __control_flow_array_hash_id);
   }

   @JvmStatic
   fun `verify_macro$lambda$9`(`$mapper`: Function2, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.Long = cursor.getLong(1);
      return `$mapper`.invoke(var10001, var10002);
   }

   @JvmStatic
   fun `verify_macro$lambda$10`(id: Long, __macro_note_set_hash_id: Long): Verify_macro {
      return new Verify_macro(id, __macro_note_set_hash_id);
   }

   @JvmStatic
   fun `selectAll$lambda$11`(`$mapper`: Function19, cursor: SqlCursor): Any {
      val var10001: java.lang.Long = cursor.getLong(0);
      val var10002: java.lang.String = cursor.getString(1);
      val var10003: java.lang.String = cursor.getString(2);
      val var10004: java.lang.Long = cursor.getLong(3);
      val var10005: java.lang.String = cursor.getString(4);
      val var10006: java.lang.Long = cursor.getLong(5);
      val var10007: java.lang.Long = cursor.getLong(6);
      val var10008: java.lang.String = cursor.getString(7);
      val var10009: java.lang.String = cursor.getString(8);
      val var10010: java.lang.String = cursor.getString(9);
      val var10011: java.lang.String = cursor.getString(10);
      val var10012: java.lang.String = cursor.getString(11);
      val var10013: java.lang.String = cursor.getString(12);
      val var10014: java.lang.String = cursor.getString(13);
      val var10015: java.lang.String = cursor.getString(14);
      val var10016: java.lang.String = cursor.getString(15);
      val var10017: java.lang.Long = cursor.getLong(16);
      return `$mapper`.invoke(
         var10001,
         var10002,
         var10003,
         var10004,
         var10005,
         var10006,
         var10007,
         var10008,
         var10009,
         var10010,
         var10011,
         var10012,
         var10013,
         var10014,
         var10015,
         var10016,
         var10017,
         cursor.getLong(17),
         cursor.getLong(18)
      );
   }

   @JvmStatic
   fun `selectAll$lambda$12`(
      id: Long,
      rule_name: java.lang.String,
      _rule_short_description_zh: java.lang.String,
      __file_id: java.lang.Long,
      _file_abs_path: java.lang.String,
      _line: Long,
      _column: Long,
      _message_en: java.lang.String,
      _message_zh: java.lang.String,
      severity: java.lang.String,
      precision: java.lang.String,
      likelihood: java.lang.String,
      impact: java.lang.String,
      technique: java.lang.String,
      analysis_scope: java.lang.String,
      line_content: java.lang.String,
      __note_array_hash_id: Long,
      __control_flow_array_hash_id: java.lang.Long,
      __macro_note_set_hash_id: java.lang.Long
   ): Diagnostic {
      return new Diagnostic(
         id,
         rule_name,
         _rule_short_description_zh,
         __file_id,
         _file_abs_path,
         _line,
         _column,
         _message_en,
         _message_zh,
         severity,
         precision,
         likelihood,
         impact,
         technique,
         analysis_scope,
         line_content,
         __note_array_hash_id,
         __control_flow_array_hash_id,
         __macro_note_set_hash_id
      );
   }

   @JvmStatic
   fun `insert$lambda$14$lambda$13`(
      `$rule_name`: java.lang.String,
      `$_rule_short_description_zh`: java.lang.String,
      `$__file_id`: java.lang.Long,
      `$_file_abs_path`: java.lang.String,
      `$_line`: Long,
      `$_column`: Long,
      `$_message_en`: java.lang.String,
      `$_message_zh`: java.lang.String,
      `$severity`: java.lang.String,
      `$precision`: java.lang.String,
      `$likelihood`: java.lang.String,
      `$impact`: java.lang.String,
      `$technique`: java.lang.String,
      `$analysis_scope`: java.lang.String,
      `$line_content`: java.lang.String,
      `$__note_array_hash_id`: Long,
      `$__control_flow_array_hash_id`: java.lang.Long,
      `$__macro_note_set_hash_id`: java.lang.Long,
      `$this$execute`: SqlPreparedStatement
   ): Unit {
      `$this$execute`.bindString(0, `$rule_name`);
      `$this$execute`.bindString(1, `$_rule_short_description_zh`);
      `$this$execute`.bindLong(2, `$__file_id`);
      `$this$execute`.bindString(3, `$_file_abs_path`);
      `$this$execute`.bindLong(4, `$_line`);
      `$this$execute`.bindLong(5, `$_column`);
      `$this$execute`.bindString(6, `$_message_en`);
      `$this$execute`.bindString(7, `$_message_zh`);
      `$this$execute`.bindString(8, `$severity`);
      `$this$execute`.bindString(9, `$precision`);
      `$this$execute`.bindString(10, `$likelihood`);
      `$this$execute`.bindString(11, `$impact`);
      `$this$execute`.bindString(12, `$technique`);
      `$this$execute`.bindString(13, `$analysis_scope`);
      `$this$execute`.bindString(14, `$line_content`);
      `$this$execute`.bindLong(15, `$__note_array_hash_id`);
      `$this$execute`.bindLong(16, `$__control_flow_array_hash_id`);
      `$this$execute`.bindLong(17, `$__macro_note_set_hash_id`);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$14`(
      `this$0`: DiagnosticQueries,
      `$rule_name`: java.lang.String,
      `$_rule_short_description_zh`: java.lang.String,
      `$__file_id`: java.lang.Long,
      `$_file_abs_path`: java.lang.String,
      `$_line`: Long,
      `$_column`: Long,
      `$_message_en`: java.lang.String,
      `$_message_zh`: java.lang.String,
      `$severity`: java.lang.String,
      `$precision`: java.lang.String,
      `$likelihood`: java.lang.String,
      `$impact`: java.lang.String,
      `$technique`: java.lang.String,
      `$analysis_scope`: java.lang.String,
      `$line_content`: java.lang.String,
      `$__note_array_hash_id`: Long,
      `$__control_flow_array_hash_id`: java.lang.Long,
      `$__macro_note_set_hash_id`: java.lang.Long,
      `$this$transaction`: TransactionWithoutReturn
   ): Unit {
      `this$0`.getDriver()
         .execute(
            1771685284,
            "INSERT OR IGNORE INTO Diagnostic(rule_name, _rule_short_description_zh,\n                                     __file_id, _file_abs_path,\n                                     _line, _column, _message_en, _message_zh,\n                                     severity, precision, likelihood, impact, technique, analysis_scope,\n                                     line_content,\n                                     __note_array_hash_id, __control_flow_array_hash_id, __macro_note_set_hash_id\n--                                      issue_context_kind, issue_context, issue_hash_func_offset\n                                     )\n    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?\n--     ?, ?, ?\n    )",
            18,
            DiagnosticQueries::insert$lambda$14$lambda$13
         );
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `insert$lambda$15`(emit: Function1): Unit {
      emit.invoke("Diagnostic");
      return Unit.INSTANCE;
   }

   private inner class IdQuery<T>(rule_name: String,
      _rule_short_description_zh: String?,
      __file_id: Long?,
      _file_abs_path: String,
      severity: String?,
      precision: String?,
      likelihood: String?,
      impact: String?,
      technique: String?,
      analysis_scope: String?,
      line_content: String?,
      __note_array_hash_id: Long,
      __control_flow_array_hash_id: Long?,
      __macro_note_set_hash_id: Long?,
      mapper: (SqlCursor) -> Any
   ) : ExecutableQuery(mapper) {
      public final val rule_name: String
      public final val _rule_short_description_zh: String?
      public final val __file_id: Long?
      public final val _file_abs_path: String
      public final val severity: String?
      public final val precision: String?
      public final val likelihood: String?
      public final val impact: String?
      public final val technique: String?
      public final val analysis_scope: String?
      public final val line_content: String?
      public final val __note_array_hash_id: Long
      public final val __control_flow_array_hash_id: Long?
      public final val __macro_note_set_hash_id: Long?

      init {
         this.this$0 = `this$0`;
         this.rule_name = rule_name;
         this._rule_short_description_zh = _rule_short_description_zh;
         this.__file_id = __file_id;
         this._file_abs_path = _file_abs_path;
         this.severity = severity;
         this.precision = precision;
         this.likelihood = likelihood;
         this.impact = impact;
         this.technique = technique;
         this.analysis_scope = analysis_scope;
         this.line_content = line_content;
         this.__note_array_hash_id = __note_array_hash_id;
         this.__control_flow_array_hash_id = __control_flow_array_hash_id;
         this.__macro_note_set_hash_id = __macro_note_set_hash_id;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, DiagnosticQueries.IdQuery::execute$lambda$1, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:id";
      }

      @JvmStatic
      fun `execute$lambda$1$lambda$0`(`this$0`: DiagnosticQueries.IdQuery, `$this$executeQuery`: SqlPreparedStatement): Unit {
         `$this$executeQuery`.bindString(0, `this$0`.rule_name);
         `$this$executeQuery`.bindString(1, `this$0`._rule_short_description_zh);
         `$this$executeQuery`.bindLong(2, `this$0`.__file_id);
         `$this$executeQuery`.bindString(3, `this$0`._file_abs_path);
         `$this$executeQuery`.bindString(4, `this$0`.severity);
         `$this$executeQuery`.bindString(5, `this$0`.precision);
         `$this$executeQuery`.bindString(6, `this$0`.likelihood);
         `$this$executeQuery`.bindString(7, `this$0`.impact);
         `$this$executeQuery`.bindString(8, `this$0`.technique);
         `$this$executeQuery`.bindString(9, `this$0`.analysis_scope);
         `$this$executeQuery`.bindString(10, `this$0`.line_content);
         `$this$executeQuery`.bindLong(11, `this$0`.__note_array_hash_id);
         `$this$executeQuery`.bindLong(12, `this$0`.__control_flow_array_hash_id);
         `$this$executeQuery`.bindLong(13, `this$0`.__macro_note_set_hash_id);
         return Unit.INSTANCE;
      }

      @JvmStatic
      fun `execute$lambda$1`(
         `this$0`: DiagnosticQueries, `this$1`: DiagnosticQueries.IdQuery, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn
      ): QueryResult {
         return DiagnosticQueries.access$getDriver(`this$0`)
            .executeQuery(
               null,
               StringsKt.trimMargin$default(
                  "\n          |SELECT id FROM Diagnostic WHERE rule_name = ? AND _rule_short_description_zh ${if (`this$1`._rule_short_description_zh == null)
                     "IS"
                     else
                     "="} ? AND __file_id ${if (`this$1`.__file_id == null) "IS" else "="} ? AND _file_abs_path = ?\n          |    AND severity ${if (`this$1`.severity
                        == null)
                     "IS"
                     else
                     "="} ? AND precision ${if (`this$1`.precision == null) "IS" else "="} ? AND likelihood ${if (`this$1`.likelihood == null) "IS" else "="} ? AND impact ${if (`this$1`.impact
                        == null)
                     "IS"
                     else
                     "="} ? AND technique ${if (`this$1`.technique == null) "IS" else "="} ? AND analysis_scope ${if (`this$1`.analysis_scope == null)
                     "IS"
                     else
                     "="} ? AND line_content ${if (`this$1`.line_content == null) "IS" else "="} ?\n          |    AND __note_array_hash_id = ? AND __control_flow_array_hash_id ${if (`this$1`.__control_flow_array_hash_id
                        == null)
                     "IS"
                     else
                     "="} ? AND __macro_note_set_hash_id ${if (`this$1`.__macro_note_set_hash_id == null) "IS" else "="} ?\n          ",
                  null,
                  1,
                  null
               ),
               `$mapper`,
               14,
               DiagnosticQueries.IdQuery::execute$lambda$1$lambda$0
            );
      }
   }

   private inner class Verify_control_flow_pathQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(
            this.this$0 as Transacter, false, DiagnosticQueries.Verify_control_flow_pathQuery::execute$lambda$0, 1, null
         ) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:verify_control_flow_path";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: DiagnosticQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            DiagnosticQueries.access$getDriver(`this$0`),
            -545866471,
            "SELECT id, __control_flow_array_hash_id FROM Diagnostic WHERE __control_flow_array_hash_id IS NOT NULL AND __control_flow_array_hash_id NOT IN (SELECT __control_flow_array_hash_id FROM ControlFlowPath)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }

   private inner class Verify_fileQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, DiagnosticQueries.Verify_fileQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:verify_file";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: DiagnosticQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            DiagnosticQueries.access$getDriver(`this$0`),
            2056148857,
            "SELECT id, __file_id FROM Diagnostic WHERE __file_id NOT IN (SELECT id FROM File)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }

   private inner class Verify_macroQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, DiagnosticQueries.Verify_macroQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:verify_macro";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: DiagnosticQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            DiagnosticQueries.access$getDriver(`this$0`),
            996722865,
            "SELECT id, __macro_note_set_hash_id FROM Diagnostic WHERE __macro_note_set_hash_id IS NOT NULL AND __macro_note_set_hash_id NOT IN (SELECT __macro_note_set_hash_id FROM MacroExpansion)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }

   private inner class Verify_note_pathQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, DiagnosticQueries.Verify_note_pathQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:verify_note_path";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: DiagnosticQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            DiagnosticQueries.access$getDriver(`this$0`),
            -1475379785,
            "SELECT id, __note_array_hash_id FROM Diagnostic WHERE __note_array_hash_id NOT IN (SELECT __note_array_hash_id FROM NotePath)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }

   private inner class Verify_rule_nameQuery<T>(mapper: (SqlCursor) -> Any) : ExecutableQuery(mapper) {
      init {
         this.this$0 = `this$0`;
      }

      public open fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> {
         return DefaultImpls.transactionWithResult$default(this.this$0 as Transacter, false, DiagnosticQueries.Verify_rule_nameQuery::execute$lambda$0, 1, null) as QueryResult<R>;
      }

      public open fun toString(): String {
         return "Diagnostic.sq:verify_rule_name";
      }

      @JvmStatic
      fun `execute$lambda$0`(`this$0`: DiagnosticQueries, `$mapper`: Function1, `$this$transactionWithResult`: TransactionWithReturn): QueryResult {
         return app.cash.sqldelight.db.SqlDriver.DefaultImpls.executeQuery$default(
            DiagnosticQueries.access$getDriver(`this$0`),
            264932435,
            "SELECT id, rule_name FROM Diagnostic WHERE rule_name NOT IN (SELECT name FROM Rule)",
            `$mapper`,
            0,
            null,
            16,
            null
         );
      }
   }
}
