/*  1:   */ package snorri.bounty;
/*  4:   */ import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
/*  6:   */ import org.bukkit.SkullType;
/*  7:   */ import org.bukkit.entity.Player;
/*  8:   */ import org.bukkit.event.EventHandler;
/*  9:   */ import org.bukkit.event.Listener;
/* 10:   */ import org.bukkit.event.entity.PlayerDeathEvent;
/* 11:   */ import org.bukkit.event.player.PlayerLoginEvent;
/* 12:   */ import org.bukkit.inventory.ItemStack;
/* 14:   */ import org.bukkit.inventory.meta.SkullMeta;
/* 15:   */ 
/* 16:   */ public class BountyEventListener
/* 17:   */   implements Listener
/* 18:   */ {
/* 19:   */   @EventHandler
/* 20:   */   public void onDeath(PlayerDeathEvent event)
/* 21:   */   {
/* 22:17 */     Player victim = event.getEntity();
/* 23:18 */     if (!Settings.enabledInWorld(victim.getWorld())) {
/* 24:19 */       return;
/* 25:   */     }
				Player killer = victim.getKiller();
/* 26:20 */     if ((killer == null) || (!(killer instanceof Player)) || killer.equals(victim)) {
/* 27:21 */       return;
/* 28:   */     }
/* 30:23 */     double reward = PlayerBounty.completeBountiesOn(victim, killer);
				Bounty.println("" + reward);
/* 31:24 */     if (reward > 0.0D)
/* 32:   */     {
/* 33:25 */       Bounty.getEconomy().depositPlayer(killer, reward);
/* 34:26 */       killer.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.compensated", victim.getName(), killer.getName(), Bounty.formatCurrency(reward)));
/* 35:27 */       Bounty.globalBroadcast(LanguageSettings.getString("broadcast.compensated", victim.getName(), killer.getName(), Bounty.formatCurrency(reward)));
/* 36:28 */       checkToRemoveBountiesDoneBy(killer);
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   @EventHandler
/* 41:   */   public void onLogin(PlayerLoginEvent event)
/* 42:   */  {
				Player player = event.getPlayer();
			//	Bounty.updateScore(player);
/* 43:34 */     checkToRemoveBountiesBy(player);
/* 44:   */   }
/* 45:   */   
/* 46:   */   private void checkToRemoveBountiesBy(Player player)
/* 47:   */   {
/* 48:38 */     for (OfflinePlayer target : PlayerBounty.removeBountiesBy(player))
/* 49:   */     {
/* 50:39 */       player.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.completed", target.getName(), player.getName(), ""));
/* 51:40 */       awardHead(player, target);
/* 52:   */     }
/* 53:   */   }

			
/* 54:   */   
/* 55:   */   private void checkToRemoveBountiesDoneBy(Player player)
/* 56:   */   {
/* 57:45 */     for (PlayerBounty bounty : PlayerBounty.removeBountiesDoneBy(player))
/* 58:   */     {
/* 59:46 */       awardHead(bounty.getSetBy().getPlayer(), bounty.getTarget());
/* 60:47 */       bounty.getSetBy().getPlayer().sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.completed", bounty.getTarget().getName(), bounty.getSetBy().getName(), ""));
/* 61:   */     }
/* 62:   */   }
/* 63:   */   

			//TODO make sure this is legit code

			private void awardHead(Player player, OfflinePlayer victim) {
				if (Settings.shouldAwardHead())
					player.getInventory().addItem(getSkullOf(victim.getName()));
			}

/* 64:   */   private ItemStack getSkullOf(String victim)
/* 65:   */   {
/* 66:52 */     ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
/* 67:53 */     SkullMeta meta = (SkullMeta)skull.getItemMeta();
/* 68:54 */     meta.setOwner(victim);
/* 69:55 */     if (Settings.shouldMarkSkulls()) {
/* 70:56 */       meta.setDisplayName(victim + " (BOUNTIED)");
/* 71:   */     }
/* 72:57 */     skull.setItemMeta(meta);
/* 73:58 */     return skull;
/* 74:   */   }
/* 75:   */ }


/* Location:           C:\Users\vikin_000\Desktop\Minecraft Plugin Development\Bounty.jar
 * Qualified Name:     snorri.bounty.BountyEventListener
 * JD-Core Version:    0.7.0.1
 */