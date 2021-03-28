package studios.jamble.hauntedhollows.structures;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import studios.jamble.hauntedhollows.Registry;
import studios.jamble.hauntedhollows.Utils;

import static studios.jamble.hauntedhollows.Utils.getLootTable;

public class ChestProcessor extends StructureProcessor {

    public static final ChestProcessor INSTANCE = new ChestProcessor();
    public static final Codec<ChestProcessor> CODEC = Codec.unit(() -> INSTANCE);
    private ChestProcessor() { }

    @Override
    public Template.BlockInfo process(IWorldReader worldView, BlockPos pos, BlockPos blockPos, Template.BlockInfo structureBlockInfoLocal, Template.BlockInfo structureBlockInfoWorld, PlacementSettings structurePlacementData, Template template) {
        if (structureBlockInfoWorld.state.matchesBlock(Blocks.CHEST) || structureBlockInfoWorld.state.matchesBlock(Blocks.BARREL)) {

            String loottable = null;

            if(structureBlockInfoWorld.nbt == null) return structureBlockInfoWorld;

            loottable = structureBlockInfoWorld.nbt.getString("LootTable");

            if(loottable.isEmpty()) {

                if(Utils.shouldHaveLoot()) {

                loottable = getLootTable();

                structureBlockInfoWorld.nbt.putString("LootTable", loottable);

                //System.out.println("Put loottable to " + loottable + " at " + pos + "and "+ blockPos);

                }

            }

            //System.out.println("Already has loottable " + loottable + " at " + pos + "and "+ blockPos);

            //worldView.getChunk(structureBlockInfoWorld.pos).setBlockState(structureBlockInfoWorld.pos, Blocks.AIR.getDefaultState(), false);
        }
        return structureBlockInfoWorld;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return Registry.CHEST_PROCESSORS;
    }
}
