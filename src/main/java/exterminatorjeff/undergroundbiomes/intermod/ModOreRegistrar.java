package exterminatorjeff.undergroundbiomes.intermod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import exterminatorjeff.undergroundbiomes.api.API;
import exterminatorjeff.undergroundbiomes.api.ModInfo;
import exterminatorjeff.undergroundbiomes.api.common.UBLogger;
import exterminatorjeff.undergroundbiomes.api.common.UBModOreRegistrar;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ModOreRegistrar implements UBModOreRegistrar {

  private static final UBLogger LOGGER = new UBLogger(OresRegistry.class, Level.INFO);
  private final File directory;
  private HashMap<String, UBOreConfig> ores = new HashMap<>();
  private final Type jsonType = new TypeToken<ArrayList<UBOreConfig>>() {
  }.getType();

  public ModOreRegistrar(FMLPreInitializationEvent event) {
    this.directory = Paths.get(event.getModConfigurationDirectory().toString(), "undergroundbiomes", "ores").toFile();
    try {
      Files.createDirectories(this.directory.toPath());
      createDefaults();
      setupOres();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void requestOreSetup(RegistryEvent.Register<Block> event, UBOreConfig ore) {
    ResourceLocation location = new ResourceLocation(ore.getInternalOreName());
    IForgeRegistry<Block> registry = event.getRegistry();
    if (!registry.containsKey(location)) {
      return;
    }
    Block block = registry.getValue(location);
    API.ORES_REGISTRY.requestOreSetup(block, ore);
    API.ORES_REGISTRY.registerOreOverlay(block, ore.getMeta(), new ResourceLocation(ore.getOverlay()));
  }

  public void requestOreSetups(RegistryEvent.Register<Block> event) {
    for (UBOreConfig ore : this.ores.values()) {
      requestOreSetup(event, ore);
    }
  }

  private void createDefaults() {
    writeDefaults(getMinecraftOres(), "minecraft.json");
    writeDefaults(getNuclearcraftOres(), "nuclearcraft.json");
    writeDefaults(getGrowthcraftOres(), "growthcraft.json");
    writeDefaults(getActuallyAdditionsOres(), "actuallyadditions.json");
    writeDefaults(getAppliedEnergisticsOres(), "appliedenergistics2.json");
    writeDefaults(getBaseMetalsOres(), "basemetals.json");
    writeDefaults(getBaseMineralsOres(), "baseminerals.json");
    writeDefaults(getBiomesOPlentyOres(), "biomesoplenty.json");
    writeDefaults(getBetterUndergroundOres(), "betterunderground.json");
    writeDefaults(getDraconicEvolutionOres(), "draconicevolution.json");
    writeDefaults(getEnderOreOres(), "enderore.json");
    writeDefaults(getExtremeReactorsOres(), "extremereactors.json");
    writeDefaults(getForestyOres(), "forestry.json");
    writeDefaults(getImmersiveEngineeringOres(), "immersiveengineering.json");
    writeDefaults(getIndustrialCraftOres(), "industrialcraft.json");
    writeDefaults(getMekanismOres(), "mekanism.json");
    writeDefaults(getModernMetalsOres(), "modernmetals.json");
    writeDefaults(getRFToolsOres(), "rftools.json");
    writeDefaults(getTechRebornOres(), "techreborn.json");
    writeDefaults(getThermalFoundationOres(), "thermalfoundation.json");
    writeDefaults(getThaumcraftOres(), "thaumcraft.json");
    writeDefaults(getGeolosysOres(), "geolosys.json");
    writeDefaults(getMysticalAgricultureOres(), "mysticalagriculture.json");
    writeDefaults(getMatterOverdriveLegacyOres(), "matteroverdrivelegacy.json");
    writeDefaults(getTaigaOres(), "taiga.json");
    writeDefaults(getMetallurgy4Ores(), "metallurgy4.json");
    writeDefaults(getProjectRedOres(), "projectred.json");
  }

  private void writeDefaults(ArrayList<UBOreConfig> ores, String filename) {
    try {
      Path filepath = Paths.get(this.directory.toString(), filename);
      if (!filepath.toFile().exists()) {
        Gson gson = new GsonBuilder()
          .setPrettyPrinting()
          .create();
        String json = gson.toJson(ores, jsonType);
        Files.write(filepath, Arrays.asList(json.split("\n")), Charset.forName("UTF-8"));
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupOres() {
    for (final File fileEntry : directory.listFiles()) {
      if (fileEntry.isFile() && FilenameUtils.getExtension(fileEntry.getPath()).equals("json")) {
        readFile(fileEntry);
      }
    }
  }

  private void readFile(File file) {
    try {
      Gson gson = new Gson();
      String json = null;
      json = FileUtils.readFileToString(file, "UTF8");
      ArrayList<UBOreConfig> ores = gson.fromJson(json, jsonType);
      if (ores != null) {
        for(int i = 0; i < ores.size(); i++) {
          UBOreConfig ore = ores.get(i);
          if(this.ores.containsKey(ore.toKey())) {
            String message = "Ore " + ore.toKey() + " has already been defined elsewhere!\nFound while checking: " + file.getAbsolutePath();
            if(API.SETTINGS.crashOnProblems()) {
              LOGGER.fatal(message);
            }
            else {
              LOGGER.warn(message);
            }
          }
          this.ores.put(ore.toKey(), ore);
        }
      } else {
        LOGGER.warn("No ores found in " + file.getPath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ArrayList<UBOreConfig> getMinecraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("minecraft:diamond_ore", ModInfo.MODID + ":blocks/overlays/minecraft/diamond"));
    ores.add(new UBOreConfig("minecraft:iron_ore", ModInfo.MODID + ":blocks/overlays/minecraft/iron"));
    ores.add(new UBOreConfig("minecraft:coal_ore", ModInfo.MODID + ":blocks/overlays/minecraft/coal"));
    ores.add(new UBOreConfig("minecraft:lapis_ore", ModInfo.MODID + ":blocks/overlays/minecraft/lapis"));
    ores.add(new UBOreConfig("minecraft:redstone_ore", ModInfo.MODID + ":blocks/overlays/minecraft/redstone"));
    ores.add(new UBOreConfig("minecraft:gold_ore", ModInfo.MODID + ":blocks/overlays/minecraft/gold"));
    ores.add(new UBOreConfig("minecraft:emerald_ore", ModInfo.MODID + ":blocks/overlays/minecraft/emerald"));
    return ores;
  }

  private ArrayList<UBOreConfig> getProjectRedOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("projectred-exploration:ore", 0, ModInfo.MODID + ":blocks/overlays/projectred/ruby_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 1, ModInfo.MODID + ":blocks/overlays/projectred/sapphire_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 2, ModInfo.MODID + ":blocks/overlays/projectred/peridot_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 3, ModInfo.MODID + ":blocks/overlays/projectred/copper_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 4, ModInfo.MODID + ":blocks/overlays/projectred/tin_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 5, ModInfo.MODID + ":blocks/overlays/projectred/silver_ore"));
    ores.add(new UBOreConfig("projectred-exploration:ore", 6, ModInfo.MODID + ":blocks/overlays/projectred/electrotine_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getActuallyAdditionsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "actuallyadditions:block_misc";
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/custom/black_quarz"));
    return ores;
  }

  private ArrayList<UBOreConfig> getAppliedEnergisticsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("appliedenergistics2:quartz_ore", "appliedenergistics2:blocks/charged_quartz_ore_light"));
    ores.add(new UBOreConfig("appliedenergistics2:charged_quartz_ore", "appliedenergistics2:blocks/charged_quartz_ore_light"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGrowthcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("growthcraft:salt_ore", ModInfo.MODID + ":blocks/overlays/growthcraft/salt_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMetallurgy4Ores() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("metallurgy:adamantine_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/adamantine_ore"));
    ores.add(new UBOreConfig("metallurgy:astral_silver_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/astral_silver_ore"));
    ores.add(new UBOreConfig("metallurgy:atlarus_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/atlarus_ore"));
    ores.add(new UBOreConfig("metallurgy:bitumen_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/bitumen_ore"));
    ores.add(new UBOreConfig("metallurgy:carmot_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/carmot_ore"));
    ores.add(new UBOreConfig("metallurgy:copper_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/copper_ore"));
    ores.add(new UBOreConfig("metallurgy:deep_iron_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/deep_iron_ore"));
    ores.add(new UBOreConfig("metallurgy:infuscolium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/infuscolium_ore"));
    ores.add(new UBOreConfig("metallurgy:lutetium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/lutetium_ore"));
    ores.add(new UBOreConfig("metallurgy:magnesium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/magnesium_ore"));
    ores.add(new UBOreConfig("metallurgy:manganese_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/manganese_ore"));
    ores.add(new UBOreConfig("metallurgy:mithril_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/mithril_ore"));
    ores.add(new UBOreConfig("metallurgy:orichalcum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/orichalcum_ore"));
    ores.add(new UBOreConfig("metallurgy:osmium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/osmium_ore"));
    ores.add(new UBOreConfig("metallurgy:oureclase_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/oureclase_ore"));
    ores.add(new UBOreConfig("metallurgy:phosporite_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/phosphorite_ore"));
    ores.add(new UBOreConfig("metallurgy:platinum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/platinum_ore"));
    ores.add(new UBOreConfig("metallurgy:potash_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/potash_ore"));
    ores.add(new UBOreConfig("metallurgy:prometheum_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/prometheum_ore"));
    ores.add(new UBOreConfig("metallurgy:rubracium_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/rubracium_ore"));
    ores.add(new UBOreConfig("metallurgy:salpeter_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/salpeter_ore"));
    ores.add(new UBOreConfig("metallurgy:silver_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/silver_ore"));
    ores.add(new UBOreConfig("metallurgy:sulfur_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/sulfur_ore"));
    ores.add(new UBOreConfig("metallurgy:tin_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/tin_ore"));
    ores.add(new UBOreConfig("metallurgy:zinc_ore", 0, ModInfo.MODID + ":blocks/overlays/metallurgy4/zinc_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getNuclearcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("nuclearcraft:ore", 0, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_copper"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 1, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_tin"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 2, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_lead"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 3, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_thorium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 4, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_uranium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 5, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_boron"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 6, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_lithium"));
    ores.add(new UBOreConfig("nuclearcraft:ore", 7, ModInfo.MODID + ":blocks/overlays/nuclearcraft/ore_magnesium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMatterOverdriveLegacyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("matteroverdrive:dilithium_ore", 0, ModInfo.MODID + ":blocks/overlays/matteroverdrivelegacy/dilithium_ore"));
    ores.add(new UBOreConfig("matteroverdrive:tritanium_ore", 0, ModInfo.MODID + ":blocks/overlays/matteroverdrivelegacy/tritanium_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getTaigaOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("taiga:dilithium_ore", 0, ModInfo.MODID + ":blocks/overlays/taiga/dilithium"));
    ores.add(new UBOreConfig("taiga:vibranium_ore", 0, ModInfo.MODID + ":blocks/overlays/taiga/vibranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getGeolosysOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("geolosys:ore", 0, ModInfo.MODID + ":blocks/overlays/geolosys/hematite"));
    ores.add(new UBOreConfig("geolosys:ore", 1, ModInfo.MODID + ":blocks/overlays/geolosys/limonite"));
    ores.add(new UBOreConfig("geolosys:ore", 2, ModInfo.MODID + ":blocks/overlays/geolosys/malachite"));
    ores.add(new UBOreConfig("geolosys:ore", 3, ModInfo.MODID + ":blocks/overlays/geolosys/azurite"));
    ores.add(new UBOreConfig("geolosys:ore", 4, ModInfo.MODID + ":blocks/overlays/geolosys/cassiterite"));
    ores.add(new UBOreConfig("geolosys:ore", 5, ModInfo.MODID + ":blocks/overlays/geolosys/teallite"));
    ores.add(new UBOreConfig("geolosys:ore", 6, ModInfo.MODID + ":blocks/overlays/geolosys/galena"));
    ores.add(new UBOreConfig("geolosys:ore", 7, ModInfo.MODID + ":blocks/overlays/geolosys/bauxite"));
    ores.add(new UBOreConfig("geolosys:ore", 8, ModInfo.MODID + ":blocks/overlays/geolosys/platinum"));
    ores.add(new UBOreConfig("geolosys:ore", 9, ModInfo.MODID + ":blocks/overlays/geolosys/autunite"));
    ores.add(new UBOreConfig("geolosys:ore", 10, ModInfo.MODID + ":blocks/overlays/geolosys/sphalerite"));

    ores.add(new UBOreConfig("geolosys:ore_vanilla", 0, ModInfo.MODID + ":blocks/overlays/geolosys/coal"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 1, ModInfo.MODID + ":blocks/overlays/geolosys/cinnabar"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 2, ModInfo.MODID + ":blocks/overlays/geolosys/gold"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 3, ModInfo.MODID + ":blocks/overlays/geolosys/lapis"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 4, ModInfo.MODID + ":blocks/overlays/geolosys/quartz"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 5, ModInfo.MODID + ":blocks/overlays/geolosys/kimberlite"));
    ores.add(new UBOreConfig("geolosys:ore_vanilla", 6, ModInfo.MODID + ":blocks/overlays/geolosys/beryl"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBaseMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("basemetals:antimony_ore", ModInfo.MODID + ":blocks/overlays/basemetals/antimony"));
    ores.add(new UBOreConfig("basemetals:bismuth_ore", ModInfo.MODID + ":blocks/overlays/basemetals/bismuth"));
    ores.add(new UBOreConfig("basemetals:copper_ore", ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig("basemetals:lead_ore", ModInfo.MODID + ":blocks/overlays/basemetals/lead"));
    ores.add(new UBOreConfig("basemetals:mercury_ore", ModInfo.MODID + ":blocks/overlays/basemetals/mercury"));
    ores.add(new UBOreConfig("basemetals:nickel_ore", ModInfo.MODID + ":blocks/overlays/basemetals/nickel"));
    ores.add(new UBOreConfig("basemetals:platinum_ore", ModInfo.MODID + ":blocks/overlays/basemetals/platinum"));
    ores.add(new UBOreConfig("basemetals:silver_ore", ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig("basemetals:tin_ore", ModInfo.MODID + ":blocks/overlays/basemetals/tin"));
    ores.add(new UBOreConfig("basemetals:zinc_ore", ModInfo.MODID + ":blocks/overlays/basemetals/zinc"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBaseMineralsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("baseminerals:lithium_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/lithium"));
    ores.add(new UBOreConfig("baseminerals:niter_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/niter"));
    ores.add(new UBOreConfig("baseminerals:phosphorus_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/phosphorus"));
    ores.add(new UBOreConfig("baseminerals:potash_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/potash"));
    ores.add(new UBOreConfig("baseminerals:salt_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/salt"));
    ores.add(new UBOreConfig("baseminerals:saltpeter_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/saltpeter"));
    ores.add(new UBOreConfig("baseminerals:sulfur_ore", ModInfo.MODID + ":blocks/overlays/baseminerals/sulfur"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMysticalAgricultureOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("mysticalagriculture:inferium_ore", "mysticalagriculture:blocks/inferium_ore"));
    ores.add(new UBOreConfig("mysticalagriculture:prosperity_ore", "mysticalagriculture:blocks/prosperity_ore"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBetterUndergroundOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("betterunderground:blockfossils", ModInfo.MODID + ":blocks/overlays/custom/fossil"));
    return ores;
  }

  private ArrayList<UBOreConfig> getBiomesOPlentyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "biomesoplenty:gem_ore";
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/custom/ruby"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/custom/peridot"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/custom/topaz"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/custom/tanzanite"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/custom/malachite"));
    ores.add(new UBOreConfig(oreName, 6, ModInfo.MODID + ":blocks/overlays/custom/sapphire"));
    ores.add(new UBOreConfig(oreName, 7, ModInfo.MODID + ":blocks/overlays/custom/amber"));
    return ores;
  }

  private ArrayList<UBOreConfig> getDraconicEvolutionOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("draconicevolution:draconium_ore", 0, ModInfo.MODID + ":blocks/overlays/draconicevolution/draconium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getEnderOreOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("enderore:ore_ender", ModInfo.MODID + ":blocks/overlays/enderore/ender"));
    return ores;
  }

  private ArrayList<UBOreConfig> getExtremeReactorsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("bigreactors:brore", 0, ModInfo.MODID + ":blocks/overlays/bigreactors/yellorite"));
    return ores;
  }

  private ArrayList<UBOreConfig> getForestyOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "forestry:resources";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/custom/apatite"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    return ores;
  }

  private ArrayList<UBOreConfig> getIndustrialCraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "ic2:resource";
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/custom/uranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getImmersiveEngineeringOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "immersiveengineering:ore";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/aluminum"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/thermalfoundation/nickel"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/custom/uranium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getMekanismOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "mekanism:oreblock";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/mekanism/osmium"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/mekanism/copper"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/mekanism/tin"));
    return ores;
  }

  private ArrayList<UBOreConfig> getModernMetalsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("modernmetals:aluminum_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/aluminum"));
    ores.add(new UBOreConfig("modernmetals:beryllium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/beryllium"));
    ores.add(new UBOreConfig("modernmetals:boron_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/boron"));
    ores.add(new UBOreConfig("modernmetals:cadmium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/cadmium"));
    ores.add(new UBOreConfig("modernmetals:chromium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/chromium"));
    ores.add(new UBOreConfig("modernmetals:iridium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/iridium"));
    ores.add(new UBOreConfig("modernmetals:magnesium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/magnesium"));
    ores.add(new UBOreConfig("modernmetals:manganese_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/manganese"));
    ores.add(new UBOreConfig("modernmetals:osmium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/osmium"));
    ores.add(new UBOreConfig("modernmetals:plutonium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/plutonium"));
    ores.add(new UBOreConfig("modernmetals:rutile_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/rutile"));
    ores.add(new UBOreConfig("modernmetals:tantalum_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/tantalum"));
    ores.add(new UBOreConfig("modernmetals:titanium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/titanium"));
    ores.add(new UBOreConfig("modernmetals:tungsten_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/tungsten"));
    ores.add(new UBOreConfig("modernmetals:uranium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/uranium"));
    ores.add(new UBOreConfig("modernmetals:zirconium_ore", ModInfo.MODID + ":blocks/overlays/modernmetals/zirconium"));
    return ores;
  }

  private ArrayList<UBOreConfig> getRFToolsOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("rftools:dimensional_shard_ore", 0, ModInfo.MODID + ":blocks/overlays/rftools/dimensionalshard"));
    return ores;
  }

  private ArrayList<UBOreConfig> getTechRebornOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "techreborn:ore";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/techreborn/galena"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/techreborn/iridium"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/techreborn/ruby"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/techreborn/sapphire"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/techreborn/bauxite"));
    ores.add(new UBOreConfig(oreName, 12, ModInfo.MODID + ":blocks/overlays/techreborn/lead"));
    ores.add(new UBOreConfig(oreName, 13, ModInfo.MODID + ":blocks/overlays/techreborn/silver"));
    return ores;
  }

  private ArrayList<UBOreConfig> getThaumcraftOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    ores.add(new UBOreConfig("thaumcraft:ore_cinnabar", ModInfo.MODID + ":blocks/overlays/custom/cinnabar"));
    return ores;
  }

  private ArrayList<UBOreConfig> getThermalFoundationOres() {
    ArrayList<UBOreConfig> ores = new ArrayList<>();
    String oreName = "thermalfoundation:ore";
    ores.add(new UBOreConfig(oreName, 0, ModInfo.MODID + ":blocks/overlays/thermalfoundation/copper"));
    ores.add(new UBOreConfig(oreName, 1, ModInfo.MODID + ":blocks/overlays/thermalfoundation/tin"));
    ores.add(new UBOreConfig(oreName, 4, ModInfo.MODID + ":blocks/overlays/thermalfoundation/aluminum"));
    ores.add(new UBOreConfig(oreName, 3, ModInfo.MODID + ":blocks/overlays/thermalfoundation/lead"));
    ores.add(new UBOreConfig(oreName, 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/silver"));
    ores.add(new UBOreConfig(oreName, 5, ModInfo.MODID + ":blocks/overlays/thermalfoundation/nickel"));
    ores.add(new UBOreConfig(oreName, 6, ModInfo.MODID + ":blocks/overlays/thermalfoundation/platinum"));
    ores.add(new UBOreConfig(oreName, 7, ModInfo.MODID + ":blocks/overlays/thermalfoundation/iridium"));
    ores.add(new UBOreConfig(oreName, 8, ModInfo.MODID + ":blocks/overlays/thermalfoundation/mana_infused"));
    ores.add(new UBOreConfig("thermalfoundation:ore_fluid", 2, ModInfo.MODID + ":blocks/overlays/thermalfoundation/destabilized_redstone"));
    return ores;
  }
}
