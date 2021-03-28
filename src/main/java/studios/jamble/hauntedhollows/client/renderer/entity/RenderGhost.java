package studios.jamble.hauntedhollows.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.util.ResourceLocation;
import studios.jamble.hauntedhollows.HauntedHollows;
import studios.jamble.hauntedhollows.client.renderer.entity.model.ModelGhost;
import studios.jamble.hauntedhollows.entities.Ghost;

public class RenderGhost extends MobRenderer<Ghost, ModelGhost> {

    private final ResourceLocation textureLocation;

    public RenderGhost(EntityRendererManager rendermanagerIn, String name) {
        super(rendermanagerIn, new ModelGhost(), 0.5f);
        textureLocation = new ResourceLocation(HauntedHollows.MODID, "/textures/entity/" + name + ".png");
    }

    @Override
    public ResourceLocation getEntityTexture(Ghost entity) {
        return textureLocation;
    }


}
