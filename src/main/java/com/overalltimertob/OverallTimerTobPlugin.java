package com.overalltimertob;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.NpcDespawned;

@Slf4j
@PluginDescriptor(
		name = "Overall ToB Timer"
)
public class OverallTimerTobPlugin extends Plugin
{
	@Inject private OverlayManager overlayManager;
	@Inject private OverallTimerTobOverlay overlay;
	@Inject private Client client;
	@Inject private OverallTimerTobConfig config;

	private boolean inTob = false;
	private boolean raidComplete = false;
	private long startTime = -1;
	private int startTick = -1;
	private long raidCompleteTimeSnapshot = 0;
	private final Point MAIDEN_GATE_START = new Point(32, 29);
	private final Point MAIDEN_GATE_END = new Point(32, 32);
	private static final int TOB_LOBBY_REGION_ID = 14642;

	@Override
	protected void startUp() throws Exception
	{
		log.info("TOB Timer started!");
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		reset();
	}

	public OverallTimerTobConfig getConfig()
	{
		return config;
	}

	@Provides
	OverallTimerTobConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OverallTimerTobConfig.class);
	}

	/*@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (npc.getName() != null && npc.getName().equalsIgnoreCase("The Maiden of Sugadinti"))
		{
			log.info("Maiden NPC spawned (ID: {})", npc.getId());
		}
	}*/

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
		{
			return;
		}

		int currentRegion = client.getLocalPlayer().getWorldLocation().getRegionID();

		if (currentRegion == TOB_LOBBY_REGION_ID)
		{
			//log.info("tob lobby detected");
			reset();
		}

		if (!inTob && crossedMaidenLine())
		{
			inTob = true;
			raidComplete = false;
			raidCompleteTimeSnapshot = 0;
			startTime = System.currentTimeMillis();
			startTick = client.getTickCount();
			//log.info("TOB raid timer started — Maiden gate line crossed!");
		}

		if (raidComplete && raidCompleteTimeSnapshot == 0)
		{
			raidCompleteTimeSnapshot = System.currentTimeMillis();
		}
	}

	private boolean crossedMaidenLine()
	{
		for (Player p : client.getPlayers())
		{
			if (p == null)
				continue;

			WorldPoint wp = p.getWorldLocation();
			int regionX = wp.getRegionX();
			int regionY = wp.getRegionY();

			for (int y = MAIDEN_GATE_START.getY(); y <= MAIDEN_GATE_END.getY(); y++)
			{
				if (regionX == MAIDEN_GATE_START.getX() && regionY == y)
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean isInTob()
	{
		return inTob;
	}

	public long getElapsedMillis()
	{
		if (!inTob || startTime == -1 || startTick == -1)
			return 0;

		if (client.getTickCount() <= startTick)
			return 0;

		long current = raidComplete ? raidCompleteTimeSnapshot : System.currentTimeMillis();
		long elapsedMs = current - startTime;
		return (elapsedMs / 600) * 600;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();
		if (npc.getId() == 8375 && inTob && !raidComplete)
		{
			log.info("Verzik P3 despawned — TOB complete");
			raidComplete = true;
		}
		if (npc.getId() == 10836 && inTob && !raidComplete) // this if statement is for entry mode (debugging)
		{
			log.info("Verzik P3 despawned — TOB complete");
			raidComplete = true;
		}
	}

	public boolean isRaidComplete()
	{
		return raidComplete;
	}

	private void reset()
	{
		//log.info("Resetting TOB timer state");
		inTob = false;
		raidComplete = false;
		startTime = -1;
		startTick = -1;
		raidCompleteTimeSnapshot = 0;
	}
}