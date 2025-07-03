
public final class FySastCli extends CliktCommand {
    /* ---------- Option Delegates / 简写示例 ----------
       真实项目里通常是：
       val verbosity by option("--verbosity").enum<Level>().default(Level.INFO)
       这里用 lazy / Delegates.notNull() 站位，保持可编译
    */
    val verbosity: Level by lazy { Level.INFO }

    val config: String by lazy { "" }
    val rules: List<String> by lazy { emptyList() }

    val defaultOutput: IResFile by lazy { TODO("init defaultOutput") }
    val output: IResFile by lazy { defaultOutput }

    val dumpSootScene: Boolean by lazy { false }
    val resultType: Set<String> by lazy { emptySet() }
    val preferredLanguages: List<String> by lazy { listOf("EN", "ZH") }

    var enableDecompile: Boolean = false

    val enableCodeMetrics: Boolean by lazy { true }
    val dumpSrcFileList: String? by lazy { null }

    /* ---------- 其余几十个选项同理 ↓ ---------- */

    val target: TargetOptions? by lazy { null }
    val process: List<String> by lazy { emptyList() }
    val classPath: List<String> by lazy { emptyList() }

    val autoAppClasses: List<String> by lazy { emptyList() }
    val autoAppTraverseMode: FileSystemLocator.TraverseMode by lazy { FileSystemLocator.TraverseMode.BFS }
    val autoAppSrcOnlyFileScheme: Boolean by lazy { false }

    val disableDefaultJavaClassPath: Boolean by lazy { false }
    val sourcePath: Set<String> by lazy { emptySet() }
    val projectRoot: List<String> by lazy { emptyList() }

    val srcPrecedence: SrcPrecedence by lazy { SrcPrecedence.HIGH }
    val incrementalScanOf: List<String> by lazy { emptyList() }
    val disableMappingDiffInArchive: Boolean by lazy { false }

    val sunBootClassPath: String? by lazy { null }
    val javaExtDirs: String? by lazy { null }
    val ecjOptions: List<String> by lazy { emptyList() }

    val serializeCG: Boolean by lazy { false }
    val c2sMode: AbstractFileIndexer.CompareMode by lazy { AbstractFileIndexer.CompareMode.C1_EQUALS_C2 }
    val hideNoSource: Boolean by lazy { false }

    val traverseMode: FileSystemLocator.TraverseMode by lazy { FileSystemLocator.TraverseMode.DFS }
    val projectScanConfig: java.io.File? by lazy { null }

    val disableWrapper: Boolean by lazy { false }
    val apponly: Boolean by lazy { false }
    val disablePreAnalysis: Boolean by lazy { false }
    val disableBuiltInAnalysis: Boolean by lazy { false }

    val dataFlowOptions: DataFlowOptions by lazy { DataFlowOptions() }
    val checkerInfoGeneratorOptions: CheckerInfoGeneratorOptions by lazy { CheckerInfoGeneratorOptions() }
    val checkerInfoCompareOptions: CheckerInfoCompareOptions by lazy { CheckerInfoCompareOptions() }
    val subtoolsOptions: SubToolsOptions by lazy { SubToolsOptions() }
    val flowDroidOptions: FlowDroidOptions by lazy { FlowDroidOptions() }
    val utAnalyzeOptions: UtAnalyzeOptions by lazy { UtAnalyzeOptions() }

    val enableStructureAnalysis: Boolean by lazy { false }
    val enableOriginalNames: Boolean by lazy { false }
    val staticFieldTrackingMode: StaticFieldTrackingMode by lazy { StaticFieldTrackingMode.ContextFlowSensitive }

    val callGraphAlgorithm: String by lazy { "SPARK" }
    val callGraphAlgorithmBuiltIn: String by lazy { "AUTO" }
    val disableReflection: Boolean by lazy { false }

    val maxThreadNum: Int by lazy { Runtime.getRuntime().availableProcessors() }
    val memoryThreshold: Double by lazy { 0.9 }

    val disableTop: Boolean by lazy { false }
    val strict: Boolean by lazy { false }

    val zipFSEnv: Map<String, String> by lazy { emptyMap() }
    val zipFSEncodings: List<String> by lazy { listOf("UTF-8") }

    val hashVersion: Int? by lazy { null }
    val sourceEncoding: Charset by lazy { Charsets.UTF_8 }

    val makeScorecard: Boolean by lazy { false }
    val timeout: Int by lazy { 0 }   // seconds

    /* ---------- 运行期字段 ---------- */

    lateinit var collectors: List<IResultCollector>
    var anchorPointFile: IResFile? = null
    var lastResult: ResultCollector? = null
    val sqliteFileIndexes: MutableSet<IResFile> = LinkedHashSet()

    /* ---------- Companion ---------- */

    companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }

    /* ---------- 站位类型声明 ---------- */
    // 以下类型在你项目中已存在；此处仅占位以便代码片段可独立编译
    class TargetOptions
    class DataFlowOptions
    class CheckerInfoGeneratorOptions
    class CheckerInfoCompareOptions
    class SubToolsOptions
    class FlowDroidOptions
    class UtAnalyzeOptions
    enum class SrcPrecedence { HIGH, LOW }
    enum class StaticFieldTrackingMode { ContextFlowSensitive }
    class FileSystemLocator { enum class TraverseMode { DFS, BFS } }
    interface IResultCollector
    class ResultCollector
    abstract class AbstractFileIndexer { enum class CompareMode { C1_EQUALS_C2 } }


    /* loaded from: FySastCli$WhenMappings.class */
    object WhenMappings {
        /** Kotlin 编译器生成的 `tableswitch` 备份映射 */
        @JvmField
        val ENUM_SWITCH_MAPPING_0: IntArray = IntArray(ResultType.values().size).apply {
            try {
                this[ResultType.PLIST.ordinal] = 1
            } catch (_: NoSuchFieldError) { }

            try {
                this[ResultType.SARIF.ordinal] = 2
            } catch (_: NoSuchFieldError) { }

            try {
                this[ResultType.SQLITE.ordinal] = 3
            } catch (_: NoSuchFieldError) { }

            try {
                this[ResultType.SarifPackSrc.ordinal] = 4
            } catch (_: NoSuchFieldError) { }

            try {
                this[ResultType.SarifCopySrc.ordinal] = 5
            } catch (_: NoSuchFieldError) { }

            try {
                this[ResultType.COUNTER.ordinal] = 6
            } catch (_: NoSuchFieldError) { }
        }
    }

    suspend fun getFilteredJavaSourceFiles(
        mainConfig: MainConfig?,
        locator: FileLocator?,
    ): List<Path> {
        // TODO: 填入原始协程逻辑
        return emptyList()
    }

    static final class C00031 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        /* synthetic */ Object result;
        int label;

        C00031(Continuation<? super C00031> $completion) {
        super($completion);
    }

        @Nullable
        public final Object invokeSuspend(@NotNull Object $result) {
        this.result = $result;
        this.label |= Integer.MIN_VALUE;
        return FySastCli.this.runAnalyze(null, null, (Continuation) this);
    }
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = 3, xi = 48)
    @DebugMetadata(f = "FySastCli.kt", l = {909}, i = {}, s = {}, n = {}, m = "runCodeMetrics", c = "cn.sast.cli.command.FySastCli")
    /* renamed from: cn.sast.cli.command.FySastCli$runCodeMetrics$1, reason: invalid class name and case insensitive filesystem */
    /* loaded from: FySastCli$runCodeMetrics$1.class */
    static final class C00051 extends ContinuationImpl {
        /* synthetic */ Object result;
        int label;

        C00051(Continuation<? super C00051> $completion) {
        super($completion);
    }

        @Nullable
        public final Object invokeSuspend(@NotNull Object $result) {
        this.result = $result;
        this.label |= Integer.MIN_VALUE;
        return FySastCli.this.runCodeMetrics(null, null, (Continuation) this);
    }
    }

    private static /* synthetic */ void getProjectRoot$annotations() {
    }

    public FySastCli() {
        super((String) null, (String) null, "CoraxJava", false, false, (Map) null, (String) null, false, false, false, 1019, (DefaultConstructorMarker) null);
        SAXReaderBugTest.INSTANCE.test();
        final FySastCli $this$versionOption_u24default$iv = this;
        final String version$iv = ApplicationKt.getVersion();
        Set names$iv = SetsKt.setOf("--version");
        EagerOptionKt.eagerOption$default($this$versionOption_u24default$iv, names$iv, "Show the version and exit", false, (Map) null, (String) null, new Function1<OptionTransformContext, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$versionOption$default$1
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(1);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.PrintMessage */
        public final void invoke(@NotNull OptionTransformContext $this$eagerOption) throws PrintMessage {
            Intrinsics.checkNotNullParameter($this$eagerOption, "$this$eagerOption");
            String it = version$iv;
            throw new PrintMessage($this$versionOption_u24default$iv.getCommandName() + " version " + it, 0, false, 6, (DefaultConstructorMarker) null);
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1) throws PrintMessage {
            invoke((OptionTransformContext) p1);
            return Unit.INSTANCE;
        }
    }, 28, (Object) null);
        CliktCommandKt.context(this, FySastCli::_init_$lambda$1);
        OptionWithValues $this$enum_u24default$iv = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets verbosity level of command line interface", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        Enum[] enumArrValues = Level.values();
        int capacity$iv$iv = RangesKt.coerceAtLeast(MapsKt.mapCapacity(enumArrValues.length), 16);
        Map destination$iv$iv$iv = new LinkedHashMap(capacity$iv$iv);
        for (Enum r0 : enumArrValues) {
        Enum it$iv = r0;
        destination$iv$iv$iv.put(it$iv.name(), r0);
    }
        this.verbosity$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv, destination$iv$iv$iv, (String) null, true, 2, (Object) null), Level.INFO, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[0]);
        this.config$delegate = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the configuration jar and portal name which will be used as the analysis configuration. eg: \"default-config.yml@{path to analysis-config}\"\nThe environment variable: CORAX_CONFIG_DEFAULT_DIR", "(custom-config.yml@)?{configPath}[{pathSeparator}{configPath}]*", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[1]);
        final OptionWithValues $this$convert_u24default$iv = OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--rules", "--enable-checkers"}, "A way to directly control the checker switch through this parameter (whitelist).", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        final String metavar$iv = "(JSON_FILE|CHECKER_TYPE,...)";
        Function1 metavar$iv$iv$iv = new Function1<Context, String>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$1
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(1);
        }

        @NotNull
        public final String invoke(@NotNull Context $this$convert) {
            Intrinsics.checkNotNullParameter($this$convert, "$this$convert");
            return metavar$iv;
        }
    };
        Function2 valueTransform$iv$iv$iv = new Function2<OptionCallTransformContext, String, List<? extends String>>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$2
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(2);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.UsageError */
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:21:0x00a0 A[Catch: UsageError -> 0x01a2, Exception -> 0x01f3, TryCatch #3 {UsageError -> 0x01a2, Exception -> 0x01f3, blocks: (B:3:0x000c, B:4:0x0027, B:6:0x0042, B:18:0x006d, B:21:0x00a0, B:22:0x00f2, B:24:0x00fc, B:25:0x012b, B:26:0x0156, B:28:0x0160, B:34:0x0189, B:35:0x0196), top: B:63:0x000c }] */
        /* JADX WARN: Removed duplicated region for block: B:9:0x0050  */
        /* JADX WARN: Type inference failed for: r0v78, types: [java.util.List] */
        /* JADX WARN: Type inference failed for: r2v12, types: [cn.sast.cli.command.FySastCli$rules$2$2$1] */
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.util.List<? extends java.lang.String> invoke(@org.jetbrains.annotations.NotNull com.github.ajalt.clikt.parameters.options.OptionCallTransformContext r8, @org.jetbrains.annotations.NotNull java.lang.String r9) throws com.github.ajalt.clikt.core.UsageError, kotlin.KotlinNothingValueException {
            /*
                Method dump skipped, instructions count: 529
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$2.invoke(com.github.ajalt.clikt.parameters.options.OptionCallTransformContext, java.lang.String):java.lang.Object");
        }
    };
        Function2 function2DefaultEachProcessor = OptionWithValuesKt.defaultEachProcessor();
        Function2 function2DefaultAllProcessor = OptionWithValuesKt.defaultAllProcessor();
        Function2 function2DefaultValidator = OptionWithValuesKt.defaultValidator();
        Function1 metavarGetter = $this$convert_u24default$iv.getMetavarGetter();
        metavarGetter = metavarGetter == null ? metavar$iv$iv$iv : metavarGetter;
        CompletionCandidates explicitCompletionCandidates = $this$convert_u24default$iv.getExplicitCompletionCandidates();
        this.rules$delegate = OptionWithValues.DefaultImpls.copy$default($this$convert_u24default$iv, valueTransform$iv$iv$iv, function2DefaultEachProcessor, function2DefaultAllProcessor, function2DefaultValidator, (Set) null, metavarGetter, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, explicitCompletionCandidates == null ? null : explicitCompletionCandidates, (Set) null, false, false, false, 253904, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[2]);
        this.defaultOutput$delegate = LazyKt.lazy(FySastCli::defaultOutput_delegate$lambda$7);
        final OptionWithValues $this$convert_u24default$iv2 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets output directory of analysis result and metadata", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        final String metavar$iv2 = "directory";
        Function1 metavar$iv$iv$iv2 = new Function1<Context, String>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$3
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(1);
        }

        @NotNull
        public final String invoke(@NotNull Context $this$convert) {
            Intrinsics.checkNotNullParameter($this$convert, "$this$convert");
            return metavar$iv2;
        }
    };
        Function2 valueTransform$iv$iv$iv2 = new Function2<OptionCallTransformContext, String, IResDirectory>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$4
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(2);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.UsageError */
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        @NotNull
        public final IResDirectory invoke(@NotNull OptionCallTransformContext $this$null, @NotNull String it) throws UsageError, KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$null, "$this$null");
            Intrinsics.checkNotNullParameter(it, "it");
            try {
                return Resource.INSTANCE.dirOf((String) $this$convert_u24default$iv2.getTransformValue().invoke($this$null, it));
            } catch (UsageError err) {
                UsageError usageError = err;
                String paramName = err.getParamName();
                if (paramName == null) {
                    String n = $this$null.getName();
                    usageError = usageError;
                    paramName = !(n.length() == 0) ? n : null;
                    if (paramName == null) {
                        paramName = OptionKt.longestName($this$null.getOption());
                    }
                }
                usageError.setParamName(paramName);
                throw err;
            } catch (Exception err2) {
                String message = err2.getMessage();
                if (message == null) {
                    message = "";
                }
                $this$null.fail(message);
                throw new KotlinNothingValueException();
            }
        }
    };
        Function2 function2DefaultEachProcessor2 = OptionWithValuesKt.defaultEachProcessor();
        Function2 function2DefaultAllProcessor2 = OptionWithValuesKt.defaultAllProcessor();
        Function2 function2DefaultValidator2 = OptionWithValuesKt.defaultValidator();
        Function1 metavarGetter2 = $this$convert_u24default$iv2.getMetavarGetter();
        metavarGetter2 = metavarGetter2 == null ? metavar$iv$iv$iv2 : metavarGetter2;
        CompletionCandidates explicitCompletionCandidates2 = $this$convert_u24default$iv2.getExplicitCompletionCandidates();
        OptionWithValues $this$check$iv = OptionWithValuesKt.default$default(OptionWithValues.DefaultImpls.copy$default($this$convert_u24default$iv2, valueTransform$iv$iv$iv2, function2DefaultEachProcessor2, function2DefaultAllProcessor2, function2DefaultValidator2, (Set) null, metavarGetter2, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, explicitCompletionCandidates2 == null ? null : explicitCompletionCandidates2, (Set) null, false, false, false, 253904, (Object) null), getDefaultOutput(), (String) null, 2, (Object) null);
        this.output$delegate = OptionWithValues.DefaultImpls.copy$default($this$check$iv, $this$check$iv.getTransformValue(), $this$check$iv.getTransformEach(), $this$check$iv.getTransformAll(), new Function2<OptionTransformContext, IResDirectory, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$1
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, IResDirectory iResDirectory) throws KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (iResDirectory != null) {
                IResDirectory it = iResDirectory;
                Path absolutePath = it.getPath().toAbsolutePath();
                Intrinsics.checkNotNullExpressionValue(absolutePath, "toAbsolutePath(...)");
                Path p = absolutePath.normalize();
                boolean value$iv = p.getNameCount() >= 2;
                if (value$iv) {
                    return;
                }
                IResDirectory it2 = iResDirectory;
                $this$copy.fail("output is not allow here: " + it2);
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (IResDirectory) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[3]);
        this.dumpSootScene$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "dump soot scene", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[4]);
        OptionWithValues $this$enum_u24default$iv2 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets output format of analysis result. \nThis can be set multiple times, then different format of output will be given simultaneously.\nEg: --result-type sqlite --result-type sarif", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        ResultType[] resultTypeArrValues = ResultType.values();
        int capacity$iv$iv2 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(resultTypeArrValues.length), 16);
        Map destination$iv$iv$iv2 = new LinkedHashMap(capacity$iv$iv2);
        for (ResultType resultType : resultTypeArrValues) {
        ResultType it$iv2 = resultType;
        destination$iv$iv$iv2.put(it$iv2.name(), resultType);
    }
        this.resultType$delegate = OptionWithValuesKt.unique(OptionWithValuesKt.multiple$default(ChoiceKt.choice$default($this$enum_u24default$iv2, destination$iv$iv$iv2, (String) null, true, 2, (Object) null), (List) null, false, 3, (Object) null)).provideDelegate((ParameterHolder) this, $$delegatedProperties[5]);
        OptionWithValues $this$enum_u24default$iv3 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--preferred-lang", "--preferred-language"}, "Preferred reporting language", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        Language[] languageArrValues = Language.values();
        int capacity$iv$iv3 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(languageArrValues.length), 16);
        Map destination$iv$iv$iv3 = new LinkedHashMap(capacity$iv$iv3);
        for (Language language : languageArrValues) {
        Language it$iv3 = language;
        destination$iv$iv$iv3.put(it$iv3.name(), language);
    }
        this.preferredLanguages$delegate = OptionWithValuesKt.multiple$default(ChoiceKt.choice$default($this$enum_u24default$iv3, destination$iv$iv$iv3, (String) null, true, 2, (Object) null), CollectionsKt.listOf(new Language[]{Language.ZH, Language.EN}), false, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[6]);
        this.enableCodeMetrics$delegate = FlagOptionKt.flag(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--enable-code-metrics"}, "enable code metrics for the project", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[]{"--disable-code-metrics"}, false, "enable").provideDelegate((ParameterHolder) this, $$delegatedProperties[7]);
        this.dumpSrcFileList$delegate = OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--dump-src-files"}, "Write source file list with \\n separator", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[8]);
        this.target$delegate = ChoiceGroupKt.groupChoice(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the analyze target. [src-only: analyze resources without class files]\nWarning: Only corresponding target options are valid and others are ignored.", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new Pair[]{TuplesKt.to("java", new JavaOptions()), TuplesKt.to("android", new AndroidOptions()), TuplesKt.to("src-only", new SrcAnalyzeOptions())}).provideDelegate(this, $$delegatedProperties[9]);
        OptionWithValues $this$check_u24default$iv = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the classes that shall be processed(analyzed).", "class dir, [dir of] jar|war|apk|dex, glob pattern, inside zip", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), (List) null, false, 1, (Object) null);
        this.process$delegate = OptionWithValues.DefaultImpls.copy$default($this$check_u24default$iv, $this$check_u24default$iv.getTransformValue(), $this$check_u24default$iv.getTransformEach(), $this$check_u24default$iv.getTransformAll(), new Function2<OptionTransformContext, List<? extends String>, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$default$1
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, List<? extends String> list) throws KotlinNothingValueException {
            boolean z;
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (list != null) {
                List<? extends String> list2 = list;
                List<? extends String> $this$all$iv = list2;
                if (!($this$all$iv instanceof Collection) || !$this$all$iv.isEmpty()) {
                    Iterator it = $this$all$iv.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = true;
                            break;
                        }
                        Object element$iv = it.next();
                        String it2 = (String) element$iv;
                        if (!(it2.length() > 0)) {
                            z = false;
                            break;
                        }
                    }
                } else {
                    z = true;
                }
                boolean value$iv = z;
                if (value$iv) {
                    return;
                }
                $this$copy.fail(list.toString());
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (List<? extends String>) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[10]);
        OptionWithValues $this$check_u24default$iv2 = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the [JAR/CLASS/SOURCE] paths.\nHint: There are library classes and system classes in a project.\nSpecify the \"--process\" with application classes to exclude them.", "class dir, [dir of] jar|dex, glob pattern, inside zip", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), (List) null, false, 1, (Object) null);
        this.classPath$delegate = OptionWithValues.DefaultImpls.copy$default($this$check_u24default$iv2, $this$check_u24default$iv2.getTransformValue(), $this$check_u24default$iv2.getTransformEach(), $this$check_u24default$iv2.getTransformAll(), new Function2<OptionTransformContext, List<? extends String>, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$default$2
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, List<? extends String> list) throws KotlinNothingValueException {
            boolean z;
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (list != null) {
                List<? extends String> list2 = list;
                List<? extends String> $this$all$iv = list2;
                if (!($this$all$iv instanceof Collection) || !$this$all$iv.isEmpty()) {
                    Iterator it = $this$all$iv.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = true;
                            break;
                        }
                        Object element$iv = it.next();
                        String it2 = (String) element$iv;
                        if (!(it2.length() > 0)) {
                            z = false;
                            break;
                        }
                    }
                } else {
                    z = true;
                }
                boolean value$iv = z;
                if (value$iv) {
                    return;
                }
                $this$copy.fail(list.toString());
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (List<? extends String>) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[11]);
        OptionWithValues $this$check_u24default$iv3 = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "The automatically classified classes from the specified paths", "target project root dir (Contains both binary and corresponding complete project source code)", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), (List) null, false, 1, (Object) null);
        this.autoAppClasses$delegate = OptionWithValues.DefaultImpls.copy$default($this$check_u24default$iv3, $this$check_u24default$iv3.getTransformValue(), $this$check_u24default$iv3.getTransformEach(), $this$check_u24default$iv3.getTransformAll(), new Function2<OptionTransformContext, List<? extends String>, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$default$3
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, List<? extends String> list) throws KotlinNothingValueException {
            boolean z;
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (list != null) {
                List<? extends String> list2 = list;
                List<? extends String> $this$all$iv = list2;
                if (!($this$all$iv instanceof Collection) || !$this$all$iv.isEmpty()) {
                    Iterator it = $this$all$iv.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = true;
                            break;
                        }
                        Object element$iv = it.next();
                        String it2 = (String) element$iv;
                        if (!(it2.length() > 0)) {
                            z = false;
                            break;
                        }
                    }
                } else {
                    z = true;
                }
                boolean value$iv = z;
                if (value$iv) {
                    return;
                }
                $this$copy.fail(list.toString());
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (List<? extends String>) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[12]);
        OptionWithValues $this$enum_u24default$iv4 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Set the locator mode for automatically loading project resources. ", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        FileSystemLocator.TraverseMode[] traverseModeArrValues = FileSystemLocator.TraverseMode.values();
        int capacity$iv$iv4 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(traverseModeArrValues.length), 16);
        Map destination$iv$iv$iv4 = new LinkedHashMap(capacity$iv$iv4);
        for (FileSystemLocator.TraverseMode traverseMode : traverseModeArrValues) {
        FileSystemLocator.TraverseMode it$iv4 = traverseMode;
        destination$iv$iv$iv4.put(it$iv4.name(), traverseMode);
    }
        this.autoAppTraverseMode$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv4, destination$iv$iv$iv4, (String) null, true, 2, (Object) null), FileSystemLocator.TraverseMode.RecursivelyIndexArchive, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[13]);
        this.autoAppSrcOnlyFileScheme$delegate = FlagOptionKt.flag(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--auto-app-src-only-file-scheme"}, "If true, the Java source file which including the ZIP is judged to not be the application class.", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[]{"--disable-auto-app-src-only-file-scheme"}, true, "enable").provideDelegate((ParameterHolder) this, $$delegatedProperties[14]);
        this.disableDefaultJavaClassPath$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Disable the default jdk/jre path for class path.\nThen a custom java class path should be given by \"--class-path\".", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[15]);
        final OptionWithValues $this$convert_u24default$iv3 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the source file path with source directory root or source.jar file", "source file dir or any parent dir", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null);
        final String metavar$iv3 = "PATH";
        Function1 metavar$iv$iv$iv3 = new Function1<Context, String>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$5
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(1);
        }

        @NotNull
        public final String invoke(@NotNull Context $this$convert) {
            Intrinsics.checkNotNullParameter($this$convert, "$this$convert");
            return metavar$iv3;
        }
    };
        Function2 valueTransform$iv$iv$iv3 = new Function2<OptionCallTransformContext, String, IResDirectory>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$6
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(2);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.UsageError */
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        @NotNull
        public final IResDirectory invoke(@NotNull OptionCallTransformContext $this$null, @NotNull String it) throws UsageError, KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$null, "$this$null");
            Intrinsics.checkNotNullParameter(it, "it");
            try {
                String it2 = (String) $this$convert_u24default$iv3.getTransformValue().invoke($this$null, it);
                if (it2.length() > 0) {
                    return Resource.INSTANCE.dirOf(it2);
                }
                throw new IllegalStateException("Check failed.".toString());
            } catch (UsageError err) {
                UsageError usageError = err;
                String paramName = err.getParamName();
                if (paramName == null) {
                    String n = $this$null.getName();
                    usageError = usageError;
                    paramName = !(n.length() == 0) ? n : null;
                    if (paramName == null) {
                        paramName = OptionKt.longestName($this$null.getOption());
                    }
                }
                usageError.setParamName(paramName);
                throw err;
            } catch (Exception err2) {
                String message = err2.getMessage();
                if (message == null) {
                    message = "";
                }
                $this$null.fail(message);
                throw new KotlinNothingValueException();
            }
        }
    };
        Function2 function2DefaultEachProcessor3 = OptionWithValuesKt.defaultEachProcessor();
        Function2 function2DefaultAllProcessor3 = OptionWithValuesKt.defaultAllProcessor();
        Function2 function2DefaultValidator3 = OptionWithValuesKt.defaultValidator();
        Function1 metavarGetter3 = $this$convert_u24default$iv3.getMetavarGetter();
        metavarGetter3 = metavarGetter3 == null ? metavar$iv$iv$iv3 : metavarGetter3;
        CompletionCandidates explicitCompletionCandidates3 = $this$convert_u24default$iv3.getExplicitCompletionCandidates();
        this.sourcePath$delegate = OptionWithValuesKt.unique(OptionWithValuesKt.multiple$default(OptionWithValues.DefaultImpls.copy$default($this$convert_u24default$iv3, valueTransform$iv$iv$iv3, function2DefaultEachProcessor3, function2DefaultAllProcessor3, function2DefaultValidator3, (Set) null, metavarGetter3, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, explicitCompletionCandidates3 == null ? null : explicitCompletionCandidates3, (Set) null, false, false, false, 253904, (Object) null), (List) null, false, 3, (Object) null)).provideDelegate((ParameterHolder) this, $$delegatedProperties[16]);
        this.projectRoot$delegate = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Specify the project root directory", "dir", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), (List) null, false, 3, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[17]);
        OptionWithValues $this$enum_u24default$iv5 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets the source precedence type of analysis targets", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        SrcPrecedence[] srcPrecedenceArrValues = SrcPrecedence.values();
        int capacity$iv$iv5 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(srcPrecedenceArrValues.length), 16);
        Map destination$iv$iv$iv5 = new LinkedHashMap(capacity$iv$iv5);
        for (SrcPrecedence srcPrecedence : srcPrecedenceArrValues) {
        SrcPrecedence it$iv5 = srcPrecedence;
        destination$iv$iv$iv5.put(it$iv5.name(), srcPrecedence);
    }
        this.srcPrecedence$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv5, destination$iv$iv$iv5, (String) null, true, 2, (Object) null), SrcPrecedence.prec_apk_class_jimple, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[18]);
        this.incrementalScanOf$delegate = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"-i", "--incremental", "--incremental-base"}, "Specify the git .diff/.patch file or a list of change file for incremental analysis", ".diff/.patch/.txt", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), (List) null, false, 3, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[19]);
        this.disableMappingDiffInArchive$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "In incremental analysis, is the process of mapping diff paths to the corresponding files within compressed archives?.", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[20]);
        OptionWithValues optionWithValuesOption$default = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, true, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null);
        String property = System.getProperty("sun.boot.class.path");
        this.sunBootClassPath$delegate = OptionWithValuesKt.default$default(optionWithValuesOption$default, property == null ? "" : property, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[21]);
        OptionWithValues optionWithValuesOption$default2 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, true, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null);
        String property2 = System.getProperty("java.ext.dirs");
        this.javaExtDirs$delegate = OptionWithValuesKt.default$default(optionWithValuesOption$default2, property2 == null ? "" : property2, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[22]);
        final OptionWithValues $this$convert_u24default$iv4 = FileKt.file$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets custom ecj options file", "file path", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), true, false, false, false, false, false, 58, (Object) null);
        final String metavar$iv4 = "JSON_FILE";
        Function1 metavar$iv$iv$iv4 = new Function1<Context, String>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$7
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(1);
        }

        @NotNull
        public final String invoke(@NotNull Context $this$convert) {
            Intrinsics.checkNotNullParameter($this$convert, "$this$convert");
            return metavar$iv4;
        }
    };
        Function2 valueTransform$iv$iv$iv4 = new Function2<OptionCallTransformContext, String, List<? extends String>>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$8
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(2);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.UsageError */
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        /* JADX WARN: Type inference failed for: r2v5, types: [cn.sast.cli.command.FySastCli$ecjOptions$2$1] */
        @NotNull
        public final List<? extends String> invoke(@NotNull OptionCallTransformContext $this$null, @NotNull String it) throws UsageError, KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$null, "$this$null");
            Intrinsics.checkNotNullParameter(it, "it");
            try {
                return (List) new Gson().fromJson(FilesKt.readText$default((File) $this$convert_u24default$iv4.getTransformValue().invoke($this$null, it), (Charset) null, 1, (Object) null), new TypeToken<List<? extends String>>() { // from class: cn.sast.cli.command.FySastCli$ecjOptions$2$1
                }.getType());
            } catch (UsageError err) {
                UsageError usageError = err;
                String paramName = err.getParamName();
                if (paramName == null) {
                    String n = $this$null.getName();
                    usageError = usageError;
                    paramName = !(n.length() == 0) ? n : null;
                    if (paramName == null) {
                        paramName = OptionKt.longestName($this$null.getOption());
                    }
                }
                usageError.setParamName(paramName);
                throw err;
            } catch (Exception err2) {
                String message = err2.getMessage();
                if (message == null) {
                    message = "";
                }
                $this$null.fail(message);
                throw new KotlinNothingValueException();
            }
        }
    };
        Function2 function2DefaultEachProcessor4 = OptionWithValuesKt.defaultEachProcessor();
        Function2 function2DefaultAllProcessor4 = OptionWithValuesKt.defaultAllProcessor();
        Function2 function2DefaultValidator4 = OptionWithValuesKt.defaultValidator();
        Function1 metavarGetter4 = $this$convert_u24default$iv4.getMetavarGetter();
        metavarGetter4 = metavarGetter4 == null ? metavar$iv$iv$iv4 : metavarGetter4;
        CompletionCandidates explicitCompletionCandidates4 = $this$convert_u24default$iv4.getExplicitCompletionCandidates();
        this.ecjOptions$delegate = OptionWithValues.DefaultImpls.copy$default($this$convert_u24default$iv4, valueTransform$iv$iv$iv4, function2DefaultEachProcessor4, function2DefaultAllProcessor4, function2DefaultValidator4, (Set) null, metavarGetter4, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, explicitCompletionCandidates4 == null ? null : explicitCompletionCandidates4, (Set) null, false, false, false, 253904, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[23]);
        this.serializeCG$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Serialize the on-the-fly call graph. (dot/json)", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[24]);
        OptionWithValues $this$enum_u24default$iv6 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Sets the mode for how to search source code based on class name", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        AbstractFileIndexer.CompareMode[] compareModeArrValues = AbstractFileIndexer.CompareMode.values();
        int capacity$iv$iv6 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(compareModeArrValues.length), 16);
        Map destination$iv$iv$iv6 = new LinkedHashMap(capacity$iv$iv6);
        for (AbstractFileIndexer.CompareMode compareMode : compareModeArrValues) {
        AbstractFileIndexer.CompareMode it$iv6 = compareMode;
        destination$iv$iv$iv6.put(it$iv6.name(), compareMode);
    }
        this.c2sMode$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv6, destination$iv$iv$iv6, (String) null, true, 2, (Object) null), AbstractFileIndexer.CompareMode.Class, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[25]);
        this.hideNoSource$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Set if problems found shall be hidden in the final report when source code is not available", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[26]);
        OptionWithValues $this$enum_u24default$iv7 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Set whether to find the source code file from the compressed package", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null);
        FileSystemLocator.TraverseMode[] traverseModeArrValues2 = FileSystemLocator.TraverseMode.values();
        int capacity$iv$iv7 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(traverseModeArrValues2.length), 16);
        Map destination$iv$iv$iv7 = new LinkedHashMap(capacity$iv$iv7);
        for (FileSystemLocator.TraverseMode traverseMode2 : traverseModeArrValues2) {
        FileSystemLocator.TraverseMode it$iv7 = traverseMode2;
        destination$iv$iv$iv7.put(it$iv7.name(), traverseMode2);
    }
        this.traverseMode$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv7, destination$iv$iv$iv7, (String) null, true, 2, (Object) null), FileSystemLocator.TraverseMode.RecursivelyIndexArchive, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[27]);
        this.projectScanConfig$delegate = FileKt.file$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--project-config", "--project-scan-config"}, "Specify the path of project scan config file", "file path", false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 504, (Object) null), true, false, false, false, false, false, 58, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[28]);
        this.disableWrapper$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Analyze the full frameworks together with the app without any optimizations", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, "Use summary modeling (taint wrapper, ...)", 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[29]);
        this.apponly$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--app-only", "--apponly"}, "Sets whether classes that are declared library classes in Soot shall be excluded from the analysis,\n i.e., no flows shall be tracked through them", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[30]);
        this.disablePreAnalysis$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Skip the pre analysis phase. This will disable some checkers.", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[31]);
        this.disableBuiltInAnalysis$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Skip the flow built-in analysis phase. This will disable some checkers. ", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[32]);
        this.dataFlowOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new DataFlowOptions()).provideDelegate(this, $$delegatedProperties[33]);
        this.checkerInfoGeneratorOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new CheckerInfoGeneratorOptions()).provideDelegate(this, $$delegatedProperties[34]);
        this.checkerInfoCompareOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new CheckerInfoCompareOptions()).provideDelegate(this, $$delegatedProperties[35]);
        this.subtoolsOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new SubToolsOptions()).provideDelegate(this, $$delegatedProperties[36]);
        this.flowDroidOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new FlowDroidOptions(false, 1, null)).provideDelegate(this, $$delegatedProperties[37]);
        this.utAnalyzeOptions$delegate = CoOccurringOptionGroupKt.cooccurring(new UtAnalyzeOptions()).provideDelegate(this, $$delegatedProperties[38]);
        this.enableStructureAnalysis$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 510, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[39]);
        this.enableOriginalNames$delegate = FlagOptionKt.flag(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--enable-original-names"}, "enable original names for stack local variables. ", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[]{"--disable-original-names"}, true, "enable").provideDelegate((ParameterHolder) this, $$delegatedProperties[40]);
        OptionWithValues $this$enum_u24default$iv8 = OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null);
        StaticFieldTrackingMode[] staticFieldTrackingModeArrValues = StaticFieldTrackingMode.values();
        int capacity$iv$iv8 = RangesKt.coerceAtLeast(MapsKt.mapCapacity(staticFieldTrackingModeArrValues.length), 16);
        Map destination$iv$iv$iv8 = new LinkedHashMap(capacity$iv$iv8);
        for (StaticFieldTrackingMode staticFieldTrackingMode : staticFieldTrackingModeArrValues) {
        StaticFieldTrackingMode it$iv8 = staticFieldTrackingMode;
        destination$iv$iv$iv8.put(it$iv8.name(), staticFieldTrackingMode);
    }
        this.staticFieldTrackingMode$delegate = OptionWithValuesKt.default$default(ChoiceKt.choice$default($this$enum_u24default$iv8, destination$iv$iv$iv8, (String) null, true, 2, (Object) null), StaticFieldTrackingMode.ContextFlowInsensitive, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[41]);
        this.callGraphAlgorithm$delegate = OptionWithValuesKt.default$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null), "insens", (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[42]);
        this.callGraphAlgorithmBuiltIn$delegate = OptionWithValuesKt.default$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null), "cha", (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[43]);
        this.disableReflection$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "True if reflective method calls shall be not supported, otherwise false", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 500, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[44]);
        OptionWithValues $this$check_u24default$iv4 = OptionWithValuesKt.default$default(IntKt.int$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--max-thread-num", "--thread-num", "--thread", "-j"}, (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null), false, 1, (Object) null), Integer.valueOf(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1)), (String) null, 2, (Object) null);
        this.maxThreadNum$delegate = OptionWithValues.DefaultImpls.copy$default($this$check_u24default$iv4, $this$check_u24default$iv4.getTransformValue(), $this$check_u24default$iv4.getTransformEach(), $this$check_u24default$iv4.getTransformAll(), new Function2<OptionTransformContext, Integer, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$default$4
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, Integer num) throws KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (num != null) {
                int it = num.intValue();
                boolean value$iv = it >= 1;
                if (value$iv) {
                    return;
                }
                $this$copy.fail(num.toString());
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (Integer) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[45]);
        this.memoryThreshold$delegate = OptionWithValuesKt.default$default(DoubleKt.double(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Memory threshold percentage. Negative numbers turn it off.", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 500, (Object) null)), Double.valueOf(0.9d), (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[46]);
        this.disableTop$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--disable-top"}, "interactive process viewer", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 500, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[47]);
        this.strict$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--strict"}, "Disable all the limits (eg: timeout and memory threshold)", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[48]);
        this.zipFSEnv$delegate = OptionWithValuesKt.associate$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--zipfs-env"}, (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 510, (Object) null), (String) null, 1, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[49]);
        this.zipFSEncodings$delegate = OptionWithValuesKt.multiple$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[]{"--zipfs-encodings"}, (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 510, (Object) null), Resource.INSTANCE.getFileSystemEncodings(), false, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[50]);
        OptionWithValues $this$check_u24default$iv5 = IntKt.int$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, true, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 502, (Object) null), false, 1, (Object) null);
        this.hashVersion$delegate = OptionWithValues.DefaultImpls.copy$default($this$check_u24default$iv5, $this$check_u24default$iv5.getTransformValue(), $this$check_u24default$iv5.getTransformEach(), $this$check_u24default$iv5.getTransformAll(), new Function2<OptionTransformContext, Integer, Unit>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$check$default$5
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        public final void invoke(@NotNull OptionTransformContext $this$copy, Integer num) throws KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$copy, "$this$copy");
            if (num != null) {
                int it = num.intValue();
                boolean z = 1 <= it && it < 3;
                boolean value$iv = z;
                if (value$iv) {
                    return;
                }
                $this$copy.fail(num.toString());
                throw new KotlinNothingValueException();
            }
        }

        public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2) throws KotlinNothingValueException {
            invoke((OptionTransformContext) p1, (Integer) p2);
            return Unit.INSTANCE;
        }
    }, (Set) null, (Function1) null, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, (CompletionCandidates) null, (Set) null, false, false, false, 262128, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[51]);
        final OptionWithValues $this$convert_u24default$iv5 = OptionWithValuesKt.help(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], (String) null, (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 510, (Object) null), "The encoding of source files");
        Function1 metavar$iv5 = new Function1<Context, String>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$9
        @NotNull
        public final String invoke(@NotNull Context $this$null) {
            Intrinsics.checkNotNullParameter($this$null, "$this$null");
            return $this$null.getLocalization().defaultMetavar();
        }
    };
        Function2 valueTransform$iv$iv = new Function2<OptionCallTransformContext, String, Charset>() { // from class: cn.sast.cli.command.FySastCli$special$$inlined$convert$default$10
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        {
            super(2);
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.UsageError */
        /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.KotlinNothingValueException */
        @NotNull
        public final Charset invoke(@NotNull OptionCallTransformContext $this$null, @NotNull String it) throws UsageError, KotlinNothingValueException {
            Intrinsics.checkNotNullParameter($this$null, "$this$null");
            Intrinsics.checkNotNullParameter(it, "it");
            try {
                return Charset.forName((String) $this$convert_u24default$iv5.getTransformValue().invoke($this$null, it));
            } catch (UsageError err) {
                UsageError usageError = err;
                String paramName = err.getParamName();
                if (paramName == null) {
                    String n = $this$null.getName();
                    usageError = usageError;
                    paramName = !(n.length() == 0) ? n : null;
                    if (paramName == null) {
                        paramName = OptionKt.longestName($this$null.getOption());
                    }
                }
                usageError.setParamName(paramName);
                throw err;
            } catch (Exception err2) {
                String message = err2.getMessage();
                if (message == null) {
                    message = "";
                }
                $this$null.fail(message);
                throw new KotlinNothingValueException();
            }
        }
    };
        Function2 function2DefaultEachProcessor5 = OptionWithValuesKt.defaultEachProcessor();
        Function2 function2DefaultAllProcessor5 = OptionWithValuesKt.defaultAllProcessor();
        Function2 function2DefaultValidator5 = OptionWithValuesKt.defaultValidator();
        Function1 metavarGetter5 = $this$convert_u24default$iv5.getMetavarGetter();
        metavarGetter5 = metavarGetter5 == null ? metavar$iv5 : metavarGetter5;
        CompletionCandidates explicitCompletionCandidates5 = $this$convert_u24default$iv5.getExplicitCompletionCandidates();
        this.sourceEncoding$delegate = OptionWithValuesKt.default$default(OptionWithValues.DefaultImpls.copy$default($this$convert_u24default$iv5, valueTransform$iv$iv, function2DefaultEachProcessor5, function2DefaultAllProcessor5, function2DefaultValidator5, (Set) null, metavarGetter5, (IntRange) null, (Function1) null, false, (Map) null, (String) null, (String) null, (Regex) null, explicitCompletionCandidates5 == null ? null : explicitCompletionCandidates5, (Set) null, false, false, false, 253904, (Object) null), Charsets.UTF_8, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[52]);
        this.makeScorecard$delegate = FlagOptionKt.flag$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "auto make scores for reports. ", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), new String[0], false, (String) null, 6, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[53]);
        this.timeout$delegate = OptionWithValuesKt.default$default(IntKt.int$default(OptionWithValuesKt.option$default((ParameterHolder) this, new String[0], "Set the analysis to stop after a certain number of seconds", (String) null, false, (String) null, (Map) null, (CompletionCandidates) null, (String) null, false, 508, (Object) null), false, 1, (Object) null), 0, (String) null, 2, (Object) null).provideDelegate((ParameterHolder) this, $$delegatedProperties[54]);
        this.collectors = CollectionsKt.emptyList();
        this.sqliteFileIndexes = new LinkedHashSet();
    }

    private static final MordantHelpFormatter lambda$1$lambda$0(Context it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return new MordantHelpFormatter(it, (String) null, true, true, 2, (DefaultConstructorMarker) null);
    }

    private static final Unit _init_$lambda$1(Context.Builder $this$context) {
        Intrinsics.checkNotNullParameter($this$context, "$this$context");
        $this$context.setHelpFormatter(FySastCli::lambda$1$lambda$0);
        return Unit.INSTANCE;
    }

    private final Level getVerbosity() {
        return (Level) this.verbosity$delegate.getValue(this, $$delegatedProperties[0]);
    }

    private final String getConfig() {
        return (String) this.config$delegate.getValue(this, $$delegatedProperties[1]);
    }

    private final List<String> getRules() {
        return (List) this.rules$delegate.getValue(this, $$delegatedProperties[2]);
    }

    private final IResDirectory getDefaultOutput() {
        return (IResDirectory) this.defaultOutput$delegate.getValue();
    }

    private static final IResDirectory defaultOutput_delegate$lambda$7() {
        return Resource.INSTANCE.dirOf(System.getProperty("user.dir") + File.separator + "output");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final IResDirectory getOutput() {
        return (IResDirectory) this.output$delegate.getValue(this, $$delegatedProperties[3]);
    }

    private final boolean getDumpSootScene() {
        return ((Boolean) this.dumpSootScene$delegate.getValue(this, $$delegatedProperties[4])).booleanValue();
    }

    private final Set<ResultType> getResultType() {
        return (Set) this.resultType$delegate.getValue(this, $$delegatedProperties[5]);
    }

    private final List<Language> getPreferredLanguages() {
        return (List) this.preferredLanguages$delegate.getValue(this, $$delegatedProperties[6]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean getEnableCodeMetrics() {
        return ((Boolean) this.enableCodeMetrics$delegate.getValue(this, $$delegatedProperties[7])).booleanValue();
    }

    private final String getDumpSrcFileList() {
        return (String) this.dumpSrcFileList$delegate.getValue(this, $$delegatedProperties[8]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final TargetOptions getTarget() {
        return (TargetOptions) this.target$delegate.getValue(this, $$delegatedProperties[9]);
    }

    private final List<String> getProcess() {
        return (List) this.process$delegate.getValue(this, $$delegatedProperties[10]);
    }

    private final List<String> getClassPath() {
        return (List) this.classPath$delegate.getValue(this, $$delegatedProperties[11]);
    }

    private final List<String> getAutoAppClasses() {
        return (List) this.autoAppClasses$delegate.getValue(this, $$delegatedProperties[12]);
    }

    private final FileSystemLocator.TraverseMode getAutoAppTraverseMode() {
        return (FileSystemLocator.TraverseMode) this.autoAppTraverseMode$delegate.getValue(this, $$delegatedProperties[13]);
    }

    private final boolean getAutoAppSrcOnlyFileScheme() {
        return ((Boolean) this.autoAppSrcOnlyFileScheme$delegate.getValue(this, $$delegatedProperties[14])).booleanValue();
    }

    private final boolean getDisableDefaultJavaClassPath() {
        return ((Boolean) this.disableDefaultJavaClassPath$delegate.getValue(this, $$delegatedProperties[15])).booleanValue();
    }

    private final Set<IResDirectory> getSourcePath() {
        return (Set) this.sourcePath$delegate.getValue(this, $$delegatedProperties[16]);
    }

    private final List<String> getProjectRoot() {
        return (List) this.projectRoot$delegate.getValue(this, $$delegatedProperties[17]);
    }

    private final SrcPrecedence getSrcPrecedence() {
        return (SrcPrecedence) this.srcPrecedence$delegate.getValue(this, $$delegatedProperties[18]);
    }

    private final List<String> getIncrementalScanOf() {
        return (List) this.incrementalScanOf$delegate.getValue(this, $$delegatedProperties[19]);
    }

    private final boolean getDisableMappingDiffInArchive() {
        return ((Boolean) this.disableMappingDiffInArchive$delegate.getValue(this, $$delegatedProperties[20])).booleanValue();
    }

    private final String getSunBootClassPath() {
        return (String) this.sunBootClassPath$delegate.getValue(this, $$delegatedProperties[21]);
    }

    private final String getJavaExtDirs() {
        return (String) this.javaExtDirs$delegate.getValue(this, $$delegatedProperties[22]);
    }

    private final List<String> getEcjOptions() {
        return (List) this.ecjOptions$delegate.getValue(this, $$delegatedProperties[23]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean getSerializeCG() {
        return ((Boolean) this.serializeCG$delegate.getValue(this, $$delegatedProperties[24])).booleanValue();
    }

    private final AbstractFileIndexer.CompareMode getC2sMode() {
        return (AbstractFileIndexer.CompareMode) this.c2sMode$delegate.getValue(this, $$delegatedProperties[25]);
    }

    private final boolean getHideNoSource() {
        return ((Boolean) this.hideNoSource$delegate.getValue(this, $$delegatedProperties[26])).booleanValue();
    }

    private final FileSystemLocator.TraverseMode getTraverseMode() {
        return (FileSystemLocator.TraverseMode) this.traverseMode$delegate.getValue(this, $$delegatedProperties[27]);
    }

    private final File getProjectScanConfig() {
        return (File) this.projectScanConfig$delegate.getValue(this, $$delegatedProperties[28]);
    }

    private final boolean getDisableWrapper() {
        return ((Boolean) this.disableWrapper$delegate.getValue(this, $$delegatedProperties[29])).booleanValue();
    }

    private final boolean getApponly() {
        return ((Boolean) this.apponly$delegate.getValue(this, $$delegatedProperties[30])).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean getDisablePreAnalysis() {
        return ((Boolean) this.disablePreAnalysis$delegate.getValue(this, $$delegatedProperties[31])).booleanValue();
    }

    private final boolean getDisableBuiltInAnalysis() {
        return ((Boolean) this.disableBuiltInAnalysis$delegate.getValue(this, $$delegatedProperties[32])).booleanValue();
    }

    private final DataFlowOptions getDataFlowOptions() {
        return (DataFlowOptions) this.dataFlowOptions$delegate.getValue(this, $$delegatedProperties[33]);
    }

    private final CheckerInfoGeneratorOptions getCheckerInfoGeneratorOptions() {
        return (CheckerInfoGeneratorOptions) this.checkerInfoGeneratorOptions$delegate.getValue(this, $$delegatedProperties[34]);
    }

    private final CheckerInfoCompareOptions getCheckerInfoCompareOptions() {
        return (CheckerInfoCompareOptions) this.checkerInfoCompareOptions$delegate.getValue(this, $$delegatedProperties[35]);
    }

    private final SubToolsOptions getSubtoolsOptions() {
        return (SubToolsOptions) this.subtoolsOptions$delegate.getValue(this, $$delegatedProperties[36]);
    }

    private final FlowDroidOptions getFlowDroidOptions() {
        return (FlowDroidOptions) this.flowDroidOptions$delegate.getValue(this, $$delegatedProperties[37]);
    }

    private final UtAnalyzeOptions getUtAnalyzeOptions() {
        return (UtAnalyzeOptions) this.utAnalyzeOptions$delegate.getValue(this, $$delegatedProperties[38]);
    }

    private final boolean getEnableStructureAnalysis() {
        return ((Boolean) this.enableStructureAnalysis$delegate.getValue(this, $$delegatedProperties[39])).booleanValue();
    }

    private final boolean getEnableOriginalNames() {
        return ((Boolean) this.enableOriginalNames$delegate.getValue(this, $$delegatedProperties[40])).booleanValue();
    }

    private final StaticFieldTrackingMode getStaticFieldTrackingMode() {
        return (StaticFieldTrackingMode) this.staticFieldTrackingMode$delegate.getValue(this, $$delegatedProperties[41]);
    }

    private final String getCallGraphAlgorithm() {
        return (String) this.callGraphAlgorithm$delegate.getValue(this, $$delegatedProperties[42]);
    }

    private final String getCallGraphAlgorithmBuiltIn() {
        return (String) this.callGraphAlgorithmBuiltIn$delegate.getValue(this, $$delegatedProperties[43]);
    }

    private final boolean getDisableReflection() {
        return ((Boolean) this.disableReflection$delegate.getValue(this, $$delegatedProperties[44])).booleanValue();
    }

    private final int getMaxThreadNum() {
        return ((Number) this.maxThreadNum$delegate.getValue(this, $$delegatedProperties[45])).intValue();
    }

    private final double getMemoryThreshold() {
        return ((Number) this.memoryThreshold$delegate.getValue(this, $$delegatedProperties[46])).doubleValue();
    }

    private final boolean getDisableTop() {
        return ((Boolean) this.disableTop$delegate.getValue(this, $$delegatedProperties[47])).booleanValue();
    }

    private final boolean getStrict() {
        return ((Boolean) this.strict$delegate.getValue(this, $$delegatedProperties[48])).booleanValue();
    }

    private final Map<String, String> getZipFSEnv() {
        return (Map) this.zipFSEnv$delegate.getValue(this, $$delegatedProperties[49]);
    }

    private final List<String> getZipFSEncodings() {
        return (List) this.zipFSEncodings$delegate.getValue(this, $$delegatedProperties[50]);
    }

    private final Integer getHashVersion() {
        return (Integer) this.hashVersion$delegate.getValue(this, $$delegatedProperties[51]);
    }

    public final Charset getSourceEncoding() {
        return (Charset) this.sourceEncoding$delegate.getValue(this, $$delegatedProperties[52]);
    }

    private final boolean getMakeScorecard() {
        return ((Boolean) this.makeScorecard$delegate.getValue(this, $$delegatedProperties[53])).booleanValue();
    }

    private final int getTimeout() {
        return ((Number) this.timeout$delegate.getValue(this, $$delegatedProperties[54])).intValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: com.github.ajalt.clikt.core.BadParameterValue */
    private final void postCheck() throws IOException, BadParameterValue {
        Set it;
        Integer hashVersion = getHashVersion();
        if (hashVersion != null) {
            int it2 = hashVersion.intValue();
            ExtSettings.INSTANCE.setHashVersion(it2);
            Report.Companion.setHashVersion(it2);
        }
        if (getCheckerInfoGeneratorOptions() != null || getSubtoolsOptions() != null) {
            ConfigPluginLoader pl = (ConfigPluginLoader) loadSAConfig(null).component1();
            List<String> rules = getRules();
            Set rules2 = (rules == null || (it = CollectionsKt.toSet(rules)) == null) ? null : compatibleOldCheckerNames(it).getEnables();
            if (getCheckerInfoGeneratorOptions() != null) {
                CheckerInfoGeneratorOptions checkerInfoGeneratorOptions = getCheckerInfoGeneratorOptions();
                Intrinsics.checkNotNull(checkerInfoGeneratorOptions);
                checkerInfoGeneratorOptions.run(pl, rules2);
            }
            if (getSubtoolsOptions() != null) {
                SubToolsOptions subtoolsOptions = getSubtoolsOptions();
                Intrinsics.checkNotNull(subtoolsOptions);
                subtoolsOptions.run(pl, rules2);
            }
        }
        if (getCheckerInfoCompareOptions() != null) {
            CheckerInfoCompareOptions checkerInfoCompareOptions = getCheckerInfoCompareOptions();
            Intrinsics.checkNotNull(checkerInfoCompareOptions);
            checkerInfoCompareOptions.run();
        }
        if (getTarget() == null) {
            throw new BadParameterValue("\"--target\" option is required");
        }
        if (getFlowDroidOptions() == null && getDataFlowOptions() == null && getUtAnalyzeOptions() == null && getDisableBuiltInAnalysis()) {
            throw new BadParameterValue("No analyze engine is enabled");
        }
        if (getDisableDefaultJavaClassPath() && getClassPath().isEmpty()) {
            throw new BadParameterValue("\"--class-path\" is required when \"--disable-default-java-class-path\" is given");
        }
        if (getSrcPrecedence().isSootJavaSourcePrec()) {
            if (getSunBootClassPath().length() == 0) {
                Object[] objArr = {"sun.boot.class.path", "--sun-boot-class-path"};
                String str = String.format("System property \"%s\" or option \"%s\" should be provided when \"--src-precedence=prec_java_soot\"", Arrays.copyOf(objArr, objArr.length));
                Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                throw new BadParameterValue(str);
            }
            if (getJavaExtDirs().length() == 0) {
                Object[] objArr2 = {"java.ext.dirs", "--java-ext-dirs"};
                String str2 = String.format("System property \"%s\" or option \"%s\" should be provided when \"--src-precedence=prec_java_soot\"", Arrays.copyOf(objArr2, objArr2.length));
                Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
                throw new BadParameterValue(str2);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:65:0x0218  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void printOptions() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 787
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.printOptions():void");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockSplitter
        jadx.core.utils.exceptions.JadxRuntimeException: Unexpected missing predecessor for block: B:4:0x000e
        	at jadx.core.dex.visitors.blocks.BlockSplitter.addTempConnectionsForExcHandlers(BlockSplitter.java:280)
        	at jadx.core.dex.visitors.blocks.BlockSplitter.visit(BlockSplitter.java:79)
        */
    private static final java.lang.CharSequence printOptions$lambda$33(com.github.ajalt.mordant.rendering.Theme r6, com.github.ajalt.clikt.parameters.options.Option r7) {
        /*
            r0 = r7
            java.lang.String r1 = "it"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            r0 = r7
            boolean r0 = r0 instanceof com.github.ajalt.clikt.parameters.options.OptionWithValues
            if (r0 == 0) goto L53
        Lf:
            r0 = r7
            com.github.ajalt.clikt.parameters.options.OptionWithValues r0 = (com.github.ajalt.clikt.parameters.options.OptionWithValues) r0     // Catch: java.lang.IllegalStateException -> L1c
            java.lang.Object r0 = r0.getValue()     // Catch: java.lang.IllegalStateException -> L1c
            r9 = r0
            goto L20
        L1c:
            r10 = move-exception
            r0 = 0
            r9 = r0
        L20:
            r0 = r9
            r8 = r0
            r0 = r6
            com.github.ajalt.mordant.rendering.TextStyle r0 = r0.getInfo()
            r1 = r7
            java.util.Set r1 = r1.getNames()
            java.lang.String r1 = r1.toString()
            java.lang.String r0 = r0.invoke(r1)
            r1 = r6
            com.github.ajalt.mordant.rendering.TextStyle r1 = r1.getWarning()
            r2 = r8
            java.lang.String r2 = java.util.Objects.toString(r2)
            r3 = r2
            java.lang.String r4 = "toString(...)"
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r3, r4)
            java.lang.String r1 = r1.invoke(r2)
            java.lang.String r0 = r0 + " \"" + r1 + "\""
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            goto L5f
        L53:
            r0 = r7
            java.util.Set r0 = r0.getNames()
            java.lang.String r0 = r0.toString()
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
        L5f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.printOptions$lambda$33(com.github.ajalt.mordant.rendering.Theme, com.github.ajalt.clikt.parameters.options.Option):java.lang.CharSequence");
    }

    private static final Object printOptions$lambda$34() {
        return Theme.Companion.getDefault().getInfo().invoke("Current work directory: " + FilesKt.normalize(new File(".")).getAbsolutePath());
    }

    private static final Object printOptions$lambda$35() {
        return Theme.Companion.getDefault().getInfo().invoke("PID: " + ProcessHandle.current().pid());
    }

    private static final Object printOptions$lambda$36() {
        return "log files: " + AnalyzerEnv.INSTANCE.getLastLogFile().getParent();
    }

    private static final Object printOptions$lambda$37(String $info) {
        return "\n" + $info;
    }

    private static final Object printOptions$lambda$38(Theme $theme) {
        String strInvoke = $theme.getSuccess().invoke(ApplicationKt.getVersion());
        URL binaryUrl = OS.INSTANCE.getBinaryUrl();
        return "corax " + strInvoke + ": " + (binaryUrl != null ? binaryUrl.getPath() : null) + "}";
    }

    /* JADX WARN: Type inference failed for: r2v4, types: [cn.sast.cli.command.FySastCli$compatibleOldCheckerNames$renameMap$1] */
    private final CheckerFilterByName compatibleOldCheckerNames(Set<String> enableCheckers) {
        IResource nameMapping;
        String it;
        CheckerFilterByName all = new CheckerFilterByName(enableCheckers, MapsKt.emptyMap());
        try {
            URL coraxExe = OS.INSTANCE.getBinaryUrl();
            if (coraxExe == null) {
                return all;
            }
            IResource parent = Resource.INSTANCE.of(coraxExe).getParent();
            if (parent == null || (nameMapping = parent.resolve(FySastCliKt.MAPPING_FILE_NAME)) == null) {
                return all;
            }
            if (!nameMapping.getExists() || !nameMapping.isFile()) {
                return all;
            }
            Object objFromJson = new Gson().fromJson(PathsKt.readText(nameMapping.getPath(), Charsets.UTF_8), new TypeToken<Map<String, ? extends String>>() { // from class: cn.sast.cli.command.FySastCli$compatibleOldCheckerNames$renameMap$1
            }.getType());
            Intrinsics.checkNotNullExpressionValue(objFromJson, "fromJson(...)");
            Map renameMap = (Map) objFromJson;
            ArrayDeque worklist = new ArrayDeque(enableCheckers.size());
            worklist.addAll(enableCheckers);
            Set visited = new LinkedHashSet();
            while (true) {
                if (!(!worklist.isEmpty())) {
                    return new CheckerFilterByName(visited, renameMap);
                }
                String cur = (String) worklist.removeLast();
                if (cur != null && visited.add(cur) && (it = (String) renameMap.get(cur)) != null) {
                    worklist.add(it);
                }
            }
        } catch (Exception e) {
            logger.error(e, () -> {
                return compatibleOldCheckerNames$lambda$40(r2);
            });
            return all;
        }
    }

    private static final Object compatibleOldCheckerNames$lambda$40(Exception $e) {
        return $e.getMessage();
    }

    private final Pair<ConfigPluginLoader, SaConfig> parseConfig(String config, CheckerFilterByName checkerFilter) throws IOException {
        Pair pair;
        List listListOf;
        IResFile iResFileFileOf;
        if (StringsKt.contains$default(config, "@", false, 2, (Object) null)) {
        String configEntry = StringsKt.substringBefore$default(config, "@", (String) null, 2, (Object) null);
        String configPluginsDir = StringsKt.substringAfter$default(config, "@", (String) null, 2, (Object) null);
        pair = TuplesKt.to(configPluginsDir, configEntry);
    } else {
        pair = TuplesKt.to(config, "default-config.yml");
    }
        Pair pair2 = pair;
        String configDirPaths = (String) pair2.component1();
        String name = (String) pair2.component2();
        Pair pair3 = StringsKt.contains$default(configDirPaths, "#", false, 2, (Object) null) ? TuplesKt.to(StringsKt.substringBefore$default(configDirPaths, "#", (String) null, 2, (Object) null), StringsKt.substringAfter$default(configDirPaths, "#", (String) null, 2, (Object) null)) : TuplesKt.to(configDirPaths, (Object) null);
        String configPathFixed = (String) pair3.component1();
        String pluginId = (String) pair3.component2();
        if (configPathFixed.length() == 0) {
            throw new IllegalStateException("pluginsPath is empty!".toString());
        }
        List configDirs = ResourceImplKt.globPaths(configPathFixed);
        if (configDirs == null) {
            throw new IllegalStateException((configPathFixed + " not exists").toString());
        }
        List $this$map$iv = configDirs;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
        destination$iv$iv.add(((IResource) item$iv$iv).resolve("plugins").toDirectory());
    }
        ConfigPluginLoader pl = new ConfigPluginLoader(configDirs, (List) destination$iv$iv);
        if (!StringsKt.endsWith$default(name, ".yml", false, 2, (Object) null)) {
        return TuplesKt.to(pl, pl.loadFromName(pluginId, name));
    }
        IResFile it = Resource.INSTANCE.fileOf(name);
        IResFile mayBeFile = it.getExists() && it.isFile() ? it : null;
        if (mayBeFile != null) {
            listListOf = CollectionsKt.listOf(mayBeFile.getPath());
        } else {
            List<IResource> $this$flatMap$iv = configDirs;
            Collection destination$iv$iv2 = new ArrayList();
            for (IResource p : $this$flatMap$iv) {
                Iterable $this$filter$iv = PathExtensionsKt.getFiles(p.getPath());
                Collection destination$iv$iv3 = new ArrayList();
                for (Object element$iv$iv : $this$filter$iv) {
                if (Intrinsics.areEqual(PathsKt.getName((Path) element$iv$iv), name)) {
                destination$iv$iv3.add(element$iv$iv);
            }
            }
                Iterable list$iv$iv = (List) destination$iv$iv3;
                CollectionsKt.addAll(destination$iv$iv2, list$iv$iv);
            }
            listListOf = (List) destination$iv$iv2;
        }
        List ymlOptionFiles = listListOf;
        if (ymlOptionFiles.isEmpty()) {
            IResource tempFileDir = (IResource) CollectionsKt.first(configDirs);
            IResFile tempFile = tempFileDir.resolve(name).getAbsolute().getNormalize().toFile();
            pl.makeTemplateYml(tempFile);
            logger.warn(() -> {
                return parseConfig$lambda$46(r1, r2, r3, r4);
            });
            iResFileFileOf = tempFile;
        } else {
            if (ymlOptionFiles.size() != 1) {
                throw new IllegalStateException(("multiple files: " + name + ": " + ymlOptionFiles + " were found in: " + CollectionsKt.joinToString$default(configDirs, "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null)).toString());
            }
            iResFileFileOf = Resource.INSTANCE.fileOf((Path) CollectionsKt.first(ymlOptionFiles));
        }
        IResFile ymlConfig = iResFileFileOf;
        logger.info(() -> {
        return parseConfig$lambda$47(r1);
    });
        return TuplesKt.to(pl, pl.searchCheckerUnits(ymlConfig, checkerFilter));
    }

    private static final Object parseConfig$lambda$46(String $name, List $configDirs, IResFile $tempFile, IResource $tempFileDir) {
        List $this$map$iv = $configDirs;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
        IResource it = (IResource) item$iv$iv;
        destination$iv$iv.add(it.getAbsolute().getNormalize());
    }
        return "\n\n\nfile: " + $name + " is not exists in plugin directories: [" + CollectionsKt.joinToString$default((List) destination$iv$iv, "\n\t", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null) + "].\nA template SA-configuration file has been generated for you. Please edit it then run it again.\n-> " + $tempFile + "\n-> args: --config " + $name + "@" + $tempFileDir + "\n\n\n";
    }

    private static final Object parseConfig$lambda$47(IResFile $ymlConfig) {
        return "Use SA-Configuration yml file: " + $ymlConfig;
    }

    private final Pair<ConfigPluginLoader, SaConfig> defaultConfig(CheckerFilterByName checkerFilter) {
        logger.info(FySastCli::defaultConfig$lambda$48);
        String configFromEnv = FySastCliKt.getDefaultConfigDir();
        if (configFromEnv == null) {
            logger.info(FySastCli::defaultConfig$lambda$49);
            return null;
        }
        return parseConfig("default-config.yml@" + configFromEnv, checkerFilter);
    }

    private static final Object defaultConfig$lambda$48() {
        return "Try find config from local env: CORAX_CONFIG_DEFAULT_DIR";
    }

    private static final Object defaultConfig$lambda$49() {
        return "No config exists in the local env: CORAX_CONFIG_DEFAULT_DIR";
    }

    private final void setTimeOut() {
        if (getTimeout() >= 1) {
            CustomRepeatingTimer $this$setTimeOut_u24lambda_u2452 = new CustomRepeatingTimer(getTimeout() * 1000, () -> {
                return setTimeOut$lambda$51(r3);
            });
            $this$setTimeOut_u24lambda_u2452.setRepeats(false);
            $this$setTimeOut_u24lambda_u2452.start();
        }
    }

    private static final Object setTimeOut$lambda$51$lambda$50(FySastCli this$0) {
        return "Custom analysis shutdown timer has been exceeded in " + this$0.getTimeout() + " seconds";
    }

    private static final Unit setTimeOut$lambda$51(FySastCli this$0) {
        logger.warn(() -> {
        return setTimeOut$lambda$51$lambda$50(r1);
    });
        System.exit(700);
        throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
    }

    public void run() throws IOException, BadParameterValue {
        setTimeOut();
        AbstractFileIndexer.Companion.setDefaultClassCompareMode(getC2sMode());
        checkOutputDir();
        MainConfig.Companion.setPreferredLanguages(getPreferredLanguages());
        OS.INSTANCE.setMaxThreadNum(getMaxThreadNum());
        MetricsMonitor it = new MetricsMonitor();
        it.start();
        CheckType2StringKind.Companion.getCheckType2StringKind();
        postCheck();
        createAnchorPointFile();
        printOptions();
        Resource.INSTANCE.setZipExtractOutputDir(getOutput().getPath());
        if (!getZipFSEnv().isEmpty()) {
            Resource.INSTANCE.setNewFileSystemEnv(getZipFSEnv());
        }
        List<String> zipFSEncodings = getZipFSEncodings();
        if (!(zipFSEncodings == null || zipFSEncodings.isEmpty())) {
            Resource resource = Resource.INSTANCE;
            List<String> zipFSEncodings2 = getZipFSEncodings();
            Intrinsics.checkNotNull(zipFSEncodings2);
            resource.setFileSystemEncodings(zipFSEncodings2);
        }
        LocalDateTime startTime = LocalDateTime.now();
        try {
            CoroutineContext coroutineCtx = new CoroutineName("sast-main" + getMaxThreadNum()).plus(CoroutineDispatcher.limitedParallelism$default(Dispatchers.getDefault(), getMaxThreadNum(), (String) null, 2, (Object) null));
            BuildersKt.runBlocking(coroutineCtx, new C00021(it, this, null));
            it.runAnalyzeFinishHook();
            logger.info(() -> {
                return run$lambda$54(r1);
            });
            logger.info(() -> {
                return run$lambda$55(r1);
            });
        } catch (Throwable t) {
            logger.info(() -> {
                return run$lambda$56(r1);
            });
            logger.info(() -> {
                return run$lambda$57(r1);
            });
            throw t;
        }
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
    @DebugMetadata(f = "FySastCli.kt", l = {598, 598}, i = {PseudoTopologicalOrderer.REVERSE, PseudoTopologicalOrderer.REVERSE}, s = {"L$0", "L$1"}, n = {"$this$bracket$iv", "s$iv"}, m = "invokeSuspend", c = "cn.sast.cli.command.FySastCli$run$1")
    @SourceDebugExtension({"SMAP\nFySastCli.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$run$1\n+ 2 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,1247:1\n16#2,8:1248\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$run$1\n*L\n597#1:1248,8\n*E\n"})
    /* renamed from: cn.sast.cli.command.FySastCli$run$1, reason: invalid class name and case insensitive filesystem */
    /* loaded from: FySastCli$run$1.class */
    static final class C00021 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        Object L$0;
        Object L$1;
        int label;
        final /* synthetic */ MetricsMonitor $monitor;
        final /* synthetic */ FySastCli this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        C00021(MetricsMonitor $monitor, FySastCli this$0, Continuation<? super C00021> $completion) {
        super(2, $completion);
        this.$monitor = $monitor;
        this.this$0 = this$0;
    }

        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
        return new C00021(this.$monitor, this.this$0, $completion);
    }

        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

        public final Object invokeSuspend(Object $result) {
        PhaseIntervalTimer.Snapshot s$iv;
        PhaseIntervalTimer $this$bracket$iv;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        try {
            switch (this.label) {
                case PointsToGraphKt.pathStrictMod /* 0 */:
                ResultKt.throwOnFailure($result);
                $this$bracket$iv = this.$monitor.timer("runAnalyze");
                FySastCli fySastCli = this.this$0;
                MetricsMonitor metricsMonitor = this.$monitor;
                if ($this$bracket$iv == null) {
                TargetOptions target = fySastCli.getTarget();
                Intrinsics.checkNotNull(target);
                this.label = 1;
                if (fySastCli.runAnalyze(target, metricsMonitor, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                return Unit.INSTANCE;
            }
                s$iv = $this$bracket$iv.start();
                TargetOptions target2 = fySastCli.getTarget();
                Intrinsics.checkNotNull(target2);
                this.L$0 = $this$bracket$iv;
                this.L$1 = s$iv;
                this.label = 2;
                if (fySastCli.runAnalyze(target2, metricsMonitor, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                Unit unit = Unit.INSTANCE;
                $this$bracket$iv.stop(s$iv);
                return Unit.INSTANCE;
                case PseudoTopologicalOrderer.REVERSE /* 1 */:
                ResultKt.throwOnFailure($result);
                return Unit.INSTANCE;
                case 2:
                s$iv = (PhaseIntervalTimer.Snapshot) this.L$1;
                $this$bracket$iv = (PhaseIntervalTimer) this.L$0;
                ResultKt.throwOnFailure($result);
                Unit unit2 = Unit.INSTANCE;
                $this$bracket$iv.stop(s$iv);
                return Unit.INSTANCE;
                default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        } catch (Throwable th) {
            $this$bracket$iv.stop(s$iv);
            throw th;
        }
    }
    }

    private static final Object run$lambda$54(FySastCli this$0) {
        return "Output: " + this$0.getOutput().getAbsolute().getNormalize();
    }

    private static final Object run$lambda$55(LocalDateTime $startTime) {
        Intrinsics.checkNotNull($startTime);
        return "Analysis was completed in " + TimeUtilsKt.prettyPrintTime$default($startTime, null, 1, null);
    }

    private static final Object run$lambda$56(FySastCli this$0) {
        return "Output: " + this$0.getOutput().getAbsolute().getNormalize();
    }

    private static final Object run$lambda$57(LocalDateTime $startTime) {
        Intrinsics.checkNotNull($startTime);
        return "Analysis was terminated after " + TimeUtilsKt.prettyPrintTime$default($startTime, null, 1, null);
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = PseudoTopologicalOrderer.REVERSE, xi = 48, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n"}, d2 = {"Lcn/sast/cli/command/FySastCli$ResultType;", "", "<init>", "(Ljava/lang/String;I)V", "PLIST", "SARIF", "SQLITE", "SarifPackSrc", "SarifCopySrc", "COUNTER", "corax-cli"})
    /* loaded from: FySastCli$ResultType.class */
    public enum ResultType {
        PLIST,
        SARIF,
        SQLITE,
        SarifPackSrc,
        SarifCopySrc,
        COUNTER;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

        @NotNull
        public static EnumEntries<ResultType> getEntries() {
            return $ENTRIES;
        }
    }

    public final void setVerbosity(@NotNull Level verbosity) throws IllegalAccessException, NoSuchFieldException, IllegalArgumentException {
        Object obj;
        Intrinsics.checkNotNullParameter(verbosity, "verbosity");
        System.out.println((Object) ("Log Level changed to [" + verbosity + "]"));
        Logger apacheLog4j = LogManager.getRootLogger();
        Configurator.setAllLevels(apacheLog4j.getName(), UtilsKt.level(verbosity));
        System.out.println((Object) ("apache log4j2 root logger: isTraceEnabled: " + apacheLog4j.isTraceEnabled() + ", isDebugEnabled: " + apacheLog4j.isDebugEnabled() + ", isInfoEnabled: " + apacheLog4j.isInfoEnabled() + ", isWarnEnabled: " + apacheLog4j.isWarnEnabled() + ", isErrorEnabled: " + apacheLog4j.isErrorEnabled()));
        Logger logger2 = LoggerFactory.getLogger("ROOT");
        Intrinsics.checkNotNullExpressionValue(logger2, "getLogger(...)");
        try {
            Field field = Log4jLogger.class.getDeclaredField("logger");
            field.setAccessible(true);
            Object obj2 = field.get(logger2);
            obj = obj2 instanceof Logger ? (Logger) obj2 : null;
        } catch (NoSuchFieldException e) {
            obj = null;
        }
        Object obj3 = obj;
        if (obj3 == null) {
            obj3 = logger2;
        }
        Object actualLogger = obj3;
        if (!Intrinsics.areEqual(apacheLog4j.getClass(), actualLogger.getClass())) {
            System.out.println((Object) ("org.slf4j root logger:" + logger2.getClass().getSimpleName() + " isTraceEnabled: " + logger2.isTraceEnabled() + ", isDebugEnabled: " + logger2.isDebugEnabled() + ", isInfoEnabled: " + logger2.isInfoEnabled() + ", isWarnEnabled: " + logger2.isWarnEnabled() + ", isErrorEnabled: " + logger2.isErrorEnabled()));
            throw new IllegalStateException(("invalid logger: " + apacheLog4j.getClass() + " != " + actualLogger + ", 不使用 apache log4j2 可能会导致分析效率减少一倍").toString());
        }
    }

    @NotNull
    public final List<IResultCollector> getCollectors() {
        return this.collectors;
    }

    public final void setCollectors(@NotNull List<? extends IResultCollector> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.collectors = list;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.NoWhenBranchMatchedException */
    private final ResultCollector getResultCollector(SootInfoCache info, ProjectFileLocator locator, MainConfig mainConfig, MetricsMonitor monitor) throws NoWhenBranchMatchedException {
        List listPlus;
        Set resultType = getResultType();
        Set resultTypes = resultType.isEmpty() ? SetsKt.setOf(ResultType.SARIF) : resultType;
        if (resultTypes.contains(ResultType.COUNTER)) {
            this.collectors = CollectionsKt.plus(this.collectors, new ResultCounter());
        }
        MissingSummaryReporter methodSummariesMissing = new MissingSummaryReporter(mainConfig.getOutput_dir().resolve("undefined_summary_methods.txt").toFile());
        ResultCounter counter = new ResultCounter();
        ProjectFileLocator projectFileLocator = locator;
        Coverage coverage = null;
        Coverage coverage2 = null;
        DataFlowOptions dataFlowOptions = getDataFlowOptions();
        boolean z = dataFlowOptions != null && dataFlowOptions.getEnableCoverage();
        JacocoCompoundCoverage coverage3 = new JacocoCompoundCoverage(projectFileLocator, coverage, coverage2, z, 6, null);
        List listPlus2 = CollectionsKt.plus(CollectionsKt.plus(this.collectors, methodSummariesMissing), counter);
        IResDirectory output_dir = mainConfig.getOutput_dir();
        Set $this$fold$iv = resultTypes;
        List listEmptyList = CollectionsKt.emptyList();
        for (Object element$iv : $this$fold$iv) {
        List acc = listEmptyList;
        ResultType type = (ResultType) element$iv;
        switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
        case PseudoTopologicalOrderer.REVERSE /* 1 */:
        listPlus = CollectionsKt.plus(acc, OutputType.PLIST);
        break;
        case 2:
        listPlus = CollectionsKt.plus(acc, OutputType.SARIF);
        break;
        case 3:
        listPlus = acc;
        break;
        case 4:
        listPlus = CollectionsKt.plus(acc, OutputType.SarifPackSrc);
        break;
        case 5:
        listPlus = CollectionsKt.plus(acc, OutputType.SarifCopySrc);
        break;
        case 6:
        listPlus = acc;
        break;
        default:
        throw new NoWhenBranchMatchedException();
    }
        listEmptyList = listPlus;
    }
        List list = listEmptyList;
        DataFlowOptions dataFlowOptions2 = getDataFlowOptions();
        boolean z2 = dataFlowOptions2 != null && dataFlowOptions2.getEnableCoverage();
        return new ResultCollector(mainConfig, info, output_dir, locator, listPlus2, list, false, null, coverage3, z2, monitor, 192, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final kotlin.Pair<cn.sast.framework.plugin.ConfigPluginLoader, cn.sast.api.config.SaConfig> loadSAConfig(cn.sast.framework.plugin.CheckerFilterByName r5) throws java.io.IOException {
        /*
            r4 = this;
            r0 = r4
            java.lang.String r0 = r0.getConfig()
            r1 = r0
            if (r1 == 0) goto L16
            r6 = r0
            r0 = 0
            r7 = r0
            r0 = r4
            r1 = r6
            r2 = r5
            kotlin.Pair r0 = r0.parseConfig(r1, r2)
            r1 = r0
            if (r1 != 0) goto L2f
        L16:
        L17:
            r0 = r4
            r1 = r5
            kotlin.Pair r0 = r0.defaultConfig(r1)
            r1 = r0
            if (r1 != 0) goto L2f
        L21:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "SA-Config is required."
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r0
        L2f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.loadSAConfig(cn.sast.framework.plugin.CheckerFilterByName):kotlin.Pair");
    }

    private final void configureMainConfig(MainConfig mainConfig, MetricsMonitor monitor) throws IOException {
        SaConfig saConfig;
        boolean z;
        Integer dataFlowInterProceduralCalleeTimeOut;
        Integer factor1;
        Set it;
        mainConfig.setAndroidScene(Boolean.valueOf(getTarget() instanceof AndroidOptions));
        mainConfig.setCallGraphAlgorithm(getCallGraphAlgorithm());
        mainConfig.setCallGraphAlgorithmBuiltIn(getCallGraphAlgorithmBuiltIn());
        mainConfig.setEnableOriginalNames(getEnableOriginalNames());
        Iterable $this$flatMapTo$iv = getAutoAppClasses();
        PersistentSet.Builder builder = (Collection) ExtensionsKt.persistentSetOf().builder();
        for (Object element$iv : $this$flatMapTo$iv) {
        String it2 = (String) element$iv;
        Iterable iterableGlobPaths = ResourceImplKt.globPaths(it2);
        if (iterableGlobPaths == null) {
            throw new IllegalStateException(("autoAppClasses option: \"" + it2 + "\" is invalid or target not exists").toString());
        }
        Iterable list$iv = iterableGlobPaths;
        CollectionsKt.addAll(builder, list$iv);
    }
        mainConfig.setAutoAppClasses(builder.build());
        mainConfig.setAutoAppTraverseMode(getAutoAppTraverseMode());
        mainConfig.setAutoAppSrcInZipScheme(!getAutoAppSrcOnlyFileScheme());
        mainConfig.setStaticFieldTrackingMode(getStaticFieldTrackingMode());
        mainConfig.setEnableReflection(!getDisableReflection());
        mainConfig.setParallelsNum(getMaxThreadNum());
        mainConfig.setMemoryThreshold(getMemoryThreshold());
        mainConfig.setOutput_dir(getOutput());
        mainConfig.setDumpSootScene(getDumpSootScene());
        List<String> rules = getRules();
        CheckerFilterByName checkerFilter = (rules == null || (it = CollectionsKt.toSet(rules)) == null) ? null : compatibleOldCheckerNames(it);
        PhaseIntervalTimer $this$bracket$iv = monitor.timer("plugins.load");
        if ($this$bracket$iv == null) {
        Pair<ConfigPluginLoader, SaConfig> pairLoadSAConfig = loadSAConfig(checkerFilter);
        ConfigPluginLoader pl = (ConfigPluginLoader) pairLoadSAConfig.component1();
        SaConfig saConfig2 = (SaConfig) pairLoadSAConfig.component2();
        CollectionsKt.addAll(mainConfig.getConfigDirs(), pl.getConfigDirs());
        CheckerInfoGenerator gen = CheckerInfoGenerator.Companion.createCheckerInfoGenerator$default(CheckerInfoGenerator.Companion, pl, null, false, 2, null);
        mainConfig.setCheckerInfo(LazyKt.lazy(() -> {
        return configureMainConfig$lambda$67$lambda$66(r1);
    }));
        saConfig = saConfig2;
    } else {
        PhaseIntervalTimer.Snapshot s$iv = $this$bracket$iv.start();
        try {
            Pair<ConfigPluginLoader, SaConfig> pairLoadSAConfig2 = loadSAConfig(checkerFilter);
            ConfigPluginLoader pl2 = (ConfigPluginLoader) pairLoadSAConfig2.component1();
            SaConfig saConfig3 = (SaConfig) pairLoadSAConfig2.component2();
            CollectionsKt.addAll(mainConfig.getConfigDirs(), pl2.getConfigDirs());
            CheckerInfoGenerator gen2 = CheckerInfoGenerator.Companion.createCheckerInfoGenerator$default(CheckerInfoGenerator.Companion, pl2, null, false, 2, null);
            mainConfig.setCheckerInfo(LazyKt.lazy(() -> {
                return configureMainConfig$lambda$67$lambda$66(r1);
            }));
            $this$bracket$iv.stop(s$iv);
            saConfig = saConfig3;
        } catch (Throwable th) {
            $this$bracket$iv.stop(s$iv);
            throw th;
        }
    }
        mainConfig.setSaConfig(saConfig);
        mainConfig.setVersion(ApplicationKt.getVersion());
        if (getSrcPrecedence() != SrcPrecedence.prec_java) {
            Iterable $this$flatMap$iv = getClassPath();
            Collection destination$iv$iv = new ArrayList();
            for (Object element$iv$iv : $this$flatMap$iv) {
                String it3 = (String) element$iv$iv;
                String str = File.pathSeparator;
                Intrinsics.checkNotNullExpressionValue(str, "pathSeparator");
                Iterable list$iv$iv = StringsKt.contains$default(it3, str, false, 2, (Object) null) ? StringsKt.split$default(it3, new String[]{File.pathSeparator}, false, 0, 6, (Object) null) : CollectionsKt.listOf(it3);
                CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
            }
            List pathSeparatorSplit = (List) destination$iv$iv;
            mainConfig.setClasspath(ExtensionsKt.toPersistentSet(pathSeparatorSplit));
        } else {
            mainConfig.setClasspath(ExtensionsKt.toPersistentSet(getClassPath()));
        }
        Iterable $this$flatMapTo$iv2 = getProjectRoot();
        PersistentSet.Builder builder2 = (Collection) ExtensionsKt.persistentSetOf().builder();
        for (Object element$iv2 : $this$flatMapTo$iv2) {
        String it4 = (String) element$iv2;
        Iterable iterableGlobPaths2 = ResourceImplKt.globPaths(it4);
        if (iterableGlobPaths2 == null) {
            throw new IllegalStateException(("option: --project-root \"" + it4 + "\" is invalid or path not exists").toString());
        }
        Iterable list$iv2 = iterableGlobPaths2;
        CollectionsKt.addAll(builder2, list$iv2);
    }
        mainConfig.setProjectRoot(builder2.build());
        if (!getIncrementalScanOf().isEmpty()) {
            IncrementalAnalyzeImplByChangeFiles ia = new IncrementalAnalyzeImplByChangeFiles(mainConfig, !getDisableMappingDiffInArchive(), null, null, null, 28, null);
            Iterable $this$flatMap$iv2 = getIncrementalScanOf();
            Collection destination$iv$iv2 = new ArrayList();
            for (Object element$iv$iv2 : $this$flatMap$iv2) {
                String it5 = (String) element$iv$iv2;
                Iterable iterableGlobPaths3 = ResourceImplKt.globPaths(it5);
                if (iterableGlobPaths3 == null) {
                    throw new IllegalStateException(("option: --incremental-base \"" + it5 + "\" is invalid or target not exists").toString());
                }
                Iterable list$iv$iv2 = iterableGlobPaths3;
                CollectionsKt.addAll(destination$iv$iv2, list$iv$iv2);
            }
            Iterable $this$forEach$iv = (List) destination$iv$iv2;
            for (Object element$iv3 : $this$forEach$iv) {
                IResource it6 = (IResource) element$iv3;
                ia.parseIncrementBaseFile(it6);
            }
            mainConfig.setIncrementAnalyze(ia);
        }
        Iterable $this$any$iv = getProcess();
        if (!($this$any$iv instanceof Collection) || !((Collection) $this$any$iv).isEmpty()) {
        Iterator it7 = $this$any$iv.iterator();
        while (true) {
            if (!it7.hasNext()) {
                z = false;
                break;
            }
            Object element$iv4 = it7.next();
            String it8 = (String) element$iv4;
            if (it8.length() == 0) {
                z = true;
                break;
            }
        }
    } else {
        z = false;
    }
        if (!(!z)) {
            throw new IllegalStateException("process has empty string".toString());
        }
        Iterable $this$flatMapTo$iv3 = getProcess();
        PersistentSet.Builder builder3 = (Collection) ExtensionsKt.persistentSetOf().builder();
        for (Object element$iv5 : $this$flatMapTo$iv3) {
        String it9 = (String) element$iv5;
        Iterable iterableGlobPaths4 = ResourceImplKt.globPaths(it9);
        if (iterableGlobPaths4 == null) {
            throw new IllegalStateException(("option: --process \"" + it9 + "\" is invalid or target not exists").toString());
        }
        Iterable list$iv3 = iterableGlobPaths4;
        CollectionsKt.addAll(builder3, list$iv3);
    }
        mainConfig.setProcessDir(builder3.build());
        mainConfig.setSourcePath(ExtensionsKt.toPersistentSet(getSourcePath()));
        mainConfig.setHideNoSource(getHideNoSource());
        mainConfig.setTraverseMode(getTraverseMode());
        mainConfig.setSrc_precedence(getSrcPrecedence());
        mainConfig.setSunBootClassPath(getSunBootClassPath());
        mainConfig.setJavaExtDirs(getJavaExtDirs());
        mainConfig.setUseDefaultJavaClassPath(!getDisableDefaultJavaClassPath());
        mainConfig.setDeCompileIfNotExists(this.enableDecompile);
        mainConfig.setEnableCodeMetrics(getEnableCodeMetrics());
        List ecjOptionsFile = getEcjOptions();
        if (ecjOptionsFile != null) {
            mainConfig.setEcj_options(ecjOptionsFile);
        }
        if (getProjectScanConfig() != null) {
            ProjectConfig.Companion companion = ProjectConfig.Companion;
            File projectScanConfig = getProjectScanConfig();
            Intrinsics.checkNotNull(projectScanConfig);
            ProjectConfig projectScanConfigParsed = companion.load(projectScanConfig);
            mainConfig.setProjectConfig(projectScanConfigParsed);
        }
        mainConfig.setUse_wrapper(!getDisableWrapper());
        mainConfig.setApponly(getApponly());
        DataFlowOptions dataFlowOptions = getDataFlowOptions();
        if (dataFlowOptions != null && (factor1 = dataFlowOptions.getFactor1()) != null) {
            int it10 = factor1.intValue();
            ExtSettings.INSTANCE.setCalleeDepChainMaxNumForLibClassesInInterProceduraldataFlow(it10);
        }
        if (getStrict()) {
            ExtSettings.INSTANCE.setDataFlowInterProceduralCalleeTimeOut(-1);
        } else {
            DataFlowOptions dataFlowOptions2 = getDataFlowOptions();
            if (dataFlowOptions2 != null && (dataFlowInterProceduralCalleeTimeOut = dataFlowOptions2.getDataFlowInterProceduralCalleeTimeOut()) != null) {
                int it11 = dataFlowInterProceduralCalleeTimeOut.intValue();
                ExtSettings.INSTANCE.setDataFlowInterProceduralCalleeTimeOut(it11);
            }
        }
        mainConfig.setRootPathsForConvertRelativePath(CollectionsKt.toList(SetsKt.plus(SetsKt.plus(SetsKt.plus(SetsKt.plus(mainConfig.getProjectRoot(), mainConfig.getSourcePath()), mainConfig.getAutoAppClasses()), mainConfig.getProcessDir()), mainConfig.getOutput_dir())));
    }

    private static final CheckerInfoGenResult configureMainConfig$lambda$67$lambda$66(CheckerInfoGenerator $gen) {
        if ($gen != null) {
        return $gen.getCheckerInfo(false);
    }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0055  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x009d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void showMetrics() {
        /*
            Method dump skipped, instructions count: 514
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.showMetrics():void");
    }

    private static final double showMetrics$lambda$83$lambda$80(double number) {
        return MathKt.roundToLong(number / 0.5d) * 0.5d;
    }

    private static final Object showMetrics$lambda$83$lambda$81(String $xmxInfo, String $info) {
        return "Hardware Info: " + $xmxInfo + " " + $info;
    }

    private static final Object showMetrics$lambda$83$lambda$82(String $xmxHint) {
        return "\nYou can add the following command before run analysis to fully utilize the machine's performance:\n\t > " + Theme.Companion.getDefault().getWarning().invoke($xmxHint) + "\n";
    }

    private final void createAnchorPointFile() {
        IResFile f = this.anchorPointFile;
        if (f != null) {
            f.mkdirs();
            PathsKt.writeText$default(f.getPath(), "nothing", (Charset) null, new OpenOption[0], 2, (Object) null);
        }
    }

    private final void checkOutputDir() {
        if (getCheckerInfoCompareOptions() != null) {
            return;
        }
        IResFile anchorPointFile = getOutput().resolve(FySastCliKt.getANCHOR_POINT_FILE()).toFile();
        if (!anchorPointFile.getExists()) {
            if (!IResDirectory.DefaultImpls.listPathEntries$default(getOutput(), null, 1, null).isEmpty()) {
                throw new IllegalArgumentException("The output directory (" + getOutput() + ") is not empty. To avoid disrupting your environment with overwritten files, please check this parameter or clear the output folder.");
            }
            this.anchorPointFile = anchorPointFile;
        }
    }

    private final void checkEnv(ProjectFileLocator locator) {
        getOutput().mkdirs();
        FySastCli $this$checkEnv_u24lambda_u2487 = this;
        Iterable $this$forEach$iv = locator.getSourceDir();
        for (Object element$iv : $this$forEach$iv) {
        IResource it = (IResource) element$iv;
        if (it.isFileScheme()) {
            Path absolutePath = $this$checkEnv_u24lambda_u2487.getOutput().getPath().toAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath, "toAbsolutePath(...)");
            Path pathNormalize = absolutePath.normalize();
            Path absolutePath2 = it.getPath().toAbsolutePath();
            Intrinsics.checkNotNullExpressionValue(absolutePath2, "toAbsolutePath(...)");
            if (pathNormalize.startsWith(absolutePath2.normalize())) {
                throw new IllegalArgumentException("The output (" + $this$checkEnv_u24lambda_u2487.getOutput() + ") directory cannot be pointed to within the resources (" + it + ") that will be scanned.");
            }
        }
    }
        List exists = SequencesKt.toList(SequencesKt.map(locator.findFromFileIndexMap(StringsKt.split$default(FySastCliKt.getANCHOR_POINT_FILE(), new String[]{File.separator}, false, 0, 6, (Object) null), AbstractFileIndexer.CompareMode.Path), FySastCli::checkEnv$lambda$87$lambda$86));
        if (!exists.isEmpty()) {
            throw new IllegalArgumentException("The corax output directory (" + exists + ") has been detected as being included in the paths " + locator.getSourceDir() + " to be scanned. Please move all the output directories to another location or delete them.");
        }
    }

    private static final IResource checkEnv$lambda$87$lambda$86(IResFile it) {
        Intrinsics.checkNotNullParameter(it, "it");
        IResource parent = it.getParent();
        if (parent != null) {
            return parent.getParent();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object getFilteredJavaSourceFiles(cn.sast.api.config.MainConfig r7, cn.sast.framework.report.ProjectFileLocator r8, kotlin.coroutines.Continuation<? super java.util.Set<? extends cn.sast.common.IResFile>> r9) {
        /*
            Method dump skipped, instructions count: 301
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.getFilteredJavaSourceFiles(cn.sast.api.config.MainConfig, cn.sast.framework.report.ProjectFileLocator, kotlin.coroutines.Continuation):java.lang.Object");
    }

    private static final boolean getFilteredJavaSourceFiles$lambda$89$lambda$88(MainConfig $mainConfig, IResFile it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return ($mainConfig.getAutoAppSrcInZipScheme() || it.isFileScheme()) && ScanFilter.getActionOf$default($mainConfig.getScanFilter(), (String) null, it.getPath(), (String) null, 4, (Object) null) != ProcessRule.ScanAction.Skip;
    }

    private final void writeSourceFileListForProbe(MainConfig mainConfig, IResFile out, Set<? extends IResFile> srcFiles) {
        out.mkdirs();
        OpenOption[] openOptionArr = new OpenOption[0];
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(out.getPath(), (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), Charsets.UTF_8);
        Throwable th = null;
        try {
            try {
                OutputStreamWriter it = outputStreamWriter;
                for (IResFile f : srcFiles) {
                    IResFile e = f.expandRes(mainConfig.getOutput_dir());
                    it.write(e + "\n");
                    this.sqliteFileIndexes.add(e);
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(outputStreamWriter, (Throwable) null);
            } finally {
            }
        } catch (Throwable th2) {
            CloseableKt.closeFinally(outputStreamWriter, th);
            throw th2;
        }
    }

    private final void addFilesToDataBase(MainConfig mainConfig, Set<? extends IResFile> files) {
        SqliteDiagnostics sqliteDiagnosticsNewSqliteDiagnostics = ResultCollector.Companion.newSqliteDiagnostics(mainConfig, null, mainConfig.getOutput_dir(), null);
        Throwable th = null;
        try {
            try {
                SqliteDiagnostics it = sqliteDiagnosticsNewSqliteDiagnostics;
                SqliteDiagnostics.open$default(it, null, 1, null);
                for (IResFile f : files) {
                    it.createFileXCachedFromFile(f);
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(sqliteDiagnosticsNewSqliteDiagnostics, (Throwable) null);
            } finally {
            }
        } catch (Throwable th2) {
            CloseableKt.closeFinally(sqliteDiagnosticsNewSqliteDiagnostics, th);
            throw th2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0029  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object runCodeMetrics(cn.sast.api.config.MainConfig r7, java.util.Set<? extends cn.sast.common.IResFile> r8, kotlin.coroutines.Continuation<? super kotlin.Unit> r9) {
        /*
            Method dump skipped, instructions count: 539
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.runCodeMetrics(cn.sast.api.config.MainConfig, java.util.Set, kotlin.coroutines.Continuation):java.lang.Object");
    }

    private static final Object runCodeMetrics$lambda$93(Ref.ObjectRef $pb) {
        List<String> listCommand = ((ProcessBuilder) $pb.element).command();
        Intrinsics.checkNotNullExpressionValue(listCommand, "command(...)");
        return "PMD command line: " + CollectionsKt.joinToString$default(listCommand, " ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
    @DebugMetadata(f = "FySastCli.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "cn.sast.cli.command.FySastCli$runCodeMetrics$4")
    @SourceDebugExtension({"SMAP\nFySastCli.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$runCodeMetrics$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt\n*L\n1#1,1247:1\n49#2,24:1248\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$runCodeMetrics$4\n*L\n914#1:1248,24\n*E\n"})
    /* renamed from: cn.sast.cli.command.FySastCli$runCodeMetrics$4, reason: invalid class name */
    /* loaded from: FySastCli$runCodeMetrics$4.class */
    static final class AnonymousClass4 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;
        private /* synthetic */ Object L$0;
        final /* synthetic */ Ref.ObjectRef<ProcessBuilder> $pb;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass4(Ref.ObjectRef<ProcessBuilder> $pb, Continuation<? super AnonymousClass4> $completion) {
        super(2, $completion);
        this.$pb = $pb;
    }

        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
        Continuation<Unit> anonymousClass4 = new AnonymousClass4(this.$pb, $completion);
        anonymousClass4.L$0 = value;
        return anonymousClass4;
    }

        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

        public final Object invokeSuspend(Object $result) throws IOException {
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case PointsToGraphKt.pathStrictMod /* 0 */:
            ResultKt.throwOnFailure($result);
            CoroutineScope $this$withContext = (CoroutineScope) this.L$0;
            Process process = ((ProcessBuilder) this.$pb.element).start();
            BuildersKt.launch$default($this$withContext, (CoroutineContext) null, (CoroutineStart) null, new AnonymousClass1(process, null), 3, (Object) null);
            LoggerWithLogMethod $this$bracket_u24default$iv = LoggingKt.info(FySastCli.logger);
            final String msg$iv = "run code metrics";
            $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$1
            public final Object invoke() {
                return "Started: " + msg$iv;
            }
        });
            final LocalDateTime startTime$iv = LocalDateTime.now();
            final Ref.ObjectRef res$iv = new Ref.ObjectRef();
            res$iv.element = Maybe.Companion.empty();
            try {
                try {
                    if (process.waitFor(10L, TimeUnit.MINUTES)) {
                        FySastCli.logger.info(AnonymousClass4::invokeSuspend$lambda$2$lambda$0);
                    } else {
                        int code = process.exitValue();
                        FySastCli.logger.error(() -> {
                            return invokeSuspend$lambda$2$lambda$1(r1);
                        });
                    }
                    res$iv.element = new Maybe(Unit.INSTANCE);
                    ((Maybe) res$iv.element).getOrThrow();
                    if (((Maybe) res$iv.element).getHasValue()) {
                        $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$2
                            public final Object invoke() {
                                LocalDateTime localDateTime = startTime$iv;
                                Intrinsics.checkNotNull(localDateTime);
                                String strElapsedSecFrom = LoggingKt.elapsedSecFrom(localDateTime);
                                String str = msg$iv;
                                Result.Companion companion = Result.Companion;
                                Result.constructor-impl(((Maybe) res$iv.element).getOrThrow());
                                return "Finished (in " + strElapsedSecFrom + "): " + str + " " + "";
                            }
                        });
                    } else {
                        $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$3
                            public final Object invoke() {
                                LocalDateTime localDateTime = startTime$iv;
                                Intrinsics.checkNotNull(localDateTime);
                                return "Finished (in " + LoggingKt.elapsedSecFrom(localDateTime) + "): " + msg$iv + " <Nothing>";
                            }
                        });
                    }
                    return Unit.INSTANCE;
                } catch (Throwable t$iv) {
                    $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$4
                    public final Object invoke() {
                        LocalDateTime localDateTime = startTime$iv;
                        Intrinsics.checkNotNull(localDateTime);
                        String strElapsedSecFrom = LoggingKt.elapsedSecFrom(localDateTime);
                        String str = msg$iv;
                        Result.Companion companion = Result.Companion;
                        Result.constructor-impl(ResultKt.createFailure(t$iv));
                        return "Finished (in " + strElapsedSecFrom + "): " + str + " :: EXCEPTION :: " + "";
                    }
                });
                    throw t$iv;
                }
            } catch (Throwable th) {
                if (0 == 0) {
                    if (((Maybe) res$iv.element).getHasValue()) {
                        $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$5
                            public final Object invoke() {
                                LocalDateTime localDateTime = startTime$iv;
                                Intrinsics.checkNotNull(localDateTime);
                                String strElapsedSecFrom = LoggingKt.elapsedSecFrom(localDateTime);
                                String str = msg$iv;
                                Result.Companion companion = Result.Companion;
                                Result.constructor-impl(((Maybe) res$iv.element).getOrThrow());
                                return "Finished (in " + strElapsedSecFrom + "): " + str + " " + "";
                            }
                        });
                    } else {
                        $this$bracket_u24default$iv.getLogMethod().invoke(new Function0<Object>() { // from class: cn.sast.cli.command.FySastCli$runCodeMetrics$4$invokeSuspend$$inlined$bracket$default$6
                            public final Object invoke() {
                                LocalDateTime localDateTime = startTime$iv;
                                Intrinsics.checkNotNull(localDateTime);
                                return "Finished (in " + LoggingKt.elapsedSecFrom(localDateTime) + "): " + msg$iv + " <Nothing>";
                            }
                        });
                    }
                }
                throw th;
            }
            default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

        /* compiled from: FySastCli.kt */
        @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
        @DebugMetadata(f = "FySastCli.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "cn.sast.cli.command.FySastCli$runCodeMetrics$4$1")
        /* renamed from: cn.sast.cli.command.FySastCli$runCodeMetrics$4$1, reason: invalid class name */
        /* loaded from: FySastCli$runCodeMetrics$4$1.class */
        static final class AnonymousClass1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        int label;
        final /* synthetic */ Process $process;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Process $process, Continuation<? super AnonymousClass1> $completion) {
        super(2, $completion);
        this.$process = $process;
    }

        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
        return new AnonymousClass1(this.$process, $completion);
    }

        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

        public final Object invokeSuspend(Object $result) {
        IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case PointsToGraphKt.pathStrictMod /* 0 */:
            ResultKt.throwOnFailure($result);
            InputStream inputStream = this.$process.getInputStream();
            Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
            PrintStream printStream = System.err;
            Intrinsics.checkNotNullExpressionValue(printStream, "err");
            ByteStreamsKt.copyTo$default(inputStream, printStream, 0, 2, (Object) null);
            return Unit.INSTANCE;
            default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }
    }

        private static final Object invokeSuspend$lambda$2$lambda$0() {
        return "Code metrics analysis of pmd finished normally.";
    }

        private static final Object invokeSuspend$lambda$2$lambda$1(int $code) {
        return "The exit code of pmd is error: " + $code;
    }
    }

    private static final Object runCodeMetrics$lambda$94(Exception $e) {
        return "There are some errors when running code metrics analysis of pmd: " + $e.getMessage();
    }

    private final void top(MetricsMonitor monitor) {
        Path path = getOutput().resolve("top.log").getPath();
        OpenOption[] openOptionArr = {StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE};
        OutputStreamWriter io = new OutputStreamWriter(Files.newOutputStream(path, (OpenOption[]) Arrays.copyOf(openOptionArr, openOptionArr.length)), Charsets.UTF_8);
        io.write(LocalDateTime.now() + ": --------------------start--------------------");
        io.write(LocalDateTime.now() + ": PID: " + ProcessHandle.current().pid());
        final FySastCli$top$topLogWriter$1 $this$top_u24lambda_u2497 = new FySastCli$top$topLogWriter$1(io, () -> {
        return top$lambda$96(r0);
    });
        $this$top_u24lambda_u2497.setRepeats(true);
        $this$top_u24lambda_u2497.start();
        monitor.addAnalyzeFinishHook(new Thread(new Runnable() { // from class: cn.sast.cli.command.FySastCli.top.1
            @Override // java.lang.Runnable
            public final void run() throws IOException {
                $this$top_u24lambda_u2497.stop();
            }
        }));
    }

    private static final Unit top$lambda$96(OutputStreamWriter $io) throws IOException {
        ProcessInfoView $this$top_u24lambda_u2496_u24lambda_u2495 = ProcessInfoView.Companion.getGlobalProcessInfo();
        $io.write(LocalDateTime.now() + ": " + $this$top_u24lambda_u2496_u24lambda_u2495.getProcessInfoText() + " " + $this$top_u24lambda_u2496_u24lambda_u2495.getCpuLoadText() + "\n");
        $io.flush();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: Thrown type has an unknown type hierarchy: kotlin.NotImplementedError */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:100:0x06c0  */
    /* JADX WARN: Removed duplicated region for block: B:103:0x06e9  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0373  */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0029  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x0577  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x060f  */
    /* JADX WARN: Type inference failed for: r0v169, types: [cn.sast.cli.command.FySastCli$runAnalyze$info$1] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object runAnalyze(cn.sast.cli.command.TargetOptions r49, final cn.sast.framework.metrics.MetricsMonitor r50, kotlin.coroutines.Continuation<? super kotlin.Unit> r51) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException, kotlin.NoWhenBranchMatchedException, java.io.IOException, java.lang.IllegalArgumentException, kotlin.NotImplementedError {
        /*
            Method dump skipped, instructions count: 1821
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.runAnalyze(cn.sast.cli.command.TargetOptions, cn.sast.framework.metrics.MetricsMonitor, kotlin.coroutines.Continuation):java.lang.Object");
    }

    private static final Object runAnalyze$lambda$100$lambda$99(long $totalAnySourceFileNum) {
        return "Total java source files: " + $totalAnySourceFileNum;
    }

    private static final Path runAnalyze$lambda$101(ProjectFileLocator $locator, SootClass sc) {
        Intrinsics.checkNotNullParameter(sc, "sc");
        IResFile iResFile = $locator.get((IBugResInfo) new ClassResInfo(sc), (IWrapperFileGenerator) NullWrapperFileGenerator.INSTANCE);
        if (iResFile != null) {
            return iResFile.getPath();
        }
        return null;
    }

    private static final Object runAnalyze$lambda$105(AccuracyValidator.Result $res) {
        return $res;
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
    @DebugMetadata(f = "FySastCli.kt", l = {1160, 1162, 1160, 1162, 1168, 1168}, i = {2, 2, 3, 3, 5, 5}, s = {"L$0", "L$4", "L$0", "L$1", "L$0", "L$1"}, n = {"$this$bracket$iv", "s$iv", "$this$bracket$iv", "s$iv", "$this$bracket$iv", "s$iv"}, m = "invokeSuspend", c = "cn.sast.cli.command.FySastCli$runAnalyze$7")
    @SourceDebugExtension({"SMAP\nFySastCli.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$runAnalyze$7\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Timer.kt\ncn/sast/api/util/TimerKt\n*L\n1#1,1247:1\n1755#2,3:1248\n16#3,8:1251\n16#3,8:1259\n*S KotlinDebug\n*F\n+ 1 FySastCli.kt\ncn/sast/cli/command/FySastCli$runAnalyze$7\n*L\n1152#1:1248,3\n1159#1:1251,8\n1167#1:1259,8\n*E\n"})
    /* renamed from: cn.sast.cli.command.FySastCli$runAnalyze$7, reason: invalid class name */
    /* loaded from: FySastCli$runAnalyze$7.class */
    static final class AnonymousClass7 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        final /* synthetic */ MetricsMonitor $monitor;
        final /* synthetic */ ResultCollector $result;
        final /* synthetic */ Set<IResFile> $srcFiles;
        final /* synthetic */ MainConfig $mainConfig;
        final /* synthetic */ ProjectFileLocator $locator;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass7(MetricsMonitor $monitor, ResultCollector $result, Set<? extends IResFile> $srcFiles, MainConfig $mainConfig, ProjectFileLocator $locator, Continuation<? super AnonymousClass7> $completion) {
        super(2, $completion);
        this.$monitor = $monitor;
        this.$result = $result;
        this.$srcFiles = $srcFiles;
        this.$mainConfig = $mainConfig;
        this.$locator = $locator;
    }

        public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
        return FySastCli.this.new AnonymousClass7(this.$monitor, this.$result, this.$srcFiles, this.$mainConfig, this.$locator, $completion);
    }

        public final Object invoke(CoroutineScope p1, Continuation<? super Unit> p2) {
        return create(p1, p2).invokeSuspend(Unit.INSTANCE);
    }

        /* JADX WARN: Finally extract failed */
        /* JADX WARN: Removed duplicated region for block: B:37:0x018e  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x0262  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x02c1  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x02f0  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.lang.Object invokeSuspend(java.lang.Object r8) {
            /*
                Method dump skipped, instructions count: 863
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.AnonymousClass7.invokeSuspend(java.lang.Object):java.lang.Object");
        }

        private static final Object invokeSuspend$lambda$3$lambda$2(Set $jSrcFiles, ProjectFileLocator $locator) {
        return "Run code metrics for " + $jSrcFiles.size() + " files in source paths: " + $locator.getSourceDir();
    }
    }

    private static final Object runAnalyze$lambda$108() {
        return "dump soot scene ...";
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x005f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final java.util.List<java.lang.String> constructPmdAnalyzerCmd(cn.sast.common.IResFile r11, cn.sast.common.IResFile r12) {
        /*
            Method dump skipped, instructions count: 499
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.sast.cli.command.FySastCli.constructPmdAnalyzerCmd(cn.sast.common.IResFile, cn.sast.common.IResFile):java.util.List");
    }

    private static final Object constructPmdAnalyzerCmd$lambda$109() {
        return "Java executable path is null";
    }

    private static final Object constructPmdAnalyzerCmd$lambda$110() {
        return "Executable main jar path is null";
    }

    private static final Object constructPmdAnalyzerCmd$lambda$111() {
        return "PMD dir does not exist or is not a directory";
    }

    private static final Object constructPmdAnalyzerCmd$lambda$112(List $pmdAnalyzerCmd) {
        return "The command of PMD is: " + $pmdAnalyzerCmd;
    }

    /* compiled from: FySastCli.kt */
    @Metadata(mv = {2, PointsToGraphKt.pathStrictMod, PointsToGraphKt.pathStrictMod}, k = PseudoTopologicalOrderer.REVERSE, xi = 48, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n��¨\u0006\u0006"}, d2 = {"Lcn/sast/cli/command/FySastCli$Companion;", "", "<init>", "()V", "logger", "Lmu/KLogger;", "corax-cli"})
    /* loaded from: FySastCli$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }

    private static final Unit logger$lambda$114() {
        return Unit.INSTANCE;
    }

    public final void main2(@NotNull List<String> argv) {
        Intrinsics.checkNotNullParameter(argv, "argv");
        logger.info(() -> {
        return main2$lambda$113(r1);
    });
        List<String> $this$toTypedArray$iv = argv;
        OS.INSTANCE.setArgs((String[]) $this$toTypedArray$iv.toArray(new String[0]));
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() { // from class: cn.sast.cli.command.FySastCli.main2.2
            @Override // java.lang.Runnable
            public final void run() throws IOException {
                PathExtensionsKt.deleteDirectoryRecursively(ResourceImplKt.getSAstTempDirectory());
            }
        }));
        main(argv);
    }

    private static final Object main2$lambda$113(List $argv) {
        return "argv is " + CollectionsKt.joinToString$default($argv, " ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    public final void main2(@NotNull String[] argv) {
        Intrinsics.checkNotNullParameter(argv, "argv");
        main2(ArraysKt.toList(argv));
    }
}