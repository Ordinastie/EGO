package net.malisis.ego.gui.render.background;

import net.malisis.ego.gui.component.UIComponent;
import net.malisis.ego.gui.render.GuiRenderer;
import net.malisis.ego.gui.render.IGuiRenderer;
import net.malisis.ego.gui.render.shape.GuiShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class PanoramicBackground implements IGuiRenderer
{
	private final ResourceLocation backgroundTexture;
	private UIComponent screen;
	private int timer;
	private ResourceLocation[] resLocs = new ResourceLocation[6];
	private GuiShape whiteGradient, blackGradient;

	public PanoramicBackground(UIComponent screen, String base)
	{
		this.screen = screen;

		backgroundTexture = Minecraft.getMinecraft()
									 .getTextureManager()
									 .getDynamicTextureLocation("background", new DynamicTexture(256, 256));

		for (int i = 0; i < 6; i++)
			resLocs[i] = new ResourceLocation(base + "_" + i + ".png");

		whiteGradient = GuiShape.builder(screen)
								.color(0xFFFFFF)
								.topAlpha(0x80)
								.bottomAlpha(0x00)
								.build();
		blackGradient = GuiShape.builder(screen)
								.color(0x000000)
								.topAlpha(0x00)
								.bottomAlpha(0x80)
								.build();
	}

	public PanoramicBackground(UIComponent screen)
	{
		this(screen, "textures/gui/title/background/panorama");
	}

	@Override
	public void render(GuiRenderer renderer)
	{
		GlStateManager.disableAlpha();
		renderer.draw();
		renderSkybox(renderer);
		renderer.startDrawing();
		GlStateManager.enableAlpha();

		whiteGradient.and(blackGradient)
					 .render(renderer);
	}

	private void renderSkybox(GuiRenderer renderer)
	{
		int x = screen.position()
					  .x();
		int y = screen.position()
					  .y();
		int width = screen.size()
						  .width();
		int height = screen.size()
						   .height();
		float partialTicks = renderer.getPartialTick();
		timer++;

		Minecraft.getMinecraft()
				 .getFramebuffer()
				 .unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		drawPanorama(tessellator, buffer, partialTicks);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		rotateAndBlurSkybox(tessellator, buffer);
		Minecraft.getMinecraft()
				 .getFramebuffer()
				 .bindFramebuffer(true);
		GlStateManager.viewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		float ratio = 120.0F / Math.max(width, height);
		float w = height * ratio / 256.0F;
		float h = width * ratio / 256.0F;
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(x, y + height, 0)
			  .tex((0.5F - w), (0.5F + h))
			  .color(1.0F, 1.0F, 1.0F, 1.0F)
			  .endVertex();
		buffer.pos(x + width, y + height, 0)
			  .tex((0.5F - w), (0.5F - h))
			  .color(1.0F, 1.0F, 1.0F, 1.0F)
			  .endVertex();
		buffer.pos(x + width, y, 0)
			  .tex((0.5F + w), (0.5F - h))
			  .color(1.0F, 1.0F, 1.0F, 1.0F)
			  .endVertex();
		buffer.pos(x, y, 0)
			  .tex((0.5F + w), (0.5F + h))
			  .color(1.0F, 1.0F, 1.0F, 1.0F)
			  .endVertex();
		tessellator.draw();
	}

	private void rotateAndBlurSkybox(Tessellator tessellator, BufferBuilder buffer)
	{
		int x = screen.position()
					  .x();
		int y = screen.position()
					  .y();
		int width = screen.size()
						  .width();
		int height = screen.size()
						   .height();

		Minecraft.getMinecraft()
				 .getTextureManager()
				 .bindTexture(backgroundTexture);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GlStateManager.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
											GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.colorMask(true, true, true, false);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();

		for (int i = 0; i < 3; ++i)
		{
			int blurIntensity = (int) ((1.0F / (i + 1)) * 255.0F);
			float rotation = (i - 1) / 256.0F;
			buffer.pos(x + width, y + height, 0)
				  .tex((0.0F + rotation), 1.0D)
				  .color(255, 255, 255, blurIntensity)
				  .endVertex();
			buffer.pos(x + width, y, 0)
				  .tex((1.0F + rotation), 1.0D)
				  .color(255, 255, 255, blurIntensity)
				  .endVertex();
			buffer.pos(x, y, 0)
				  .tex((1.0F + rotation), 0.0D)
				  .color(255, 255, 255, blurIntensity)
				  .endVertex();
			buffer.pos(x, y + height, 0)
				  .tex((0.0F + rotation), 0.0D)
				  .color(255, 255, 255, blurIntensity)
				  .endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	private void drawPanorama(Tessellator tessellator, BufferBuilder buffer, float partialTicks)
	{
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
											GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		for (int i = 0; i < 64; ++i)
		{
			GlStateManager.pushMatrix();
			float x = ((i % 8.0F) / 8.0F - 0.5F) / 64.0F;
			float y = ((i / 8.0F) / 8.0F - 0.5F) / 64.0F;
			GlStateManager.translate(x, y, 0.0F);
			GlStateManager.rotate(MathHelper.sin((timer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-(timer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int j = 0; j < 6; ++j)
			{
				GlStateManager.pushMatrix();

				switch (j)
				{
					case 1:
						GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 2:
						GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 3:
						GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
						break;
					case 4:
						GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
						break;
					case 5:
						GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
						break;
				}

				Minecraft.getMinecraft()
						 .getTextureManager()
						 .bindTexture(resLocs[j]);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				int alpha = 255 / (i + 1);
				buffer.pos(-1.0D, -1.0D, 1.0D)
					  .tex(0.0D, 0.0D)
					  .color(255, 255, 255, alpha)
					  .endVertex();
				buffer.pos(1.0D, -1.0D, 1.0D)
					  .tex(1.0D, 0.0D)
					  .color(255, 255, 255, alpha)
					  .endVertex();
				buffer.pos(1.0D, 1.0D, 1.0D)
					  .tex(1.0D, 1.0D)
					  .color(255, 255, 255, alpha)
					  .endVertex();
				buffer.pos(-1.0D, 1.0D, 1.0D)
					  .tex(0.0D, 1.0D)
					  .color(255, 255, 255, alpha)
					  .endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		buffer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}
}
