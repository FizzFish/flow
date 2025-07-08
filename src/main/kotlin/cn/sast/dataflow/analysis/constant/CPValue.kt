package cn.sast.dataflow.analysis.constant

open class CPValue private constructor(val value: Int?) {

   fun value(): Int? = value

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is CPValue) return false
      if (this === undef && other === nac) return false
      if (this === nac && other === undef) return false
      return value == other.value
   }

   override fun hashCode(): Int = value.hashCode()

   override fun toString(): String = when (this) {
      nac -> "NAC"
      undef -> "UNDEF"
      else -> value.toString()
   }

   companion object {
      val nac = CPValue(null)
      val undef = CPValue(null)

      fun makeConstant(value: Int): CPValue = CPValue(value)
      fun makeConstant(value: Boolean): CPValue = if (value) makeConstant(1) else makeConstant(0)

      fun meetValue(v1: CPValue, v2: CPValue): CPValue = when {
         v1 === undef -> v2
         v2 === undef -> v1
         v1 != nac && v2 != nac -> if (v1.value == v2.value) makeConstant(v1.value!!) else nac
         else -> nac
      }
   }
}