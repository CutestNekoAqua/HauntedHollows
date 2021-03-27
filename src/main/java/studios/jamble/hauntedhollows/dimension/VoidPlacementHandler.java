package studios.jamble.hauntedhollows.dimension;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import studios.jamble.hauntedhollows.Utils;


@SuppressWarnings("deprecation")
public class VoidPlacementHandler {
    public static BlockPos enter(ServerWorld voidWorld, final BlockPos portalPos) {

        return enterVoid(voidWorld, portalPos);

    }

    /*public static void leave(ServerWorld newWorld, final BlockPos portalPos) {
        BlockPos pos = leaveVoid(newWorld, portalPos);
        return new TeleportTarget(Vec3d.of(pos).add(0.5, 0, 0.5), Vec3d.ZERO, 0, 0);
    }*/

    private static BlockPos enterVoid(ServerWorld newWorld, BlockPos portalPos) {
        BlockPos spawnPos = new BlockPos(portalPos.getX(), 129, portalPos.getZ());
        //spawnVoidPlatform(newWorld, spawnPos.down());
        return spawnPos;
    }

    private static BlockPos leaveVoid(ServerWorld newWorld, BlockPos portalPos) {
        return newWorld.getHeight(Heightmap.Type.MOTION_BLOCKING, portalPos).up();
    }

    private static void spawnVoidPlatform(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
            BlockState platformBlock = Blocks.STONE.getDefaultState();
            for (int x = -3; x < 4; x++) {
                for (int z = -3; z < 4; z++) {
                    if (world.isAirBlock(pos.add(x, 0, z))) {
                        world.setBlockState(pos.add(x, 0, z), platformBlock);
                    }

                }
            }
            world.setBlockState(pos, Blocks.DARK_OAK_PLANKS.getDefaultState());
            for (Direction facing : Direction.values()) {
                if (facing.getAxis().isHorizontal()) {
                    //world.setBlockState(pos.up().offset(facing), Blocks.TORCH.getDefaultState());
                }
            }

        }
    }
}