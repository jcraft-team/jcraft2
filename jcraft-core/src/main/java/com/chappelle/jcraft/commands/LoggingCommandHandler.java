package com.chappelle.jcraft.commands;

import java.util.logging.*;

public class LoggingCommandHandler implements CommandHandler
{
	private static final Logger log = Logger.getLogger(LoggingCommandHandler.class.getName());
	
	@Override
	public void handleCommand(String command)
	{
		log.log(Level.INFO, String.format("Handling command: %s", command));
	}

}
