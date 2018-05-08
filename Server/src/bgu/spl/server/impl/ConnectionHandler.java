//package bgu.spl.server.impl;
//import java.io.*;
//import java.net.*;

//import bgu.spl.server.ProtocolCallback;
//import bgu.spl.server.ServerProtocol;

/*public class ConnectionHandler implements Runnable {
	private BufferedReader in;
	    private PrintWriter out;
	    Socket clientSocket;
	    ServerProtocol<String> protocol;
	    boolean run;



	    public ConnectionHandler(Socket acceptedSocket, ServerProtocol<String> p){
	    	in = null;
	        out = null;
	        clientSocket = acceptedSocket;
	        protocol = p;
	        System.out.println("Accepted connection from client!");
	        System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	    }

	    public void run() {
	//        String msg;
	        try {
	            initialize();
	        }
	        catch (IOException e) {
	            System.out.println("Error in initializing I/O");
	        }

	        try {
	            process();
	        } 
	        catch (IOException e) {
	            System.out.println("Error in I/O");
	        }

	        System.out.println("Connection closed - bye bye...");
	        close();
	    }

	    public void process() throws IOException
	    {
	        String msg;

	        while ((msg = in.readLine()) != null)
	        {
	            System.out.println(msg);  
	            protocol.processMessage(msg, new ProtocolCallback<String>(){
					@Override
					public void sendMessage(String msg) throws IOException {
						out.println(msg);
					}});
	        }
	    }

	    // Starts listening
	    public void initialize() throws IOException
	    {
	        // Initialize I/O
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
	        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
	        System.out.println("I/O initialized");
	    }

	    // Closes the connection
	    public void close()
	    {
	        try {
	            if (in != null)
	            {
	                in.close();
	            }
	            if (out != null)
	            {
	                out.close();
	            }

	            clientSocket.close();
	        }
	        catch (IOException e)
	        {
	            System.out.println("Exception in closing I/O");
	        }
	    }

	}
 */
package bgu.spl.server.impl;

/*import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import bgu.spl.server.ProtocolCallback;
import bgu.spl.server.ServerProtocol;
import bgu.spl.tokenizer.FixedSeparatorMessageTokenizer;
import bgu.spl.tokenizer.StringMessage;

public class ConnectionHandler implements Runnable {

	private ProtocolCallback<StringMessage> callback;
	//private BufferedReader in;
	private ByteBuffer buf;
	//private PrintWriter out;
	private SocketChannel clientSocket;
	private ServerProtocol<StringMessage> protocol;
	private FixedSeparatorMessageTokenizer token;
	private Charset charset;

	public ConnectionHandler(SocketChannel acceptedSocket, ServerProtocol<StringMessage> p) {
		//in = null;
		//	out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		charset = Charset.defaultCharset();
		System.out.println("Accepted connection from client!");
		try {
			System.out.println("The client is from: " + acceptedSocket.getRemoteAddress() + ":" + acceptedSocket.socket().getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {

		String msg;

		try {
			initialize();
		} catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} catch (IOException e) {
			System.out.println("Error in I/O");
		}

		System.out.println("Connection closed - bye bye...");
		close();

	}

	public void process() throws IOException
	{

		String msg;
		while ((token.nextMessage()) != null){
			token.addBytes(token.getBytesForMessage(new StringMessage(token.nextMessage())));// adding the msg to the tokenizer

			if(token.hasMessage()){

				System.out.println("Received \"" + token.nextMessage()+ "\" from client");

				protocol.processMessage(token.nextMessage(),(ProtocolCallback<StringMessage>)callback);// sending the next msg in the tokenizer to the protocol
			}
		}
	}

	// Starts listening
	public void initialize() throws IOException {
		// Initialize I/O
		//in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		//out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
		System.out.println("I/O initialized");
		//callback = new ProtocolCallback(out);
		token = new FixedSeparatorMessageTokenizer("/n", charset);
	}

	// Closes the connection
	public void close() {
		try {
			//if (in != null) {
			//		in.close();
			//	}
			//if (out != null) {
			//	out.close();
			//}

			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Exception in closing I/O");
		}
	}

}*/

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Logger;
import bgu.spl.server.AsyncServerProtocol;
import bgu.spl.server.ProtocolCallback;
import bgu.spl.server.ServerProtocol;
//import bgu.spl.server.CallbackFactory;
import bgu.spl.tokenizer.MessageTokenizer;
import bgu.spl.tokenizer.StringMessage;

public class ConnectionHandler<T> implements Runnable {
	SocketChannel clientSocket;
	ServerProtocol<StringMessage> protocol;
	MessageTokenizer<StringMessage> tokenizer;
	ProtocolCallback<StringMessage> callback;
	//private TPCCallbackFactory<T> callback;
	private static final int BUFFER_SIZE = 1024;
	private static final Logger logger = Logger.getLogger("edu.spl.TPC");


	public ConnectionHandler(SocketChannel socketChannel, ServerProtocol<StringMessage> p, MessageTokenizer<StringMessage> tok) {
		clientSocket = socketChannel;
		protocol = p;
		tokenizer = tok;
		//callback = callbackFac;
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + socketChannel.socket().getRemoteSocketAddress());
	}

	public void run() {	
		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 

		System.out.println("Connection closed - bye bye...");
		close();

	}

	@SuppressWarnings("unchecked")
	public void process() throws IOException {

		StringMessage msg= null;
		ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE );
		//	clientSocket.read(buff);
		//	buff.flip();
		//	tokenizer.addBytes(buff);
		//clientSocket.read(buff);
		//	if (tokenizer.hasMessage()) {
		//		msg = tokenizer.nextMessage();
		//	}
		//System.out.println("gggggggggggggggggggggggggggggggggggggggggggggg" + msg.getMessage());
		//	buff.clear();
		int numBytesRead;

		while (true){
			clientSocket.read(buff);
			buff.flip();
			tokenizer.addBytes(buff);
			if (tokenizer.hasMessage()) {
				msg = tokenizer.nextMessage();
				buff.clear();
				protocol.processMessage(msg, new ProtocolCallback<StringMessage>(){
					
					public void sendMessage(StringMessage msg) throws IOException {
						ByteBuffer buf;
						try {
							buf = tokenizer.getBytesForMessage(msg);
							while (buf.remaining() != 0) {
								clientSocket.write(buf);
							}
							 
						} catch (CharacterCodingException e1) {
							e1.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}});
			}
			numBytesRead = 0;
			try {
				numBytesRead = clientSocket.read(buff);
			} catch (IOException e) {
				numBytesRead = -1;
			}
			// is the channel closed??
			if (numBytesRead == -1) {
				// No more bytes can be read from the channel
				logger.info("client on " + clientSocket.socket().getRemoteSocketAddress() + " has disconnected");
				close();
				// tell the protocol that the connection terminated.
				if (protocol instanceof AsyncServerProtocol){
					((AsyncServerProtocol<T>)protocol).connectionTerminated();
				}
				return;
			}
		//	clientSocket.read(buff);
		//	buff.flip();
		//	tokenizer.addBytes(buff);
			//	if (!tokenizer.hasMessage()) {
			//		break;
			//  msg = tokenizer.nextMessage();
			/*	protocol.processMessage(msg, new ProtocolCallback<StringMessage>(){
					@Override
					public void sendMessage(StringMessage msg) throws IOException {
						System.out.println(msg+"****************************************************************");
						ByteBuffer buf;
						try {
							buf = tokenizer.getBytesForMessage(msg);
							while (buf.remaining() != 0) {
								clientSocket.write(buf);
							}
						} catch (CharacterCodingException e1) {
							e1.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}});*/
			if (protocol.isEnd(msg)) break;
			//buff.clear();
		}
	}

	// Closes the connection
	public void close()
	{
		try {			
			clientSocket.close();
		}
		catch (IOException e){
			System.out.println("Exception in closing I/O");
		}
	}
}

