package com.quantumparkour.listener;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();

        if (isRedstoneComponent(type)) return;

        if (isFluidOrBubbleColumn(block) || isProneableBlock(type) || isCoralBlock(type)) {
            event.setCancelled(true);
            return;
        }

        forEachAdjacentBlock(block, adjacentBlock -> {
            Material adjacentType = adjacentBlock.getType();
            if (isProneableBlock(adjacentType) || isCoralBlock(adjacentType) || isFluidOrBubbleColumn(adjacentBlock)) {
                event.setCancelled(true);
            }
        });
    }
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block source = event.getBlock();
        Block destination = event.getToBlock();
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
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlock();
        Material placedType = placedBlock.getType();
    
        if (isProneableBlock(placedType)) {
            return;
        }
        if (placedBlock.getType() == Material.BUBBLE_COLUMN) {
            event.setCancelled(true);
        }
    
        forEachAdjacentBlock(placedBlock, nearby -> {
            if (isProneableBlock(nearby.getType()) 
                || nearby.getType() == Material.BUBBLE_COLUMN 
                || nearby.getType() == Material.WATER 
                || nearby.getType() == Material.LAVA) {
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
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material brokenType = brokenBlock.getType();

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
            case OAK_HANGING_SIGN:
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
            case CHERRY_HANGING_SIGN:
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