package com.iafenvoy.iceandfire.world.structure;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafStructurePieces;
import com.iafenvoy.iceandfire.registry.IafStructureTypes;
import com.iafenvoy.iceandfire.registry.tag.IafBlockTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructureContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.StructureType;
import com.iafenvoy.iceandfire.item.block.GoldPileBlock;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.Direction;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NetherDragonCaveStructure extends DragonCaveStructure {
    public static final MapCodec<NetherDragonCaveStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(configCodecBuilder(instance)).apply(instance, NetherDragonCaveStructure::new));

    protected NetherDragonCaveStructure(Config config) {
        super(config);
    }

    @Override
    protected DragonCavePiece createPiece(BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
        return new NetherDragonCavePiece(0, boundingBox, male, offset, y, seed);
    }

    @Override
    protected double getGenerateChance() {
        return IafCommonConfig.INSTANCE.worldGen.generateFireDragonCaveChance.getValue(); // Use fire dragon chance as base
    }

    @Override
    public StructureType<?> getType() {
        return IafStructureTypes.NETHER_DRAGON_CAVE.get();
    }

    public static class NetherDragonCavePiece extends DragonCavePiece {
        public static final Identifier NETHER_DRAGON_CHEST = Identifier.of(IceAndFire.MOD_ID, "chest/nether_dragon_cave");
        public static final Identifier NETHER_DRAGON_CHEST_MALE = Identifier.of(IceAndFire.MOD_ID, "chest/nether_dragon_male_cave");

        protected NetherDragonCavePiece(int length, BlockBox boundingBox, boolean male, BlockPos offset, int y, long seed) {
            super(IafStructurePieces.NETHER_DRAGON_CAVE.get(), length, boundingBox, male, offset, y, seed);
        }

        public NetherDragonCavePiece(StructureContext context, NbtCompound nbt) {
            super(IafStructurePieces.NETHER_DRAGON_CAVE.get(), nbt);
        }

        @Override
        protected TagKey<Block> getOreTag() {
            return IafBlockTags.NETHER_DRAGON_CAVE_ORES;
        }

        @Override
        protected WorldGenCaveStalactites getCeilingDecoration() {
            return new WorldGenCaveStalactites(Blocks.BLACKSTONE, 4);
        }

        @Override
        protected BlockState getTreasurePile() {
            return IafBlocks.GOLD_PILE.get().getDefaultState();
        }

        @Override
        protected BlockState getPaletteBlock(Random random) {
            // Use nether-themed blocks for cave palette with soul soil for soul fire generation
            double chance = random.nextDouble();
            if (chance < 0.4) {
                return Blocks.BLACKSTONE.getDefaultState();
            } else if (chance < 0.7) {
                return Blocks.BASALT.getDefaultState();
            } else if (chance < 0.9) {
                return Blocks.SOUL_SOIL.getDefaultState(); // Soul soil for soul fire generation
            } else {
                return Blocks.NETHERRACK.getDefaultState();
            }
        }

        @Override
        public void createShell(WorldAccess worldIn, Random rand, Set<BlockPos> positions) {
            List<Block> rareOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_RARE_ORES);
            List<Block> uncommonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_UNCOMMON_ORES);
            List<Block> commonOres = this.getBlockList(IafBlockTags.DRAGON_CAVE_COMMON_ORES);
            List<Block> dragonTypeOres = this.getBlockList(this.getOreTag());
            positions.forEach(blockPos -> {
                if (!(worldIn.getBlockState(blockPos).getBlock() instanceof BlockWithEntity) && worldIn.getBlockState(blockPos).getHardness(worldIn, blockPos) >= 0) {
                    boolean doOres = rand.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateOreRatio.getValue();
                    if (doOres) {
                        Block toPlace = null;
                        if (rand.nextBoolean())
                            toPlace = !dragonTypeOres.isEmpty() ? dragonTypeOres.get(rand.nextInt(dragonTypeOres.size())) : null;
                        else {
                            double chance = rand.nextDouble();
                            if (!rareOres.isEmpty() && chance <= 0.15)
                                toPlace = rareOres.get(rand.nextInt(rareOres.size()));
                            else if (!uncommonOres.isEmpty() && chance <= 0.45)
                                toPlace = uncommonOres.get(rand.nextInt(uncommonOres.size()));
                            else if (!commonOres.isEmpty())
                                toPlace = commonOres.get(rand.nextInt(commonOres.size()));
                        }
                        if (toPlace != null)
                            worldIn.setBlockState(blockPos, toPlace.getDefaultState(), Block.NOTIFY_LISTENERS);
                        else
                            worldIn.setBlockState(blockPos, this.getPaletteBlock(rand), Block.NOTIFY_LISTENERS);
                    } else
                        worldIn.setBlockState(blockPos, this.getPaletteBlock(rand), Block.NOTIFY_LISTENERS);
                }
            });
        }

        // Copy the private getBlockList method from parent class
        private List<Block> getBlockList(final TagKey<Block> tagKey) {
            return Registries.BLOCK.getEntryList(tagKey).map(holders -> holders.stream().map(RegistryEntry::value).toList()).orElse(Collections.emptyList());
        }

        @Override
        protected RegistryKey<LootTable> getChestTable(boolean male) {
            return RegistryKey.of(RegistryKeys.LOOT_TABLE, male ? NETHER_DRAGON_CHEST_MALE : NETHER_DRAGON_CHEST);
        }

        @Override
        protected EntityType<? extends DragonBaseEntity> getDragonType() {
            return IafEntities.NETHER_DRAGON.get();
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            // Override generate to add nether-specific positioning and decorations
            super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
            
            // Clear terrain around the cave to prevent it from being covered
            this.clearTerrainAroundCave(world, pivot, random, 20);
            
            // Add nether-specific decorations after the base cave generation
            this.addNetherDecorations(world, pivot, random);
        }

        @Override
        public void decorateCave(WorldAccess worldIn, Random random, Set<BlockPos> positions, List<SphereInfo> spheres, BlockPos center) {
            // Generate ceiling decorations
            for (SphereInfo sphere : spheres) {
                BlockPos pos = sphere.pos();
                int radius = sphere.radius();
                for (int i = 0; i < 15 + random.nextInt(10); i++)
                    this.getCeilingDecoration().generate(worldIn, random, pos.up(radius / 2 - 1).add(random.nextInt(radius) - radius / 2, 0, random.nextInt(radius) - radius / 2));
            }

            // Generate treasure piles and chests - override the condition for nether blocks
            positions.forEach(blockPos -> {
                if (blockPos.getY() < center.getY()) {
                    BlockState stateBelow = worldIn.getBlockState(blockPos.down());
                    // Check for nether blocks instead of overworld stone
                    if ((stateBelow.isOf(Blocks.BLACKSTONE) || stateBelow.isOf(Blocks.BASALT) || 
                         stateBelow.isOf(Blocks.NETHERRACK) || stateBelow.isOf(Blocks.SOUL_SOIL) ||
                         stateBelow.isIn(BlockTags.BASE_STONE_OVERWORLD) || stateBelow.isIn(IafBlockTags.DRAGON_ENVIRONMENT_BLOCKS)) 
                        && worldIn.getBlockState(blockPos).isAir()) {
                        this.setGoldPile(worldIn, blockPos, random);
                    }
                }
            });
            
            // Add nether-specific decorations
            this.addNetherDecorations(worldIn, center, random);
        }

        @Override
        public void setGoldPile(WorldAccess world, BlockPos pos, Random random) {
            if (!(world.getBlockState(pos).getBlock() instanceof BlockWithEntity)) {
                int chance = random.nextInt(99) + 1;
                // Much reduced spawn rates - only 15% chance for gold, 0.1% chance for chest
                if (chance < 15) {
                    boolean isMale = this.getIsMale();
                    boolean generateGold = random.nextDouble() < IafCommonConfig.INSTANCE.dragon.generateDenGoldChance.getValue() * (isMale ? 1 : 2);
                    world.setBlockState(pos, generateGold ? this.getTreasurePile().with(GoldPileBlock.LAYERS, 1 + random.nextInt(7)) : Blocks.AIR.getDefaultState(), 3);
                } else if (chance == 99 && random.nextInt(10) == 0) { // Only 0.1% chance for chests (extremely rare)
                    world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.Type.HORIZONTAL.random(random)), Block.NOTIFY_LISTENERS);
                    if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(pos);
                        if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                            // Try a different approach - use the loot table directly
                            RegistryKey<LootTable> lootTable = this.getChestTable(this.getIsMale());
                            chestBlockEntity.setLootTable(lootTable, random.nextLong());
                        }
                    }
                }
            }
        }

        private boolean getIsMale() {
            try {
                java.lang.reflect.Field isMaleField = this.getClass().getSuperclass().getDeclaredField("male");
                isMaleField.setAccessible(true);
                return isMaleField.getBoolean(this);
            } catch (Exception e) {
                return false; // Fallback
            }
        }

        private void addNetherDecorations(WorldAccess world, BlockPos origin, Random random) {
            // Add soul fire decorations - more frequent on soul soil
            if (random.nextInt(2000) == 0) {
                BlockPos firePos = origin.add(random.nextInt(16) - 8, random.nextInt(8) - 4, random.nextInt(16) - 8);
                BlockState stateBelow = world.getBlockState(firePos.down());
                if (world.isAir(firePos) && (stateBelow.isOf(Blocks.SOUL_SOIL) || stateBelow.isOf(Blocks.NETHERRACK))) {
                    world.setBlockState(firePos, Blocks.SOUL_FIRE.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
            
            // Add obsidian piles - much reduced frequency
            if (random.nextInt(8000) == 0) {
                BlockPos obsidianPos = origin.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
                this.generateObsidianPile(world, random, obsidianPos);
            }
            
            // Add netherrack piles - much reduced frequency
            if (random.nextInt(10000) == 0) {
                BlockPos netherrackPos = origin.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
                this.generateNetherrackPile(world, random, netherrackPos);
            }
            
            // Add basalt formations - much reduced frequency
            if (random.nextInt(15000) == 0) {
                BlockPos basaltPos = origin.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
                this.generateBasaltFormation(world, random, basaltPos);
            }
        }

        private void generateObsidianPile(WorldAccess world, Random random, BlockPos pos) {
            int height = 2 + random.nextInt(3);
            for (int y = 0; y < height; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (random.nextFloat() < 0.7f) {
                            BlockPos pilePos = pos.add(x, y, z);
                            if (world.isAir(pilePos) || world.getBlockState(pilePos).getHardness(world, pilePos) >= 0) {
                                world.setBlockState(pilePos, Blocks.OBSIDIAN.getDefaultState(), Block.NOTIFY_LISTENERS);
                            }
                        }
                    }
                }
            }
        }

        private void generateNetherrackPile(WorldAccess world, Random random, BlockPos pos) {
            int height = 1 + random.nextInt(2);
            for (int y = 0; y < height; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (random.nextFloat() < 0.8f) {
                            BlockPos pilePos = pos.add(x, y, z);
                            if (world.isAir(pilePos) || world.getBlockState(pilePos).getHardness(world, pilePos) >= 0) {
                                world.setBlockState(pilePos, Blocks.NETHERRACK.getDefaultState(), Block.NOTIFY_LISTENERS);
                            }
                        }
                    }
                }
            }
        }

        private void generateBasaltFormation(WorldAccess world, Random random, BlockPos pos) {
            int height = 3 + random.nextInt(4);
            for (int y = 0; y < height; y++) {
                BlockPos pillarPos = pos.up(y);
                if (world.isAir(pillarPos) || world.getBlockState(pillarPos).getHardness(world, pillarPos) >= 0) {
                    world.setBlockState(pillarPos, Blocks.BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }
        }

        // Clear terrain around the cave to prevent it from being covered by terrain generation
        private void clearTerrainAroundCave(StructureWorldAccess world, BlockPos origin, Random random, int radius) {
            // Clear a larger area around the cave to ensure it's not covered by terrain
            int clearRadius = radius + 8; // Clear 8 extra blocks around the cave
            int clearHeight = 15; // Clear 15 blocks high to ensure the cave area is clear
            
            BlockPos.stream(origin.add(-clearRadius, -clearHeight, -clearRadius), 
                           origin.add(clearRadius, clearHeight, clearRadius))
                .map(BlockPos::toImmutable)
                .forEach(position -> {
                    // Only clear blocks that are within the clear radius
                    if (position.getSquaredDistance(origin) <= clearRadius * clearRadius) {
                        BlockState currentState = world.getBlockState(position);
                        // Only clear solid blocks, leave air and important blocks alone
                        if (currentState.isSolid() && !currentState.isOf(Blocks.BEDROCK) && 
                            !currentState.isOf(Blocks.NETHER_PORTAL) && !currentState.isOf(Blocks.END_PORTAL) &&
                            !currentState.isOf(Blocks.SOUL_FIRE) && !currentState.isOf(Blocks.FIRE)) {
                            world.setBlockState(position, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                        }
                    }
                });
        }
    }
}
