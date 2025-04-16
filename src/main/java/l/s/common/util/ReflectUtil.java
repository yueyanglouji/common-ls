package l.s.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReflectUtil {

	/**
	 * Find method form class and parent class, null will return if not found.
	 */
	public static Method getDeclaredMethod(Class<?> cl, String methodName,
			Class<?>... parameterTypes) {

		do {
			try {
				return cl
						.getDeclaredMethod(methodName, parameterTypes);
			} catch (Exception e) {
				// nothing.
			}
		} while ((cl = cl.getSuperclass()) != null);

		return null;
	}

	/**
	 * Find method form class and parent class, null will return if not found.
	 */
	public static List<Method> getDeclaredMethods(Class<?> cl) {
		List<Method> list = new ArrayList<>();
		do {
			try {
				Method[] methods = cl.getDeclaredMethods();
				if(methods!=null){
					list.addAll(Arrays.asList(methods));
				}
			} catch (Exception e) {
				// nothing.
			}
		} while ((cl = cl.getSuperclass()) != null);

		return list;
	}
	
	/**
	 * Find all the field from class and parent class, if same name in class and parent class, in return value Field[], class field will sort front of parent field.
	 */
	public static Field[] getDeclaredFields(Class<?> cl) {
        List<Field> list = new ArrayList<Field>();
		do {
			try {
				Field[] field = cl.getDeclaredFields();
				if (field == null) {
					continue;
				}
				list.addAll(Arrays.asList(field));
			} catch (Exception e) {
				// nothing.
			}
		} while ((cl = cl.getSuperclass()) != null);
		if (list.size() == 0) {
			return null;
		} else {
			Field[] fs = new Field[list.size()];
			return list.toArray(fs);
		}
    }

	public static Field getDeclaredField(Class<?> cl, String fieldName) {

		do {
			try {
				return cl.getDeclaredField(fieldName);
			} catch (Exception e) {
				//nothing.
			}
		} while ((cl = cl.getSuperclass()) != null);

		return null;
	}

	public static Class<?> loadClass(String classname) {
		Class<?> cl;
		try {
			cl = Class.forName(classname);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return cl;
	}

	@SuppressWarnings("unchecked")
	public static void setValue(Object o, String fieldName, Object value) {
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

	public static Object getStaticValueByClassName(String className, String fieldName) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return getValue(aClass, aClass, fieldName);
	}
	public static Object getValue(Object o, String fieldName) {
		Class<?> cl = o.getClass();
		return getValue(cl, o, fieldName);
	}
	public static Object getValue(Class<?> cl, Object o, String fieldName) {
		if (Map.class.isAssignableFrom(cl)) {
			@SuppressWarnings("rawtypes")
			Map map = (Map) o;
			return map.get(fieldName);
		} else {
			String methodName = getMethodName(fieldName, "get");
			Method method = getDeclaredMethod(cl, methodName);
			if(method != null){
				try {
					return method.invoke(o);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			Field declaredField = getDeclaredField(cl, fieldName);
			declaredField.setAccessible(true);
            try {
                return declaredField.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

	}
	
	private static String getMethodName(String fieldName, String pre){
		if(fieldName.length() > 1){
			if((fieldName.charAt(1) + "").matches("[A-Z]")){
				return pre + fieldName;
			}
		}
		return pre + changeFirstChar2Upper(fieldName);
	}

	
	public static String changeFirstChar2Upper(String str){
		if(str!=null && !str.equals("")){
			String first = str.substring(0,1);
			return first.toUpperCase()+str.substring(1);
		}else{
			return null;
		}
	}
	
	public static String changeFirstChar2Lower(String str){
		if(str!=null && !str.equals("")){
			String first = str.substring(0,1);
			return first.toLowerCase()+str.substring(1);
		}else{
			return null;
		}
	}
	
	public static Object clone(Object o) throws Exception{
		ByteArrayOutputStream out = null;
		ObjectOutputStream objectOutputStream = null;
		ByteArrayInputStream in = null;
		ObjectInputStream objectInputStream = null;
		try{
			out = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(out);
			objectOutputStream.writeObject(o);
			objectOutputStream.flush();
			out.flush();
			byte[] b = out.toByteArray();

			in = new ByteArrayInputStream(b);
			objectInputStream = new ObjectInputStream(in);
            return objectInputStream.readObject();
		}finally {
			IoUtil.close(out);
			IoUtil.close(objectOutputStream);
			IoUtil.close(in);
			IoUtil.close(objectInputStream);
		}

	}
}
