package com.overalltimertob;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("overalltobtimer")
public interface OverallTimerTobConfig extends Config
{
	@ConfigItem(
		keyName = "showTimer",
		name = "Enable Timer Overlay",
		description = "Toggle overall raid timer on/off"
	)
	default boolean showTimer()
	{
		return true;
	}
}
