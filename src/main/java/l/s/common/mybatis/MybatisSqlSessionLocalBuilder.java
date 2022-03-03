package l.s.common.mybatis;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MybatisSqlSessionLocalBuilder {

	public static SqlSessionLocal build(String driver, String url, String username, String password, boolean autoCommit){
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driver);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		
		return build(dataSource, autoCommit);
	}
	
	public static SqlSessionLocal build(BasicDataSource dataSource, boolean autoCommit){
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		
		Configuration configuration = new Configuration(new Environment(MybatisSqlSessionLocalBuilder.class.getSimpleName(), transactionFactory, dataSource));
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory f = builder.build(configuration);
		
		return SqlSessionLocal._init(f, autoCommit);
	}
	
}
