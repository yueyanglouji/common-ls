package test;

import l.s.common.bean.BeanConnector;
import l.s.common.json.JsonNode;
import l.s.common.util.ReflectUtil;
import test.jsondef.CommentDef;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

public class JSonNodeJavaTest {
    public static void main(String[] args) throws Exception{
        String json = loadJson();
        JsonNode node = JsonNode.createFromJsonString(json);
        JsonNode listnode = node.trip("./comments");
        List<CommentDef> list = listnode.toArray(CommentDef.class);

        System.out.println("abc");
    }

    private static String loadJson(){
        StringBuilder builder = new StringBuilder();
        Scanner sc = null;
        try{
            sc = new Scanner(new File("D:\\lixiaobao\\git\\common-ls\\src\\test\\resources\\comment_defination.json"), "UTF-8");
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                builder.append(line);
                builder.append("\n");
            }
            sc.close();
            return builder.toString();
        }catch (Exception e){
            throw new RuntimeException("config file error", e);
        }finally {

        }
    }
}
