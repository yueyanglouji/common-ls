package l.s.common.groovy.json;

import l.s.common.bean.BeanConverter;

public class RootNode {

    private JsonNode root;

    private BeanConverter beanConverter;

    public RootNode(){
        this.beanConverter = new BeanConverter();
    }

    public JsonNode getRoot() {
        return root;
    }

    void setRoot(JsonNode root) {
        this.root = root;
    }

    public BeanConverter getBeanConverter(){
        return beanConverter;
    }
}
