package l.s.common.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

public class CSVWrite {

	CSVFormat format;
	
	List<String[]> rows;
	
	CSVWrite(CSVFormat format){
		this.format = format;
		this.rows = new ArrayList<>();
	}
	
	public CSVWrite withHeader(String... header){
		if(header == null){
			format = format.builder().setHeader().build();
		}
		else if(header.length == 0){
			format = format.builder().setHeader().build();
		}
		else{
			format = format.builder().setHeader(header).build();
		}
		return this;
	}
	
	public CSVWrite withDelimiter(char ch){
		format = format.builder().setDelimiter(ch).build();
		return this;
	}
	
	public CSVWrite withQuoteMode(l.s.common.csv.QuoteMode mode){
		return withQuoteMode(mode.getValue());
	}
	
	public CSVWrite withQuoteMode(QuoteMode mode){
		format = format.builder().setQuoteMode(mode).build();
		return this;
	}
	
	public CSVWrite saveFile(String file, String charset) throws Exception{
		return saveFile(new File(file), charset);
	}
	
	public CSVWrite saveFile(File file, String charset) throws Exception{
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		FileOutputStream out = new FileOutputStream(file);
		
		saveFile(out, charset);
		return this;
	}

	public CSVWrite saveFile(OutputStream out, String charset) throws Exception{
		try(
			OutputStreamWriter writer = new OutputStreamWriter(out, charset);
			CSVPrinter pt = new CSVPrinter(writer, format)
		){
		    for (final String[] row : rows) {
		        pt.printRecord(Arrays.asList(row));
		    }
		} finally {
			l.s.common.util.IoUtil.close(out);
		}
		return this;
	}
	
	public CSVWrite addRow(String[] row){
		this.rows.add(row);
		return this;
	}
	
	public int rowSize(){
		return rows.size();
	}
}
