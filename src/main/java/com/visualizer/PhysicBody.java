package com.visualizer;

import lombok.Data;

@Data
public class PhysicBody
{
	private final Vector position;
	private final Vector velocity;

	private static final double FLOOR = 0;
	private static final double CEILING = 800;

	public void applyForce(Vector force, double deltaT)
	{
		velocity.add(Vector.multiply(force, deltaT));
		position.add(Vector.multiply(velocity, deltaT));

		if (position.y < FLOOR)
		{
			position.y = FLOOR;
			velocity.y = 0;
		}
		if (position.y > CEILING)
		{
			position.y = CEILING;
			velocity.y = 0;
		}
		if (Double.isNaN(position.y))
		{
			position.y = FLOOR;
			velocity.y = 0;
		}
	}
}
