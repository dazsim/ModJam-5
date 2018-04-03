package org.atomicworkshop.jammachine.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.atomicworkshop.jammachine.JamMachineMod;


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

	public static Vec3d calculateSlopeHit(BlockPos pos, EnumFacing blockFacing, EntityPlayer player)
	{
		final Vec3d headPosition = getPlayerHeadPosition(player);
		final Vec3d lookVector = getPlayerLookVector(player);

		//FIXME: rotate Origin according to block direction
		//Steven: My brain is fried and I should be able to solve this with a matrix, but for various reasons
		//I've decided to brute force it for the modjam release. Need to find a server-friendly Matrix4f that
		//can work properly with Vec3d, or find another vector class that works with matrices.
		final Vec3d planeOrigin;
		final Vec3d bottomLeft;
		final Vec3d bottomRightCorner;
		switch (blockFacing)
		{
			case NORTH:
				planeOrigin = new Vec3d(pos.getX() + 1, pos.getY() + 7.5f / 16.0f, pos.getZ() + 15.75f / 16.0f);
				bottomLeft = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ());
				bottomRightCorner = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ());
				break;
			case SOUTH:
				planeOrigin = new Vec3d(pos.getX(), pos.getY() + 7.5f / 16.0f, pos.getZ() + (1 - (15.75f / 16.0f)));
				bottomLeft = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				bottomRightCorner = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				break;
			case WEST:
				planeOrigin = new Vec3d(pos.getX() + 15.75f / 16.0f, pos.getY() + 7.5f / 16.0f, pos.getZ());
				bottomLeft = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ());
				bottomRightCorner = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				break;
			case EAST:
				planeOrigin = new Vec3d(pos.getX() + (1 - (15.75f / 16.0f)), pos.getY() + 7.5f / 16.0f, pos.getZ() + 1);
				bottomLeft = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				bottomRightCorner = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ());
				break;
			default:
				return null;
		}

		//build matrix to rotate
		//GlStateManager.translate(0.5, 0.0, 0.5);
		//GlStateManager.rotate(), 0.0f, 1.0f, 0.0f);
		//GlStateManager.translate(-0.5, 0.0, -0.5);


		final Vec3d u = bottomLeft.subtract(planeOrigin);
		final Vec3d v = bottomRightCorner.subtract(planeOrigin);
		final Vec3d planeNormal = u.crossProduct(v);

		final Vec3d vector3d = intersectionLinePlane(headPosition, lookVector, planeOrigin, planeNormal);

		if (vector3d == null) {
			JamMachineMod.logger.info("player missed");
			return null;
		} else
		{
			final Vec3d hitVector = vector3d.subtract(pos.getX(), pos.getY(), pos.getZ());

			final Vec3d finalHitVector;
			//FIXME: HAAAAACCCKKKKK
			switch (blockFacing)
			{
				case NORTH:
					finalHitVector = new Vec3d(1-hitVector.x, hitVector.y, 1-hitVector.z);
					break;
				case SOUTH:
					finalHitVector = hitVector;
					break;
				case WEST:
					finalHitVector = new Vec3d(hitVector.z, hitVector.y, 1-hitVector.x);
					break;
				case EAST:
					finalHitVector = new Vec3d(1-hitVector.z, hitVector.y, hitVector.x);
					break;
				default:
					finalHitVector = hitVector;
			}
			JamMachineMod.logger.info("player clicked at {},{},{}", finalHitVector.x, finalHitVector.y, finalHitVector.z);
			return finalHitVector;
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
