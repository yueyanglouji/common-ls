package l.s.common.excel;

import l.s.common.bean.BeanConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Excel {

    public static final int XLSX = 0;

    public static final int XLS = 1;

    Workbook workbook;

    Sheet activeSheet;

    public static Excel open(InputStream in) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(in);
        return  ex;
    }

    public static Excel open(InputStream in, String password) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(in, password);
        return  ex;
    }

    public static Excel open(File f) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(f);
        return  ex;
    }

    public static Excel open(File f, String password) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(f, password);
        return  ex;
    }

    public static Excel newExcel(int type) throws Exception{
        Excel ex = new Excel();
        if(type == XLS){
            ex.workbook = new HSSFWorkbook();
        }else{
            ex.workbook = new XSSFWorkbook();
        }
        return  ex;
    }

    public Excel sheet(int index){
        this.activeSheet = workbook.getSheetAt(index);
        return this;
    }

    public Excel sheet(String name){
        this.activeSheet = workbook.getSheet(name);
        return this;
    }

    public Excel createSheet(String name){
        this.activeSheet = this.workbook.createSheet(name);
        return this;
    }

    public int getRowCount(){
        return this.activeSheet.getLastRowNum() + 1;
    }

    public Excel setCellValueText(String cell, String value){
        return setCellValueText(numberRow(cell), numberColumn(cell), value);
    }

    public Excel setCellValueText(int row, int column, String value){
        Cell cell = getCell(row, column);
        CreationHelper createHelper = workbook.getCreationHelper();
        cell.setCellValue(createHelper.createRichTextString(value));
        return this;
    }

    public Excel setCellValueDate(String cell, Date value, String format){
        return setCellValueDate(numberRow(cell), numberColumn(cell), value, format);
    }

    public Excel setCellValueDate(int row, int column, Date value, String format){
        Cell cell = getCell(row, column);
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return this;
    }

    public Excel setCellValueNumeric(String cell, double value){
        return setCellValueNumeric(numberRow(cell), numberColumn(cell), value);
    }

    public Excel setCellValueNumeric(int row, int column, double value){
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
        return this;
    }

    public Excel setCellValueBoolean(String cell, boolean value){
        return setCellValueBoolean(numberRow(cell), numberColumn(cell), value);
    }

    public Excel setCellValueBoolean(int row, int column, boolean value){
        Cell cell = getCell(row, column);
        cell.setCellValue(value);
        return this;
    }

    public Row getRow(int row){
        Row r = this.activeSheet.getRow(row);
        if(r == null){
            r = this.activeSheet.createRow(row);
        }
        return r;
    }

    public Cell getCell(String cell){
        return getCell(numberRow(cell), numberColumn(cell));
    }

    public Cell getCell(int row, int column){
        CellRangeAddress range = isInMergeRegin(row, column);
        if(range != null){
            row = range.getFirstRow();
            column = range.getFirstColumn();
        }
        Row r = getRow(row);
        Cell cell = r.getCell(column);
        if(cell == null){
            cell = r.createCell(column);
        }
        return cell;
    }

    private CellRangeAddress isInMergeRegin(int row, int colum){
        int sheetMergeCount = this.activeSheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = this.activeSheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow && colum >= firstColumn && colum <= lastColumn){
                return range;
            }
        }
        return null;
    }

    public String getTextCellValue(String cell){
        return getTextCellValue(numberRow(cell), numberColumn(cell));
    }

    public String getTextCellValue(int row, int column){
        Cell cell = getCell(row, column);
        CellType type = cell.getCellType();
        return cell.getStringCellValue();
    }

    public Date getDateCellValue(String cell){
        return getDateCellValue(numberRow(cell), numberColumn(cell));
    }

    public Date getDateCellValue(int row, int column){
        Cell cell = getCell(row, column);
        return cell.getDateCellValue();
    }

    public double getNumericCellValue(String cell){
        return getNumericCellValue(numberRow(cell), numberColumn(cell));
    }

    public double getNumericCellValue(int row, int column){
        Cell cell = getCell(row, column);
        return cell.getNumericCellValue();
    }

    public boolean getBooleanCellValue(String cell){
        return getBooleanCellValue(numberRow(cell), numberColumn(cell));
    }

    public boolean getBooleanCellValue(int row, int column){
        Cell cell = getCell(row, column);
        return cell.getBooleanCellValue();
    }

    public Object getCellValue(String cell){
        return getCellValue(numberRow(cell), numberColumn(cell));
    }

    public Object getCellValue(int row, int column){
        Cell cell = getCell(row, column);
        CellType type = cell.getCellType();
        if(type == CellType.STRING){
            return cell.getRichStringCellValue().getString();
        }
        else if(type == CellType.NUMERIC){
            if (DateUtil.isCellDateFormatted(cell)) {
                Date dt = cell.getDateCellValue();
                return dt;
            } else {
                double d = cell.getNumericCellValue();
                return d;
            }
        }
        else if(type == CellType.BOOLEAN){
            return cell.getBooleanCellValue();
        }
        else if(type == CellType.FORMULA){
            return cell.getCellFormula();
        }
        else if(type == CellType.BLANK){
            return "";
        }
        else{
            return "";
        }
    }

    public String getCellValueAsString(String cell){
        return getCellValueAsString(numberRow(cell), numberColumn(cell));
    }

    public String getCellValueAsString(int row, int column){
        BeanConverter c = BeanConverter.getDefault();
        Object o = getCell(row, column);
        if(o == null){
            return null;
        }

        if(c.canConvert(o, String.class)){
            return c.convert(o, String.class);
        }else{
            return o.toString();
        }
    }

    public Map<String, Object> getRow(int row, String[] keys){
        return getRow(row, keys, 0);
    }

    public Map<String, Object> getRow(int row, String[] keys, int startColumn){
        Map<String, Object> map = new HashMap<>();
        for(int i=0; i<keys.length;i++){
            String key = keys[i];
            Object value = getCellValue(row, startColumn + i);
            map.put(key, value);
        }
        return map;
    }

    public Excel mergeCells(String startCell, String endCell){
        return mergeCells(numberRow(startCell), numberColumn(startCell), numberRow(endCell), numberColumn(endCell));
    }

    public Excel mergeCells(int startRow, int startColumn, int endRow, int endColumn){
        CellRangeAddress addresses = new CellRangeAddress(startRow, endRow, startColumn, endColumn);
        this.activeSheet.addMergedRegion(addresses);
        return this;
    }

    public CellStyle createCellStyle(){
        return this.workbook.createCellStyle();
    }

//    public Excel setBorder(int startRow, int startColumn, int endRow, int endColumn){
//        this.activeSheet.getrow
//    }

    public Excel close() throws Exception{
        this.workbook.close();
        return this;
    }

    public Excel saveAs(File f) throws Exception{
        try (FileOutputStream fileOut = new FileOutputStream(f)) {
            workbook.write(fileOut);
        }
        return this;
    }

    public Excel saveAs(OutputStream out) throws Exception{
        workbook.write(out);
        return this;
    }

    private int numberColumn(String cell)
    {
        Pattern p = Pattern.compile("^[A-Z]+");
        Matcher m = p.matcher(cell.toUpperCase());
        m.find();
        String row = m.group();

        int sum = 0;
        for (int i = 0; i < row.length(); i++)
        {
            char ch = row.charAt(i);
            int weishu = row.length() - i;

            sum += (ch - 'A' + 1) * (long)Math.pow(26, weishu - 1);
        }

        return sum - 1;
    }

    private int numberRow(String cell)
    {
        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(cell.toUpperCase());
        m.find();
        String row = m.group();
        return Integer.parseInt(row) - 1;
    }
}
