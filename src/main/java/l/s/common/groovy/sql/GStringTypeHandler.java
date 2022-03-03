package l.s.common.groovy.sql;

import groovy.lang.GString;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.codehaus.groovy.runtime.GStringImpl;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GStringTypeHandler extends BaseTypeHandler<GString>{

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GString parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public GString getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        if(string == null){
            return null;
        }
        return new GStringImpl(null, new String[]{string});
    }

    @Override
    public GString getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        if(string == null){
            return null;
        }
        return new GStringImpl(null, new String[]{string});
    }

    @Override
    public GString getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        if(string == null){
            return null;
        }
        return new GStringImpl(null, new String[]{string});
    }

}