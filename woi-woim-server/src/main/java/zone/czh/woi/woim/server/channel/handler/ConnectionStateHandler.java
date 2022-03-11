package zone.czh.woi.woim.server.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zone.czh.woi.protocol.util.PayloadUtil;
import zone.czh.woi.woim.base.constant.WOIMConfig;
import zone.czh.woi.woim.server.WOIMServer;
import zone.czh.woi.woim.server.constant.AttributeKeyConstant;
import zone.czh.woi.woim.server.util.AttributeKeyUtil;

import java.util.concurrent.TimeUnit;

/**
*@ClassName: ConnectionStateHandler
*@Description: None
*@author woi
*/
@Data
@AllArgsConstructor
@ChannelHandler.Sharable
public class ConnectionStateHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionStateHandler.class);

    private WOIMServer.EventListener eventListener;

    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
        //定时关闭未鉴权的连接
        ctx.channel().eventLoop().schedule(() -> {
            AttributeKey<String> cid = AttributeKey.valueOf(AttributeKeyConstant.CHANNEL_ID);
            String id = ctx.channel().attr(cid).get();
            if (id==null){
                ctx.close();
                LOGGER.info("Close channel {} for unauthorized",cid);
            }
        }, WOIMConfig.AUTH_EXPIRED_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        eventListener.onChannelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        eventListener.onChannelExceptionCaught(ctx,cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            switch (((IdleStateEvent) evt).state()){
                case READER_IDLE:
                    String uid = AttributeKeyUtil.get(ctx.channel(), AttributeKeyConstant.USER_ID, String.class);
                    String cid = AttributeKeyUtil.get(ctx.channel(), AttributeKeyConstant.CHANNEL_ID, String.class);
                    LOGGER.info("Channel uid:{} cid:{} reader idle");
                    try {
                        //尝试服务端主动发送心跳包
                        ctx.channel().writeAndFlush(PayloadUtil.buildHeartbeat());
                    }catch (Exception e){
                        ctx.channel().close();
                        LOGGER.info("Close channel uid:{} cid:{} for reader idle",uid,cid);
                    }
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
            }
        }
    }
}
