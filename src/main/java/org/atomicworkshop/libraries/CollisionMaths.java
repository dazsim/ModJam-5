package org.atomicworkshop.libraries;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.util.math.MathHelper;

public class CollisionMaths {

	
	public Vector3d IntersectionLinePlane(Vector3d head,Vector3d look, Vector3d planeCo, Vector3d planeNo)
	{
		/* 	head and look define the line
		 	planeCo and planeNo define the plane of intersection
		 	return a vector if there is an intersection or nothing if it cant be found
		*/
		Vector3d su = subVector3d(head,look);
		double dot = dotVector3d(planeNo,su);
		if (MathHelper.abs((float)dot)>1e-6)
		{
			/*
			 *  0-1.0 = intersection
			 *  <1.0  = in front of plane
			 *  <0    = behind plane 
			 */
			Vector3d w = subVector3d(head,planeCo);
			double factor = -dotVector3d(planeNo,w);
			su = mulVector3d(su,factor);
			return addVector3d(head,su);
			
		} else
		{
			return null;
		}
		
	}
	
	public Vector3d subVector3d (Vector3d a,Vector3d b)
	{
		Vector3d result = new Vector3d();
		result.x = a.x - b.x;
		result.y = a.y - b.y;
		result.z = a.z - b.z;
		return result;
	}
	
	public Vector3d addVector3d (Vector3d a, Vector3d b)
	{
		Vector3d result = new Vector3d();
		result.x = a.x + b.x;
		result.y = a.y + b.y;
		result.z = a.z + b.z;
		return result;
	}
	
	public double dotVector3d( Vector3d a, Vector3d b)
	{
		
		double result;
		result = (a.x*b.x) + (a.y*b.y) + (a.z*b.z); 
		return result;
	}
	public double lengthSquaredVector3d ( Vector3d a)
	{
		double result;
		result = dotVector3d(a,a);
		return result;
	}
	
	/*
	 * This function takes a vector and a scalar value and multiplies the vector by the scalar value.
	 */
	public Vector3d mulVector3d(Vector3d a, double f)
	{
		Vector3d result = new Vector3d(); 
		result.x = a.x*f;
		result.y = a.y*f;
		result.z = a.z*f;
		return result;
	}
}
