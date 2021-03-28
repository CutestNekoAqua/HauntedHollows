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

    public static List<WeightedItem<ResourceLocation>> weightedrooms = new ArrayList<>();

    public static List<Offset> offsets = new ArrayList<>();
    public static List<String> biggies = new ArrayList<>();
    public static List<WeightedItem<String>> loottables = new ArrayList<>();

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

    public static class WeightedItem<T> {
        public final T object;
        public final int weight;

        public WeightedItem(T object, int weight) {
            this.object = object;
            this.weight = weight;
        }
    }



    public static void setupRooms() {

        weightedrooms.clear();

        for (int i = 1; i <= 61; i++) {

            //rooms.add(new ResourceLocation("room" + i));

            weightedrooms.add(new WeightedItem<>(new ResourceLocation("room" + i), getWeightForRoom(i)));

        }

        setupOffsets();
        setupBiggies();
        setupLoottables();

    }

    private static int getWeightForRoom(int number) {

        switch (number) {
            case 5:
            case 33:
            case 28:
                return 5;
            case 7:
            case 11:
            case 8:
            case 9:
            case 10:
            case 24:
            case 12:
            case 49:
            case 50:
            case 13:
                return 10;
            case 53:
            case 52:
            case 3:
                return 30;
        }

        return 50;

    }

    public static void setupLoottables() {

        loottables.add(new WeightedItem<>("chests/end_city_treasure", 20));
        loottables.add(new WeightedItem<>("chests/abandoned_mineshaft", 40));
        loottables.add(new WeightedItem<>("chests/bastion_bridge", 15));
        loottables.add(new WeightedItem<>("chests/bastion_hoglin_stable", 15));
        loottables.add(new WeightedItem<>("chests/bastion_treasure", 10));
        loottables.add(new WeightedItem<>("chests/bastion_other", 20));
        loottables.add(new WeightedItem<>("chests/ruined_portal", 40));
        loottables.add(new WeightedItem<>("chests/woodland_mansion", 30));
        loottables.add(new WeightedItem<>("chests/igloo_chest", 40));
        loottables.add(new WeightedItem<>("chests/jungle_temple", 30));
        loottables.add(new WeightedItem<>("chests/desert_pyramid", 30));

        loottables.add(new WeightedItem<>("chests/village/village_armorer", 30));
        loottables.add(new WeightedItem<>("chests/village/village_butcher", 30));
        loottables.add(new WeightedItem<>("chests/village/village_cartographer", 30));
        loottables.add(new WeightedItem<>("chests/village/village_mason", 30));
        loottables.add(new WeightedItem<>("chests/village/village_shepherd", 30));
        loottables.add(new WeightedItem<>("chests/village/village_tannery", 30));
        loottables.add(new WeightedItem<>("chests/village/village_weaponsmith", 30));
        loottables.add(new WeightedItem<>("chests/village/village_desert_house", 30));
        loottables.add(new WeightedItem<>("chests/village/village_plains_house", 30));
        loottables.add(new WeightedItem<>("chests/village/village_savanna_house", 30));
        loottables.add(new WeightedItem<>("chests/village/village_snowy_house", 30));
        loottables.add(new WeightedItem<>("chests/village/village_taiga_house", 30));
        loottables.add(new WeightedItem<>("chests/village/village_fisher", 30));
        loottables.add(new WeightedItem<>("chests/village/village_fletcher", 30));
        loottables.add(new WeightedItem<>("chests/village/village_temple", 30));
        loottables.add(new WeightedItem<>("chests/village/village_toolsmith", 30));

    }

    public static boolean shouldHaveLoot() {

        Random r = new Random();
        if(r.nextInt(100) < 50) return true;

        return false;
    }

    public static String getLootTable() {

        // Compute the total weight of all items together.
        // This can be skipped of course if sum is already 1.
        double totalWeight = 0.0;
        for (Utils.WeightedItem i : loottables) {
            totalWeight += i.weight;
        }

        // Now choose a random item.
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < loottables.size() - 1; ++idx) {
            r -= loottables.get(idx).weight;
            if (r <= 0.0) break;
        }

        return Utils.loottables.get(idx).object;

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

        if(random.nextInt(100) > 97) {

            return true;

        }

        return false;
    }

    public static BlockPos moveRoofRandom(BlockPos pos) {

        Random random = new Random();
        return pos.add(random.nextInt(5) - 2, 0 , random.nextInt(5) - 2);

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
