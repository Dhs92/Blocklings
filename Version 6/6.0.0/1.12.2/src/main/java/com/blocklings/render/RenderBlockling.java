package com.blocklings.render;

import javax.annotation.Nonnull;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.models.ModelBlockling;
import com.blocklings.util.ResourceLocationBlocklings;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class RenderBlockling extends RenderLiving<EntityBlockling> {
	
	private ResourceLocation texture = new ResourceLocationBlocklings("textures/entities/blockling/blockling_0.png");

	public static final Factory FACTORY = new Factory();

	public RenderBlockling(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelBlockling(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityBlockling entity) {
		return texture;
	}

	public static class Factory implements IRenderFactory<EntityBlockling> {
		@Override
		public Render<? super EntityBlockling> createRenderFor(RenderManager manager) {
			return new RenderBlockling(manager);
		}

	}
	
}