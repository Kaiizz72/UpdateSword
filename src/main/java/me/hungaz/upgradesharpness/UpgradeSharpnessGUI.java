
package me.hungaz.upgradesharpness;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class UpgradeSharpnessGUI extends JavaPlugin implements Listener {

    private final String GUI_TITLE = "§6➡ Nâng Cấp Sát Thương";
    private final int COST_BLOCKS = 640; // 10 stack khối kim cương
    private final int MAX_LEVEL = 10;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("UpgradeSharpnessGUI enabled for Paper 1.21.4");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("nangcap") && sender instanceof Player) {
            Player player = (Player) sender;
            openUpgradeGUI(player);
            return true;
        }
        return false;
    }

    public void openUpgradeGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(13, null); // Slot chính giữa để đặt kiếm

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;

        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        if (slot != 13) {
            e.setCancelled(true);
            return;
        }

        if (e.getCursor() != null && e.getCurrentItem() == null && e.getCursor().getType().name().endsWith("SWORD")) {
            e.setCancelled(false);
        } else if (e.getCurrentItem() != null && e.getClick().isLeftClick()) {
            e.setCancelled(true);
            ItemStack sword = e.getCurrentItem();
            if (!sword.getType().name().endsWith("SWORD")) return;

            ItemMeta meta = sword.getItemMeta();
            int currentLevel = meta.getEnchantLevel(Enchantment.SHARPNESS);
            if (currentLevel >= MAX_LEVEL) {
                player.sendMessage("§cKiếm đã đạt cấp tối đa!");
                return;
            }

            ItemStack diamondBlock = new ItemStack(Material.DIAMOND_BLOCK);
            if (!player.getInventory().containsAtLeast(diamondBlock, COST_BLOCKS)) {
                player.sendMessage("§cBạn cần 10 stack (640) khối kim cương để nâng cấp!");
                return;
            }

            player.getInventory().removeItemAnySlot(new ItemStack(Material.DIAMOND_BLOCK, COST_BLOCKS));

            boolean success = random.nextBoolean();
            if (success) {
                meta.addEnchant(Enchantment.SHARPNESS, currentLevel + 1, true);
                sword.setItemMeta(meta);
                player.sendMessage("§aNâng cấp thành công! Sắc Bén " + (currentLevel + 1));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            } else {
                player.sendMessage("§cNâng cấp thất bại! Kiếm giữ nguyên cấp " + currentLevel);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.6f);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
