package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class CloudServerHandlerOutbound extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        MyMessage myMessage = new MyMessage("Сервер -> Клиент работает");
        ctx.write(myMessage, promise);
    }

}
