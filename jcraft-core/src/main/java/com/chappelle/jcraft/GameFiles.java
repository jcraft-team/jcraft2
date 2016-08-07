package com.chappelle.jcraft;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class GameFiles
{
	static
	{
		File dataDir = getDataDir();
		if(!dataDir.exists())
		{
			dataDir.mkdirs();
		}
		File saveDir = getSaveDir();
		if(!saveDir.exists())
		{
			saveDir.mkdirs();
		}
	}
	/**
	 * Returns the root folder for which user specific data will be stored. Game saves, options...etc.
	 * @return the root folder for which user specific data will be stored
	 */
    public static File getDataDir() 
    {
    	if(SystemUtils.IS_OS_WINDOWS)
    	{
    		return new File(System.getenv("APPDATA"), ".jcraft");
    	}
    	else
    	{
    		return new File(SystemUtils.getUserHome(), ".jcraft");
    	}
    }

    public static File getSaveDir() 
    {
    	return new File(getDataDir(), "saves");
    }

}
