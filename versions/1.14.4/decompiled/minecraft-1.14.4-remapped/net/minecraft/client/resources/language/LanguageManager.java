package net.minecraft.client.resources.language;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.Language;
import net.minecraft.client.resources.language.Locale;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class LanguageManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Locale LOCALE = new Locale();
   private String currentCode;
   private final Map languages = Maps.newHashMap();

   public LanguageManager(String currentCode) {
      this.currentCode = currentCode;
      I18n.setLocale(LOCALE);
   }

   public void reload(List list) {
      this.languages.clear();

      for(Pack var3 : list) {
         try {
            LanguageMetadataSection var4 = (LanguageMetadataSection)var3.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if(var4 != null) {
               for(Language var6 : var4.getLanguages()) {
                  if(!this.languages.containsKey(var6.getCode())) {
                     this.languages.put(var6.getCode(), var6);
                  }
               }
            }
         } catch (IOException | RuntimeException var7) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", var3.getName(), var7);
         }
      }

   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      List<String> var2 = Lists.newArrayList(new String[]{"en_us"});
      if(!"en_us".equals(this.currentCode)) {
         var2.add(this.currentCode);
      }

      LOCALE.loadFrom(resourceManager, var2);
      net.minecraft.locale.Language.forceData(LOCALE.storage);
   }

   public boolean isBidirectional() {
      return this.getSelected() != null && this.getSelected().isBidirectional();
   }

   public void setSelected(Language selected) {
      this.currentCode = selected.getCode();
   }

   public Language getSelected() {
      String var1 = this.languages.containsKey(this.currentCode)?this.currentCode:"en_us";
      return (Language)this.languages.get(var1);
   }

   public SortedSet getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }

   public Language getLanguage(String string) {
      return (Language)this.languages.get(string);
   }
}
