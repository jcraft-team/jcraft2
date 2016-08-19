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
	public void handleCommand(String command)
	{
		if(command != null && command.trim().length() > 0)
		{
			String[] args = command.split(" ");
			String commandName = args[0];
			if("time".equalsIgnoreCase(commandName) && args.length > 1)
			{
				try
				{
					float timeOfDay = Float.parseFloat(args[1]);
					if(timeOfDay > 0 && timeOfDay < 24)
					{
						world.getTimeOfDayProvider().setTimeOfDay(timeOfDay);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if("position".equalsIgnoreCase(commandName) && args.length == 4)
			{
				try
				{
					double x = Double.parseDouble(args[1]);
					double y = Double.parseDouble(args[2]);
					double z = Double.parseDouble(args[3]);
					player.setPosition(x, y, z);
					player.update(0);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
