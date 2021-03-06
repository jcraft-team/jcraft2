package com.chappelle.jcraft.commands;

import com.chappelle.jcraft.EntityPlayer;
import com.chappelle.jcraft.world.World;

public class SimpleCommandHandler implements CommandHandler
{
	private final EntityPlayer player;
	private final World world;
	
	public SimpleCommandHandler(EntityPlayer player, World world)
	{
		this.player = player;
		this.world = world;
	}
	
	@Override
	public String handleCommand(String command)
	{
		if(command != null && command.trim().length() > 0)
		{
			try
			{
				String[] args = command.split(" ");
				String commandName = args[0];
				if("time".equalsIgnoreCase(commandName) && args.length > 1)
				{
					float timeOfDay = Float.parseFloat(args[1]);
					if(timeOfDay <= 0 || timeOfDay >= 24)
					{
						timeOfDay = 0;
					}
					world.getTimeOfDayProvider().setTimeOfDay(timeOfDay);
					return String.format("Time of day set to %f", timeOfDay);
				}
				else if("select".equalsIgnoreCase(commandName) && args.length == 2)
				{
					player.setSelectedBlock(Integer.parseInt(args[1]));
					return "Selected block: " + player.getSelectedBlock().blockId;
				}
				else if("cam".equalsIgnoreCase(commandName) && args.length == 2)
				{
					float fovY = Float.parseFloat(args[1]);
					player.cam.setFrustumPerspective(fovY, player.cam.getWidth()/player.cam.getHeight(), 1, 1000);
					return String.format("Field of view set to %f", fovY);
				}
				else if("position".equalsIgnoreCase(commandName) && args.length == 4)
				{
					double x = Double.parseDouble(args[1]);
					double y = Double.parseDouble(args[2]);
					double z = Double.parseDouble(args[3]);
					player.setPosition(x, y, z);
					player.update(0);
					return "Player position set to (" + x + ", " + y + ", " + z + ")";
				}
				else if("enemy".equalsIgnoreCase(commandName))
				{
					double x = 0;
					double y = 0;
					double z = 0;
					if(args.length == 4)
					{
						x = Double.parseDouble(args[1]);
						y = Double.parseDouble(args[2]);
						z = Double.parseDouble(args[3]);
					}
					else
					{
						x = player.posX + 10;
						y = player.posY + 10;
						z = player.posZ + 10;
					}
					world.spawnEnemy(x, y, z);
					player.update(0);
					return "Spawned enemy at (" + x + ", " + y + ", " + z + ")";
				}
				else
				{
					return String.format("Unknown command: %s", commandName);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return e.getMessage();
			}
		}
		return "";
	}

}
