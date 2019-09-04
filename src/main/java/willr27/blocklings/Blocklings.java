package willr27.blocklings;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import willr27.blocklings.entity.BlocklingEntity;
import willr27.blocklings.entity.EntityTypeBuilder;
import willr27.blocklings.entity.EntityTypes;
import willr27.blocklings.gui.container.ContainerTypes;
import willr27.blocklings.network.NetworkHandler;
import willr27.blocklings.render.BlocklingRenderer;

@Mod(Blocklings.MODID)
public class Blocklings
{
    public static final String MODID = "blocklings";
    public static final String MODNAME = "Blocklings";

    public static final EntityTypeBuilder ENTITY_TYPE_BUILDER = new EntityTypeBuilder();

    public Blocklings()
    {
        EntityTypes.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        //ScreenManager.registerFactory(null, EquipmentScreen::new);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.init();
    }

    private void doClientStuff(final FMLClientSetupEvent e)
    {
        RenderingRegistry.registerEntityRenderingHandler(BlocklingEntity.class, (EntityRendererManager renderManager) -> new BlocklingRenderer(renderManager));
    }

    private void enqueueIMC(final InterModEnqueueEvent e)
    {

    }

    private void processIMC(final InterModProcessEvent e)
    {

    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void registerEntityTypes(final RegistryEvent.Register<EntityType<?>> e)
        {
            for (EntityType type : ENTITY_TYPE_BUILDER.ENTITY_TYPES)
            {
                e.getRegistry().register(type);
            }
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> e)
        {
            for (Item egg : ENTITY_TYPE_BUILDER.SPAWN_EGGS)
            {
                e.getRegistry().register(egg);
            }
        }

        @SubscribeEvent
        public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> e)
        {
            for (ContainerType<?> type : ContainerTypes.CONTAINER_TYPES)
            {
                e.getRegistry().register(type);
            }
        }
    }
}