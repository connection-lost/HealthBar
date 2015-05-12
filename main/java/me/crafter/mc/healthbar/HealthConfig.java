package me.crafter.mc.healthbar;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HealthConfig {

	public static int visibledistance = 30;
	public static float barscale = 26;
	public static boolean barhp = true;
	public static boolean barpct = false;
	public static boolean barsecret = true;

	
	public static Configuration config;
	 
    public static final String CATEGORY_GENERAL = "general";
    
    public static void startConfig(FMLPreInitializationEvent event) {
        setConfigLocation(event.getSuggestedConfigurationFile());
        init();
    }
 
    public static void setConfigLocation(File configFile) {
    	if (config == null){
            config = new Configuration(configFile);
    	}
    }
 
    public static void init() {
    	config.load();
        try {
        	visibledistance = config.getInt("1. Visible Distance", Configuration.CATEGORY_GENERAL, 25, 5, 80, "nope");
        	barscale = config.getInt("2. Bar Size", Configuration.CATEGORY_GENERAL, 26, 10, 50, "nope");
        	barhp = config.getBoolean("3. Display HP Value", Configuration.CATEGORY_GENERAL, true, "nope");
        	barpct = config.getBoolean("4. Display HP Percentage", Configuration.CATEGORY_GENERAL, true, "nope");
        	barsecret = config.getBoolean("?. Kancolle?", Configuration.CATEGORY_GENERAL, false, "nope");            
        } catch (Exception e){
        } finally {
                if(config.hasChanged()){
                config.save();
                System.out.println("Config saved");
                HealthBarMaker.reloadConfig();
            }
        }
    }
 
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if(eventArgs.modID.equalsIgnoreCase("healthbar")) {
        	config.save();
        	init();
        	HealthBarMaker.reloadConfig();
        }
    }
	
	
}
