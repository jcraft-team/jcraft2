package com.chappelle.jcraft.blocks;

public class Blocks
{
	public static final Block grass = new BlockGrass(1).setStepSound(SoundConstants.DIG_GRASS_2);
	public static final Block glass = new BlockGlass(2).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block door = new BlockDoor(3, true).setStepSound(SoundConstants.STEP_WOOD_1);
	public static final Block torch = new BlockTorch(4).setLightValue(14);
	public static final Block cobbleStone = new BlockStone(5, 0, 1).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block ice = new BlockIce(6).setStepSound(SoundConstants.STEP_STONE_4);
	public static final Block ladder = new BlockLadder(7).setStepSound(SoundConstants.STEP_WOOD_4);
	public static final Block glowstone = new BlockGlowstone(8).setLightValue(15).setStepSound(SoundConstants.STEP_STONE_3);
	public static final Block sand = new BlockSand(9).setStepSound(SoundConstants.DIG_SAND_2);
	public static final Block coal = new BlockCoal(10);
	public static final Block gravel = new BlockGravel(11);
	public static final Block diamond = new BlockDiamond(12);
	public static final Block bedrock = new BlockBedrock(13);
	public static final Block gold = new BlockGold(14);
	public static final Block plantRed = new BlockFlower(15, 12, 0);
	public static final Block plantYellow = new BlockFlower(16, 13, 0);
	public static final Block mushroomBrown = new BlockFlower(17, 13, 1);
	public static final Block mushroomRed = new BlockFlower(18, 12, 1);
	public static final Block water = new BlockWater(19);
	public static final Block woolWhite = new BlockWool(20, 0, 4);
	public static final Block woolBlack = new BlockWool(21, 1, 7);
	public static final Block woolRed = new BlockWool(22, 1, 8);
	public static final Block woolGreen = new BlockWool(23, 1, 9);
	public static final Block woolBrown = new BlockWool(24, 1, 10);
	public static final Block woolBlue = new BlockWool(25, 1, 11);
	public static final Block woolPurple = new BlockWool(26, 1, 12);
	public static final Block woolCyan = new BlockWool(27, 1, 13);
	public static final Block woolSilver = new BlockWool(28, 1, 14);
	public static final Block woolGray = new BlockWool(29, 2, 7);
	public static final Block woolPink = new BlockWool(30, 2, 8);
	public static final Block woolLime = new BlockWool(31, 2, 9);
	public static final Block woolYellow = new BlockWool(32, 2, 10);
	public static final Block woolLightBlue = new BlockWool(33, 2, 11);
	public static final Block woolMagenta = new BlockWool(34, 2, 12);
	public static final Block woolOrange = new BlockWool(35, 2, 13);
	public static final Block smoothStone = new BlockStone(36, 1, 0).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block redstone = new BlockRedstoneOre(37).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block iron = new BlockIronOre(38).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block wood = new BlockLog(39).setStepSound(SoundConstants.STEP_WOOD_1);
	public static final Block leaves = new BlockLeaves(40).setStepSound(SoundConstants.STEP_GRASS_1);
	public static final Block tallGrass = new BlockTallGrass(41, 7, 2);
	public static final Block snow = new BlockSnow(42);
	public static final Block cactus = new BlockSnow(43);
	public static final Block dirt = new BlockDirt(44).setStepSound(SoundConstants.DIG_GRASS_2);

}
