package l.s.common.bean;

import javafx.beans.property.Property;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Properties;

public class MapSupportBeanWrapper extends BeanWrapperImpl {

    public MapSupportBeanWrapper(Object object) {
        super(object);
    }

//    public boolean isWritableProperty(String propertyName){
//        if(isSampleMap()){
//            return true;
//        }else{
//            return super.isWritableProperty(propertyName);
//        }
//    }
//
//    public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException{
//        if(isSampleMap()){
//            Map map = (Map)this.getWrappedInstance();
//            map.put(propertyName, value);
//        }else{
//            super.setPropertyValue(propertyName, value);
//        }
//    }
//
//    public boolean isReadableProperty(String propertyName){
//        if(isSampleMap()){
//            return true;
//        }else{
//            return super.isReadableProperty(propertyName);
//        }
//    }
//
//    public Object getPropertyValue(String propertyName) throws BeansException{
//        if(isSampleMap()){
//            Map map = (Map)this.getWrappedInstance();
//            return map.get(propertyName);
//        }else{
//            return super.getPropertyValue(propertyName);
//        }
//    }
//
//    private boolean isSampleMap(){
//        Class c = this.getWrappedClass();
//        if(Map.class.isAssignableFrom(this.getWrappedClass())){
//            return true;
//        }else{
//            return false;
//        }
//    }

}
