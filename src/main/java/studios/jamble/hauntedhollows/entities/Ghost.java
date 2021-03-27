package studios.jamble.hauntedhollows.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;

public class Ghost extends MonsterEntity {
    public Ghost(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes() {
        return MobEntity.registerAttributes()
                .createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.5D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.8D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 5.0D);
    }

}
