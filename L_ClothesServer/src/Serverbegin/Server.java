package Serverbegin;

import Keys.StaticValue;
import Lcodefactory.Lcoderfactory;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by L on 2017/5/30.
 */
public class Server {
    public static void main(String[]args)
    {
        System.out.print("服务开始");

        NioSocketAcceptor acceptor=new NioSocketAcceptor();//接收是没有问题的，但是没办法传给handler
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new Lcoderfactory()));
        acceptor.setHandler(new ServerHandler());//这是入口,用来放设置的东西，解码之后看什么情况
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,60);
        //一分钟为通话，进入空闲状态
        try
        {
            acceptor.bind(new InetSocketAddress(StaticValue.SERVER_PORT));
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
