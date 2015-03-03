package me.andpay.ti.spring.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import me.andpay.ti.util.CloseUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * 基于Poi的Excel文件写入器类。
 * 
 * @author sea.bao
 *
 * @param <T>
 */
public class PoiExcelFileItemWriter<T> extends ExecutionContextUserSupport implements
		ResourceAwareItemWriterItemStream<T>, InitializingBean {

	private Resource resource;

	private Workbook workbook = null;

	private OutputStream out = null;

	private List<? extends AbstractPoiExcelSheetItemWriter<? extends T>> sheetItemWriters;
	
	@SuppressWarnings({ "rawtypes" })
	private void initializeIfNecessary() {
		if (out == null) {
			File file;
			try {
				file = resource.getFile();
			} catch (IOException e) {
				throw new ItemStreamException("Could not convert resource to file: [" + resource + "]", e);
			}

			Assert.state(!file.exists() || file.canWrite(), "Resource is not writable: [" + resource + "]");
			workbook = new HSSFWorkbook();

			FileUtils.setUpOutputFile(file, false, false, true);

			try {
				out = new FileOutputStream(file.getAbsolutePath(), true);
			} catch (IOException e) {
				CloseUtil.close(out);
				out = null;
				workbook = null;
				throw new ItemStreamException("Init excel file meet error, file: [" + resource + "].", e);
			}

			for ( int i=0; i < sheetItemWriters.size(); i++ ) {
				AbstractPoiExcelSheetItemWriter writer = sheetItemWriters.get(i);
				writer.writeHeader(resource, workbook, i+1);
			}
		}
	}

	/**
	 * Initialize the reader. This method may be called multiple times before
	 * close is called.
	 * 
	 * @see ItemStream#open(ExecutionContext)
	 */
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		Assert.notNull(resource, "The resource must be set");
		Assert.notNull(sheetItemWriters, "The sheetItemWriters must be set");
		initializeIfNecessary();
	}

	public void update(ExecutionContext executionContext) throws ItemStreamException {
	}

	@SuppressWarnings({ "rawtypes" })
	public void close() throws ItemStreamException {
		if (out != null) {
			for ( int i=0; i < sheetItemWriters.size(); i++ ) {
				AbstractPoiExcelSheetItemWriter writer = sheetItemWriters.get(i);
				writer.writeFooter(resource, workbook);
			}

			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new ItemStreamException("Write excel file meet error, file: [" + resource + "].", e);
			}

			CloseUtil.close(out);
			out = null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void write(List<? extends T> items) throws Exception {
		for ( int i=0; i < sheetItemWriters.size(); i++ ) {
			AbstractPoiExcelSheetItemWriter writer = sheetItemWriters.get(i);
			writer.write(items);
		}
	}

	public void afterPropertiesSet() throws Exception {
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List<? extends AbstractPoiExcelSheetItemWriter<? extends T>> getSheetItemWriters() {
		return sheetItemWriters;
	}

	public void setSheetItemWriters(List<? extends AbstractPoiExcelSheetItemWriter<? extends T>> sheetItemWriters) {
		this.sheetItemWriters = sheetItemWriters;
	}

}
