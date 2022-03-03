package l.s.common.groovy;

public final class DefaultGroovyObject extends GroovyObjectSupportDefault{
	
	private Object _value;

	public Object _Value() {
		return _value;
	}
	
//	public Object get$() {
//		return _value;
//	}
	
	@Override
	public Object getProperty(String property) {
		if(property.equals("$")){
			return _Value();
		}
		return super.getProperty(property);
	}

	public void _Value(Object value) {
		this._value = value;
	}

	@Override
	public int hashCode() {
		if(_value != null){
			return _value.hashCode();
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if(_value != null){
			return _value.equals(obj);
		}else{
			return obj == null;
		}
	}

	@Override
	public String toString() {
		if(_value != null){
			return _value.toString();
		}else{
			return null;
		}
	}
	
}
