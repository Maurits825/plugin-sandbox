package com.visualizer;

public class Vector
{
	public double x;
	public double y;
	public double z;

	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector zero()
	{
		return new Vector(0, 0, 0);
	}

	public static Vector multiply(Vector vector, double magnitude)
	{
		return new Vector(vector.x * magnitude, vector.y * magnitude, vector.z * magnitude);
	}

	public void add(Vector vector)
	{
		x += vector.x;
		y += vector.y;
		z += vector.z;
	}
}
