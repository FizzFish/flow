@file:SourceDebugExtension(["SMAP\nSourceLocatorPlus.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlusKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,116:1\n1628#2,3:117\n1797#2,3:120\n*S KotlinDebug\n*F\n+ 1 SourceLocatorPlus.kt\ncn/sast/framework/SourceLocatorPlusKt\n*L\n23#1:117,3\n27#1:120,3\n*E\n"])

package cn.sast.framework

import cn.sast.common.IResource
import cn.sast.common.Resource
import java.io.File
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import soot.Scene

public fun sootClassPathsCvt(sourceDir: Set<IResource>): Set<IResource> {
    val srcTranslate: Iterable<IResource> = sourceDir
    val resultSet = LinkedHashSet<String>()

    for (item in srcTranslate) {
        resultSet.add(item.getAbsolutePath())
    }

    val pathsList = resultSet.toMutableList()
    val sootClassPath = Scene.v().sootClassPath
    pathsList.addAll(sootClassPath.split(File.pathSeparator).toList())

    val finalSet = LinkedHashSet<IResource>()

    for (element in pathsList) {
        val aPath = element as String
        if ("VIRTUAL_FS_FOR_JDK" == aPath) {
            finalSet.add(Resource.INSTANCE.of(System.getProperty("java.home")))
        } else {
            finalSet.add(Resource.INSTANCE.of(aPath))
        }
    }

    return finalSet
}