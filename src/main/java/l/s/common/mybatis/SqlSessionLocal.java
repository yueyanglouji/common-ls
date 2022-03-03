package l.s.common.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SqlSessionLocal {

	private static final ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
	
	private SqlSession getSession(){
		SqlSession session = threadLocal.get();
		if(session == null){
			session = factory.openSession(autoCommit);
			threadLocal.set(session);
		}
		return session;
	}
	
	private final SqlSessionFactory factory;
	
	private boolean autoCommit = true;
	

	public SqlSession getOriginalSqlSession(){
		return getSession();
	}
	
	public SqlSessionLocal newInstance(BasicDataSource dataSource, boolean autoCommit){
		return MybatisSqlSessionLocalBuilder.build(dataSource, autoCommit);
	}
	
	private SqlSessionLocal(SqlSessionFactory f, boolean autoCommit){
		this.autoCommit = autoCommit;
		this.factory = f;
	}
	
	public static SqlSessionLocal _init(SqlSessionFactory f, boolean autoCommit){
		return new SqlSessionLocal(f, autoCommit);
	}
	
	public boolean hasStatement(String statement){
		
		return getSession().getConfiguration().hasStatement(statement);
	}
	
	public void statement(String statement, String sql){
		statement(statement, sql, LinkedHashMap.class);
	}
	
	public synchronized void statement(String statement, String sql, Class<?> resultType){
		
		if(hasStatement(statement)){
			//warn
			return;
		}
		
		sql = sql.trim();
		SqlCommandType type;
		if(sql.toLowerCase().startsWith("insert")){
			type = SqlCommandType.INSERT;
		}
		else if(sql.toLowerCase().startsWith("update")){
			type = SqlCommandType.UPDATE;
		}
		else if(sql.toLowerCase().startsWith("select")){
			type = SqlCommandType.SELECT;
		}
		else if(sql.toLowerCase().startsWith("delete")){
			type = SqlCommandType.DELETE;
		}
		else{
			type = SqlCommandType.UNKNOWN;
		}
		
		
		SqlSourceBuilder builder = new SqlSourceBuilder(getSession().getConfiguration());
		SqlSource sqlSource = builder.parse(sql, HashMap.class, new HashMap<String, Object>());
		
		
		if(type == SqlCommandType.SELECT){
			ResultMap.Builder mapBuilder = new ResultMap.Builder(getSession().getConfiguration(), statement, resultType, new ArrayList<ResultMapping>(), null);
			ResultMap map = mapBuilder.build();
			//session.getConfiguration().addResultMap(mapBuilder.build());
			
			MappedStatement.Builder statementBuilder = new MappedStatement.Builder(getSession().getConfiguration(), statement, sqlSource, type);
			List<ResultMap> list = new ArrayList<>();
			list.add(map);
			statementBuilder.resultMaps(list);
			
			getSession().getConfiguration().addMappedStatement(statementBuilder.build());
		}else{
			MappedStatement.Builder statementBuilder = new MappedStatement.Builder(getSession().getConfiguration(), statement, sqlSource, type);
			
			getSession().getConfiguration().addMappedStatement(statementBuilder.build());
		}
		
	}
	
	public int insert(String statement, Map<String, Object> param){
		
		return getSession().insert(statement, param);
	}
	
	public int update(String statement, Map<String, Object> param){
		
		return getSession().update(statement, param);
	}
	
	public int delete(String statement, Map<String, Object> param){
		
		return getSession().delete(statement, param);
	}
	
	public List<Map<String, Object>> selectList(String statement, Map<String, Object> param){
		
		return getSession().selectList(statement, param);
	}
	
	public <T> T selectOne(String statement, Map<String, Object> param){
		
		return getSession().selectOne(statement, param);
	}
	
	public void rollback(){
		getSession().rollback();
	}
	
	public void commit(){
		getSession().commit();
	}
	
	public void close(){
		getSession().close();
	}
}
