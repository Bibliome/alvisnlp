package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.trie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.util.marshall.DataBuffer;
import fr.inra.maiage.bibliome.util.marshall.Decoder;
import fr.inra.maiage.bibliome.util.marshall.Encoder;
import fr.inra.maiage.bibliome.util.marshall.StringCodec;

public enum StringListCodex implements Decoder<List<String>>, Encoder<List<String>> {
	INSANCE;
	
	@Override
	public int getSize(List<String> object) {
		int result = 4;
		for (String s : object)
			result += StringCodec.INSTANCE.getSize(s);
		return result;
	}

	@Override
	public void encode(List<String> object, ByteBuffer buf) throws IOException {
		buf.putInt(object.size());
		for (String s : object)
			StringCodec.INSTANCE.encode(s, buf);
	}

	@Override
	public List<String> decode1(DataBuffer buffer) {
		final int len = buffer.getInt();
		String[] result = new String[len];
		for (int i = 0; i < len; ++i)
			result[i] = StringCodec.INSTANCE.decode1(buffer);
		return Arrays.asList(result);
	}

	@Override
	public void decode2(DataBuffer buffer, List<String> object) {
	}
}
