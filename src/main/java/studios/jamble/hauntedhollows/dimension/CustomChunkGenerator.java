package studios.jamble.hauntedhollows.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.server.ServerWorld;
import studios.jamble.hauntedhollows.HauntedHollows;
import studios.jamble.hauntedhollows.Utils;
import studios.jamble.hauntedhollows.structures.ChestProcessor;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static studios.jamble.hauntedhollows.Utils.weightedrooms;

public class CustomChunkGenerator extends ChunkGenerator {

    public static void registerChunkgenerator() {
        Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(HauntedHollows.MODID, "chunk_generator"), CustomChunkGenerator.CODEC);
    }

    public static final Codec<CustomChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BiomeProvider.CODEC.fieldOf("biome_source")
                            .forGetter((generator) -> generator.biomeProvider)
            ).apply(instance, instance.stable(CustomChunkGenerator::new))
    );

    public CustomChunkGenerator(BiomeProvider biomeSource) {
        super(biomeSource, new DimensionStructuresSettings(Optional.empty(), Collections.emptyMap()));
    }

    @Override
    protected Codec<? extends ChunkGenerator> func_230347_a_() {
        return CODEC;
    }

    @Override
    public ChunkGenerator func_230349_a_(long seed) {
        return this;
    }

    @Override
    public void generateSurface(WorldGenRegion region, IChunk chunk) {
    }

    @Override
    public void func_230352_b_(IWorld world, StructureManager accessor, IChunk chunk) {

        int chunkWorldX = chunk.getPos().getXStart();
        int chunkWorldZ = chunk.getPos().getZStart();
        BlockPos.Mutable blockPos = new BlockPos.Mutable();

        for(int j = 0; j < 16; j++)
        {
            for(int k = 0; k < 16; k++)
            {

                if(Utils.shouldGen(chunkWorldX + j, chunkWorldZ + k)) {

                    loadStructure(blockPos.setPos(new Vector3i(chunkWorldX + j, 128, chunkWorldZ + k)), world, (chunkWorldX == 0 && chunkWorldZ == 0) ? new ResourceLocation("mainhall") : getRoom(), chunk);

                }

            }
        }

    }

    public BlockPos getStructureOffset(String name, BlockPos pos) {

        for (Utils.Offset offset:
        Utils.offsets) {

            if (offset.getName().equalsIgnoreCase(name)) {

                return pos.add(0, offset.getOffset(), 0);

            }

        }

        return pos;

    }

    public Utils.PosAndRot getRotationAndPosition(String name, BlockPos pos) {

        BlockPos position = getStructureOffset(name, pos);
        Rotation rotation = Rotation.randomRotation(new Random());

        if(rotation == Rotation.CLOCKWISE_90) {
            position = position.add(20, 0, 0);
        } else if(rotation == Rotation.CLOCKWISE_180) {
            position = position.add(20, 0, 20);
        } else if(rotation == Rotation.COUNTERCLOCKWISE_90) {
            position = position.add(0, 0, 20);
        }

        return new Utils.PosAndRot(position, rotation);

    }

    public ResourceLocation getRoom() {

        // Compute the total weight of all items together.
        // This can be skipped of course if sum is already 1.
        double totalWeight = 0.0;
        for (Utils.WeightedItem i : weightedrooms) {
            totalWeight += i.weight;
        }

        // Now choose a random item.
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < weightedrooms.size() - 1; ++idx) {
            r -= weightedrooms.get(idx).weight;
            if (r <= 0.0) break;
        }

        return Utils.weightedrooms.get(idx).object;

    }

    public void loadStructure(BlockPos pos, IWorld world, ResourceLocation loc, IChunk chunk) {
        WorldGenRegion worldGenRegion = (WorldGenRegion) world;
        ServerWorld worldserver = worldGenRegion.getWorld();
        if(worldserver == null) {
            HauntedHollows.LOGGER.error("WORLD NULL");
            return;
        } else {
            //HauntedHollows.LOGGER.info("HOUSE GENERATION started");
        }
        TemplateManager templatemanager = worldserver.getStructureTemplateManager();
        Template template = templatemanager.getTemplate(loc);

        Utils.PosAndRot posAndRot = getRotationAndPosition(loc.getPath(), pos);

        if (template != null) {
            BlockState iblockstate = world.getBlockState(pos);
            worldserver.notifyBlockUpdate(pos, iblockstate, iblockstate, 3);
            PlacementSettings placementsettings = (new PlacementSettings()).setMirror(Mirror.NONE)
                    .setRotation(posAndRot.rotation).setIgnoreEntities(false).setChunk(null);

            placementsettings.addProcessor(ChestProcessor.INSTANCE);

            template.func_237144_a_(worldGenRegion, posAndRot.pos, placementsettings, getRandom(0));

            if(!Utils.isBig(loc.getPath())) {

                ResourceLocation floor2 = getRoom();

                while (Utils.isBig(floor2.getPath())) {
                    floor2 = getRoom();
                }

                Template template2 = templatemanager.getTemplate(floor2);

                pos = pos.add(0, 5, 0);

                Utils.PosAndRot posAndRot2 = getRotationAndPosition(floor2.getPath(), pos);

                if (template2 != null) {

                    BlockState iblockstate2 = world.getBlockState(pos);
                    worldserver.notifyBlockUpdate(pos, iblockstate2, iblockstate2, 3);
                    PlacementSettings placementsettings2 = (new PlacementSettings()).setMirror(Mirror.NONE)
                            .setRotation(posAndRot2.rotation).setIgnoreEntities(false).setChunk(null);

                    placementsettings2.addProcessor(ChestProcessor.INSTANCE);

                    template2.func_237144_a_(worldGenRegion, posAndRot2.pos, placementsettings2, getRandom(0));

                    if(Utils.shouldGenRoof()) {

                        Template rooftemplate = templatemanager.getTemplate(new ResourceLocation("roof"));
                        Utils.PosAndRot roofposrot = getRotationAndPosition("roof", pos.add(0, 1, 0));

                        if(rooftemplate != null) {

                            worldserver.notifyBlockUpdate(pos, iblockstate2, iblockstate2, 3);
                            PlacementSettings placementsettings3 = (new PlacementSettings()).setMirror(Mirror.NONE)
                                    .setRotation(roofposrot.rotation).setIgnoreEntities(false).setChunk(null);

                            placementsettings3.addProcessor(ChestProcessor.INSTANCE);

                            rooftemplate.func_237144_a_(worldGenRegion, Utils.moveRoofRandom(roofposrot.pos), placementsettings3, getRandom(0));

                        }

                    }

                }

            }

            //HauntedHollows.LOGGER.info("HOUSE GENERATION Generated: " + loc.getPath() + " at " + pos + " with Rotation " + posAndRot.rotation.toString() + " and calculated Coords " + posAndRot.pos);
        }
    }

    private static Random getRandom(long seed) {
        return seed == 0L ? new Random(Util.milliTime()) : new Random(seed);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    @Override
    public IBlockReader func_230348_a_(int x, int z) {
        return new Blockreader(new BlockState[0]);
    }

}
