/*   1:    */ package snorri.bounty;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   6:    */ import java.util.UUID;

/*   7:    */ import org.bukkit.OfflinePlayer;
/*   9:    */ import org.bukkit.configuration.ConfigurationSection;
/*  10:    */ import org.bukkit.configuration.file.FileConfiguration;
/*  11:    */ import org.bukkit.entity.Player;
/*  12:    */ 
/*  13:    */ public class PlayerBounty
/*  14:    */ {
/*  15: 21 */   public static ArrayList<PlayerBounty> active = new ArrayList<PlayerBounty>();
/*  16: 22 */   public static ArrayList<PlayerBounty> completed = new ArrayList<PlayerBounty>();
/*  17:    */   private OfflinePlayer target;
/*  18:    */   private OfflinePlayer setBy;
/*  19:    */   private OfflinePlayer doneBy;
/*  20:    */   private double reward;
/*  21:    */   private boolean anonymous;
/*  22:    */   
/*  23:    */   public static void writeCategories(FileConfiguration config)
/*  24:    */   {
/*  25: 26 */     config.createSection("active");
/*  26: 27 */     config.createSection("completed");
/*  27:    */   }
/*  28:    */   
/*  29:    */   private static String getPlayerSignature(OfflinePlayer player)
/*  30:    */   {
/*  31: 31 */     return player.getUniqueId().toString();
/*  32:    */   }
/*  33:    */   
				@SuppressWarnings("deprecation")
/*  34:    */   private static OfflinePlayer readPlayerSignature(String signature)
/*  35:    */   {
/*  36:    */     try
/*  37:    */     {
/*  38: 37 */       UUID id = UUID.fromString(signature);
/*  39: 38 */       return Bounty.server.getOfflinePlayer(id);
/*  40:    */     }
/*  41:    */     catch (IllegalArgumentException e) {}
/*  42: 40 */     return Bounty.server.getOfflinePlayer(signature);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static void writeToConfig(FileConfiguration config)
/*  46:    */   {
/*  47: 45 */     for (PlayerBounty bounty : active)
/*  48:    */     {
/*  49: 46 */       config.set("active." + getPlayerSignature(bounty.getSetBy()) + "." + getPlayerSignature(bounty.getTarget()) + ".reward", Double.valueOf(bounty.getReward()));
/*  50: 47 */       config.set("active." + getPlayerSignature(bounty.getSetBy()) + "." + getPlayerSignature(bounty.getTarget()) + ".anonymous", Boolean.valueOf(bounty.isAnonymous()));
/*  51:    */     }
/*  52: 49 */     for (PlayerBounty bounty : completed)
/*  53:    */     {
/*  54: 50 */       config.set("completed." + getPlayerSignature(bounty.getSetBy()) + "." + getPlayerSignature(bounty.getTarget()) + ".doneBy", bounty.getDoneBy());
/*  55: 51 */       config.set("completed." + getPlayerSignature(bounty.getSetBy()) + "." + getPlayerSignature(bounty.getTarget()) + ".reward", Double.valueOf(bounty.getReward()));
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static void readFromConfig(FileConfiguration config)
/*  60:    */   {
/*  61: 62 */     ConfigurationSection activeList = config.getConfigurationSection("active");
/*  62: 63 */     ConfigurationSection completedList = config.getConfigurationSection("completed");
/*  64: 65 */     for (String setBy : activeList.getKeys(false))
/*  65:    */     {
/*  67: 66 */       ConfigurationSection subSect = activeList.getConfigurationSection(setBy);
/*  68: 67 */       for (String target : subSect.getKeys(false)) {
/*  69: 68 */      		 OfflinePlayer setter = readPlayerSignature(setBy);
/*  70: 69 */      		 OfflinePlayer victim = readPlayerSignature(target);
/*  71: 70 */      		 double amount = subSect.getDouble(target + ".reward");
/*  72: 71 */      		 boolean anonymous = subSect.getBoolean(target + ".anonymous");
/*  73: 72 */      		 new PlayerBounty(victim, setter, amount, anonymous);
					}
/*  74:    */     }
/*  75: 75 */     for (String setBy : completedList.getKeys(false))
/*  76:    */     {
/*  78: 76 */       ConfigurationSection subSect = completedList.getConfigurationSection(setBy);
/*  79: 77 */       for (String target : subSect.getKeys(false)) {
/*  80: 78 */       	OfflinePlayer setter = readPlayerSignature(setBy);
/*  81: 79 */       	OfflinePlayer victim = readPlayerSignature(target);
/*  82: 80 */       	double amount = subSect.getDouble(target + ".reward");
/*  83: 81 */       	OfflinePlayer doneBy = readPlayerSignature(subSect.getString(target + ".doneBy"));
/*  84: 82 */      		new PlayerBounty(victim, setter, doneBy, amount);
					}
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static double completeBountiesOn(OfflinePlayer target, OfflinePlayer doneBy)
/*  89:    */   {
/*  90: 88 */     double totalReward = 0.0D;
/*  91: 89 */     for (int i = 0; i < active.size(); i++)
/*  92:    */     {
/*  93: 90 */       PlayerBounty bounty = (PlayerBounty)active.get(i);
/*  94: 91 */       if (bounty.target.getUniqueId().equals(target.getUniqueId()))
/*  95:    */       {
/*  96: 92 */         totalReward += bounty.complete(doneBy);
/*  97: 93 */         i--;
/*  98:    */       }
/*  99:    */     }
/* 100: 96 */     return totalReward;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static PlayerBounty getBounty(OfflinePlayer target, OfflinePlayer setBy)
/* 104:    */   {
/* 105:100 */     for (PlayerBounty bounty : active) {
/* 106:101 */       if ((bounty.target.getUniqueId().equals(target.getUniqueId())) && (bounty.setBy.getUniqueId().equals(setBy.getUniqueId()))) {
/* 107:102 */         return bounty;
/* 108:    */       }
/* 109:    */     }
/* 110:103 */     return null;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static double getSumOn(OfflinePlayer target)
/* 114:    */   {
/* 115:107 */     double totalReward = 0.0D;
/* 116:108 */     for (PlayerBounty bounty : active) {
/* 117:109 */       if (bounty.target.getUniqueId().equals(target.getUniqueId())) {
/* 118:110 */         totalReward += bounty.reward;
/* 119:    */       }
/* 120:    */     }
/* 121:111 */     return totalReward;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static ArrayList<OfflinePlayer> removeBountiesBy(OfflinePlayer setBy)
/* 125:    */   {
/* 126:115 */     ArrayList<OfflinePlayer> names = new ArrayList<OfflinePlayer>();
/* 127:116 */     for (int i = 0; i < completed.size(); i++)
/* 128:    */     {
/* 129:117 */       PlayerBounty bounty = (PlayerBounty)completed.get(i);
/* 130:118 */       if (bounty.setBy.getUniqueId().equals(setBy.getUniqueId()))
/* 131:    */       {
/* 132:119 */         names.add(bounty.remove());
/* 133:120 */         i--;
/* 134:    */       }
/* 135:    */     }
/* 136:123 */     return names;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public static ArrayList<PlayerBounty> removeBountiesDoneBy(OfflinePlayer doneBy)
/* 140:    */   {
/* 141:127 */     ArrayList<PlayerBounty> names = new ArrayList<PlayerBounty>();
/* 142:128 */     for (int i = 0; i < completed.size(); i++)
/* 143:    */     {
/* 144:129 */       PlayerBounty bounty = (PlayerBounty)completed.get(i);
/* 145:130 */       if (bounty.doneBy.getUniqueId().equals(doneBy.getUniqueId()))
/* 146:    */       {
/* 147:131 */         names.add(bounty);
/* 148:132 */         bounty.remove();
/* 149:133 */         i--;
/* 150:    */       }
/* 151:    */     }
/* 152:136 */     return names;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public PlayerBounty(OfflinePlayer target, OfflinePlayer setBy, double reward, boolean anonymous)
/* 156:    */   {
/* 157:140 */     this.target = target;
/* 158:141 */     this.setBy = setBy;
/* 159:142 */     this.reward = reward;
/* 160:143 */     active.add(this);
/* 161:144 */     this.anonymous = anonymous;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public PlayerBounty(Player target, Player setBy, double reward, boolean anonymous)
/* 165:    */   {
/* 166:148 */     this((OfflinePlayer) target, (OfflinePlayer) setBy, reward, anonymous);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public PlayerBounty(OfflinePlayer target, OfflinePlayer setBy, OfflinePlayer doneBy, double reward)
/* 170:    */   {
/* 171:152 */     this(target, setBy, reward, true);
/* 172:153 */     complete(doneBy);
/* 173:    */   }
/* 174:    */   
/* 175:    */   private double complete(OfflinePlayer doneBy2)
/* 176:    */   {
/* 177:157 */     this.doneBy = doneBy2;
/* 178:158 */     active.remove(this);
/* 179:159 */     completed.add(this);
/* 180:160 */     this.anonymous = true;
/* 181:161 */     return this.reward;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public double cancel()
/* 185:    */   {
/* 186:165 */     active.remove(this);
/* 187:166 */     return Settings.getReturnAmount(this.reward);
/* 188:    */   }
/* 189:    */   
/* 190:    */   private OfflinePlayer remove()
/* 191:    */   {
/* 192:170 */     completed.remove(this);
/* 193:171 */     return this.target;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public double getReward()
/* 197:    */   {
/* 198:175 */     return this.reward;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public boolean isAnonymous()
/* 202:    */   {
/* 203:179 */     return this.anonymous;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void addReward(double increment)
/* 207:    */   {
/* 208:183 */     this.reward += increment;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public OfflinePlayer getSetBy()
/* 212:    */   {
/* 213:187 */     return this.setBy;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String getSetByPlayerName()
/* 217:    */   {
/* 218:191 */     if (isAnonymous()) {
/* 219:192 */       return Settings.getColor("blank") + Settings.getAnonymousName();
/* 220:    */     }
/* 221:193 */     return this.setBy.getName();
/* 222:    */   }
/* 223:    */   
/* 224:    */   public OfflinePlayer getDoneBy()
/* 225:    */   {
/* 226:197 */     return this.doneBy;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public OfflinePlayer getTarget()
/* 230:    */   {
/* 231:201 */     return this.target;
/* 232:    */   }
/* 233:    */ }


/* Location:           C:\Users\vikin_000\Desktop\Minecraft Plugin Development\Bounty.jar
 * Qualified Name:     snorri.bounty.PlayerBounty
 * JD-Core Version:    0.7.0.1
 */