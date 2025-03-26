package com.quantumparkour.listener;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class BlockEventListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<Block, BlockData> savedBlocks = new WeakHashMap<>();
    //private final Map<Block, String[]> signTextCache = new WeakHashMap<>();
    //private final Map<Location, String[]> signCache = new HashMap<>();


    public BlockEventListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        if (isCoralBlock(block.getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block source = event.getBlock();
        Block destination = event.getToBlock();

        if (isWaterlogged(source)) {
            event.setCancelled(true);
            return;
        }

        if (isFluidOrBubbleColumn(source) || isFluidOrBubbleColumn(destination)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        Block block = event.getBlock();
        if (isFluidOrBubbleColumn(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material brokenType = brokenBlock.getType();

        // Handle slab breaking
        if (brokenBlock.getBlockData() instanceof Slab) {
            Slab slab = (Slab) brokenBlock.getBlockData();

            // Only modify behavior if it's a DOUBLE slab
            if (slab.getType() == Slab.Type.DOUBLE) {
                Player nearestPlayer = findNearestPlayer(brokenBlock);

                // Ensure there's a player nearby and they are in CREATIVE mode
                if (nearestPlayer != null && nearestPlayer.getGameMode() == GameMode.CREATIVE) {
                    event.setCancelled(true);

                    RayTraceResult result = nearestPlayer.getWorld().rayTraceBlocks(
                        nearestPlayer.getEyeLocation(),
                        nearestPlayer.getLocation().getDirection(),
                        16.0D
                    );

                    if (result != null) {
                        Vector hitPosition = result.getHitPosition();
                        double blockY = hitPosition.getY() - hitPosition.getBlockY();

                        // Break the top half if the player is looking at the top
                        if (blockY > 0.5D || hitPosition.getBlockY() - brokenBlock.getLocation().getBlockY() == 1) {
                            slab.setType(Slab.Type.BOTTOM);
                        } else {
                            // Break the bottom half if the player is looking at the bottom
                            slab.setType(Slab.Type.TOP);
                        }

                        brokenBlock.setBlockData((BlockData) slab);
                    }
                }
            }
        }

        // Existing logic for proneable blocks
        if (isProneableBlock(brokenType)) {
            return;
        }
        if (brokenBlock.getType() == Material.BUBBLE_COLUMN) {
            event.setCancelled(true);
        }
    
        forEachAdjacentBlock(brokenBlock, nearby -> {
            if (isProneableBlock(nearby.getType())) {
                savedBlocks.put(nearby, nearby.getBlockData());
                nearby.setType(Material.AIR, false);
            }
        });
    
        new BukkitRunnable() {
            @Override
            public void run() {
                savedBlocks.forEach((block, blockData) -> block.setBlockData(blockData, false));
                savedBlocks.clear();
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material placedType = placedBlock.getType();
        Bukkit.getLogger().info("[DEBUG] BlockPlaceEvent: " + placedBlock.getType());

        // Check if the block is a sign type (could be any kind of sign: regular, wall sign, etc.)
        /* if (isSign(placedBlock.getType())) {
            Bukkit.getLogger().info("[DEBUG] Sign placed at: " + placedBlock.getLocation());
            // Do not cache the text here; wait for SignChangeEvent
        } */

        // Existing logic for proneable blocks
        if (isProneableBlock(placedType)) {
            return; // Skip further handling if the placed block is proneable
        }

        if (placedBlock.getType() == Material.BUBBLE_COLUMN) {
            event.setCancelled(true); // Cancel event for Bubble Column
        }

        // Handle adjacent blocks
        forEachAdjacentBlock(placedBlock, nearby -> {
            /* if (isSign(nearby.getType())) {
                Bukkit.getLogger().info("[DEBUG] Restoring text for adjacent sign at: " + nearby.getLocation());
                restoreSignText(nearby); // Restore the cached text for adjacent signs
            } else  */
             if (isProneableBlock(nearby.getType())
                    || nearby.getType() == Material.BUBBLE_COLUMN 
                    || nearby.getType() == Material.WATER 
                    || nearby.getType() == Material.LAVA) {
                savedBlocks.put(nearby, nearby.getBlockData());
                nearby.setType(Material.AIR, false); // Remove the proneable block
            }
        });

        // Restore the saved blocks after the event
        new BukkitRunnable() {
            @Override
            public void run() {
                savedBlocks.forEach((block, blockData) -> block.setBlockData(blockData, false));
                savedBlocks.clear();
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        // Allow pistons to function normally
        if (isRedstoneComponent(type)) {
            return; // Allow pistons to push or retract
        }

        // Prevent updates for proneable blocks
        if (isProneableBlock(type)) {
            event.setCancelled(true);
            return;
        }

        // Prevent updates for fluid or coral blocks
        if (isFluidOrBubbleColumn(block) || isCoralBlock(type)) {
            event.setCancelled(true);
            return;
        }

        // Check adjacent blocks for proneable block interactions
        forEachAdjacentBlock(block, adjacentBlock -> {
            Material adjacentType = adjacentBlock.getType();
            if (isProneableBlock(adjacentType) || isCoralBlock(adjacentType) || isFluidOrBubbleColumn(adjacentBlock)) {
                event.setCancelled(true);
            }
        });
    }
    

    private Player findNearestPlayer(Block block) {
        double closestDistance = 5.0; // Small radius to detect nearby players
        Player nearestPlayer = null;
    
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(block.getLocation()) < closestDistance) {
                nearestPlayer = player;
                closestDistance = player.getLocation().distance(block.getLocation());
            }
        }
    
        return nearestPlayer;
    }

    

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Block interactedBlock = event.getClickedBlock();
        if (interactedBlock == null) return;
    
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && interactedBlock.getType() == Material.DRAGON_EGG && !event.getPlayer().isSneaking()) {
            event.setCancelled(true);
        }
    
        Material interactedType = interactedBlock.getType();
        if (isProneableBlock(interactedType)) {
            return;
        }
        if (interactedBlock.getType() == Material.BUBBLE_COLUMN) {
            event.setCancelled(true);
        }
    
        forEachAdjacentBlock(interactedBlock, nearby -> {
            if (isProneableBlock(nearby.getType())) {
                savedBlocks.put(nearby, nearby.getBlockData());
                nearby.setType(Material.AIR, false);
            }
        });
    
        new BukkitRunnable() {
            @Override
            public void run() {
                savedBlocks.forEach((block, blockData) -> block.setBlockData(blockData, false));
                savedBlocks.clear();
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        EntityType entityType = event.getEntityType();
        if (entityType == EntityType.END_CRYSTAL || entityType == EntityType.TNT_MINECART) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedExplode(BlockExplodeEvent event) {
        Block block = event.getBlock();
        if (isBed(block.getType()) || block.getType() == Material.RESPAWN_ANCHOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedOrAnchorUse(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            Material type = block.getType();
            if (isBed(type) || type == Material.RESPAWN_ANCHOR) {
                Player player = event.getPlayer();

                // Allow shift-right click for placement, prevent explosions otherwise
                if (!player.isSneaking()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isWaterlogged(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Waterlogged) {
            return ((Waterlogged) blockData).isWaterlogged();
        }
        return false;
    }

    private boolean isRegularPiston(Block block) {
        return block.getType() == Material.PISTON;
    }

    private void forEachAdjacentBlock(Block block, java.util.function.Consumer<Block> action) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip the block itself
                    Block adjacentBlock = block.getRelative(x, y, z);
                    action.accept(adjacentBlock);
                }
            }
        }
    }


    private boolean isFluidOrBubbleColumn(Block block) {
        Material type = block.getType();
        return type == Material.WATER || type == Material.LAVA || type == Material.BUBBLE_COLUMN;
    }

    private boolean isBed(Material type) {
        return type == Material.RED_BED 
        || type == Material.BLUE_BED 
        || type == Material.GREEN_BED 
        || type == Material.YELLOW_BED 
        || type == Material.PURPLE_BED 
        || type == Material.BLACK_BED 
        || type == Material.WHITE_BED 
        || type == Material.ORANGE_BED 
        || type == Material.MAGENTA_BED 
        || type == Material.LIGHT_BLUE_BED 
        || type == Material.LIME_BED 
        || type == Material.PINK_BED 
        || type == Material.GRAY_BED 
        || type == Material.LIGHT_GRAY_BED 
        || type == Material.CYAN_BED 
        || type == Material.BROWN_BED;
    }

    private boolean isProneableBlock(Material type) {
        switch (type) {
            case LADDER:
            case VINE:
            case LANTERN:
            case COCOA:
            case COCOA_BEANS:
            case SNOW:
            case BAMBOO:
            case OAK_SIGN:
            case SPRUCE_SIGN:
            case BIRCH_SIGN:
            case JUNGLE_SIGN:
            case ACACIA_SIGN:
            case DARK_OAK_SIGN:
            case CRIMSON_SIGN:
            case WARPED_SIGN:
            case PALE_OAK_SIGN:
            case BELL:
            case TWISTING_VINES:
            case WEEPING_VINES:
            case CAKE:
            case ANVIL:
            case DRAGON_EGG:
            case LILY_PAD:
            case CACTUS:
            case WHITE_CARPET:
            case ORANGE_CARPET:
            case MAGENTA_CARPET:
            case LIGHT_BLUE_CARPET:
            case YELLOW_CARPET:
            case LIME_CARPET:
            case PINK_CARPET:
            case GRAY_CARPET:
            case LIGHT_GRAY_CARPET:
            case CYAN_CARPET:
            case PURPLE_CARPET:
            case BLUE_CARPET:
            case BROWN_CARPET:
            case GREEN_CARPET:
            case RED_CARPET:
            case BLACK_CARPET:
            case NETHER_PORTAL:
            case END_PORTAL:
                return true;
            default:
                return false;
        }
    }

    private boolean isCoralBlock(Material type) {
        switch (type) {
            case TUBE_CORAL_BLOCK:
            case BRAIN_CORAL_BLOCK:
            case BUBBLE_CORAL_BLOCK:
            case FIRE_CORAL_BLOCK:
            case HORN_CORAL_BLOCK:
            case TUBE_CORAL:
            case BRAIN_CORAL:
            case BUBBLE_CORAL:
            case FIRE_CORAL:
            case HORN_CORAL:
            case TUBE_CORAL_FAN:
            case BRAIN_CORAL_FAN:
            case BUBBLE_CORAL_FAN:
            case FIRE_CORAL_FAN:
            case HORN_CORAL_FAN:
            case TUBE_CORAL_WALL_FAN:
            case BRAIN_CORAL_WALL_FAN:
            case BUBBLE_CORAL_WALL_FAN:
            case FIRE_CORAL_WALL_FAN:
            case HORN_CORAL_WALL_FAN:
                return true;
            default:
                return false;
        }
    }

    private boolean isRedstoneComponent(Material type) {
        switch (type) {
            case REDSTONE_WIRE:
            case REDSTONE_TORCH:
            case REDSTONE_BLOCK:
            case LEVER:
            case STONE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
            case BIRCH_BUTTON:
            case JUNGLE_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case CRIMSON_BUTTON:
            case WARPED_BUTTON:
            case PALE_OAK_BUTTON:
            case REPEATER:
            case IRON_TRAPDOOR:
            case COMPARATOR:
            case PISTON:
            case STICKY_PISTON:
            case OBSERVER:
            case DISPENSER:
            case DROPPER:
            case NOTE_BLOCK:
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
                return true;
            default:
                return false;
        }
    }


}