package cn.sast.common

import java.nio.file.Path

data class WalkFileTreeResult(val root: Path, val files: List<Path>, val dirs: List<Path>)
