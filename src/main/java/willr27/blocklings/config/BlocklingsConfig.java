package willr27.blocklings.config;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import willr27.blocklings.block.BlockUtil;
import willr27.blocklings.entity.EntityUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class BlocklingsConfig
{
    public static final String CATEGORY_GENERAL = "General";
    public static final String CATEGORY_ENTITIES = "Entities";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;


    private static ForgeConfigSpec.ConfigValue<List<String>> entities;
    private static ForgeConfigSpec.ConfigValue<List<String>> ores;
    private static ForgeConfigSpec.ConfigValue<List<String>> logs;
    private static ForgeConfigSpec.ConfigValue<List<String>> cropsSeeds;

    public static List<String> getEntities()
    {
        return entities.get();
    }
    public static void initEntities()
    {
        if (entities.get().isEmpty())
        {
            List<String> strings = EntityUtil.ENTITIES.stream().map(entity -> entity.toString()).collect(Collectors.toList());
            entities.set(strings);
        }
    }
    public static List<String> getOres()
    {
        return ores.get();
    }
    public static List<String> getLogs()
    {
        return logs.get();
    }
    public static Map<String, String> getCropsSeeds()
    {
        Map<String, String> returnMap = new HashMap<>();
        List<String> cropsToSeeds = cropsSeeds.get(); // TODO: CACHE
        for (String entry : cropsToSeeds)
        {
            String crop = entry.split(";")[0];
            String seed = entry.split(";")[1];
            returnMap.put(crop, seed);
        }
        return returnMap;
    }

    static
    {
        COMMON_BUILDER.push(CATEGORY_GENERAL);

        ores = COMMON_BUILDER.define("ores", BlockUtil.ORES.stream().map(block -> block.getRegistryName().toString()).collect(Collectors.toList()));
        logs = COMMON_BUILDER.define("logs", BlockUtil.LOGS.stream().map(block -> block.getRegistryName().toString()).collect(Collectors.toList()));

        List<String> cropsToSeeds = new ArrayList<>();
        for (Map.Entry<Block, Item>  entry : BlockUtil.CROPS_SEEDS.entrySet())
        {
            String crop = entry.getKey().getRegistryName().toString();
            String seed = entry.getValue().getRegistryName().toString();
            cropsToSeeds.add(crop + ";" + seed);
        }
        cropsSeeds = COMMON_BUILDER.define("crops_seeds", cropsToSeeds);

        COMMON_BUILDER.pop();


        COMMON_BUILDER.push(CATEGORY_ENTITIES);

        entities = COMMON_BUILDER.define("entities", new ArrayList<>());

        COMMON_BUILDER.pop();


        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent)
    {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent)
    {

    }
}