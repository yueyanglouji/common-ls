package test

import l.s.common.groovy.json.JsonNode

import java.text.DecimalFormat
import java.text.NumberFormat

class BeanConverterGroovyTest {
    static void main(String[] args){
        JsonNode json = JsonNode.create();

        json{
            date new Date();
            f 199.423899529
        }

        json.addConverter{
            Date it ->
                return "123"
        }
        json.addConverter {
            double it ->
                return 199
        }

        DecimalFormat f = new DecimalFormat("0000.##");

        System.out.println(f.format(199))
        System.out.println(org.noggit.JSONUtil.toJSON(new BigDecimal(f.format(199))))

        System.out.println(json.toJsonString())
    }
}
