package com.chappelle.jcraft;

import java.io.*;
import java.util.logging.Logger;

public class GameSettings
{
	private final static Logger log = Logger.getLogger(GameSettings.class.getName()); 

	public static int screenWidth = 1366;
	public static int screenHeight = 768;
	public static int chunkRenderDistance = 3;
	public static boolean showSettings;
	public static int frameRate = -1;//-1 is unlimited
	public static boolean debugEnabled;
	public static boolean skyEnabled;
	public static boolean ambientOcclusionEnabled = true;
	public static float ambientOcclusionIntensity = 1.0f;
	
	/**
	 * Set to -1 to disable auto-save
	 */
	public static long autoSaveInterval = 5*1000;
	
	private static File optionsFile;
	
	private GameSettings()
	{
	}
	/**/
	public static void load()
	{
		optionsFile = new File(GameFiles.getDataDir(), "jcraft-options.txt");
		if(optionsFile.exists())
		{
			log.info("Loading options from " + optionsFile);
			try(BufferedReader bufferedreader = new BufferedReader(new FileReader(optionsFile)))
			{
				String line = "";
				while ((line = bufferedreader.readLine()) != null)
				{
					try
					{
						String[] lineParts = line.split("=");

						if (lineParts[0].equals("screenWidth"))
						{
							screenWidth = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("screenHeight"))
						{
							screenHeight = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("chunkRenderDistance"))
						{
							chunkRenderDistance = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("showSettings"))
						{
							showSettings = Boolean.parseBoolean(lineParts[1]);
						}
						else if (lineParts[0].equals("frameRate"))
						{
							frameRate = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("debugEnabled"))
						{
							debugEnabled = Boolean.parseBoolean(lineParts[1]);
						}
						else if (lineParts[0].equals("skyEnabled"))
						{
							skyEnabled = Boolean.parseBoolean(lineParts[1]);
						}
						else if (lineParts[0].equals("ambientOcclusionEnabled"))
						{
							ambientOcclusionEnabled = Boolean.parseBoolean(lineParts[1]);
						}
						else if (lineParts[0].equals("autoSaveInterval"))
						{
							autoSaveInterval = Long.parseLong(lineParts[1]);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void save()
	{
		if(!optionsFile.exists())
		{
			try
			{
				optionsFile.createNewFile();
			}
			catch(IOException e)
			{
				throw new RuntimeException("Unable to create options file", e);
			}
		}
		try(PrintWriter printWriter = new PrintWriter(new FileWriter(optionsFile)))
		{
			log.info("Saving options to " + optionsFile);
			printWriter.println("screenWidth=" + screenWidth);
			printWriter.println("screenHeight=" + screenHeight);
			printWriter.println("chunkRenderDistance=" + chunkRenderDistance);
			printWriter.println("showSettings=" + showSettings);
			printWriter.println("frameRate=" + frameRate);
			printWriter.println("debugEnabled=" + debugEnabled);
			printWriter.println("skyEnabled=" + skyEnabled);
			printWriter.println("ambientOcclusionEnabled=" + ambientOcclusionEnabled);
			printWriter.println("autoSaveInterval=" + autoSaveInterval);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
