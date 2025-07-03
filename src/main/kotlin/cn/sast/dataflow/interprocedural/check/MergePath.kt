package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.PersistentSet.Builder
import soot.Unit

public class MergePath private constructor(node: Unit, all: PersistentSet<IPath>) : IPath() {
    public override val node: Unit
    public val all: PersistentSet<IPath>

    public var hash: Int? = null
        internal set

    init {
        this.node = node
        this.all = all
    }

    public override fun equivTo(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other !is MergePath) {
            return false
        } else if (this.equivHashCode() != other.equivHashCode()) {
            return false
        } else if (this.node != other.node) {
            return false
        } else {
            return this.all === other.all
        }
    }

    public override fun equivHashCode(): Int {
        var result = hash
        if (result == null) {
            result = 31 * System.identityHashCode(node) + System.identityHashCode(all)
            hash = result
        }
        return result
    }

    public companion object {
        public fun v(env: HeapValuesEnv, a: IPath, b: IPath): IPath {
            if (a === b) {
                return a
            } else {
                val all: Builder<IPath> = ExtensionsKt.persistentHashSetOf<IPath>().builder()
                if (a is MergePath) {
                    all.addAll(a.all)
                } else {
                    all.add(a)
                }

                if (b is MergePath) {
                    all.addAll(b.all)
                } else {
                    all.add(b)
                }

                return v(env.node, all.build())
            }
        }

        public fun v(env: HeapValuesEnv, paths: Set<IPath>): IPath {
            if (paths.isEmpty()) {
                return UnknownPath.v(env)
            } else if (paths.size == 1) {
                return paths.first()
            } else {
                val all: Builder<IPath> = ExtensionsKt.persistentHashSetOf<IPath>().builder()

                for (path in paths) {
                    if (path is MergePath) {
                        all.addAll(path.all)
                    } else {
                        all.add(path)
                    }
                }

                return v(env.node, all.build())
            }
        }

        private fun v(node: Unit, set: PersistentSet<IPath>): MergePath {
            return IPath.interner.intern(MergePath(node, IPath.specialInterner.intern(set)))
        }
    }
}