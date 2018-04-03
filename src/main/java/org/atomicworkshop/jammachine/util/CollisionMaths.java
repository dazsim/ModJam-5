package org.atomicworkshop.jammachine.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public final class CollisionMaths {
	private CollisionMaths() {}

	/**
	 * Calculates the point on a plane where a line intersects
	 * @param head the point where the player's head is
	 * @param look the vector that the player is looking
	 * @param planeOrigin the center of the plane
	 * @param planeNormal the normal of the plane
	 * @return the point relative to the origin that the look vector intersects, or null if it does not.
	 */
	public static Vec3d intersectionLinePlane(Vec3d head, Vec3d look, Vec3d planeOrigin, Vec3d planeNormal)
	{
		final double dot = planeNormal.dotProduct(look);
		if (MathHelper.abs((float)dot) > 1.0e-6) {
			/*
			 *  0-1.0 = intersection
			 *  <1.0  = in front of plane
			 *  <0    = behind plane 
			 */
			final Vec3d w = head.subtract(planeOrigin);
			final double factor = -planeNormal.dotProduct(w) / dot;

			return head.add(look.scale(factor));
		} else {
			return null;
		}
	}

	public static Vec3d getPlayerLookVector(EntityPlayer playerIn)
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
}
