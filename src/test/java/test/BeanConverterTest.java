package test;

import l.s.common.bean.BeanConverter;
import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanConverterTest {

    public static void main(String[] args){
        BeanConverter c = new BeanConverter();
        c.addConverter(new Converter<Date, String>() {
            @Override
            public String convert(Date source) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss.SSSXXX");
                return f.format(source);
            }
        });

        String str = c.convert(new Date(), String.class);
        System.out.println(str);
    }
}
