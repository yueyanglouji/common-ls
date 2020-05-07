GroovySQL

1. Introduction

   This is a [mybatis](https://mybatis.org/mybatis-3/) plugin with [groovy](http://www.groovy-lang.org/) script language driver.

2. Getting Started

   1. Add jar file to class path.

   2. Configuration the language driver.

      - In spring project you can config SqlSessionFactory like this:

        ```
            @Bean
            SqlSessionFactory sqlSessionFactory(DataSource dataSource){
                SqlSessionFactoryBean r = new SqlSessionFactoryBean();
                r.dataSource = dataSource;
                org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
                configuration.setDefaultScriptingLanguage(MybatisGroovyLanguageDriver.class);
                configuration.getTypeHandlerRegistry().register(GString.class, GStringTypeHandler.class);
                r.configuration = configuration
                r.getObject();
            }
        ```

      - In springboot:

        1. Create a ConfigurationCustomizer class

        ```java
        public class MybatisConfigurationCustomizer implements ConfigurationCustomizer {
            @Override
            public void customize(Configuration configuration) {
                configuration.setDefaultScriptingLanguage(MybatisGroovyLanguageDriver.class);
                configuration.getTypeHandlerRegistry().register(GString.class, GStringTypeHandler.class);
            }
        }
        ```

        2. Config bean

        ```java
        @Bean
            MybatisConfigurationCustomizer mybatisConfigurationCustomizer(){
                MybatisConfigurationCustomizer r = new MybatisConfigurationCustomizer();
                r;
            }
        ```

      - In other project you can create SqlSessionFactory like this:

        ```java
        public SqlSessionFactory produceFactory(DataSource dataSource) throws Exception{
                Configuration configuration = new Configuration();
                TransactionFactory transactionFactory = new JdbcTransactionFactory();
                String environment = SqlSessionFactoryProvider.class.getSimpleName();
                configuration.setEnvironment(new Environment(environment, transactionFactory, dataSource));
                configuration.setDefaultScriptingLanguage(MybatisGroovyLanguageDriver.class);
                configuration.getTypeHandlerRegistry().register(GString.class, GStringTypeHandler.class);
                configuration.addMapper(This is the mapper interface there.);
                SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(configuration);
                return factory;
        
            }
        ```

   3. Make interface

      - In spring and springboot like this:

        This sample file is a groovy file. It's also can write the same code in java file, but the multiple rows string is not allow in old java version. 

      ```java
      @Repository
      @Mapper
      interface MyRepository {
      
          @Select('''
              groovy{
                  select{
                      yield "table_field1"
                      yield "table_field2"
                  }
                  from{
                      yield "table1"
                  }
                  left join{
                      yield "table2"
                  }
                  on{
                      yield "table1.id_source = table2.id"
                  }
                  where{
                      and{
                          if($param.source_text){
                          	yield "source_text=#{source_text}"
                          }
                          if($param.source_text){
                          	yield "language=#{language}"
                          }
                      }
                  }
              }
          ''')
          List<QueryResult> test(@Param("source_text") String source_text, @Param("language") String language);
      }
      ```

3. Document

   1. select

      ```java
      groovy{
          select{
             count(-sns_info.'*')
          }
          from{
          	-sns_info
          }
      }
      ```

      ```
      groovy{
          select{
             yield "count(sns_info.*)"
          }
          from{
          	yield "sns_info"
          }
      }
      ```

      The table name can write like `-sns_info` or `yield "sns_info"`.

   2. left join

      ```
      groovy{
          select{
          	count(-sns_info.'*')
          }
          from{
          	-sns_info
          }
          left join{
          	yield "jointable"
          }
          on{
          	yield "jointable.a = sns_info.id"
      	}
      }
      ```

   3. left join a sub select

      ```
      groovy{
          select{
          	count(-sns_info.'*')
          }
          from{
          	-sns_info
          }
          left join{
              sub{
                  select{
                  	yield "*"
                  }
                  from{
                  	yield "table"
                  } 
              } >> "table2"
          }
          on{
          	yield "table2.a = sns_info.id"
      	}
      }
      ```

   4. where

      ```
      groovy{
          select{
          	count(-sns_info.'*')
          }
          from{
          	-sns_info
          }
          where{
              and{
                  if($param.type_id){
                      yield "sns_type = #{type_id}"
                  }
                  if($param.date1){
                      yield "entry_date >= #{date1}"
                  }
                  if($param.date2){
                      yield "entry_date <= #{date2}"
                  }
                  or{
                      if($param.type_id){
                          yield "sns_type = #{type_id}"
                      }
                      if($param.date1){
                          yield "entry_date >= #{date1}"
                      }
                      if($param.date2){
                          yield "entry_date <= #{date2}"
                      }
                  }
              }
          }
      }
      ```

      - $param - All the query parameter in this object. If use @param annotation the $param's item is the annotation's value, else the $param's item is the parameter's properties.
      - In there you can use the groovy language free.
      - and or operator  

   5. in

      ```
      groovy{
          select{
          	count(-sns_info.'*')
          }
          from{
          	-sns_info
          }
          where{
              and{
                  if($param.type_id){
                      yield "sns_type = #{type_id}"
                  }
                  yield "id in ('${$param.iterator.join("','")}')"
                  yield "id in ("
                  for(int i=0;i<$param.iterator.size();i++){
                      yieldAppend "#{iterator[${i}]}"
                      if(i!=$param.iterator.size()-1){
                      	yieldAppend ", "
                  	}
                  }
                  yieldAppend ")"
              }
          }
      }
      ```

   6. limit

      ```
      groovy{
          select{
          	yield "*"
          }
          from{
          	-sns_info
          }
          limit "#{start}", "#{length}"
      }
      ```

   7. insert

      ```
      groovy{
          insert into{
          	-table
          }
          field{
              yield "field1"
              yield "field2"
              yield "field3"
          }
          values{
              yield "#{filed1}"
              yield "#{filed2}"
              yield "#{filed3}"
          }
      }
      ```

   8. update

      ```
      groovy{
          update{
          	yield "m_user"
          }
          set{
          	yield "password=#{new_password}"
          }
          where{
              and{
                  yield "user_id=#{user_id}"
                  yield "password=#{old_password}"
              }
          }
      }
      ```

   9. delete

      ```
      groovy{
          delete from{
          	-sns_email
          }
          where{
          	yield "snsid=#{sns_info_auto_id}"
          }
      }
      ```

   10. $ and #

       ```
       groovy{
           delete from{
           	-sns_email
           }
           where{
           	yield "snsid=#{sns_info_auto_id}"
           }
       }
       ```

       ```
       groovy{
           delete from{
           	-sns_email
           }
           where{
           	yield "snsid='${$param.sns_info_auto_id}'"
           }
       }
       ```

       

