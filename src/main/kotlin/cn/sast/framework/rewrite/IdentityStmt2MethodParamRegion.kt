package cn.sast.framework.rewrite

import com.feysh.corax.cache.analysis.SootInfoCache
import com.feysh.corax.config.api.report.Region
import mu.KotlinLogging
import soot.Body
import soot.BodyTransformer
import soot.Unit
import soot.tagkit.SourceLnPosTag
import soot.tagkit.Tag
import soot.jimple.IdentityStmt
import soot.jimple.ParameterRef
import soot.jimple.ThisRef
import soot.options.Options

/**
 * Adds/updates a [SourceLnPosTag] on every *IdentityStmt* so downstream analyses can map
 * `this` / *parameter* references back to precise source locations.
 */
class IdentityStmt2MethodParamRegion(private val info: SootInfoCache) : BodyTransformer() {

   private val logger = KotlinLogging.logger {}

   override fun internalTransform(body: Body, phaseName: String, options: Map<String, String>) {
      if (Options.v().verbose()) logger.debug("[${body.method.name}] rewrite IdentityStmt region …")

      // Nothing to do for trivial static methods with no parameters
      if (body.method.isStatic && body.method.parameterCount == 0) return

      val decl = info.sootHost2decl(body.method) ?: return
      val paramRegions: List<Region?> = decl.decl.parameters.map { Region(it).takeIf { r -> r.startLine >= 0 } }
      val thisRegion: Region? = Region(decl.nameDecl).takeIf { it.startLine >= 0 }

      body.units.filterIsInstance<IdentityStmt>().forEach { stmt ->
         val region = when (val right = stmt.rightOp) {
            is ThisRef      -> thisRegion
            is ParameterRef -> paramRegions.getOrNull(right.index)
            else            -> null
         }
         region?.let { attachTag(stmt, it) }
      }
   }

   private fun attachTag(unit: Unit, region: Region) {
      if (!Options.v().keep_line_number()) return
      unit.tags.removeIf { it is SourceLnPosTag }
      unit.addTag(SourceLnPosTag(region.startLine, region.endLine, region.startColumn, region.endColumn) as Tag)
   }

   companion object { const val PHASE = "wjtp.idStmt2Region" }
}
