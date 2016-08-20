package com.chappelle.jcraft.jme3.ui;

import com.simsilica.lemur.Button;

public class ClickSoundButton extends Button
{

	@SuppressWarnings("unchecked")
	public ClickSoundButton(String s)
	{
		super(s);
		
		addClickCommands(new ClickSoundCommand());
	}

}
