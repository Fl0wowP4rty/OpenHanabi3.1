package cn.hanabi.utils;

import cn.hanabi.utils.rotation.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class setBlockAndFacing {
   static Minecraft mc = Minecraft.getMinecraft();

   private void setBlockAndFacing(BlockPos var1) {
      EnumFacing currentFacing;
      BlockPos currentPos;
      if (mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock() != Blocks.air) {
         currentPos = var1.add(0, -1, 0);
         currentFacing = EnumFacing.UP;
      } else if (mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock() != Blocks.air) {
         currentPos = var1.add(-1, 0, 0);
         currentFacing = EnumFacing.EAST;
      } else if (mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock() != Blocks.air) {
         currentPos = var1.add(1, 0, 0);
         currentFacing = EnumFacing.WEST;
      } else if (mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock() != Blocks.air) {
         currentPos = var1.add(0, 0, -1);
         currentFacing = EnumFacing.SOUTH;
      } else if (mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock() != Blocks.air) {
         currentPos = var1.add(0, 0, 1);
         currentFacing = EnumFacing.NORTH;
      } else {
         currentPos = null;
         currentFacing = null;
      }

   }

   public static class BlockUtil {
      private static final Minecraft mc = Minecraft.getMinecraft();
      static float last = 0.0F;

      public static void placeHeldItemUnderPlayer() {
         BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY - 1.0, mc.thePlayer.posZ);
         Vec3d vec = (new Vec3d(blockPos)).addVector(0.4000000059604645, 0.4000000059604645, 0.4000000059604645);
         mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, (ItemStack)null, blockPos, EnumFacing.UP, vec.scale(0.4));
      }

      public static Rotation limitAngleChange(Rotation currentRotation, Rotation targetRotation, float turnSpeed) {
         float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
         float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());
         return new Rotation(currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)), currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
      }

      private static float getAngleDifference(float a, float b) {
         return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
      }

      public static float[] getDirectionToBlock(int var0, int var1, int var2, EnumFacing var3) {
         EntityEgg var4 = new EntityEgg(mc.theWorld);
         var4.posX = (double)var0 + 0.5;
         var4.posY = (double)var1 + 0.5;
         var4.posZ = (double)var2 + 0.5;
         var4.posX += (double)var3.getDirectionVec().getX() * 0.25;
         var4.posY += (double)var3.getDirectionVec().getY() * 0.25;
         var4.posZ += (double)var3.getDirectionVec().getZ() * 0.25;
         return getDirectionToEntity(var4);
      }

      private static float[] getDirectionToEntity(Entity var0) {
         return new float[]{getYaw(var0) + mc.thePlayer.rotationYaw, getPitch(var0) + mc.thePlayer.rotationPitch};
      }

      public static float[] getRotationNeededForBlock(EntityPlayer paramEntityPlayer, BlockPos pos) {
         double d1 = (double)pos.getX() - paramEntityPlayer.posX;
         double d2 = (double)pos.getY() + 0.5 - (paramEntityPlayer.posY + (double)paramEntityPlayer.getEyeHeight());
         double d3 = (double)pos.getZ() - paramEntityPlayer.posZ;
         double d4 = Math.sqrt(d1 * d1 + d3 * d3);
         float f1 = (float)(Math.atan2(d3, d1) * 180.0 / Math.PI) - 90.0F;
         float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
         return new float[]{f1, f2};
      }

      public static float getYaw(Entity var0) {
         double var1 = var0.posX - mc.thePlayer.posX;
         double var3 = var0.posZ - mc.thePlayer.posZ;
         double var5;
         if (var3 < 0.0 && var1 < 0.0) {
            var5 = 90.0 + Math.toDegrees(Math.atan(var3 / var1));
         } else if (var3 < 0.0 && var1 > 0.0) {
            var5 = -90.0 + Math.toDegrees(Math.atan(var3 / var1));
         } else {
            var5 = Math.toDegrees(-Math.atan(var1 / var3));
         }

         return MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float)var5));
      }

      public static float getPitch(Entity var0) {
         double var1 = var0.posX - mc.thePlayer.posX;
         double var3 = var0.posZ - mc.thePlayer.posZ;
         double var5 = var0.posY - 1.6 + (double)var0.getEyeHeight() - mc.thePlayer.posY;
         double var7 = (double)MathHelper.sqrt_double(var1 * var1 + var3 * var3);
         double var9 = -Math.toDegrees(Math.atan(var5 / var7));
         return -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float)var9);
      }
   }
}
