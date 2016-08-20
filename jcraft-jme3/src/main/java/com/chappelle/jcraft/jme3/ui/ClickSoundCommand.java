package com.chappelle.jcraft.jme3.ui;

import com.chappelle.jcraft.jme3.JCraftApplication;
import com.jme3.audio.*;
import com.simsilica.lemur.*;

public class ClickSoundCommand implements Command<Button>
{
	private AudioNode clickAudio;
	
	public ClickSoundCommand()
	{
        clickAudio = new AudioNode(JCraftApplication.getInstance().getAssetManager(), "Sounds/effects/random/click.ogg", AudioData.DataType.Buffer);
        clickAudio.setReverbEnabled(false);
        clickAudio.setVolume(2.0f);
        clickAudio.setPositional(false);
	}
	
	@Override
	public void execute(Button source)
	{
		clickAudio.play();
	}
}
