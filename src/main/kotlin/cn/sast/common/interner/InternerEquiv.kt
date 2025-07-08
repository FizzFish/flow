package cn.sast.common.interner

interface InternerEquiv {
   fun equivTo(other: Any?): Boolean

   fun equivHashCode(): Int
}
