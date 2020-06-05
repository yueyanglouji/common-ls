package tmp

import l.s.common.httpclient.AsyncHttpClient
import l.s.common.httpclient.Response
import org.apache.hc.core5.util.Timeout

class BasicAuth {
    public static void main(String[] args) {
        AsyncHttpClient client = AsyncHttpClient.getNewInstance().connectTimeout(Timeout.ofSeconds(300)).useCookeStore(false);
        Response r = client.connect("http://intra01-ysl.jp.ykgw.net/sobaya/fyds_interface/connection/yuep_return_prop_no.php")
        .useResponseCharset("euc-jp")
                .get()

        println r.header.headers
        println r.content.content
    }
}
