package com.quantumparkour.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.EnumSet;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Predicate;

//----------------------------------------------------------------------------------------------------------------------
public class BlockEventListener implements Listener
{
    //---------------------------------------------------------------------------------------------
    private final JavaPlugin                        m_plugin;
    private final Map<Category, EnumSet<Material>>  m_categories            = new EnumMap<>(Category.class);
    private final Map<Category, EnumSet<Material>>  m_fallbackCategories    = new EnumMap<>(Category.class);

    //---------------------------------------------------------------------------------------------
    private static final EnumSet<Category> UNSTABLE_CATEGORIES = EnumSet.of(
            Category.CORAL,
            Category.BED,
            Category.FLUIDS,
            Category.FRAGILE,
            Category.GRAVITY,
            Category.REDSTONE
    );

    public enum Category
    {
        FLUIDS,
        FRAGILE,
        REDSTONE,
        GRAVITY,
        CORAL,
        BED
    }

    private static final BlockFace[]                ADJACENT_BLOCKS = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

    //---------------------------------------------------------------------------------------------
    public BlockEventListener(JavaPlugin plugin)
    {
        this.m_plugin = plugin;
        initializeFallbackCategories();
        reloadConfig();
    }

    //---------------------------------------------------------------------------------------------
    private void loadCategories()
    {
        var section = m_plugin.getConfig().getConfigurationSection("physics.categories");
        if (section == null)
        {
            m_plugin.getLogger().warning("Missing 'physics.categories' section in config. Using fallback categories.");
            return;
        }

        for (String key : section.getKeys(false))
        {
            Category category;
            try
            {
                category = Category.valueOf(key.toUpperCase());
            }
            catch (IllegalArgumentException exception)
            {
                m_plugin.getLogger().warning("Unknown category '" + key + "' in config.");
                continue;
            }

            EnumSet<Material> set = EnumSet.noneOf(Material.class);

            for (String entry : section.getStringList(key))
            {
                if (entry.contains("*"))
                {
                    addWildcardMaterials(set, entry);
                    continue;
                }

                try
                {
                    set.add(Material.valueOf(entry.toUpperCase()));
                }
                catch (IllegalArgumentException exception)
                {
                    m_plugin.getLogger().warning("Invalid material '" + entry + "' in category '" + key + "'");
                }
            }

            m_categories.put(category, set);
        }
    }

    //---------------------------------------------------------------------------------------------
    public void initializeFallbackCategories()
    {
        m_fallbackCategories.clear();

        m_fallbackCategories.put(Category.FLUIDS, EnumSet.of(
                Material.WATER,
                Material.LAVA,
                Material.BUBBLE_COLUMN
        ));

        m_fallbackCategories.put(Category.BED, collectMaterials(mat ->
                mat.name().endsWith("_BED")));

        m_fallbackCategories.put(Category.CORAL, collectMaterials(mat ->
                mat.name().contains("CORAL")));

        m_fallbackCategories.put(Category.GRAVITY, collectMaterials(Material::hasGravity));

        m_fallbackCategories.put(Category.REDSTONE, collectMaterials(mat ->
                mat.name().contains("REDSTONE")
                        || mat.name().endsWith("_BUTTON")
                        || mat == Material.LEVER
                        || mat == Material.REPEATER
                        || mat == Material.COMPARATOR
                        || mat == Material.OBSERVER
                        || mat == Material.PISTON
                        || mat == Material.STICKY_PISTON
                        || mat == Material.DISPENSER
                        || mat == Material.DROPPER
                        || mat == Material.NOTE_BLOCK
                        || mat == Material.POWERED_RAIL
                        || mat == Material.ACTIVATOR_RAIL
                        || mat == Material.DETECTOR_RAIL
                        || mat == Material.IRON_TRAPDOOR
                        || mat == Material.TRIPWIRE
                        || mat == Material.TRIPWIRE_HOOK
                        || mat == Material.DAYLIGHT_DETECTOR
                        || mat == Material.TARGET));

        m_fallbackCategories.put(Category.FRAGILE, collectMaterials(mat ->
                mat.name().endsWith("_SIGN")
                        || mat.name().endsWith("_WALL_SIGN")
                        || mat == Material.TORCH
                        || mat == Material.WALL_TORCH
                        || mat == Material.SOUL_TORCH
                        || mat == Material.SOUL_WALL_TORCH
                        || mat == Material.REDSTONE_TORCH
                        || mat == Material.REDSTONE_WALL_TORCH
                        || mat == Material.LADDER
                        || mat == Material.SCAFFOLDING
                        || mat == Material.VINE
                        || mat == Material.LANTERN
                        || mat == Material.CAKE
                        || mat == Material.LILY_PAD
                        || mat == Material.CACTUS
                        || mat == Material.SNOW
                        || mat == Material.BAMBOO
                        || mat == Material.BELL
                        || mat == Material.TWISTING_VINES
                        || mat == Material.WEEPING_VINES
                        || mat == Material.COCOA
                        || mat == Material.ANVIL
                        || mat == Material.DRAGON_EGG
                        || mat == Material.NETHER_PORTAL
                        || mat == Material.END_PORTAL));
    }

    //---------------------------------------------------------------------------------------------
    private EnumSet<Material> collectMaterials(java.util.function.Predicate<Material> filter)
    {
        EnumSet<Material> set = EnumSet.noneOf(Material.class);
        for (Material material : Material.values())
        {
            if (filter.test(material))
            {
                set.add(material);
            }
        }
        return set;
    }

    //---------------------------------------------------------------------------------------------
    public void reloadConfig()
    {
        m_plugin.reloadConfig();
        loadCategories();
    }

    //---------------------------------------------------------------------------------------------
    private void addWildcardMaterials(EnumSet<Material> enumSet, String pattern)
    {
        String regex = pattern.toUpperCase().replace("*", ".*");
        for (Material material : Material.values())
        {
            if (material.name().matches(regex))
            {
                enumSet.add(material);
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onBlockFade(BlockFadeEvent event)
    {
        Block block = event.getBlock();
        Material type = block.getType();
        if (isCoralBlock(type))
        {
            event.setCancelled(true);
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        Block block = event.getBlock();
        Material type = block.getType();

        // Early outs
        if (type.isAir()) return;

        // Early out for redstone components
        if (isRedstoneComponent(type)) return;

        if (hasBlockedPhysicsNeighbor(block))
        {
            event.setCancelled(true);
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler   //(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        Material type = block.getType();
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Slab slab && slab.getType() == Slab.Type.DOUBLE)
        {
            double playerRadius = 5.0;
            Player player = findNearbyCreativePlayer(block, playerRadius);
            if (player != null)
            {
                event.setCancelled(true);
                doubleSlabBreakEvent(block, player);
                return;
            }
        }

        if (isFragileBlock(type))     return;

        if (type == Material.BUBBLE_COLUMN)
        {
            event.setCancelled(true);
            return;
        }
        tempRemoveAdjacentBlocks(block, this::isUnstableBlock);
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onFallingBlockLand(EntityChangeBlockEvent event)
    {

    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlockPlaced();
        Material type = block.getType();

        if (isFragileBlock(type))   return;

        if (type == Material.BUBBLE_COLUMN)
        {
            event.setCancelled(true);
            return;
        }

        tempRemoveAdjacentBlocks(block, this::isUnstableBlock);
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        Block block = event.getBlock();
        Material type = block.getType();
        if (isFluid(type))
        {
            event.setCancelled(true);
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event)
    {
        Block block = event.getBlock();
        Material type = block.getType();
        if (isFluid(type))
        {
            event.setCancelled(true);
        }
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onBedOrAnchorUse(PlayerInteractEvent event)
    {
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (block == null) return;

        Material type = block.getType();
        Player player = event.getPlayer();

        if (isBed(type) && player.getWorld().getEnvironment() != World.Environment.NORMAL)
        {
            event.setCancelled(true);
        }
        if (type == Material.RESPAWN_ANCHOR && player.getWorld().getEnvironment() == World.Environment.NORMAL)
        {
            event.setCancelled(true);
        }
    }

    //---------------------------------------------------------------------------------------------
    // Helper functions
    private boolean hasBlockedPhysicsNeighbor(Block block)
    {
        Material type = block.getType();

        for (BlockFace blockFace : ADJACENT_BLOCKS)
        {
            Block adjacentBlock = block.getRelative(blockFace);
            Material adjacentType = adjacentBlock.getType();
            if (!isUnstableBlock(adjacentBlock))
            {
                continue;
            }

            if (isRedstoneComponent(adjacentType))
            {
                continue;
            }

            if (isGravityBlock(type) && isGravityBlock(adjacentType))
            {
                continue;
            }

            return true;
        }
        return false;
    }

    //---------------------------------------------------------------------------------------------
    private Player findNearbyCreativePlayer(Block block, double radius)
    {
        Player nearest = null;
        double closestDistance = radius;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() != GameMode.CREATIVE) continue;

            double distance = player.getLocation().distance(block.getLocation());
            if (distance < closestDistance)
            {
                nearest = player;
                closestDistance = distance;
            }
        }
        return nearest;
    }

    //---------------------------------------------------------------------------------------------
    private void doubleSlabBreakEvent(Block block, Player player)
    {
        BlockData data = block.getBlockData();
        if (!(data instanceof Slab slab) || slab.getType() != Slab.Type.DOUBLE)
        {
            return;
        }

        Slab.Type newSlabType = getHitSlabHalf(player, block);

        if (newSlabType == null) return;

        slab.setType(newSlabType);
        block.setBlockData(slab, false);
    }

    //---------------------------------------------------------------------------------------------
    private Slab.Type getHitSlabHalf(Player player, Block slabBlock)
    {
        double maxDistance = 16.0;
        RayTraceResult result = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getLocation().getDirection(),
                maxDistance
        );

        if (result == null) return null;

        Vector hitPosition = result.getHitPosition();
        double offsetY = hitPosition.getY() - slabBlock.getY();

        if (offsetY > 0.5 || hitPosition.getBlockY() - slabBlock.getY() == 1)
        {
            return Slab.Type.BOTTOM;
        }

        return Slab.Type.TOP;
    }

    //---------------------------------------------------------------------------------------------
    private void tempRemoveAdjacentBlocks(Block center, Predicate<Block> filter)
    {
        Map<Block, BlockData> savedBlocks = new HashMap<>();
        for (BlockFace adjacentFace : ADJACENT_BLOCKS)
        {
            Block adjacentBlock = center.getRelative(adjacentFace);
            if (filter.test(adjacentBlock))
            {
                savedBlocks.put(adjacentBlock, adjacentBlock.getBlockData());
                adjacentBlock.setType(Material.AIR, false);
            }
        }
        restoreSavedBlocks(savedBlocks);
    }

    //---------------------------------------------------------------------------------------------
    private void restoreSavedBlocks(Map<Block, BlockData> savedBlocks) {
        if (savedBlocks.isEmpty()) return;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                savedBlocks.forEach((block, data) -> block.setBlockData(data, false));
            }
        }.runTaskLater(m_plugin, 1L);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isInCategory(Category category, Material type)
    {
        EnumSet<Material> configuredSet = m_categories.get(category);
        if (configuredSet != null && configuredSet.contains(type))
        {
            return true;
        }
        EnumSet<Material> fallbackSet = m_fallbackCategories.get(category);
        return fallbackSet != null && fallbackSet.contains(type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isCoralBlock(Material type)
    {
        return isInCategory(Category.CORAL, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isBed(Material type)
    {
        return isInCategory(Category.BED, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isFluid(Material type)
    {
        return isInCategory(Category.FLUIDS, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isRedstoneComponent(Material type)
    {
        return isInCategory(Category.REDSTONE, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isFragileBlock(Material type)
    {
        return isInCategory(Category.FRAGILE, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isGravityBlock(Material type)
    {
        return isInCategory(Category.GRAVITY, type);
    }

    //---------------------------------------------------------------------------------------------
    private boolean isUnstableBlock(Block block)
    {
        Material type = block.getType();
        for (Category category : UNSTABLE_CATEGORIES)
        {
            if (isInCategory(category, type))
            {
                return true;
            }
        }
        return false;
    }
}