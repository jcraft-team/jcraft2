package com.chappelle.jcraft.jme3;

import com.chappelle.jcraft.EntityPlayer;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class PlayerControl extends NodeControl
{
	private Node playerNode = new Node("player");
	private Camera cam;
	private EntityPlayer player;

	public PlayerControl(JCraft app, EntityPlayer player)
	{
		cam = app.getCamera();
		
		this.player = player;
	}
	
	@Override
	protected void setNode(Node node)
	{
		node.attachChild(playerNode);
	}

	@Override
	protected void controlUpdate(float tpf)
	{
		player.update(tpf);
		
		playerNode.setLocalTranslation(player.pos);
		cam.setLocation(playerNode.getLocalTranslation().add(0, 2, 0));
	}
}