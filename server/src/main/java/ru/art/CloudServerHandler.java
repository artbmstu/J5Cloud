package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    private List filePathes;

    CloudServerHandler(List filePathes){
        this.filePathes = filePathes;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            if (msg instanceof MyFile){
                MyFile myFile = (MyFile)msg;
                byte[] bytes = myFile.getBytes();
                String newFileName = myFile.getName();
                FileOutputStream fos = new FileOutputStream(new File("common/storage/" + newFileName));
                fos.write(bytes);
                fos.close();
                filePathes.add(newFileName);
                ctx.writeAndFlush(new UpdateMessage(filePathes,"/update"));
            }
            if (msg instanceof String){
                byte[] data = Files.readAllBytes(Paths.get("common/storage/" + msg));
                MyFile myFile = new MyFile();
                myFile.setName((String) msg);
                myFile.setBytes(data);
                ctx.writeAndFlush(myFile);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
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
