package com.blocklings;

import com.blocklings.entity.EntityHelper;
import com.blocklings.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Blocklings.MODID, name = Blocklings.MODNAME)
public class Blocklings
{
    public static final String MODID = "blocklings";
    public static final String MODNAME = "Blocklings";

    @SidedProxy(clientSide = "com.blocklings.proxy.ClientProxy", serverSide = "com.blocklings.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance
    public static Blocklings instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        EntityHelper.registerEntities();

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit(e);
    }
}