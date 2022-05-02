package net.minecraft.world.level.levelgen.feature;

import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.feature.VillagePieces;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.PillagerOutpostPieces;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public interface StructurePieceType {
   StructurePieceType MINE_SHAFT_CORRIDOR = setPieceId(MineShaftPieces.MineShaftCorridor::<init>, "MSCorridor");
   StructurePieceType MINE_SHAFT_CROSSING = setPieceId(MineShaftPieces.MineShaftCrossing::<init>, "MSCrossing");
   StructurePieceType MINE_SHAFT_ROOM = setPieceId(MineShaftPieces.MineShaftRoom::<init>, "MSRoom");
   StructurePieceType MINE_SHAFT_STAIRS = setPieceId(MineShaftPieces.MineShaftStairs::<init>, "MSStairs");
   StructurePieceType PILLAGER_OUTPOST = setPieceId(PillagerOutpostPieces.PillagerOutpostPiece::<init>, "PCP");
   StructurePieceType VILLAGE = setPieceId(VillagePieces.VillagePiece::<init>, "NVi");
   StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = setPieceId(NetherBridgePieces.BridgeCrossing::<init>, "NeBCr");
   StructurePieceType NETHER_FORTRESS_BRIDGE_END_FILLER = setPieceId(NetherBridgePieces.BridgeEndFiller::<init>, "NeBEF");
   StructurePieceType NETHER_FORTRESS_BRIDGE_STRAIGHT = setPieceId(NetherBridgePieces.BridgeStraight::<init>, "NeBS");
   StructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS = setPieceId(NetherBridgePieces.CastleCorridorStairsPiece::<init>, "NeCCS");
   StructurePieceType NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY = setPieceId(NetherBridgePieces.CastleCorridorTBalconyPiece::<init>, "NeCTB");
   StructurePieceType NETHER_FORTRESS_CASTLE_ENTRANCE = setPieceId(NetherBridgePieces.CastleEntrance::<init>, "NeCE");
   StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING = setPieceId(NetherBridgePieces.CastleSmallCorridorCrossingPiece::<init>, "NeSCSC");
   StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN = setPieceId(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece::<init>, "NeSCLT");
   StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR = setPieceId(NetherBridgePieces.CastleSmallCorridorPiece::<init>, "NeSC");
   StructurePieceType NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = setPieceId(NetherBridgePieces.CastleSmallCorridorRightTurnPiece::<init>, "NeSCRT");
   StructurePieceType NETHER_FORTRESS_CASTLE_STALK_ROOM = setPieceId(NetherBridgePieces.CastleStalkRoom::<init>, "NeCSR");
   StructurePieceType NETHER_FORTRESS_MONSTER_THRONE = setPieceId(NetherBridgePieces.MonsterThrone::<init>, "NeMT");
   StructurePieceType NETHER_FORTRESS_ROOM_CROSSING = setPieceId(NetherBridgePieces.RoomCrossing::<init>, "NeRC");
   StructurePieceType NETHER_FORTRESS_STAIRS_ROOM = setPieceId(NetherBridgePieces.StairsRoom::<init>, "NeSR");
   StructurePieceType NETHER_FORTRESS_START = setPieceId(NetherBridgePieces.StartPiece::<init>, "NeStart");
   StructurePieceType STRONGHOLD_CHEST_CORRIDOR = setPieceId(StrongholdPieces.ChestCorridor::<init>, "SHCC");
   StructurePieceType STRONGHOLD_FILLER_CORRIDOR = setPieceId(StrongholdPieces.FillerCorridor::<init>, "SHFC");
   StructurePieceType STRONGHOLD_FIVE_CROSSING = setPieceId(StrongholdPieces.FiveCrossing::<init>, "SH5C");
   StructurePieceType STRONGHOLD_LEFT_TURN = setPieceId(StrongholdPieces.LeftTurn::<init>, "SHLT");
   StructurePieceType STRONGHOLD_LIBRARY = setPieceId(StrongholdPieces.Library::<init>, "SHLi");
   StructurePieceType STRONGHOLD_PORTAL_ROOM = setPieceId(StrongholdPieces.PortalRoom::<init>, "SHPR");
   StructurePieceType STRONGHOLD_PRISON_HALL = setPieceId(StrongholdPieces.PrisonHall::<init>, "SHPH");
   StructurePieceType STRONGHOLD_RIGHT_TURN = setPieceId(StrongholdPieces.RightTurn::<init>, "SHRT");
   StructurePieceType STRONGHOLD_ROOM_CROSSING = setPieceId(StrongholdPieces.RoomCrossing::<init>, "SHRC");
   StructurePieceType STRONGHOLD_STAIRS_DOWN = setPieceId(StrongholdPieces.StairsDown::<init>, "SHSD");
   StructurePieceType STRONGHOLD_START = setPieceId(StrongholdPieces.StartPiece::<init>, "SHStart");
   StructurePieceType STRONGHOLD_STRAIGHT = setPieceId(StrongholdPieces.Straight::<init>, "SHS");
   StructurePieceType STRONGHOLD_STRAIGHT_STAIRS_DOWN = setPieceId(StrongholdPieces.StraightStairsDown::<init>, "SHSSD");
   StructurePieceType JUNGLE_PYRAMID_PIECE = setPieceId(JunglePyramidPiece::<init>, "TeJP");
   StructurePieceType OCEAN_RUIN = setPieceId(OceanRuinPieces.OceanRuinPiece::<init>, "ORP");
   StructurePieceType IGLOO = setPieceId(IglooPieces.IglooPiece::<init>, "Iglu");
   StructurePieceType SWAMPLAND_HUT = setPieceId(SwamplandHutPiece::<init>, "TeSH");
   StructurePieceType DESERT_PYRAMID_PIECE = setPieceId(DesertPyramidPiece::<init>, "TeDP");
   StructurePieceType OCEAN_MONUMENT_BUILDING = setPieceId(OceanMonumentPieces.MonumentBuilding::<init>, "OMB");
   StructurePieceType OCEAN_MONUMENT_CORE_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentCoreRoom::<init>, "OMCR");
   StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentDoubleXRoom::<init>, "OMDXR");
   StructurePieceType OCEAN_MONUMENT_DOUBLE_XY_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentDoubleXYRoom::<init>, "OMDXYR");
   StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentDoubleYRoom::<init>, "OMDYR");
   StructurePieceType OCEAN_MONUMENT_DOUBLE_YZ_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentDoubleYZRoom::<init>, "OMDYZR");
   StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentDoubleZRoom::<init>, "OMDZR");
   StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentEntryRoom::<init>, "OMEntry");
   StructurePieceType OCEAN_MONUMENT_PENTHOUSE = setPieceId(OceanMonumentPieces.OceanMonumentPenthouse::<init>, "OMPenthouse");
   StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentSimpleRoom::<init>, "OMSimple");
   StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentSimpleTopRoom::<init>, "OMSimpleT");
   StructurePieceType OCEAN_MONUMENT_WING_ROOM = setPieceId(OceanMonumentPieces.OceanMonumentWingRoom::<init>, "OMWR");
   StructurePieceType END_CITY_PIECE = setPieceId(EndCityPieces.EndCityPiece::<init>, "ECP");
   StructurePieceType WOODLAND_MANSION_PIECE = setPieceId(WoodlandMansionPieces.WoodlandMansionPiece::<init>, "WMP");
   StructurePieceType BURIED_TREASURE_PIECE = setPieceId(BuriedTreasurePieces.BuriedTreasurePiece::<init>, "BTP");
   StructurePieceType SHIPWRECK_PIECE = setPieceId(ShipwreckPieces.ShipwreckPiece::<init>, "Shipwreck");

   StructurePiece load(StructureManager var1, CompoundTag var2);

   static default StructurePieceType setPieceId(StructurePieceType var0, String string) {
      return (StructurePieceType)Registry.register(Registry.STRUCTURE_PIECE, (String)string.toLowerCase(Locale.ROOT), var0);
   }
}
