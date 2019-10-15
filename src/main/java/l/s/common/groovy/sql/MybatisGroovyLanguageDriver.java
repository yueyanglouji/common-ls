package l.s.common.groovy.sql;

import groovy.lang.Writable;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import l.s.common.groovy.GroovyS;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

public class MybatisGroovyLanguageDriver extends XMLLanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        try {
            if(script.startsWith("groovy")){
                return new GroovySqlSource(configuration, script, parameterType);
            }else{
                return super.createSqlSource(configuration, script, parameterType);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
