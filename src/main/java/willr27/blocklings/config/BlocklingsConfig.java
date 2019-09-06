package willr27.blocklings.config;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import willr27.blocklings.block.BlockUtil;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class BlocklingsConfig
{
    public static final String CATEGORY_BLOCKS = "blocks";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;


    public static ForgeConfigSpec.ConfigValue<List<String>> ORES;
    public static ForgeConfigSpec.ConfigValue<List<String>> LOGS;

    static
    {
        COMMON_BUILDER.comment("Block settings").push(CATEGORY_BLOCKS);
        ORES = COMMON_BUILDER.define("ores", BlockUtil.ORES.stream().map(block -> block.getRegistryName().toString()).collect(Collectors.toList()));
        LOGS = COMMON_BUILDER.define("logs", BlockUtil.LOGS.stream().map(block -> block.getRegistryName().toString()).collect(Collectors.toList()));
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