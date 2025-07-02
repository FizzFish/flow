package cn.sast.dataflow.infoflow.manager

import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkCategory

class SourceSinkCategory(
   val detector: String,
   val pattern: String
) : ISourceSinkCategory {

   override fun getHumanReadableDescription(): String = ""

   override fun getID(): String = pattern

   override fun toString(): String = "$detector::$pattern"
}
