package net.minelink.ctplus.compat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.v1_16_R3.*;

import javax.annotation.Nullable;
import java.net.SocketAddress;

public final class NpcNetworkManager extends NetworkManager {

    public NpcNetworkManager() {
        super(EnumProtocolDirection.SERVERBOUND);
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {

    }

    @Override
    public void setProtocol(EnumProtocol enumprotocol) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {

    }

    @Override
    public void a() {

    }

    @Override
    public void setPacketListener(PacketListener packetlistener) {

    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericfuturelistener) {
    }

    @Override
    public SocketAddress getSocketAddress() {
        return new SocketAddress() {
        };
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void stopReading() {

    }

    @Override
    public void setCompressionLevel(int i) {

    }

    @Override
    public void handleDisconnection() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception {

    }

}
