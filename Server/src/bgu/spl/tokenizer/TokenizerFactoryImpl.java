package bgu.spl.tokenizer;

import java.nio.charset.Charset;

public class TokenizerFactoryImpl implements TokenizerFactory<StringMessage> {
	public MessageTokenizer<StringMessage> create() {
		final Charset charset = Charset.forName("UTF-8");
		return new FixedSeparatorMessageTokenizer("\n", charset);
	}
}
