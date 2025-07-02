package cn.sast.framework.plugin

import cn.sast.api.config.BuiltinAnalysisConfig
import cn.sast.api.config.MainConfigKt
import cn.sast.api.config.PreAnalysisConfig
import cn.sast.api.config.SaConfig
import cn.sast.api.util.Kotlin_extKt
import cn.sast.common.GLB
import cn.sast.common.IResFile
import cn.sast.framework.plugin.CheckersConfig.CheckTypeConfig
import cn.sast.framework.plugin.PluginDefinitions.Definition
import com.charleskorn.kaml.Yaml
import com.feysh.corax.config.api.AIAnalysisUnit
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.CheckerUnit
import com.feysh.corax.config.api.ISootInitializeHandler
import com.feysh.corax.config.api.PreAnalysisUnit
import com.feysh.corax.config.api.SAOptions
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.util.ArrayList
import java.util.Arrays
import java.util.IdentityHashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.NoSuchElementException
import java.util.Comparator
import kotlin.comparisons.compareValues
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.collections.immutable.ExtensionsKt
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModule
import mu.KLogger
import soot.Main
import soot.Scene
import soot.jimple.toolkits.callgraph.CallGraph
import soot.options.Options

@Serializable
@SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,557:1\n126#2:558\n153#2,3:559\n126#2:645\n153#2,3:646\n1557#3:562\n1628#3,3:563\n1261#3,4:566\n1628#3,3:570\n230#3,2:573\n1628#3,3:575\n774#3:578\n865#3,2:579\n2632#3,3:581\n774#3:584\n865#3,2:585\n1611#3,9:587\n1863#3:596\n1864#3:598\n1620#3:599\n1611#3,9:600\n1863#3:609\n1864#3:611\n1620#3:612\n1611#3,9:613\n1863#3:622\n1864#3:624\n1620#3:625\n1611#3,9:626\n1863#3:635\n1864#3:637\n1620#3:638\n1279#3,2:639\n1293#3,4:641\n1557#3:649\n1628#3,3:650\n1#4:597\n1#4:610\n1#4:623\n1#4:636\n1#4:660\n487#5,7:653\n*S KotlinDebug\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration\n*L\n195#1:558\n195#1:559,3\n289#1:645\n289#1:646,3\n196#1:562\n196#1:563,3\n203#1:566,4\n204#1:570,3\n245#1:573,2\n262#1:575,3\n263#1:578\n263#1:579,2\n266#1:581,3\n270#1:584\n270#1:585,2\n274#1:587,9\n274#1:596\n274#1:598\n274#1:599\n275#1:600,9\n275#1:609\n275#1:611\n275#1:612\n276#1:613,9\n276#1:622\n276#1:624\n276#1:625\n277#1:626,9\n277#1:635\n277#1:637\n277#1:638\n288#1:639,2\n288#1:641,4\n336#1:649\n336#1:650,3\n274#1:597\n275#1:610\n276#1:623\n277#1:636\n418#1:653,7\n*E\n"])
public data class SAConfiguration(builtinAnalysisConfig: BuiltinAnalysisConfig = new BuiltinAnalysisConfig(null, null, 0, 0, 15, null),
   preAnalysisConfig: PreAnalysisConfig = new PreAnalysisConfig(0, 0, 0, null, 0, 31, null),
   configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = new LinkedHashMap(),
   checkers: LinkedHashSet<CheckersConfig> = new LinkedHashSet()
) {
   private final var builtinAnalysisConfig: BuiltinAnalysisConfig
   private final var preAnalysisConfig: PreAnalysisConfig
   private final var configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>>
   private final var checkers: LinkedHashSet<CheckersConfig>

   @Transient
   private final val relatedMap: IdentityHashMap<Definition<*>, IConfig>

   @Transient
   private final val disabled: IdentityHashMap<Definition<*>, IConfig>

   private object ConfigPairComparator : Comparator<Pair<*, *>> {
      override fun compare(a: Pair<*, *>, b: Pair<*, *>): Int {
         return compareValues(a.first as Comparable<Any>, b.first as Comparable<Any>)
      }
   }

   private object CheckersConfigComparator : Comparator<CheckersConfig> {
      override fun compare(a: CheckersConfig, b: CheckersConfig): Int {
         return compareValues(a, b)
      }
   }

   init {
      this.builtinAnalysisConfig = builtinAnalysisConfig;
      this.preAnalysisConfig = preAnalysisConfig;
      this.configurations = configurations;
      this.checkers = checkers;
      this.relatedMap = new IdentityHashMap<>();
      this.disabled = new IdentityHashMap<>();
   }

   private fun linkedHashCode(): Int {
      val hash: java.util.List = new ArrayList();
      var var2: java.util.Collection = hash;
      val `$this$map$iv`: java.util.Map = this.configurations;
      var `destination$iv$iv`: java.util.Collection = new ArrayList(this.configurations.size());

      for (Entry item$iv$iv : $this$map$iv.entrySet()) {
         `destination$iv$iv`.add(CollectionsKt.toList(`item$iv$iv`.getValue() as java.lang.Iterable));
      }

      var2.add((`destination$iv$iv` as java.util.List).hashCode());
      var2 = hash;
      val `$this$map$ivx`: java.lang.Iterable = CollectionsKt.toList(this.checkers);
      `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$ivx`, 10));

      for (Object item$iv$iv : $this$map$ivx) {
         `destination$iv$iv`.add(CollectionsKt.toList((var19 as CheckersConfig).getCheckTypes()));
      }

      var2.add((`destination$iv$iv` as java.util.List).hashCode());
      return hash.hashCode();
   }

   public fun sort(): Boolean {
      val old: Int = this.linkedHashCode();
      var `$this$mapTo$iv`: java.lang.Iterable = CollectionsKt.sortedWith(MapsKt.toList(this.configurations), ConfigPairComparator);
      val `destination$iv`: java.util.Map = new LinkedHashMap();

      for (Object element$iv : $this$mapTo$iv) {
         val var17: Pair = TuplesKt.to((`item$iv` as Pair).getFirst(), SAConfigurationKt.access$getSort((`item$iv` as Pair).getSecond() as LinkedHashSet));
         `destination$iv`.put(var17.getFirst(), var17.getSecond());
      }

      this.configurations = `destination$iv` as LinkedHashMap<java.langString, LinkedHashSet<ConfigSerializable>>;
      `$this$mapTo$iv` = CollectionsKt.sortedWith(CollectionsKt.toList(this.checkers), CheckersConfigComparator);
      val var13: java.util.Collection = new LinkedHashSet();

      for (Object item$iv : $this$mapTo$iv) {
         var13.add((var16 as CheckersConfig).sort());
      }

      this.checkers = var13 as LinkedHashSet<CheckersConfig>;
      return this.linkedHashCode() != old;
   }

   private fun <T> Definition<*>.getInstance(clz: Class<T>): T? {
      return (T)(if (clz.isInstance(`$this$getInstance`.getSingleton())) `$this$getInstance`.getSingleton() else null);
   }

   private fun Definition<*>.relateConfig(): IConfig {
      val var10000: IConfig = this.relatedMap.get(`$this$relateConfig`);
      if (var10000 == null) {
         throw new IllegalStateException(("$`$this$relateConfig` not relate to a config").toString());
      } else {
         return var10000;
      }
   }

   private fun <T> Definition<*>.get(clz: Class<T>): T? {
      val config: IConfig = this.relateConfig(`$this$get`);
      if ((config as? IOptional) != null && !(config as? IOptional).getEnable()) {
         this.disabled.put(`$this$get`, config);
         return null;
      } else {
         if (`$this$get` is PluginDefinitions.CheckTypeDefinition) {
            val checkerMatches: CheckersConfig = new CheckersConfig((`$this$get` as PluginDefinitions.CheckTypeDefinition).getSingleton().getChecker());
            val var7: java.util.Iterator = this.checkers.iterator();

            val `element$iv`: Any;
            do {
               if (!var7.hasNext()) {
                  throw new NoSuchElementException("Collection contains no element matching the predicate.");
               }

               `element$iv` = var7.next();
            } while (!(((CheckersConfig)element$iv).getName() == checkerMatches.getName()));

            if (!(`element$iv` as CheckersConfig).getEnable()) {
               return null;
            }
         }

         return (T)this.getInstance(`$this$get`, clz);
      }
   }

   private fun getCheckers(defs: PluginDefinitions, checkerFilter: CheckerFilterByName?): cn.sast.framework.plugin.SAConfiguration.EnablesConfig {
      val res: SAConfiguration.EnablesConfig = new SAConfiguration.EnablesConfig(null, null, null, null, null, 31, null);
      val checkerUnits: LinkedHashSet = defs.getCheckerUnitDefinition(CheckerUnit.class);
      val sootInitializeHandlers: LinkedHashSet = defs.getISootInitializeHandlerDefinition(ISootInitializeHandler.class);
      val checkTypes: LinkedHashSet = defs.getCheckTypeDefinition(CheckType.class);
      var var26: java.util.Set = if (checkerFilter != null) checkerFilter.getEnables() else null;
      if (checkerFilter != null) {
         var26 = checkerFilter.getEnables();
         val def2checkerUnit: java.util.Set = checkerFilter.getRenameMap().keySet();
         val `$this$map$iv`: java.lang.Iterable = checkTypes;
         val `$i$f$map`: java.util.Collection = new LinkedHashSet();

         for (Object item$iv : $this$map$iv) {
            `$i$f$map`.add((`$i$f$mapTo` as PluginDefinitions.CheckTypeDefinition).getDefaultConfig().getCheckType());
         }

         val `$this$associateWith$iv`: java.util.Set = `$i$f$map` as java.util.Set;
         val var41: java.lang.Iterable = var26;
         val var61: java.util.Collection = new ArrayList();

         for (Object element$iv$iv : $this$filter$iv) {
            if (!`$this$associateWith$iv`.contains(var17 as java.lang.String) && !def2checkerUnit.contains(var17 as java.lang.String)) {
               var61.add(var17);
            }
         }

         val var38: java.util.List = var61 as java.util.List;
         if (!(var61 as java.util.List).isEmpty()) {
            logger.warn(SAConfiguration::getCheckers$lambda$9);
            val var42: java.lang.Iterable = var26;
            var var10000: Boolean;
            if (var26 is java.util.Collection && (var26 as java.util.Collection).isEmpty()) {
               var10000 = true;
            } else {
               val var57: java.util.Iterator = var42.iterator();

               while (true) {
                  if (!var57.hasNext()) {
                     var10000 = true;
                     break;
                  }

                  if (`$this$associateWith$iv`.contains(var57.next() as java.lang.String)) {
                     var10000 = false;
                     break;
                  }
               }
            }

            if (var10000) {
               throw new IllegalStateException("No checker type are enabled".toString());
            }
         }

         val `$this$filter$ivx`: java.lang.Iterable = `$this$associateWith$iv`;
         val `destination$iv$ivx`: java.util.Collection = new ArrayList();

         for (Object element$iv$ivx : $this$filter$ivx) {
            if (!var26.contains(`element$iv$ivx` as java.lang.String)) {
               `destination$iv$ivx`.add(`element$iv$ivx`);
            }
         }

         logger.debug(SAConfiguration::getCheckers$lambda$12);
      }

      var var116: java.util.List = res.getPreAnalysisUnits();
      var var27: java.lang.Iterable = checkerUnits;
      var var44: java.util.Collection = new ArrayList();

      for (Object element$iv$iv$iv : var27) {
         val var117: PreAnalysisUnit = this.get(var81 as PluginDefinitions.CheckerUnitDefinition, PreAnalysisUnit.class);
         if (var117 != null) {
            var44.add(var117);
         }
      }

      var116.addAll(var44 as java.util.List);
      var116 = res.getAiAnalysisUnits();
      var27 = checkerUnits;
      var44 = new ArrayList();

      for (Object element$iv$iv$ivx : var27) {
         val var119: AIAnalysisUnit = this.get(`element$iv$iv$ivx` as PluginDefinitions.CheckerUnitDefinition, AIAnalysisUnit.class);
         if (var119 != null) {
            var44.add(var119);
         }
      }

      var116.addAll(var44 as java.util.List);
      var116 = res.getSootConfig();
      var27 = sootInitializeHandlers;
      var44 = new ArrayList();

      for (Object element$iv$iv$ivxx : var27) {
         val var121: ISootInitializeHandler = this.get(`element$iv$iv$ivxx` as PluginDefinitions.ISootInitializeHandlerDefinition, ISootInitializeHandler.class);
         if (var121 != null) {
            var44.add(var121);
         }
      }

      var116.addAll(var44 as java.util.List);
      var116 = res.getCheckTypes();
      var27 = checkTypes;
      var44 = new ArrayList();

      for (Object element$iv$iv$ivxxx : var27) {
         val var123: CheckType = if (var26 != null)
            (
               if (var26.contains((`element$iv$iv$ivxxx` as PluginDefinitions.CheckTypeDefinition).getDefaultConfig().getCheckType()))
                  this.getInstance(`element$iv$iv$ivxxx` as PluginDefinitions.CheckTypeDefinition, CheckType.class)
                  else
                  null
            )
            else
            this.get(`element$iv$iv$ivxxx` as PluginDefinitions.CheckTypeDefinition, CheckType.class);
         if (var123 != null) {
            var44.add(var123);
         }
      }

      var116.addAll(var44 as java.util.List);
      val var36: java.lang.Iterable = checkerUnits;
      val var48: LinkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(checkerUnits, 10)), 16));

      for (Object element$iv$ivxx : $this$associateWith$iv) {
         var48.put(`element$iv$ivxx`, this.get(`element$iv$ivxx` as PluginDefinitions.CheckerUnitDefinition, CheckerUnit.class));
      }

      val var31: java.util.Map = Kotlin_extKt.nonNullValue(var48);
      val var37: java.util.Map = res.getDef2config();
      val `destination$iv$ivx`: java.util.Collection = new ArrayList(var31.size());

      for (Entry item$iv$iv : def2checkerUnit.entrySet()) {
         `destination$iv$ivx`.add(TuplesKt.to(var86.getValue() as CheckerUnit, this.relateConfig(var86.getKey() as PluginDefinitions.CheckerUnitDefinition)));
      }

      MapsKt.putAll(var37, `destination$iv$ivx` as java.util.List);
      return res;
   }

   public fun filter(defs: PluginDefinitions, checkerFilter: CheckerFilterByName?): SaConfig {
      val enableDefinitions: SAConfiguration.EnablesConfig = this.getCheckers(defs, checkerFilter);
      logger.info(SAConfiguration::filter$lambda$19);
      logger.info(SAConfiguration::filter$lambda$20);
      logger.info(SAConfiguration::filter$lambda$21);
      logger.info(SAConfiguration::filter$lambda$22);
      val sootConfigMerge: ISootInitializeHandler = if (enableDefinitions.getSootConfig().size() == 1)
         CollectionsKt.first(enableDefinitions.getSootConfig()) as ISootInitializeHandler
         else
         new ISootInitializeHandler(enableDefinitions) {
            {
               this.$enableDefinitions = `$enableDefinitions`;
            }

            @Override
            public void configure(Options options) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).configure(options);
               }
            }

            @Override
            public void configure(Scene scene) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).configure(scene);
               }
            }

            @Override
            public void configureAfterSceneInit(Scene scene, Options options) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).configureAfterSceneInit(scene, options);
               }
            }

            @Override
            public void configure(Main main) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).configure(main);
               }
            }

            @Override
            public void onBeforeCallGraphConstruction(Scene scene, Options options) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).onBeforeCallGraphConstruction(scene, options);
               }
            }

            @Override
            public void onAfterCallGraphConstruction(CallGraph cg, Scene scene, Options options) {
               val `$this$forEach$iv`: java.lang.Iterable;
               for (Object element$iv : $this$forEach$iv) {
                  (`element$iv` as ISootInitializeHandler).onAfterCallGraphConstruction(cg, scene, options);
               }
            }

            @Override
            public java.lang.String toString() {
               return "SootConfigMerge-${this.$enableDefinitions.getSootConfig()}";
            }
         };
      val var10000: GLB = GLB.INSTANCE;
      val `$this$map$iv`: java.lang.Iterable = defs.getCheckTypeDefinition(CheckType.class);
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as PluginDefinitions.CheckTypeDefinition).getSingleton());
      }

      var10000.plusAssign(`destination$iv$iv`);
      return new SaConfig(
         this.builtinAnalysisConfig,
         this.preAnalysisConfig,
         ExtensionsKt.toPersistentSet(CollectionsKt.plus(enableDefinitions.getPreAnalysisUnits(), enableDefinitions.getAiAnalysisUnits())) as MutableSet<CheckerUnit>,
         sootConfigMerge,
         CollectionsKt.toSet(enableDefinitions.getCheckTypes())
      );
   }

   public fun serialize(serializersModule: SerializersModule, out: IResFile) {
      val yml: Yaml = new Yaml(serializersModule, MainConfigKt.getYamlConfiguration());

      try {
         label24: {
            val var10000: Path = out.getPath();
            val var10001: Array<OpenOption> = new OpenOption[0];
            val var15: OutputStream = Files.newOutputStream(var10000, Arrays.copyOf(var10001, var10001.length));
            val e: Closeable = var15;
            var var5: java.lang.Throwable = null;

            try {
               try {
                  Yaml.encodeToStream$default(yml, Companion.serializer() as SerializationStrategy, this, e as OutputStream, null, 8, null);
               } catch (var8: java.lang.Throwable) {
                  var5 = var8;
                  throw var8;
               }
            } catch (var9: java.lang.Throwable) {
               CloseableKt.closeFinally(e, var5);
            }

            CloseableKt.closeFinally(e, null);
         }
      } catch (var10: Exception) {
         Files.delete(out.getPath());
         throw var10;
      }
   }

   public fun supplementAndMerge(defs: PluginDefinitions, ymlPath: String?): Boolean {
      val hashOld: Int = this.hashCode();
      val var4: SAConfiguration = Companion.getDefaultConfig(defs);
      val head: Lazy = LazyKt.lazy(SAConfiguration::supplementAndMerge$lambda$26);
      (new SAConfiguration.Compare(this, defs, head) {
            {
               super(`$receiver`);
               this.this$0 = `$receiver`;
               this.$defs = `$defs`;
               this.$head = `$head`;
            }

            @Override
            public void existsHandler(IConfig self, IConfig thatConfig) {
               val definitions: java.util.Map = SAConfiguration.access$supplementAndMerge$getAndRelate(this.$defs, this.this$0, self, thatConfig);
               if (self is IFieldOptions) {
                  val options: SAOptions = (self as IFieldOptions).getOptions();
                  if (options != null) {
                     val var5: java.util.Iterator = definitions.entrySet().iterator();

                     while (var5.hasNext()) {
                        ((var5.next() as Entry).getKey() as PluginDefinitions.Definition).setOptions(options);
                     }
                  }
               }
            }

            @Override
            public void multipleHandler(IConfig self, java.util.Collection<? extends IConfig> multipleThat) {
               throw new IllegalStateException(("Please remove duplicate definitions: $multipleThat from your plugin directory").toString());
            }

            @Override
            public void notExistsHandler(java.lang.String type, ConfigSerializable miss) {
               this.$head.getValue();
               SAConfiguration.access$getLogger$cp().warn(<unrepresentable>::notExistsHandler$lambda$0);
            }

            @Override
            public void notExistsHandler(CheckersConfig checker, CheckersConfig.CheckTypeConfig miss) {
               this.$head.getValue();
               SAConfiguration.access$getLogger$cp().warn(<unrepresentable>::notExistsHandler$lambda$1);
            }

            private static final Object notExistsHandler$lambda$0(ConfigSerializable $miss, java.lang.String $type) {
               return "There is a configuration ${`$miss`.getName()} of type $`$type` defined, but there is no corresponding definition in the plugin directory.";
            }

            private static final Object notExistsHandler$lambda$1(CheckersConfig.CheckTypeConfig $miss, CheckersConfig $checker) {
               return "There is a configuration $`$miss` of checker ${`$checker`.getName()} defined, but there is no corresponding definition in the plugin directory.";
            }
         })
         .compare(var4);
      (new SAConfiguration.Compare(var4, ymlPath, head, this, defs) {
         {
            super(`$default`);
            this.$ymlPath = `$ymlPath`;
            this.$head = `$head`;
            this.this$0 = `$receiver`;
            this.$defs = `$defs`;
         }

         @Override
         public void multipleHandler(IConfig self, java.util.Collection<? extends IConfig> multipleThat) {
            throw new IllegalStateException(("Please remove duplicate configurations: $multipleThat from ${this.$ymlPath}").toString());
         }

         @Override
         public void notExistsHandler(java.lang.String type, ConfigSerializable miss) {
            if (this.$ymlPath != null) {
               this.$head.getValue();
               SAConfiguration.access$getLogger$cp().warn(<unrepresentable>::notExistsHandler$lambda$0);
            }

            val `$this$getOrPut$iv`: java.util.Map = SAConfiguration.access$getConfigurations$p(this.this$0);
            val `value$iv`: Any = `$this$getOrPut$iv`.get(type);
            val var10000: Any;
            if (`value$iv` == null) {
               val var8: Any = new LinkedHashSet();
               `$this$getOrPut$iv`.put(type, var8);
               var10000 = var8;
            } else {
               var10000 = `value$iv`;
            }

            (var10000 as LinkedHashSet).add(miss);
            SAConfiguration.access$supplementAndMerge$getAndRelate(this.$defs, this.this$0, miss, miss);
         }

         @Override
         public void notExistsHandler(CheckersConfig checker, CheckersConfig.CheckTypeConfig miss) {
            if (this.$ymlPath != null) {
               this.$head.getValue();
               SAConfiguration.access$getLogger$cp().warn(<unrepresentable>::notExistsHandler$lambda$2);
            }

            checker.getCheckTypes().add(miss);
            if (!SAConfiguration.access$getCheckers$p(this.this$0).contains(checker)) {
               SAConfiguration.access$getCheckers$p(this.this$0).add(checker);
            }

            SAConfiguration.access$supplementAndMerge$getAndRelate(this.$defs, this.this$0, miss, miss);
         }

         private static final Object notExistsHandler$lambda$0(ConfigSerializable $miss, java.lang.String $type, java.lang.String $ymlPath) {
            return "There is an definition \"$`$miss`\" of type $`$type`, but it is not configured in configuration: $`$ymlPath`.";
         }

         private static final Object notExistsHandler$lambda$2(CheckersConfig.CheckTypeConfig $miss, java.lang.String $ymlPath) {
            return "There is an definition \"$`$miss`\", but it is not configured in configuration: $`$ymlPath`.";
         }
      }).compare(this);
      if (head.isInitialized()) {
         logger.warn(SAConfiguration::supplementAndMerge$lambda$30);
      }

      return this.hashCode() != hashOld;
   }

   private operator fun component1(): BuiltinAnalysisConfig {
      return this.builtinAnalysisConfig;
   }

   private operator fun component2(): PreAnalysisConfig {
      return this.preAnalysisConfig;
   }

   private operator fun component3(): LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> {
      return this.configurations;
   }

   private operator fun component4(): LinkedHashSet<CheckersConfig> {
      return this.checkers;
   }

   public fun copy(
      builtinAnalysisConfig: BuiltinAnalysisConfig = this.builtinAnalysisConfig,
      preAnalysisConfig: PreAnalysisConfig = this.preAnalysisConfig,
      configurations: LinkedHashMap<String, LinkedHashSet<ConfigSerializable>> = this.configurations,
      checkers: LinkedHashSet<CheckersConfig> = this.checkers
   ): SAConfiguration {
      return new SAConfiguration(builtinAnalysisConfig, preAnalysisConfig, configurations, checkers);
   }

   public override fun toString(): String {
      return "SAConfiguration(builtinAnalysisConfig=${this.builtinAnalysisConfig}, preAnalysisConfig=${this.preAnalysisConfig}, configurations=${this.configurations}, checkers=${this.checkers})";
   }

   public override fun hashCode(): Int {
      return ((this.builtinAnalysisConfig.hashCode() * 31 + this.preAnalysisConfig.hashCode()) * 31 + this.configurations.hashCode()) * 31
         + this.checkers.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is SAConfiguration) {
         return false;
      } else {
         val var2: SAConfiguration = other as SAConfiguration;
         if (!(this.builtinAnalysisConfig == (other as SAConfiguration).builtinAnalysisConfig)) {
            return false;
         } else if (!(this.preAnalysisConfig == var2.preAnalysisConfig)) {
            return false;
         } else if (!(this.configurations == var2.configurations)) {
            return false;
         } else {
            return this.checkers == var2.checkers;
         }
      }
   }

   @JvmStatic
   fun `getCheckers$lambda$9`(`$notExistsCheckerNames`: java.util.List): Any {
      return "\nThese check types named $`$notExistsCheckerNames` cannot be found in analysis-config\n";
   }

   @JvmStatic
   fun `getCheckers$lambda$12`(`$disabledCheckers`: java.util.List): Any {
      return "\nThese check types $`$disabledCheckers` are not enabled\n";
   }

   @JvmStatic
   fun `filter$lambda$19`(`$enableDefinitions`: SAConfiguration.EnablesConfig, `$defs`: PluginDefinitions): Any {
      return "Num of effective PreAnalysisUnit is ${`$enableDefinitions`.getPreAnalysisUnits().size()}/${`$defs`.getPreAnalysisUnit(PreAnalysisUnit.class)
         .size()}";
   }

   @JvmStatic
   fun `filter$lambda$20`(`$enableDefinitions`: SAConfiguration.EnablesConfig, `$defs`: PluginDefinitions): Any {
      return "Num of effective AIAnalysisUnit is ${`$enableDefinitions`.getAiAnalysisUnits().size()}/${`$defs`.getAIAnalysisUnit(AIAnalysisUnit.class).size()}";
   }

   @JvmStatic
   fun `filter$lambda$21`(`$enableDefinitions`: SAConfiguration.EnablesConfig, `$defs`: PluginDefinitions): Any {
      return "Num of effective ISootInitializeHandler is ${`$enableDefinitions`.getSootConfig().size()}/${`$defs`.getISootInitializeHandlerDefinition(
            ISootInitializeHandler.class
         )
         .size()}";
   }

   @JvmStatic
   fun `filter$lambda$22`(`$enableDefinitions`: SAConfiguration.EnablesConfig, `$defs`: PluginDefinitions): Any {
      return "Num of effective CheckType is ${`$enableDefinitions`.getCheckTypes().size()}/${`$defs`.getCheckTypeDefinition(CheckType.class).size()}";
   }

   @JvmStatic
   fun `supplementAndMerge$lambda$26$lambda$25`(): Any {
      return "/--------------------- config information view ---------------------";
   }

   @JvmStatic
   fun `supplementAndMerge$lambda$26`(): Unit {
      logger.warn(SAConfiguration::supplementAndMerge$lambda$26$lambda$25);
      return Unit.INSTANCE;
   }

   @JvmStatic
   fun `supplementAndMerge$getAndRelate`(`$defs`: PluginDefinitions, `this$0`: SAConfiguration, self: IConfig, thatConfig: IConfig): MutableMap<PluginDefinitionsDefinition<?>, IConfig> {
      val `$this$filterKeys$iv`: java.util.Map = `$defs`.getDefaultConfigs();
      val `result$iv`: LinkedHashMap = new LinkedHashMap();

      for (Entry entry$iv : $this$filterKeys$iv.entrySet()) {
         if ((`entry$iv`.getKey() as PluginDefinitions.Definition).getDefaultConfig() === thatConfig) {
            `result$iv`.put(`entry$iv`.getKey(), `entry$iv`.getValue());
         }
      }

      val definitions: java.util.Map = `result$iv`;
      if (`result$iv`.isEmpty()) {
         throw new IllegalStateException(("internal error. empty definition. config: $self and $thatConfig").toString());
      } else if (definitions.size() != 1) {
         throw new IllegalStateException(("internal error. multiple definitions: $definitions. config: $self and $thatConfig").toString());
      } else {
         `this$0`.relatedMap.put((PluginDefinitions.Definition<?>)CollectionsKt.first(definitions.keySet()), self);
         return definitions;
      }
   }

   @JvmStatic
   fun `supplementAndMerge$lambda$30`(): Any {
      return "\\--------------------- config information view ---------------------";
   }

   @JvmStatic
   fun `logger$lambda$31`(): Unit {
      return Unit.INSTANCE;
   }

   fun SAConfiguration() {
      this(null, null, null, null, 15, null);
   }

   @SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,557:1\n774#2:558\n865#2,2:559\n381#3,7:561\n381#3,7:568\n*S KotlinDebug\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration$Companion\n*L\n511#1:558\n511#1:559,2\n523#1:561,7\n527#1:568,7\n*E\n"])
   public companion object {
      private final val logger: KLogger

      public final val UnitTypeName: String
         public final get() {
            val var10000: java.lang.String = `$this$UnitTypeName`.getSimpleName();
            return var10000;
         }


      public fun getDefaultConfig(defs: PluginDefinitions): SAConfiguration {
         val defaultConfigMap: java.util.Map = defs.getDefaultConfigs();
         val configurations: LinkedHashMap = new LinkedHashMap();
         val checkers: LinkedHashSet = new LinkedHashSet();

         for (Entry var6 : defaultConfigMap.entrySet()) {
            val def: PluginDefinitions.Definition = var6.getKey() as PluginDefinitions.Definition;
            if (!(var6.getValue() as IConfig == def.getDefaultConfig())) {
               throw new IllegalStateException("Check failed.".toString());
            }

            if (def is PluginDefinitions.CheckTypeDefinition) {
               val var23: CheckType = (def as PluginDefinitions.CheckTypeDefinition).getSingleton();
               val var26: CheckersConfig = new CheckersConfig(var23.getChecker());
               val var30: java.lang.Iterable = checkers;
               val `destination$iv$iv`: java.util.Collection = new ArrayList();

               for (Object element$iv$iv : $this$filter$iv) {
                  if ((`element$iv$iv` as CheckersConfig).getName() == var26.getName()) {
                     `destination$iv$iv`.add(`element$iv$iv`);
                  }
               }

               val var28: java.util.List = `destination$iv$iv` as java.util.List;
               val var38: CheckersConfig;
               if ((`destination$iv$iv` as java.util.List).isEmpty()) {
                  checkers.add(var26);
                  var38 = var26;
               } else {
                  if (var28.size() != 1) {
                     throw new IllegalStateException(
                        ("Please fix duplicate IChecker::name of checker definitions: ${var23.getChecker()} and $var28").toString()
                     );
                  }

                  var38 = CollectionsKt.first(var28) as CheckersConfig;
               }

               var38.getCheckTypes().add((def as PluginDefinitions.CheckTypeDefinition).getDefaultConfig());
            } else if (def is PluginDefinitions.CheckerUnitDefinition) {
               val `$this$getOrPut$iv`: java.util.Map = configurations;
               val `key$iv`: Any = this.getUnitTypeName((def as PluginDefinitions.CheckerUnitDefinition).getType());
               val `value$iv`: Any = `$this$getOrPut$iv`.get(`key$iv`);
               val var10000: Any;
               if (`value$iv` == null) {
                  val var34: Any = new LinkedHashSet();
                  `$this$getOrPut$iv`.put(`key$iv`, var34);
                  var10000 = var34;
               } else {
                  var10000 = `value$iv`;
               }

               (var10000 as LinkedHashSet).add((def as PluginDefinitions.CheckerUnitDefinition).getDefaultConfig());
            } else {
               if (def !is PluginDefinitions.ISootInitializeHandlerDefinition) {
                  throw new NoWhenBranchMatchedException();
               }

               val var25: java.util.Map = configurations;
               val var27: Any = this.getUnitTypeName((def as PluginDefinitions.ISootInitializeHandlerDefinition).getType());
               val var32: Any = var25.get(var27);
               val var37: Any;
               if (var32 == null) {
                  val var36: Any = new LinkedHashSet();
                  var25.put(var27, var36);
                  var37 = var36;
               } else {
                  var37 = var32;
               }

               (var37 as LinkedHashSet).add((def as PluginDefinitions.ISootInitializeHandlerDefinition).getDefaultConfig());
            }
         }

         return new SAConfiguration(null, null, configurations, checkers, 3, null);
      }

      public fun deserialize(serializersModule: SerializersModule, in1: IResFile): SAConfiguration {
         label19: {
            val yml: Yaml = new Yaml(serializersModule, MainConfigKt.getYamlConfiguration());
            val var10000: Path = in1.getPath();
            val var10001: Array<OpenOption> = new OpenOption[0];
            val var13: InputStream = Files.newInputStream(var10000, Arrays.copyOf(var10001, var10001.length));
            val var4: Closeable = var13;
            var var5: java.lang.Throwable = null;

            try {
               try {
                  val var12: SAConfiguration = Yaml.decodeFromStream$default(
                     yml, SAConfiguration.Companion.serializer() as DeserializationStrategy, var4 as InputStream, null, 4, null
                  ) as SAConfiguration;
               } catch (var8: java.lang.Throwable) {
                  var5 = var8;
                  throw var8;
               }
            } catch (var9: java.lang.Throwable) {
               CloseableKt.closeFinally(var4, var5);
            }

            CloseableKt.closeFinally(var4, null);
         }
      }

      public fun serializer(): KSerializer<SAConfiguration> {
         return SAConfiguration.$serializer.INSTANCE as KSerializer<SAConfiguration>;
      }
   }

   @SourceDebugExtension(["SMAP\nSAConfiguration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration$Compare\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,557:1\n774#2:558\n865#2,2:559\n774#2:561\n865#2,2:562\n1368#2:564\n1454#2,2:565\n774#2:567\n865#2,2:568\n1456#2,3:570\n*S KotlinDebug\n*F\n+ 1 SAConfiguration.kt\ncn/sast/framework/plugin/SAConfiguration$Compare\n*L\n373#1:558\n373#1:559,2\n386#1:561\n386#1:562,2\n388#1:564\n388#1:565,2\n388#1:567\n388#1:568,2\n388#1:570,3\n*E\n"])
   private abstract class Compare {
      public final val self: SAConfiguration

      open fun Compare(self: SAConfiguration) {
         this.self = self;
      }

      public open fun existsHandler(self: IConfig, thatConfig: IConfig) {
      }

      public abstract fun multipleHandler(self: IConfig, multipleThat: Collection<IConfig>) {
      }

      public open fun notExistsHandler(type: String, miss: ConfigSerializable) {
      }

      public open fun notExistsHandler(checker: CheckersConfig, miss: CheckTypeConfig) {
      }

      public fun compare(that: SAConfiguration) {
         for (Entry configSerializable : SAConfiguration.access$getConfigurations$p(this.self).entrySet()) {
            var var10000: java.util.Iterator = (checker.getValue() as LinkedHashSet).iterator();
            val checkerMatches: java.util.Iterator = var10000;

            while (checkerMatches.hasNext()) {
               var10000 = (java.util.Iterator)checkerMatches.next();
               val `$this$filter$iv`: ConfigSerializable = var10000 as ConfigSerializable;
               val var48: LinkedHashSet = SAConfiguration.access$getConfigurations$p(that).get(checker.getKey()) as LinkedHashSet;
               val matched: java.lang.Iterable = if (var48 != null) var48 else SetsKt.emptySet();
               val `$this$flatMapTo$iv$iv`: java.util.Collection = new ArrayList();

               for (Object element$iv$iv : $this$filter$iv) {
                  if ((var13 as ConfigSerializable).getName() == `$this$filter$iv`.getName()) {
                     `$this$flatMapTo$iv$iv`.add(var13);
                  }
               }

               val checkTypeConfig: java.util.List = `$this$flatMapTo$iv$iv` as java.util.List;
               if (!(`$this$flatMapTo$iv$iv` as java.util.List).isEmpty()) {
                  if (checkTypeConfig.size() > 1) {
                     this.multipleHandler(`$this$filter$iv`, checkTypeConfig);
                  } else {
                     this.existsHandler(`$this$filter$iv`, CollectionsKt.first(checkTypeConfig) as IConfig);
                  }
               } else {
                  this.notExistsHandler(checker.getKey() as java.lang.String, `$this$filter$iv`);
               }
            }
         }

         var var49: java.util.Iterator = SAConfiguration.access$getCheckers$p(this.self).iterator();
         val var26: java.util.Iterator = var49;

         while (var26.hasNext()) {
            var49 = (java.util.Iterator)var26.next();
            val var27: CheckersConfig = var49 as CheckersConfig;
            val var29: java.lang.Iterable = SAConfiguration.access$getCheckers$p(that);
            val var34: java.util.Collection = new ArrayList();

            for (Object element$iv$ivx : $this$filter$iv) {
               if ((`element$iv$ivx` as CheckersConfig).getName() == var27.getName()) {
                  var34.add(`element$iv$ivx`);
               }
            }

            val var28: java.util.List = var34 as java.util.List;
            var49 = var27.getCheckTypes().iterator();
            val var30: java.util.Iterator = var49;

            while (var30.hasNext()) {
               var49 = (java.util.Iterator)var30.next();
               val var32: CheckersConfig.CheckTypeConfig = var49 as CheckersConfig.CheckTypeConfig;
               val var35: java.lang.Iterable = var28;
               val `destination$iv$ivx`: java.util.Collection = new ArrayList();

               for (Object element$iv$ivxx : $this$flatMap$iv) {
                  val `$this$filter$ivx`: java.lang.Iterable = (`element$iv$ivxx` as CheckersConfig).getCheckTypes();
                  val `destination$iv$ivxx`: java.util.Collection = new ArrayList();

                  for (Object element$iv$ivxxx : $this$filter$ivx) {
                     if ((`element$iv$ivxxx` as CheckersConfig.CheckTypeConfig).getCheckType() == var32.getCheckType()) {
                        `destination$iv$ivxx`.add(`element$iv$ivxxx`);
                     }
                  }

                  CollectionsKt.addAll(`destination$iv$ivx`, `destination$iv$ivxx` as java.util.List);
               }

               val var33: java.util.List = `destination$iv$ivx` as java.util.List;
               if (!(`destination$iv$ivx` as java.util.List).isEmpty()) {
                  if (var33.size() > 1) {
                     this.multipleHandler(var32, var33);
                  } else {
                     this.existsHandler(var32, CollectionsKt.first(var33) as IConfig);
                  }
               } else {
                  this.notExistsHandler(var27, var32);
               }
            }
         }
      }
   }

   public data class EnablesConfig(aiAnalysisUnits: MutableList<AIAnalysisUnit> = (new ArrayList()) as java.util.List,
      preAnalysisUnits: MutableList<PreAnalysisUnit> = (new ArrayList()) as java.util.List,
      sootConfig: MutableList<ISootInitializeHandler> = (new ArrayList()) as java.util.List,
      checkTypes: MutableList<CheckType> = (new ArrayList()) as java.util.List,
      def2config: MutableMap<CheckerUnit, IConfig> = (new IdentityHashMap()) as java.util.Map
   ) {
      public final val aiAnalysisUnits: MutableList<AIAnalysisUnit>
      public final val preAnalysisUnits: MutableList<PreAnalysisUnit>
      public final val sootConfig: MutableList<ISootInitializeHandler>

      public final var checkTypes: MutableList<CheckType>
         internal set

      public final var def2config: MutableMap<CheckerUnit, IConfig>
         internal set

      init {
         this.aiAnalysisUnits = aiAnalysisUnits;
         this.preAnalysisUnits = preAnalysisUnits;
         this.sootConfig = sootConfig;
         this.checkTypes = checkTypes;
         this.def2config = def2config;
      }

      public operator fun plusAssign(b: cn.sast.framework.plugin.SAConfiguration.EnablesConfig) {
         CollectionsKt.addAll(this.aiAnalysisUnits, b.aiAnalysisUnits);
         CollectionsKt.addAll(this.preAnalysisUnits, b.preAnalysisUnits);
         CollectionsKt.addAll(this.sootConfig, b.sootConfig);
      }

      public operator fun component1(): MutableList<AIAnalysisUnit> {
         return this.aiAnalysisUnits;
      }

      public operator fun component2(): MutableList<PreAnalysisUnit> {
         return this.preAnalysisUnits;
      }

      public operator fun component3(): MutableList<ISootInitializeHandler> {
         return this.sootConfig;
      }

      public operator fun component4(): MutableList<CheckType> {
         return this.checkTypes;
      }

      public operator fun component5(): MutableMap<CheckerUnit, IConfig> {
         return this.def2config;
      }

      public fun copy(
         aiAnalysisUnits: MutableList<AIAnalysisUnit> = this.aiAnalysisUnits,
         preAnalysisUnits: MutableList<PreAnalysisUnit> = this.preAnalysisUnits,
         sootConfig: MutableList<ISootInitializeHandler> = this.sootConfig,
         checkTypes: MutableList<CheckType> = this.checkTypes,
         def2config: MutableMap<CheckerUnit, IConfig> = this.def2config
      ): cn.sast.framework.plugin.SAConfiguration.EnablesConfig {
         return new SAConfiguration.EnablesConfig(aiAnalysisUnits, preAnalysisUnits, sootConfig, checkTypes, def2config);
      }

      public override fun toString(): String {
         return "EnablesConfig(aiAnalysisUnits=${this.aiAnalysisUnits}, preAnalysisUnits=${this.preAnalysisUnits}, sootConfig=${this.sootConfig}, checkTypes=${this.checkTypes}, def2config=${this.def2config})";
      }

      public override fun hashCode(): Int {
         return (
                  ((this.aiAnalysisUnits.hashCode() * 31 + this.preAnalysisUnits.hashCode()) * 31 + this.sootConfig.hashCode()) * 31
                     + this.checkTypes.hashCode()
               )
               * 31
            + this.def2config.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is SAConfiguration.EnablesConfig) {
            return false;
         } else {
            val var2: SAConfiguration.EnablesConfig = other as SAConfiguration.EnablesConfig;
            if (!(this.aiAnalysisUnits == (other as SAConfiguration.EnablesConfig).aiAnalysisUnits)) {
               return false;
            } else if (!(this.preAnalysisUnits == var2.preAnalysisUnits)) {
               return false;
            } else if (!(this.sootConfig == var2.sootConfig)) {
               return false;
            } else if (!(this.checkTypes == var2.checkTypes)) {
               return false;
            } else {
               return this.def2config == var2.def2config;
            }
         }
      }

      fun EnablesConfig() {
         this(null, null, null, null, null, 31, null);
      }
   }
}
