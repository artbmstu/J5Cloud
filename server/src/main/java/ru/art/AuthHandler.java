package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.hibernate.Session;

import java.util.List;

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
        if (msg instanceof DoMessage) {
            if (((DoMessage) msg).getCommand().equals("/exit")){
                ctx.close();
            }
        }
        if (!authorized){
            if (msg instanceof AuthMessage){
                AuthMessage am = (AuthMessage) msg;
                Session session = CloudServer.getSession();
                session.beginTransaction();
                List result = session.createQuery("select users from UsersEntity users where users.login = :login and users.password = :password")
                        .setParameter("login", am.getLogin())
                        .setParameter("password", am.getPassword())
                        .list();
                if (!result.isEmpty()){
                    authorized = true;
                    ctx.writeAndFlush(new AuthMessage(new FileReader().readFileStructure(),"/authOk"));
                    CloudServer.allChannels.add(ctx.channel());
                }
                session.getTransaction().commit();
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
