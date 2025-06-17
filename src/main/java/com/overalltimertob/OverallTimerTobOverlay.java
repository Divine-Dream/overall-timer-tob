package com.overalltimertob;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class OverallTimerTobOverlay extends Overlay
{
    private final Client client;
    private final OverallTimerTobPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public OverallTimerTobOverlay(Client client, OverallTimerTobPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;

        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.MED);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInTob() || !plugin.getConfig().showTimer())
        {
            return null;
        }

        long elapsedMillis = plugin.getElapsedMillis();
        long minutes = elapsedMillis / 60000;
        long seconds = (elapsedMillis % 60000) / 1000;
        long hundredths = (elapsedMillis % 1000) / 10;

        Color textColor = plugin.isRaidComplete() ? Color.GREEN : Color.WHITE;

        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("TOB Timer:")
                .right(String.format("%d:%02d.%02d", minutes, seconds, hundredths))
                .rightColor(textColor)
                .build());

        return panelComponent.render(graphics);
    }
}