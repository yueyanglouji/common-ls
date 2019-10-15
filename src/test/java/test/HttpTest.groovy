package test

import l.s.common.excel.Excel
import l.s.common.httpclient.AsyncHttpClient
import l.s.common.httpclient.DownloadDevice
import l.s.common.httpclient.Response
import l.s.common.httpclient.ResponseHeader
import l.s.common.httpclient.Timeout
import l.s.common.quartz.QuartZ

class HttpTest {
    public static void main(String[] args) {

        int index = 1;

        while (true){
            try {
                AsyncHttpClient client = AsyncHttpClient.getNewInstance().connectTimenout(Timeout.ofSeconds(5)).useCookeStore(false);

                DownloadDevice r = client.connect("http://10.48.210.42:8080/WebQuery-Search/report/report.asp")
                        .param("LEVEL_SHITEI", "1")
                        .param("ORGANIZATION_CODE", "---")
                        .param("YEARMONTH", "201909")
                        .param("SEARCHFLAG", "1")
                        .param("DOWNLOADFLAG", "1")
                        .param("radio1", "38")
                        .param("EmployeeNo", "30041837")
                        .downloadPost(new FileOutputStream(new File("C:\\Users\\30041837\\Desktop\\test.xls")))

                while(r.isWaite()){
                    Thread.sleep(200);
                    //println r.getRate();
                }
                println r.getRate();

                println r.getOriginalResponse().getHeader().getHeaders();

                String header = r.getOriginalResponse().getHeader().getHeader("content-disposition")
                if(header != null){
                    println header;
                    break;
                }
            }catch(Exception e){

            }

        }



//        QuartZ quartZ = QuartZ.getInstance();
//        quartZ.submit(new Runnable() {
//            @Override
//            void run() {
//                client.connect("http://10.48.210.42:8080/WebQuery-Search/report/report.asp")
//                        .param("LEVEL_SHITEI", "1")
//                        .param("ORGANIZATION_CODE", "---")
//                        .post()
//            }
//        }, "0/1 * * * * ?");

    }
}

