package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/world/entity/Entity")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/entity/Entity")
public class Entity extends ClassImitator {

    public Object instance;

    private Entity() {
    }

    public Entity(Object instance) {
        this.instance = instance;
    }

    @Override
    public Object getInstanceBehind() {
        return instance;
    }

    public static ClassInformation target;


    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "id")
    @ResolveField(version = Version.MC1_7_10, value = "field_145783_c")
    @ResolveField(value = "entityId")
    private static FieldDefinition entityID;

    public int getEntityID() {
        return Entity.entityID.getInt(this.getInstanceBehind());
    }

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "horizontalCollision")
    @ResolveField(value = "isCollidedHorizontally")
    private static FieldDefinition isCollidedHorizontally;

    @ResolveField(value = "onGround")
    private static FieldDefinition onGround;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
    @ResolveField(value = "motionX")
    private static FieldDefinition motionX;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
    @ResolveField(value = "motionY")
    private static FieldDefinition motionY;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
    @ResolveField(value = "motionZ")
    private static FieldDefinition motionZ;


    public boolean isCollidedHorizontally() {
        return Entity.isCollidedHorizontally.getBoolean(instance);
    }

    public boolean isOnGround() {
        return Entity.onGround.getBoolean(instance);
    }

    public void setMotionX(double motionX) {
        if (Reflector.USE_NEW_API) {
            Vec3 delta = getDeltaMovement();
            setDeltaMovement(motionX, delta.getY(), delta.getZ());
            return;
        }

        Entity.motionX.set(instance, motionX);
    }

    public void setMotionY(double motionY) {
        if (Reflector.USE_NEW_API) {
            Vec3 delta = getDeltaMovement();
            setDeltaMovement(delta.getX(), motionY, delta.getZ());
            return;
        }

        Entity.motionY.set(instance, motionY);
    }

    public void setMotionZ(double motionZ) {
        if (Reflector.USE_NEW_API) {
            Vec3 delta = getDeltaMovement();
            setDeltaMovement(delta.getX(), delta.getY(), motionZ);
            return;
        }

        Entity.motionZ.set(instance, motionZ);
    }

    public double getMotionX() {
        if (Reflector.USE_NEW_API)
            return getDeltaMovement().getX();

        return Entity.motionX.getDouble(instance);
    }

    public double getMotionY() {
        if (Reflector.USE_NEW_API)
            return getDeltaMovement().getY();

        return Entity.motionY.getDouble(instance);
    }

    public double getMotionZ() {
        if (Reflector.USE_NEW_API)
            return getDeltaMovement().getZ();

        return Entity.motionZ.getDouble(instance);
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "getDeltaMovement", descriptor = "()Lnet/minecraft/world/phys/Vec3;")
    private static MethodDefinition getDeltaMovement;

    public Vec3 getDeltaMovement() {
        return new Vec3(Entity.getDeltaMovement.invoke(getInstanceBehind()));
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "setDeltaMovement", descriptor = "(DDD)V")
    private static MethodDefinition setDeltaMovement;

    public void setDeltaMovement(double x, double y, double z) {
       if(Reflector.USE_NEW_API){
           Entity.setDeltaMovement.invoke(getInstanceBehind(), x, y, z);
       }else{
           setMotionX(x);
           setMotionY(y);
           setMotionZ(z);
       }
    }


    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "x")
    @ResolveField(value = "posX")
    private static FieldDefinition posX;

    public double getPosX() {
        return Entity.posX.getDouble(instance);
    }

    public void setPosX(double posX) {
        Entity.posX.set(instance, posX);
    }

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "y")
    @ResolveField(value = "posY")
    public static FieldDefinition posY;

    public double getPosY() {
        return Entity.posY.getDouble(instance);
    }

    public void setPosY(double posY) {
        Entity.posY.set(instance, posY);
    }

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "z")
    @ResolveField(value = "posZ")
    public static FieldDefinition posZ;

    public double getPosZ() {
        return Entity.posZ.getDouble(instance);
    }

    public void setPosZ(double posZ) {
        Entity.posZ.set(instance, posZ);
    }


    @ResolveMethod(name = "setSprinting", descriptor = "(Z)V")
    private static MethodDefinition setSprinting;

    public void setSprinting(boolean sprinting) {
        Entity.setSprinting.invoke(this.getInstanceBehind(), sprinting);
    }

    @ResolveMethod(name = "isInWater", descriptor = "()Z")
    private static MethodDefinition isInWater;

    public boolean isInWater() {
        return Entity.isInWater.invokeType(this.getInstanceBehind());
    }

    @ResolveField(value = "fallDistance")
    private static FieldDefinition fallDistance;

    public float getFallDistance() {
        return Entity.fallDistance.getFloat(this.getInstanceBehind());
    }
}
