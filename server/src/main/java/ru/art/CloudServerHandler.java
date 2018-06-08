package ru.art;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    private Set filePathes;
    private final String DIRECROTY_PATH = "common/storage/";

    CloudServerHandler(){
        try {
            filePathes = new FileReader().readFileStructure();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            if (msg instanceof MyFile){
                MyFile myFile = (MyFile)msg;
                saveFile(myFile);
                updateOnClient(ctx);
            }
            if (msg instanceof Message){
                Message doMsg = (Message) msg;
                switch (doMsg.getCommand()){
                    case "/download":
                        ctx.writeAndFlush(sendFile(doMsg));
                        break;
                    case "/delete":
                        deleteFile(doMsg.getFileName());
                        updateOnClient(ctx);
                        break;
                    case "/update":
                        updateOnClient(ctx);
                        break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
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

    private MyFile sendFile(Message doMsg) throws Exception{
        byte[] data = Files.readAllBytes(Paths.get(DIRECROTY_PATH + doMsg.getFileName()));
        MyFile myFile = new MyFile();
        myFile.setName(doMsg.getFileName());
        myFile.setBytes(data);
        return myFile;
    }

    private void deleteFile(String fileToDelete){
        new File(DIRECROTY_PATH + fileToDelete).delete();
        filePathes.remove(fileToDelete);
    }
    private void saveFile(MyFile myFile) throws Exception{
        byte[] bytes = myFile.getBytes();
        String newFileName = myFile.getName();
        FileOutputStream fos = new FileOutputStream(new File(DIRECROTY_PATH + newFileName));
        fos.write(bytes);
        fos.close();
        filePathes.add(newFileName);
    }
    private void updateOnClient(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new DoMessage(new FileReader().readFileStructure(),"/update"));
    }
}
