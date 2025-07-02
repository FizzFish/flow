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
      if (!Modifier.isAbstract(abstractClass.getModifiers())) {
         throw new IllegalArgumentException(("$abstractClass is not abstract").toString());
      } else {
         val cw: ClassWriter = new ClassWriter(0);
         var var10000: java.lang.String = abstractClass.getName();
         val superClassName: java.lang.String = StringsKt.replace$default(var10000, '.', '/', false, 4, null);
         var10000 = UnsafeUtils.class.getPackage().getName();
         cw.visit(52, 0, "${StringsKt.replace$default(var10000, '.', '/', false, 4, null)}/Anonymous", null, superClassName, null);
         cw.visitEnd();

         try {
            val defineHiddenClass: Optional = Arrays.stream(Lookup.class.getMethods())
               .filter(new Predicate(UnsafeUtils::defineAnonymousConcreteSubclass$lambda$1) {
                  {
                     this.function = function;
                  }
               })
               .findFirst();
            val var14: Optional = Arrays.stream(Lookup.class.getClasses()).filter(new Predicate(UnsafeUtils::defineAnonymousConcreteSubclass$lambda$2) {
               {
                  this.function = function;
               }
            }).findFirst();
            val var23: Class;
            if (defineHiddenClass.isPresent() && var14.isPresent()) {
               var10000 = (java.lang.String)(defineHiddenClass.get() as Method)
                  .invoke(MethodHandles.lookup(), cw.toByteArray(), true, Array.newInstance(var14.get() as Class<?>, 0));
               var23 = (var10000 as Lookup).lookupClass().asSubclass(abstractClass);
            } else {
               val var15: Any = UnsafeProvider.INSTANCE
                  .getUnsafe()
                  .getClass()
                  .getMethod("defineAnonymousClass", Class.class, byte[].class, Object[].class)
                  .invoke(UnsafeProvider.INSTANCE.getUnsafe(), UnsafeUtils.class, cw.toByteArray(), null);
               var23 = var15 as Class;
            }

            return var23;
         } catch (var9: IllegalAccessException) {
            throw new IllegalStateException(var9);
         } catch (var10: InvocationTargetException) {
            throw new IllegalStateException(var10);
         } catch (var11: NoSuchMethodException) {
            throw new IllegalStateException(var11);
         }
      }
   }

   @JvmStatic
   fun `defineAnonymousConcreteSubclass$lambda$1`(method: Method): Boolean {
      return method.getName() == "defineHiddenClass";
   }

   @JvmStatic
   fun `defineAnonymousConcreteSubclass$lambda$2`(clazz: Class): Boolean {
      return clazz.getSimpleName() == "ClassOption";
   }
}
