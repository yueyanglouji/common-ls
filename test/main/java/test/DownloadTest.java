package test;

import org.apache.hc.core5.util.Timeout;

import l.s.common.httpclient.AsyncHttpClient;
import l.s.common.httpclient.DownloadDevice;
import l.s.common.httpclient.Response;

public class DownloadTest {

	public static void main(String[] args) throws Exception{
		AsyncHttpClient client = AsyncHttpClient.getNewInstance().connectTimenout(Timeout.ofSeconds(3));
		
		DownloadDevice d = client.connect("http://10.240.10.189:8080/downloadEml?code_no=&es_index=&es_typ=&es_id=&mail_id=5B59E1F2-AAA16988-739D51CF").downloadGet(System.out);
		
		while(d.isWaite()) {
			System.out.println(d.getRate());
			Thread.sleep(1000);
		}
		Response r = d.getOriginalResponse();
		System.out.println(r);
		System.out.println(r.getHeader().getHeaders());
		
		
	}
}
