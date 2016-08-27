package com.chappelle.jcraft;

import java.util.List;

import com.chappelle.jcraft.util.AABB;
import com.chappelle.jcraft.world.World;

public class Enemy extends Entity
{
	public Enemy(World world)
	{
		super(world);
	}
	
	@Override
	public void update(float tpf)
	{
		super.update(tpf);
		
		this.motionY *= 0.9800000190734863D;
		//At this point all movement should be done(ie. altering of the motion vectors)
		moveEntity(motionX, motionY, motionZ);
	}

	public void moveEntity(double x, double y, double z)
	{
		double orgX = x;
		double orgY = y;
		double orgZ = z;
		
		List<AABB> boundingBoxes = world.getCollidingBoundingBoxes(this, boundingBox.addCoord(x, y, z));
		for(AABB boundingBox : boundingBoxes)
		{
			y = boundingBox.calculateYOffset(this.boundingBox, y);
		}
		this.boundingBox.offset(0, y, 0);
		for(AABB boundingBox : boundingBoxes)
		{
			x = boundingBox.calculateXOffset(this.boundingBox, x);
		}
		this.boundingBox.offset(x, 0, 0);
		for(AABB boundingBox : boundingBoxes)
		{
			z = boundingBox.calculateZOffset(this.boundingBox, z);
		}
		this.boundingBox.offset(0, 0, z);

		this.onGround = orgY != y && orgY < 0.0D;
		this.isCollidedHorizontally = orgX != x || orgZ != z;
		this.isCollidedVertically = orgY != y;
		this.updateFallState(y, this.onGround);
		
		posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0f;
		posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0f;
		posY = (this.boundingBox.minY + this.yOffset - this.ySize);
		
//		updateWalkDistance();
	}

}