package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authorized;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился новый неавторизованный пользователь");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        if (msg == null){
            return;
        }
        if (!authorized){
            if (msg instanceof AuthMessage){
                AuthMessage am = (AuthMessage) msg;
                if ((am.getLogin().equals("a")) && (am.getPassword().equals("a"))){
                    authorized = true;
                    ctx.writeAndFlush(new AuthMessage(new FileReader().readFileStructure(),"/authOk"));
                    CloudServer.allChannels.add(ctx.channel());
                }
            } else {
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        CloudServer.allChannels.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
