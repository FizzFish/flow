package cn.sast.common

import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Path

/**
 * Abstraction over a file-system resource.
 */
sealed interface IResource : Comparable<IResource> {
    val path: Path
    val name: String
    val extension: String
    val exists: Boolean
    val isFile: Boolean
    val isDirectory: Boolean
    val isRegularFile: Boolean
    val absolutePath: String
    val absolute: IResource
    val normalize: IResource
    val isFileScheme: Boolean
    val isJrtScheme: Boolean
    val isJarScheme: Boolean
    val schemePath: Path
    val uri: URI
    val url: URL
    val zipEntry: String?
    val file: File
    val parent: IResource?

    val zipLike: Boolean
        get() = ResourceKt.getZipLike(path)

    val pathString: String

    fun resolve(name: String): IResource
    fun toFile(): IResFile
    fun toDirectory(): IResDirectory
    fun expandRes(outPut: IResDirectory): IResource
    fun mkdirs()

    @Throws(IOException::class)
    fun deleteDirectoryRecursively() {
        path.deleteDirectoryRecursively()
    }

    @Throws(IOException::class)
    fun deleteDirectoryContents() {
        path.deleteDirectoryContents()
    }

    fun seq(): Sequence<Path>
}
