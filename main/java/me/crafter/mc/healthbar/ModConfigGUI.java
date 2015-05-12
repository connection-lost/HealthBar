package me.crafter.mc.healthbar;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModConfigGUI extends GuiConfig {
	public ModConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(HealthConfig.config.getCategory(Configuration.CATEGORY_GENERAL))
		.getChildElements(), "healthbar", false, false, "==[HealthBar Settings]==");
	}
}