package com.chappelle.jcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.jamonapi.MonitorFactory;

public class GameSettings
{
	public static int screenWidth = 1366;
	public static int screenHeight = 768;
	public static boolean showSettings;
	public static int frameRate = -1;//-1 is unlimited
	public static boolean debugEnabled;
	public static boolean skyEnabled;
	
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
			System.out.println("Loading options from " + optionsFile);
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
						else if (lineParts[0].equals("profilingEnabled"))
						{
							MonitorFactory.setEnabled(Boolean.parseBoolean(lineParts[1]));
						}
						else if (lineParts[0].equals("skyEnabled"))
						{
							skyEnabled = Boolean.parseBoolean(lineParts[1]);
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
			System.out.println("Saving options to " + optionsFile);
			printWriter.println("screenWidth=" + screenWidth);
			printWriter.println("screenHeight=" + screenHeight);
			printWriter.println("showSettings=" + showSettings);
			printWriter.println("frameRate=" + frameRate);
			printWriter.println("debugEnabled=" + debugEnabled);
			printWriter.println("profilingEnabled=" + MonitorFactory.isEnabled());
			printWriter.println("skyEnabled=" + skyEnabled);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
