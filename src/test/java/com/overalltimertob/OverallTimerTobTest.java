package com.overalltimertob;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OverallTimerTobTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OverallTimerTobPlugin.class);
		RuneLite.main(args);
	}
}