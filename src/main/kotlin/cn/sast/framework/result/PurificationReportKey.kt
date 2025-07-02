package cn.sast.framework.result

import cn.sast.api.report.BugPathEvent
import cn.sast.api.report.IBugResInfo

public data class PurificationReportKey(bugResFile: IBugResInfo, line: Int, checkName: String, firstEvent: BugPathEvent) {
   public final val bugResFile: IBugResInfo
   public final val line: Int
   public final val checkName: String
   public final val firstEvent: BugPathEvent

   init {
      this.bugResFile = bugResFile;
      this.line = line;
      this.checkName = checkName;
      this.firstEvent = firstEvent;
   }

   public operator fun component1(): IBugResInfo {
      return this.bugResFile;
   }

   public operator fun component2(): Int {
      return this.line;
   }

   public operator fun component3(): String {
      return this.checkName;
   }

   public operator fun component4(): BugPathEvent {
      return this.firstEvent;
   }

   public fun copy(
      bugResFile: IBugResInfo = this.bugResFile,
      line: Int = this.line,
      checkName: String = this.checkName,
      firstEvent: BugPathEvent = this.firstEvent
   ): PurificationReportKey {
      return new PurificationReportKey(bugResFile, line, checkName, firstEvent);
   }

   public override fun toString(): String {
      return "PurificationReportKey(bugResFile=${this.bugResFile}, line=${this.line}, checkName=${this.checkName}, firstEvent=${this.firstEvent})";
   }

   public override fun hashCode(): Int {
      return ((this.bugResFile.hashCode() * 31 + Integer.hashCode(this.line)) * 31 + this.checkName.hashCode()) * 31 + this.firstEvent.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is PurificationReportKey) {
         return false;
      } else {
         val var2: PurificationReportKey = other as PurificationReportKey;
         if (!(this.bugResFile == (other as PurificationReportKey).bugResFile)) {
            return false;
         } else if (this.line != var2.line) {
            return false;
         } else if (!(this.checkName == var2.checkName)) {
            return false;
         } else {
            return this.firstEvent == var2.firstEvent;
         }
      }
   }
}
