package me.andpay.ti.spring;

import java.io.IOException;
import java.io.Writer;

import me.andpay.ti.spring.batch.FlatFileFooterCallback;

public class MockFileFooterCallback implements FlatFileFooterCallback {

	public void writeFooter(Writer writer, int rowCount) throws IOException {
		writer.write("END=" + rowCount);
	}

}
