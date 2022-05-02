package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NbtPathArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "foo.bar", "foo[0]", "[0]", "[]", "{foo=bar}"});
   public static final SimpleCommandExceptionType ERROR_INVALID_NODE = new SimpleCommandExceptionType(new TranslatableComponent("arguments.nbtpath.node.invalid", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_NOTHING_FOUND = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.nbtpath.nothing_found", new Object[]{object});
   });

   public static NbtPathArgument nbtPath() {
      return new NbtPathArgument();
   }

   public static NbtPathArgument.NbtPath getPath(CommandContext commandContext, String string) {
      return (NbtPathArgument.NbtPath)commandContext.getArgument(string, NbtPathArgument.NbtPath.class);
   }

   public NbtPathArgument.NbtPath parse(StringReader stringReader) throws CommandSyntaxException {
      List<NbtPathArgument.Node> var2 = Lists.newArrayList();
      int var3 = stringReader.getCursor();
      Object2IntMap<NbtPathArgument.Node> var4 = new Object2IntOpenHashMap();
      boolean var5 = true;

      while(stringReader.canRead() && stringReader.peek() != 32) {
         NbtPathArgument.Node var6 = parseNode(stringReader, var5);
         var2.add(var6);
         var4.put(var6, stringReader.getCursor() - var3);
         var5 = false;
         if(stringReader.canRead()) {
            char var7 = stringReader.peek();
            if(var7 != 32 && var7 != 91 && var7 != 123) {
               stringReader.expect('.');
            }
         }
      }

      return new NbtPathArgument.NbtPath(stringReader.getString().substring(var3, stringReader.getCursor()), (NbtPathArgument.Node[])var2.toArray(new NbtPathArgument.Node[0]), var4);
   }

   private static NbtPathArgument.Node parseNode(StringReader stringReader, boolean var1) throws CommandSyntaxException {
      switch(stringReader.peek()) {
      case '\"':
         String var2 = stringReader.readString();
         return readObjectNode(stringReader, var2);
      case '[':
         stringReader.skip();
         int var2 = stringReader.peek();
         if(var2 == 123) {
            CompoundTag var3 = (new TagParser(stringReader)).readStruct();
            stringReader.expect(']');
            return new NbtPathArgument.MatchElementNode(var3);
         } else {
            if(var2 == 93) {
               stringReader.skip();
               return NbtPathArgument.AllElementsNode.INSTANCE;
            }

            int var3 = stringReader.readInt();
            stringReader.expect(']');
            return new NbtPathArgument.IndexedElementNode(var3);
         }
      case '{':
         if(!var1) {
            throw ERROR_INVALID_NODE.createWithContext(stringReader);
         }

         CompoundTag var2 = (new TagParser(stringReader)).readStruct();
         return new NbtPathArgument.MatchRootObjectNode(var2);
      default:
         String var2 = readUnquotedName(stringReader);
         return readObjectNode(stringReader, var2);
      }
   }

   private static NbtPathArgument.Node readObjectNode(StringReader stringReader, String string) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 123) {
         CompoundTag var2 = (new TagParser(stringReader)).readStruct();
         return new NbtPathArgument.MatchObjectNode(string, var2);
      } else {
         return new NbtPathArgument.CompoundChildNode(string);
      }
   }

   private static String readUnquotedName(StringReader stringReader) throws CommandSyntaxException {
      int var1 = stringReader.getCursor();

      while(stringReader.canRead() && isAllowedInUnquotedName(stringReader.peek())) {
         stringReader.skip();
      }

      if(stringReader.getCursor() == var1) {
         throw ERROR_INVALID_NODE.createWithContext(stringReader);
      } else {
         return stringReader.getString().substring(var1, stringReader.getCursor());
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private static boolean isAllowedInUnquotedName(char c) {
      return c != 32 && c != 34 && c != 91 && c != 93 && c != 46 && c != 123 && c != 125;
   }

   private static Predicate createTagPredicate(CompoundTag compoundTag) {
      return (tag) -> {
         return NbtUtils.compareNbt(compoundTag, tag, true);
      };
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class AllElementsNode implements NbtPathArgument.Node {
      public static final NbtPathArgument.AllElementsNode INSTANCE = new NbtPathArgument.AllElementsNode();

      public void getTag(Tag tag, List list) {
         if(tag instanceof CollectionTag) {
            list.addAll((CollectionTag)tag);
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         if(tag instanceof CollectionTag) {
            CollectionTag<?> var4 = (CollectionTag)tag;
            if(var4.isEmpty()) {
               Tag var5 = (Tag)supplier.get();
               if(var4.addTag(0, var5)) {
                  list.add(var5);
               }
            } else {
               list.addAll(var4);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         if(!(tag instanceof CollectionTag)) {
            return 0;
         } else {
            CollectionTag<?> var3 = (CollectionTag)tag;
            int var4 = var3.size();
            if(var4 == 0) {
               var3.addTag(0, (Tag)supplier.get());
               return 1;
            } else {
               Tag var5 = (Tag)supplier.get();
               Stream var10001 = var3.stream();
               var5.getClass();
               int var6 = var4 - (int)var10001.filter(var5::equals).count();
               if(var6 == 0) {
                  return 0;
               } else {
                  var3.clear();
                  if(!var3.addTag(0, var5)) {
                     return 0;
                  } else {
                     for(int var7 = 1; var7 < var4; ++var7) {
                        var3.addTag(var7, (Tag)supplier.get());
                     }

                     return var6;
                  }
               }
            }
         }
      }

      public int removeTag(Tag tag) {
         if(tag instanceof CollectionTag) {
            CollectionTag<?> var2 = (CollectionTag)tag;
            int var3 = var2.size();
            if(var3 > 0) {
               var2.clear();
               return var3;
            }
         }

         return 0;
      }
   }

   static class CompoundChildNode implements NbtPathArgument.Node {
      private final String name;

      public CompoundChildNode(String name) {
         this.name = name;
      }

      public void getTag(Tag tag, List list) {
         if(tag instanceof CompoundTag) {
            Tag tag = ((CompoundTag)tag).get(this.name);
            if(tag != null) {
               list.add(tag);
            }
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         if(tag instanceof CompoundTag) {
            CompoundTag var4 = (CompoundTag)tag;
            Tag var5;
            if(var4.contains(this.name)) {
               var5 = var4.get(this.name);
            } else {
               var5 = (Tag)supplier.get();
               var4.put(this.name, var5);
            }

            list.add(var5);
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         if(tag instanceof CompoundTag) {
            CompoundTag var3 = (CompoundTag)tag;
            Tag var4 = (Tag)supplier.get();
            Tag var5 = var3.put(this.name, var4);
            if(!var4.equals(var5)) {
               return 1;
            }
         }

         return 0;
      }

      public int removeTag(Tag tag) {
         if(tag instanceof CompoundTag) {
            CompoundTag var2 = (CompoundTag)tag;
            if(var2.contains(this.name)) {
               var2.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class IndexedElementNode implements NbtPathArgument.Node {
      private final int index;

      public IndexedElementNode(int index) {
         this.index = index;
      }

      public void getTag(Tag tag, List list) {
         if(tag instanceof CollectionTag) {
            CollectionTag<?> var3 = (CollectionTag)tag;
            int var4 = var3.size();
            int var5 = this.index < 0?var4 + this.index:this.index;
            if(0 <= var5 && var5 < var4) {
               list.add(var3.get(var5));
            }
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         this.getTag(tag, list);
      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         if(tag instanceof CollectionTag) {
            CollectionTag<?> var3 = (CollectionTag)tag;
            int var4 = var3.size();
            int var5 = this.index < 0?var4 + this.index:this.index;
            if(0 <= var5 && var5 < var4) {
               Tag var6 = (Tag)var3.get(var5);
               Tag var7 = (Tag)supplier.get();
               if(!var7.equals(var6) && var3.setTag(var5, var7)) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag tag) {
         if(tag instanceof CollectionTag) {
            CollectionTag<?> var2 = (CollectionTag)tag;
            int var3 = var2.size();
            int var4 = this.index < 0?var3 + this.index:this.index;
            if(0 <= var4 && var4 < var3) {
               var2.remove(var4);
               return 1;
            }
         }

         return 0;
      }
   }

   static class MatchElementNode implements NbtPathArgument.Node {
      private final CompoundTag pattern;
      private final Predicate predicate;

      public MatchElementNode(CompoundTag pattern) {
         this.pattern = pattern;
         this.predicate = NbtPathArgument.createTagPredicate(pattern);
      }

      public void getTag(Tag tag, List list) {
         if(tag instanceof ListTag) {
            ListTag var3 = (ListTag)tag;
            var3.stream().filter(this.predicate).forEach(list::add);
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         MutableBoolean var4 = new MutableBoolean();
         if(tag instanceof ListTag) {
            ListTag var5 = (ListTag)tag;
            var5.stream().filter(this.predicate).forEach((tag) -> {
               list.add(tag);
               var4.setTrue();
            });
            if(var4.isFalse()) {
               CompoundTag var6 = this.pattern.copy();
               var5.add(var6);
               list.add(var6);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new ListTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         int var3 = 0;
         if(tag instanceof ListTag) {
            ListTag var4 = (ListTag)tag;
            int var5 = var4.size();
            if(var5 == 0) {
               var4.add(supplier.get());
               ++var3;
            } else {
               for(int var6 = 0; var6 < var5; ++var6) {
                  Tag var7 = var4.get(var6);
                  if(this.predicate.test(var7)) {
                     Tag var8 = (Tag)supplier.get();
                     if(!var8.equals(var7) && var4.setTag(var6, var8)) {
                        ++var3;
                     }
                  }
               }
            }
         }

         return var3;
      }

      public int removeTag(Tag tag) {
         int var2 = 0;
         if(tag instanceof ListTag) {
            ListTag var3 = (ListTag)tag;

            for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
               if(this.predicate.test(var3.get(var4))) {
                  var3.remove(var4);
                  ++var2;
               }
            }
         }

         return var2;
      }
   }

   static class MatchObjectNode implements NbtPathArgument.Node {
      private final String name;
      private final CompoundTag pattern;
      private final Predicate predicate;

      public MatchObjectNode(String name, CompoundTag pattern) {
         this.name = name;
         this.pattern = pattern;
         this.predicate = NbtPathArgument.createTagPredicate(pattern);
      }

      public void getTag(Tag tag, List list) {
         if(tag instanceof CompoundTag) {
            Tag tag = ((CompoundTag)tag).get(this.name);
            if(this.predicate.test(tag)) {
               list.add(tag);
            }
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         if(tag instanceof CompoundTag) {
            CompoundTag var4 = (CompoundTag)tag;
            Tag var5 = var4.get(this.name);
            if(var5 == null) {
               CompoundTag var6 = this.pattern.copy();
               var4.put(this.name, var6);
               list.add(var6);
            } else if(this.predicate.test(var5)) {
               list.add(var5);
            }
         }

      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         if(tag instanceof CompoundTag) {
            CompoundTag var3 = (CompoundTag)tag;
            Tag var4 = var3.get(this.name);
            if(this.predicate.test(var4)) {
               Tag var5 = (Tag)supplier.get();
               if(!var5.equals(var4)) {
                  var3.put(this.name, var5);
                  return 1;
               }
            }
         }

         return 0;
      }

      public int removeTag(Tag tag) {
         if(tag instanceof CompoundTag) {
            CompoundTag var2 = (CompoundTag)tag;
            Tag var3 = var2.get(this.name);
            if(this.predicate.test(var3)) {
               var2.remove(this.name);
               return 1;
            }
         }

         return 0;
      }
   }

   static class MatchRootObjectNode implements NbtPathArgument.Node {
      private final Predicate predicate;

      public MatchRootObjectNode(CompoundTag compoundTag) {
         this.predicate = NbtPathArgument.createTagPredicate(compoundTag);
      }

      public void getTag(Tag tag, List list) {
         if(tag instanceof CompoundTag && this.predicate.test(tag)) {
            list.add(tag);
         }

      }

      public void getOrCreateTag(Tag tag, Supplier supplier, List list) {
         this.getTag(tag, list);
      }

      public Tag createPreferredParentTag() {
         return new CompoundTag();
      }

      public int setTag(Tag tag, Supplier supplier) {
         return 0;
      }

      public int removeTag(Tag tag) {
         return 0;
      }
   }

   public static class NbtPath {
      private final String original;
      private final Object2IntMap nodeToOriginalPosition;
      private final NbtPathArgument.Node[] nodes;

      public NbtPath(String original, NbtPathArgument.Node[] nodes, Object2IntMap nodeToOriginalPosition) {
         this.original = original;
         this.nodes = nodes;
         this.nodeToOriginalPosition = nodeToOriginalPosition;
      }

      public List get(Tag tag) throws CommandSyntaxException {
         List<Tag> list = Collections.singletonList(tag);

         for(NbtPathArgument.Node var6 : this.nodes) {
            list = var6.get(list);
            if(list.isEmpty()) {
               throw this.createNotFoundException(var6);
            }
         }

         return list;
      }

      public int countMatching(Tag tag) {
         List<Tag> var2 = Collections.singletonList(tag);

         for(NbtPathArgument.Node var6 : this.nodes) {
            var2 = var6.get(var2);
            if(var2.isEmpty()) {
               return 0;
            }
         }

         return var2.size();
      }

      private List getOrCreateParents(Tag tag) throws CommandSyntaxException {
         List<Tag> list = Collections.singletonList(tag);

         for(int var3 = 0; var3 < this.nodes.length - 1; ++var3) {
            NbtPathArgument.Node var4 = this.nodes[var3];
            int var5 = var3 + 1;
            NbtPathArgument.Node var10002 = this.nodes[var5];
            this.nodes[var5].getClass();
            list = var4.getOrCreate(list, var10002::createPreferredParentTag);
            if(list.isEmpty()) {
               throw this.createNotFoundException(var4);
            }
         }

         return list;
      }

      public List getOrCreate(Tag tag, Supplier supplier) throws CommandSyntaxException {
         List<Tag> list = this.getOrCreateParents(tag);
         NbtPathArgument.Node var4 = this.nodes[this.nodes.length - 1];
         return var4.getOrCreate(list, supplier);
      }

      private static int apply(List list, Function function) {
         return ((Integer)list.stream().map(function).reduce(Integer.valueOf(0), (var0, var1) -> {
            return Integer.valueOf(var0.intValue() + var1.intValue());
         })).intValue();
      }

      public int set(Tag tag, Supplier supplier) throws CommandSyntaxException {
         List<Tag> var3 = this.getOrCreateParents(tag);
         NbtPathArgument.Node var4 = this.nodes[this.nodes.length - 1];
         return apply(var3, (tag) -> {
            return Integer.valueOf(var4.setTag(tag, supplier));
         });
      }

      public int remove(Tag tag) {
         List<Tag> var2 = Collections.singletonList(tag);

         for(int var3 = 0; var3 < this.nodes.length - 1; ++var3) {
            var2 = this.nodes[var3].get(var2);
         }

         NbtPathArgument.Node var3 = this.nodes[this.nodes.length - 1];
         var3.getClass();
         return apply(var2, var3::removeTag);
      }

      private CommandSyntaxException createNotFoundException(NbtPathArgument.Node nbtPathArgument$Node) {
         int var2 = this.nodeToOriginalPosition.getInt(nbtPathArgument$Node);
         return NbtPathArgument.ERROR_NOTHING_FOUND.create(this.original.substring(0, var2));
      }

      public String toString() {
         return this.original;
      }
   }

   interface Node {
      void getTag(Tag var1, List var2);

      void getOrCreateTag(Tag var1, Supplier var2, List var3);

      Tag createPreferredParentTag();

      int setTag(Tag var1, Supplier var2);

      int removeTag(Tag var1);

      default List get(List list) {
         return this.collect(list, this::getTag);
      }

      default List getOrCreate(List var1, Supplier supplier) {
         return this.collect(var1, (tag, list) -> {
            this.getOrCreateTag(tag, supplier, list);
         });
      }

      default List collect(List var1, BiConsumer biConsumer) {
         List<Tag> var3 = Lists.newArrayList();

         for(Tag var5 : var1) {
            biConsumer.accept(var5, var3);
         }

         return var3;
      }
   }
}
