package org.atomicworkshop.libraries;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public final class CollisionMaths {
	private CollisionMaths() {}

	public static Vector3d getPlayerLookVector(EntityPlayer playerIn, Vector3d headPosition)
	{
		float playerPitch = playerIn.rotationPitch;
		float playerYaw = playerIn.rotationYaw;

		float yawCosine = MathHelper.cos((float)(-playerYaw * (Math.PI / 180) - Math.PI));
		float yawSine = MathHelper.sin((float)(-playerYaw * (Math.PI / 180) - Math.PI));
		float pitchCosine = -MathHelper.cos((float)(-playerPitch * (Math.PI / 180)));
		float pitchSine = MathHelper.sin((float)(-playerPitch * (Math.PI / 180)));
		float lookX = yawSine * pitchCosine;
		float lookZ = yawCosine * pitchCosine;
		float playerReach = (float)playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();

		Vector3d lookVector = new Vector3d();
		lookVector.x = lookX * playerReach;
		lookVector.y = pitchSine * playerReach;
		lookVector.z = lookZ * playerReach;

		return addVector3d(headPosition, lookVector);
	}

	public static Vector3d getPlayerHeadPosition(EntityPlayer playerIn)
	{
		Vector3d headPosition = new Vector3d();
		headPosition.x = playerIn.posX;
		headPosition.y = playerIn.posY + playerIn.getEyeHeight();
		headPosition.z = playerIn.posZ;
		return headPosition;
	}

	public static Vector3d intersectionLinePlane(Vector3d head, Vector3d look, Vector3d planeCo, Vector3d planeNo)
	{
		/* 	head and look define the line
		 	planeCo and planeNo define the plane of intersection
		 	return a vector if there is an intersection or nothing if it cant be found
		*/

		Vector3d su = subVector3d(head, look);
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
	
	public static Vector3d subVector3d (Vector3d a,Vector3d b)
	{
		Vector3d result = new Vector3d();
		result.x = a.x - b.x;
		result.y = a.y - b.y;
		result.z = a.z - b.z;
		return result;
	}
	
	public static Vector3d addVector3d (Vector3d a, Vector3d b)
	{
		Vector3d result = new Vector3d();
		result.x = a.x + b.x;
		result.y = a.y + b.y;
		result.z = a.z + b.z;
		return result;
	}
	
	public static double dotVector3d( Vector3d a, Vector3d b)
	{
		
		double result;
		result = (a.x*b.x) + (a.y*b.y) + (a.z*b.z); 
		return result;
	}
	public static double lengthSquaredVector3d ( Vector3d a)
	{
		double result;
		result = dotVector3d(a,a);
		return result;
	}
	
	/*
	 * This function takes a vector and a scalar value and multiplies the vector by the scalar value.
	 */
	public static Vector3d mulVector3d(Vector3d a, double f)
	{
		Vector3d result = new Vector3d(); 
		result.x = a.x*f;
		result.y = a.y*f;
		result.z = a.z*f;
		return result;
	}
}
