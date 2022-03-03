package l.s.common.csv;

public enum QuoteMode {

    /**
     * Quotes all fields.
     */
    ALL(org.apache.commons.csv.QuoteMode.ALL),

    /**
     * Quotes all non-null fields.
     */
    ALL_NON_NULL(org.apache.commons.csv.QuoteMode.ALL_NON_NULL),

    /**
     * Quotes fields which contain special characters such as a delimiter, quotes character or any of the characters in
     * line separator.
     */
    MINIMAL(org.apache.commons.csv.QuoteMode.MINIMAL),

    /**
     * Quotes all non-numeric fields.
     */
    NON_NUMERIC(org.apache.commons.csv.QuoteMode.NON_NUMERIC),

    /**
     * Never quotes fields. When the delimiter occurs in data, the printer prefixes it with the current escape
     * character. If the escape character is not set, format validation throws an exception.
     */
    NONE(org.apache.commons.csv.QuoteMode.NONE);
	
	private org.apache.commons.csv.QuoteMode v;
	
	private QuoteMode(org.apache.commons.csv.QuoteMode mode) {
		this.v = mode;
	}
	
	public org.apache.commons.csv.QuoteMode getValue(){
		return v;
	}
}
