package test


import groovy.util.logging.Slf4j
import l.s.common.httpclient.AsyncHttpClient
import l.s.common.httpclient.DownloadDevice
import l.s.common.httpclient.Response
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.util.Timeout

@Slf4j
class HuaweiTest {
    public static void main(String[] args) {


            try {
                AsyncHttpClient client = AsyncHttpClient.getNewInstance().connectTimeout(Timeout.ofSeconds(300)).useCookeStore(false);

                Date start = new Date();
//                {
//                    "text": "Example message",
//                    "attachments": [
//                        {
//                            "title": "Rocket.Chat",
//                            "title_link": "https://rocket.chat",
//                            "text": "Rocket.Chat, the best open source chat",
//                            "image_url": "/images/integration-attachment-example.png",
//                            "color": "#764FA5"
//                        }
//                ]
//                }
//                curl -X POST -H
//                'Content-Type: application/json'
//                --data
//                '{"text":"Example message",
//                "attachments":[{
//                "title":"Rocket.Chat",
//                "title_link":"https://rocket.chat",
//                "text":"Rocket.Chat,
//                the best open source chat",
//                "image_url":"/images/integration-attachment-example.png","color":"#764FA5"
//                }]}'
//                http://10.211.55.5:3000/hooks/rMK8GhEnXeecERt6N/Duv97BmvvYN6wqs3Pahk5rsndBYaTkHigCmtSsiMmjQ2hqN8
                Response r = client.connect("http://10.211.55.5:3000/hooks/rMK8GhEnXeecERt6N/Duv97BmvvYN6wqs3Pahk5rsndBYaTkHigCmtSsiMmjQ2hqN8")
                        .header("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        //.header("User-Agent", "curl/7.64.1")
                        //.header("Accept", "*/*")
                        //.header("Connection", "")
                        .header("Content-Encoding", "")
                        .stream('{"text":"中文测试 日本語テスト","attachments":[{"title":"Rocket.Chat","title_link":"https://rocket.chat","text":"Rocket.Chat, the best open source chat","color":"#764FA5"}]}')
                        .post()
                println r.getHeader().getHeaders()
                println r.getContent().getContent()

            }catch(Exception e){
                e.printStackTrace()
            }

    }
}

