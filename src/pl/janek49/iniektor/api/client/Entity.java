package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

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


    @ResolveField(version = Version.MC1_7_10, value = "field_145783_c")
    @ResolveField(value = "entityId")
    private static FieldDefinition entityID;

    public int getEntityID() {
        return Entity.entityID.getInt(this.getInstanceBehind());
    }

    @ResolveField(value = "isCollidedHorizontally")
    private static FieldDefinition isCollidedHorizontally;

    @ResolveField(value = "onGround")
    private static FieldDefinition onGround;

    @ResolveField(value = "motionX")
    private static FieldDefinition motionX;

    @ResolveField(value = "motionY")
    private static FieldDefinition motionY;

    @ResolveField(value = "motionZ")
    private static FieldDefinition motionZ;


    public boolean isCollidedHorizontally() {
        return Entity.isCollidedHorizontally.getBoolean(instance);
    }

    public boolean isOnGround() {
        return Entity.onGround.getBoolean(instance);
    }

    public void setMotionX(double motionX) {
        Entity.motionX.set(instance, motionX);
    }

    public void setMotionY(double motionY) {
        Entity.motionY.set(instance, motionY);
    }

    public void setMotionZ(double motionZ) {
        Entity.motionZ.set(instance, motionZ);
    }

    public double getMotionX() {
        return Entity.motionX.getDouble(instance);
    }

    public double getMotionY() {
        return Entity.motionY.getDouble(instance);
    }

    public double getMotionZ() {
        return Entity.motionZ.getDouble(instance);
    }

    @ResolveField(value = "posX")
    private static FieldDefinition posX;

    public double getPosX() {
        return Entity.posX.getDouble(instance);
    }

    public void setPosX(double posX) {
        Entity.posX.set(instance, posX);
    }

    @ResolveField(value = "posY")
    public static FieldDefinition posY;

    public double getPosY() {
        return Entity.posY.getDouble(instance);
    }

    public void setPosY(double posY) {
        Entity.posY.set(instance, posY);
    }

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

    public float getFallDistance(){
        return Entity.fallDistance.getFloat(this.getInstanceBehind());
    }
}
