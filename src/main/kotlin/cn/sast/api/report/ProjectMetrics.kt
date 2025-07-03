package cn.sast.api.report

import cn.sast.common.OS
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class ProjectMetrics(
    public var command: List<String>? = OS.getCommandLine$default(OS.INSTANCE, null, false, 3, null),
    public var paths: MutableList<String> = ArrayList(),
    public var applicationClasses: Int = -1,
    public var libraryClasses: Int = -1,
    public var phantomClasses: Int = -1,
    public var applicationMethods: Int = -1,
    public var libraryMethods: Int = -1,
    public var applicationMethodsHaveBody: Int = -1,
    private var applicationMethodsHaveBodyRatio: Float = -1.0F,
    public var libraryMethodsHaveBody: Int = -1,
    private var libraryMethodsHaveBodyRatio: Float = -1.0F,
    public var analyzedFiles: Int = -1,
    public var appJavaFileCount: Int = -1,
    public var appJavaLineCount: Int = -1,
    public var totalFileNum: Long = -1L,
    public var totalAnySourceFileNum: Long = -1L,
    public var totalSourceFileNum: Long = -1L,
    @SerialName("analyzedClasses")
    private var _analyzedClasses: Int = -1,
    @SerialName("analyzedMethodEntries")
    private var _analyzedMethodEntries: Int = -1,
    @SerialName("analyzedApplicationMethods")
    private var _analyzedApplicationMethods: Int = -1,
    private var analyzedApplicationMethodsRatio: Float = -1.0F,
    @SerialName("analyzedLibraryMethods")
    private var _analyzedLibraryMethods: Int = -1,
    private var analyzedLibraryMethodsRatio: Float = -1.0F,
    public var serializedReports: Int = -1
) {
    @Transient
    public val analyzedClasses: MutableSet<String> = Collections.synchronizedSet(LinkedHashSet())

    @Transient
    public val analyzedMethodEntries: MutableSet<String> = Collections.synchronizedSet(LinkedHashSet())

    @Transient
    public val analyzedApplicationMethods: MutableSet<String> = Collections.synchronizedSet(LinkedHashSet())

    @Transient
    public val analyzedLibraryMethods: MutableSet<String> = Collections.synchronizedSet(LinkedHashSet())

    public fun process() {
        this._analyzedClasses = this.analyzedClasses.size
        this._analyzedApplicationMethods = this.analyzedApplicationMethods.size
        this._analyzedLibraryMethods = this.analyzedLibraryMethods.size
        this._analyzedMethodEntries = this.analyzedMethodEntries.size
        if (this.applicationMethods != 0) {
            this.applicationMethodsHaveBodyRatio = this.applicationMethodsHaveBody.toFloat() / this.applicationMethods
            this.analyzedApplicationMethodsRatio = this._analyzedApplicationMethods.toFloat() / this.applicationMethods
        }

        if (this.libraryMethods != 0) {
            this.libraryMethodsHaveBodyRatio = this.libraryMethodsHaveBody.toFloat() / this.libraryMethods
            this.analyzedLibraryMethodsRatio = this._analyzedLibraryMethods.toFloat() / this.libraryMethods
        }
    }

    private operator fun component9(): Float = this.applicationMethodsHaveBodyRatio
    private operator fun component11(): Float = this.libraryMethodsHaveBodyRatio
    private operator fun component18(): Int = this._analyzedClasses
    private operator fun component19(): Int = this._analyzedMethodEntries
    private operator fun component20(): Int = this._analyzedApplicationMethods
    private operator fun component21(): Float = this.analyzedApplicationMethodsRatio
    private operator fun component22(): Int = this._analyzedLibraryMethods
    private operator fun component23(): Float = this.analyzedLibraryMethodsRatio

    public fun copy(
        command: List<String>? = this.command,
        paths: MutableList<String> = this.paths,
        applicationClasses: Int = this.applicationClasses,
        libraryClasses: Int = this.libraryClasses,
        phantomClasses: Int = this.phantomClasses,
        applicationMethods: Int = this.applicationMethods,
        libraryMethods: Int = this.libraryMethods,
        applicationMethodsHaveBody: Int = this.applicationMethodsHaveBody,
        applicationMethodsHaveBodyRatio: Float = this.applicationMethodsHaveBodyRatio,
        libraryMethodsHaveBody: Int = this.libraryMethodsHaveBody,
        libraryMethodsHaveBodyRatio: Float = this.libraryMethodsHaveBodyRatio,
        analyzedFiles: Int = this.analyzedFiles,
        appJavaFileCount: Int = this.appJavaFileCount,
        appJavaLineCount: Int = this.appJavaLineCount,
        totalFileNum: Long = this.totalFileNum,
        totalAnySourceFileNum: Long = this.totalAnySourceFileNum,
        totalSourceFileNum: Long = this.totalSourceFileNum,
        _analyzedClasses: Int = this._analyzedClasses,
        _analyzedMethodEntries: Int = this._analyzedMethodEntries,
        _analyzedApplicationMethods: Int = this._analyzedApplicationMethods,
        analyzedApplicationMethodsRatio: Float = this.analyzedApplicationMethodsRatio,
        _analyzedLibraryMethods: Int = this._analyzedLibraryMethods,
        analyzedLibraryMethodsRatio: Float = this.analyzedLibraryMethodsRatio,
        serializedReports: Int = this.serializedReports
    ): ProjectMetrics {
        return ProjectMetrics(
            command,
            paths,
            applicationClasses,
            libraryClasses,
            phantomClasses,
            applicationMethods,
            libraryMethods,
            applicationMethodsHaveBody,
            applicationMethodsHaveBodyRatio,
            libraryMethodsHaveBody,
            libraryMethodsHaveBodyRatio,
            analyzedFiles,
            appJavaFileCount,
            appJavaLineCount,
            totalFileNum,
            totalAnySourceFileNum,
            totalSourceFileNum,
            _analyzedClasses,
            _analyzedMethodEntries,
            _analyzedApplicationMethods,
            analyzedApplicationMethodsRatio,
            _analyzedLibraryMethods,
            analyzedLibraryMethodsRatio,
            serializedReports
        )
    }

    public override fun toString(): String {
        return "ProjectMetrics(command=$command, paths=$paths, applicationClasses=$applicationClasses, libraryClasses=$libraryClasses, phantomClasses=$phantomClasses, applicationMethods=$applicationMethods, libraryMethods=$libraryMethods, applicationMethodsHaveBody=$applicationMethodsHaveBody, applicationMethodsHaveBodyRatio=$applicationMethodsHaveBodyRatio, libraryMethodsHaveBody=$libraryMethodsHaveBody, libraryMethodsHaveBodyRatio=$libraryMethodsHaveBodyRatio, analyzedFiles=$analyzedFiles, appJavaFileCount=$appJavaFileCount, appJavaLineCount=$appJavaLineCount, totalFileNum=$totalFileNum, totalAnySourceFileNum=$totalAnySourceFileNum, totalSourceFileNum=$totalSourceFileNum, _analyzedClasses=$_analyzedClasses, _analyzedMethodEntries=$_analyzedMethodEntries, _analyzedApplicationMethods=$_analyzedApplicationMethods, analyzedApplicationMethodsRatio=$analyzedApplicationMethodsRatio, _analyzedLibraryMethods=$_analyzedLibraryMethods, analyzedLibraryMethodsRatio=$analyzedLibraryMethodsRatio, serializedReports=$serializedReports)"
    }

    public override fun hashCode(): Int {
        var result = command?.hashCode() ?: 0
        result = 31 * result + paths.hashCode()
        result = 31 * result + applicationClasses
        result = 31 * result + libraryClasses
        result = 31 * result + phantomClasses
        result = 31 * result + applicationMethods
        result = 31 * result + libraryMethods
        result = 31 * result + applicationMethodsHaveBody
        result = 31 * result + applicationMethodsHaveBodyRatio.hashCode()
        result = 31 * result + libraryMethodsHaveBody
        result = 31 * result + libraryMethodsHaveBodyRatio.hashCode()
        result = 31 * result + analyzedFiles
        result = 31 * result + appJavaFileCount
        result = 31 * result + appJavaLineCount
        result = 31 * result + totalFileNum.hashCode()
        result = 31 * result + totalAnySourceFileNum.hashCode()
        result = 31 * result + totalSourceFileNum.hashCode()
        result = 31 * result + _analyzedClasses
        result = 31 * result + _analyzedMethodEntries
        result = 31 * result + _analyzedApplicationMethods
        result = 31 * result + analyzedApplicationMethodsRatio.hashCode()
        result = 31 * result + _analyzedLibraryMethods
        result = 31 * result + analyzedLibraryMethodsRatio.hashCode()
        result = 31 * result + serializedReports
        return result
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectMetrics) return false

        if (command != other.command) return false
        if (paths != other.paths) return false
        if (applicationClasses != other.applicationClasses) return false
        if (libraryClasses != other.libraryClasses) return false
        if (phantomClasses != other.phantomClasses) return false
        if (applicationMethods != other.applicationMethods) return false
        if (libraryMethods != other.libraryMethods) return false
        if (applicationMethodsHaveBody != other.applicationMethodsHaveBody) return false
        if (applicationMethodsHaveBodyRatio != other.applicationMethodsHaveBodyRatio) return false
        if (libraryMethodsHaveBody != other.libraryMethodsHaveBody) return false
        if (libraryMethodsHaveBodyRatio != other.libraryMethodsHaveBodyRatio) return false
        if (analyzedFiles != other.analyzedFiles) return false
        if (appJavaFileCount != other.appJavaFileCount) return false
        if (appJavaLineCount != other.appJavaLineCount) return false
        if (totalFileNum != other.totalFileNum) return false
        if (totalAnySourceFileNum != other.totalAnySourceFileNum) return false
        if (totalSourceFileNum != other.totalSourceFileNum) return false
        if (_analyzedClasses != other._analyzedClasses) return false
        if (_analyzedMethodEntries != other._analyzedMethodEntries) return false
        if (_analyzedApplicationMethods != other._analyzedApplicationMethods) return false
        if (analyzedApplicationMethodsRatio != other.analyzedApplicationMethodsRatio) return false
        if (_analyzedLibraryMethods != other._analyzedLibraryMethods) return false
        if (analyzedLibraryMethodsRatio != other.analyzedLibraryMethodsRatio) return false
        if (serializedReports != other.serializedReports) return false

        return true
    }

    public companion object {
        public fun serializer(): KSerializer<ProjectMetrics> {
            return ProjectMetrics.serializer()
        }
    }
}