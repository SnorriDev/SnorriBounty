/*  1:   */ package snorri.bounty;
/*  2:   */ 
/*  3:   */ import com.google.common.base.Joiner;

/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.HashMap;
/*  6:   */ import java.util.List;

/*  7:   */ import org.bukkit.ChatColor;
/*  8:   */ import org.bukkit.World;
/*  9:   */ import org.bukkit.configuration.ConfigurationSection;
/* 10:   */ import org.bukkit.configuration.file.FileConfiguration;
/* 11:   */ 
/* 12:   */ public class Settings
/* 13:   */ {
	
			//TODO: add a bounty "price" or "tax" similar to cancel penalty
	
/* 14:16 */   private static double minBounty = 50.0D;
			  private static int listLength = 5;
/* 15:17 */   private static double cancelPenalty = 0.25D;
			  private static double awardHeadChance = 1.0D;
/* 16:18 */   private static boolean markSkulls = true;
/* 17:19 */   private static boolean globalBroadcast = true;
/* 18:20 */   private static boolean allowAnonymous = true;
/* 19:21 */   private static String anonymousName = "anonymous";
/* 20:22 */   private static boolean enableInAllWorlds = true;
/* 21:23 */   private static List<String> enabledWorlds = new ArrayList<String>();
/* 22:24 */   private static HashMap<String, String> colors = new HashMap<String, String>();
/* 23:   */   
/* 24:   */   static
/* 25:   */   {
/* 26:27 */     enabledWorlds.add("list worlds here if 'enableInAllWorlds' is false");
/* 27:28 */     colors.put("success", ChatColor.GREEN.toString());
/* 28:29 */     colors.put("failure", ChatColor.RED.toString());
/* 29:30 */     colors.put("global", ChatColor.GOLD.toString());
/* 30:31 */     colors.put("blank", ChatColor.GRAY.toString());
/* 31:   */   }
/* 32:   */   
			  public static boolean shouldAwardHead() {
				  return Math.random() < awardHeadChance;
			  }

/* 33:   */   public static double getMin()
/* 34:   */   {
/* 35:35 */     return minBounty;
/* 36:   */   }

			 	public static int getListLength() {
			 		return listLength;
			 	}
/* 37:   */   
/* 38:   */   public static boolean allowAnonymous()
/* 39:   */   {
/* 40:39 */     return allowAnonymous;
/* 41:   */   }
/* 42:   */   
/* 43:   */   public static boolean enabledInWorld(World world)
/* 44:   */   {
/* 45:43 */     return (enableInAllWorlds) || (enabledWorlds.contains(world.getName()));
/* 46:   */   }
/* 47:   */   
/* 48:   */   public static boolean shouldMarkSkulls()
/* 49:   */   {
/* 50:47 */     return markSkulls;
/* 51:   */   }
/* 52:   */   
/* 53:   */   public static boolean shouldBroadcast()
/* 54:   */   {
/* 55:51 */     return globalBroadcast;
/* 56:   */   }
/* 57:   */   
/* 58:   */   public static double getReturnAmount(double bountyCost)
/* 59:   */   {
/* 60:55 */     return (1.0D - cancelPenalty) * bountyCost;
/* 61:   */   }
/* 62:   */   
/* 63:   */   public static String getColor(String purpose)
/* 64:   */   {
/* 65:59 */     return (String)colors.get(purpose);
/* 66:   */   }
/* 67:   */   
/* 68:   */   public static String getAnonymousName()
/* 69:   */   {
/* 70:63 */     return anonymousName;
/* 71:   */   }
/* 72:   */   
/* 73:   */   public static void readFromConfig(FileConfiguration config)
/* 74:   */   {
/* 75:68 */     minBounty = config.getDouble("minBounty");
				listLength = config.getInt("listLength");
/* 76:69 */     cancelPenalty = config.getDouble("cancelPenalty");
				awardHeadChance = config.getDouble("awardHeadChance");
/* 77:70 */     markSkulls = config.getBoolean("markSkulls");
/* 78:71 */     globalBroadcast = config.getBoolean("globalBroadcast");
/* 79:72 */     allowAnonymous = config.getBoolean("allowAnonymous");
/* 80:73 */     anonymousName = config.getString("anonymousName");
/* 81:74 */     enableInAllWorlds = config.getBoolean("enableInAllWorlds");
/* 82:75 */     enabledWorlds = config.getStringList("enabledWorlds");
/* 83:76 */     if (!enableInAllWorlds) {
/* 84:77 */       Bounty.println("Enabled in worlds: " + Joiner.on(", ").join(enabledWorlds));
/* 85:   */     }
/* 86:79 */     ConfigurationSection colors = config.createSection("colors");
/* 87:80 */     for (String key : colors.getKeys(false)) {
/* 88:81 */       ((HashMap) colors).put(key, colors.getString(key));
/* 89:   */     }
/* 90:   */   }
/* 91:   */   
/* 92:   */   public static void writeToConfig(FileConfiguration config)
/* 93:   */   {
/* 94:85 */     config.set("minBounty", Double.valueOf(minBounty));
				config.set("listLength", listLength);
/* 95:86 */     config.set("cancelPenalty", Double.valueOf(cancelPenalty));
				config.set("awardHeadChance", Double.valueOf(awardHeadChance));
/* 96:87 */     config.set("markSkulls", Boolean.valueOf(markSkulls));
/* 97:88 */     config.set("globalBroadcast", Boolean.valueOf(globalBroadcast));
/* 98:89 */     config.set("allowAnonymous", Boolean.valueOf(allowAnonymous));
/* 99:90 */     config.set("anonymousName", anonymousName);
/* :0:91 */     config.set("enableInAllWorlds", Boolean.valueOf(enableInAllWorlds));
/* :1:92 */     config.set("enabledWorlds", enabledWorlds);
/* :2:93 */     for (String key : colors.keySet()) {
/* :3:94 */       config.set("colors." + key, colors.get(key));
/* :4:   */     }
/* :5:   */   }
/* :6:   */ }


/* Location:           C:\Users\vikin_000\Desktop\Minecraft Plugin Development\Bounty.jar
 * Qualified Name:     snorri.bounty.Settings
 * JD-Core Version:    0.7.0.1
 */