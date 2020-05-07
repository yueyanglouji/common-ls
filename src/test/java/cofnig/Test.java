package cofnig;

import l.s.common.config.ConfigProperty;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;
import java.util.Enumeration;

public class Test {

    public static void main(String[] args) throws Exception{
        ConfigProperty configProperty = ConfigProperty.getInstance();
        configProperty.setFileEncoding("UTF-8");
        configProperty.setLocation(new ClassPathResource("/config.properties"));
        configProperty.loadProperties();

        String prop =  configProperty.getProperty("a", null);
        System.out.println(prop);

        System.out.println(ConfigProperty.class.getClassLoader());
        System.out.println(Test.class.getClassLoader());

        Enumeration<URL> em = Test.class.getClassLoader().getResources("config.properties");
        while (em.hasMoreElements()){
            URL url = em.nextElement();
            System.out.println(url);
        }

    }
}
