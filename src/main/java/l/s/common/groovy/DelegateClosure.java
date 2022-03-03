package l.s.common.groovy;

import groovy.lang.Closure;

public interface DelegateClosure {
	Object call(Closure<Object> c);
}
