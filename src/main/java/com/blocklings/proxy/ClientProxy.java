package com.blocklings.proxy;

import com.blocklings.entity.EntityHelper;
import com.blocklings.util.ResourceLocationBlocklings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy
{
    public static int sphereIdOutside;
    public static int sphereIdInside;

    @Override
    public void preInit(FMLPreInitializationEvent e)
    {
        EntityHelper.registerRenderers();
    }

    @Override
    public void init(FMLInitializationEvent e)
    {
//        List<IResourcePack> defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks");
//        defaultResourcePacks.add(new BlocklingsResourcePack());
//        Minecraft.getMinecraft().refreshResources();

        Sphere sphere = new Sphere();
        sphere.setDrawStyle(GLU.GLU_FILL);
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setTextureFlag(true);
        sphere.setOrientation(GLU.GLU_OUTSIDE);

        sphereIdOutside = GL11.glGenLists(1);
        GL11.glNewList(sphereIdOutside, GL11.GL_COMPILE);
        ResourceLocation rL = new ResourceLocationBlocklings("textures/entities/blockling/melon.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(rL);
        sphere.draw(0.5F, 64, 64);
        GL11.glEndList();

        sphere.setOrientation(GLU.GLU_INSIDE);
        sphereIdInside = GL11.glGenLists(1);
        GL11.glNewList(sphereIdInside, GL11.GL_COMPILE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rL);
        sphere.draw(0.5F, 32, 32);
        GL11.glEndList();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e)
    {

    }

    @Override
    public EntityPlayer getPlayer(MessageContext ctx)
    {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : ctx.getServerHandler().player);
    }
}