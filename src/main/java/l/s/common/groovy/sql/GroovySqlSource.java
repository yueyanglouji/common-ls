package l.s.common.groovy.sql;

import l.s.common.groovy.GroovyS;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GroovySqlSource implements SqlSource {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Configuration configuration;

    private String script;

    private Class<?> parameterType;

    public GroovySqlSource(Configuration configuration, String script, Class<?> parameterType) {
        this.configuration = configuration;
        this.script = script;
        this.parameterType = parameterType;
    }
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        try {
            SQL s = SQL.createRootSql(parameterObject);
            GroovyS.connect().param("groovy", s).run(script);
            String sql = s.toSqlString();
            if(log.isInfoEnabled()){
                log.info("groovy script : " + sql);
            }
            SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
            Class<?> clazz = parameterType == null ? Object.class : parameterType;
            SqlSource staticSqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<String, Object>());
            return staticSqlSource.getBoundSql(parameterObject);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
