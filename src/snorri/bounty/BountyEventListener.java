/*  1:   */ package snorri.bounty;
/*  2:   */ 
/*  3:   */ import net.milkbowl.vault.economy.Economy;
/*  4:   */ import org.bukkit.Material;
/*  5:   */ import org.bukkit.OfflinePlayer;
/*  6:   */ import org.bukkit.SkullType;
/*  7:   */ import org.bukkit.entity.Player;
/*  8:   */ import org.bukkit.event.EventHandler;
/*  9:   */ import org.bukkit.event.Listener;
/* 10:   */ import org.bukkit.event.entity.PlayerDeathEvent;
/* 11:   */ import org.bukkit.event.player.PlayerLoginEvent;
/* 12:   */ import org.bukkit.inventory.ItemStack;
/* 13:   */ import org.bukkit.inventory.PlayerInventory;
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
/* 26:20 */     if ((victim.getKiller() == null) || (!(victim.getKiller() instanceof Player))) {
/* 27:21 */       return;
/* 28:   */     }
/* 29:22 */     Player killer = victim.getKiller();
/* 30:23 */     double reward = PlayerBounty.completeBountiesOn(victim, killer);
/* 31:24 */     if (reward > 0.0D)
/* 32:   */     {
/* 33:25 */       Bounty.getEconomy().depositPlayer(killer.getName(), reward);
/* 34:26 */       killer.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.compensated", victim.getName(), killer.getName(), Bounty.formatCurrency(reward)));
/* 35:27 */       Bounty.globalBroadcast(LanguageSettings.getString("broadcast.compensated", victim.getName(), killer.getName(), Bounty.formatCurrency(reward)));
/* 36:28 */       checkToRemoveBountiesDoneBy(killer);
/* 37:   */     }
/* 38:   */   }
/* 39:   */   
/* 40:   */   @EventHandler
/* 41:   */   public void onLogin(PlayerLoginEvent event)
/* 42:   */   {
/* 43:34 */     checkToRemoveBountiesBy(event.getPlayer());
/* 44:   */   }
/* 45:   */   
/* 46:   */   private void checkToRemoveBountiesBy(Player player)
/* 47:   */   {
/* 48:38 */     for (String target : PlayerBounty.removeBountiesBy(player))
/* 49:   */     {
/* 50:39 */       player.sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.completed", target, player.getName(), ""));
/* 51:40 */       player.getInventory().addItem(new ItemStack[] { getSkullOf(target) });
/* 52:   */     }
/* 53:   */   }
/* 54:   */   
/* 55:   */   private void checkToRemoveBountiesDoneBy(Player player)
/* 56:   */   {
/* 57:45 */     for (PlayerBounty bounty : PlayerBounty.removeBountiesDoneBy(player))
/* 58:   */     {
/* 59:46 */       player.getInventory().addItem(new ItemStack[] { getSkullOf(bounty.getTarget().getName()) });
/* 60:47 */       bounty.getSetBy().getPlayer().sendMessage(Settings.getColor("success") + LanguageSettings.getString("chat.completed", bounty.getTarget().getName(), bounty.getSetBy().getName(), ""));
/* 61:   */     }
/* 62:   */   }
/* 63:   */   
/* 64:   */   private ItemStack getSkullOf(String victim)
/* 65:   */   {
/* 66:52 */     ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
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