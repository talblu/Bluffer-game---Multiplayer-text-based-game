package bgu.spl.reactor;

import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ClosedChannelException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Vector;
import java.util.logging.Logger;

import bgu.spl.server.AsyncServerProtocol;
import bgu.spl.server.ServerProtocol;
import bgu.spl.server.impl.*;
import bgu.spl.tokenizer.*;

/**
 * Handles messages from clients
 */
public class ConnectionHandler<T> {

	private static final int BUFFER_SIZE = 1024;

	protected final SocketChannel _sChannel;

	protected final ReactorData<StringMessage> _data;

	protected final AsyncServerProtocol<StringMessage> _protocol;
	protected final MessageTokenizer<StringMessage> _tokenizer;

	protected Vector<ByteBuffer> _outData = new Vector<ByteBuffer>();

	protected final SelectionKey _skey;

	private static final Logger logger = Logger.getLogger("edu.spl.reactor");

	private ProtocolTask<StringMessage> _task = null;

	/**
	 * Creates a new ConnectionHandler object
	 * 
	 * @param sChannel
	 *            the SocketChannel of the client
	 * @param data
	 *            a reference to a ReactorData object
	 */
	private ConnectionHandler(SocketChannel sChannel, ReactorData<StringMessage> data, SelectionKey key) {
		_sChannel = sChannel;
		_data = data;
		_protocol = (AsyncServerProtocol<StringMessage>) _data.getProtocolMaker().create();
		_tokenizer = _data.getTokenizerMaker().create();
		_skey = key;
	}

	// make sure 'this' does not escape b4 the object is fully constructed!
	private void initialize() {
		_skey.attach(this);
		_task = new ProtocolTask(_protocol, _tokenizer, this);
	}

	public static <T> ConnectionHandler<T> create(SocketChannel sChannel, ReactorData<StringMessage> data, SelectionKey key) {
		ConnectionHandler<T> h = new ConnectionHandler(sChannel, data, key);
		h.initialize();
		return h;
	}

	public synchronized void addOutData(ByteBuffer buf) {
		_outData.add(buf);
		switchToReadWriteMode();
	}

	private void closeConnection() {
		// remove from the selector.
		_skey.cancel();
		try {
			_sChannel.close();
		} catch (IOException ignored) {
			ignored = null;
		}
	}

	/**
	 * Reads incoming data from the client:
	 * <UL>
	 * <LI>Reads some bytes from the SocketChannel
	 * <LI>create a protocolTask, to process this data, possibly generating an
	 * answer
	 * <LI>Inserts the Task to the ThreadPool
	 * </UL>
	 * 
	 * @throws
	 * 
	 * @throws IOException
	 *             in case of an IOException during reading
	 */
	public void read() {
		// do not read if protocol has terminated. only write of pending data is
		// allowed
		if (_protocol.shouldClose()) {
			return;
		}

		SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
		logger.info("Reading from " + address);

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
		int numBytesRead = 0;
		try {
			numBytesRead = _sChannel.read(buf);
		} catch (IOException e) {
			numBytesRead = -1;
		}
		// is the channel closed??
		if (numBytesRead == -1) {
			// No more bytes can be read from the channel
			logger.info("client on " + address + " has disconnected");
			closeConnection();
			// tell the protocol that the connection terminated.
			_protocol.connectionTerminated();
			return;
		}

		//add the buffer to the protocol task
		buf.flip();
		_task.addBytes(buf);
		// add the protocol task to the reactor
		_data.getExecutor().execute(_task);
	}

	/**
	 * attempts to send data to the client<br/>
	 * if all the data has been successfully sent, the ConnectionHandler will
	 * automatically switch to read only mode, otherwise it'll stay in it's
	 * current mode (which is read / write).
	 * 
	 * @throws IOException
	 *             if the write operation fails
	 * @throws ClosedChannelException
	 *             if the channel have been closed while registering to the Selector
	 */
	public synchronized void write() {
		if (_outData.size() == 0) {
			// if nothing left in the output string, go back to read mode
			switchToReadOnlyMode();
			return;
		}
		// if there is something to send
		ByteBuffer buf = _outData.remove(0);
		if (buf.remaining() != 0) {
			try {
				_sChannel.write(buf);
			} catch (IOException e) {
				// this should never happen.
				e.printStackTrace();
			}
			// check if the buffer contains more data
			if (buf.remaining() != 0) {
				_outData.add(0, buf);
			}
		}
		// check if the protocol indicated close.
		if (_protocol.shouldClose()) {
			switchToWriteOnlyMode();
			if (buf.remaining() == 0) {
				closeConnection();
				SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
				logger.info("disconnecting client on " + address);
			}
		}
	}

	/**
	 * switches the handler to read / write TODO Auto-generated catch blockmode
	 * 
	 * @throws ClosedChannelException
	 *             if the channel is closed
	 */
	public void switchToReadWriteMode() {
		_skey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		_data.getSelector().wakeup();
	}

	/**
	 * switches the handler to read only mode
	 * 
	 * @throws ClosedChannelException
	 *             if the channel is closed
	 */
	public void switchToReadOnlyMode() {
		_skey.interestOps(SelectionKey.OP_READ);
		_data.getSelector().wakeup();
	}

	/**
	 * switches the handler to write only mode
	 * 
	 * @throws ClosedChannelException
	 *             if the channel is closed
	 */
	public void switchToWriteOnlyMode() {
		_skey.interestOps(SelectionKey.OP_WRITE);
		_data.getSelector().wakeup();
	}

}
