package test

import groovy.util.logging.Log4j2
import groovy.util.logging.Slf4j
import l.s.common.excel.Excel
import l.s.common.httpclient.AsyncHttpClient
import l.s.common.httpclient.DownloadDevice
import l.s.common.httpclient.Response
import l.s.common.httpclient.ResponseHeader
import l.s.common.quartz.QuartZ
import org.apache.hc.core5.util.Timeout

@Slf4j
class HttpTest {
    public static void main(String[] args) {

        int index = 1;

        while (true){
            try {
                AsyncHttpClient client = AsyncHttpClient.getNewInstance().connectTimeout(Timeout.ofSeconds(300)).useCookeStore(false);

                Date start = new Date();
                DownloadDevice r = client.connect("http://10.48.210.42:8080/WebQuery-Search/report/report.asp")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:71.0) Gecko/20100101 Firefox/71.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Origin", "http://10.48.210.42:8080")
                .header("Referer", "http://10.48.210.42:8080/WebQuery-Search/report/report.asp")
                .header("Cookie", "ASPSESSIONIDQSACSDCT=FJDLNHEBCKFKELLMAAGJENAG")
                .header("Upgrade-Insecure-Requests", "1")
                        .param("LEVEL_SHITEI", "1")
                        .param("ORGANIZATION_CODE", "---")
                        .param("YEARMONTH", "201912")
                        .param("SEARCHFLAG", "1")
                        .param("DOWNLOADFLAG", "1")
                        .param("radio1", "38")
                        .param("EmployeeNo", "30041837")
                        //.connectTimeout(Timeout.ofMilliseconds(100000))
                        .downloadPost(new FileOutputStream(new File("/Users/admin/lixiaobao/tmp/test.xls")))

                while(r.isWaite()){
                    Thread.sleep(200);
//                    println r.getRate();
                }
                println r.getRate();

                println r.getOriginalResponse()?.getHeader()?.getHeaders();

                String header = r.getOriginalResponse()?.getHeader()?.getHeader("content-disposition")
                //
                if(header != null && new Date().getTime() - start.getTime() > 5000){
                    println header;
                    break;
                }
            }catch(Exception e){
                e.printStackTrace()
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

