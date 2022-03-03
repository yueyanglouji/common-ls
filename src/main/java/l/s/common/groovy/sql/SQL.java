package l.s.common.groovy.sql;

import groovy.lang.Closure;
import groovy.sql.Sql;
import l.s.common.groovy.DelegateClosure;
import l.s.common.groovy.GroovyObjectSupportDefault;
import l.s.common.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.*;

public class SQL extends GroovyObjectSupportDefault {

    static final short FIELD = 0;

    static final short TABLE = 1;

    static final short KEYWORD = 2;

    static final short ALIAS = 3;

    static final short ROOT = 4;

    static final short TEXT = 5;

    static final short FUNCTION = 6;

    static final short KEYWORDCLOSURE = 7;

    static final short YIELD = 8;

    static final short INFUNCTION = 9;

    static final short YIELDSTRING = 10;

    static final short JOINYIELD = 11;

    static final short JOINYIELDSTRING = 12;

    private short type;

    private String indexKey;

    private String name;

    private Map<String, SQL> map;

    private List<SQL> functionArg;

    private SQL parent;

    private SQL root;

    private SQL closure;

    private int index;

    private Object param;

    public Object getParam(){
        return param;
    }

    public void setParam(Object param){
        this.param = param;
    }

    private SQL() {
        this.map = new LinkedHashMap<>();
        this.functionArg = new ArrayList<>();
    }

    public static SQL createRootSql(Object param){
        SQL sql = new SQL();
        sql.type = ROOT;
        sql.root = sql;
        sql.param = param;
        return  sql;
    }

    private SQL createSql(String name, short type, SQL closure){
        SQL sql = new SQL();
        sql.name = name;
        sql.type = type;
        sql.closure = closure;

        return sql;
    }

    private SQL createSql(String name, short type, boolean selfClosure){
        SQL sql = new SQL();
        sql.name = name;
        sql.type = type;
        if(selfClosure){
            sql.closure = sql;
        }
        return sql;
    }

    private void addChild(SQL sql){
        sql.parent = this;
        sql.root = root;
        sql.param = root.param;
        this.map.put(index + "", sql);
        sql.indexKey = index + "";
        index ++;
    }

    private void removeChild(SQL sql){
        this.map.remove(sql.indexKey);
    }

    public SQL getDeffField(String property){
        return map.get(property);
    }

    public Map<String, SQL> getAllDeffField(){
        return this.map;
    }

    public void putProperty(String property, SQL value){
        map.put(property, value);
    }

    @Override
    public Object getProperty(String property) {
        try {
            if(property.equals("$param")){
                return param;
            }
            SQL sql = this.createSql(property, FIELD, this.closure);
            this.addChild(sql);
            return sql;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Object invokeMethod(String name, Object args) {
        if(args.getClass().isArray()){
            Object[] arr = (Object[])args;
            if(arr.length == 1 && Closure.class.isAssignableFrom(arr[0].getClass())){
                Closure<Object> c = (Closure<Object>)arr[0];
                ReflectUtil reflectUtil = new ReflectUtil();
                Method m = reflectUtil.getDeclaredMethod(this.getClass(), name, Closure.class);
                if(m == null){
                    SQL df = this.createSql(name, KEYWORDCLOSURE, true);
                    this.addChild(df);
                    df.call(c);
                    return df;
                }
            }
            else if(arr.length == 1 && arr[0].getClass() == SQL.class){
                SQL sql = (SQL) arr[0];
                if(sql.type == KEYWORDCLOSURE){
                    sql.parent.removeChild(sql);
                    SQL keyword = this.createSql(name, KEYWORD, this.closure);
                    this.addChild(keyword);
                    keyword.addChild(sql);
                    return this;
                }
            }

            SQL sql = this.createSql(name, FUNCTION, this.closure);
            this.addChild(sql);

            for(int i=0;i<arr.length;i++){
                Object arg = arr[i];
                if(arg == null){
                    SQL argsql = this.createSql("null", TEXT, this.closure);
                    argsql.root = sql.root;
                    argsql.parent = sql;
                    sql.functionArg.add(argsql);
                }
                else if(arg.getClass() == String.class){
                    SQL argsql = this.createSql( arg.toString(), TEXT, this.closure);
                    argsql.root = sql.root;
                    argsql.parent = sql;
                    sql.functionArg.add(argsql);
                }
                else if(arg.getClass() == SQL.class){
                    SQL argsql = (SQL) arg;
                    argsql.parent.removeChild(argsql);
                    argsql.parent = sql;
                    argsql.root = sql.root;
                    sql.functionArg.add(argsql);
                }else{
                    SQL argsql = this.createSql( arg.toString(), TEXT, this.closure);
                    argsql.root = sql.root;
                    argsql.parent = sql;
                    sql.functionArg.add(argsql);
                }
            }
            return sql;
        }else{
            assert false;
        }
        return null;
    }

//    public Object positive(){
//
//    }

    private SQL getLast(){
        SQL sql = null;
        for(Map.Entry<String, SQL> e : map.entrySet()){
            sql = e.getValue();
        }
        return sql;
    }

    public Object minus(SQL sql){
        if(sql.type == FIELD){
            this.type = KEYWORD;
            while(sql.parent.type == FIELD){
                sql = sql.parent;
            }
            sql.type = TABLE;
            sql.parent.removeChild(sql);
            this.addChild(sql);
        }
        return this;
    }

    public Object negative(){
        if(this.type == FIELD){
            SQL sql = this;
            while(sql.parent.type == FIELD){
                sql = sql.parent;
            }
            sql.type = TABLE;
            return sql;
        }else{
            return this;
        }
    }

    public Object yieldAppend(String yield){
        if(this.map.size() > 0){
            SQL last = this.getLast();
            if(last.type == YIELD){
                SQL sql = this.createSql(yield, JOINYIELD, this.closure);
                last.addChild(sql);
                return sql;
            }
        }
        throw new RuntimeException("yieldAppend must next to yield");
    }

    public Object yield(String yield){
        SQL sql = this.createSql(yield, YIELD, this.closure);
        this.addChild(sql);
        return sql;
    }

    public Object yieldString(String yield){
        if(yield == null){
            return yield(yield);
        }
        SQL sql = this.createSql(yield, YIELDSTRING, this.closure);
        this.addChild(sql);
        return sql;
    }

    public Object distinct(String str){
        SQL sql = this.createSql("distinct", KEYWORD, this.closure);
        this.addChild(sql);

        SQL text = sql.createSql(str, TEXT, sql.closure);
        sql.addChild(text);
        return sql;
    }

    public Object distinct(int str){
        SQL sql = this.createSql("distinct", KEYWORD, this.closure);
        this.addChild(sql);

        SQL text = sql.createSql(str + "", TEXT, sql.closure);
        sql.addChild(text);
        return sql;
    }

    public Object distinct(Object str){
        SQL sql = this.createSql("distinct", KEYWORD, this.closure);
        this.addChild(sql);

        SQL text = sql.createSql(str.toString(), TEXT, sql.closure);
        sql.addChild(text);
        return sql;
    }

    public Object distinct(SQL ss){
        SQL sql = this.createSql("distinct", KEYWORD, this.closure);
        this.addChild(sql);

        ss.parent.removeChild(ss);
        sql.addChild(ss);
        return sql;
    }

    public Object distinct(Closure c){
        SQL df = this.createSql("distinct", KEYWORDCLOSURE, true);
        this.addChild(df);
        df.call(c);
        return df;
    }

    public Object rightShift(SQL sql){
        if(this.type == KEYWORDCLOSURE && this.name.toLowerCase().equals("sub")){
            sql.type = ALIAS;
        }else{
            sql.parent.removeChild(sql);
            sql.parent = this;
            sql.type = ALIAS;
            this.addChild(sql);
        }
        return sql;
    }

    public Object rightShift(String property){
        SQL sql = this.createSql(property, ALIAS, this.closure);
        if(this.type == KEYWORDCLOSURE && this.name.toLowerCase().equals("sub")){
            this.parent.addChild(sql);
        }else{
            this.addChild(sql);
        }
        return sql;
    }

    private boolean isFirst(){
        for(Map.Entry e : parent.map.entrySet()){
            if(e.getValue() == this){
                return true;
            }
            return false;
        }
        return false;
    }

    private String childToSqlString(){
        StringBuilder b = new StringBuilder();
        boolean isFirst = isFirst();
        if(type == FIELD){
            if(this.parent.type == KEYWORDCLOSURE){
                if(!isFirst){
                    b.append(", ");
                }
                b.append("\n");
            }
            else if(this.parent.type == TABLE){
                b.append(".");
            }
            if(this.name.equals("*")){
                b.append("*");
            }else{
                b.append("`" + this.name + "`");
            }
        }
        else if(type == ALIAS){
            b.append(" as ");
            b.append(this.name);
        }
        else if(type == TABLE){
            if(this.parent.type == KEYWORDCLOSURE){
                if(!isFirst){
                    b.append(", ");
                }
                b.append("\n");
            }
            b.append("`" + this.name + "`");
        }
        else if(type == KEYWORD){
            b.append("\n");
            b.append(this.name);
        }
        else if(type == TEXT){
            b.append(" ");
            b.append( this.name );
        }
        else if(type == FUNCTION){
            if(this.parent.type == KEYWORDCLOSURE){
                if(!isFirst){
                    b.append(", ");
                }
                b.append("\n");
            }
            else if(this.parent.type == FUNCTION){
                b.append(", ");
            }
            else if(this.parent.type == ROOT){
                b.append("\n");
            }
            b.append(buildFunction(false));
        }
        else if(type == KEYWORDCLOSURE){
            if(!this.name.toLowerCase().matches("sub|field|values")){
                if(parent.type != KEYWORD){
                    b.append("\n");
                }else{
                    b.append(" ");
                }
                b.append(this.name);
            }
        }
        else if(type == YIELD){
            if(this.parent.type == KEYWORDCLOSURE){
                if(this.parent.name.toLowerCase().matches("select|set|field|values")){
                    if(!isFirst){
                        b.append(", ");
                    }
                }
                b.append("\n");
            }

            b.append(this.name);
        }
        else if(type == YIELDSTRING){
            if(this.parent.type == KEYWORDCLOSURE){
                if(!isFirst){
                    b.append(", ");
                }
                b.append("\n");
            }
            String str = org.noggit.JSONUtil.toJSON(this.name);
            if(str != null){
                if(str.length() == 2){
                    str = "";
                }else{
                    str = str.substring(1, str.length() - 1);
                }
                str = str.replaceAll("'", "\\\\'");
            }
            b.append("'" + str + "'");
        }
        else if(type == JOINYIELD){
            b.append(this.name);
        }
        else if(type == JOINYIELDSTRING){
            String str = org.noggit.JSONUtil.toJSON(this.name);
            if(str != null){
                if(str.length() == 2){
                    str = "";
                }else{
                    str = str.substring(1, str.length() - 1);
                }
                str = str.replaceAll("'", "\\\\'");
            }
            b.append("'" + str + "'");
        }
        else{
            b.append("\n");
            b.append(this.name);
        }

        for(Map.Entry<String, SQL> entry : map.entrySet()){
            SQL child = entry.getValue();
            buildChild(child, b);
        }

        return b.toString();
    }

    public String toSqlString(){
        StringBuilder b = new StringBuilder();

        for(Map.Entry<String, SQL> entry : this.root.map.entrySet()){
            SQL child = entry.getValue();
            if(child.type == KEYWORDCLOSURE){
                buildChild(child, b);
            }else{
                b.append(child.childToSqlString());
            }
        }

        return b.toString();
    }

    private String buildAnd(){
        StringBuilder b = new StringBuilder();
        String pre = "";
        if(map.size() == 0){
            return null;
        }
        if(!this.isFirst() && this.parent.name.toLowerCase().equals("where")){
            pre += "\nand\n";
        }
        pre += "(";
        int i=0;

        boolean hasAppendChild = false;
        for(Map.Entry<String, SQL> entry : map.entrySet()){
            SQL child = entry.getValue();
            if(!hasAppendChild){
                hasAppendChild = buildChild(child, b);
            }else{
                buildChild(child, b, "\nand");
            }
            i++;
        }
        if(!b.toString().trim().equals("")){
            return pre + b.toString() + "\n)";
        }else{
            return null;
        }
    }

    private String buildOr(){
        StringBuilder b = new StringBuilder();
        String pre = "";
        if(map.size() == 0){
            return null;
        }
        if(!this.isFirst() && this.parent.name.toLowerCase().equals("where")){
            pre += "\nand\n";
        }
        pre += "(";
        int i=0;

        boolean hasAppendChild = false;
        for(Map.Entry<String, SQL> entry : map.entrySet()){
            SQL child = entry.getValue();
            if(!hasAppendChild){
                hasAppendChild = buildChild(child, b);
            }else{
                buildChild(child, b, "\nor");
            }
            i++;
        }
        if(!b.toString().trim().equals("")){
            return pre + b.toString() + "\n)";
        }else{
            return null;
        }
    }

    private boolean buildChild(SQL child, StringBuilder b){
        return  buildChild(child, b, "");
    }

    private boolean buildChild(SQL child, StringBuilder b, String preStr){
        if(preStr == null){
            preStr = "";
        }

        String str;
        if(child.type == KEYWORDCLOSURE){
            if(child.name.toLowerCase().equals("and")){
                str = child.buildAnd();
            }
            else if(child.name.toLowerCase().equals("or")){
                str = child.buildOr();
            }
            else if(child.name.toLowerCase().equals("sub")){
                if(child.parent.name!=null){
                    str = "\n(" + child.childToSqlString() + "\n)";
                }else{
                    str = child.childToSqlString();
                }
            }
            else if(child.name.toLowerCase().equals("where")){
                str = child.childToSqlString();
                if(str!=null && str.trim().toLowerCase().equals("where")){
                    str = null;
                }
            }
            else if(child.name.toLowerCase().equals("values")){
                str = "\nvalues\n(" + child.childToSqlString() + "\n)";
            }
            else if(child.name.toLowerCase().equals("field")){
                str = "\n(" + child.childToSqlString() + "\n)";
            }
            else{
                str = child.childToSqlString();
            }
        }
        else{
            str = child.childToSqlString();
        }

        if(str == null || str.trim().equals("")){
            return false;
        }else{
            b.append(preStr + str);
            return true;
        }
    }

    private String buildFunction(boolean em){
        StringBuilder b = new StringBuilder();
        if(this.type != FUNCTION){
            assert false;
        }
        b.append(name);

        if(em){
            b.append("(");
        }else{
            b.append(" ");
        }

        //SQL up = this.root.up;
        for(int i=0;i<functionArg.size();i++){
            SQL sql = this.createSql(null, INFUNCTION, null);
            sql.root = this.root;

            String str = functionArg.get(i).childToSqlString();
            str = str.replaceAll("\n", " ");
            b.append(str);
            if(i != functionArg.size() - 1){
                b.append(", ");
            }
        }
        if(em){
            b.append(")");
        }
        return b.toString();
    }
}
