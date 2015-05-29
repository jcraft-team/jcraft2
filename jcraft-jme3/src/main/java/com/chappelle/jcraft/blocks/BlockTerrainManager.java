package com.chappelle.jcraft.blocks;

import java.util.ArrayList;
import java.util.List;

import com.chappelle.jcraft.JCraft;
import com.cubes.Block;
import com.cubes.BlockChunkControl;
import com.cubes.BlockNavigator;
import com.cubes.BlockState;
import com.cubes.BlockTerrainControl;
import com.cubes.BlockTerrain_LocalBlockState;
import com.cubes.Chunk;
import com.cubes.CubesSettings;
import com.cubes.Direction;
import com.cubes.Vector3Int;
import com.cubes.World;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class BlockTerrainManager extends AbstractAppState implements World
{
	private CubesSettings cubeSettings;
    private BlockTerrainControl terrain;
    private Node node = new Node("active blocks");
    private List<FallingBlocks> fallingBlocksList = new ArrayList<FallingBlocks>();
    private JCraft app;

    public BlockTerrainManager(CubesSettings cubeSettings, BlockTerrainControl terrain)
    {
        this.terrain = terrain;
        this.cubeSettings = cubeSettings;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);

        this.app = (JCraft) app;
        this.app.getRootNode().attachChild(node);
    }

    private Vector3Int toBlockCenter(Vector3Int blockLocation)
    {
        return blockLocation.mult((int) cubeSettings.getBlockSize());
    }

    public void actOnBlock(PickedBlock pickedBlock)
    {
    	Block block = pickedBlock.getBlock();
    	block.onAction(pickedBlock);
    }
    
    public void removeBlock(PickedBlock pickedBlock)
    {
    	Block block = pickedBlock.getBlock();
    	Vector3Int location = pickedBlock.getBlockLocation();
    	removeBlock(block, location);
    }
    
    public void removeBlock(Block block, Vector3Int location)
    {
        if (block.isRemovable())
        {
            terrain.removeBlock(location);
            checkForFallingBlocks(location);

            //Notify neighbors of block removal
            for (Block.Face face : Block.Face.values())
            {
                Vector3Int neighborLocation = BlockNavigator.getNeighborBlockLocalLocation(location, face);
                Block neighbor = terrain.getBlock(neighborLocation);
                if (neighbor != null)
                {
                    neighbor.onNeighborRemoved(location, neighborLocation);
                }
            }

        }
    }

    public BlockTerrainControl getTerrain()
    {
        return terrain;
    }
    
    public void setBlock(PickedBlock pickedBlock, Block blockToPlace)
    {
        pickedBlock.setBlock(blockToPlace);//Kind of a hack, the pickedBlock has a null block at this point

        Vector3Int location = pickedBlock.getBlockLocation();
        Block.Face face = Block.Face.fromNormal(pickedBlock.getContactNormal());
        if (blockToPlace.isValidPlacementFace(face))
        {
            Block bottomBlock = terrain.getBlock(location.subtract(0, 1, 0));
            if (blockToPlace.isAffectedByGravity() && bottomBlock == null)
            {
                Geometry geometry = blockToPlace.makeBlockGeometry();
                if(geometry != null)
                {
                	geometry.setName("active block");
                	Vector3f placementLocation = location.mult((int) cubeSettings.getBlockSize()).toVector3f();
                	geometry.setLocalTranslation(placementLocation);
                	node.attachChild(geometry);
                	
                	FallingBlocks fallingBlocks = new FallingBlocks(getFloorGeometry(location), location);
                	fallingBlocks.add(blockToPlace, geometry);
                	fallingBlocksList.add(fallingBlocks);
                }
            }
            else
            {
                terrain.setBlock(location, blockToPlace);
                blockToPlace.onBlockPlaced(location, pickedBlock.getContactNormal(), getCameraDirectionAsUnitVector(pickedBlock.getCameraDirection()));
            }
        }
    }
    
    private Vector3f getCameraDirectionAsUnitVector(Vector3f cameraDirection)
    {
    	cameraDirection = cameraDirection.normalize();
		float xPos = cameraDirection.angleBetween(Vector3f.UNIT_X);
    	float xNeg = cameraDirection.angleBetween(Vector3f.UNIT_X.negate());
    	float zPos = cameraDirection.angleBetween(Vector3f.UNIT_Z);
    	float zNeg = cameraDirection.angleBetween(Vector3f.UNIT_Z.negate());
    	if(isFirstArgMin(xPos, xNeg, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X;
    	}
    	else if(isFirstArgMin(xNeg, xPos, zPos, zNeg))
    	{
    		return Vector3f.UNIT_X.negate();
    	}
    	else if(isFirstArgMin(zPos, xPos, xNeg, zNeg))
    	{
    		return Vector3f.UNIT_Z;
    	}
		return Vector3f.UNIT_Z.negate();
    }
    
    private boolean isFirstArgMin(float a, float b, float c, float d)
    {
    	if(a < b && a < c && a < d)
    	{
    		return true;
    	}
    	return false;
    }

    public BlockState getBlockState(Vector3Int location)
    {
        return terrain.getBlockState(location);
    }

    private void checkForFallingBlocks(Vector3Int location)
    {
        Vector3Int topBlockLocation = location.add(0, 1, 0);
        Block topBlock = terrain.getBlock(topBlockLocation);
        if (topBlock != null && topBlock.isAffectedByGravity())
        {
            //Loop over all upper blocks and add geometries if necessary
            int y = 1;

            FallingBlocks fallingBlocks = new FallingBlocks(getFloorGeometry(location), topBlockLocation);
            int height = cubeSettings.getChunkSizeY();
            for (int i = location.getY() + 1; i < height; i++, y++)
            {
                Vector3Int currentLocation = location.add(0, y, 0);
                Block block = terrain.getBlock(currentLocation);
                if (block != null && block.isAffectedByGravity())
                {
                    terrain.removeBlock(currentLocation);

                    Geometry geometry = block.makeBlockGeometry();
                    geometry.setName("active block");
                    Vector3f placementLocation = toBlockCenter(location).toVector3f().add(0, y * cubeSettings.getBlockSize(), 0);
                    geometry.setLocalTranslation(placementLocation);
                    node.attachChild(geometry);

                    fallingBlocks.add(block, geometry);
                }
                else
                {
                    break;
                }
            }
            fallingBlocksList.add(fallingBlocks);
        }
    }

    private Geometry getFloorGeometry(Vector3Int location)
    {
        return ((BlockChunkControl)terrain.getChunk(location)).getOptimizedGeometry_Opaque();//FIXME: Casting?
    }

    public void cleanup()
    {
        super.cleanup();
    }

    @Override
    public void update(float tpf)
    {
        super.update(tpf);

        List<FallingBlocks> doomed = new ArrayList<FallingBlocks>();

        for (FallingBlocks fallingBlocks : fallingBlocksList)
        {
            if (fallingBlocks.isDead())
            {
                doomed.add(fallingBlocks);
                for (Geometry geometry : fallingBlocks.getGeometries())
                {
                    node.detachChild(geometry);
                }
            }
            else
            {
                if (fallingBlocks.hitFloor())
                {
                    fallingBlocks.setDead(true);//Don't remove the geometries yet, it causes the block to blink
                    Vector3Int startingLocation = fallingBlocks.getStartingLocation();
                    int distanceFell = (int) (Math.round(fallingBlocks.getFallDistance()) / cubeSettings.getBlockSize());
                    Vector3Int placementLocation = new Vector3Int(startingLocation.getX(), startingLocation.getY() - distanceFell, startingLocation.getZ());
                    for (Block block : fallingBlocks.getBlocks())
                    {
                        terrain.setBlock(placementLocation, block);
                        placementLocation.addLocal(0, 1, 0);
                    }
                }
                else
                {
                    fallingBlocks.fall(tpf);
                }
            }
        }
        fallingBlocksList.removeAll(doomed);
        doomed.clear();
    }

    private class FallingBlocks
    {

        private List<Block> blocks = new ArrayList<Block>();
        private List<Geometry> geometries = new ArrayList<Geometry>();
        private Geometry floor;
        private boolean dead;
        private float fallDistance;
        private Vector3Int startingLocation;//This is the location of the bottom most block

        public FallingBlocks(Geometry floor, Vector3Int startingLocation)
        {
            this.floor = floor;
            this.startingLocation = startingLocation;
        }

        public Vector3Int getStartingLocation()
        {
            return startingLocation;
        }

        public List<Geometry> getGeometries()
        {
            return geometries;
        }

        public boolean isDead()
        {
            return dead;
        }

        public void setDead(boolean dead)
        {
            this.dead = dead;
        }

        public List<Block> getBlocks()
        {
            return blocks;
        }

        public void add(Block block, Geometry geometry)
        {
            geometries.add(geometry);
            blocks.add(block);
        }

        public float getFallDistance()
        {
            return fallDistance;
        }

        public void fall(float tpf)
        {
            float fallAmount = tpf * 12;
            for (Geometry geometry : geometries)
            {
                geometry.setLocalTranslation(geometry.getLocalTranslation().subtract(0, fallAmount, 0));
            }
            fallDistance += fallAmount;
        }

        private Geometry getBottomBlock()
        {
            if (geometries.isEmpty())
            {
                return null;
            }
            return geometries.get(0);

        }

        public boolean hitFloor()
        {
        	float blockSize = cubeSettings.getBlockSize();
            if (fallDistance > blockSize * 0.8)
            {
                Vector3f origin = getBottomBlock().getWorldTranslation().add(blockSize / 2, 0, blockSize / 2);
                Vector3f direction = new Vector3f(0.0f, -1.0f, 0.0f);
                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();

                floor.collideWith(ray, results);

                if (results.size() > 0)
                {
                    Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
                    float distanceToFloor = collisionContactPoint.distance(origin);
                    if (distanceToFloor < 0.2)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }

	@Override
	public Block getBlock(int x, int y, int z)
	{
		return terrain.getBlock(x, y, z);
	}

	@Override
	public Block getBlock(Vector3Int location)
	{
		return terrain.getBlock(location);
	}

	@Override
	public CubesSettings getSettings()
	{
		return terrain.getSettings();
	}

	@Override
	public Chunk getChunkNeighbor(Chunk chunk, Direction direction)
	{
		return terrain.getChunkNeighbor(chunk, direction);
	}

	@Override
	public Chunk getChunk(Vector3Int blockLocation)
	{
		return terrain.getChunk(blockLocation);
	}

	@Override
	public Vector3Int getLocalBlockLocation(Vector3Int blockLocation, Chunk chunk)
	{
		return terrain.getLocalBlockLocation(blockLocation, chunk);
	}

	@Override
	public void setBlock(Vector3Int topLocation, Block block)
	{
		terrain.setBlock(topLocation, block);
	}

	@Override
	public void removeBlock(Vector3Int location)
	{
		terrain.removeBlock(location);
	}
}