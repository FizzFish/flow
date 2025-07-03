@file:SourceDebugExtension(["SMAP\nMainConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfigKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 SerializersModuleBuilders.kt\nkotlinx/serialization/modules/SerializersModuleBuildersKt\n*L\n1#1,463:1\n1#2:464\n31#3,2:465\n254#3,9:467\n33#3:476\n*S KotlinDebug\n*F\n+ 1 MainConfig.kt\ncn/sast/api/config/MainConfigKt\n*L\n51#1:465,2\n52#1:467,9\n51#1:476\n*E\n"])

package cn.sast.api.config

import cn.sast.api.incremental.IncrementalAnalyze
import cn.sast.api.incremental.IncrementalAnalyzeByChangeFiles
import cn.sast.common.IResDirectory
import cn.sast.common.IResource
import cn.sast.common.Resource
import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.feysh.corax.config.api.IAnalysisDepends
import com.feysh.corax.config.api.PhantomAnalysisDepends
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.modules.SerializersModule

public val serializersModule: SerializersModule = TODO("FIXME — uninitialized serializersModule")
public val yamlConfiguration: YamlConfiguration = YamlConfiguration(
    encodeDefaults = true,
    polymorphismStyle = PolymorphismStyle.Tag,
    sequenceStyle = SequenceStyle.Block
)
public val yamlFormat: Yaml = TODO("FIXME — uninitialized yamlFormat")

public fun MainConfig.simpleIAnalysisDepends(): IAnalysisDepends {
    val var2: IncrementalAnalyze = this.getIncrementAnalyze()
    var analysisDepends: IAnalysisDepends? = (var2 as? IncrementalAnalyzeByChangeFiles)?.getSimpleDeclAnalysisDependsGraph()
    
    if (analysisDepends == null) {
        analysisDepends = PhantomAnalysisDepends.INSTANCE
    }

    return analysisDepends
}

public fun MainConfig.interProceduralAnalysisDepends(): IAnalysisDepends {
    val var2: IncrementalAnalyze = this.getIncrementAnalyze()
    var analysisDepends: IAnalysisDepends? = (var2 as? IncrementalAnalyzeByChangeFiles)?.getInterProceduralAnalysisDependsGraph()
    
    if (analysisDepends == null) {
        analysisDepends = PhantomAnalysisDepends.INSTANCE
    }

    return analysisDepends
}

public fun MainConfig.skipResourceInArchive(res: IResource): Boolean {
    if (!res.isJarScheme()) {
        return false
    } else {
        try {
            return !this.getSourcePathZFS().contains(Resource.INSTANCE.getZipFileSystem(res.getSchemePath()))
        } catch (var3: Exception) {
            return true
        }
    }
}

public fun checkerInfoDir(configDirs: List<IResource>, stopOnError: Boolean = true): IResDirectory? {
    if (configDirs.size != 1) {
        if (stopOnError) {
            throw IllegalStateException("Only one plugin folder could be exists: $configDirs")
        } else {
            return null
        }
    } else {
        val analysisConfigDir: Path = configDirs.single().getPath()
        val checkerInfoDir: Path = analysisConfigDir.normalize()
        val linkOptions = emptyArray<LinkOption>()
        
        if (!Files.exists(checkerInfoDir, *linkOptions)) {
            if (stopOnError) {
                throw IllegalStateException("$checkerInfoDir is not exists")
            } else {
                return null
            }
        } else {
            val hasCheckerInfo = if (Files.exists(checkerInfoDir, *linkOptions)) {
                Files.exists(checkerInfoDir.resolve("checker_info.csv"), *linkOptions)
            } else {
                false
            }

            val checkerInfoDirInConfigProjectDevPath: Path? = if (hasCheckerInfo) checkerInfoDir else null
            if (checkerInfoDirInConfigProjectDevPath != null) {
                return Resource.INSTANCE.dirOf(checkerInfoDirInConfigProjectDevPath)
            } else {
                val fallbackPath = analysisConfigDir.resolve("../../Canary/analysis-config").normalize()
                val hasFallbackCheckerInfo = if (Files.exists(fallbackPath, *linkOptions)) {
                    Files.exists(fallbackPath.resolve("checker_info.csv"), *linkOptions)
                } else {
                    false
                }

                val fallbackDir = if (hasFallbackCheckerInfo) fallbackPath else null
                if (fallbackDir != null) {
                    return Resource.INSTANCE.dirOf(fallbackDir)
                } else if (stopOnError) {
                    throw IllegalStateException("checker_info.csv not exists in $checkerInfoDir")
                } else {
                    return null
                }
            }
        }
    }
}

@JvmSynthetic
internal fun checkerInfoDir$default(configDirs: List<IResource>, stopOnError: Boolean, mask: Int, any: Any): IResDirectory? {
    val actualStopOnError = if ((mask and 2) != 0) true else stopOnError
    return checkerInfoDir(configDirs, actualStopOnError)
}