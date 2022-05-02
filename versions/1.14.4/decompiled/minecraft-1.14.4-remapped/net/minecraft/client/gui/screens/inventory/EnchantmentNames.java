package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.Font;
import org.apache.commons.lang3.StringUtils;

@ClientJarOnly
public class EnchantmentNames {
   private static final EnchantmentNames INSTANCE = new EnchantmentNames();
   private final Random random = new Random();
   private final String[] words = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale phnglui mglwnafh cthulhu rlyeh wgahnagl fhtagnbaguette".split(" ");

   public static EnchantmentNames getInstance() {
      return INSTANCE;
   }

   public String getRandomName(Font font, int var2) {
      int var3 = this.random.nextInt(2) + 3;
      String var4 = "";

      for(int var5 = 0; var5 < var3; ++var5) {
         if(var5 > 0) {
            var4 = var4 + " ";
         }

         var4 = var4 + this.words[this.random.nextInt(this.words.length)];
      }

      List<String> var5 = font.split(var4, var2);
      return StringUtils.join(var5.size() >= 2?var5.subList(0, 2):var5, " ");
   }

   public void initSeed(long l) {
      this.random.setSeed(l);
   }
}
