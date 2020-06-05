package json

import l.s.common.json.JsonNode

class JsonMapTest {
    public static void main(String[] args) {
        HashMap map = new HashMap();
        map.put("a", "a")
        map.put("a", "a")
        map.put("a", "a")
        List list = new ArrayList()
        list.add(map)
        JsonNode jsonNode = JsonNode.createFromBeanObject(list)

        def arr = jsonNode.toArray(HashMap.class)
        print arr
    }
}
