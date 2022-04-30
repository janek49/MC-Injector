package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/world/phys/Vec3")
public class Vec3 extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = {"D", "D", "D"})
    public static ConstructorDefinition constructor;

    public Vec3(Object instance) {
        super(instance);
    }

    public Vec3(double x, double y, double z) {
        super(Vec3.constructor.newInstance(x, y, z));
    }

    private Vec3() {
    }

    @ResolveField("x")
    private static FieldDefinition x;
    @ResolveField("y")
    private static FieldDefinition y;
    @ResolveField("z")
    private static FieldDefinition z;

    public double getX() {
        return x.getDouble(getInstanceBehind());
    }

    public double getY() {
        return y.getDouble(getInstanceBehind());
    }

    public double getZ() {
        return z.getDouble(getInstanceBehind());
    }
}
