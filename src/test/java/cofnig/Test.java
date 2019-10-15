package cofnig;

import l.s.common.config.ConfigProperty;
import org.springframework.core.io.ClassPathResource;

public class Test {

    public static void main(String[] args) throws Exception{
        ConfigProperty configProperty = ConfigProperty.getInstance();
        configProperty.setFileEncoding("UTF-8");
        configProperty.setLocation(new ClassPathResource("/config.properties"));
        configProperty.loadProperties();

        int prop =  configProperty.getProperty("a", null);
        System.out.println(prop);
    }
}
