package studios.jamble.hauntedhollows.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import studios.jamble.hauntedhollows.entities.Ghost;

public class ModelGhost extends EntityModel<Ghost> {

    private final ModelRenderer Head;
    private final ModelRenderer Body;
    private final ModelRenderer RightArm;
    private final ModelRenderer LeftArm;
    private final ModelRenderer GhostLegs2;

    public boolean isSneak;
    public float swimAnimation;

    public ModelGhost() {
        textureWidth = 64;
        textureHeight = 64;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        setRotationAngle(Head, -0.1047F, 0.0873F, 0.0F);
        Head.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        Head.setTextureOffset(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, false);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        Body.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        setRotationAngle(RightArm, -1.3526F, 0.0F, 0.0F);


        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        setRotationAngle(LeftArm, -1.3177F, 0.0F, 0.0F);
        LeftArm.setTextureOffset(36, 18).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 12.0F, 2.0F, 0.25F, false);
        LeftArm.setTextureOffset(44, 18).addBox(-11.0F, -2.0F, -2.0F, 2.0F, 12.0F, 2.0F, 0.25F, false);

        GhostLegs2 = new ModelRenderer(this);
        GhostLegs2.setRotationPoint(-1.9F, 12.0F, 0.0F);
        setRotationAngle(GhostLegs2, 1.1519F, 0.0F, 0.0349F);
        GhostLegs2.setTextureOffset(0, 16).addBox(0.0F, -1.0F, -0.85F, 4.0F, 10.0F, 2.0F, 0.25F, false);
    }

    protected float rotLerpRad(float angleIn, float maxAngleIn, float mulIn) {
        float f = (mulIn - maxAngleIn) % ((float)Math.PI * 2F);
        if (f < -(float)Math.PI) {
            f += ((float)Math.PI * 2F);
        }

        if (f >= (float)Math.PI) {
            f -= ((float)Math.PI * 2F);
        }

        return maxAngleIn + angleIn * f;
    }

    @Override
    public void setRotationAngles(Ghost entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        boolean flag = entity.getTicksElytraFlying() > 4;
        boolean flag1 = entity.isActualySwimming();
        this.Head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        if (flag) {
            this.Head.rotateAngleX = (-(float)Math.PI / 4F);
        } else if (this.swimAnimation > 0.0F) {
            if (flag1) {
                this.Head.rotateAngleX = this.rotLerpRad(this.swimAnimation, this.Head.rotateAngleX, (-(float)Math.PI / 4F));
            } else {
                this.Head.rotateAngleX = this.rotLerpRad(this.swimAnimation, this.Head.rotateAngleX, headPitch * ((float)Math.PI / 180F));
            }
        } else {
            this.Head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        }

        this.Body.rotateAngleY = 0.0F;
        this.RightArm.rotationPointZ = 0.0F;
        this.RightArm.rotationPointX = -5.0F;
        this.LeftArm.rotationPointZ = 0.0F;
        this.LeftArm.rotationPointX = 5.0F;
        float f = 1.0F;
        if (flag) {
            f = (float)entity.getMotion().lengthSquared();
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        this.RightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        this.LeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        this.RightArm.rotateAngleZ = 0.0F;
        this.LeftArm.rotateAngleZ = 0.0F;
        if (this.isSitting) {
            this.RightArm.rotateAngleX += (-(float)Math.PI / 5F);
            this.LeftArm.rotateAngleX += (-(float)Math.PI / 5F);
        }

        this.RightArm.rotateAngleY = 0.0F;
        this.LeftArm.rotateAngleY = 0.0F;
            this.Body.rotateAngleX = 0.0F;
            this.Head.rotationPointY = 0.0F;
            this.Body.rotationPointY = 0.0F;
            this.LeftArm.rotationPointY = 2.0F;
            this.RightArm.rotationPointY = 2.0F;

        ModelHelper.func_239101_a_(this.RightArm, this.LeftArm, ageInTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        Head.render(matrixStack, buffer, packedLight, packedOverlay);
        Body.render(matrixStack, buffer, packedLight, packedOverlay);
        RightArm.render(matrixStack, buffer, packedLight, packedOverlay);
        LeftArm.render(matrixStack, buffer, packedLight, packedOverlay);
        GhostLegs2.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}
