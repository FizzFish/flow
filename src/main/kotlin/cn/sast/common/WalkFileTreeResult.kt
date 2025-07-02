package cn.sast.common

import java.nio.file.Path

public class WalkFileTreeResult(root: Path, files: List<Path>, dirs: List<Path>) {
   public final val root: Path
   public final val files: List<Path>
   public final val dirs: List<Path>

   init {
      this.root = root;
      this.files = files;
      this.dirs = dirs;
   }
}
