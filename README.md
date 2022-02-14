# use this project
gradle:
```java
implementation("com.woyouyigegushi.common-ls:common-ls:1.0.0")
```
maven:
```java
<dependency>
    <groupId>com.woyouyigegushi.common-ls</groupId>
    <artifactId>common-ls</artifactId>
    <version>1.0.0</version>
</dependency>

```

## GroovySQL
   This is a [mybatis](https://mybatis.org/mybatis-3/) plugin with [groovy](http://www.groovy-lang.org/) script language driver.
   
   doc: [GroovySql.md](doc/GroovySQL.md)

## Bean connector
   You can use the BeanConnector to access a java bean data.
   ```java
        Person person = new Person("Jan", 30);
        BeanConnector connector = BeanConnector.connect(person);
        System.out.println(connector.getProperty("age"));
        System.out.println(connector.getProperty("name"));
   ```
## Config properties
   Read configuration from multi place.
   For example, if a properties file in the jar file and the content is
   ```properties
   example.key1=key1
   example.key2=key2
   ```
   And a properties file in the classpath(WEB-INF/classes or the home of a bach e.g.),and the content is
   ```properties
   example.key1=cat
   ```
   so, if you call 
   ```java
   ConfigProperty configProperty = ConfigProperty.getInstance();
   configProperty.setFileEncoding("UTF-8");
   configProperty.setLocations(getResource());
   configProperty.loadProperties();
   configProperty.getProperty("example.key1", "default"); // return cat
   configProperty.getProperty("example.key2", "default"); // return key2
   List<Properties> history = configProperty.getAllPropertiesLoadedHistoryIncludeOverwrite(); // return all histories of all properties file loaded.
   ```
## context
   easy to use of Context.
   
   Application: Application.getContext().setAttribute();

   GlobalContext: GlobalContext.getContext().setAttribute();
   
   ThreadLocalContext: ThreadLocalContext.getContext().setAttribute();
   
   ServletContext: servlet information.

   ServletContextFilter: a filter to web application.
## csv
   ```java
   CSVHeader header = CSV.openCsv("test.csv", "UTF-8", true).getHeader();
   List<CSVRow> list =  CSV.openCsv("test.csv", "UTF-8", true).getRows();
   CSV.newCsv("header1", "header2", "header3").addRow(new String[]{"1", "2", "3"}).addRow("4", "5", "6").saveFile("test.csv", "UTF-8");
   ```
## excel
## ftp
## groovy
## httpclient
## json
## mail
## messagesource
## mybatis
## quartz
## string
## thymeleaf
## util
       

