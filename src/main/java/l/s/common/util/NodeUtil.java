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
import java.nio.charset.StandardCharsets;
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

@Deprecated
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
		return parse(file, "UTF-8");
	}

	public Document parse(File file, String charset) throws Exception{
		Scanner in = null;
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset);
			in = new Scanner(reader);
			StringBuilder builder = new StringBuilder();
			while(in.hasNextLine()){
				builder.append(in.nextLine());
				builder.append("\n");
			}
			return parse(builder.toString());
		} finally {
			IoUtil.close(in);
		}
	}

	public Document parse(InputStream in){
		return parse(in, "UTF-8");
	}

	public Document parse(InputStream in, String charset){
		Scanner scanner = null;
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(in, charset);
			scanner = new Scanner(reader);
			StringBuilder builder = new StringBuilder();
			while(scanner.hasNextLine()){
				builder.append(scanner.nextLine());
				builder.append("\n");
			}
			return parse(builder.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			IoUtil.close(scanner);
			IoUtil.close(reader);
			IoUtil.close(in);
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
	 * When using this method, please start evaluate with "./" or ".//". If
	 * you don't do this, the search will still start from the root node of the document. The effect is the same as the above method
	 * .
	 * @param e node
	 * @param cl
	 * This type uses generics, which is consistent with the return type and does not require forced conversion
	 * . The Class type can only be String.class, NodeList.class, and
	 * Element.class, Attr.class, Comment.class, Text.class, Node.class, Integer.class, Long.class, Double.class, Float.class, boolean.class.
	 * @return
	 * The return type is consistent with the generic type passed in
	 * If the node is not found: 
	 * 1. If the return type is NodeList type, the returned object is a NodeList instance with length==0.
	 * 2. If the return type is Element or Attr, NULL is returned.
	 * 3. If the return type is String, "" is returned --- a String object with a length of 0.
	 * 4. If the return type is int or long, the decimal point will be omitted.
	 * All of the above can be forced to convert without causing type conversion errors due to Null.
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

	public void outputToStream(Document doc,OutputStream outputStream)throws Exception{
		outputToStream(doc, outputStream, "UTF-8");
	}

	public void outputToStream(Document doc, OutputStream outputStream, String charset)throws Exception{
		outputToStream(doc.getDocumentElement(), outputStream, charset);
	}

	public void outputToStream(Element element, OutputStream outputStream)throws Exception{
		outputToStream(element, outputStream, "UTF-8");
	}

	public void outputToStream(Element element, OutputStream outputStream, String charset)throws Exception{
		PrintWriter out = null;
		XMLEventWriter eventWriter = null;
		try{
			out = new PrintWriter(new OutputStreamWriter(outputStream, charset));

			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			eventWriter = outputFactory.createXMLEventWriter(out);

			final NamedNodeMap attrs = element.getAttributes();
			final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			Iterator<Attribute> iterator = getAttributeIterator(attrs, eventFactory);
			eventWriter.add(eventFactory.createStartDocument("UTF-8","1.0"));
			eventWriter.add(eventFactory.createStartElement(new QName(element.getNodeName()), iterator, null));

			addEvent(element.getChildNodes(), eventWriter,eventFactory);

			eventWriter.add(eventFactory.createEndElement(new QName(element.getNodeName()), null));
			eventWriter.flush();
			out.flush();
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

	public void outputToFile(Document doc, File file)throws Exception{
		outputToFile(doc, file, "UTF-8");
	}

	public void outputToFile(Document doc, File file, String charset)throws Exception{
		IoUtil.mkdirsParents(file);
		IoUtil.createNewFile(file);
		outputXmlFile(doc, file, charset);
	}

	public void outputToFile(Element doc, File file)throws Exception{
		outputToFile(doc, file, "UTF-8");
	}

	public void outputToFile(Element doc, File file, String charset)throws Exception{
		IoUtil.mkdirsParents(file);
//		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		IoUtil.createNewFile(file);
//		transformer.transform(new DOMSource(doc), new StreamResult(file));
		
		//zui 2
		outputXmlFile(doc, file, charset);
	}


	private void outputXmlFile(Document doc, File file, String charset) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(file)) {
						outputToStream(doc, fos, charset);
		}
	}

	private void outputXmlFile(Element element, File file, String charset) throws Exception {
	    try (FileOutputStream fos = new FileOutputStream(file)) {
	        outputToStream(element, fos, charset);
	    }
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
	
	public void change(String evaluate, Node node, String value){
		Node get = trip(evaluate, node, Node.class);
		change(get, value);
	}

	/**
	 * Modify the value of a node.
	 *
	 * <p>This method calls the corresponding modification method according to the type of the node. If the node is an attribute node ({@code ATTRIBUTE_NODE}), the method for modifying the attribute value is called;
	 * If the node is an element node ({@code ELEMENT_NODE}), the method for modifying the attribute value of the element node is called. If the node is neither an attribute node nor an element node,
	 * A runtime exception is thrown. </p>
	 *
	 * @param node The node to be modified
	 * @param value The new node value
	 *
	 * @throws RuntimeException Thrown if the node is neither an attribute node nor an element node
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
	 * Modify the value of an attribute.
	 *
	 * <p>This method is used to change the value of a given attribute. </p>
	 *
	 * @param attr The attribute object to be modified
	 * @param value The new attribute value
	 *
	 */
	private void change(Attr attr,String value){
		if(attr==null) return;
		attr.setNodeValue(value);
	}

	/**
	 * Modify the attribute value of an element.
	 *
	 * <p>This method is used to change the value of the specified attribute of the specified element. </p>
	 *
	 * @param element The element whose attribute is to be modified
	 * @param attr The name of the attribute to be modified
	 * @param value The new attribute value
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

	public Element cloneElement(Document document,String evaluate){
		Element target = this.trip(evaluate, document, Element.class);
		return cloneElement(target);
	}

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
	 * Copies nodes from different documents to the target document.
	 *
	 * <p>This method copies the specified element node and all its attributes and child nodes from the source document to the target document.
	 * If the element contains text nodes or CDATA section nodes, they will be converted to CDATA sections and copied to the target document.
	 * If the element contains child element nodes, these child elements will also be copied recursively. </p>
	 *
	 * @param e The source element node to be copied
	 * @param sourceDocument The target document, that is, the document to be copied
	 * @return Returns the new element node created in the target document, which is a copy of the source element node
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
				if(!value.trim().isEmpty()){
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
	 * Delete the specified element node and the text node that follows it.
	 *
	 * <p>This method will traverse the child node list of the parent node of the specified element node, find the element node and delete it.
	 * If there is a text node ({@code Text} type) immediately after the element node, delete the text node at the same time.
	 * Please use this method with caution because it will permanently remove the specified element and its subsequent text nodes from the DOM tree. </p>
	 *
	 * @param element The element node to be deleted
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
		outputToStream(e, out, "UTF-8");

		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}
	
	public String childToString(Element e) throws Exception{
		ByteArrayOutputStream out = null;
		XMLEventWriter eventWriter = null;
		try{
			out = new ByteArrayOutputStream();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			eventWriter = outputFactory.createXMLEventWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

			final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			addEvent(e.getChildNodes(), eventWriter,eventFactory);
			eventWriter.flush();
			out.flush();

			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		} finally {
			IoUtil.close(eventWriter);
			IoUtil.close(out);
		}

	}
}
