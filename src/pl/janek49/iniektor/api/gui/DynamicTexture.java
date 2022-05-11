package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@ClassImitator.ResolveClass("net/minecraft/client/renderer/texture/DynamicTexture")
public class DynamicTexture extends ClassImitator {
    public static ClassInformation target;

    public DynamicTexture(Object instance){
        super(instance);
    }

    private DynamicTexture(){}

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, params = "com/mojang/blaze3d/platform/NativeImage")
    @ResolveConstructor(params = "java/awt/image/BufferedImage")
    public static ConstructorDefinition constructor;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "com/mojang/blaze3d/platform/NativeImage/read",
            descriptor = "(Ljava/io/InputStream;)Lcom/mojang/blaze3d/platform/NativeImage;")
    public static MethodDefinition NativeImage_read;

    public static DynamicTexture fromInputStream(InputStream is) {
        if (Reflector.USE_NEW_API) {
            return new DynamicTexture(constructor.newInstance(NativeImage_read.invokeSt(is)));
        } else {
            return new DynamicTexture(constructor.newInstance(readImage(is)));
        }
    }

    public static BufferedImage readImage(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (Exception ex) {
            Logger.ex(ex);
        }
        return null;
    }
}
