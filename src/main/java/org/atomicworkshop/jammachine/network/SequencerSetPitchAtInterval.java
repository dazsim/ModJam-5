package org.atomicworkshop.jammachine.network;

import com.google.common.base.MoreObjects;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;

import java.util.function.Supplier;

public class SequencerSetPitchAtInterval {
    private int pitch;
    private final int interval;
    private final boolean isEnabled;
    private final BlockPos pos;
    private final DimensionType dimension;

    public SequencerSetPitchAtInterval(PacketBuffer buf) {
        pitch = buf.readInt();
        interval = buf.readInt();
        isEnabled = buf.readBoolean();
        pos = buf.readBlockPos();
        dimension = DimensionType.getById(buf.readInt());
    }

    public SequencerSetPitchAtInterval(int pitch, int interval, boolean isEnabled, BlockPos pos, DimensionType dimension) {
        this.pitch = pitch;
        this.interval = interval;
        this.isEnabled = isEnabled;
        this.pos = pos;
        this.dimension = dimension;
    }

    public void toBytes(PacketBuffer buf)
    {
        buf.writeInt(pitch);
        buf.writeInt(interval);
        buf.writeBoolean(isEnabled);
        buf.writeBlockPos(pos);
        buf.writeInt(dimension.getId());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("pitch", pitch)
                .add("interval", interval)
                .add("isEnabled", isEnabled)
                .add("pos", pos)
                .add("dimension", dimension)
                .toString();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            ServerWorld world = ctx.get().getSender().server.getWorld(this.dimension);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof SequencerTileEntity) {
                ((SequencerTileEntity)tileEntity).notifyPitchAtIntervalChanged(pitch, interval, isEnabled);
            }

        });
    }
}
