package mobi.language;

import java.io.InputStream;

import mobi.core.relation.GenericRelation;

public class Parser {

	private Lexer lexer = null;
	private String tokenString = "";
	private int token;
	private CreateDomain mobiLanguage;

	public Parser(InputStream in) {
		lexer = new Lexer(in);
		mobiLanguage = new CreateDomain("DefaultDomain");
	}

	public Expression parse() throws Exception {
		nextToken();
		Expression exp = expression();
		// expect( Lexer.EOF, Lexer.EOL);
		return exp;
	}

	private Expression expression() throws Exception {

		// Enquanto n�o achar o ponto e virgula fa�a
		while (this.token != TokenEnum.FINAL_LINE.getId()) {
			if (this.token == TokenEnum.CREATE_RELATION.getId()) {
				this.createRelation();
				mobiLanguage.getGenericRelation().processCardinality();
				mobiLanguage.getMobi().addConcept(
						mobiLanguage.getGenericRelation());
			}
			nextToken();

		}
		return mobiLanguage;
	}

	private void createRelation() throws Exception {

		if (this.token == TokenEnum.CREATE_RELATION.getId()) {

			nextToken(); // Palavra reservada CREATE_RELATION
			nextToken(); // Cracter abre chave;
			nextToken(); // Nome da rela��o
			System.out.println("Nome relacao:" + this.tokenString);

			mobiLanguage.setGenericRelation((GenericRelation) mobiLanguage
					.getMobi().createGenericRelation(this.tokenString));

			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // ClASSA
			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da CLASSA
			System.out.println("Nome ClasseA:" + this.tokenString);
			mobiLanguage.createClassA(this.tokenString);

			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // ClASSB
			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da CLASSB

			System.out.println("Nome ClasseB: " + this.tokenString);

			mobiLanguage.createClassB(this.tokenString);
			mobiLanguage.getGenericRelation().setClassA(
					mobiLanguage.getClassA());
			mobiLanguage.getGenericRelation().setClassB(
					mobiLanguage.getClassB());

			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha
			nextToken(); // AddRelation
		}
		if (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) { // token igual a
														// addRelation
			System.out.println("Token AddRrelation pan +" + tokenString);

			while (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) {

				nextToken(); // Caracter abre par�ntese ap�s o add Relation
				nextToken(); // Nome da InstanciaA ou menos

				String nameInstanceA = this.tokenString;
				System.out.println("Creating instance A " + nameInstanceA);
				mobiLanguage.createInstanceA(nameInstanceA);
				if (mobiLanguage.getInstanceA() != null) {
					mobiLanguage.createIsOneOfA(nameInstanceA);
				}

				nextToken(); // Caracter virgula
				nextToken(); // Nome da InstanciaB

				String nameInstanceB = this.tokenString;
				System.out.println("Creating instance B " + nameInstanceB);
				mobiLanguage.createInstanceB(nameInstanceB);
				if (mobiLanguage.getInstanceB() != null) {
					mobiLanguage.createIsOneOfB(nameInstanceB);
				}

				nextToken(); // Caracter fecha par�ntese ap�s o add Relation
				nextToken();
				// System.out.println("TOOOKENNNNN" + this.tokenString);

				if (this.tokenString.equals(",")) { // H� mais addRelation
					nextToken(); // fim de linha
					nextToken(); // addRalation
				}

				mobiLanguage.addGenericRelation();
				System.out.println("addInstanceRelation "
						+ mobiLanguage.getInstanceA() + ", "
						+ mobiLanguage.getInstanceB());
			}
		}

	}

	private void nextToken() throws Exception {
		this.token = lexer.nextToken();
		this.tokenString = Lexer.tokenCurrent;
		System.out.println("Token atual -> " + this.tokenName(this.token));
	}

	private String tokenName(int token) {
		if(token == TokenEnum.EOF.getId()){
			return TokenEnum.EOF.getName();
		} else if(token == TokenEnum.FINAL_LINE.getId()){
			return TokenEnum.FINAL_LINE.getName();
		} else {
			return "default";
		}
	}
}
