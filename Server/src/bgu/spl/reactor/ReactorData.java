package bgu.spl.reactor;


import java.util.concurrent.ExecutorService;
import java.nio.channels.Selector;

import bgu.spl.server.ServerProtocolFactory;
import bgu.spl.server.impl.*;
import bgu.spl.tokenizer.*;

/**
 * a simple data structure that hold information about the reactor, including getter methods
 */
public class ReactorData<T> {

	private final ExecutorService _executor;
	private final Selector _selector;
	private final ServerProtocolFactory _protocolMaker;
	private final TokenizerFactory<T> _tokenizerMaker;

	public ExecutorService getExecutor() {
		return _executor;
	}

	public Selector getSelector() {
		return _selector;
	}

	public ReactorData(ExecutorService _executor, Selector _selector, ServerProtocolFactory protocol, TokenizerFactory<T> tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	public ServerProtocolFactory getProtocolMaker() {
		return _protocolMaker;
	}

	public TokenizerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
