package org.infinispan.client.hotrod.impl.operations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.impl.protocol.Codec;
import org.infinispan.client.hotrod.impl.protocol.HeaderParams;
import org.infinispan.client.hotrod.impl.transport.netty.ByteBufUtil;
import org.infinispan.client.hotrod.impl.transport.netty.HeaderDecoder;
import org.infinispan.client.hotrod.impl.transport.netty.ChannelFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Reads more keys at a time. Specified <a href="http://community.jboss.org/wiki/HotRodBulkGet-Design">here</a>.
 *
 * @author Mircea.Markus@jboss.com
 * @since 4.1
 */
public class BulkGetOperation<K, V> extends RetryOnFailureOperation<Map<K, V>> {
   private final int entryCount;
   private final Map<K, V> result = new HashMap<>();
   private HeaderDecoder<Map<K, V>> decoder;

   public BulkGetOperation(Codec codec, ChannelFactory channelFactory, byte[] cacheName, AtomicInteger topologyId,
                           int flags, Configuration cfg, int entryCount) {
      super(codec, channelFactory, cacheName, topologyId, flags, cfg);
      this.entryCount = entryCount;
   }

   @Override
   protected void executeOperation(Channel channel) {
      HeaderParams header = headerParams(BULK_GET_REQUEST);
      decoder = scheduleRead(channel, header);

      ByteBuf buf = channel.alloc().buffer(codec.estimateHeaderSize(header) + ByteBufUtil.estimateVIntSize(entryCount));

      codec.writeHeader(buf, header);
      ByteBufUtil.writeVInt(buf, entryCount);
      channel.writeAndFlush(buf);
   }

   @Override
   public Map<K, V> decodePayload(ByteBuf buf, short status) {
      while (buf.readUnsignedByte() == 1) { //there's more!
         K key = codec.readUnmarshallByteArray(buf, status, cfg.serialWhitelist(), channelFactory.getMarshaller());
         V value = codec.readUnmarshallByteArray(buf, status, cfg.serialWhitelist(), channelFactory.getMarshaller());
         result.put(key, value);
         decoder.checkpoint();
      }
      return result;
   }
}
