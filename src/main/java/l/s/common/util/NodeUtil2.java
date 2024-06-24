package l.s.common.util;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class NodeUtil2 {
    private boolean isCData = true;

    public NodeUtil2() {
    }

    /**
     * @param isCData default is true;
     */
    public NodeUtil2(boolean isCData){
        this.isCData = isCData;
    }

    public void setCData(boolean isCData) {
        this.isCData = isCData;
    }

    public Document parse(File file, String charset) throws Exception{
        Scanner in = new Scanner(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset));
        StringBuilder builder = new StringBuilder();
        while(in.hasNextLine()){
            builder.append(in.nextLine());
            builder.append("\n");
        }
        in.close();
        return parse(builder.toString());
    }

    public Document parse(InputStream in, String charset){
        try {
            Scanner scanner = new Scanner(new InputStreamReader(in,charset));
            StringBuilder builder = new StringBuilder();
            while(scanner.hasNextLine()){
                builder.append(scanner.nextLine());
                builder.append("\n");
            }
            scanner.close();
            return parse(builder.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Document parse(String str){
        try {
            str = str.replaceFirst("<!DOCTYPE\\s[^>]*>", "");
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            return factory.newDocumentBuilder().parse(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param evaluate
     * 			使用这个方法请把evaluate以"./"或".//"开头，如果
     * 			不这样的话依然从文档的根节点开始检索。 效果跟上面方
     * 			法一样。
     * @param e node
     * @param cl
     * 			此类型为使用泛型，与return的类型一致，不需要强制转换
     * 			，Class类型只能是String.class,NodeList.class,以及
     * 			Element.class,Attr.class,Comment.class,Text.class,Node.class,Integer.class,Long.class,Double.class,Float.class,boolean.class中的一种。
     * @return
     * 			返回类型与传入的泛型一致
     * 			节点未找到的情况： 1.如果返回类型是NodeList类型 则返回对象为length==0的NodeList实例。
     * 							2.如果返回类型是Element 或者 Attr则返回NULL.
     * 							3.如果返回类型是String 类型 则返回""---length为0的String对象。
     * 							4.如果返回类型是int 或long 那么小数点后会被省略。
     * 			以上均可强制转换而不会因为Null发生类型转换错误。
     */
    @SuppressWarnings("unchecked")
    public <T>T trip(String evaluate,Node e,Class<T> cl){
        evaluate = redoEvaluate(evaluate);
        XPath path = XPathFactory.newInstance().newXPath();
        try {
            if(cl==Element.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODE); //Element
            }else if(cl==NodeList.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODESET); //NodeList
            }else if(cl==Attr.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODE); //Attr
            }else if(cl == Comment.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODE); //Comment
            }else if(cl == Text.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODE); //Text
            }else if(cl==Node.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.NODE); //Node
            }else if(cl==String.class){
                return (T)path.evaluate(evaluate, e, XPathConstants.STRING);
            }else if(cl==int.class||cl==Integer.class){
                String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
                int intValue = (int)Double.parseDouble(value);
                return (T)(Integer)intValue;
            }else if(cl==long.class||cl==Long.class){
                String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
                long longValue = (long)Double.parseDouble(value);
                return (T)(Long)longValue;
            }else if(cl==double.class||cl==Double.class){
                String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
                double doubleValue = Double.parseDouble(value);
                return (T)(Double)doubleValue;
            }else if(cl==float.class||cl==Float.class){
                String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
                float floatValue = Float.parseFloat(value);
                return (T)(Float)floatValue;
            }else if(cl==boolean.class||cl==Boolean.class){
                String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
                boolean booleanValue = Boolean.parseBoolean(value);
                return (T)(Boolean)booleanValue;
            }else{
                return null;
            }
        } catch (XPathExpressionException e1) {
            throw new RuntimeException(e1);
        }
    }

    private String redoEvaluate(String evaluate){
        char tsf = '!';
        int i = 0;
        while(true){
            tsf = (char)(++i);

            if(tsf == '\''){
                continue;
            }
            if(Character.isDefined(tsf) && !Character.isIdentifierIgnorable(tsf) && !Character.isISOControl(tsf) && !Character.isSpaceChar(tsf) && !Character.isWhitespace(tsf)){
                if(evaluate.indexOf(tsf) == -1){
                    break;
                }
            }
        }
        evaluate = evaluate.replace("!'!", tsf + "");

        StringBuilder builder = new StringBuilder();

        int odd = 1;
        while(evaluate.indexOf('\'')!=-1){
            int index = evaluate.indexOf('\'');
            String before = evaluate.substring(0,index);
            String after = evaluate.substring(index + 1);

            builder.append(before);
            if(odd == 1){
                builder.append("translate('");
                odd = 0;
            }else{
                builder.append("','").append(tsf).append("',\"'\")");
                odd = 1;
            }

            evaluate = after;
        }

        builder.append(evaluate);
        return builder.toString();
    }

    public String getNodeValue(Element e){
        if(e.getChildNodes().getLength()==0){
            return "";
        }
        else if(e.getChildNodes().getLength()>1){
            return "";
        }
        return ((Text)e.getChildNodes().item(0)).getData();
    }
    //public String getNodeORAttributeValue(Element element,String no)
    public void outputToStream(Document doc, OutputStream outputStream, String charset)throws Exception{
        outputToStream(doc.getDocumentElement(), outputStream, charset);
    }

    public void outputToStream(Element element, OutputStream outputStream, String charset)throws Exception{
        PrintWriter out = null;
        XMLEventWriter eventWriter = null;
        try{
            out = new PrintWriter(new OutputStreamWriter(outputStream,charset));
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");


            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            eventWriter = outputFactory.createXMLEventWriter(out);

            final NamedNodeMap attrs = element.getAttributes();
            final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            Iterator<Attribute> iterator = getAttributeIterator(attrs, eventFactory);
            eventWriter.add(eventFactory.createStartElement(new QName(element.getNodeName()), iterator, null));

            addEvent(element.getChildNodes(), eventWriter,eventFactory);

            eventWriter.add(eventFactory.createEndElement(new QName(element.getNodeName()), null));
        } finally {
            IoUtil.close(eventWriter);
            IoUtil.close(out);
            IoUtil.close(outputStream);
        }

    }
    private Iterator<Attribute> getAttributeIterator(NamedNodeMap attrs, XMLEventFactory eventFactory){
        return new Iterator<Attribute>(){
            int i=0;
            public boolean hasNext() {
                if(i<attrs.getLength()){
                    return true;
                }
                return false;
            }

            public Attribute next() {
                Node node =  attrs.item(i++);
                return eventFactory.createAttribute(new QName(node.getNodeName()), node.getNodeValue());
            }

            public void remove() {
                //do nothing.. dot remove.
            }

        };
    }

    public void creatXmlFile(Document doc,String fileName, String charset)throws Exception{
        File f = new File(fileName);
        this.creatXmlFile(doc, f, charset);
    }
    public void creatXmlFile(Document doc,File file, String charset)throws Exception{
        IoUtil.mkdirsParent(file);
        IoUtil.createNewFile(file);
        outputXmlFile(doc, file, charset);
    }
    private void outputXmlFile(Document doc,File file, String charset)throws Exception{
        outputToStream(doc, new FileOutputStream(file), charset);
    }
    // add event to xmleventwriter.
    private void addEvent(NodeList list,XMLEventWriter eventWriter,final XMLEventFactory eventFactory)throws Exception{
        for(int i=0;i<list.getLength();i++){
            Node node = list.item(i);
            if(node instanceof Text){
                String value = node.getNodeValue();
                if(value.length()>0&&list.getLength()==1){
                    if(isCData){
                        eventWriter.add(eventFactory.createCData(value));
                    }else{
                        eventWriter.add(eventFactory.createCharacters(value));
                    }
                }else{
                    eventWriter.add(eventFactory.createCharacters(value));
                }
            }
            else if(node instanceof Element){
                Element element = (Element)(node);
                final NamedNodeMap attrs = element.getAttributes();
                Iterator<Attribute> iterator = getAttributeIterator(attrs, eventFactory);
                eventWriter.add(eventFactory.createStartElement(new QName(element.getNodeName()), iterator, null));

                addEvent(element.getChildNodes(), eventWriter,eventFactory);

                eventWriter.add(eventFactory.createEndElement(new QName(element.getNodeName()), null));
            }
        }
    }

    /**
     * change attribute value。
     * @param evaluate evaluate
     * @param node node
     * @param value value
     */
    public void change(String evaluate, Node node, String value){
        Node get = trip(evaluate, node, Node.class);
        change(get, value);
    }

    /**
     * change attribute value。
     * @param node node
     * @param value value
     */
    public void change(Node node,String value){
        if(node.getNodeType() == Node.ATTRIBUTE_NODE){
            change((Attr)node, value);
        }
        else if(node.getNodeType() == Node.ELEMENT_NODE){
            change((Element)node, value);
        }
        else{
            throw new RuntimeException("change node value error.that node is not Attr node and not Element node.");
        }
    }

    /**
     * change attribute value
     * @param attr attr
     * @param value value
     */
    private void change(Attr attr,String value){
        if(attr==null) return;
        attr.setNodeValue(value);
    }

    /**
     * change attribute value。
     * @param attr attr
     * @param value value
     */
    public void change(Element element,String attr,String value){
        if(element==null) return;
        element.setAttribute(attr, value);
    }

    /**
     * 修改Element的值，但是之后对只有一个text节点或空节点的Element有作用。
     * @param element element
     */
    private void change(Element element,String value){
        if(element==null) return;
        NodeList nodes = element.getChildNodes();
        if(nodes.getLength()>1) return;
        if(nodes.getLength()==0){
            element.setTextContent(value);
        }
        else if(nodes.getLength()==1){
            Node node = nodes.item(0);
            if(node.getNodeType() == Node.TEXT_NODE||node.getNodeType() == Node.CDATA_SECTION_NODE){
                element.removeChild(node);
                element.setTextContent(value);
            }
        }
    }

    public void appendChild(Element parentElement,Element childElement){
        Document doc = parentElement.getOwnerDocument();


        Node lastNode = parentElement.getLastChild();
        if(lastNode==null||lastNode.getNodeType()!=Node.TEXT_NODE&&lastNode.getNodeType()!=Node.CDATA_SECTION_NODE||lastNode.getNodeValue().charAt(lastNode.getNodeValue().length()-1)!='\n'){
            parentElement.appendChild(doc.createTextNode("\n"));
        }
        parentElement.appendChild(childElement);
        parentElement.appendChild(doc.createTextNode("\n"));
    }

    /**
     *
     * @param document document
     * @param evaluate 使用发放与trip相同。
     * @return Element
     */
    public Element cloneElement(Document document,String evaluate){
        Element target = this.trip(evaluate, document, Element.class);
        return cloneElement(target);
    }
    /**
     *
     * @param element element
     * @param evaluate 使用发放与trip相同。
     * @return Element
     */
    public Element cloneElement(Element element,String evaluate){
        Element target = this.trip(evaluate, element, Element.class);
        return cloneElement(target);
    }
    public Element cloneElement(Element element){
        return (Element)element.cloneNode(true);
    }

    public boolean hasChildElements(Element element){
        NodeList nodes = element.getChildNodes();
        for(int i=0;i<nodes.getLength();i++){
            if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                return true;
            }
        }
        return false;
    }

    public List<Element> getChildElements(Element element){
        NodeList nodes = element.getChildNodes();
        List<Element> list = new ArrayList<>();
        for(int i=0;i<nodes.getLength();i++){
            if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
                list.add((Element)nodes.item(i));
            }
        }
        return list;
    }
    /**
     * 复制 不同document中的节点 到sourceDocument.
     * @param e Element
     * @param sourceDocument sourceDocument
     * @return Element
     */
    public Element copyElement(Element e,Document sourceDocument){
        String nodeName = e.getNodeName();
        Element top = sourceDocument.createElement(nodeName);
        NamedNodeMap attrs = e.getAttributes();
        for(int i=0;i<attrs.getLength();i++){
            this.change(top, attrs.item(i).getNodeName(), attrs.item(i).getNodeValue());
        }
        NodeList childs = e.getChildNodes();
        for(int i=0;i<childs.getLength();i++){
            Node child = childs.item(i);
            if(child.getNodeType()==Node.TEXT_NODE||child.getNodeType()==Node.CDATA_SECTION_NODE){
                String value = child.getNodeValue();
                if(!value.trim().equals("")){
                    top.appendChild(sourceDocument.createCDATASection(value));
                }
            }
            else if(child.getNodeType()==Node.ELEMENT_NODE){
                Element childElement = copyElement((Element)child, sourceDocument);
                this.appendChild(top, childElement);
            }
        }
        return top;
    }

    /**
     *  此方法会删除包括element节点和此节点的下一个TEXT节点，谨慎使用。
     */
    public void removeElement(Element element) {
        Element parent = (Element)element.getParentNode();
        NodeList list = parent.getChildNodes();
        int i=0;
        for(;i<list.getLength();i++){
            if(list.item(i)==element){
                break;
            }
        }
        Node nextNode = list.item(i+1);
        if(nextNode instanceof Text){
            parent.removeChild(nextNode);
        }
        parent.removeChild(element);

    }

    public void removeNode(Node node) {
        Node parent = node.getParentNode();
        parent.removeChild(node);
    }

    public String toString(Element e) throws Exception{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outputToStream(e, out, "UTF-8");

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public String childToString(Element e) throws Exception{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

        final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        addEvent(e.getChildNodes(), eventWriter,eventFactory);
        eventWriter.flush();
        eventWriter.close();
        out.flush();
        out.close();

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}

