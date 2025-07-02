package cn.sast.framework

import cn.sast.api.util.OthersKt
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.feysh.corax.config.api.IMethodMatch
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.Arrays
import java.util.LinkedHashSet
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import mu.KLogger
import mu.KotlinLogging
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.SourceLocator

@SourceDebugExtension(["SMAP\nEntryPointCreatorFactory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EntryPointCreatorFactory.kt\ncn/sast/framework/EntryPointCreatorFactory\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Strings.kt\nkotlin/text/StringsKt__StringsKt\n*L\n1#1,73:1\n1863#2:74\n1864#2:98\n108#3:75\n80#3,22:76\n*S KotlinDebug\n*F\n+ 1 EntryPointCreatorFactory.kt\ncn/sast/framework/EntryPointCreatorFactory\n*L\n32#1:74\n32#1:98\n52#1:75\n52#1:76,22\n*E\n"])
public object EntryPointCreatorFactory {
   private final val logger: KLogger = KotlinLogging.INSTANCE.logger(EntryPointCreatorFactory::logger$lambda$0)

   private fun lookFromDir(res: MutableSet<SootClass>, direction: IResource) {
      val scene: Scene = Scene.v();

      for (java.lang.String cl : SourceLocator.v().getClassesUnder(direction.getAbsolutePath())) {
         val var10000: SootClass = scene.loadClass(cl, 2);
         res.add(var10000);
      }
   }

   private fun loadClass(className: String) {
      Scene.v().forceResolve(className, 3);
      Scene.v().loadClassAndSupport(className);
   }

   public fun getEntryPointFromArgs(args: List<String>): () -> Set<SootMethod> {
      return EntryPointCreatorFactory::getEntryPointFromArgs$lambda$5;
   }

   @JvmStatic
   fun `logger$lambda$0`(): Unit {
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `getEntryPointFromArgs$lambda$5$lambda$4$lambda$3$lambda$2`(`$line`: ObjectRef): java.lang.String {
      return "soot method: ${`$line`.element} not exists";
   }

   @JvmStatic
   fun `getEntryPointFromArgs$lambda$5`(`$args`: java.util.List): java.util.Set {
      label158: {
         val mSet: java.util.Set = new LinkedHashSet();

         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val arg: java.lang.String = `element$iv` as java.lang.String;
            val match: IMethodMatch = OthersKt.methodSignatureToMatcher(`element$iv` as java.lang.String);
            if (match != null) {
               val var39: Scene = Scene.v();
               val var31: java.util.List = match.matched(var39);
               if (var31.isEmpty()) {
                  throw new IllegalStateException(("method: $match not exists").toString());
               }

               mSet.addAll(var31);
            } else {
               val res: IResource = Resource.INSTANCE.of(arg);
               if (!res.getExists()) {
                  throw new IllegalStateException(("invalidate $arg").toString());
               }

               if (res.isFile()) {
                  val var10: Path = res.getPath();
                  val var11: Charset = Charsets.UTF_8;
                  val sd: Array<OpenOption> = new OpenOption[0];
                  val var32: Closeable = new InputStreamReader(Files.newInputStream(var10, Arrays.copyOf(sd, sd.length)), var11);
                  var var33: java.lang.Throwable = null;

                  try {
                     try {
                        val reader: BufferedReader = new BufferedReader(var32 as InputStreamReader);

                        while (true) {
                           val line: ObjectRef = new ObjectRef();
                           var var10001: java.lang.String = reader.readLine();
                           if (var10001 == null) {
                              break;
                           }

                           line.element = var10001;
                           val `$this$trim$iv$iv`: java.lang.CharSequence = line.element as java.lang.String;
                           var `startIndex$iv$iv`: Int = 0;
                           var `endIndex$iv$iv`: Int = `$this$trim$iv$iv`.length() - 1;
                           var `startFound$iv$iv`: Boolean = false;

                           while (true) {
                              if (`startIndex$iv$iv` <= `endIndex$iv$iv`) {
                                 val var37: Boolean = Intrinsics.compare(
                                       `$this$trim$iv$iv`.charAt(if (!`startFound$iv$iv`) `startIndex$iv$iv` else `endIndex$iv$iv`), 32
                                    )
                                    <= 0;
                                 if (!`startFound$iv$iv`) {
                                    if (!var37) {
                                       `startFound$iv$iv` = true;
                                    } else {
                                       `startIndex$iv$iv`++;
                                    }
                                    continue;
                                 }

                                 if (var37) {
                                    `endIndex$iv$iv`--;
                                    continue;
                                 }
                              }

                              line.element = `$this$trim$iv$iv`.subSequence(`startIndex$iv$iv`, `endIndex$iv$iv` + 1).toString();
                              if ((line.element as java.lang.CharSequence).length() != 0
                                 && !StringsKt.startsWith$default(line.element as java.lang.String, "-", false, 2, null)) {
                                 val var10000: EntryPointCreatorFactory = INSTANCE;
                                 var10001 = Scene.signatureToClass(line.element as java.lang.String);
                                 var10000.loadClass(var10001);
                                 val var36: SootMethod = Scene.v().grabMethod(line.element as java.lang.String);
                                 if (var36 == null) {
                                    throw new IllegalStateException(
                                       (EntryPointCreatorFactory::getEntryPointFromArgs$lambda$5$lambda$4$lambda$3$lambda$2).toString()
                                    );
                                 }

                                 mSet.add(var36);
                              }
                              break;
                           }
                        }
                     } catch (var27: java.lang.Throwable) {
                        var33 = var27;
                        throw var27;
                     }
                  } catch (var28: java.lang.Throwable) {
                     CloseableKt.closeFinally(var32, var33);
                  }

                  CloseableKt.closeFinally(var32, null);
               }
            }
         }

         return mSet;
      }
   }
}
