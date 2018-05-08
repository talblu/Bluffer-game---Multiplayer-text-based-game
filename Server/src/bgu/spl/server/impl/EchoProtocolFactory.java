package bgu.spl.server.impl;
import bgu.spl.server.ServerProtocolFactory;

public class EchoProtocolFactory implements ServerProtocolFactory {
    public ServerProtocolImpl create(){
        return new ServerProtocolImpl ();
    }
}