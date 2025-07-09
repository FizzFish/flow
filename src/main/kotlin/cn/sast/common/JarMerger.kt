package cn.sast.common

import com.google.common.collect.ImmutableSortedMap
import java.io.*
import java.nio.file.*
import java.nio.file.attribute.FileTime
import java.util.function.Predicate
import java.util.jar.Attributes
import java.util.jar.Manifest
import org.apache.commons.compress.archivers.jar.*
import org.apache.commons.compress.archivers.zip.ZipMethod
import org.apache.commons.io.FilenameUtils
import java.nio.file.attribute.BasicFileAttributes

class JarMerger(
    jarFile: Path,
    private val filter: Predicate<String>? = null
) : Closeable {

    private val buffer = ByteArray(8192)
    private val jarOutputStream: JarArchiveOutputStream

    init {
        Files.createDirectories(jarFile.parent)
        jarOutputStream = JarArchiveOutputStream(BufferedOutputStream(Files.newOutputStream(jarFile)))
    }

    private fun toSystemIndependentPath(path: Path): String =
        path.toString().replace(path.fileSystem.separator, "/")

    @JvmOverloads
    fun addDirectory(
        directory: Path,
        filterOverride: Predicate<String>? = this.filter,
        transformer: Transformer? = null,
        relocator: Relocator? = null
    ) {
        val candidates = ImmutableSortedMap.naturalOrder<String, Path>()

        Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                var entryPath = toSystemIndependentPath(directory.relativize(file))
                if (filterOverride != null && !filterOverride.test(entryPath)) return FileVisitResult.CONTINUE
                if (relocator != null) entryPath = relocator.relocate(entryPath)
                candidates.put(entryPath, file)
                return FileVisitResult.CONTINUE
            }
        })

        for ((entryPath, file) in candidates.build()) {
            BufferedInputStream(Files.newInputStream(file)).use { input ->
                val stream = transformer?.filter(entryPath, input) ?: input
                stream?.let { write(JarArchiveEntry(entryPath), it) }
            }
        }
    }

    @JvmOverloads
    fun addJar(file: Path, filterOverride: Predicate<String>? = this.filter, relocator: Relocator? = null) {
        JarArchiveInputStream(Files.newInputStream(file)).use { zis ->
            while (true) {
                val entry = zis.nextZipEntry ?: break
                if (entry.isDirectory) continue
                var name = entry.name
                if (filterOverride == null || filterOverride.test(name)) {
                    if (relocator != null) name = relocator.relocate(name)
                    val newEntry = JarArchiveEntry(name).apply {
                        method = entry.method
                        if (method == ZipMethod.STORED.code) {
                            size = entry.size
                            compressedSize = entry.compressedSize
                            crc = entry.crc
                        }
                        lastModifiedTime = fileTime
                    }
                    write(newEntry, zis)
                }
            }
        }
    }

    fun addFile(entryPath: String, file: Path) {
        BufferedInputStream(Files.newInputStream(file)).use {
            write(JarArchiveEntry(entryPath), it)
        }
    }

    fun addEntry(entryPath: String, input: InputStream) {
        BufferedInputStream(input).use {
            write(JarArchiveEntry(entryPath), it)
        }
    }

    fun setManifestProperties(properties: Map<String, String>) {
        val manifest = Manifest()
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0.0"
        properties.forEach { (k, v) -> manifest.mainAttributes[Attributes.Name(k)] = v }

        val entry = JarArchiveEntry("META-INF/MANIFEST.MF").apply { setEntryAttributes(this) }
        jarOutputStream.putArchiveEntry(entry)
        manifest.write(jarOutputStream)
        jarOutputStream.closeArchiveEntry()
    }

    private fun write(entry: JarArchiveEntry, from: InputStream) {
        setEntryAttributes(entry)
        jarOutputStream.putArchiveEntry(entry)
        from.copyTo(jarOutputStream, buffer.size)
        jarOutputStream.closeArchiveEntry()
    }

    private fun setEntryAttributes(entry: JarArchiveEntry) {
        entry.lastModifiedTime = fileTime
        entry.lastAccessTime = fileTime
        entry.creationTime = fileTime
    }

    override fun close() = jarOutputStream.close()

    interface Relocator {
        fun relocate(entryPath: String): String
    }

    interface Transformer {
        fun filter(entryPath: String, input: InputStream): InputStream?
    }

    companion object {
        val fileTime: FileTime = FileTime.fromMillis(0)
    }
}
