package studios.jamble.hauntedhollows;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import studios.jamble.hauntedhollows.client.renderer.entity.RenderGhost;
import studios.jamble.hauntedhollows.dimension.CustomChunkGenerator;
import studios.jamble.hauntedhollows.entities.Ghost;
import studios.jamble.hauntedhollows.structures.ConfiguredStructures;

import java.lang.reflect.Method;
import java.util.*;

import static studios.jamble.hauntedhollows.Registry.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HauntedHollows.MODID)
public class HauntedHollows {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "hauntedhollows";

    public static final ResourceLocation MOD_DIMENSION_ID = new ResourceLocation(MODID, "house_inside");

    public static final RegistryKey<World> VOID_WORLD_KEY = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, MOD_DIMENSION_ID);

    public HauntedHollows() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        studios.jamble.hauntedhollows.Registry.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        studios.jamble.hauntedhollows.Registry.DEFERRED_ENTITY_TYPE.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("Enqueueing work for Haunted Hollows..");

        event.enqueueWork(() -> {

            CustomChunkGenerator.registerChunkgenerator();
            LOGGER.info("Our Void Chunk generator has been loaded..");

            Utils.setupRooms();

            studios.jamble.hauntedhollows.Registry.registerProcessors();

            //studios.jamble.hauntedhollows.Registry.setupStructures();
            //ConfiguredStructures.registerConfiguredStructures();

        });

        //LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

    }

    /**
     * This is the event you will use to add anything to any biome.
     * This includes spawns, changing the biome's looks, messing with its surfacebuilders,
     * adding carvers, spawning new features... etc
     *
     * Here, we will use this to add our structure to all biomes.
     */
    @SubscribeEvent
    public void biomeModification(final BiomeLoadingEvent event) {
        /*
         * Add our structure to all biomes including other modded biomes.
         * You can skip or add only to certain biomes based on stuff like biome category,
         * temperature, scale, precipitation, mod id, etc. All kinds of options!
         *
         * You can even use the BiomeDictionary as well! To use BiomeDictionary, do
         * RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()) to get the biome's
         * registrykey. Then that can be fed into the dictionary to get the biome's types.
         */
        event.getGeneration().getStructures().add(() -> ConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE);
        LOGGER.info("Test add biome");
    }

    /**
     * Will go into the world's chunkgenerator and manually add our structure spacing.
     * If the spacing is not added, the structure doesn't spawn.
     *
     * Use this for dimension blacklists for your structure.
     * (Don't forget to attempt to remove your structure too from the map if you are blacklisting that dimension!)
     * (It might have your structure in it already.)
     *
     * Basically use this to make absolutely sure the chunkgenerator can or cannot spawn your structure.
     */
    private static Method GETCODEC_METHOD;
    @SubscribeEvent
    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();

            /*
             * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
             * They will handle your structure spacing for your if you add to WorldGenRegistries.NOISE_SETTINGS in FMLCommonSetupEvent.
             * This here is done with reflection as this tutorial is not about setting up and using Mixins.
             * If you are using mixins, you can call getCodec with an invoker mixin instead of using reflection.
             */
            try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR_CODEC.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkProvider().generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                LOGGER.error("Was unable to check if " + serverWorld.getDimensionKey().getLocation() + " is using Terraforged's ChunkGenerator.");
            }

            LOGGER.info("Test Load");

            /*
             * Prevent spawning our structure in Vanilla's superflat world as
             * people seem to want their superflat worlds free of modded structures.
             * Also that vanilla superflat is really tricky and buggy to work with in my experience.
             */
            if(serverWorld.getChunkProvider().getChunkGenerator() instanceof FlatChunkGenerator &&
                    serverWorld.getDimensionKey().equals(World.OVERWORLD)){
                return;
            }

            /*
             * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
             * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
             *
             * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_SETTINGS in FMLCommonSetupEvent
             * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
             * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
             */
            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
            tempMap.putIfAbsent(studios.jamble.hauntedhollows.Registry.RUN_DOWN_HOUSE.get(), DimensionStructuresSettings.field_236191_b_.get(studios.jamble.hauntedhollows.Registry.RUN_DOWN_HOUSE.get()));
            serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
        }
    }

    /*public static EntityType<Ghost> WHITE_GHOST = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
            .setTrackingRange(48).setUpdateInterval(3).size(0.6F, 0.9F)
                .build("white_ghost");;

    @SubscribeEvent
    public void registerEntity(final RegistryEvent.Register<EntityType<?>> event) {

        WHITE_GHOST.setRegistryName(HauntedHollows.MODID, "white_ghost");

        GlobalEntityTypeAttributes.put(WHITE_GHOST, Ghost.registerAttributes().create());

        event.getRegistry().register(WHITE_GHOST);
        System.out.println("Registered Ghost!!");

    }*/

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        EntityType<Ghost> white_ghost = WHITE_GHOST.get();
        EntityType<Ghost> yellow_ghost = YELLOW_GHOST.get();
        EntityType<Ghost> orange_ghost = ORANGE_GHOST.get();
        EntityType<Ghost> red_ghost = RED_GHOST.get();
        EntityType<Ghost> purple_ghost = PURPLE_GHOST.get();
        EntityType<Ghost> pink_ghost = PINK_GHOST.get();
        EntityType<Ghost> teal_ghost = TEAL_GHOST.get();
        EntityType<Ghost> black_ghost = BLACK_GHOST.get();
        EntityType<Ghost> blue_ghost = BLUE_GHOST.get();
        EntityType<Ghost> green_ghost = GREEN_GHOST.get();
        EntityType<Ghost> grey_ghost = GREY_GHOST.get();

        RenderingRegistry.registerEntityRenderingHandler(white_ghost, manager -> new RenderGhost(manager, "white_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(yellow_ghost, manager -> new RenderGhost(manager, "yellow_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(orange_ghost, manager -> new RenderGhost(manager, "orange_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(red_ghost, manager -> new RenderGhost(manager, "red_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(purple_ghost, manager -> new RenderGhost(manager, "purple_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(pink_ghost, manager -> new RenderGhost(manager, "pink_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(teal_ghost, manager -> new RenderGhost(manager, "teal_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(black_ghost, manager -> new RenderGhost(manager, "black_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(blue_ghost, manager -> new RenderGhost(manager, "blue_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(green_ghost, manager -> new RenderGhost(manager, "green_ghost"));
        RenderingRegistry.registerEntityRenderingHandler(grey_ghost, manager -> new RenderGhost(manager, "grey_ghost"));

        System.out.println("White Ghost registered");

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        /*InterModComms.sendTo("HauntedHollows", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });*/
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        /*LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));*/
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");

        Utils.setupRooms();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        List<MobSpawnInfo.Spawners> spawns =
                event.getSpawns().getSpawner(EntityClassification.MONSTER);

        spawns.clear();

        // Make Enderman spawns more frequent and add Blaze spawns in all biomes
        spawns.add(new MobSpawnInfo.Spawners(BLACK_GHOST.get(), 1, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(BLUE_GHOST.get(), 2, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(GREY_GHOST.get(), 3, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(GREEN_GHOST.get(), 4, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(ORANGE_GHOST.get(), 9, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(PINK_GHOST.get(), 5, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(PURPLE_GHOST.get(), 6, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(RED_GHOST.get(), 8, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(TEAL_GHOST.get(), 7, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(WHITE_GHOST.get(), 11, 1, 1));
        spawns.add(new MobSpawnInfo.Spawners(YELLOW_GHOST.get(), 10, 1, 1));
    }

    @SubscribeEvent
    public void onBreakStuff(BlockEvent.BreakEvent event) {
        // do something when the server starts
        //LOGGER.info("HELLO from server starting");

        BlockPos pos = event.getPos();

        if(pos.getY() < 129 || pos.getY() > 136) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if(entity instanceof ServerPlayerEntity) {

            if(event.getWorld().getDimensionKey().getRegistryName().getNamespace().equalsIgnoreCase("minecraft")) {

                Timer t = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {

                        Utils.enteringVoid(entity);

                    }
                };
                t.schedule(tt, new Date(System.currentTimeMillis() + 100));
            } else {
                HauntedHollows.LOGGER.info("Not in vanilla dimension! " + event.getWorld().getDimensionKey().toString());
            }

        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            //LOGGER.info("HELLO from Register Block");
        }
    }
}
