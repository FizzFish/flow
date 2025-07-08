package cn.sast.common

import com.google.common.io.MoreFiles
import com.google.common.io.RecursiveDeleteOption
import org.apache.commons.io.FilenameUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

val Path.isRegularFile: Boolean get() = Files.isRegularFile(this)
val Path.isDirectory: Boolean get() = Files.isDirectory(this)

val Path.files: List<Path>
   get() {
      require(Files.isDirectory(this)) { "Not a directory: $this" }
      return Files.list(this).filter { Files.isRegularFile(it) }.toList()
   }

val Path.text: String
   get() {
      require(Files.isRegularFile(this)) { "Not a file: $this" }
      return Files.readAllLines(this).joinToString(System.lineSeparator())
   }

fun Path.resolveDir(dir: String): Path {
   require(this.isDirectory) { "Receiver must be directory: $this" }
   require(dir.isNotEmpty()) { "dir name must not be empty" }
   val resolved = this.resolve(dir)
   require(Files.isDirectory(resolved)) { "Resolved path must be directory: $resolved" }
   return resolved
}

fun Path.replaceText(sourceText: String, replacementText: String) {
   writeText(readText().replace(sourceText, replacementText))
}

fun Path.getExtension(): String = FilenameUtils.getExtension(fileName.toString())

@Throws(IOException::class)
fun Path.deleteDirectoryRecursively() {
   if (Files.exists(this, LinkOption.NOFOLLOW_LINKS)) {
      MoreFiles.deleteRecursively(this, RecursiveDeleteOption.ALLOW_INSECURE)
   }
}

@Throws(IOException::class)
fun Path.deleteDirectoryContents() {
   if (Files.exists(this, LinkOption.NOFOLLOW_LINKS)) {
      MoreFiles.deleteDirectoryContents(this, RecursiveDeleteOption.ALLOW_INSECURE)
   }
}
