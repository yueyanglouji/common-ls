package test

import l.s.common.excel.Excel

class PoiTest {

    public static void main(String[] args){
        Excel excel = Excel.open(new File("/Users/yueyanglouji/Desktop/importdata.xlsx"));
        excel.sheet(0);
        String value = excel.getCellValueAsString("C22")
        println value
    }

}
