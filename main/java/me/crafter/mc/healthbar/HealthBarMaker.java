package me.crafter.mc.healthbar;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HealthBarMaker {

	public static Minecraft mc = Minecraft.getMinecraft();
	public static int visibledistance = HealthConfig.visibledistance;
	public static float barscale = HealthConfig.barscale;
	public static boolean barhp = HealthConfig.barhp;
	public static boolean barpct = HealthConfig.barpct;
	public static boolean barsecret = HealthConfig.barsecret;
	public static int innerframe = 0x1A1A1A;
	public static int outerframe = 0x969AA5;
	
	public static void reloadConfig(){
		visibledistance = HealthConfig.visibledistance;
		barscale = HealthConfig.barscale;
		barhp = HealthConfig.barhp;
		barpct = HealthConfig.barpct;
		barsecret = HealthConfig.barsecret;
	}

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onRenderWorldLast(RenderWorldLastEvent event) {

		EntityLivingBase cameraEntity = (EntityLivingBase) mc.getRenderViewEntity();
		Vec3 renderingVector = cameraEntity.getPositionEyes(event.partialTicks);
		Frustum frustrum = new Frustum();
		double viewX = cameraEntity.lastTickPosX
				+ (cameraEntity.posX - cameraEntity.lastTickPosX)
				* event.partialTicks;
		double viewY = cameraEntity.lastTickPosY
				+ (cameraEntity.posY - cameraEntity.lastTickPosY)
				* event.partialTicks;
		double viewZ = cameraEntity.lastTickPosZ
				+ (cameraEntity.posZ - cameraEntity.lastTickPosZ)
				* event.partialTicks;
		frustrum.setPosition(viewX, viewY, viewZ);
		List<Entity> loadedEntities = mc.theWorld
				.getLoadedEntityList();
		for (Entity entity : loadedEntities) {// entity.isInRangeToRenderVec3D(renderingVector) &&
			if (entity != null
					&& entity instanceof EntityLivingBase
//					&& (entity.ignoreFrustumCheck
//							// || frustrum.isBoundingBoxInFrustum(entity.getBoundingBox())
//					)
					&& entity.isEntityAlive()) {
				renderHealthBar((EntityLivingBase) entity, event.partialTicks,
						cameraEntity);
			}
		}
	}

	public void renderHealthBar(EntityLivingBase entity, float partialTicks,
			Entity viewPoint) {
		float distance = entity.getDistanceToEntity(viewPoint);
		if (distance > visibledistance || !entity.canEntityBeSeen(viewPoint)
				|| entity == viewPoint || entity.riddenByEntity == viewPoint)
			return;
		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
				* partialTicks;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
				* partialTicks;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
				* partialTicks;
		float scale = barscale/1000;
		
		//MAYBE optimize the following two lines
		float maxHealth = new BigDecimal(entity.getMaxHealth()).round(
				new MathContext(2)).floatValue();
		float health = new BigDecimal(entity.getHealth()).round(
				new MathContext(2)).floatValue();
		
		if (maxHealth > 1000) return;
		
		String lable = "";
		
		if (barhp){
			lable += health + "/" + maxHealth;
			if (barpct && maxHealth != 0){
				lable += " (" + (int)(health * 100 / maxHealth) + "%)";
			}
		} else {
			if (barpct){
				lable += (int)(health * 100 / maxHealth) + "%";
			}
		}
		
		int pct = maxHealth == 0 ? 0 : (int) (health * 100 / maxHealth);
		if (barsecret){
			if (pct < 26){
				if (!lable.equals("")) lable += " ";
				lable += ("\u00a7\u0063\u5927\u7834");
			} else if (pct < 51){
				if (!lable.equals("")) lable += " ";
				lable += ("\u00a7\u0036\u4e2d\u7834");
			} else if (pct < 76){
				if (!lable.equals("")) lable += " ";
				lable += ("\u00a7\u0065\u5c0f\u7834");
			}
		}
		
//		lable = String.format("%s/%s (%s", health, maxHealth,
//				maxHealth == 0 ? 0 : (int) (health * 100 / maxHealth))
//				+ "%)";
		RenderManager renderManager = mc.getRenderManager();
		if (!lable.equals("")) {
			renderLabel(entity, lable, (float) (x - renderManager.viewerPosX),
					(float) (y - renderManager.viewerPosY + entity.height + 1),
					(float) (z - renderManager.viewerPosZ), visibledistance);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) (x - renderManager.viewerPosX), (float) (y
				- renderManager.viewerPosY + entity.height + 0.7),
				(float) (z - renderManager.viewerPosZ));
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		int innerframe2 = innerframe;
		
		//Health Bar inside
		int coloractual = 0;
		if (pct < 50){
			//red to yellow
			coloractual = 16711680 + pct*1280;
			if (pct <= 25) {
				innerframe2 += (Math.abs(10 - entity.ticksExisted % 20))*(523774);
			}
		} else {
			//yellow to green
			coloractual = 65280 + (100-pct)*327680;
		}
		
		//Health Bar outline
		//x y z width height color1 color2
		drawDoubleOutlinedBox(-(int) maxHealth / 2, -1, 3, (int) maxHealth, 2, innerframe2, outerframe);		
		//float fade = 0.6F
		//fade black health bar
		drawSolidGradientRect(-(int) maxHealth + (maxHealth % 2 == 1 ? 1 : 0), -2, 3, (int) health * 2, 4, 
				(int)(coloractual*0.6F), coloractual);
		
		//oni red health bar
		//drawSolidGradientRect(-(int) maxHealth + (maxHealth % 2 == 1 ? 1 : 0), -2, 3, (int) health * 2, 4, 
		//	0x44 << 16, 0xFF << 16);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public void drawDoubleOutlinedBox(final int x, final int y, final int z,
			final int width, final int height, final int color,
			final int outlineColor) {
		drawDoubleOutlinedBox(x, y, z, width, height, color, outlineColor,
				color);
	}

	public void drawDoubleOutlinedBox(final int x, final int y, final int z,
			final int width, final int height, final int color,
			final int outlineColor, final int outline2Color) {
		drawSolidRect(x * 2 - 2, y * 2 - 2, z, (x + width) * 2 + 2,
				(y + height) * 2 + 2, color);
		drawSolidRect(x * 2 - 1, y * 2 - 1, z, (x + width) * 2 + 1,
				(y + height) * 2 + 1, outlineColor);
		drawSolidRect(x * 2, y * 2, z, (x + width) * 2, (y + height) * 2,
				outline2Color);
	}

	public void drawSolidRect(final int vertex1, final int vertex2,
			final int zLevel, final int vertex3, final int vertex4,
			final int color) {
		GL11.glPushMatrix();
		final Color color1 = new Color(color);
		final Tessellator tess = Tessellator.getInstance();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tess.getWorldRenderer().startDrawingQuads();
		tess.getWorldRenderer().setColorOpaque(color1.getRed(), color1.getGreen(), color1.getBlue());
		tess.getWorldRenderer().addVertex(vertex1, vertex4, zLevel);
		tess.getWorldRenderer().addVertex(vertex3, vertex4, zLevel);
		tess.getWorldRenderer().addVertex(vertex3, vertex2, zLevel);
		tess.getWorldRenderer().addVertex(vertex1, vertex2, zLevel);
		tess.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	public static void renderLabel(EntityLivingBase entity,
			String label, double xpos, double ypos, double zpos, int visibledistance) {

		RenderManager renderManager = mc.getRenderManager();
		if (renderManager.livingPlayer == null || entity == null)
			return;
		double distance = entity.getDistanceSqToEntity(renderManager.livingPlayer);
		//TODO check if next line "if..." is not needed
		if (distance <= visibledistance * visibledistance) {
			FontRenderer fontRenderer = mc.fontRendererObj;
			float scaletemp = 1.6F;
			float scale = 0.016666668F * scaletemp;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) xpos, (float) ypos, (float) zpos);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-scale, -scale, scale);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			byte bytezero = 0;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tessellator.getWorldRenderer().startDrawingQuads();
			int halfstringwidth = fontRenderer.getStringWidth(label) / 2;
			tessellator.getWorldRenderer().setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
			tessellator.getWorldRenderer().addVertex(-halfstringwidth - 1, -1 + bytezero, 0.0D);
			tessellator.getWorldRenderer().addVertex(-halfstringwidth - 1, 8 + bytezero, 0.0D);
			tessellator.getWorldRenderer().addVertex(halfstringwidth + 1, 8 + bytezero, 0.0D);
			tessellator.getWorldRenderer().addVertex(halfstringwidth + 1, -1 + bytezero, 0.0D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			fontRenderer.drawString(label, -fontRenderer.getStringWidth(label) / 2,	bytezero, 553648127);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);
			fontRenderer.drawString(label, -fontRenderer.getStringWidth(label) / 2,	bytezero, -1);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public void drawSolidGradientRect(final int x, final int y, final int z, final int width, 
			final int height, final int color1, final int color2) {
		drawSolidGradientRect0(x, y, (x + width), (y + height), color1, color2, z);
	}

	public void drawSolidGradientRect0(final int x1, final int y1,
			final int x2, final int y2, final int color1,
			final int color2, final int z) {
		GL11.glPushMatrix();
		final Color color1Color = new Color(color1);
		final Color color2Color = new Color(color2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		final Tessellator tess = Tessellator.getInstance();
		tess.getWorldRenderer().startDrawingQuads();
		tess.getWorldRenderer().setColorOpaque(color1Color.getRed(), color1Color.getGreen(),
				color1Color.getBlue());
		tess.getWorldRenderer().addVertex(x1, y2, z);
		tess.getWorldRenderer().addVertex(x2, y2, z);
		tess.getWorldRenderer().setColorOpaque(color2Color.getRed(), color2Color.getGreen(),
				color2Color.getBlue());
		tess.getWorldRenderer().addVertex(x2, y1, z);
		tess.getWorldRenderer().addVertex(x1, y1, z);
		tess.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

}
