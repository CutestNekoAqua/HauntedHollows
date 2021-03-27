package studios.jamble.hauntedhollows.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import studios.jamble.hauntedhollows.HauntedHollows;
import studios.jamble.hauntedhollows.client.renderer.entity.model.ModelGhost;
import studios.jamble.hauntedhollows.entities.Ghost;

public class RenderGhost extends MobRenderer<Ghost, ModelGhost> {

    private String texture_name = "WhiteGhost";
    private ResourceLocation textureLocation = new ResourceLocation(HauntedHollows.MODID, "textures/entity/" + texture_name + ".png");

    public RenderGhost(EntityRendererManager rendermanagerIn) {
        super(rendermanagerIn, new ModelGhost(), 0.5f);
    }

    public RenderGhost set_tex_name(String name) {
        texture_name = name;
        textureLocation = new ResourceLocation(HauntedHollows.MODID, "textures/entity/" + texture_name + ".png");
        return this;
    }

    @Override
    public ResourceLocation getEntityTexture(Ghost entity) {
        return textureLocation;
    }
}
