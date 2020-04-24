# rpc : call a remote service just like a local method.
This is a simple rpc project base on netty that can build and run locally.

Sever run: RpcServer.java

Clent run: RpcClient.java

A consumer RpcClient wants to call helloWorld function of the service of HelloWold in 127.0.0.1:9999.
RemoteServiceProxy.java as the servcie proxy. when call the method, it will create a netty socket, 
connect to the remote server and send a new request message data（RpcRequest.java）which was encoded by an ObjectEncoder. 

The server stub which is RpcServer.java here keeps listening on the port(9999).
RequestHandler.java uses to get the request message dataand find the service instance, then invoke the method ,
response the result by ChannelHandlerContext.writeAndFlush  of netty.

The customer will get the decoded response data in ResponseHandler which extends ChannelInboundHandlerAdapter.
Return the result in RemoteServiceProxy.

### Add one new service 
 To add the interface and its implementation in the package 'cn.jamie.learning.rpc.nettyserver.service'.
 To tag the @RpcService on the implementation.
 To copy the new added interface to the package 'cn.jamie.learning.rpc.nettyclient.service', then it could be called in the client.
