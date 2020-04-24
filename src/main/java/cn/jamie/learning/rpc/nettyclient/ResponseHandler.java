package cn.jamie.learning.rpc.nettyclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author xujing
 * @see
 */
public class ResponseHandler extends ChannelInboundHandlerAdapter {

  private Object response;

  public Object getResponse() {
    return response;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    response = msg;
    ctx.close();
  }
}
