package cn.sast.framework.plugin

import cn.sast.api.AnalyzerEnv
import cn.sast.api.config.SaConfig
import cn.sast.common.IResDirectory
import cn.sast.common.IResFile
import cn.sast.common.IResource
import cn.sast.common.Resource
import cn.sast.framework.AnalyzeTaskRunner
import com.feysh.corax.config.api.IConfigPluginExtension
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.Base64
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.function.BooleanSupplier
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import org.pf4j.ClassLoadingStrategy
import org.pf4j.CompoundPluginLoader
import org.pf4j.CompoundPluginRepository
import org.pf4j.DefaultPluginLoader
import org.pf4j.DefaultPluginManager
import org.pf4j.DefaultPluginRepository
import org.pf4j.ManifestPluginDescriptorFinder
import org.pf4j.PluginClassLoader
import org.pf4j.PluginDescriptor
import org.pf4j.PluginDescriptorFinder
import org.pf4j.PluginLoader
import org.pf4j.PluginRepository
import org.pf4j.PluginWrapper
import org.reflections.Configuration
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

@SourceDebugExtension(["SMAP\nConfigPluginLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,197:1\n1#2:198\n774#3:199\n865#3,2:200\n1557#3:202\n1628#3,3:203\n*S KotlinDebug\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader\n*L\n133#1:199\n133#1:200,2\n180#1:202\n180#1:203,3\n*E\n"])
public class ConfigPluginLoader(configDirs: List<IResource>, pluginsDirs: List<IResDirectory>) {
   public final val configDirs: List<IResource>
   private final val pluginsDirs: List<IResDirectory>

   public final val pluginManager: cn.sast.framework.plugin.ConfigPluginLoader.PluginManager
      public final get() {
         return this.pluginManager$delegate.getValue() as ConfigPluginLoader.PluginManager;
      }


   private final val serializersModule: SerializersModule
      private final get() {
         return this.serializersModule$delegate.getValue() as SerializersModule;
      }


   init {
      this.configDirs = configDirs;
      this.pluginsDirs = pluginsDirs;
      this.pluginManager$delegate = LazyKt.lazy(ConfigPluginLoader::pluginManager_delegate$lambda$0);
      this.serializersModule$delegate = LazyKt.lazy(ConfigPluginLoader::serializersModule_delegate$lambda$1);
   }

   private fun getConfigExtensions(pluginId: String?): List<IConfigPluginExtension> {
      val var10000: java.util.List;
      if (pluginId != null) {
         var10000 = this.getPluginManager().getExtensions(IConfigPluginExtension.class, pluginId);
      } else {
         var10000 = this.getPluginManager().getExtensions(IConfigPluginExtension.class);
      }

      logger.info(ConfigPluginLoader::getConfigExtensions$lambda$2);
      return var10000;
   }

   public fun loadFromName(pluginId: String?, name: String?): SaConfig {
      if (name == null) {
         logger.info(ConfigPluginLoader::loadFromName$lambda$3);
      }

      val configExtensions: java.util.List = this.getConfigExtensions(pluginId);
      if (configExtensions.isEmpty()) {
         throw new IllegalStateException(("not found IConfigPluginExtension in: ${this.pluginsDirs}").toString());
      } else {
         val var10000: java.lang.Iterable = this.pluginsDirs;
         val var10001: java.lang.String = File.pathSeparator;
         val ps: java.lang.String = CollectionsKt.joinToString$default(var10000, var10001, null, null, 0, null, null, 62, null);
         val var18: IConfigPluginExtension;
         if (name == null) {
            if (configExtensions.size() != 1) {
               throw new IllegalStateException(
                  ("you need choose which one of names: [ \n\t${CollectionsKt.joinToString$default(
                        configExtensions, ",\n\t", null, null, 0, null, ConfigPluginLoader::loadFromName$lambda$5, 30, null
                     )} ]")
                     .toString()
               );
            }

            var18 = CollectionsKt.first(configExtensions) as IConfigPluginExtension;
         } else {
            val `$this$filter$iv`: java.lang.Iterable = configExtensions;
            val `destination$iv$iv`: java.util.Collection = new ArrayList();

            for (Object element$iv$iv : $this$filter$iv) {
               if ((`element$iv$iv` as IConfigPluginExtension).getName() == name) {
                  `destination$iv$iv`.add(`element$iv$iv`);
               }
            }

            val choose: java.util.List = `destination$iv$iv` as java.util.List;
            if ((`destination$iv$iv` as java.util.List).isEmpty()) {
               throw new IllegalStateException(("your choose: $name not found in plugins dir: $ps").toString());
            }

            if (choose.size() != 1) {
               throw new IllegalStateException(("dup choose: $name in plugins dir: $ps. choose: $choose").toString());
            }

            var18 = CollectionsKt.first(choose) as IConfigPluginExtension;
         }

         logger.info(ConfigPluginLoader::loadFromName$lambda$7);
         return new SaConfig(null, null, ExtensionsKt.toImmutableSet(var18.getUnits()) as java.util.Set, var18.getSootConfig(), null, 3, null);
      }
   }

   public fun searchCheckerUnits(ymlConfig: IResFile, checkerFilter: CheckerFilterByName?): SaConfig {
      val configFromYml: SAConfiguration = SAConfiguration.Companion.deserialize(this.getSerializersModule(), ymlConfig);
      val needNormalize: Boolean = configFromYml.sort();
      val defs: PluginDefinitions = PluginDefinitions.Companion.load(this.getPluginManager());
      val hasChange: Boolean = configFromYml.supplementAndMerge(defs, ymlConfig.toString());
      val var10000: IResource = ymlConfig.getParent();
      if (var10000 != null) {
         if (needNormalize || hasChange) {
            val normalizedConfig: IResFile = var10000.resolve(
                  "${StringsKt.dropLast(StringsKt.substringBeforeLast$default(ymlConfig.getName(), ymlConfig.getExtension(), null, 2, null), 1)}.normalize.yml"
               )
               .toFile();
            configFromYml.sort();
            configFromYml.serialize(this.getSerializersModule(), normalizedConfig);
            logger.info(ConfigPluginLoader::searchCheckerUnits$lambda$9$lambda$8);
         }
      }

      return configFromYml.filter(defs, checkerFilter);
   }

   public fun makeTemplateYml(tempFile: IResFile) {
      val defs: PluginDefinitions = PluginDefinitions.Companion.load(this.getPluginManager());
      val emptyYaml: SAConfiguration = new SAConfiguration(null, null, null, null, 15, null);
      emptyYaml.supplementAndMerge(defs, null);
      emptyYaml.sort();
      emptyYaml.serialize(this.getSerializersModule(), tempFile);
      logger.info(ConfigPluginLoader::makeTemplateYml$lambda$10);
   }

   private fun loadPlugin(): cn.sast.framework.plugin.ConfigPluginLoader.PluginManager {
      logger.info(ConfigPluginLoader::loadPlugin$lambda$11);
      val plugins: java.lang.Iterable = this.pluginsDirs;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.pluginsDirs, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as IResDirectory).getPath());
      }

      val pluginManager: ConfigPluginLoader.PluginManager = new ConfigPluginLoader.PluginManager(`destination$iv$iv` as java.util.List, null, 2, null);
      pluginManager.loadPlugins();
      pluginManager.startPlugins();
      if (pluginManager.getPlugins().isEmpty()) {
         throw new IllegalStateException("no config plugin found".toString());
      } else {
         return pluginManager;
      }
   }

   @JvmStatic
   fun `pluginManager_delegate$lambda$0`(`this$0`: ConfigPluginLoader): ConfigPluginLoader.PluginManager {
      return `this$0`.loadPlugin();
   }

   @JvmStatic
   fun `serializersModule_delegate$lambda$1`(`this$0`: ConfigPluginLoader): SerializersModule {
      return PluginDefinitions.Companion.getSerializersModule(`this$0`.getPluginManager());
   }

   @JvmStatic
   fun `getConfigExtensions$lambda$2`(`$configExtensions`: java.util.List): Any {
      val var2: Array<Any> = new Object[]{`$configExtensions`.size(), IConfigPluginExtension.class.getName()};
      val var10000: java.lang.String = java.lang.String.format("Found %d extensions for extension point '%s'", Arrays.copyOf(var2, var2.length));
      return var10000;
   }

   @JvmStatic
   fun `loadFromName$lambda$3`(`this$0`: ConfigPluginLoader): Any {
      return "Automatically search for the SA-Config under path '${`this$0`.pluginsDirs}', with the requirement that there can only exist one config.";
   }

   @JvmStatic
   fun `loadFromName$lambda$5`(`$ps`: java.lang.String, it: IConfigPluginExtension): java.lang.CharSequence {
      return "${it.getName()}@$`$ps`";
   }

   @JvmStatic
   fun `loadFromName$lambda$7`(`$config`: IConfigPluginExtension): Any {
      return "use config method for entry: ${`$config`.getName()} in ${Resource.INSTANCE.locateAllClass(`$config`.getClass())}";
   }

   @JvmStatic
   fun `searchCheckerUnits$lambda$9$lambda$8`(`$normalizedConfig`: IResFile): Any {
      return "Serialized a normalized SA-Configuration yml file: $`$normalizedConfig`";
   }

   @JvmStatic
   fun `makeTemplateYml$lambda$10`(`$tempFile`: IResFile): Any {
      return "Serialized template SA-Configuration file: $`$tempFile`";
   }

   @JvmStatic
   fun `loadPlugin$lambda$11`(`this$0`: ConfigPluginLoader): Any {
      return "Plugin directory: ${`this$0`.pluginsDirs}";
   }

   @JvmStatic
   fun `logger$lambda$13`(): Unit {
      return Unit.INSTANCE;
   }

   public companion object {
      private final val logger: KLogger
   }

   @SourceDebugExtension(["SMAP\nConfigPluginLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader$PluginManager\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,197:1\n1863#2,2:198\n1279#2,2:204\n1293#2,4:206\n126#3:200\n153#3,3:201\n*S KotlinDebug\n*F\n+ 1 ConfigPluginLoader.kt\ncn/sast/framework/plugin/ConfigPluginLoader$PluginManager\n*L\n65#1:198,2\n81#1:204,2\n81#1:206,4\n81#1:200\n81#1:201,3\n*E\n"])
   public class PluginManager(importPaths: List<Path>, classLoadStrategy: ClassLoadingStrategy = ClassLoadingStrategy.ADP) : DefaultPluginManager(importPaths) {
      private final val classLoadStrategy: ClassLoadingStrategy

      public final val pluginToReflections: Map<PluginWrapper, Reflections>
         public final get() {
            return this.pluginToReflections$delegate.getValue() as MutableMap<PluginWrapper, Reflections>;
         }


      init {
         this.classLoadStrategy = classLoadStrategy;
         this.pluginToReflections$delegate = LazyKt.lazy(ConfigPluginLoader.PluginManager::pluginToReflections_delegate$lambda$3);
      }

      protected open fun createPluginDescriptorFinder(): PluginDescriptorFinder {
         return (new ManifestPluginDescriptorFinder()) as PluginDescriptorFinder;
      }

      protected open fun createPluginRepository(): PluginRepository? {
         return new CompoundPluginRepository().add((new DefaultPluginRepository(this.getPluginsRoots())) as PluginRepository, new BooleanSupplier(this) {
            {
               this.this$0 = `$receiver`;
            }

            @Override
            public final boolean getAsBoolean() {
               return this.this$0.isNotDevelopment();
            }
         }) as PluginRepository;
      }

      protected open fun createPluginLoader(): PluginLoader? {
         return new CompoundPluginLoader()
            .add(
               (
                  new DefaultPluginLoader(this) {
                     {
                        super(`$receiver` as org.pf4j.PluginManager);
                        this.this$0 = `$receiver`;
                     }

                     protected PluginClassLoader createPluginClassLoader(Path pluginPath, PluginDescriptor pluginDescriptor) {
                        return new PluginClassLoader(
                           this.pluginManager,
                           pluginDescriptor,
                           this.getClass().getClassLoader(),
                           ConfigPluginLoader.PluginManager.access$getClassLoadStrategy$p(this.this$0)
                        );
                     }
                  }
               ) as PluginLoader,
               new BooleanSupplier(this) {
                  {
                     this.this$0 = `$receiver`;
                  }

                  @Override
                  public final boolean getAsBoolean() {
                     return this.this$0.isNotDevelopment();
                  }
               }
            ) as PluginLoader;
      }

      public open fun startPlugins() {
         val `$this$forEach$iv`: java.lang.Iterable;
         for (Object element$iv : $this$forEach$iv) {
            val it: PluginWrapper = `element$iv` as PluginWrapper;

            try {
               val var9: ClassLoader = it.getPluginClassLoader();
               val var10001: ByteArray = Base64.getDecoder().decode("Y29tLmRpZm9jZC5WZXJpZnlKTkk=");
               AnalyzeTaskRunner.Companion.setV3r14yJn1Class(var9.loadClass(StringsKt.decodeToString(var10001)));
            } catch (var8: Exception) {
            }

            val var10: PluginDefinitions.Companion = PluginDefinitions.Companion;
            val var11: java.lang.String = (`element$iv` as PluginWrapper).getPluginId();
            if (var10.checkCommercial(var11)) {
               AnalyzerEnv.INSTANCE.getBvs1n3ss().getAndAdd(1);
            }
         }

         super.startPlugins();
      }

      @JvmStatic
      fun `pluginToReflections_delegate$lambda$3`(`this$0`: ConfigPluginLoader.PluginManager): java.util.Map {
         var var10000: java.util.Map = `this$0`.plugins;
         val `$this$associateWithTo$iv$iv`: java.util.Collection = new ArrayList(var10000.size());

         for (Entry item$iv$iv : var10000.entrySet()) {
            `$this$associateWithTo$iv$iv`.add(`element$iv$iv`.getValue() as PluginWrapper);
         }

         val var16: java.lang.Iterable = `$this$associateWithTo$iv$iv` as java.util.List;
         val `result$iv`: LinkedHashMap = new LinkedHashMap(
            RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(`$this$associateWithTo$iv$iv` as java.util.List, 10)), 16)
         );

         for (Object element$iv$iv : var16) {
            var10000 = `result$iv`;
            val var23: ClassLoader = (var20 as PluginWrapper).getPluginClassLoader();
            val classLoader: URLClassLoader = var23 as URLClassLoader;
            val var24: ConfigurationBuilder = new ConfigurationBuilder();
            val var10001: Array<URL> = classLoader.getURLs();
            var10000.put(var20, new Reflections(var24.addUrls(ArraysKt.toList(var10001)).addClassLoaders(new ClassLoader[]{classLoader}) as Configuration));
         }

         return `result$iv`;
      }
   }
}
