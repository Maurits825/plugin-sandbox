package com.visualizer;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class RuneliteObjectUtil
{
	@Inject
	private Client client;

	public RuneLiteObject spawnGridTileObject(WorldPoint point, int tileObjectId)
	{
		RuneLiteObject obj = client.createRuneLiteObject();

		Model tile = client.loadModel(tileObjectId);
		obj.setModel(tile);
		LocalPoint lp = LocalPoint.fromWorld(client, point);
		obj.setLocation(lp, client.getPlane());

		obj.setActive(true);
		return obj;
	}
}
