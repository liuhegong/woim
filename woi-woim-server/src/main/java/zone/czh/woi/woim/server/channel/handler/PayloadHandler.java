package zone.czh.woi.woim.server.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zone.czh.woi.protocol.protocol.Payload;
import zone.czh.woi.protocol.protocol.WoiProtobuf;
import zone.czh.woi.woim.base.obj.vo.Packet;
import zone.czh.woi.woim.server.constant.AttributeKeyConstant;
import zone.czh.woi.woim.server.util.AttributeKeyUtil;

import java.util.List;
import java.util.Objects;

/**
*@ClassName: PayloadHandler
*@Description: None
*@author woi
*/
@ChannelHandler.Sharable
public class PayloadHandler extends MessageToMessageDecoder<WoiProtobuf.Payload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayloadHandler.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, WoiProtobuf.Payload payload, List<Object> out) throws Exception {
        if (Objects.equals(payload.getCmd(), Payload.Cmd.HEARTBEAT)) {
            LOGGER.info("Heartbeat from:{}", AttributeKeyUtil.get(ctx.channel(),AttributeKeyConstant.USER_ID,String.class));
        }else {
            out.add(new Packet(payload));
        }
    }
}
