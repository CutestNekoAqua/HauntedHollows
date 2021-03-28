package studios.jamble.hauntedhollows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import studios.jamble.hauntedhollows.entities.Ghost;
import studios.jamble.hauntedhollows.structures.ChestProcessor;
import studios.jamble.hauntedhollows.structures.MainHall;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registry {


    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     *
     * HOWEVER, do note that Deferred Registries only work for anything that is a Forge Registry. This means that
     * configured structures and configured features need to be registered directly to WorldGenRegistries as there
     * is no Deferred Registry system for them.
     */
    public static final DeferredRegister<Structure<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, HauntedHollows.MODID);

    public static final DeferredRegister<EntityType<?>> DEFERRED_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.ENTITIES, HauntedHollows.MODID);

    public static final RegistryObject<EntityType<Ghost>> WHITE_GHOST = DEFERRED_ENTITY_TYPE.register("white_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("white_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(20.0D, 2.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> YELLOW_GHOST = DEFERRED_ENTITY_TYPE.register("yellow_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("yellow_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(25.0D, 3.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> RED_GHOST = DEFERRED_ENTITY_TYPE.register("red_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("red_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(35.0D, 5.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> ORANGE_GHOST = DEFERRED_ENTITY_TYPE.register("orange_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("orange_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(30.0D, 4.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> TEAL_GHOST = DEFERRED_ENTITY_TYPE.register("teal_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("teal_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(40.0D, 6.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> GREEN_GHOST = DEFERRED_ENTITY_TYPE.register("green_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("green_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(45.0D, 7.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> BLUE_GHOST = DEFERRED_ENTITY_TYPE.register("blue_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("blue_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(50.0D, 8.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> PINK_GHOST = DEFERRED_ENTITY_TYPE.register("pink_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("pink_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(55.0D, 9.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> PURPLE_GHOST = DEFERRED_ENTITY_TYPE.register("purple_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("purple_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(60.0D, 10.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> GREY_GHOST = DEFERRED_ENTITY_TYPE.register("grey_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("grey_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(65.0D, 11.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    public static final RegistryObject<EntityType<Ghost>> BLACK_GHOST = DEFERRED_ENTITY_TYPE.register("black_ghost", () -> {

        EntityType<Ghost> ghost = EntityType.Builder.create(Ghost::new, EntityClassification.MONSTER)
                .setTrackingRange(48).setUpdateInterval(3).size(EntityType.PLAYER.getWidth(), EntityType.PLAYER.getHeight())
                .build("black_ghost");

        GlobalEntityTypeAttributes.put(ghost, Ghost.registerAttributes(70.0D, 12.0D, 60.0D, 32.0D, 3.0D, 3.0D).create());

        return ghost;

    });

    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the resourcelocation of structure_tutorial:run_down_house.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It great for mod/datapacks compatibility.
     *
     * IMPORTANT: Once you have set the name for your structure below and distributed your mod,
     * it should NEVER be changed or else it can cause worlds to become corrupted if they generated
     * any chunks with your mod with the old structure name. See MC-194811 in Mojang's bug tracker for details.
     *
     * Forge has an issue report here: https://github.com/MinecraftForge/MinecraftForge/issues/7363
     * Keep watch on that to know when it is safe to remove or change structure's registry names
     */
    public static final RegistryObject<Structure<NoFeatureConfig>> RUN_DOWN_HOUSE = registerStructure("mainhall", () -> (new MainHall(NoFeatureConfig.CODEC)));

    /**
     * Helper method for registering all structures
     */
    private static <T extends Structure<?>> RegistryObject<T> registerStructure(String name, Supplier<T> structure) {
        return DEFERRED_REGISTRY_STRUCTURE.register(name, structure);
    }

    /**
     * This is where we set the rarity of your structures and determine if land conforms to it.
     * See the comments in below for more details.
     */
    public static void setupStructures() {
        setupMapSpacingAndLand(
                RUN_DOWN_HOUSE.get(), /* The instance of the structure */
                new StructureSeparationSettings(2 /* maximum distance apart in chunks between spawn attempts */,
                        1 /* minimum distance apart in chunks between spawn attempts */,
                        1234567890 /* this modifies the seed of the structure so no two structures always spawn over each-other. Make this large and unique. */),
                true);


        // Add more structures here and so on
    }

    public static IStructureProcessorType<ChestProcessor> CHEST_PROCESSORS = () -> ChestProcessor.CODEC;

    public static void registerProcessors() {
        net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.STRUCTURE_PROCESSOR, new ResourceLocation(HauntedHollows.MODID, "chest_processors"), CHEST_PROCESSORS);
    }

    /**
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the structureSeparationSettings argument. Called by registerFeatures.
     */
    public static <F extends Structure<?>> void setupMapSpacingAndLand(
            F structure,
            StructureSeparationSettings structureSeparationSettings,
            boolean transformSurroundingLand)
    {
        /*
         * We need to add our structures into the map in Structure alongside vanilla
         * structures or else it will cause errors. Called by registerStructure.
         *
         * If the registration is setup properly for the structure,
         * getRegistryName() should never return null.
         */
        Structure.NAME_STRUCTURE_BIMAP.put(structure.getRegistryName().toString(), structure);

        /*
         * Whether surrounding land will be modified automatically to conform to the bottom of the structure.
         * Basically, it adds land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         *
         * Note: The air space this method will create will be filled with water if the structure is below sealevel.
         * This means this is best for structure above sealevel so keep that in mind.
         *
         * field_236384_t_ requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
         */
        if(transformSurroundingLand){
            Structure.field_236384_t_ =
                    ImmutableList.<Structure<?>>builder()
                            .addAll(Structure.field_236384_t_)
                            .add(structure)
                            .build();
        }

        /*
         * Adds the structure's spacing into a default structure spacing map that other mods can utilize.
         *
         * However, while it does propagate the spacing to some correct dimensions form this map,
         * it seems it doesn't always work for code made dimensions as they read from this list beforehand.
         *
         * Instead, we will use the WorldEvent.Load event in StructureTutorialMain to add the structure
         * spacing from this list into that dimension or do dimension blacklisting properly. We also use
         * our entry in DimensionStructuresSettings.field_236191_b_ in WorldEvent.Load as well.
         *
         * field_236191_b_ requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
         */
        DimensionStructuresSettings.field_236191_b_ =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.field_236191_b_)
                        .put(structure, structureSeparationSettings)
                        .build();


        /*
         * There are very few mods that relies on seeing your structure in the noise settings registry before the world is made.
         *
         * This is best done here in FMLCommonSetupEvent after you created your configuredstructures.
         * You may see some mods add their spacings to DimensionSettings.field_242740_q instead of the NOISE_SETTINGS loop below but
         * that field only applies for the default overworld and won't add to other worldtypes or dimensions (like amplified or Nether).
         * So yeah, don't do DimensionSettings.field_242740_q. Use the NOISE_SETTINGS loop below instead.
         */
        WorldGenRegistries.NOISE_SETTINGS.getEntries().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().getStructures().func_236195_a_();

            /*
             * Pre-caution in case a mod makes the structure map immutable like datapacks do.
             * I take no chances myself. You never know what another mods does...
             *
             * field_236193_d_ requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
             */
            if(structureMap instanceof ImmutableMap){
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().getStructures().field_236193_d_ = tempMap;
            }
            else{
                structureMap.put(structure, structureSeparationSettings);
            }
        });
    }

}
