package me.andpay.ti.spring;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class MockFileHeaderCallback implements FlatFileHeaderCallback {

	public void writeHeader(Writer writer) throws IOException {
		writer.write("BEGIN");
	}

}
