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
public data class ProjectMetrics(command: List<String>? = OS.getCommandLine$default(OS.INSTANCE, null, false, 3, null),
   paths: MutableList<String> = (new ArrayList()) as java.util.List,
   applicationClasses: Int = -1,
   libraryClasses: Int = -1,
   phantomClasses: Int = -1,
   applicationMethods: Int = -1,
   libraryMethods: Int = -1,
   applicationMethodsHaveBody: Int = -1,
   applicationMethodsHaveBodyRatio: Float = -1.0F,
   libraryMethodsHaveBody: Int = -1,
   libraryMethodsHaveBodyRatio: Float = -1.0F,
   analyzedFiles: Int = -1,
   appJavaFileCount: Int = -1,
   appJavaLineCount: Int = -1,
   totalFileNum: Long = -1L,
   totalAnySourceFileNum: Long = -1L,
   totalSourceFileNum: Long = -1L,
   _analyzedClasses: Int = -1,
   _analyzedMethodEntries: Int = -1,
   _analyzedApplicationMethods: Int = -1,
   analyzedApplicationMethodsRatio: Float = -1.0F,
   _analyzedLibraryMethods: Int = -1,
   analyzedLibraryMethodsRatio: Float = -1.0F,
   serializedReports: Int = -1
) {
   public final var command: List<String>?
      internal set

   public final var paths: MutableList<String>
      internal set

   public final var applicationClasses: Int
      internal set

   public final var libraryClasses: Int
      internal set

   public final var phantomClasses: Int
      internal set

   public final var applicationMethods: Int
      internal set

   public final var libraryMethods: Int
      internal set

   public final var applicationMethodsHaveBody: Int
      internal set

   private final var applicationMethodsHaveBodyRatio: Float

   public final var libraryMethodsHaveBody: Int
      internal set

   private final var libraryMethodsHaveBodyRatio: Float

   public final var analyzedFiles: Int
      internal set

   public final var appJavaFileCount: Int
      internal set

   public final var appJavaLineCount: Int
      internal set

   public final var totalFileNum: Long
      internal set

   public final var totalAnySourceFileNum: Long
      internal set

   public final var totalSourceFileNum: Long
      internal set

   @SerialName("analyzedClasses")
   private final var _analyzedClasses: Int

   @SerialName("analyzedMethodEntries")
   private final var _analyzedMethodEntries: Int

   @SerialName("analyzedApplicationMethods")
   private final var _analyzedApplicationMethods: Int

   private final var analyzedApplicationMethodsRatio: Float

   @SerialName("analyzedLibraryMethods")
   private final var _analyzedLibraryMethods: Int

   private final var analyzedLibraryMethodsRatio: Float

   public final var serializedReports: Int
      internal set

   @Transient
   public final val analyzedClasses: MutableSet<String>

   @Transient
   public final val analyzedMethodEntries: MutableSet<String>

   @Transient
   public final val analyzedApplicationMethods: MutableSet<String>

   @Transient
   public final val analyzedLibraryMethods: MutableSet<String>

   init {
      this.command = command;
      this.paths = paths;
      this.applicationClasses = applicationClasses;
      this.libraryClasses = libraryClasses;
      this.phantomClasses = phantomClasses;
      this.applicationMethods = applicationMethods;
      this.libraryMethods = libraryMethods;
      this.applicationMethodsHaveBody = applicationMethodsHaveBody;
      this.applicationMethodsHaveBodyRatio = applicationMethodsHaveBodyRatio;
      this.libraryMethodsHaveBody = libraryMethodsHaveBody;
      this.libraryMethodsHaveBodyRatio = libraryMethodsHaveBodyRatio;
      this.analyzedFiles = analyzedFiles;
      this.appJavaFileCount = appJavaFileCount;
      this.appJavaLineCount = appJavaLineCount;
      this.totalFileNum = totalFileNum;
      this.totalAnySourceFileNum = totalAnySourceFileNum;
      this.totalSourceFileNum = totalSourceFileNum;
      this._analyzedClasses = _analyzedClasses;
      this._analyzedMethodEntries = _analyzedMethodEntries;
      this._analyzedApplicationMethods = _analyzedApplicationMethods;
      this.analyzedApplicationMethodsRatio = analyzedApplicationMethodsRatio;
      this._analyzedLibraryMethods = _analyzedLibraryMethods;
      this.analyzedLibraryMethodsRatio = analyzedLibraryMethodsRatio;
      this.serializedReports = serializedReports;
      var var10001: java.util.Set = Collections.synchronizedSet(new LinkedHashSet());
      this.analyzedClasses = var10001;
      var10001 = Collections.synchronizedSet(new LinkedHashSet());
      this.analyzedMethodEntries = var10001;
      var10001 = Collections.synchronizedSet(new LinkedHashSet());
      this.analyzedApplicationMethods = var10001;
      var10001 = Collections.synchronizedSet(new LinkedHashSet());
      this.analyzedLibraryMethods = var10001;
   }

   public fun process() {
      this._analyzedClasses = this.analyzedClasses.size();
      this._analyzedApplicationMethods = this.analyzedApplicationMethods.size();
      this._analyzedLibraryMethods = this.analyzedLibraryMethods.size();
      this._analyzedMethodEntries = this.analyzedMethodEntries.size();
      if (this.applicationMethods != 0) {
         this.applicationMethodsHaveBodyRatio = (float)this.applicationMethodsHaveBody / this.applicationMethods;
         this.analyzedApplicationMethodsRatio = (float)this._analyzedApplicationMethods / this.applicationMethods;
      }

      if (this.libraryMethods != 0) {
         this.libraryMethodsHaveBodyRatio = (float)this.libraryMethodsHaveBody / this.libraryMethods;
         this.analyzedLibraryMethodsRatio = (float)this._analyzedLibraryMethods / this.libraryMethods;
      }
   }

   public operator fun component1(): List<String>? {
      return this.command;
   }

   public operator fun component2(): MutableList<String> {
      return this.paths;
   }

   public operator fun component3(): Int {
      return this.applicationClasses;
   }

   public operator fun component4(): Int {
      return this.libraryClasses;
   }

   public operator fun component5(): Int {
      return this.phantomClasses;
   }

   public operator fun component6(): Int {
      return this.applicationMethods;
   }

   public operator fun component7(): Int {
      return this.libraryMethods;
   }

   public operator fun component8(): Int {
      return this.applicationMethodsHaveBody;
   }

   private operator fun component9(): Float {
      return this.applicationMethodsHaveBodyRatio;
   }

   public operator fun component10(): Int {
      return this.libraryMethodsHaveBody;
   }

   private operator fun component11(): Float {
      return this.libraryMethodsHaveBodyRatio;
   }

   public operator fun component12(): Int {
      return this.analyzedFiles;
   }

   public operator fun component13(): Int {
      return this.appJavaFileCount;
   }

   public operator fun component14(): Int {
      return this.appJavaLineCount;
   }

   public operator fun component15(): Long {
      return this.totalFileNum;
   }

   public operator fun component16(): Long {
      return this.totalAnySourceFileNum;
   }

   public operator fun component17(): Long {
      return this.totalSourceFileNum;
   }

   private operator fun component18(): Int {
      return this._analyzedClasses;
   }

   private operator fun component19(): Int {
      return this._analyzedMethodEntries;
   }

   private operator fun component20(): Int {
      return this._analyzedApplicationMethods;
   }

   private operator fun component21(): Float {
      return this.analyzedApplicationMethodsRatio;
   }

   private operator fun component22(): Int {
      return this._analyzedLibraryMethods;
   }

   private operator fun component23(): Float {
      return this.analyzedLibraryMethodsRatio;
   }

   public operator fun component24(): Int {
      return this.serializedReports;
   }

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
      return new ProjectMetrics(
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
      );
   }

   public override fun toString(): String {
      return "ProjectMetrics(command=${this.command}, paths=${this.paths}, applicationClasses=${this.applicationClasses}, libraryClasses=${this.libraryClasses}, phantomClasses=${this.phantomClasses}, applicationMethods=${this.applicationMethods}, libraryMethods=${this.libraryMethods}, applicationMethodsHaveBody=${this.applicationMethodsHaveBody}, applicationMethodsHaveBodyRatio=${this.applicationMethodsHaveBodyRatio}, libraryMethodsHaveBody=${this.libraryMethodsHaveBody}, libraryMethodsHaveBodyRatio=${this.libraryMethodsHaveBodyRatio}, analyzedFiles=${this.analyzedFiles}, appJavaFileCount=${this.appJavaFileCount}, appJavaLineCount=${this.appJavaLineCount}, totalFileNum=${this.totalFileNum}, totalAnySourceFileNum=${this.totalAnySourceFileNum}, totalSourceFileNum=${this.totalSourceFileNum}, _analyzedClasses=${this._analyzedClasses}, _analyzedMethodEntries=${this._analyzedMethodEntries}, _analyzedApplicationMethods=${this._analyzedApplicationMethods}, analyzedApplicationMethodsRatio=${this.analyzedApplicationMethodsRatio}, _analyzedLibraryMethods=${this._analyzedLibraryMethods}, analyzedLibraryMethodsRatio=${this.analyzedLibraryMethodsRatio}, serializedReports=${this.serializedReports})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             (
                                                                                                                                                      (
                                                                                                                                                               (
                                                                                                                                                                        (
                                                                                                                                                                                 (
                                                                                                                                                                                          (
                                                                                                                                                                                                   (
                                                                                                                                                                                                            (
                                                                                                                                                                                                                     if (this.command
                                                                                                                                                                                                                           == null)
                                                                                                                                                                                                                        0
                                                                                                                                                                                                                        else
                                                                                                                                                                                                                        this.command
                                                                                                                                                                                                                           .hashCode()
                                                                                                                                                                                                                  )
                                                                                                                                                                                                                  * 31
                                                                                                                                                                                                               + this.paths
                                                                                                                                                                                                                  .hashCode()
                                                                                                                                                                                                         )
                                                                                                                                                                                                         * 31
                                                                                                                                                                                                      + Integer.hashCode(
                                                                                                                                                                                                         this.applicationClasses
                                                                                                                                                                                                      )
                                                                                                                                                                                                )
                                                                                                                                                                                                * 31
                                                                                                                                                                                             + Integer.hashCode(
                                                                                                                                                                                                this.libraryClasses
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                       * 31
                                                                                                                                                                                    + Integer.hashCode(
                                                                                                                                                                                       this.phantomClasses
                                                                                                                                                                                    )
                                                                                                                                                                              )
                                                                                                                                                                              * 31
                                                                                                                                                                           + Integer.hashCode(
                                                                                                                                                                              this.applicationMethods
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                                     * 31
                                                                                                                                                                  + Integer.hashCode(
                                                                                                                                                                     this.libraryMethods
                                                                                                                                                                  )
                                                                                                                                                            )
                                                                                                                                                            * 31
                                                                                                                                                         + Integer.hashCode(
                                                                                                                                                            this.applicationMethodsHaveBody
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + java.lang.Float.hashCode(
                                                                                                                                                   this.applicationMethodsHaveBodyRatio
                                                                                                                                                )
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + Integer.hashCode(
                                                                                                                                          this.libraryMethodsHaveBody
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + java.lang.Float.hashCode(
                                                                                                                                 this.libraryMethodsHaveBodyRatio
                                                                                                                              )
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + Integer.hashCode(this.analyzedFiles)
                                                                                                               )
                                                                                                               * 31
                                                                                                            + Integer.hashCode(this.appJavaFileCount)
                                                                                                      )
                                                                                                      * 31
                                                                                                   + Integer.hashCode(this.appJavaLineCount)
                                                                                             )
                                                                                             * 31
                                                                                          + java.lang.Long.hashCode(this.totalFileNum)
                                                                                    )
                                                                                    * 31
                                                                                 + java.lang.Long.hashCode(this.totalAnySourceFileNum)
                                                                           )
                                                                           * 31
                                                                        + java.lang.Long.hashCode(this.totalSourceFileNum)
                                                                  )
                                                                  * 31
                                                               + Integer.hashCode(this._analyzedClasses)
                                                         )
                                                         * 31
                                                      + Integer.hashCode(this._analyzedMethodEntries)
                                                )
                                                * 31
                                             + Integer.hashCode(this._analyzedApplicationMethods)
                                       )
                                       * 31
                                    + java.lang.Float.hashCode(this.analyzedApplicationMethodsRatio)
                              )
                              * 31
                           + Integer.hashCode(this._analyzedLibraryMethods)
                     )
                     * 31
                  + java.lang.Float.hashCode(this.analyzedLibraryMethodsRatio)
            )
            * 31
         + Integer.hashCode(this.serializedReports);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is ProjectMetrics) {
         return false;
      } else {
         val var2: ProjectMetrics = other as ProjectMetrics;
         if (!(this.command == (other as ProjectMetrics).command)) {
            return false;
         } else if (!(this.paths == var2.paths)) {
            return false;
         } else if (this.applicationClasses != var2.applicationClasses) {
            return false;
         } else if (this.libraryClasses != var2.libraryClasses) {
            return false;
         } else if (this.phantomClasses != var2.phantomClasses) {
            return false;
         } else if (this.applicationMethods != var2.applicationMethods) {
            return false;
         } else if (this.libraryMethods != var2.libraryMethods) {
            return false;
         } else if (this.applicationMethodsHaveBody != var2.applicationMethodsHaveBody) {
            return false;
         } else if (java.lang.Float.compare(this.applicationMethodsHaveBodyRatio, var2.applicationMethodsHaveBodyRatio) != 0) {
            return false;
         } else if (this.libraryMethodsHaveBody != var2.libraryMethodsHaveBody) {
            return false;
         } else if (java.lang.Float.compare(this.libraryMethodsHaveBodyRatio, var2.libraryMethodsHaveBodyRatio) != 0) {
            return false;
         } else if (this.analyzedFiles != var2.analyzedFiles) {
            return false;
         } else if (this.appJavaFileCount != var2.appJavaFileCount) {
            return false;
         } else if (this.appJavaLineCount != var2.appJavaLineCount) {
            return false;
         } else if (this.totalFileNum != var2.totalFileNum) {
            return false;
         } else if (this.totalAnySourceFileNum != var2.totalAnySourceFileNum) {
            return false;
         } else if (this.totalSourceFileNum != var2.totalSourceFileNum) {
            return false;
         } else if (this._analyzedClasses != var2._analyzedClasses) {
            return false;
         } else if (this._analyzedMethodEntries != var2._analyzedMethodEntries) {
            return false;
         } else if (this._analyzedApplicationMethods != var2._analyzedApplicationMethods) {
            return false;
         } else if (java.lang.Float.compare(this.analyzedApplicationMethodsRatio, var2.analyzedApplicationMethodsRatio) != 0) {
            return false;
         } else if (this._analyzedLibraryMethods != var2._analyzedLibraryMethods) {
            return false;
         } else if (java.lang.Float.compare(this.analyzedLibraryMethodsRatio, var2.analyzedLibraryMethodsRatio) != 0) {
            return false;
         } else {
            return this.serializedReports == var2.serializedReports;
         }
      }
   }

   fun ProjectMetrics() {
      this(null, null, 0, 0, 0, 0, 0, 0, 0.0F, 0, 0.0F, 0, 0, 0, 0L, 0L, 0L, 0, 0, 0, 0.0F, 0, 0.0F, 0, 16777215, null);
   }

   public companion object {
      public fun serializer(): KSerializer<ProjectMetrics> {
         return ProjectMetrics.$serializer.INSTANCE as KSerializer<ProjectMetrics>;
      }
   }
}
