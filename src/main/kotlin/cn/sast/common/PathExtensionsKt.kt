@file:SourceDebugExtension(["SMAP\nPathExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathExtensions.kt\ncn/sast/common/PathExtensionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,94:1\n1#2:95\n*E\n"])

package cn.sast.common

import com.google.common.io.MoreFiles
import com.google.common.io.RecursiveDeleteOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.function.Predicate
import java.util.stream.Collectors
import kotlin.io.path.PathsKt
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.SourceDebugExtension
import org.apache.commons.io.FilenameUtils

public final val isRegularFile: Boolean
   public final get() {
      return Files.isRegularFile(`$this$isRegularFile`);
   }


public final val isDirectory: Boolean
   public final get() {
      return Files.isDirectory(`$this$isDirectory`);
   }


public final val files: List<Path>
   public final get() {
      if (!isDirectory(`$this$files`)) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         val var1: Any = Files.list(`$this$files`).filter(new Predicate(<unrepresentable>.INSTANCE as Function1) {
            {
               this.function = function;
            }
         }).collect(Collectors.toList());
         return var1 as MutableList<Path>;
      }
   }


public final val text: String
   public final get() {
      if (!isRegularFile(`$this$text`)) {
         throw new IllegalStateException("Check failed.".toString());
      } else {
         val var10000: java.util.List = Files.readAllLines(`$this$text`);
         val var2: java.lang.Iterable = var10000;
         val var10001: java.lang.String = System.lineSeparator();
         return CollectionsKt.joinToString$default(var2, var10001, null, null, 0, null, null, 62, null);
      }
   }


public fun Path.resolveDir(dir: String): Path {
   if (!isDirectory(`$this$resolveDir`)) {
      throw new IllegalStateException(("Failed check: receiver.isDirectory, where receiver is: $`$this$resolveDir`").toString());
   } else if (dir.length() <= 0) {
      throw new IllegalStateException(("Failed check: dir.length > 0, where dir is: '$dir'").toString());
   } else {
      val resolvedDir: Path = `$this$resolveDir`.resolve(dir);
      if (!isDirectory(resolvedDir)) {
         throw new IllegalStateException(("Failed check: resolvedDir.isDirectory, where resolvedDir is: $resolvedDir").toString());
      } else {
         return resolvedDir;
      }
   }
}

public fun Path.replaceText(sourceText: String, replacementText: String) {
   PathsKt.writeText$default(
      `$this$replaceText`,
      StringsKt.replace$default(getText(`$this$replaceText`), sourceText, replacementText, false, 4, null),
      null,
      new OpenOption[0],
      2,
      null
   );
}

public fun Path.getExtension(): String {
   val var10000: java.lang.String = FilenameUtils.getExtension(`$this$getExtension`.getFileName().toString());
   return var10000;
}

@Throws(java/io/IOException::class)
public fun Path.deleteDirectoryRecursively() {
   val var10001: Array<LinkOption> = new LinkOption[0];
   if (Files.exists(`$this$deleteDirectoryRecursively`, Arrays.copyOf(var10001, var10001.length))) {
      MoreFiles.deleteRecursively(`$this$deleteDirectoryRecursively`, new RecursiveDeleteOption[]{RecursiveDeleteOption.ALLOW_INSECURE});
   }
}

@Throws(java/io/IOException::class)
public fun Path.deleteDirectoryContents() {
   val var10001: Array<LinkOption> = new LinkOption[0];
   if (Files.exists(`$this$deleteDirectoryContents`, Arrays.copyOf(var10001, var10001.length))) {
      MoreFiles.deleteDirectoryContents(`$this$deleteDirectoryContents`, new RecursiveDeleteOption[]{RecursiveDeleteOption.ALLOW_INSECURE});
   }
}
