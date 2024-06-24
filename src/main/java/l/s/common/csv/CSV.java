package l.s.common.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

public class CSV {
	
	private CSV(){
	}
	
	public static CSVWrite newCsv(){
		CSVFormat format = CSVFormat.EXCEL.builder().setQuoteMode(QuoteMode.ALL).build();
		return newCsv(format);
	}
	
	public static CSVWrite newCsv(String[] headers){
		CSVFormat format = CSVFormat.EXCEL.builder().setHeader(headers).setQuoteMode(QuoteMode.ALL).build();
		return newCsv(format);
	}
	
	public static CSVWrite newCsv(CSVFormat format){
		CSVWrite csv = new CSVWrite(format);
		csv.format = format;
		return csv;
	}
	
	public static CSVRead openCsv(String file, String charset) throws Exception{
		return openCsv(file, charset, false);
	}
	
	public static CSVRead openCsv(String file, String charset, boolean withHeader) throws Exception{
		return openCsv(new File(file), charset, withHeader);
	}
	
	public static CSVRead openCsv(String file, String charset, CSVFormat format) throws Exception{
		return openCsv(new File(file), charset, format);
	}
	
	public static CSVRead openCsv(File file, String charset) throws Exception{
		return openCsv(file, charset, false);
	}
	
	public static CSVRead openCsv(File file, String charset, boolean withHeader) throws Exception{
		CSVFormat format;
		if(withHeader){
			format = CSVFormat.EXCEL.builder().setHeader().build();
		}else{
			format = CSVFormat.EXCEL;
		}
		return openCsv(file, charset, format);
	}
	
	public static CSVRead openCsv(File file, String charset, CSVFormat format) throws Exception{
		return openCsv(file, charset, format, 0);
	}
	
	public static CSVRead openCsv(File file, String charset, CSVFormat format, final long characterOffset) throws Exception{
		CSVRead csv = new CSVRead();
		csv.format = format;
		
		if(!file.exists()){
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		
		FileInputStream in = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(in, charset);
		CSVParser ps = new CSVParser(reader, format, characterOffset, 1);
		
		try {
			if(format.getHeader() != null){
				Map<String, Integer> headermap = ps.getHeaderMap();
				csv.header = new CSVHeader(headermap);
			}
		    for (final CSVRecord record : ps) {
		        csv.rows.add(new CSVRow(record));
		    }
		} finally {
		    ps.close();
		    reader.close();
		    in.close();
		}
		return csv;
		
	}
	
}
