package com.chappelle.jcraft.blocks;

import com.chappelle.jcraft.blocks.shapes.*;
import com.chappelle.jcraft.util.physics.AABB;
import com.jme3.math.ColorRGBA;

public class Blocks
{
	private static int nextId = 1;
	
	public static final Block grass = new Block(nextId++, new Skin[] { new Skin(0, 0, false), new Skin(0, 2, false),new Skin(0, 3, false)}).setStepSound(SoundConstants.DIG_GRASS_2);
	public static final Block glass = new Block(nextId++, new Skin[] { new Skin(3, 1, true)}).setStepSound(SoundConstants.STEP_STONE_1).setTransparent(true);
	public static final Block door = new BlockDoor(nextId++, true).setStepSound(SoundConstants.STEP_WOOD_1);
	public static final Block torch = new BlockTorch(nextId++).setLightValue(14);
	public static final Block cobbleStone = new Block(nextId++, 1, 0).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block ice = new Block(nextId++, new Skin[]{new Skin(4, 3, true)}).setStepSound(SoundConstants.STEP_STONE_4).setTransparent(true).setSlipperiness(0.98f);
	public static final Block ladder = new BlockLadder(nextId++).setStepSound(SoundConstants.STEP_WOOD_4);
	public static final Block glowstone = new Block(nextId++, 6, 9).setLightValue(15).setStepSound(SoundConstants.STEP_STONE_3);
	public static final Block sand = new Block(nextId++, 1, 2).setStepSound(SoundConstants.DIG_SAND_2);
	public static final Block coal = new Block(nextId++, 2, 2);
	public static final Block gravel = new Block(nextId++, 1, 3);
	public static final Block diamond = new Block(nextId++, 3, 2);
	public static final Block bedrock = new Block(nextId++, 1, 1).setBreakable(false);
	public static final Block gold = new Block(nextId++, 2, 0);

	public static final Block plantRed = new BlockFlower(nextId++, 12, 0).setShapes(new BlockShape_Flower()).bounds(AABB.fromWidthAndHeight(0.25, 0.4)).setTransparent(true);
	public static final Block plantYellow = new BlockFlower(nextId++, 13, 0).setShapes(new BlockShape_Flower()).bounds(AABB.fromWidthAndHeight(0.25, 0.4)).setTransparent(true);
	public static final Block mushroomBrown = new BlockFlower(nextId++, 13, 1).setShapes(new BlockShape_Flower()).bounds(AABB.fromWidthAndHeight(0.25, 0.4)).setTransparent(true);
	public static final Block mushroomRed = new BlockFlower(nextId++, 12, 1).setShapes(new BlockShape_Flower()).bounds(AABB.fromWidthAndHeight(0.25, 0.4)).setTransparent(true);
	public static final Block water = new Block(nextId++, new Skin[] { new Skin(12, 13, true) }).setTransparent(true).setReplacementAllowed(true).setLiquid(true).setCollidable(false).setSelectable(false).setBlockedSkylight(1).setShapes(new BlockShape_Cuboid(new float[]{0.4f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f}));
	public static final Block woolWhite = new Block(nextId++, 4, 0);
	public static final Block woolBlack = new Block(nextId++, 7, 1);
	public static final Block woolRed = new Block(nextId++, 8, 1);
	public static final Block woolGreen = new Block(nextId++, 9, 1);
	public static final Block woolBrown = new Block(nextId++, 10, 1);
	public static final Block woolBlue = new Block(nextId++, 11, 1);
	public static final Block woolPurple = new Block(nextId++, 12, 1);
	public static final Block woolCyan = new Block(nextId++, 13, 1);
	public static final Block woolSilver = new Block(nextId++, 14, 1);
	public static final Block woolGray = new Block(nextId++, 7, 2);
	public static final Block woolPink = new Block(nextId++, 8, 2);
	public static final Block woolLime = new Block(nextId++, 9, 2);
	public static final Block woolYellow = new Block(nextId++, 10, 2);
	public static final Block woolLightBlue = new Block(nextId++, 11, 2);
	public static final Block woolMagenta = new Block(nextId++, 12, 2);
	public static final Block woolOrange = new Block(nextId++, 13, 2);
	public static final Block smoothStone = new Block(nextId++, 0, 1).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block redstone = new Block(nextId++, 3, 3).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block iron = new Block(nextId++, 2, 1).setStepSound(SoundConstants.STEP_STONE_1);
	public static final Block wood = new Block(nextId++, new Skin[] { new Skin(1, 5, false), new Skin(1, 5, false), new Skin(1, 4, false)}).setStepSound(SoundConstants.STEP_WOOD_1);
	public static final Block leaves = new Block(nextId++, new Skin[] { new Skin(8, 4, true) }).setStepSound(SoundConstants.STEP_GRASS_1).setTransparent(true).setBlockedSkylight(2);
	public static final Block tallGrass = new BlockFlower(nextId++, 7, 2).setShapes(new BlockShape_Flower()).bounds(AABB.fromWidthAndHeight(0.25, 0.75)).setTransparent(true).setColor(new ColorRGBA(1, 0, 0, 0));
	public static final Block snow = new Block(nextId++, new Skin[]{
			new Skin(4, 2, false),
			new Skin(0, 2, false),
			new Skin(4, 4, false),
			});
	public static final Block cactus = new Block(nextId++, new Skin[]{
			new Skin(4, 5, 0.1f, 0.1f, true),
			new Skin(4, 5, 0.1f, 0.1f, true),
			new Skin(4, 6, 0.1f, 0.0f, true),
	}).setShapes(new BlockShape_Cuboid(new float[] { 0.5f, 0.5f, 0.35f, 0.35f, 0.35f, 0.35f })).setTransparent(true).setUseNeighborLight(false).bounds(AABB.getBoundingBox(0.15f, 0, 0.15f, 0.85f, 1, 0.85f));
	public static final Block dirt = new Block(nextId++, 0, 2).setStepSound(SoundConstants.DIG_GRASS_2);
}
