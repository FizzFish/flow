package cn.sast.cli.command

import java.io.File

public const val defaultConfigPathName: String = "CORAX_CONFIG_DEFAULT_DIR"

public final val defaultConfigDir: String?
   public final get() {
      var var10000: java.lang.String = System.getenv("CORAX_CONFIG_DEFAULT_DIR");
      if (var10000 == null) {
         var10000 = System.getProperty("CORAX_CONFIG_DEFAULT_DIR");
      }

      return var10000;
   }


internal final val ANCHOR_POINT_FILE: String = ".corax${File.separator}anchor_point"
public const val MAPPING_FILE_NAME: String = "checker_name_mapping.json"
