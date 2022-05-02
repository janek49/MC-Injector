package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.sensing.DummySensor;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.HurtBySensor;
import net.minecraft.world.entity.ai.sensing.InteractableDoorsSensor;
import net.minecraft.world.entity.ai.sensing.NearestBedSensor;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.SecondaryPoiSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;

public class SensorType {
   public static final SensorType DUMMY = register("dummy", DummySensor::<init>);
   public static final SensorType NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitySensor::<init>);
   public static final SensorType NEAREST_PLAYERS = register("nearest_players", PlayerSensor::<init>);
   public static final SensorType INTERACTABLE_DOORS = register("interactable_doors", InteractableDoorsSensor::<init>);
   public static final SensorType NEAREST_BED = register("nearest_bed", NearestBedSensor::<init>);
   public static final SensorType HURT_BY = register("hurt_by", HurtBySensor::<init>);
   public static final SensorType VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::<init>);
   public static final SensorType VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::<init>);
   public static final SensorType SECONDARY_POIS = register("secondary_pois", SecondaryPoiSensor::<init>);
   public static final SensorType GOLEM_LAST_SEEN = register("golem_last_seen", GolemSensor::<init>);
   private final Supplier factory;

   private SensorType(Supplier factory) {
      this.factory = factory;
   }

   public Sensor create() {
      return (Sensor)this.factory.get();
   }

   private static SensorType register(String string, Supplier supplier) {
      return (SensorType)Registry.register(Registry.SENSOR_TYPE, (ResourceLocation)(new ResourceLocation(string)), new SensorType(supplier));
   }
}
