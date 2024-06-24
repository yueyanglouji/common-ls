package l.s.common.thymeleaf;

public abstract class AbstractFileThymeleaf extends Thymeleaf{

	protected String encoding;

	/**
	 * prefix is file directory path
	 */
	protected String prefix;

	/**
	 * default suffix is .html
	 */
	protected String suffix;

	protected boolean cacheAble;

	public AbstractFileThymeleaf(){
		this.encoding = "UTF-8";
		this.suffix = ".html";
		this.prefix = getWEB_INF_Path();
	}

	public AbstractFileThymeleaf(CONTEXT_TYPE contextType) {
		super(contextType);
		this.encoding = "UTF-8";
		this.suffix = ".html";
		this.prefix = getWEB_INF_Path();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setCacheAble(boolean cacheAble) {
		this.cacheAble = cacheAble;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isCacheAble() {
		return cacheAble;
	}

	public static String getWEB_INF_Path(){
		String filePath = AbstractFileThymeleaf.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(filePath.indexOf("WEB-INF") > 0) {
			filePath = filePath.substring(0, filePath.lastIndexOf("WEB-INF"));
			if(filePath.startsWith("file:/")){
				filePath = filePath.substring(6);
			}
		}
		return filePath;
	}

}
