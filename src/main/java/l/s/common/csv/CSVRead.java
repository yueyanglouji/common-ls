package l.s.common.csv;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;

public class CSVRead {

	CSVFormat format;
	
	CSVHeader header;
	
	List<CSVRow> rows;
	
	CSVRead() {
		this.rows = new ArrayList<>();
	}
	
	public CSVRead withDelimiter(char ch){
		format = format.builder().setDelimiter(ch).build();
		return this;
	}
	
	public CSVRead withQuoteMode(l.s.common.csv.QuoteMode mode){
		return withQuoteMode(mode.getValue());
	}
	
	public CSVRead withQuoteMode(QuoteMode mode){
		format = format.builder().setQuoteMode(mode).build();
		return this;
	}
	
	public CSVFormat getFormat() {
		return format;
	}

	public void setFormat(CSVFormat format) {
		this.format = format;
	}

	public CSVHeader getHeader() {
		return header;
	}

	public List<CSVRow> getRows() {
		return rows;
	}
	
	public CSVWrite toWrite(){
		CSVWrite csv = new CSVWrite(format);
		for (CSVRow row : rows) {
			csv.addRow(row.toArray());
		}
		
		return csv;
	}
}
