package org.infinispan.client.hotrod.impl.operations;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.exceptions.InvalidResponseException;
import org.infinispan.client.hotrod.impl.protocol.Codec;
import org.infinispan.client.hotrod.impl.protocol.HeaderParams;
import org.infinispan.client.hotrod.impl.protocol.HotRodConstants;
import org.infinispan.client.hotrod.impl.transport.netty.ChannelFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.jcip.annotations.Immutable;

/**
 * Implements "put" as defined by  <a href="http://community.jboss.org/wiki/HotRodProtocol">Hot Rod protocol specification</a>.
 *
 * @author Mircea.Markus@jboss.com
 * @since 4.1
 */
@Immutable
public class PutOperation<V> extends AbstractKeyValueOperation<V> {

   public PutOperation(Codec codec, ChannelFactory channelFactory,
                       Object key, byte[] keyBytes, byte[] cacheName, AtomicInteger topologyId,
                       int flags, Configuration cfg, byte[] value, long lifespan, TimeUnit lifespanTimeUnit,
                       long maxIdle, TimeUnit maxIdleTimeUnit) {
      super(codec, channelFactory, key, keyBytes, cacheName, topologyId,
         flags, cfg, value, lifespan, lifespanTimeUnit, maxIdle, maxIdleTimeUnit);
   }

   @Override
   protected void executeOperation(Channel channel) {
      HeaderParams header = headerParams(PUT_REQUEST);
      scheduleRead(channel, header);
      sendKeyValueOperation(channel, header);
   }

   @Override
   public V decodePayload(ByteBuf buf, short status) {
      if (HotRodConstants.isSuccess(status)) {
         return returnPossiblePrevValue(buf, status);
      }
      throw new InvalidResponseException("Unexpected response status: " + Integer.toHexString(status));
   }
}
