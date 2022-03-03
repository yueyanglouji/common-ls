package l.s.common.groovy.json;

import org.springframework.core.DecoratingProxy;
import org.springframework.core.convert.converter.Converter;

public interface JsonNodeConverter<S> extends DecoratingProxy, Converter<S, String> {

}
