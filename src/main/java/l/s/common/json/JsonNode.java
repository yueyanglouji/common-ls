package l.s.common.json;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import l.s.common.bean.BeanConnector;
import l.s.common.bean.BeanConverter;
import org.springframework.core.convert.converter.Converter;

public class JsonNode{

    private String key;

    private List<JsonNode> childs;

    private JsonNode parentNode;

    private RootNode rootNode;

    private Object value;

    private short type;

    private boolean isRootNode;

    private Class<?> T;

    public final static short ObjectNode = 0;

    public final static short ArrayNode = 1;

    public final static short TextNode = 2;

    private DecimalFormat decimalFormat;

    private JsonNode(){
        this.childs = new ArrayList<JsonNode>();
        this.decimalFormat = new DecimalFormat("#.##");
    }

    public void setDecimalFormat(DecimalFormat decimalFormat){
        this.decimalFormat = decimalFormat;
    }

    public <T> void addConverter(Converter converter){
        rootNode.getBeanConverter().addConverter(converter);
    }

    private static JsonNode createRootNode(short type){

        if(type != ObjectNode && type != ArrayNode){
            throw new RuntimeException("the root node type is not support.");
        }

        RootNode rootNode = new RootNode();
        JsonNode root = new JsonNode();
        root.key = "/";
        root.isRootNode = true;
        root.type = type;
        root.rootNode = rootNode;
        root.parentNode = null;

        rootNode.setRoot(root);

        return root;
    }

    private JsonNode createChildNode(short type){
        JsonNode child = new JsonNode();
        child.isRootNode = false;
        child.type = type;
        child.rootNode = this.rootNode;
        child.parentNode = this;

        return child;
    }

    private JsonNode createChildNode(){
        JsonNode child = new JsonNode();
        child.isRootNode = false;
        child.type = type;
        child.rootNode = this.rootNode;
        child.parentNode = this;

        return child;
    }

    public static JsonNode create(){
        return createFromJsonString("{}");
    }

    public static JsonNode createFromJsonString(String jsonSring){
        if(jsonSring.startsWith("[")){
            JSONArray array = new JSONArray(jsonSring);
            JsonNode root = createRootNode(ArrayNode);

            for(int i=0;i<array.length();i++){
                JsonNode child = root.createChildNode();
                child.setKey(i);
                addChild(root, child, array.get(i));
            }

            return root;
        }else{
            JSONObject object = new JSONObject(jsonSring);
            JsonNode root = createRootNode(ObjectNode);

            Iterator<String> keys = object.keys();
            while(keys.hasNext()){
                String key = keys.next();
                JsonNode child = root.createChildNode();
                child.setKey(key);

                addChild(root, child, object.get(key));
            }
            return root;
        }

    }

    public static JsonNode createFromBeanArray(Object[] bean){
        JsonNode root = createRootNode(ArrayNode);

        if(bean!=null){
            for(int i=0;i<bean.length;i++){
                Object b = bean[i];
                JsonNode child = root.createChildNode();
                child.setKey(i);
                addChild(root, child, b);
            }
        }
        return root;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static JsonNode createFromBeanObject(Object bean){
        if(bean == null){
            return createFromJsonString("{}");
        }
        else if(bean instanceof JSONObject || bean instanceof JSONArray){
            return createFromJsonString(bean.toString());
        }
        else if(bean.getClass().isArray()){
            return createFromBeanArray((Object[])bean);
        }
        else if(bean instanceof Collection){
            List<Object> list = new ArrayList<>((Collection)bean);
            return createFromBeanArray(list.toArray());
        }
        else if(bean instanceof Map){
            Map<Object, Object> map = (Map<Object, Object>)bean;

            JsonNode root = createRootNode(ObjectNode);

            for(Entry<Object, Object> e : map.entrySet()){
                String key = String.valueOf(e.getKey());
                JsonNode child = root.createChildNode();
                child.setKey(key);

                addChild(root, child, e.getValue());
            }
            return root;
        }
        else{
            JsonNode root = createRootNode(ObjectNode);
            BeanConnector conn = BeanConnector.connect(bean, root.rootNode.getBeanConverter());

            PropertyDescriptor[] pds = conn.getAllPropertyPropertyDescriptors();
            for(PropertyDescriptor pd : pds){
                String key = pd.getName();
                if(key.equals("metaClass")){
                    if(pd.getPropertyType().getName().equals("groovy.lang.MetaClass")){
                        continue;
                    }
                }
                JsonNode child = root.createChildNode();
                child.setKey(key);

                addChild(root, child, conn.getProperty(key));
            }

            return root;
        }

    }

    //object append {}, array append [] ,if array append a new array use [[...],...]
    public JsonNode append(String jsonString){

        if(jsonString.startsWith("[")){
            if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ArrayNode){
                JSONArray array = new JSONArray(jsonString);

                this.type = ArrayNode;
                int length = this.getChilds().size();

                for(int i=0;i<array.length();i++){
                    JsonNode child = this.createChildNode();
                    child.setKey(i + length);
                    addChild(this, child, array.get(i));
                }

            }else{
                throw new RuntimeException("this node is not a array node, can not append jsonString.");
            }
        }else{
            if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ObjectNode){

                JSONObject object = new JSONObject(jsonString);

                this.type = ObjectNode;
                Iterator<String> keys = object.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    JsonNode child = this.createChildNode();
                    child.setKey(key);

                    addChild(this, child, object.get(key));
                }
            }else{
                throw new RuntimeException("this node is not a object node, can not append jsonString.");
            }
        }
        return this;
    }

    public JsonNode append(Object bean){
        JsonNode node = JsonNode.createFromBeanObject(bean);
        moveAppend(node);
        return this;
    }

    public JsonNode moveAppend(JsonNode node){

        if(node.type == JsonNode.ArrayNode){
            if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ArrayNode){
                this.type = ArrayNode;
                int length = this.getChilds().size();

                for(int i=0;i<node.getChilds().size();i++){
                    JsonNode child = node.getChilds().get(i);
                    child.isRootNode = false;
                    child.parentNode = this;
                    child.rootNode = this.rootNode;
                    child.setKey(i + length);
                    this.childs.add(child);
                }
                node.childs = new ArrayList<>();
                node.type = JsonNode.TextNode;
                node.value = null;
            }
            else{
                throw new RuntimeException("this node is not a array node, can not append this node.");
            }
        }else if(node.type == JsonNode.ObjectNode){
            if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ArrayNode){
                if(!node.isRootNode){
                    node.parentNode.childs.remove(node);
                }
                this.type = ArrayNode;
                int length = this.getChilds().size();
                node.isRootNode = false;
                node.parentNode = this;
                node.rootNode = this.rootNode;
                node.setKey(length);
                this.childs.add(node);
            }
            else if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ObjectNode){
                this.type = ObjectNode;
                List<JsonNode> list = node.getChilds();
                for(int i=0;i<list.size();i++){
                    JsonNode child = list.get(i);
                    child.isRootNode = false;
                    child.parentNode = this;
                    child.rootNode = this.rootNode;
                    this.childs.add(child);
                }
                node.childs = new ArrayList<>();
                node.type = JsonNode.TextNode;
                node.value = null;
            }else{
                throw new RuntimeException("this node is not a object node, can not append this node.");
            }
        }
        else{
            throw new RuntimeException("not append able");
        }
        return this;
    }

    public JsonNode append(String key, Object value){

        if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ObjectNode){
            this.type = JsonNode.ObjectNode;
            JsonNode child = this.createChildNode();
            child.setKey(key);

            addChild(this, child, value);
        }else{
            throw new RuntimeException("this node is not a object node, can not append jsonString.");
        }
        return this;
    }

    //will append if not exists
    public JsonNode replace(String jsonString){

        if(jsonString.startsWith("[")){
            throw new RuntimeException("replace method just used to ObjectNode.");
        }else{
            if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ObjectNode){

                JSONObject object = new JSONObject(jsonString);

                this.type = ObjectNode;
                Iterator<String> keys = object.keys();
                while(keys.hasNext()){
                    String key = keys.next();
                    this.tripRemove("./" + key);
                    JsonNode child = this.createChildNode();
                    child.setKey(key);

                    addChild(this, child, object.get(key));
                }
            }else{
                throw new RuntimeException("replace method just used to ObjectNode.");
            }
        }
        return this;
    }

    public JsonNode replace(Object bean){
        JsonNode node = JsonNode.createFromBeanObject(bean);
        replace(node.toJsonString());
        return this;
    }

    public JsonNode replace(String key, Object value){

        if(this.getType() == JsonNode.TextNode && this.tripValue(".") == null || this.getType() == JsonNode.ObjectNode){
            this.type = JsonNode.ObjectNode;
            this.tripRemove("./" + key);
            JsonNode child = this.createChildNode();
            child.setKey(key);

            addChild(this, child, value);
        }else{
            throw new RuntimeException("replace method just used to ObjectNode.");
        }
        return this;
    }


    @SuppressWarnings("unchecked")
    private static void addChild(JsonNode parentMjson, JsonNode mjson, Object json){

        if(json == null || json.getClass() == JSONObject.NULL.getClass()){
            mjson.type = JsonNode.TextNode;
            mjson.setValue(null);
            parentMjson.appendChild(mjson);
        }
        else if(json.getClass() == JSONObject.class){
            mjson.type = JsonNode.ObjectNode;
            JSONObject object = (JSONObject)json;
            Iterator<String> keys = object.keys();
            while(keys.hasNext()){
                String key = keys.next();
                JsonNode child = mjson.createChildNode();
                child.setKey(key);

                addChild(mjson, child, object.get(key));
            }
            parentMjson.appendChild(mjson);
        }
        else if(json.getClass() == JSONArray.class){
            mjson.type = JsonNode.ArrayNode;
            JSONArray array = (JSONArray)json;
            for(int i=0;i<array.length();i++){
                JsonNode child = mjson.createChildNode();
                child.setKey(i + "");
                addChild(mjson, child, array.get(i));
            }
            parentMjson.appendChild(mjson);
        }
        else{
            String className = json.getClass().getName();
            if (json instanceof JSONString
                    || json instanceof Byte || json instanceof Character
                    || json instanceof Short || json instanceof Integer
                    || json instanceof Long || json instanceof Boolean
                    || json instanceof Float || json instanceof Double
                    || json instanceof String || json instanceof BigInteger
                    || json instanceof BigDecimal || json.getClass().isPrimitive()) {
                mjson.type = JsonNode.TextNode;
                mjson.setValue(json);
                parentMjson.appendChild(mjson);
            }
            else if (json instanceof Collection) {
                Collection<Object> coll = (Collection<Object>) json;
                mjson.type = JsonNode.ArrayNode;
                List<Object> list = new ArrayList<>(coll);

                for(int i=0;i<list.size();i++){
                    Object o = list.get(i);
                    JsonNode child = mjson.createChildNode();
                    child.setKey(i + "");
                    addChild(mjson, child, o);
                }
                parentMjson.appendChild(mjson);
            }
            else if (json.getClass().isArray()) {
                Object arr = json;
                mjson.type = JsonNode.ArrayNode;
                for(int i=0;i<Array.getLength(arr);i++){
                    Object o = Array.get(arr, i);
                    JsonNode child = mjson.createChildNode();
                    child.setKey(i + "");
                    addChild(mjson, child, o);
                }
                parentMjson.appendChild(mjson);
            }
            else if (json instanceof Map) {
                Map<Object, Object> map = (Map<Object, Object>) json;
                mjson.type = JsonNode.ObjectNode;
                for (final Entry<?, ?> e : map.entrySet()) {
                    final Object value = e.getValue();
                    String key = String.valueOf(e.getKey());
                    JsonNode child = mjson.createChildNode();
                    child.setKey(key);

                    addChild(mjson, child, value);
                }
                parentMjson.appendChild(mjson);
            }
            else if(className.startsWith("java.") || className.startsWith("javax.")){
                mjson.type = JsonNode.TextNode;
                mjson.setValue(json);
                parentMjson.appendChild(mjson);
            }
            else{
                mjson.type = JsonNode.ObjectNode;
                BeanConnector conn = BeanConnector.connect(json, parentMjson.rootNode.getBeanConverter());

                PropertyDescriptor[] pds = conn.getAllPropertyPropertyDescriptors();
                for(PropertyDescriptor pd : pds){
                    String key = pd.getName();
                    if(key.equals("metaClass")){
                        if(pd.getPropertyType().getName().equals("groovy.lang.MetaClass")){
                            continue;
                        }
                    }
                    JsonNode child = mjson.createChildNode();
                    child.setKey(key);

                    addChild(mjson, child, conn.getProperty(key));
                }
                parentMjson.appendChild(mjson);
            }
        }
    }

    public static String toJSON(String value){
        return org.noggit.JSONUtil.toJSON(value);
    }

    public String toString(){
        return valueToJsonString();
    }

    public String toJsonString(){
        StringBuilder json = new StringBuilder("");
        if(!this.isRootNode && parentNode.type != ArrayNode){
            json.append(org.noggit.JSONUtil.toJSON(this.key) + ":");
        }
        json.append(valueToJsonString());
        return json.toString();
    }

    public String valueToJsonString(){
        StringBuilder json = new StringBuilder("");
        if(this.type == ObjectNode){
            json.append("{");
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                json.append(child.toJsonString());

                if(i != childs.size() -1){
                    json.append(", ");
                }
            }
            json.append("}");
        }
        else if(this.type == ArrayNode){
            json.append("[");
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                json.append(child.toJsonString());

                if(i != childs.size() -1){
                    json.append(", ");
                }
            }
            json.append("]");
        }else{

            Object o = this.value;

            BeanConverter c = this.rootNode.getBeanConverter();
            Object value;
            if(o == null){
                value = null;
            }
            else if(o instanceof Float || o instanceof Double || o.getClass() == float.class || o.getClass() == double.class || o instanceof BigDecimal){
                String s = decimalFormat.format(o);
                value = new BigDecimal(s);
            }
            else if (o instanceof Byte || o instanceof Character
                    || o instanceof Short || o instanceof Integer
                    || o instanceof Long || o instanceof Boolean
                    || o instanceof String || o instanceof BigInteger
                    || o.getClass().isPrimitive()) {
                value = o;
            }
            else if(c.canConvert(o, String.class)){
                value = c.convert(o, String.class);
            }else{
                value = o.toString();
            }
            json.append(org.noggit.JSONUtil.toJSON(value));
        }
        return json.toString();
    }

    public Map toMap(){
        return toMap(new HashMap());
    }

    public Map toMap(Map map){
        if(this.getType() == ObjectNode){
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                child.wrapMap(map);
            }
            return map;
        }else{
            throw new RuntimeException("this node is not a object node");
        }
    }

    private void wrapMap(Map map){
        if(this.type == ObjectNode){
            Map m = new HashMap();
            map.put(this.getKey(), m);
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                child.wrapMap(m);
            }
        }
        else if(this.type == ArrayNode){
            List list = new ArrayList();
            map.put(this.getKey(), list);
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                child.wrapArray(list);
            }
        }else{
            map.put(this.getKey(), this.getValue());
        }
    }

    private void wrapArray(List listp){
        if(this.type == ObjectNode){
            Map m = new HashMap();
            listp.add(m);
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                child.wrapMap(m);
            }
        }
        else if(this.type == ArrayNode){
            List list = new ArrayList();
            listp.add(list);
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                child.wrapArray(list);
            }
        }else{
            listp.add(this.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public<T> T toBean(Class<T> c) throws Exception{
        if(this.getType() == ObjectNode){
            if(Map.class.isAssignableFrom(c)){
                return (T)(this.toMap((Map)c.newInstance()));
            }
            Object o = c.newInstance();
            BeanConnector conn = BeanConnector.connect(o, this.rootNode.getBeanConverter());
            wrap(conn, null, null);

            return (T)o;
        }else{
            throw new RuntimeException("this node is not a object node");
        }
    }

    private void wrap(BeanConnector conn, String path, String parent){
        if(path != null){
            Class<?> c = conn.getPropertyType(path);
            if(c == null){
                doIfNotWritableProperty(conn, this, path, parent, false);
                return;
            }
        }
        if(this.type == ObjectNode){
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                String next;
                if(path == null){
                    next = child.getKey();
                }else{
                    Class<?> c = conn.getPropertyType(path);
                    if(Map.class.isAssignableFrom(c)){
                        next = path + "[" + child.getKey() + "]";
                    }else{
                        next = path + "." + child.getKey();
                    }
                }
                child.wrap(conn, next, path);
            }
        }
        else if(this.type == ArrayNode){
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                String next = path + "[" + child.getKey() + "]";
                child.wrap(conn, next, path);
            }
        }else{
            conn.setProperty(path, this.getValue());
        }
    }

    private Object outsizeArray(Object arr){
        Object newArray = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
        for(int i=0;i<Array.getLength(arr);i++){
            Array.set(newArray, i, Array.get(arr, i));
        }
        return newArray;
    }

    private void doIfNotWritableProperty(BeanConnector conn, JsonNode parent, String path, String parentPath, boolean trymap){
        if(path.matches("^.*\\[\\d+\\]$") && !trymap){
            try{
                if(parent.type == ObjectNode){
                    Object o = conn.getProperty(parentPath);
                    if(o.getClass().isArray()){
                        Object newArray = outsizeArray(o);
                        Array.set(newArray, Array.getLength(o), parent.toMap());
                        conn.setProperty(parentPath, newArray);
                    }else{
                        List b = (List) conn.getProperty(parentPath);
                        b.add(parent.toMap());
                    }
                }
                else if(parent.type == ArrayNode){
                    Object o = conn.getProperty(parentPath);
                    if(o.getClass().isArray()){
                        Object newArray = outsizeArray(o);
                        Array.set(newArray, Array.getLength(o), parent.valueToArray());
                        conn.setProperty(parentPath, newArray);
                    }else{
                        List b = (List) o;
                        b.add(parent.valueToArray());
                    }

                }
                else{
                    Object o = conn.getProperty(parentPath);
                    if(o.getClass().isArray()){
                        Object newArray = outsizeArray(o);
                        Array.set(newArray, Array.getLength(o), parent.getValue());
                        conn.setProperty(parentPath, newArray);
                    }else{
                        List b = (List) o;
                        b.add(parent.getValue());
                    }
                }
            }catch (Exception e){
                this.doIfNotWritableProperty(conn, parent, path, parentPath, true);
            }
        }else{
            if(parent.type == ObjectNode){
                Map b = (Map)conn.getProperty(parentPath);
                b.put(parent.getKey(), parent.toMap());
            }
            else if(parent.type == ArrayNode){
                Map b = (Map)conn.getProperty(parentPath);
                b.put(parent.getKey(), parent.valueToArray());
            }
            else{
                Map b = (Map)conn.getProperty(parentPath);
                b.put(parent.getKey(), parent.getValue());
            }
        }
    }

    private List valueToArray(){
        if(this.type == ArrayNode){
            List ret = new ArrayList();
            for(int i=0;i<childs.size();i++){
                JsonNode child = childs.get(i);
                if(child.type == ObjectNode){
                    ret.add(child.toMap());
                }else if(child.type == ArrayNode){
                    ret.add(child.valueToArray());
                }else{
                    ret.add(child.value);
                }
            }
            return ret;
        }else{
            throw new RuntimeException("this node is not a array node");
        }
    }

    public<T> List<T> toArray(Class<T> c) throws Exception{
        if(this.getType() == ArrayNode){
            List<T> ret = new ArrayList<>();
            for(int i=0;i<childs.size();i++){
                ret.add(childs.get(i).toBean(c));
            }
            return ret;
        }
        else{
            throw new RuntimeException("this node is not a array node");
        }
    }

    public Object getValue(){
        return value;
    }

    public int getIntValue(){
        return (Integer)value;
    }

    public Class<?> getValueType(){
        return T;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if(isRootNode){
            return;
        }

        if(parentNode.type == ArrayNode){
            try {
                Integer.parseInt(key);
            } catch (NumberFormatException e) {
                throw new RuntimeException("the key of ArrayNode item should a number.");
            }
        }

        this.key = key;
    }

    public void setKey(int arraykey){
        if(parentNode.type != ArrayNode){
            throw new RuntimeException("the node is not ArrayNode item.");
        }
        setKey(arraykey + "");
    }

    public List<JsonNode> getChilds() {
        return childs;
    }

    public <T> void setValue(T value) {
        if(type != TextNode){
            throw new RuntimeException("the node type is not TextNode.");
        }
        this.value = value;
        if(value != null){
            T = value.getClass();
        }
    }

    public short getType() {
        return type;
    }

    public JsonNode getParentNode() {
        return parentNode;
    }

    public JsonNode getRootNode() {
        return rootNode.getRoot();
    }

    private void appendChild(JsonNode child){
        if(type != ArrayNode && type != ObjectNode){
            throw new RuntimeException("the node type has not child node.");
        }

        if(child.parentNode != this){
            throw new RuntimeException("that child node is not this's child node.");
        }

        childs.add(child);

        if(type == ArrayNode){
            Collections.sort(childs, new Comparator<JsonNode>() {
                @Override
                public int compare(JsonNode o1, JsonNode o2) {
                    return Integer.parseInt(o1.key) - Integer.parseInt(o2.key);
                }
            });
        }
    }

    public JsonNode getChild(String key){
        for(int i=0;i<childs.size();i++){
            if(childs.get(i).getKey().equals(key)){
                return childs.get(i);
            }
        }
        return null;
    }

    public JsonNode trip(String env){

        if(!isRootNode && env.startsWith("/")){
            return this.getRootNode().trip(env);
        }

        String[] split = env.split("/");
        if(split.length == 0){
            throw new RuntimeException();
        }

        JsonNode tmp = this;
        for(int i=0;i<split.length;i++){
            if(split[i].equals("")){
                continue;
            }
            if(split[i].equals(".")){
                continue;
            }
            if(tmp == null){
                return null;
            }
            tmp = tmp.getChild(split[i]);
        }

        return tmp;
    }

    public void tripRemove(String env){
        if(!isRootNode && env.startsWith("/")){
            this.getRootNode().tripRemove(env);
        }

        String[] split = env.split("/");
        if(split.length == 0){
            throw new RuntimeException();
        }

        JsonNode tmp = this;
        for(int i=0;i<split.length;i++){
            if(split[i].equals("")){
                continue;
            }
            if(split[i].equals(".")){
                continue;
            }
            if(tmp == null){
                return;
            }
            tmp = tmp.getChild(split[i]);
        }
        if(tmp !=null){
            tmp.parentNode.childs.remove(tmp);
        }
    }

    public Object tripValue(String env){
        JsonNode node = trip(env);

        if(node == null){
            return null;
        }else{

            if(node.type != TextNode){
                throw new RuntimeException("the node is not a text node ,node key : " + node.getKey());
            }
            return node.getValue();
        }
    }

    public <T> T tripValue(String env, Class<T> c, T defaultValue){
        Object value = tripValue(env);
        if(value == null){
            return defaultValue;
        }

        BeanConverter converter = this.rootNode.getBeanConverter();
        if(converter.canConvert(value, c)) {
            T t = converter.convert(value, c);
            return t;
        }else {
            throw new RuntimeException("can not convert " + value.getClass() + " to " + c);
        }
    }

    public static void main(String[] args) {
        JsonNode node = JsonNode.createFromJsonString("{'abc':0}");
        System.out.println(node.tripValue("./abc", long.class).getClass());
    }

    public <T> T tripValue(String env, Class<T> c){
        return tripValue(env, c, null);
    }

    public boolean isRootNode() {
        return isRootNode;
    }
}
