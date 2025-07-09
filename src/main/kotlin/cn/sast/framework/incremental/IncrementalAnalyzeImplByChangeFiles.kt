package cn.sast.framework.incremental

class IncrementalAnalyzeImplByChangeFiles(
    private val mainConfig: MainConfig,
    private val mappingDiffInArchive: Boolean = true,
    private val factory: ModifyInfoFactory = ModifyInfoFactoryImpl(),
    override val simpleDeclAnalysisDependsGraph: SimpleDeclAnalysisDependsGraph =
        factory.createSimpleDeclAnalysisDependsGraph(),
    override val interProceduralAnalysisDependsGraph: InterProceduralAnalysisDependsGraph =
        factory.createInterProceduralAnalysisDependsGraph(),
) : IncrementalAnalyzeByChangeFiles {

    /* ---------- patch 解析阶段 ---------- */

    private val pathsInPatch      = linkedSetOf<String>()
    private val modifyFiles       = linkedSetOf<String>()
    private val oldPath2Header    = linkedMapOf<String, FileHeader>()
    private val newPath2Header    = linkedMapOf<String, FileHeader>()
    private val name2Path         = mutableMapOf<String, MutableSet<String>>()

    private val ignoreCase =
        System.getProperty("os.name").startsWith("Windows", ignoreCase = true)

    fun parsePatch(diffFile: IResFile) {
        Paths.get(diffFile.path).bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                when {
                    line.startsWith("diff --git ") -> {/* 进入一段新 patch */}
                    line.startsWith("--- ")        -> {/* old path */}
                    line.startsWith("+++ ")        -> {/* new path */}
                    // 其他如 rename/copy，可自行补充
                }
            }
        }
        // 归一化、填充 pathsInPatch / name2Path …
    }

    /* ---------- 场景更新阶段 ---------- */

    override fun update(scene: Scene, locator: ProjectFileLocator?) {
        scene.classes.snapshotIterator().forEachRemaining { sc ->
            val classPath = locator?.locateClass(sc) ?: return@forEachRemaining
            if (classPath in pathsInPatch) {
                val decls = factory.getSubDecls(factory.toDecl(sc))
                simpleDeclAnalysisDependsGraph.visitChangedDecl(classPath, decls)
                interProceduralAnalysisDependsGraph.visitChangedDecl(classPath, decls)
            }
        }
    }

    /* ---------- 变化类型查询 ---------- */

    override fun getChangeType(target: Any): ChangeType =
        when (normalizePath(target)) {
            in modifyFiles   -> ChangeType.Modified
            in pathsInPatch  -> ChangeType.AddedOrDeleted
            else             -> ChangeType.Unchanged
        }

    /* ---------- 私有工具 ---------- */

    private fun normalizePath(p: Any): String =
        p.toString().replace('\\', '/').let { if (ignoreCase) it.lowercase() else it }
}
