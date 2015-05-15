package me.crafter.mc.healthbar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;



@Mod(modid = HealthBar.MODID, version = HealthBar.VERSION, guiFactory = "me.crafter.mc.healthbar.ModGuiFactory")
public class HealthBar {
	
		@Mod.Instance("healthbar")
		public static HealthBar instance;
		
	    public static final String MODID = "healthbar";
	    public static final String VERSION = "1.6";
	    
	    @Mod.EventHandler
	    public void init(FMLInitializationEvent event)
	    {
	        MinecraftForge.EVENT_BUS.register(new HealthBarMaker());
	        FMLCommonHandler.instance().bus().register(new HealthConfig());
	    }
	    
	    @Mod.EventHandler
	    public void preInit(FMLPreInitializationEvent event) {
	        HealthConfig.startConfig(event);
	    }
	    
	    
}
