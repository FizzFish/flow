@file:SourceDebugExtension(["SMAP\nSourceLocatorPlus.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlusKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,116:1\n1628#2,3:117\n1797#2,3:120\n*S KotlinDebug\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlusKt\n*L\n23#1:117,3\n27#1:120,3\n*E\n"])

package cn.sast.framework

import cn.sast.common.IResource
import cn.sast.common.Resource
import java.io.File
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.Scene

public fun sootClassPathsCvt(sourceDir: Set<IResource>): Set<IResource> {
   val srcTranslate: java.lang.Iterable = sourceDir;
   val `$this$fold$iv`: java.util.Collection = new LinkedHashSet();

   for (Object item$iv : srcTranslate) {
      `$this$fold$iv`.add((`accumulator$iv` as IResource).getAbsolutePath());
   }

   val var13: java.util.List = CollectionsKt.toMutableList(`$this$fold$iv`);
   val var10001: java.lang.String = Scene.v().getSootClassPath();
   var13.addAll(StringsKt.split$default(var10001, new java.lang.String[]{File.pathSeparator}, false, 0, 6, null));
   val var14: java.lang.Iterable = var13;
   var var19: Any = new LinkedHashSet();

   for (Object element$iv : var14) {
      val aPath: java.lang.String = var21 as java.lang.String;
      if ("VIRTUAL_FS_FOR_JDK" == var21 as java.lang.String) {
         val var23: Resource = Resource.INSTANCE;
         val var10002: java.lang.String = System.getProperty("java.home");
         var19.add(var23.of(var10002));
      } else {
         var19.add(Resource.INSTANCE.of(aPath));
      }

      var19 = var19;
   }

   return (java.util.Set<IResource>)var19;
}
