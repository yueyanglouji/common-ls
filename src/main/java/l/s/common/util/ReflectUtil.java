package l.s.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReflectUtil {

	/**
	 * 一次寻找子类 和超类中的方法 未找到则返回null;
	 * 
	 * @param cl
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public Method getDeclaredMethod(Class<?> cl, String methodName,
			Class<?>... parameterTypes) {

		do {
			try {
				Method method = cl
						.getDeclaredMethod(methodName, parameterTypes);
				return method;
			} catch (Exception e) {
			}
		} while ((cl = cl.getSuperclass()) != null);

		return null;
	}

	/**
	 * 一次寻找子类 和超类中的方法 未找到则返回null;
	 * 
	 * @param cl
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public List<Method> getDeclaredMethods(Class<?> cl) {
		List<Method> list = new ArrayList<>();
		do {
			try {
				Method[] methods = cl.getDeclaredMethods();
				if(methods!=null){
					for(int i=0;i<methods.length;i++){
						list.add(methods[i]);
					}
				}
			} catch (Exception e) {
			}
		} while ((cl = cl.getSuperclass()) != null);

		return list;
	}
	
	/**
	 * 获取所有子类 父类中的属性，如果子类父类中有同名的属性，子类的field会优先于父类属性，排在返回值Filed[]数组中的前面。
	 * 
	 * @param cl
	 * @return
	 */
	public Field[] getDeclaredFields(Class<?> cl) {
        List<Field> list = new ArrayList<Field>();
		do {
			try {
				Field[] field = cl.getDeclaredFields();
				if (field == null) {
					continue;
				}
				for (int i = 0; i < field.length; i++) {
					list.add(field[i]);
				}
			} catch (Exception e) {
			}
		} while ((cl = cl.getSuperclass()) != null);
		if (list.size() == 0) {
			return null;
		} else {
			Field[] fs = new Field[list.size()];
			return list.toArray(fs);
		}
    }

	public Field getDeclaredField(Class<?> cl, String fieldName) {

		do {
			try {
				Field field = cl.getDeclaredField(fieldName);
				return field;
			} catch (Exception e) {
			}
		} while ((cl = cl.getSuperclass()) != null);

		return null;
	}

	public Class<?> loadClass(String classname) {
		Class<?> cl;
		try {
			cl = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return cl;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object o, String fieldName, Object value) {
		Class<?> cl = o.getClass();
		if (Map.class.isAssignableFrom(cl)) {
			@SuppressWarnings("rawtypes")
			Map map = (Map) o;
			map.put(fieldName, value);
		} else {
			Field field = getDeclaredField(cl, fieldName);
			String methodName = getMethodName(fieldName, "set");
			Method method = getDeclaredMethod(cl, methodName, field.getType());
			if (method == null) {
				throw new RuntimeException("method:" + methodName
						+ " not found exception.!");
			}
			try {
				method.invoke(o, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Object getValue(Object o, String fieldName) {
		Class<?> cl = o.getClass();
		if (Map.class.isAssignableFrom(cl)) {
			@SuppressWarnings("rawtypes")
			Map map = (Map) o;
			return map.get(fieldName);
		} else {
			String methodName = getMethodName(fieldName, "get");
			Method method = getDeclaredMethod(cl, methodName);
			try {
				return method.invoke(o);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}
	
	private String getMethodName(String fieldName, String pre){
		if(fieldName.length() > 1){
			if((fieldName.charAt(1) + "").matches("[A-Z]")){
				return pre + fieldName;
			}
		}
		return pre + changeFirstChar2Upper(fieldName);
	}

	
	public String changeFirstChar2Upper(String str){
		if(str!=null && !str.equals("")){
			String first = str.substring(0,1);
			return first.toUpperCase()+str.substring(1);
		}else{
			return null;
		}
	}
	
	public String changeFirstChar2Lower(String str){
		if(str!=null && !str.equals("")){
			String first = str.substring(0,1);
			return first.toLowerCase()+str.substring(1);
		}else{
			return null;
		}
	}
	
	public Object clone(Object o) throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
		objectOutputStream.writeObject(o);
		objectOutputStream.flush();
		objectOutputStream.close();
		
		out.flush();
		byte[] b = out.toByteArray();
		out.close();
		
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		ObjectInputStream objectInputStream = new ObjectInputStream(in);
		Object newojb = objectInputStream.readObject();
		objectInputStream.close();
		in.close();
		
		return newojb;
	}
}
