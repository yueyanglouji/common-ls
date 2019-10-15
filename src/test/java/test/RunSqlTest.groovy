package test

import l.s.common.groovy.DefaultGroovyDeffinitionObject
import l.s.common.groovy.GroovyS
import l.s.common.groovy.sql.SQL

class RunSqlTest {
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
                            yield "jointable.a = sns_info.id"
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
                            }
                            
                        }
                        limit "#{start}", "#{length}"
                    }
                    
                    '''
        )

        println s.toSqlString();

    }
}

