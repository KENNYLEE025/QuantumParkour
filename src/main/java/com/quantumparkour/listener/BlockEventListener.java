package com.quantumparkour.listener;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
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
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block adjacentBlock = block.getRelative(x, y, z);
                    Material adjacentType = adjacentBlock.getType();
                    if (isProneableBlock(adjacentType) || isCoralBlock(adjacentType) || isFluidOrBubbleColumn(adjacentBlock)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
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
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block nearby = placedBlock.getRelative(x, y, z);
                    if (isProneableBlock(nearby.getType()) 
                    || nearby.getType() == Material.BUBBLE_COLUMN 
                    || nearby.getType() == Material.WATER 
                    || nearby.getType() == Material.LAVA) { 
                        savedBlocks.put(nearby, nearby.getBlockData());
                        nearby.setType(Material.AIR, false);
                    }
                }
            }
        }
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
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block nearby = brokenBlock.getRelative(x, y, z);
                    if (isProneableBlock(nearby.getType())) {
                        savedBlocks.put(nearby, nearby.getBlockData());
                        nearby.setType(Material.AIR, false);
                    }
                }
            }
        }
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
        Material interactedType = interactedBlock.getType();
        if (isProneableBlock(interactedType)) {
            return;
        }
        if (interactedBlock.getType() == Material.BUBBLE_COLUMN) {
            event.setCancelled(true);
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block nearby = interactedBlock.getRelative(x, y, z);
                    if (isProneableBlock(nearby.getType())) {
                        savedBlocks.put(nearby, nearby.getBlockData());
                        nearby.setType(Material.AIR, false);
                    }
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                savedBlocks.forEach((block, blockData) -> block.setBlockData(blockData, false));
                savedBlocks.clear();
            }
        }.runTaskLater(plugin, 1L);
    }

    private boolean isFluidOrBubbleColumn(Block block) {
        Material type = block.getType();
        return type == Material.WATER || type == Material.LAVA || type == Material.BUBBLE_COLUMN;
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