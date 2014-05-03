package mobi.language;

public enum TokenEnum {
	
	INVALID(-1, "Invalid"), NO_TOKENS(0, "No Tokens"), CREATE_HISTORY(1, "CREATE_HISTORY"), CREATE_RELATION(2, "CREATE_RELATION"), ADDINSTACE_RELATION(3, "ADDINSTACE_RELATION")
	, TYPE(4, "TYPE"), HISTORY(5, "HISTORY"), ClASSA(6, "ClASSA"), ClASSB(7, "ClASSB"), INHERITANCE(8, "INHERITANCE"), EQUIVALENCE(9, "EQUIVALENCE"), COMPOSITION(10, "COMPOSITION")
	, SYMMETRIC(11, "SYMMETRIC"), FINAL_LINE(12, "FINAL_LINE"), EOL(13, "EOL"), EOF(14, "EOF"), CREATE_CLASS(15, "CREATE_CLASS"), ADD_ATTRIBUTE(16, "ADD_ATTRIBUTE")
	, INSTANCE_ATTRIBUTES(17, "INSTANCE_ATTRIBUTES"), DECLARE_ATTRIBUTES(18, "DECLARE_ATTRIBUTES") ;
	private final int id;
	private final String name;
	
	private TokenEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

}
