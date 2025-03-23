package com.quantumparkour.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    // To-Do: Implement the following event handlers
    // - BlockBreakEvent
    // - BlockPhysicsEvent
    // - SignChangeEvent
    // - BlockPlaceEvent

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material brokenType = brokenBlock.getType();

        
        /* // Handle adjacent signs
        forEachAdjacentBlock(brokenBlock, adjacentBlock -> {
            if (isSign(adjacentBlock.getType())) {
                restoreSignText(adjacentBlock);
            }
        }); */
    
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

        if (isWaterlogged(block)) {
            event.setCancelled(true);
            return;
        }

        // Step 1: Allow Signs to Restore Their Text Separately
        /* if (isSign(type)) {
            event.setCancelled(true); // Prevent sign updates
            restoreSignText(block);
            return;
        } */

        // Step 2: Cancel Physics Updates for Proneable Blocks
        if (isProneableBlock(type) || isFluidOrBubbleColumn(block) || isCoralBlock(type)) {
            event.setCancelled(true);
            return;
        }

        // Step 3: Check Adjacent Blocks for Proneable Block Interactions
        forEachAdjacentBlock(block, adjacentBlock -> {
            Material adjacentType = adjacentBlock.getType();
            if (isProneableBlock(adjacentType) || isCoralBlock(adjacentType) || isFluidOrBubbleColumn(adjacentBlock)) {
                event.setCancelled(true);
            }
        });
    }

    /* @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();

        // Cache the sign's text when it is changed
        if (isSign(block.getType())) {
            String[] lines = event.getLines();
            signTextCache.put(block, lines.clone()); // Cache the text
            cacheSignText(block); // Update the cache

            Bukkit.getLogger().info("[DEBUG] Sign text cached at: " + block.getLocation());
            for (int i = 0; i < lines.length; i++) {
                Bukkit.getLogger().info("    Line " + i + ": " + lines[i]);
            }
        }
    } */

    /* private void restoreSignText(Block signBlock) {
        if (!(signBlock.getState() instanceof Sign)) return;

        Sign sign = (Sign) signBlock.getState();
        Location loc = signBlock.getLocation();

        // Debug: Check if the cache contains the sign
        if (!signCache.containsKey(loc)) {
            Bukkit.getLogger().info("[DEBUG] No cached text found for sign at: " + loc);
            return;
        }

        Bukkit.getLogger().info("[DEBUG] Restoring text for sign at: " + loc);
        String[] cachedLines = signCache.get(loc);

        for (int i = 0; i < cachedLines.length; i++) {
            sign.setLine(i, cachedLines[i]);
            Bukkit.getLogger().info("[DEBUG]     Restored Line " + i + ": " + cachedLines[i]);
        }

        sign.update();
    } */
    
    /* private void cacheSignText(Block signBlock) {
        if (!(signBlock.getState() instanceof Sign)) return;
    
        Sign sign = (Sign) signBlock.getState();
        Location loc = signBlock.getLocation();
    
        // Debug: Check if the cache already had this sign
        if (signCache.containsKey(loc)) {
            Bukkit.getLogger().info("[DEBUG] Updating cached text for sign at: " + loc);
        } else {
            Bukkit.getLogger().info("[DEBUG] Sign text cached at: " + loc);
        }
    
        // Store the sign text
        String[] lines = sign.getLines();
        signCache.put(loc, lines);
    
        for (int i = 0; i < lines.length; i++) {
            Bukkit.getLogger().info("[DEBUG]     Line " + i + ": " + lines[i]);
        }
    } */

    // End of To-Do

    private boolean isWaterlogged(Block block) {
    BlockData blockData = block.getBlockData();
    if (blockData instanceof Waterlogged) {
        return ((Waterlogged) blockData).isWaterlogged();
    }
    return false;
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
            /* case OAK_HANGING_SIGN:
            case SPRUCE_HANGING_SIGN:
            case BIRCH_HANGING_SIGN:
            case JUNGLE_HANGING_SIGN:
            case ACACIA_HANGING_SIGN:
            case DARK_OAK_HANGING_SIGN:
            case CRIMSON_HANGING_SIGN:
            case WARPED_HANGING_SIGN:
            case PALE_OAK_HANGING_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case CRIMSON_WALL_SIGN:
            case WARPED_WALL_SIGN:
            case PALE_OAK_WALL_SIGN:
            case CHERRY_SIGN:
            case CHERRY_WALL_SIGN:
            case CHERRY_HANGING_SIGN: */
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

    /* private boolean isSign(Material type)
    {
        switch (type) {
            case OAK_SIGN:
            case SPRUCE_SIGN:
            case BIRCH_SIGN:
            case JUNGLE_SIGN:
            case ACACIA_SIGN:
            case DARK_OAK_SIGN:
            case CRIMSON_SIGN:
            case WARPED_SIGN:
            case PALE_OAK_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case CRIMSON_WALL_SIGN:
            case WARPED_WALL_SIGN:
            case PALE_OAK_WALL_SIGN:
            case CHERRY_SIGN:
            case CHERRY_WALL_SIGN:
            case CHERRY_HANGING_SIGN:
                return true;
            default:
                return false;
        }
    }
        */
}