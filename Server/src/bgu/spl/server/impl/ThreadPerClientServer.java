package bgu.spl.server.impl;

import bgu.spl.server.*;
import bgu.spl.tokenizer.StringMessage;
import bgu.spl.tokenizer.TokenizerFactory;
import bgu.spl.tokenizer.TokenizerFactoryImpl;

import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.util.Vector;
import java.util.logging.Logger;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class ThreadPerClientServer implements Runnable{
	private ServerSocketChannel serverSocket;
	private int listenPort;
	private ServerProtocolFactory factory;
	//private final Logger logger;
	private Vector<ByteBuffer> _outData = new Vector<ByteBuffer>();
	private TokenizerFactoryImpl tokenizerFactory;
	private  ProtocolCallback<StringMessage> callback;
	
	public ThreadPerClientServer(int port, EchoProtocolFactory p, TokenizerFactoryImpl t){
		serverSocket =null;
		listenPort = port;
		factory = p;
		tokenizerFactory=t;		
	}

	@SuppressWarnings("unchecked")
	public void run() {
		try {
			serverSocket = createServerSocket(listenPort);
			System.out.println("Listening on port number: " + listenPort);
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port number " + listenPort);
		}

		while (true)	{
			try {
				
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create(), tokenizerFactory.create());
				new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port number " + listenPort);
			}
		}
	}


	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	private ServerSocketChannel createServerSocket(int port)
            throws IOException {
        try {
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.socket().bind(new InetSocketAddress(port));
            return ssChannel;
        } catch (IOException e) {
            //logger.info("Port " + port + " is busy");
            throw e;
        }
    }

	public static void main(String[] args) throws IOException{
		// Get port
		int port = Integer.decode(args[0]).intValue();

		ThreadPerClientServer server = new ThreadPerClientServer(port, new EchoProtocolFactory(), new TokenizerFactoryImpl());
		Thread serverThread = new Thread(server);
		serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
	}
}