package com.ttpod.rpc.netty.codec;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.ttpod.rpc.ResponseBean;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * date: 14-2-7 下午12:01
 *
 * @author: yangyang.cong@ttpod.com
 */
@ChannelHandler.Sharable
public class ResponseEncoder extends MessageToMessageEncoder<ResponseBean> {





    /**
     *
     *http://code.google.com/p/protostuff/wiki/ProtostuffRuntime
     *
     * http://code.google.com/p/protostuff/source/browse/trunk/protostuff-runtime-registry/src/test/java/com/dyuproject/protostuff/runtime/ExplicitRuntimeObjectSchemaTest.java
     *
     *
     * Performance guidelines
     As much as possible, use the concrete type when declaring a field.

     For polymorhic datasets, prefer abstract classes vs interfaces.

     Use ExplicitIdStrategy to write the type metadata as int (ser/deser will be faster and the serialized size will be smaller).
     Register your concrete classes at startup via ExplicitIdStrategy.Registry.
     For objects not known ahead of time, use IncrementalIdStrategy
     You can activate it using the system property:
     -Dprotostuff.runtime.id_strategy_factory=com.dyuproject.protostuff.runtime.IncrementalIdStrategy$Factory
     You can also use these strategies independently. E.g:
     final IncrementalIdStrategy strategy = new IncrementalIdStrategy(....);
     // use its registry if you want to pre-register classes.

     // Then when your app needs a schema, use it.
     RuntimeSchema.getSchema(clazz, strategy);

     DEFAULT_JVM_OPTS="-DResponseEncoder.buffer=32768"


     */

    static final int DEAULT_ENCODER_BUFFSIZE = Integer.getInteger("ResponseEncoder.buffer",1024);
    static final Logger logger = LoggerFactory.getLogger(ResponseEncoder.class);
    @Override
    protected void encode(
            ChannelHandlerContext ctx, ResponseBean msg, List<Object> out) throws Exception {
//        ExplicitIdStrategy.Registry.
        byte[] data = ProtostuffIOUtil.toByteArray(msg, ResponseDecoder.schema, LinkedBuffer.allocate(DEAULT_ENCODER_BUFFSIZE));

        if( data.length > DEAULT_ENCODER_BUFFSIZE){
            logger.info("encode QueryRes bytes: {}", data.length);
        }
//        schema.newMessage();
        out.add(wrappedBuffer(data));
    }
}