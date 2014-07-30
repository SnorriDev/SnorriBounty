/*   1:    */ package snorri.bounty;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.logging.Logger;
/*   7:    */ import net.milkbowl.vault.economy.Economy;
/*   8:    */ import net.milkbowl.vault.economy.EconomyResponse;
/*   9:    */ import net.milkbowl.vault.permission.Permission;
/*  10:    */ import org.bukkit.Bukkit;
/*  11:    */ import org.bukkit.OfflinePlayer;
/*  12:    */ import org.bukkit.Server;
/*  13:    */ import org.bukkit.command.Command;
/*  14:    */ import org.bukkit.command.CommandSender;
/*  15:    */ import org.bukkit.configuration.file.FileConfiguration;
/*  16:    */ import org.bukkit.configuration.file.YamlConfiguration;
/*  17:    */ import org.bukkit.entity.Player;
/*  18:    */ import org.bukkit.plugin.PluginManager;
/*  19:    */ import org.bukkit.plugin.RegisteredServiceProvider;
/*  20:    */ import org.bukkit.plugin.ServicesManager;
/*  21:    */ import org.bukkit.plugin.java.JavaPlugin;
/*  22:    */ 
/*  23:    */ public class Bounty
/*  24:    */   extends JavaPlugin
/*  25:    */ {
/*  26: 25 */   private static final Logger log = Logger.getLogger("Minecraft");
/*  27:    */   private FileConfiguration config;
/*  28:    */   private FileConfiguration languageConfig;
/*  29:    */   private static final String NAME = "Bounty";
/*  30:    */   private static Economy economy;
/*  31:    */   private static Permission permission;
/*  32:    */   public static Server server;
/*  33:    */   
/*  34:    */   public void onEnable()
/*  35:    */   {
/*  36: 37 */     println("Plugin enabled");
/*  37: 38 */     this.config = getConfig();
/*  38: 39 */     this.languageConfig = getCustomConfig("languageConfig.yml");
/*  39: 40 */     server = getServer();
/*  40: 41 */     getServer().getPluginManager().registerEvents(new BountyEventListener(), this);
/*  41: 42 */     setupEconomy();
/*  42: 43 */     setupPermission();
/*  43: 44 */     loadConfig();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void onDisable()
/*  47:    */   {
/*  48:    */     try
/*  49:    */     {
/*  50: 49 */       closeConfig();
/*  51:    */     }
/*  52:    */     catch (IOException e)
/*  53:    */     {
/*  54: 51 */       warn("Failed to close config");
/*  55:    */     }
/*  56: 53 */     println("Plugin disabled");
/*  57:    */   }
/*  58:    */   
/*  59:    */   private FileConfiguration getCustomConfig(String name)
/*  60:    */   {
/*  61: 57 */     File c = new File(getDataFolder(), name);
/*  62: 58 */     return YamlConfiguration.loadConfiguration(c);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean can(CommandSender sender, String p)
/*  66:    */   {
/*  67: 64 */     if (permission == null) {
/*  68: 65 */       return true;
/*  69:    */     }
/*  70: 67 */     return permission.has(sender, p);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String getFlags(String[] args)
/*  74:    */   {
/*  75: 71 */     String result = "";
/*  76: 72 */     for (String arg : args) {
/*  77: 73 */       if (arg.charAt(0) == '-') {
/*  78: 74 */         for (int i = 1; i < arg.length(); i++) {
/*  79: 75 */           result = result + arg.charAt(i);
/*  80:    */         }
/*  81:    */       }
/*  82:    */     }
/*  83: 78 */     return result;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String[] getArgs(String[] args)
/*  87:    */   {
/*  88: 82 */     ArrayList<String> result = new ArrayList();
/*  89: 83 */     for (String arg : args) {
/*  90: 84 */       if (arg.charAt(0) != '-') {
/*  91: 85 */         result.add(arg);
/*  92:    */       }
/*  93:    */     }
/*  94: 87 */     return (String[])result.toArray(args);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
/*  98:    */   {
/*  99: 91 */     if ((cmd.getName().equalsIgnoreCase("bounty")) && (args.length > 1))
/* 100:    */     {
/* 101: 93 */       if (((sender instanceof Player)) && (!can(sender, "bounty.set")))
/* 102:    */       {
/* 103: 94 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("permissions.denied", "", "", ""));
/* 104: 95 */         return true;
/* 105:    */       }
/* 106: 98 */       String flags = getFlags(args);
/* 107: 99 */       args = getArgs(args);
/* 108:100 */       OfflinePlayer target = getServer().getOfflinePlayer(args[0]);
/* 109:101 */       if ((!target.hasPlayedBefore()) && (!flags.contains("o")))
/* 110:    */       {
/* 111:102 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("chat.newPlayer1", target.getName(), sender.getName(), ""));
/* 112:103 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("chat.newPlayer2", target.getName(), sender.getName(), ""));
/* 113:104 */         return true;
/* 114:    */       }
/* 115:106 */       double amount = 0.0D + Integer.parseInt(args[1]);
/* 116:107 */       PlayerBounty existingBounty = PlayerBounty.getBounty(target, (Player)sender);
/* 117:108 */       if ((existingBounty == null) && (amount < Settings.getMin()))
/* 118:    */       {
/* 119:109 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("chat.minimumBounty", target.getName(), sender.getName(), formatCurrency(Settings.getMin())));
/* 120:110 */         return true;
/* 121:    */       }
/* 122:112 */       EconomyResponse result = getEconomy().withdrawPlayer(sender.getName(), amount);
/* 123:113 */       if (!result.transactionSuccess())
/* 124:    */       {
/* 125:114 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("chat.outOfCash", target.getName(), sender.getName(), formatCurrency(amount)));
/* 126:115 */         return true;
/* 127:    */       }
/* 128:117 */       if (existingBounty == null)
/* 129:    */       {
/* 130:119 */         if ((!Settings.allowAnonymous()) && (flags.contains("a")))
/* 131:    */         {
/* 132:120 */           sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("permissions.anonymous", "", "", ""));
/* 133:121 */           return true;
/* 134:    */         }
/* 135:124 */         PlayerBounty b = new PlayerBounty(target, (OfflinePlayer)sender, amount, flags.contains("a"));
/* 136:125 */         sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.bountyPlaced", target.getName(), b.getSetByPlayerName(), formatCurrency(amount)));
/* 137:126 */         globalBroadcast(LanguageSettings.getString("broadcast.bountyPlaced", target.getName(), b.getSetByPlayerName(), formatCurrency(amount)));
/* 138:127 */         return true;
/* 139:    */       }
/* 140:129 */       existingBounty.addReward(amount);
/* 141:130 */       sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.bountyIncreased", target.getName(), sender.getName(), formatCurrency(existingBounty.getReward())));
/* 142:131 */       return true;
/* 143:    */     }
/* 144:133 */     if ((cmd.getName().equalsIgnoreCase("bounties")) && (args.length == 0))
/* 145:    */     {
/* 146:135 */       if (((sender instanceof Player)) && (!can(sender, "bounty.view")))
/* 147:    */       {
/* 148:136 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("permissions.denied", "", "", ""));
/* 149:137 */         return true;
/* 150:    */       }
/* 151:140 */       sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.activeHeader", "", "", ""));
/* 152:141 */       if (PlayerBounty.active.size() == 0) {
/* 153:142 */         sender.sendMessage(Settings.getColor("blank") + LanguageSettings.getString("chat.none", "", "", ""));
/* 154:    */       }
/* 155:144 */       for (int i = 0; (i < PlayerBounty.active.size()) && (i < 5); i++)
/* 156:    */       {
/* 157:145 */         PlayerBounty bounty = (PlayerBounty)PlayerBounty.active.get(i);
/* 158:146 */         sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.bountyList", bounty.getTarget().getName(), bounty.getSetByPlayerName(), formatCurrency(bounty.getReward())));
/* 159:    */       }
/* 160:148 */       return true;
/* 161:    */     }
/* 162:150 */     if ((cmd.getName().equalsIgnoreCase("bountyon")) && (args.length == 1))
/* 163:    */     {
/* 164:152 */       if (((sender instanceof Player)) && (!can(sender, "bounty.view")))
/* 165:    */       {
/* 166:153 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("permissions.denied", "", "", ""));
/* 167:154 */         return true;
/* 168:    */       }
/* 169:157 */       OfflinePlayer target = getServer().getOfflinePlayer(args[0]);
/* 170:158 */       double amount = PlayerBounty.getSumOn(target);
/* 171:159 */       sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.bountyOn", target.getName(), "", formatCurrency(amount)));
/* 172:160 */       return true;
/* 173:    */     }
/* 174:162 */     if ((cmd.getName().equalsIgnoreCase("unbounty")) && (args.length == 1))
/* 175:    */     {
/* 176:164 */       if (((sender instanceof Player)) && (!can(sender, "bounty.cancel")))
/* 177:    */       {
/* 178:165 */         sender.sendMessage(Settings.getColor("failure") + LanguageSettings.getString("permissions.denied", "", "", ""));
/* 179:166 */         return true;
/* 180:    */       }
/* 181:169 */       OfflinePlayer target = getServer().getOfflinePlayer(args[0]);
/* 182:    */       
/* 183:171 */       OfflinePlayer setBy = (OfflinePlayer)sender;
/* 184:172 */       double refund = PlayerBounty.getBounty(target, setBy).cancel();
/* 185:173 */       getEconomy().depositPlayer(sender.getName(), refund);
/* 186:174 */       sender.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.bountyCancelled", target.getName(), sender.getName(), formatCurrency(refund)));
/* 187:175 */       return true;
/* 188:    */     }
/* 189:177 */     return false;
/* 190:    */   }
/* 191:    */   
/* 192:    */   private void loadConfig()
/* 193:    */   {
/* 194:181 */     File dir = new File("plugins/" + getName());
/* 195:182 */     if (!dir.exists())
/* 196:    */     {
/* 197:183 */       dir.mkdir();
/* 198:184 */       println("Created plugin data folder");
/* 199:    */     }
/* 200:186 */     File configLoc = new File(dir, "config.yml");
/* 201:187 */     if (!configLoc.exists())
/* 202:    */     {
/* 203:    */       try
/* 204:    */       {
/* 205:189 */         configLoc.createNewFile();
/* 206:190 */         Settings.writeToConfig(this.config);
/* 207:191 */         PlayerBounty.writeCategories(this.config);
/* 208:192 */         this.config.save(configLoc);
/* 209:193 */         println("Created default config file");
/* 210:    */       }
/* 211:    */       catch (IOException localIOException) {}
/* 212:    */     }
/* 213:    */     else
/* 214:    */     {
/* 215:196 */       Settings.readFromConfig(this.config);
/* 216:197 */       PlayerBounty.readFromConfig(this.config);
/* 217:198 */       println("Settings read from config file");
/* 218:    */     }
/* 219:200 */     configLoc = new File(dir, "languageConfig.yml");
/* 220:201 */     if (!configLoc.exists())
/* 221:    */     {
/* 222:    */       try
/* 223:    */       {
/* 224:203 */         configLoc.createNewFile();
/* 225:204 */         LanguageSettings.writeDefaultLanguageFile(this.languageConfig);
/* 226:205 */         this.languageConfig.save(configLoc);
/* 227:206 */         println("Created default language config file");
/* 228:    */       }
/* 229:    */       catch (IOException localIOException1) {}
/* 230:    */     }
/* 231:    */     else
/* 232:    */     {
/* 233:209 */       LanguageSettings.setConfig(this.languageConfig);
/* 234:210 */       println("Language settings read from language config file");
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   private void closeConfig()
/* 239:    */     throws IOException
/* 240:    */   {
/* 241:215 */     Settings.writeToConfig(this.config);
/* 242:216 */     PlayerBounty.writeToConfig(this.config);
/* 243:217 */     this.config.save("plugins/" + getName() + "/config.yml");
/* 244:    */   }
/* 245:    */   
/* 246:    */   private boolean setupEconomy()
/* 247:    */   {
/* 248:221 */     RegisteredServiceProvider<Economy> ecoProvider = getServer().getServicesManager().getRegistration(Economy.class);
/* 249:222 */     if (ecoProvider != null) {
/* 250:223 */       setEconomy((Economy)ecoProvider.getProvider());
/* 251:    */     }
/* 252:224 */     return getEconomy() != null;
/* 253:    */   }
/* 254:    */   
/* 255:    */   private boolean setupPermission()
/* 256:    */   {
/* 257:229 */     RegisteredServiceProvider<Permission> permProvider = getServer().getServicesManager().getRegistration(Permission.class);
/* 258:230 */     if (permProvider != null) {
/* 259:231 */       setPermission((Permission)permProvider.getProvider());
/* 260:    */     }
/* 261:232 */     return getPermission() != null;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public static String formatCurrency(double value)
/* 265:    */   {
/* 266:236 */     return economy.format(value);
/* 267:    */   }
/* 268:    */   
/* 269:    */   public static void globalBroadcast(String msg)
/* 270:    */   {
/* 271:240 */     if (!Settings.shouldBroadcast()) {
/* 272:241 */       return;
/* 273:    */     }
/* 274:243 */     for (Player player : Bukkit.getOnlinePlayers()) {
/* 275:244 */       player.sendMessage(Settings.getColor("global") + "[" + "Bounty" + "] " + msg);
/* 276:    */     }
/* 277:    */   }
/* 278:    */   
/* 279:    */   public static void println(String msg)
/* 280:    */   {
/* 281:248 */     log.info("[Bounty] " + msg);
/* 282:    */   }
/* 283:    */   
/* 284:    */   public static void warn(String msg)
/* 285:    */   {
/* 286:252 */     log.warning("[Bounty] " + msg);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public static Economy getEconomy()
/* 290:    */   {
/* 291:256 */     return economy;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setEconomy(Economy economy)
/* 295:    */   {
/* 296:260 */     economy = economy;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public static Permission getPermission()
/* 300:    */   {
/* 301:264 */     return permission;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public void setPermission(Permission permission)
/* 305:    */   {
/* 306:268 */     permission = permission;
/* 307:    */   }
/* 308:    */ }


/* Location:           C:\Users\vikin_000\Desktop\Minecraft Plugin Development\Bounty.jar
 * Qualified Name:     snorri.bounty.Bounty
 * JD-Core Version:    0.7.0.1
 */