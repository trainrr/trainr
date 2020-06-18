package xyz.trainr.trainr.islands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnLocationController {

    private int currentIndex = 0;

    private final int xOffset;
    private final int zOffset;
    private final Location baseSpawnLocation;

    private final Map<UUID, Integer> playerIndexes = new HashMap<>();

    public SpawnLocationController(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        this.baseSpawnLocation = (Location) config.get("spawn.location");
        this.xOffset = config.getInt("spawn.offset.x");
        this.zOffset = config.getInt("spawn.offset.z");
    }

    public void handleJoin(Player player) {
        player.teleport(getNextSpawnLocation());
        playerIndexes.put(player.getUniqueId(), currentIndex);
        changeId();

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(0, new ItemStack(Material.SANDSTONE, 64));

        player.sendMessage("§8§m-------------------------------------------------------");
        player.sendMessage(" ");
        player.sendMessage("§r      §6§lWelcome to the §e§lTrainr §6§lserver!");
        player.sendMessage(" ");
        player.sendMessage("§8§m-------------------------------------------------------");
    }

    public void handleLeave(Player player) {
        playerIndexes.remove(player.getUniqueId());
        changeId();
    }

    private void changeId() {
        int newIndex = 0;
        while (playerIndexes.containsValue(newIndex)) {
            newIndex++;
        }
        currentIndex = newIndex;
    }

    private Location getNextSpawnLocation() {
        Location baseSpawnLocation = getBaseSpawnLocation().clone();
        baseSpawnLocation.add(xOffset * currentIndex, 0, zOffset * currentIndex);
        return baseSpawnLocation;
    }

    public int getDeathHeight() {
        return this.baseSpawnLocation.getBlockY() - 10;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getxOffset() {
        return xOffset;
    }

    public int getzOffset() {
        return zOffset;
    }

    public Location getBaseSpawnLocation() {
        return baseSpawnLocation;
    }

}