package com.chappelle.jcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GameSettings
{
	public int screenWidth = 1366;
	public int screenHeight = 768;
	public boolean showSettings = false;
	public int frameRate = -1;//-1 is unlimited
	
	private File optionsFile;
	
	public GameSettings(File parent)
	{
		this.optionsFile = new File(parent, "jcraft-options.txt");
	}
	
	public void load()
	{
		if(optionsFile.exists())
		{
			try(BufferedReader bufferedreader = new BufferedReader(new FileReader(this.optionsFile)))
			{
				String line = "";
				while ((line = bufferedreader.readLine()) != null)
				{
					try
					{
						String[] lineParts = line.split("=");

						if (lineParts[0].equals("screenWidth"))
						{
							this.screenWidth = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("screenHeight"))
						{
							this.screenHeight = Integer.parseInt(lineParts[1]);
						}
						else if (lineParts[0].equals("showSettings"))
						{
							this.showSettings = Boolean.parseBoolean(lineParts[1]);
						}
						else if (lineParts[0].equals("frameRate"))
						{
							this.frameRate = Integer.parseInt(lineParts[1]);
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
	
	public void save()
	{
		try(PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile)))
		{
			printWriter.println("screenWidth=" + screenWidth);
			printWriter.println("screenHeight=" + screenHeight);
			printWriter.println("showSettings=" + showSettings);
			printWriter.println("frameRate=" + frameRate);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
