package test


import l.s.common.groovy.GroovyS
import l.s.common.groovy.sql.SQL
import org.codehaus.groovy.runtime.GStringImpl

class RunSqlInsertTest {
    public static void  main(String[] args){

        SQL s = SQL.createRootSql();
        s.setParam(new HashMap<>());
        s.getParam().put("type_id", "param aaa")
        s.getParam().put("date1", "param aaa")
        s.getParam().put("date2", "param aaa")
        s.getParam().put("iterator", ["a", "b", "c"])
        GroovyS.connect()
                .param("groovy", s)run(
                '''
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
                            yield "filed1"
                            yield "filed2"
                        }
                    }
                    
                    '''
        )

        println s.toSqlString();


        println new GStringImpl([] as Object[], [''] as String[]).toString()

    }
}

