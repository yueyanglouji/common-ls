package l.s.common.util;

public interface RetryFunc<T> {

	T function() throws Exception;
}
