package l.s.common.mybatis;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class MybatisSqlSessionLocalBuilder {
	
	public static SqlSessionLocal build(DataSource dataSource, boolean autoCommit){
		
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		
		Configuration configuration = new Configuration(new Environment(MybatisSqlSessionLocalBuilder.class.getSimpleName(), transactionFactory, dataSource));
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory f = builder.build(configuration);
		
		return SqlSessionLocal._init(f, autoCommit);
	}

	public static SqlSessionLocal build(DataSource dataSource, boolean autoCommit, Class<?>... typeHandler){

		TransactionFactory transactionFactory = new JdbcTransactionFactory();

		Configuration configuration = new Configuration(new Environment(MybatisSqlSessionLocalBuilder.class.getSimpleName(), transactionFactory, dataSource));
		for(Class<?> c : typeHandler){
			configuration.getTypeHandlerRegistry().register(c);
		}
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory f = builder.build(configuration);

		return SqlSessionLocal._init(f, autoCommit);
	}
	
}
