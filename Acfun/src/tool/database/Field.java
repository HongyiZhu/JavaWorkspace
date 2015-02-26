package tool.database;

public class Field {
	String name;
	String type; // varchar(?), bigint, text, int, date, time
	boolean null_is_allowed;
	String defaultValue;  // null
	String comment;
}
