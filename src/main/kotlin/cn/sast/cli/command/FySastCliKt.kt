package cn.sast.cli.command

import java.io.File

public const val defaultConfigPathName: String = "CORAX_CONFIG_DEFAULT_DIR"

public val defaultConfigDir: String?
    get() = System.getenv("CORAX_CONFIG_DEFAULT_DIR") ?: System.getProperty("CORAX_CONFIG_DEFAULT_DIR")

internal const val ANCHOR_POINT_FILE: String = ".corax${File.separator}anchor_point"
public const val MAPPING_FILE_NAME: String = "checker_name_mapping.json"