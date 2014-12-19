/*  1:   */ package snorri.bounty;
/*  2:   */ 
/*  3:   */ import org.bukkit.configuration.file.FileConfiguration;
/*  4:   */ 
/*  5:   */ public class LanguageSettings
/*  6:   */ {
/*  7:   */   private static FileConfiguration config;
/*  8:   */   
/*  9:   */   public static void setConfig(FileConfiguration c)
/* 10:   */   {
/* 11:10 */     config = c;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public static void writeDefaultLanguageFile(FileConfiguration config)
/* 15:   */   {
/* 16:14 */     config.set("chat.newPlayer1", "[WARNING] That player has not played on this server!");
/* 17:15 */     config.set("chat.newPlayer2", "If you really want to bounty them, use /bounty -o <player> <$$$>");
/* 18:16 */     config.set("chat.minimumBounty", "The minimum bounty on this server is $a");
/* 19:17 */     config.set("chat.outOfCash", "You do not have enough cash");
/* 20:18 */     config.set("chat.bountyPlaced", "A bounty has been placed on $t");
/* 21:19 */     config.set("broadcast.bountyPlaced", "$a bounty placed on $t by $s");
/* 22:20 */     config.set("chat.bountyIncreased", "Your bounty on $t has been increased");
/* 23:21 */     config.set("chat.activeHeader", "Active bounties:");
/* 24:22 */     config.set("chat.none", "NONE! Make one");
/* 25:23 */     config.set("chat.bountyList", "$a on $t by $s");
/* 26:24 */     config.set("chat.bountyOn", "The bounty on $t is $a");
/* 27:25 */     config.set("chat.bountyCancelled", "You have received $a for cancelling your bounty on $t");
/* 28:26 */     config.set("chat.compensated", "You have been compensated with $a for your work, assassin");
/* 29:27 */     config.set("broadcast.compensated", "$a reward has been claimed by $s for kiling $t");
/* 30:28 */     config.set("chat.completed", "Your bounty on $t has been completed");
/* 31:29 */     config.set("permissions.denied", "You do not have permission");
/* 32:30 */     config.set("permissions.anonymous", "Anonymous bounties are disabled on this server");
/* 33:31 */     setConfig(config);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public static String getString(String path, String targetName, String setByName, String formattedAmount)
/* 37:   */   {	
/* 38:35 */     String raw = config.getString(path);
/* 39:36 */     raw = raw.replace("$t", targetName);
/* 40:37 */     raw = raw.replace("$s", setByName);
/* 41:38 */     raw = raw.replace("$a", formattedAmount);
/* 42:39 */     return raw;
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\vikin_000\Desktop\Minecraft Plugin Development\Bounty.jar
 * Qualified Name:     snorri.bounty.LanguageSettings
 * JD-Core Version:    0.7.0.1
 */