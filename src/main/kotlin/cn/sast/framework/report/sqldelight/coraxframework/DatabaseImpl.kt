package cn.sast.framework.report.sqldelight.coraxframework

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult.Value
import app.cash.sqldelight.db.SqlDriver.DefaultImpls
import cn.sast.framework.report.sqldelight.AbsoluteFilePathQueries
import cn.sast.framework.report.sqldelight.AnalyzeResultFileQueries
import cn.sast.framework.report.sqldelight.AnalyzerStatisticsQueries
import cn.sast.framework.report.sqldelight.ControlFlowPathQueries
import cn.sast.framework.report.sqldelight.ControlFlowQueries
import cn.sast.framework.report.sqldelight.Database
import cn.sast.framework.report.sqldelight.DiagnosticExtQueries
import cn.sast.framework.report.sqldelight.DiagnosticQueries
import cn.sast.framework.report.sqldelight.FileQueries
import cn.sast.framework.report.sqldelight.MacroExpansionQueries
import cn.sast.framework.report.sqldelight.NoteExtQueries
import cn.sast.framework.report.sqldelight.NotePathQueries
import cn.sast.framework.report.sqldelight.NoteQueries
import cn.sast.framework.report.sqldelight.RegionQueries
import cn.sast.framework.report.sqldelight.RuleMappingQueries
import cn.sast.framework.report.sqldelight.RuleQueries
import cn.sast.framework.report.sqldelight.RuleSetInfoQueries
import cn.sast.framework.report.sqldelight.SchemaInfoQueries

private class DatabaseImpl(driver: SqlDriver) : TransacterImpl(driver), Database {
   public open val absoluteFilePathQueries: AbsoluteFilePathQueries
   public open val analyzeResultFileQueries: AnalyzeResultFileQueries
   public open val analyzerStatisticsQueries: AnalyzerStatisticsQueries
   public open val controlFlowQueries: ControlFlowQueries
   public open val controlFlowPathQueries: ControlFlowPathQueries
   public open val diagnosticQueries: DiagnosticQueries
   public open val diagnosticExtQueries: DiagnosticExtQueries
   public open val fileQueries: FileQueries
   public open val macroExpansionQueries: MacroExpansionQueries
   public open val noteQueries: NoteQueries
   public open val noteExtQueries: NoteExtQueries
   public open val notePathQueries: NotePathQueries
   public open val regionQueries: RegionQueries
   public open val ruleQueries: RuleQueries
   public open val ruleMappingQueries: RuleMappingQueries
   public open val ruleSetInfoQueries: RuleSetInfoQueries
   public open val schemaInfoQueries: SchemaInfoQueries

   init {
      this.absoluteFilePathQueries = new AbsoluteFilePathQueries(driver);
      this.analyzeResultFileQueries = new AnalyzeResultFileQueries(driver);
      this.analyzerStatisticsQueries = new AnalyzerStatisticsQueries(driver);
      this.controlFlowQueries = new ControlFlowQueries(driver);
      this.controlFlowPathQueries = new ControlFlowPathQueries(driver);
      this.diagnosticQueries = new DiagnosticQueries(driver);
      this.diagnosticExtQueries = new DiagnosticExtQueries(driver);
      this.fileQueries = new FileQueries(driver);
      this.macroExpansionQueries = new MacroExpansionQueries(driver);
      this.noteQueries = new NoteQueries(driver);
      this.noteExtQueries = new NoteExtQueries(driver);
      this.notePathQueries = new NotePathQueries(driver);
      this.regionQueries = new RegionQueries(driver);
      this.ruleQueries = new RuleQueries(driver);
      this.ruleMappingQueries = new RuleMappingQueries(driver);
      this.ruleSetInfoQueries = new RuleSetInfoQueries(driver);
      this.schemaInfoQueries = new SchemaInfoQueries(driver);
   }

   public object Schema : SqlSchema<Value<Unit>> {
      public open val version: Long
         public open get() {
            return 1L;
         }


      public open fun create(driver: SqlDriver): Value<Unit> {
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS AbsoluteFilePath(\n  -- Key\n    file_abs_path TEXT NOT NULL, -- 文件全路径并normalize后的 path string FIXME: 需要全体明确\n                                 -- 请阅读 Readme.md\n    __file_id INTEGER NOT NULL REFERENCES File(id) ON DELETE CASCADE,\n\n  -- 约束\n    UNIQUE (file_abs_path, __file_id)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS AnalyzerResultFile (\n    file_name TEXT NOT NULL,  -- 文件名字比如 corax-java-log.txt\n    file_path TEXT,           -- 正常文件路径并normalize后的 path string (全路径相对路径都可以, 不需要统一unix path),\n    __file_id INTEGER NOT NULL REFERENCES File(id) ON DELETE CASCADE, -- 文件内容\n\n    UNIQUE (file_path, __file_id)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS AnalyzerStatistics (\n    name TEXT NOT NULL, -- \"Corax\"\n    -- 报告版本看 SchemaInfo 表\n    corax_probe_version TEXT NOT NULL, -- \"probe-8a8e8f81\"\n    analyzer_version TEXT NOT NULL,    -- \"utbot-2.10-dev\"\n\n    analysis_begin_date TEXT NOT NULL,        -- 年月日分秒\n    analysis_begin_timestamp INTEGER NOT NULL,-- 时间戳\n    analysis_escape_seconds INTEGER NOT NULL, -- 分析花了多少秒\n    analysis_escape_time TEXT NOT NULL,       -- 分析花了多少日分秒, 不超过日分就不不建议输出日分\n    analysis_end_date TEXT NOT NULL,          -- 年月日分秒\n    analysis_end_timestamp INTEGER NOT NULL,  -- 时间戳\n\n    file_count INTEGER NOT NULL,   -- 分析代码总文件数\n    line_count INTEGER NOT NULL,   -- 分析代码总行数\n    code_coverage_covered INTEGER, -- 代码扫描覆盖的文件数\n    code_coverage_missed INTEGER,  -- 代码扫描未覆盖文件数\n    num_of_report_dir INTEGER,     -- 报告输出路径数量\n    source_paths TEXT NOT NULL,    -- 多个源码路径，\",\" 分割\n    os_name TEXT NOT NULL,         -- \"windows 10\"\n\n    command_json TEXT NOT NULL,      -- json array of command args\n    working_directory TEXT NOT NULL, -- 正常的源码绝对路径，normalize一下就行，不需要统一\n    output_path TEXT NOT NULL,       -- 正常的源码绝对路径，normalize一下就行，不需要统一\n    project_root TEXT NOT NULL,      -- 多个正常的源码绝对路径，normalize一下就行，不需要统一，\",\" 分割，注意先后顺序\n    log_file TEXT NOT NULL,          -- 绝对路径，normalize一下就行，不需要统一\n\n    enable_rules TEXT NOT NULL,      -- 多个开启的规则, \",\" 分割\n    disable_rules TEXT NOT NULL,     -- 多个关闭的规则, \",\" 分割\n\n    failed_sources TEXT NOT NULL,            -- 分析失败的文件路径数组。多个正常的源码绝对路径，normalize一下就行，不需要统一，\",\" 分割\n    failed_sources_num INTEGER NOT NULL,     -- 分析失败个数\n    successful_sources TEXT NOT NULL,        -- 分析成功的文件路径数组。多个正常的源码绝对路径，normalize一下就行，不需要统一，\",\" 分割\n    successful_sources_num INTEGER NOT NULL, -- 分析成功个数\n    skipped_sources TEXT NOT NULL,           -- 跳过分析的文件路径数组。多个正常的源码绝对路径，normalize一下就行，不需要统一，\",\" 分割\n    skipped_sources_num INTEGER NOT NULL     -- 跳过分析的数量\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS ControlFlow( -- control flow table for diagnostic\n  -- Key\n    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 本表的唯一标识\n                                                   -- 被 ControlFlowPath 表关联\n\n  -- Value\n    __file_id INTEGER NOT NULL REFERENCES File(id) ON DELETE CASCADE,\n    _file_abs_path TEXT NOT NULL, -- 文件全路径并normalize后的 path string FIXME: 需要全体明确\n                                  -- 请阅读 Readme.md\n    message_en TEXT,              -- message\n    message_zh TEXT,              -- message\n\n    __edge_from_region_id INTEGER NOT NULL REFERENCES Region(id) ON DELETE CASCADE, -- 一段曲线 start\n    __edge_to_region_id INTEGER NOT NULL REFERENCES Region(id) ON DELETE CASCADE, -- 一段曲线 end\n\n  -- 约束\n    UNIQUE (__file_id, _file_abs_path, message_en, message_zh, __edge_from_region_id, __edge_to_region_id)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS ControlFlowPath(\n  -- Value\n    -- 不是主键！可以重复\n    __control_flow_array_hash_id INTEGER NOT NULL,  -- 被 Diagnostic 表关联\n         -- FIXME：讲解\n         -- 一条控制流路径对应的有序的ControlFlow数组 唯一对应一个 array_hash_id\n         -- 此 array_hash_id 可以是 hash 数值，（或者是多个不同 array_hash 对应的自增 id，引擎自行选择）\n         -- 注意1：control_flow的hash计算输入: (缺一不可)\n         --      1: control_flow_id：control_flow的当前db里面的id，注意：hash存储到平台端时候， 因为Note id变了，平台也需再次计算新的note hash再存到平台数据库\n         --      2: control_flow_sequence\n         -- 注意2：平台数据库不能存储此hash, 不要把滥用此hash, 并且此hash计算的输入不可包含其他非control_flow信息\n         -- 设计此字段的原因：\n         --       因为一个 Diagnostic(诊断) 对应一个ControlFlowPath(控制流路径)，一个ControlFlow是由多个有序的 ControlFlow 组成的数组\n         --       可能出现同一个ControlFlow可能同时存在于多个ControlFlowPath里面的情况，\n         --       所以需要一个path hash表示此ControlFlow属于哪一个ControlFlowPath\n\n  -- Value\n    control_flow_sequence INTEGER NOT NULL, -- control flow的排序\n    __control_flow_id INTEGER NOT NULL REFERENCES ControlFlow(id) ON DELETE CASCADE,      -- 外键\n\n  -- 约束\n    PRIMARY KEY (__control_flow_array_hash_id, control_flow_sequence,  __control_flow_id) -- 确保唯一\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS Diagnostic( --diagnostics\n  -- Key\n    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 本表的唯一标识\n                                                   -- 被 DiagnosticExt 表关联\n\n\n  -- Value\n    rule_name TEXT NOT NULL REFERENCES Rule(name) ON DELETE CASCADE, -- misra-c2012-R.10.1\n    _rule_short_description_zh TEXT, -- 规则最短的描述\n\n    -- FIXME：location 和与关联的note的last是一致的, 是否需要？\n    __file_id INTEGER REFERENCES File(id) ON DELETE CASCADE, -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段\n    _file_abs_path TEXT NOT NULL, -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段。冗余的多余的（但是不能为空）\n                                  -- 文件全路径并normalize后的 path string FIXME: 需要全体明确\n                                  -- 请阅读 Readme.md\n    _line INTEGER NOT NULL,       -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段。冗余的多余的（但是不能为空）\n    _column INTEGER NOT NULL,     -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段。冗余的多余的（可以为空）\n    _message_en TEXT NOT NULL,    -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段。冗余的多余的（但是不能为空）\n    _message_zh TEXT NOT NULL,    -- 和最后一个note数据相等，只是为了直观方便debug才声明此字段。冗余的多余的（所以可以为空）\n\n\n    severity TEXT,          -- 评估维度: 严重程度 (High|Medium|Low|Info), 如果没有,平台则会寻找Rule里面的severity\n    precision TEXT,         -- 精准度 (Highest|High|Medium|Low|Lowest)\n    likelihood TEXT,        -- 漏洞被利用可能性 (Highest|High|Medium|Low|Lowest)\n    impact TEXT,            -- 指出漏洞可以造成多大的损害 (Highest|High|Medium|Low|Lowest)\n    technique TEXT,         -- 实现原理，支持多个，逗号','分割, 需要先排序 (SA|AST|DataFlow|CG|CPG|...不限)\n    analysis_scope TEXT,    -- 分析范围，支持多个，逗号','分割, 需要先排序 (TU|System|...不限)\n\n    -- FIXME: 需要全体明确, 讨论：引擎提供的就是一行原始文本，平台去过滤空格等等字符\n    line_content TEXT, -- line content 编码采用 UTF-8\n                       -- // 注意1：\n                       --        需要平台端字符过滤 \\s空格、\\t、\\r、\\n 、\\u0000）\n                       -- // 注意2：严格定义，后续不能变动！！！(当然真过滤或者取行数出错了，平台还是可以重新根据原文件内容计算一次新的line_content补救)\n                       --       平台根据此字段进行不同行数的漏洞的状态关联和继承，所以该字符过滤步骤必须小心和严谨\n                       -- // 注意3:\n                       --       长度限制384字符，超过则截断前384字符. java示例： if (it.length > 384) return it.substring(0, 384) else it\n\n    -- 一个 NotePath 里面至少包含一个 kind 为 event 的 Note\n    __note_array_hash_id INTEGER NOT NULL REFERENCES NotePath(__note_array_hash_id) ON DELETE CASCADE, -- 外键, 不能为空\n    __control_flow_array_hash_id INTEGER REFERENCES ControlFlowPath(__control_flow_array_hash_id) ON DELETE CASCADE,-- 外键, 可以为空\n    __macro_note_set_hash_id INTEGER REFERENCES MacroExpansion(__macro_note_set_hash_id) ON DELETE CASCADE,-- 外键, 可以为空，仅C/C++语言适用\n\n\n    -- FIXME: 是不是可以放到DiagnosticExt？反正很多字段都是空着的\n--     issue_context_kind TEXT,     -- (class|method|function|...不限) FIXME\n--     issue_context TEXT,          -- DeclName FIXME\n--     issue_hash_func_offset TEXT, -- FIXME 这是什么？\n\n  -- 约束\n    -- _line, _column, _message_en, _message_zh 不必 UNIQUE\n    UNIQUE (rule_name, _rule_short_description_zh,\n            __file_id, _file_abs_path,\n            severity, precision, likelihood, impact, technique, analysis_scope,\n            line_content,\n            __note_array_hash_id, __control_flow_array_hash_id, __macro_note_set_hash_id\n--             issue_context_kind, issue_context, issue_hash_func_offset\n           )\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS DiagnosticExt(\n  -- Key\n    __diagnostic_id INTEGER NOT NULL REFERENCES Diagnostic(id) ON DELETE CASCADE,\n\n  -- Value\n    attr_name  TEXT NOT NULL, -- attribute name\n    attr_value TEXT NOT NULL, -- attribute value\n\n  -- 约束\n    PRIMARY KEY (__diagnostic_id, attr_name, attr_value)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS File(\n  -- Key\n    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 唯一标识\n                   -- 被\n                   --   - Diagnostic\n                   --   - Note\n                   --   - ControlFlow\n                   --   - Region\n                   -- 表关联\n\n  -- Value\n    file_raw_content_hash TEXT NOT NULL, -- （不是主键, 也不唯一）, 必须小写， sha256 of file_raw_content\n    relative_path     TEXT NOT NULL,     -- path/to/file.cpp FIXME: 需要全体明确\n           -- 注意：全引擎组要求：\n           -- 当文件路径属于某个project root路径或者output目录路径时候，不需要根据root路径去除全路径前缀成为相对路径，\n           -- 当文件路径不属于任何一个root目录，则允许全路径\n           -- 不允许包含..  不允许开头有任何斜杠，不允许使用反斜杠，并且采用unix标准格式\n           -- 例如：\n           --     project_roots 为 [/home/xxx/proj/] 时\n           --     则该全路径：/home/xxx/proj/src/a.java 对应的相对路径为\n           --         合规路径，\n           --             src/a.java\n           --         不合规路径：\n           --             /home/xxx/proj/src/a.java\n           --             /src/a.java\n           --             \\src/a.java\n           --             src\\a.java\n           --             src//a.java\n           --             src\\a.java\n           --             src\\\\a.java\n           --             src/../src/a.java\n           --             ../proj/src/../src/a.java\n           --     则该全路径：/usr/include/libc/stdio.h ，因为不属于任何project_root，则对应相对路径为全路径, 或者理解为root目录就是/\n           --         usr/include/libc/stdio.h\n           -- 注意：平台组要求：请检查path是否符合规范，不符合直接扫描失败\n\n    lines INTEGER NOT NULL,                  -- UTF-8 字符串的长度, 注意：平台计算hash不要hash此字段\n    encoding TEXT,                           -- utf-8, 小写\n\n    file_raw_content_size  INTEGER NOT NULL, -- file_raw_content bytes 的长度, 注意：平台计算hash不要hash此字段（怕引擎端设置错误的值）\n    file_raw_content BLOB NOT NULL,          -- file bytes code raw content, 注意：平台计算hash不要hash此字段\n\n  -- 约束\n    UNIQUE (file_raw_content_hash, relative_path) -- 不要加content做UNIQUE,性能消耗高\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS MacroExpansion (\n  -- Value\n    -- 不是主键！可以重复\n    __macro_note_set_hash_id INTEGER NOT NULL,  -- 被 Diagnostic 表关联\n         -- FIXME：讲解\n         -- 一个的 note集合(Set) 唯一对应一个 set_hash_id\n         -- 此 set_hash_id 可以是 hash 数值，（或者是多个不同 set_hash 对应的自增 id，引擎自行选择）\n         -- 注意1：note的hash计算输入: (缺一不可)\n         --      1: note_id：note的当前db里面的id，注意：hash存储到平台端时候， 因为Note id变了，平台也需再次计算新的note hash再存到平台数据库\n\n    __macro_note_id INTEGER NOT NULL REFERENCES Note(id) ON DELETE CASCADE, -- 外键\n                                                                            -- 关联的Note的kind应该都是macro-expansion\n\n  -- 约束\n    PRIMARY KEY (__macro_note_set_hash_id, __macro_note_id) -- 设定复合主键确保唯一\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS Note( -- note table for diagnostic\n  -- Key\n    id INTEGER PRIMARY KEY AUTOINCREMENT, -- 本表的唯一标识\n                                          -- 被 NotePath、NoteExt 表关联\n\n  -- Value\n    kind TEXT NOT NULL,           -- (note|event|pop-up|macro-expansion)\n    display_hint TEXT NOT NULL,   -- (Above|Below)\n\n    __file_id INTEGER NOT NULL REFERENCES File(id) ON DELETE CASCADE, -- note location\n    _file_abs_path TEXT NOT NULL, -- 文件全路径并normalize后的 path string FIXME: 需要全体明确\n                                  -- 请阅读 Readme.md\n    line INTEGER NOT NULL,\n    column INTEGER,\n\n    message_en TEXT NOT NULL,     -- message or macro expansion\n    message_zh TEXT NOT NULL,     -- message or macro expansion\n\n    __notices_region_id INTEGER REFERENCES Region(id) ON DELETE CASCADE, -- 波浪线\n    __func_region_id INTEGER REFERENCES Region(id) ON DELETE CASCADE,    -- 用于AI代码切片的的函数范围\n--     __remove_region_id INTEGER REFERENCES Region(id) ON DELETE CASCADE,  -- FixItHint for note/event. 暂时不支持修复所以不需要此字段\n--     __remove_insert_text TEXT,\n\n  -- 约束\n    UNIQUE (kind, display_hint,\n            __file_id, _file_abs_path, line, column,\n            message_en, message_zh,\n            __notices_region_id, __func_region_id\n    )\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS NoteExt(\n  -- Key\n    __note_id INTEGER NOT NULL REFERENCES Note(id) ON DELETE CASCADE,\n\n  -- Value\n    attr_name  TEXT NOT NULL, -- attribute name (macro_name|insert_string)\n    attr_value TEXT NOT NULL, -- attribute value\n\n  -- 约束\n    PRIMARY KEY (__note_id, attr_name, attr_value)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS NotePath (\n  -- Value\n    -- 不是主键！可以重复\n    __note_array_hash_id INTEGER NOT NULL,  -- 被 Diagnostic 表关联\n         -- FIXME：讲解\n         -- 一条触发路径对应的有序的note数组 唯一对应一个 array_hash_id\n         -- 此 array_hash_id 可以是 hash 数值，（或者是多个不同 array_hash 对应的自增 id，引擎自行选择）\n         -- 注意1：note的hash计算输入: (缺一不可)\n         --      1: note_id：note的当前db里面的id，注意：hash存储到平台端时候， 因为Note id变了，平台也需再次计算新的note hash再存到平台数据库\n         --      2: note_sequence\n         --      3: note_stack_depth\n         --      3: note_is_key_event\n         -- 注意2：平台数据库不能存储此hash, 不要滥用此hash, 并且此hash计算的输入不可包含其他非Note信息\n         -- 设计此字段的原因：\n         --       因为一个 Diagnostic(诊断) 对应一个NotePath(提示路径)，一个NotePath是由多个有序的 Note 组成的数组\n         --       可能出现同一个Note可能同时存在于多个NotePath里面的情况，\n         --       所以需要一个path hash表示此Note属于哪一个NotePath\n\n    note_sequence INTEGER NOT NULL, -- Note的排序，一般最后一个是sin\n    note_stack_depth INTEGER,       -- 函数调用栈深度\n    note_is_key_event INTEGER,      -- is in main source file\n    __note_id INTEGER NOT NULL REFERENCES Note(id) ON DELETE CASCADE, -- 外键\n\n  -- 约束\n    PRIMARY KEY (__note_array_hash_id, note_sequence, note_stack_depth, note_is_key_event, __note_id) -- 设定复合主键确保唯一\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS Region( -- 位置区间\n  -- Key\n    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 唯一标识\n                                                   -- 被 Note、ControlFlow 表关联\n\n  -- Value\n    __file_id    INTEGER NOT NULL REFERENCES File ON DELETE CASCADE,\n    start_line   INTEGER NOT NULL,\n    start_column INTEGER,\n    end_line     INTEGER,\n    end_column   INTEGER,\n\n  -- 约束\n    UNIQUE (__file_id, start_line, start_column, end_line, end_column) -- 不要加content做UNIQUE,性能消耗高\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS Rule(\n  -- Key\n    name TEXT PRIMARY KEY NOT NULL, -- 主键，唯一：规则id: misra-c2012-R.10.1\n                                    -- 被 Diagnostic 表关联\n\n  -- Value\n    short_description_en TEXT NOT NULL, -- 规则最短的描述 (对应checker_info:name)\n    short_description_zh TEXT NOT NULL, -- 规则最短的描述\n    severity TEXT,             -- 评估维度1: 严重程度 (High|Medium|Low|Info)  除了misra其他不能为空\n    priority TEXT,             -- 评估维度2: 修复优先级 (Advisory|Required|Mandatory|...不限) misra等等特有，其他语言为空\n    language TEXT NOT NULL,    -- (C/C++|Java|..)\n\n    precision TEXT,       -- 平均精确率 (Highest|High|Medium|Low|Lowest)\n    recall TEXT,          -- 平均召回率 (Highest|High|Medium|Low|Lowest)\n    likelihood TEXT,      -- 可利用性, 综合的漏洞被利用可能性 (Highest|High|Medium|Low|Lowest)\n    impact TEXT,          -- 影响程度, 综合地指出漏洞可以造成多大的损害 (Highest|High|Medium|Low|Lowest)\n    technique TEXT,       -- 实现原理，支持多个，逗号','分割, 需要先排序 (SA|AST|DataFlow|CG|CPG|...不限)\n    analysis_scope TEXT,  -- 分析范围，支持多个，逗号','分割, 需要先排序 (TU|System|...不限)\n    performance TEXT,     -- 综合分析效率，越高越好 (Highest|High|Medium|Low|Lowest)\n    configurable INTEGER, -- 是否可自定义配置 0 | 1\n    implemented INTEGER,  -- 该规则是否已有实现 0 | 1\n\n    static_analyzability TEXT, -- 可分析性 (automated|partially-automated|non-automated)\n    c_allocated_target TEXT,   -- (implementation|verification|toolchain|infrastructure) C语言特有\n\n    category_en    TEXT NOT NULL, -- 规则所属类别 (对应checker_info:category)\n    category_zh    TEXT NOT NULL, -- 规则所属类别\n\n    rule_sort_number INTEGER,     -- 用于排序规则的编号（此规则对应在排序后的规则列表中的一个index）。\n                                  -- number从小到大对应chapter从上到下，这里暂时没做去重约束，最好不要重复，如果引擎暂时没有实现排序，可以不填。\n\n    chapter_name_1 TEXT,          -- 引擎自定义内容 (例如Java引擎这里设定chapter_name_1为category_zh)\n    chapter_name_2 TEXT,\n    chapter_name_3 TEXT,\n    chapter_name_4 TEXT,\n\n    description_en TEXT NOT NULL, -- 规则的简单描述 (对应checker_info:abstract)\n    description_zh TEXT,          -- 规则的简单描述\n    document_en TEXT NOT NULL,    -- markdown\n    document_zh TEXT NOT NULL     -- markdown\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS RuleMapping (\n   rule_name TEXT NOT NULL REFERENCES Rule(name) ON DELETE CASCADE, -- Rule feysh.java.xxx.XxxxXxxx\n\n   standard_name TEXT, -- (CWE|CERT-J)\n   standard_rule TEXT,  -- 大小写敏感, 多个rule不要拼接到一起，一个映射只对应一条RuleMapping\n\n  -- 约束\n    UNIQUE (rule_name, standard_name, standard_rule)\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS RuleSetInfo(\n  -- Key\n    name TEXT PRIMARY KEY NOT NULL, -- 主键，唯一：MISRAC2012\n\n  -- Value\n    language TEXT NOT NULL,         -- (C/C++|Java|Mix|..) 混合语言则为Mix\n    description TEXT,               -- MISRA C:2012\n    prefix TEXT,                    -- misra-c2012\n    id_pattern TEXT,                -- ([DR])\\.([0-9]+)\\.([0-9]+)\n    section_level INTEGER,          -- #groups in ID_PATTERN: 3\n    version TEXT NOT NULL,          -- release_4.0.1\n    revision TEXT NOT NULL          -- f82a51290f288b715356ad496bb938fdc86a8790\n)",
            0,
            null,
            8,
            null
         );
         DefaultImpls.execute$default(
            driver,
            null,
            "CREATE TABLE IF NOT EXISTS SchemaInfo(\n  -- Key\n    key TEXT PRIMARY KEY NOT NULL, -- 主键，唯一：CLANG_TIDY_VERSION\n\n  -- Value\n    value TEXT NOT NULL -- release_4.1.0\n)",
            0,
            null,
            8,
            null
         );
         return QueryResult.Companion.getUnit-mlR-ZEE();
      }

      public open fun migrate(driver: SqlDriver, oldVersion: Long, newVersion: Long, vararg callbacks: AfterVersion): Value<Unit> {
         return QueryResult.Companion.getUnit-mlR-ZEE();
      }
   }
}
