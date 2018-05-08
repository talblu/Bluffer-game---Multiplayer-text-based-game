package bgu.spl.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import bgu.spl.server.ProtocolCallback;
import bgu.spl.server.ServerProtocol;
import bgu.spl.tokenizer.*;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask<T> implements Runnable {

	private final ServerProtocol<StringMessage> _protocol;
	private final MessageTokenizer<StringMessage> _tokenizer;
	private final ConnectionHandler<StringMessage> _handler;

	public ProtocolTask(final ServerProtocol<StringMessage> protocol, final MessageTokenizer<StringMessage> tokenizer, final ConnectionHandler<StringMessage> h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.

	public synchronized void run() {
		// go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			//				         @SuppressWarnings("unchecked")
			StringMessage msg =  _tokenizer.nextMessage();
			this._protocol.processMessage(msg, new ProtocolCallback<StringMessage>(){
				public void sendMessage(StringMessage msg) throws IOException {
					if (msg != null) {
						try {
							ByteBuffer bytes = _tokenizer.getBytesForMessage((StringMessage) msg);
							_handler.addOutData(bytes);
						} catch (CharacterCodingException e) { e.printStackTrace(); 
						}
					}
				}
			});
		}

	}


	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
