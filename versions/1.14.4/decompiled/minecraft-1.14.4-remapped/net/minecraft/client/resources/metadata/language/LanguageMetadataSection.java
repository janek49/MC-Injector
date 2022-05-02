package net.minecraft.client.resources.metadata.language;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Collection;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSectionSerializer;

@ClientJarOnly
public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer SERIALIZER = new LanguageMetadataSectionSerializer();
   private final Collection languages;

   public LanguageMetadataSection(Collection languages) {
      this.languages = languages;
   }

   public Collection getLanguages() {
      return this.languages;
   }
}
