package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authorized;
    private List filePathes;

    AuthHandler(List filePathes){
        this.filePathes = filePathes;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
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
                if ((am.getLogin().equals("login")) && (am.getPassword().equals("pas"))){
                    authorized = true;
                    AuthMessage authMessage = new AuthMessage(filePathes, "/authOk");
                    ctx.write(authMessage);
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
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
