package l.s.common.util;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class NodeUtil {
	private boolean isCData = true;
	
	public NodeUtil() {
	}
	/**
	 * default is true;
	 * @param isCData
	 */
	public NodeUtil(boolean isCData){
		this.isCData = isCData;
	}
	
	public void setCData(boolean isCData) {
		this.isCData = isCData;
	}
	
	public Document parse(File file) throws Exception{
		try {
			Scanner in = new Scanner(new InputStreamReader(new BufferedInputStream(new FileInputStream(file)),"UTF-8"));
			StringBuilder builder = new StringBuilder();
			while(in.hasNextLine()){
				builder.append(in.nextLine());
				builder.append("\n");
			}
			in.close();
			return parse(builder.toString());
		} catch (Exception e) {
			throw e;
		}
	}
	public Document parse(InputStream in){
		try {
			Scanner scanner = new Scanner(new InputStreamReader(in,"UTF-8"));
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
			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
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
				Integer intvalue = (int)Double.parseDouble(value);
				return (T)intvalue;
			}else if(cl==long.class||cl==Long.class){
				String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
				Long longvalue = (long)Double.parseDouble(value);
				return (T)longvalue;
			}else if(cl==double.class||cl==Double.class){
				String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
				Double doublevalue = Double.parseDouble(value);
				return (T)doublevalue;
			}else if(cl==float.class||cl==Float.class){
				String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
				Float floatvalue = Float.parseFloat(value);
				return (T)floatvalue;
			}else if(cl==boolean.class||cl==Boolean.class){
				String value = (String)path.evaluate(evaluate, e, XPathConstants.STRING);
				Boolean booleanvalue = Boolean.parseBoolean(value);
				return (T)booleanvalue;
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
				builder.append("','" + tsf + "',\"'\")");
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
	
	public void outputToStream(Document doc,OutputStream outputStream)throws Exception{
		PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream,"UTF-8"));
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);
		
		Element root = doc.getDocumentElement();
		
		final NamedNodeMap attrs = root.getAttributes();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		Iterator<Attribute> iterator = new Iterator<Attribute>(){
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
		eventWriter.add(eventFactory.createStartElement(new QName(root.getNodeName()), iterator, null));
		
		addEvent(root.getChildNodes(), eventWriter,eventFactory);
		
		eventWriter.add(eventFactory.createEndElement(new QName(root.getNodeName()), null));
		eventWriter.flush();
		eventWriter.close();
		out.flush();
		out.close();
	}
	
	public void creatXmlFile(Document doc,String fileName)throws Exception{
		File f = new File(fileName);
		this.creatXmlFile(doc, f);
	}
	public void creatXmlFile(Document doc,File file)throws Exception{
		File parent = file.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
//		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		file.createNewFile();
//		transformer.transform(new DOMSource(doc), new StreamResult(file));
		
		//zui 2
		outputXmlFile(doc, file);
	
	}
	private void outputXmlFile(Document doc,File file)throws Exception{
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(out);
		
		Element root = doc.getDocumentElement();
		
		final NamedNodeMap attrs = root.getAttributes();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		Iterator<Attribute> iterator = new Iterator<Attribute>(){
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
		eventWriter.add(eventFactory.createStartElement(new QName(root.getNodeName()), iterator, null));
		
		addEvent(root.getChildNodes(), eventWriter,eventFactory);
		
		eventWriter.add(eventFactory.createEndElement(new QName(root.getNodeName()), null));
		eventWriter.flush();
		eventWriter.close();
		out.flush();
		out.close();
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
				Iterator<Attribute> iterator = new Iterator<Attribute>(){
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
				eventWriter.add(eventFactory.createStartElement(new QName(element.getNodeName()), iterator, null));
				
				addEvent(element.getChildNodes(), eventWriter,eventFactory);
				
				eventWriter.add(eventFactory.createEndElement(new QName(element.getNodeName()), null));
			}
		}
	}
	
	/**
	 * 修改属性的值。
	 * @param attr
	 * @param value
	 */
	public void change(String evaluate, Node node, String value){
		Node get = trip(evaluate, node, Node.class);
		change(get, value);
	}
	
	/**
	 * 修改属性的值。
	 * @param attr
	 * @param value
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
	 * 修改属性的值。
	 * @param attr
	 * @param value
	 */
	private void change(Attr attr,String value){
		if(attr==null) return;
		attr.setNodeValue(value);
	}
	
	/**
	 * 修改属性的值。
	 * @param attr
	 * @param value
	 */
	public void change(Element element,String attr,String value){
		if(element==null) return;
		element.setAttribute(attr, value);
	}
	
	/**
	 * 修改Element的值，但是之后对只有一个text节点或空节点的Element有作用。
	 * @param element
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
	 * @param document
	 * @param evaluate 使用发放与trip相同。
	 * @return
	 */
	public Element cloneElement(Document document,String evaluate){
		Element target = this.trip(evaluate, document, Element.class);
		return cloneElement(target);
	}
	/**
	 * 
	 * @param element
	 * @param evaluate 使用发放与trip相同。
	 * @return
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
			continue;
		}
		return false;
	}
	
	public List<Element> getChildElements(Element element){
		NodeList nodes = element.getChildNodes();
		List<Element> list = new ArrayList<Element>();
		for(int i=0;i<nodes.getLength();i++){
			if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
				list.add((Element)nodes.item(i));
			}
			continue;
		}
		return list;
	}
	/**
	 * 复制 不同document中的节点 到sourceDocument.
	 * @param e
	 * @param sourceDocument
	 * @return
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
		if(nextNode!=null && nextNode instanceof Text){
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
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new OutputStreamWriter(out,"UTF-8"));
		
		final NamedNodeMap attrs = e.getAttributes();
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		Iterator<Attribute> iterator = new Iterator<Attribute>(){
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
		eventWriter.add(eventFactory.createStartElement(new QName(e.getNodeName()), iterator, null));
		
		addEvent(e.getChildNodes(), eventWriter,eventFactory);
		
		eventWriter.add(eventFactory.createEndElement(new QName(e.getNodeName()), null));
		eventWriter.flush();
		eventWriter.close();
		out.flush();
		out.close();
		
		return new String(out.toByteArray(),"UTF-8");
	}
	
	public String childToString(Element e) throws Exception{
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new OutputStreamWriter(out,"UTF-8"));
		
		final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		addEvent(e.getChildNodes(), eventWriter,eventFactory);
		eventWriter.flush();
		eventWriter.close();
		out.flush();
		out.close();
		
		return new String(out.toByteArray(),"UTF-8");
	}
	
	public static void main(String[] args) throws Exception{
		NodeUtil util = new NodeUtil(false);
		Document doc = util.parse(new File("C:/Users/lixiaobao/Desktop/xiaoxisheji/scmonitorCopy/src/dao/jp/co/khi/scm/dao/ibatis/sql/MCategoryUpdateSQL.xml"));
		
		System.out.println(util.childToString(doc.getDocumentElement()));
		
		
//		System.out.println("1212<!DOCTYPE sqlMap PUBLIC \"-//ibatis.apache.org//DTD SQL Map 2.0//EN\"\n     \"http://ibatis.apache.org/dtd/sql-map-2.dtd\">41545".replaceFirst("<!DOCTYPE\\s[^>]*>", ""));
		System.out.println("end");
	}
}
