package studios.jamble.hauntedhollows;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.Level;
import studios.jamble.hauntedhollows.dimension.VoidPlacementHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static List<ResourceLocation> rooms = new ArrayList<>();
    public static List<Offset> offsets = new ArrayList<>();
    public static List<String> biggies = new ArrayList<>();
    public static List<String> loottables = new ArrayList<>();

    public static class Offset {

        private final int offset;
        private final String name;

        public Offset(int offset, String name) {
            this.offset = offset;
            this.name = name;
        }

        public int getOffset() {
            return offset;
        }

        public String getName() {
            return name;
        }
    }

    public static class PosAndRot {

        public final BlockPos pos;
        public final Rotation rotation;

        public PosAndRot(BlockPos pos, Rotation rotation) {
            this.pos = pos;
            this.rotation = rotation;
        }

    }

    public static boolean shouldGen(int x, int z) {
        return x % 21 == 0 && z % 21 == 0;
    }

    public static void setupRooms() {

        rooms.clear();

        for (int i = 1; i <= 60; i++) {

            rooms.add(new ResourceLocation("room" + i));

        }

        setupOffsets();
        setupBiggies();
        setupLoottables();

    }

    public static void setupLoottables() {

        loottables.add("chests/end_city_treasure");
        loottables.add("chests/abandoned_mineshaft");
        loottables.add("chests/bastion_bridge");
        loottables.add("chests/bastion_hoglin_stable");
        loottables.add("chests/bastion_treasure");
        loottables.add("chests/bastion_other");
        loottables.add("chests/ruined_portal");
        loottables.add("chests/woodland_mansion");
        loottables.add("chests/igloo_chest");
        loottables.add("chests/jungle_temple");

    }

    public static boolean shouldHaveLoot() {

        Random r = new Random();
        if(r.nextInt(100) < 50) return true;

        return false;
    }

    public static String getLootTable() {

        Random r = new Random();
        return loottables.get(r.nextInt(loottables.size()));

    }

    public static void setupBiggies() {

        biggies.add("room24");
        biggies.add("room34");
        biggies.add("room35");
        biggies.add("room49");
        biggies.add("room50");
        biggies.add("room7");
        biggies.add("room8");
        biggies.add("room9");
        biggies.add("room10");
        biggies.add("room11");
        biggies.add("room57");

        biggies.add("room13");
        biggies.add("room12");

        biggies.add("mainhall");

    }

    public static boolean shouldGenRoof() {
        Random random = new Random();

        if(random.nextInt(100) < 15) {

            return true;

        }

        return false;
    }

    public static BlockPos moveRoofRandom(BlockPos pos) {

        Random random = new Random();
        return pos.add(random.nextInt(20) - 10, 0 , random.nextInt(20) - 10);

    }

    public static boolean isBig(String name) {

        for (String biggy:
             biggies) {
            if(name.equalsIgnoreCase(biggy)) {
                return true;
            }
        }

        return false;
    }

    public static void setupOffsets() {

        offsets.add(new Offset(-2, "room11"));
        offsets.add(new Offset(-1, "room10"));

    }

    public static void enteringVoid(Entity entity){
        //Note, the player does not hold the previous dimension oddly enough.
        Vector3d destinationPosition;

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerEntity = ((ServerPlayerEntity) entity);

            MinecraftServer minecraftServer = playerEntity.getServer(); // the server itself

            if(minecraftServer == null) {
                HauntedHollows.LOGGER.fatal("Something went really wrong! Contact Waterdev with error code SERVER_NOT_FOUND");
            }

            ServerWorld voidWorld = minecraftServer.getWorld(HauntedHollows.VOID_WORLD_KEY);

            // Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
            if(voidWorld == null){
                HauntedHollows.LOGGER.log(Level.INFO, "Haunted Hollows: Please restart the server. The Mansion hasn't been made yet due to this bug: https://bugs.mojang.com/browse/MC-195468. A restart will fix this.");
                ITextComponent message = new StringTextComponent("Haunted Hollows: Please restart the server. The Mansion hasn't been made yet due to this bug: §6https://bugs.mojang.com/browse/MC-195468§f. A restart will fix this.");
                playerEntity.sendStatusMessage(message, true);
                return;
            }

            destinationPosition = new Vector3d(10, 130, 10);
            BlockPos pos = VoidPlacementHandler.enter(voidWorld, new BlockPos(destinationPosition));
            playerEntity.teleport(
                    voidWorld,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    playerEntity.rotationYaw,
                    playerEntity.rotationPitch
            );
        }
    }

}
