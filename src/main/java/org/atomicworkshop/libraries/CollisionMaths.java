package org.atomicworkshop.libraries;

import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public final class CollisionMaths {
	private CollisionMaths() {}

	public static Vec3d getPlayerLookVector(EntityPlayer playerIn, Vec3d headPosition)
	{
		final float playerPitch = playerIn.rotationPitch;
		final float playerYaw = playerIn.rotationYaw;

		final float yawCosine = MathHelper.cos((float)(-playerYaw * (Math.PI / 180) - Math.PI));
		final float yawSine = MathHelper.sin((float)(-playerYaw * (Math.PI / 180) - Math.PI));
		final float pitchCosine = -MathHelper.cos((float)(-playerPitch * (Math.PI / 180)));
		final float pitchSine = MathHelper.sin((float)(-playerPitch * (Math.PI / 180)));

		final float lookX = yawSine * pitchCosine;
		final float lookZ = yawCosine * pitchCosine;
		final float playerReach = (float)playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();

		final Vec3d lookVector = new Vec3d(
				lookX * playerReach,
				pitchSine * playerReach,
				lookZ * playerReach
		);
		return lookVector;
	}

	public static Vec3d getPlayerHeadPosition(EntityPlayer playerIn)
	{
		final Vec3d headPosition = new Vec3d(
			playerIn.posX,
			playerIn.posY + playerIn.getEyeHeight(),
			playerIn.posZ
		);
		return headPosition;
	}

	public static Vec3d intersectionLinePlane(Vec3d head, Vec3d look, Vec3d planeCo, Vec3d planeNo)
	{
		/* 	head and look define the line
		 	planeCo and planeNo define the plane of intersection
		 	return a vector if there is an intersection or nothing if it cant be found
		*/


		//Vector3d su = subVector3d(head, look);
		Vec3d su = head.subtract(look);
		//final double dot = dotVector3d(planeNo,su);
		final double dot = planeNo.dotProduct(su);
		if (MathHelper.abs((float)dot) > 1.0e-6)
		{
			/*
			 *  0-1.0 = intersection
			 *  <1.0  = in front of plane
			 *  <0    = behind plane 
			 */
			//final Vector3d w = subVector3d(head,planeCo);
			final Vec3d w = head.subtract(planeCo);
			//final double factor = -dotVector3d(planeNo,w);
			final double factor = -planeNo.dotProduct(w);
			//su = mulVector3d(su,factor);
			su = su.scale(factor);

			//return addVector3d(head,su);
			return head.add(su);
			
		} else
		{
			return null;
		}
		
	}
	/*
	public static Vector3d subVector3d (Vector3d a,Vector3d b)
	{
		final Vector3d result = new Vector3d();
		result.x = a.x - b.x;
		result.y = a.y - b.y;
		result.z = a.z - b.z;
		return result;
	}
	
	public static Vector3d addVector3d (Vector3d a, Vector3d b)
	{
		final Vector3d result = new Vector3d();
		result.x = a.x + b.x;
		result.y = a.y + b.y;
		result.z = a.z + b.z;
		return result;
	}
	
	public static double dotVector3d( Vector3d a, Vector3d b)
	{
		
		final double result;
		result = (a.x*b.x) + (a.y*b.y) + (a.z*b.z); 
		return result;
	}
	public static double lengthSquaredVector3d ( Vector3d a)
	{
		final double result;
		result = dotVector3d(a,a);
		return result;
	}
	
	/*
	 * This function takes a vector and a scalar value and multiplies the vector by the scalar value.
	 *
	public static Vector3d mulVector3d(Vector3d a, double f)
	{
		final Vector3d result = new Vector3d();
		result.x = a.x*f;
		result.y = a.y*f;
		result.z = a.z*f;
		return result;
	}*/
}
