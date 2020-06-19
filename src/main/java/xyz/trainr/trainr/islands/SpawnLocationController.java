package xyz.trainr.trainr.islands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.trainr.trainr.Trainr;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of the spawn locations of every player
 *
 * @author Cerus
 * @version 1.0.0
 * @since 1.0.0
 */
public class SpawnLocationController {

    // Define local variables
    private int currentIndex = 0;
    private final int xOffset;
    private final int zOffset;
    private final Location baseSpawnLocation;
    private final Map<UUID, Integer> playerIndexes = new ConcurrentHashMap<>();

    /**
     * Creates a new spawn location controller
     */
    public SpawnLocationController() {
        Configuration config = JavaPlugin.getPlugin(Trainr.class).getConfig();

        this.baseSpawnLocation = (Location) config.get("spawn.location");
        this.xOffset = config.getInt("spawn.offset.x");
        this.zOffset = config.getInt("spawn.offset.z");
    }

    /**
     * Assigns an island to a joining player
     *
     * @param player The joining player
     */
    public void handleJoin(Player player) {
        // Teleport the player to his reserved island
        player.teleport(getNextSpawnLocation());
        playerIndexes.put(player.getUniqueId(), currentIndex);
        changeId();

        // Provide the player with some  building material
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setItem(0, new ItemStack(Material.SANDSTONE, 64));

        // Set the player's game mode
        player.setGameMode(GameMode.SURVIVAL);

        // Send a welcome message to the player
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("");
        player.sendMessage("§r      §6§lWelcome to the §e§lTrainr §6§lserver!");
        player.sendMessage("§r      §7You were teleported to island §f#" + playerIndexes.get(player.getUniqueId()));
        player.sendMessage("");
        player.sendMessage("§8§m-----------------------------------------------------");
    }

    /**
     * Frees the island of a quitting player
     *
     * @param player The quitting player
     */
    public void handleLeave(Player player) {
        playerIndexes.remove(player.getUniqueId());
        changeId();
    }

    /**
     * Changes the current island ID
     */
    private void changeId() {
        int newIndex = 0;
        while (playerIndexes.containsValue(newIndex)) {
            newIndex++;
        }
        currentIndex = newIndex;
    }

    /**
     * @return The next free spawn location
     */
    private Location getNextSpawnLocation() {
        Location baseSpawnLocation = getBaseSpawnLocation().clone();
        baseSpawnLocation.add(xOffset * currentIndex, 0, zOffset * currentIndex);
        return baseSpawnLocation;
    }

    /**
     * Retrieves the island location of a player
     *
     * @param player The player
     * @return The optional location of the players island
     */
    public Optional<Location> getIslandLocation(Player player) {
        // Check if the player has an island
        if (!playerIndexes.containsKey(player.getUniqueId())) {
            return Optional.empty();
        }

        // Calculate the island location and return it
        int index = playerIndexes.get(player.getUniqueId());
        Location baseSpawnLocation = getBaseSpawnLocation().clone();
        baseSpawnLocation.add(xOffset * index, 0, zOffset * index);
        return Optional.of(baseSpawnLocation);
    }

    /**
     * @return The height on which a player dies
     */
    public int getDeathHeight() {
        return this.baseSpawnLocation.getBlockY() - 10;
    }

    /**
     * @return The current island ID
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * @return The current x-offset
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * @return The current z-offset
     */
    public int getZOffset() {
        return zOffset;
    }

    /**
     * @return The current base spawn location
     */
    public Location getBaseSpawnLocation() {
        return baseSpawnLocation;
    }

    /**
     * Respawns the given player
     *
     * @param player The player to respawn
     */
    public void respawn(Player player) {
        // Retrieve the location of the players island
        Optional<Location> location = getIslandLocation(player);
        if (!location.isPresent()) {
            player.kickPlayer("Invalid island location");
            return;
        }

        // Teleport the player to his island
        player.teleport(location.get());
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.SANDSTONE, 64));
    }

}
