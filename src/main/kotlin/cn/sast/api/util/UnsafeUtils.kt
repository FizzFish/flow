package cn.sast.api.util

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodHandles.Lookup
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Arrays
import java.util.Optional
import java.util.function.Predicate
import kotlin.jvm.internal.SourceDebugExtension
import org.objectweb.asm.ClassWriter

@SourceDebugExtension(["SMAP\nUnsafeUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UnsafeUtils.kt\ncn/sast/api/util/UnsafeUtils\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,61:1\n1#2:62\n*E\n"])
public object UnsafeUtils {
    public fun <T> defineAnonymousConcreteSubclass(abstractClass: Class<T>): Class<out T> {
        if (!Modifier.isAbstract(abstractClass.modifiers)) {
            throw IllegalArgumentException("$abstractClass is not abstract")
        } else {
            val cw = ClassWriter(0)
            val superClassName = abstractClass.name.replace('.', '/')
            val packageName = UnsafeUtils::class.java.`package`.name.replace('.', '/')
            cw.visit(52, 0, "$packageName/Anonymous", null, superClassName, null)
            cw.visitEnd()

            try {
                val defineHiddenClass = Arrays.stream(Lookup::class.java.methods)
                    .filter(Predicate { method -> defineAnonymousConcreteSubclass$lambda$1(method) })
                    .findFirst()
                val classOption = Arrays.stream(Lookup::class.java.classes)
                    .filter(Predicate { clazz -> defineAnonymousConcreteSubclass$lambda$2(clazz) })
                    .findFirst()

                return if (defineHiddenClass.isPresent && classOption.isPresent) {
                    val lookup = (defineHiddenClass.get() as Method)
                        .invoke(MethodHandles.lookup(), cw.toByteArray(), true, Array.newInstance(classOption.get() as Class<*>, 0))
                    (lookup as Lookup).lookupClass().asSubclass(abstractClass)
                } else {
                    UnsafeProvider.INSTANCE
                        .unsafe
                        .javaClass
                        .getMethod("defineAnonymousClass", Class::class.java, ByteArray::class.java, Array<Any>::class.java)
                        .invoke(UnsafeProvider.INSTANCE.unsafe, UnsafeUtils::class.java, cw.toByteArray(), null) as Class<*>
                } as Class<out T>
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(e)
            } catch (e: InvocationTargetException) {
                throw IllegalStateException(e)
            } catch (e: NoSuchMethodException) {
                throw IllegalStateException(e)
            }
        }
    }

    @JvmStatic
    fun `defineAnonymousConcreteSubclass$lambda$1`(method: Method): Boolean {
        return method.name == "defineHiddenClass"
    }

    @JvmStatic
    fun `defineAnonymousConcreteSubclass$lambda$2`(clazz: Class<*>): Boolean {
        return clazz.simpleName == "ClassOption"
    }
}