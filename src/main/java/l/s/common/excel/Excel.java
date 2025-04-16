package l.s.common.excel;

import l.s.common.bean.BeanConverter;
import l.s.common.util.IoUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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

    File file;

    public static Excel open(InputStream in) throws Exception{
        try {
            byte[] bytes = readStream(in);
            return open(bytes);
        }finally {
            IoUtil.close(in);
        }
    }

    public static Excel open(InputStream in, String password) throws Exception{
        try {
            byte[] bytes = readStream(in);
            return open(bytes, password);
        }finally {
            IoUtil.close(in);
        }
    }

    public static Excel open(File f) throws Exception{
        try (
                InputStream in = Files.newInputStream(f.toPath())
        ){
            byte[] bytes = readStream(in);
            Excel excel =  open(bytes);
            excel.file = f;
            return excel;
        }
    }

    public static Excel open(File f, String password) throws Exception{
        try (
                InputStream in = Files.newInputStream(f.toPath())
        ){
            byte[] bytes = readStream(in);
            Excel excel =  open(bytes, password);
            excel.file = f;
            return excel;
        }
    }

    public static Excel open(byte[] byteArray) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArray));
        return  ex;
    }

    public static Excel open(byte[] byteArray, String password) throws Exception{
        Excel ex = new Excel();
        ex.workbook = WorkbookFactory.create(new ByteArrayInputStream(byteArray), password);
        return  ex;
    }

    private static byte[] readStream(InputStream in) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = -1;
        while ((n = in.read(buffer)) > 0){
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    public static Excel newExcel(int type){
        Excel ex = new Excel();
        if(type == XLS){
            ex.workbook = new HSSFWorkbook();
        }else{
            ex.workbook = new XSSFWorkbook();
        }
        return  ex;
    }

    public int getNumberOfSheets(){
        return workbook.getNumberOfSheets();
    }

    public String getSheetName(int index){
        return workbook.getSheetName(index);
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
        return createSheet(name, -1);
    }

    public Excel createSheet(String name, int pos){
        this.activeSheet = this.workbook.createSheet(name);
        if(pos >= 0){
            this.workbook.setSheetOrder(name, pos);
        }
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
        CellStyle cellStyle = cell.getCellStyle();
        if(cellStyle == null){
            cellStyle = workbook.createCellStyle();
        }
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

    public Excel copyRow(int row, int toRow){
        CopyRow.copyRow(this.workbook, this.activeSheet, row, toRow);
        return this;
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
        //CellType type = cell.getCellType();
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
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
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

    public Excel save() throws Exception{
        if(file == null){
            throw new RuntimeException("Not read from a file.");
        }
        saveAs(file);
        return this;
    }

    private int numberColumn(String cell)
    {
        String regex = "^[A-Z]+";
        Pattern p = Pattern.compile(regex);
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
        String regex = "[0-9]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cell.toUpperCase());
        m.find();
        String row = m.group();
        return Integer.parseInt(row) - 1;
    }

    public String getFooter(FooterType footerType){
        Footer foo = this.activeSheet.getFooter();
        if(footerType == FooterType.LEFT){
            return foo.getLeft();
        }else if(footerType == FooterType.RIGHT){
            return foo.getRight();
        }else if(footerType == FooterType.CENTER){
            return foo.getCenter();
        }else {
            throw new RuntimeException("Not support FooterType");
        }
    }

    public Excel setFooter(String footer, FooterType footerType){
        Footer foo = this.activeSheet.getFooter();
        if(footerType == FooterType.LEFT){
            foo.setLeft(footer);
        }else if(footerType == FooterType.RIGHT){
            foo.setRight(footer);
        }else if(footerType == FooterType.CENTER){
            foo.setCenter(footer);
        }
        return this;
    }

    public Excel setPrintRange(int r0, int c0, int rn, int cn){
        int sheetIndex = this.workbook.getSheetIndex(this.activeSheet);
        this.workbook.setPrintArea(sheetIndex ,c0, cn, r0, rn);
        return this;
    }

    public Excel formula(int row, int column, String formula){
        Cell cell = this.getCell(row, column);
        CellStyle cellStyle = cell.getCellStyle();
        if(cellStyle == null){
            cellStyle = workbook.createCellStyle();
        }
        cell.setCellFormula(formula);
        cell.setCellStyle(cellStyle);
        return this;
    }

    public Excel formulaEvaluate(int row, int column){
        FormulaEvaluator formulaEvaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
        formulaEvaluator.evaluate(this.getCell(row, column));
        return this;
    }

    public Excel formulaEvaluateAll(){
        this.workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        return this;
    }

    /**
     * Include image in the Excel.
     * @param image image bytes
     * @param type image type
     * @param row row index
     * @param column column index
     * @param scaleWidth number of columns to use for this image. If this number is less than 0, then the scaleWith will be automatically calculated.
     * @param scaleHeight number of rows to use for this image. If this number is less than 0, then the scaleHeight will be automatically calculated.
     * @return this
     * @throws IOException * @throws IOException
     */
    public Excel image(byte[] image, PICTURE_TYPE type, int row, int column, double scaleWidth, double scaleHeight) throws IOException {
        if(scaleWidth <=0 && scaleHeight <= 0){
            image(image, type, row, column);
        }

        if(scaleWidth <= 0 || scaleHeight <= 0){
            BufferedImage read = ImageIO.read(new ByteArrayInputStream(image));
            int width1 = read.getWidth();
            int height1 = read.getHeight();

            int width2 = this.activeSheet.getColumnWidth(column);
            int height2 = getRow(row).getHeight();

            if(scaleWidth <= 0){
                scaleWidth = scaleHeight * height2 / (double)height1 * width1 / (double)width2;
            }
            else {
                scaleHeight = scaleWidth * width2 / (double)width1 * height1 / (double)height2;
            }
        }

        Picture pict = makePictrue(image, type, row, column);
        pict.resize(scaleWidth, scaleHeight);
        return this;
    }

    @Deprecated
    public Excel image(byte[] image, PICTURE_TYPE type, int row, int column, int width, int height, boolean useWidth) throws IOException {
        if(width <=0 && height <= 0){
            image(image, type, row, column);
        }

        int width2 = this.activeSheet.getColumnWidth(column);
        int height2 = getRow(row).getHeight();

        double scaleWidth;
        double scaleHeight;
        if(width <= 0){
            scaleWidth = -1;
            scaleHeight = height / (double)height2;
        }
        else if(height <= 0){
            scaleWidth = width / (double)width2;
            scaleHeight = -1;
        }else{
            scaleWidth = width / (double)width2;
            scaleHeight = height / (double)height2;
        }
        image(image, type, row, column, scaleWidth, scaleHeight);
        return this;
    }

    public Excel image(byte[] image, PICTURE_TYPE type, int row, int column) throws IOException {
        Picture pict = makePictrue(image, type, row, column);;
        pict.resize();
        return this;
    }

    private Picture makePictrue(byte[] image, PICTURE_TYPE type, int row, int column) {
        final CreationHelper helper = workbook.getCreationHelper();
        final Drawing<?> drawing = activeSheet.createDrawingPatriarch();

        final ClientAnchor anchor = helper.createClientAnchor();
        anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_AND_RESIZE );


        final int pictureIndex = workbook.addPicture(image, type.value);

        anchor.setRow1( row );
        anchor.setCol1( column );
        anchor.setRow2( row + 1 );
        anchor.setCol2( column + 1 );
        return drawing.createPicture( anchor, pictureIndex );
    }

    public Excel setRowHeight(int row, short height){
        Row r = this.getRow(row);
        r.setHeight(height);
        return this;
    }

    public SheetConditionalFormatting rules(){
        return this.activeSheet.getSheetConditionalFormatting();
    }

    public enum PICTURE_TYPE{
        /** Mac EMF format */
        PICTURE_TYPE_EMF(2),

        /** Windows Meta File */
        PICTURE_TYPE_WMF(3),

        /** Mac PICT format */
        PICTURE_TYPE_PICT(4),

        /** JPEG format */
        PICTURE_TYPE_JPEG(5),

        /** PNG format */
        PICTURE_TYPE_PNG(6),

        /** Device independent bitmap */
        PICTURE_TYPE_DIB(7);

        int value = 6;

        PICTURE_TYPE(int value){
            this.value = value;
        }
    }
}
