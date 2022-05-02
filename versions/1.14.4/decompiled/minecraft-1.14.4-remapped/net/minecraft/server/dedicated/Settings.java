package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Settings {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Properties properties;

   public Settings(Properties properties) {
      this.properties = properties;
   }

   public static Properties loadFromFile(Path path) {
      Properties properties = new Properties();

      try {
         InputStream var2 = Files.newInputStream(path, new OpenOption[0]);
         Throwable var3 = null;

         try {
            properties.load(var2);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to load properties from file: " + path);
      }

      return properties;
   }

   public void store(Path path) {
      try {
         OutputStream var2 = Files.newOutputStream(path, new OpenOption[0]);
         Throwable var3 = null;

         try {
            this.properties.store(var2, "Minecraft server properties");
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (IOException var15) {
         LOGGER.error("Failed to store properties to file: " + path);
      }

   }

   private static Function wrapNumberDeserializer(Function function) {
      return (string) -> {
         try {
            return (Number)function.apply(string);
         } catch (NumberFormatException var3) {
            return null;
         }
      };
   }

   protected static Function dispatchNumberOrString(IntFunction intFunction, Function var1) {
      return (string) -> {
         try {
            return intFunction.apply(Integer.parseInt(string));
         } catch (NumberFormatException var4) {
            return var1.apply(string);
         }
      };
   }

   @Nullable
   private String getStringRaw(String string) {
      return (String)this.properties.get(string);
   }

   @Nullable
   protected Object getLegacy(String string, Function function) {
      String string = this.getStringRaw(string);
      if(string == null) {
         return null;
      } else {
         this.properties.remove(string);
         return function.apply(string);
      }
   }

   protected Object get(String string, Function var2, Function var3, Object var4) {
      String string = this.getStringRaw(string);
      V var6 = MoreObjects.firstNonNull(string != null?var2.apply(string):null, var4);
      this.properties.put(string, var3.apply(var6));
      return var6;
   }

   protected Settings.MutableValue getMutable(String string, Function var2, Function var3, Object object) {
      String string = this.getStringRaw(string);
      V var6 = MoreObjects.firstNonNull(string != null?var2.apply(string):null, object);
      this.properties.put(string, var3.apply(var6));
      return new Settings.MutableValue(string, var6, var3);
   }

   protected Object get(String string, Function var2, UnaryOperator unaryOperator, Function var4, Object var5) {
      return this.get(string, (string) -> {
         V object = var2.apply(string);
         return object != null?unaryOperator.apply(object):null;
      }, var4, var5);
   }

   protected Object get(String string, Function function, Object var3) {
      return this.get(string, function, Objects::toString, var3);
   }

   protected Settings.MutableValue getMutable(String string, Function function, Object object) {
      return this.getMutable(string, function, Objects::toString, object);
   }

   protected String get(String var1, String var2) {
      return (String)this.get(var1, Function.identity(), Function.identity(), var2);
   }

   @Nullable
   protected String getLegacyString(String string) {
      return (String)this.getLegacy(string, Function.identity());
   }

   protected int get(String string, int var2) {
      return ((Integer)this.get(string, wrapNumberDeserializer(Integer::parseInt), Integer.valueOf(var2))).intValue();
   }

   protected Settings.MutableValue getMutable(String string, int var2) {
      return this.getMutable(string, wrapNumberDeserializer(Integer::parseInt), Integer.valueOf(var2));
   }

   protected int get(String string, UnaryOperator unaryOperator, int var3) {
      return ((Integer)this.get(string, wrapNumberDeserializer(Integer::parseInt), unaryOperator, Objects::toString, Integer.valueOf(var3))).intValue();
   }

   protected long get(String string, long var2) {
      return ((Long)this.get(string, wrapNumberDeserializer(Long::parseLong), Long.valueOf(var2))).longValue();
   }

   protected boolean get(String string, boolean var2) {
      return ((Boolean)this.get(string, Boolean::valueOf, Boolean.valueOf(var2))).booleanValue();
   }

   protected Settings.MutableValue getMutable(String string, boolean var2) {
      return this.getMutable(string, Boolean::valueOf, Boolean.valueOf(var2));
   }

   @Nullable
   protected Boolean getLegacyBoolean(String string) {
      return (Boolean)this.getLegacy(string, Boolean::valueOf);
   }

   protected Properties cloneProperties() {
      Properties properties = new Properties();
      properties.putAll(this.properties);
      return properties;
   }

   protected abstract Settings reload(Properties var1);

   public class MutableValue implements Supplier {
      private final String key;
      private final Object value;
      private final Function serializer;

      private MutableValue(String key, Object value, Function serializer) {
         this.key = key;
         this.value = value;
         this.serializer = serializer;
      }

      public Object get() {
         return this.value;
      }

      public Settings update(Object object) {
         Properties var2 = Settings.this.cloneProperties();
         var2.put(this.key, this.serializer.apply(object));
         return Settings.this.reload(var2);
      }
   }
}
