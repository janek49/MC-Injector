package net.minecraft.client.model.geom;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.Cube;

@ClientJarOnly
public class ModelPart {
   public float xTexSize;
   public float yTexSize;
   private int xTexOffs;
   private int yTexOffs;
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   private boolean compiled;
   private int list;
   public boolean mirror;
   public boolean visible;
   public boolean neverRender;
   public final List cubes;
   public List children;
   public final String id;
   public float translateX;
   public float translateY;
   public float translateZ;

   public ModelPart(Model model, String id) {
      this.xTexSize = 64.0F;
      this.yTexSize = 32.0F;
      this.visible = true;
      this.cubes = Lists.newArrayList();
      model.cubes.add(this);
      this.id = id;
      this.setTexSize(model.texWidth, model.texHeight);
   }

   public ModelPart(Model model) {
      this(model, (String)null);
   }

   public ModelPart(Model model, int var2, int var3) {
      this(model);
      this.texOffs(var2, var3);
   }

   public void copyFrom(ModelPart modelPart) {
      this.xRot = modelPart.xRot;
      this.yRot = modelPart.yRot;
      this.zRot = modelPart.zRot;
      this.x = modelPart.x;
      this.y = modelPart.y;
      this.z = modelPart.z;
   }

   public void addChild(ModelPart modelPart) {
      if(this.children == null) {
         this.children = Lists.newArrayList();
      }

      this.children.add(modelPart);
   }

   public void removeChild(ModelPart modelPart) {
      if(this.children != null) {
         this.children.remove(modelPart);
      }

   }

   public ModelPart texOffs(int xTexOffs, int yTexOffs) {
      this.xTexOffs = xTexOffs;
      this.yTexOffs = yTexOffs;
      return this;
   }

   public ModelPart addBox(String string, float var2, float var3, float var4, int var5, int var6, int var7, float var8, int var9, int var10) {
      string = this.id + "." + string;
      this.texOffs(var9, var10);
      this.cubes.add((new Cube(this, this.xTexOffs, this.yTexOffs, var2, var3, var4, var5, var6, var7, var8)).setId(string));
      return this;
   }

   public ModelPart addBox(float var1, float var2, float var3, int var4, int var5, int var6) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, 0.0F));
      return this;
   }

   public ModelPart addBox(float var1, float var2, float var3, int var4, int var5, int var6, boolean var7) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, 0.0F, var7));
      return this;
   }

   public void addBox(float var1, float var2, float var3, int var4, int var5, int var6, float var7) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, var7));
   }

   public void addBox(float var1, float var2, float var3, int var4, int var5, int var6, float var7, boolean var8) {
      this.cubes.add(new Cube(this, this.xTexOffs, this.yTexOffs, var1, var2, var3, var4, var5, var6, var7, var8));
   }

   public void setPos(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void render(float f) {
      if(!this.neverRender) {
         if(this.visible) {
            if(!this.compiled) {
               this.compile(f);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.translateX, this.translateY, this.translateZ);
            if(this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F) {
               if(this.x == 0.0F && this.y == 0.0F && this.z == 0.0F) {
                  GlStateManager.callList(this.list);
                  if(this.children != null) {
                     for(int var2 = 0; var2 < this.children.size(); ++var2) {
                        ((ModelPart)this.children.get(var2)).render(f);
                     }
                  }
               } else {
                  GlStateManager.pushMatrix();
                  GlStateManager.translatef(this.x * f, this.y * f, this.z * f);
                  GlStateManager.callList(this.list);
                  if(this.children != null) {
                     for(int var2 = 0; var2 < this.children.size(); ++var2) {
                        ((ModelPart)this.children.get(var2)).render(f);
                     }
                  }

                  GlStateManager.popMatrix();
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.x * f, this.y * f, this.z * f);
               if(this.zRot != 0.0F) {
                  GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if(this.yRot != 0.0F) {
                  GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if(this.xRot != 0.0F) {
                  GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.list);
               if(this.children != null) {
                  for(int var2 = 0; var2 < this.children.size(); ++var2) {
                     ((ModelPart)this.children.get(var2)).render(f);
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void renderRollable(float f) {
      if(!this.neverRender) {
         if(this.visible) {
            if(!this.compiled) {
               this.compile(f);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.x * f, this.y * f, this.z * f);
            if(this.yRot != 0.0F) {
               GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if(this.xRot != 0.0F) {
               GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if(this.zRot != 0.0F) {
               GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.list);
            GlStateManager.popMatrix();
         }
      }
   }

   public void translateTo(float f) {
      if(!this.neverRender) {
         if(this.visible) {
            if(!this.compiled) {
               this.compile(f);
            }

            if(this.xRot == 0.0F && this.yRot == 0.0F && this.zRot == 0.0F) {
               if(this.x != 0.0F || this.y != 0.0F || this.z != 0.0F) {
                  GlStateManager.translatef(this.x * f, this.y * f, this.z * f);
               }
            } else {
               GlStateManager.translatef(this.x * f, this.y * f, this.z * f);
               if(this.zRot != 0.0F) {
                  GlStateManager.rotatef(this.zRot * 57.295776F, 0.0F, 0.0F, 1.0F);
               }

               if(this.yRot != 0.0F) {
                  GlStateManager.rotatef(this.yRot * 57.295776F, 0.0F, 1.0F, 0.0F);
               }

               if(this.xRot != 0.0F) {
                  GlStateManager.rotatef(this.xRot * 57.295776F, 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   private void compile(float f) {
      this.list = MemoryTracker.genLists(1);
      GlStateManager.newList(this.list, 4864);
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();

      for(int var3 = 0; var3 < this.cubes.size(); ++var3) {
         ((Cube)this.cubes.get(var3)).compile(var2, f);
      }

      GlStateManager.endList();
      this.compiled = true;
   }

   public ModelPart setTexSize(int var1, int var2) {
      this.xTexSize = (float)var1;
      this.yTexSize = (float)var2;
      return this;
   }
}
