package com.ttpod.rpc.netty.codec;

import com.ttpod.rpc.InnerBindUtil;
import com.ttpod.rpc.RequestBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * date: 14-1-28 上午11:40
 *
 * @author: yangyang.cong@ttpod.com
 */
//@ChannelHandler.Sharable
public class StringReqDec extends ByteToMessageDecoder {
    static final int MAGIC_BYTE  = 1;
    static final int LENGTH_BYTE  = 2;
    static final int HEADER_BYTE   = MAGIC_BYTE + LENGTH_BYTE ;
    protected static final Logger logger = LoggerFactory.getLogger(StringReqDec.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEADER_BYTE) {// Wait until the length prefix is available.
            return;
        }
        in.markReaderIndex();
        int frameLength;
        if (        in.readUnsignedByte() != StringReqEnc.MAGIC
              ||    (frameLength = in.readUnsignedShort() )> in.readableBytes()  // Wait until the whole data is available.
         ) {
            in.resetReaderIndex();
            return;
        }

        int dataIndex = in.readerIndex();

        short reqId = in.readShort();

        byte service =  in.readByte();
        short page = in.readUnsignedByte();
        short size = in.readUnsignedByte();
        int stringIndex  = in.readerIndex();
        int endIndex =  frameLength  +  dataIndex;
        String q = in.toString(stringIndex,endIndex - stringIndex, CharsetUtil.UTF_8);
        in.readerIndex(endIndex);

        // TODO needd release here?
//        logger.debug("StringReqDec release ByteBuf  in {} ",in.release());

        RequestBean<String> req = new RequestBean<>();
        InnerBindUtil.bind(req,reqId);
        req.setService(service);
        req.setPage(page);
        req.setSize(size);
        req.setData(q);

        out.add(req);
    }
}
