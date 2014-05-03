package mobi.language;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.AttributeTypeEnum;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;
import mobi.core.concept.Tale;
import mobi.core.relation.GenericRelation;

public class ParserSpecificRelation {

	private Lexer lexer = null;
	private String tokenString = "";
	private int token;
	private CreateDomain mobiLanguage;

	public ParserSpecificRelation(InputStream in) {
		this.lexer = new Lexer(in);
		this.mobiLanguage = new CreateDomain("DefaultDomain");
	}

	public Expression parse() throws Exception {
		nextToken();
		Expression exp = expression();
		// expect( Lexer.EOF, Lexer.EOL);
		return exp;
	}

	private Expression expression() throws Exception {

		while (this.token != TokenEnum.FINAL_LINE.getId()) {
			System.out.println("this.token: " + this.token);
			
			if (token == TokenEnum.CREATE_HISTORY.getId()) {
				this.createTale();
			} else if (token == TokenEnum.CREATE_RELATION.getId()) {
				this.createRelation();
				this.mobiLanguage.addTypeRelation(); // adicionando a rela��o
														// criada ao mobi
			} else if (token == TokenEnum.CREATE_CLASS.getId()) {
				createClass();
			} else if (token == TokenEnum.DECLARE_ATTRIBUTES.getId()) {
				System.out.println("declareAttributes");
				declareAttributes();
			}
			nextToken();

		}
		return this.mobiLanguage;
	}

	/**
	 * @author Andr� Schmid Covers the DECLARE_ATTRIBUTES command Leaves the
	 *         token in the current position
	 * @throws Exception
	 */
	private void declareAttributes() throws Exception {
		validateNextToken("{");
		validateNextToken(TokenEnum.EOL);
		validateNextToken(TokenEnum.INSTANCE_ATTRIBUTES);
		instanceAttributes();
		validateCurrentToken("}");
	}

	/**
	 * @author Andr� Schmid Covers the INSTANCE_ATTRIBUTES command Leaves the
	 *         token in the next position
	 * @throws Exception
	 */
	private void instanceAttributes() throws Exception {
		/**
		 * [instanceUri, [attributeUri, [value, type](1)]*]*
		 */
		HashMap<String, HashMap<String, HashMap<String, AttributeTypeEnum>>> instanceAttributeAndValueMap = new HashMap<String, HashMap<String, HashMap<String, AttributeTypeEnum>>>();
		do {
			validateCurrentToken(TokenEnum.INSTANCE_ATTRIBUTES);
			validateNextToken("(");
			nextToken();
			String instanceName = tokenString;
			if (!instanceAttributeAndValueMap.containsKey(tokenString)) {
				instanceAttributeAndValueMap
						.put(tokenString,
								new HashMap<String, HashMap<String, AttributeTypeEnum>>());
			}
			validateNextToken(",");
			do {
				nextToken();
				HashMap<String, HashMap<String, AttributeTypeEnum>> attributeAndValueMap = instanceAttributeAndValueMap
						.get(instanceName);
				String currentAttributeName = tokenString;
				System.out.println("currentAttributeName "
						+ currentAttributeName);
				if (attributeAndValueMap.containsKey(currentAttributeName)) {
					throw new Exception(
							"There is already an defined value for the attribute "
									+ currentAttributeName
									+ " of the instance " + instanceName);
				}
				validateNextToken(":");
				nextToken();
				String value = "";
				HashMap<String, AttributeTypeEnum> valueMap = new HashMap<String, AttributeTypeEnum>();
				AttributeTypeEnum type;
				if (tokenString.equals("\"")) {
					type = AttributeTypeEnum.STRING;
					nextToken();
					boolean firstStringToken = true;
					while (!tokenString.equals("\"")) {
						if (firstStringToken) {
							firstStringToken = false;
							value += tokenString;
						} else {
							value += " " + tokenString;
						}
						nextToken();
					}
				} else {
					type = AttributeTypeEnum.INTEGER;
					value = tokenString;
				}
				valueMap.put(value, type);
				System.out.println("value " + valueMap.keySet());
				System.out.println("type " + valueMap.values());
				attributeAndValueMap.put(currentAttributeName, valueMap);
				nextToken();
			} while (tokenString.equals(","));
			validateCurrentToken(")");
			validateNextToken(TokenEnum.EOL);
			nextToken();
		} while (!tokenString.equals("}"));
		System.out.println(instanceAttributeAndValueMap);
		mobiLanguage
				.createClassAttributesByInstances(instanceAttributeAndValueMap);
	}

	/**
	 * @author Andr� Schmid Covers the CREATE_CLASS command
	 * @throws Exception
	 */
	@Deprecated
	private void createClass() throws Exception {
		System.out.println("Started createClass()");
		System.out.println("tokenString " + tokenString);
		validateNextToken("{");
		validateNextToken(TokenEnum.EOL);
		nextToken();
		String className = tokenString;
		validateNextToken(",");
		validateNextToken(TokenEnum.EOL);
		validateNextToken(TokenEnum.ADD_ATTRIBUTE);
		List<String> attributeNameList = addAtribute();
		validateCurrentToken("}");
		// TODO add current attributes to MOBI
		// mobiLanguage.createClassAttributes(className, attributeNameList);
	}

	/**
	 * @author Andr� Schmid Covers the ADD_ATTRIBUTE command after an
	 *         CREATE_CLASS
	 * @return List of attributes names found
	 * @throws Exception
	 * 
	 */
	@Deprecated
	private List<String> addAtribute() throws Exception {
		List<String> attributeNameList = new ArrayList<String>();
		while (token == TokenEnum.ADD_ATTRIBUTE.getId()) {
			validateNextToken("(");
			nextToken();
			attributeNameList.add(tokenString);
			validateNextToken(")");
			validateNextToken(TokenEnum.EOL);
			nextToken();
		}
		return attributeNameList;
	}

	private void validateCurrentToken(TokenEnum tokenEnum) throws Exception {
		if (token != tokenEnum.getId()) {
			throw new Exception("Token atual inv�lido: \"" + tokenString
					+ "\". Token esperado: \"" + tokenEnum.getName() + "\"");
		}
	}

	private void validateCurrentToken(String expectedToken) throws Exception {
		if (tokenString != expectedToken) {
			throw new Exception("Token atual inv�lido: \"" + tokenString
					+ "\". Token esperado: \"" + expectedToken + "\"");
		}
	}

	private void validateNextToken(TokenEnum tokenEnum) throws Exception {
		nextToken();
		if (token != tokenEnum.getId()) {
			throw new Exception("Token atual inv�lido: \"" + tokenString
					+ "\". Token esperado: \"" + tokenEnum.getName() + "\"");
		}
	}

	private void validateNextToken(String expectedToken) throws Exception {
		nextToken();
		if (tokenString != expectedToken) {
			throw new Exception("Token atual inv�lido: \"" + tokenString
					+ "\". Token esperado: \"" + expectedToken + "\"");
		}
	}

	private void createTale() throws Exception {
		nextToken(); // Caracter abre par�nteses
		nextToken(); // Caracter abre aspas
		nextToken(); // Nome da hist�ria
		System.out.println(this.tokenString);

		// this.mobiLanguage.getTale().setUri(this.tokenString);
		this.mobiLanguage.setTale(new Tale(this.tokenString));

		nextToken(); // Caracter fecha aspas
		nextToken(); // Caracter virgula
		nextToken(); // Caracter abre aspas, inicio da hist�ria
		nextToken(); // Primeira palavra da hist�ria

		System.out.println(this.tokenString);

		/*
		 * Percorre todas as palavras da hist�ria digitada at� encontrar o
		 * tokenString igual ao caracter fecha aspas
		 */

		while (!this.tokenString.equals("\"")) {
			if (!this.tokenString.equals("fim de linha")) {
				if (this.mobiLanguage.getTale().getText() != null) { // J� foi
																		// setada
																		// alguma
																		// palavra
																		// da
																		// hist�ria
					this.mobiLanguage.getTale().setText(
							this.mobiLanguage.getTale().getText() + " "
									+ this.tokenString); // Setando a hist�ria
															// criada
				} else {
					// N�o foi setada nenhuma palavra da hist�ria.
					this.mobiLanguage.getTale().setText(this.tokenString);
				}

			} else { // tokenString = final de linha, foi pulada uma linha na
						// esrita da hist�ria
				this.mobiLanguage.getTale().setText(
						this.mobiLanguage.getTale().getText() + "\n"); // Insere-se
																		// um
																		// quebra
																		// de
																		// linha
																		// no
																		// texto
			}

			nextToken(); // Proxima palavra da hist�ria
		}
		System.out.println("Hist�ria" + this.mobiLanguage.getTale().getText());

		nextToken(); // Caracter fecha par�ntese
		nextToken(); // Caracter virgula
		nextToken(); // Caracter fim de linha
		nextToken(); // Fim de linha(espa�o entre a senten�a HISTORY e o
						// CREATE_RELATION)

		this.mobiLanguage.getMobi().addConcept(this.mobiLanguage.getTale()); // adiciona
																				// a
																				// hist�ria
																				// criada
																				// ao
																				// mobi
	}

	private void createRelation() throws Exception {
		Mobi mobi = this.mobiLanguage.populatedDomain();
		Relation relation = null;

		if (this.token == TokenEnum.CREATE_RELATION.getId()) {

			nextToken(); // Palavra reservada CREATE_RELATION
			nextToken(); // Cracter abre chave;
			nextToken(); // Nome da rela��o
			System.out.println("Nome relacao:" + this.tokenString);

			String relationName = this.tokenString;
			// this.mobiLanguage
			// .setGenericRelation((GenericRelation) this.mobiLanguage
			// .getMobi().createGenericRelation(this.tokenString));

			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // Tipo da rela��o
			nextToken(); // Caracter abre par�nteses
			nextToken(); // Nome do tipo da rela��o

			String relationType = this.tokenString.toUpperCase();

			// this.mobiLanguage.setTypeNameRelation(this.tokenString
			// .toUpperCase());

			nextToken(); // Caracter virgula
			nextToken(); // nome da propriedade de ida
			// this.mobiLanguage.setPropertyA(this.tokenString);
			String frontRelationName = this.tokenString;
			String backRelationName = null;
			nextToken(); // Caracter fecha par�ntese ou virgula
			if (this.tokenString.equals(",")) { // Composi��o � sim�trica ou �
												// bidirecional
				nextToken(); // nome da propriedade de volta
				// this.mobiLanguage.setPropertyB(this.tokenString);
				backRelationName = this.tokenString;
				nextToken(); // Caracter fecha par�ntese
			}
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // History ou ClassA
			System.out.println(this.tokenString);
			if (!this.tokenString.equals("CLASSA")) { // Foi inserida a hist�ria
														// da rela��o
				nextToken(); // Caracter abre par�ntese
				nextToken(); // Caracter abre aspas
				nextToken(); // Nome da hist�ria da rela��o
				System.out.println("Nome da Hist�ria adicionada a rela��o"
						+ this.tokenString);
				this.mobiLanguage.setNameTaleRelation(this.tokenString);

				nextToken(); // Caracter fecha aspas
				nextToken(); // Caracter fecha par�ntese
				nextToken(); // Caracter virgula
				nextToken(); // Fim de linha
				nextToken(); // ClASSA
			}

			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da ClASSA
			System.out.println("Nome ClasseA:" + this.tokenString);
			// this.mobiLanguage.createClassA(this.tokenString);
			String nameClassA = this.tokenString;
			Class classA = this.mobiLanguage.createClass(nameClassA);
			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // ClASSB
			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da ClASSB

			// System.out.println("Nome ClasseB: " + this.tokenString);
			String nameClassB = this.tokenString;
			Class classB = this.mobiLanguage.createClass(nameClassB);
			// this.mobiLanguage.createClassB(this.tokenString);
			// this.mobiLanguage.getGenericRelation().setClassA(
			// this.mobiLanguage.getClassA());
			// this.mobiLanguage.getGenericRelation().setClassB(
			// this.mobiLanguage.getClassB());

			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha
			nextToken(); // ADDINSTACE_RELATION
			if (relationType.equals("COMPOSITION")) {
				if (backRelationName != null) {
					relation = mobi.createBidirecionalCompositionRelationship(
							frontRelationName, backRelationName);
				} else {
					relation = mobi
							.createUnidirecionalCompositionRelationship(frontRelationName);
				}
			} else if (relationType.equals("INHERITANCE")) {
				relation = mobi.createInheritanceRelation(frontRelationName);
			} else if (relationType.equals("EQUIVALENCE")) {
				relation = mobi.createEquivalenceRelation(frontRelationName);
			} else if (relationType.equals("SYMMETRIC")) {
				relation = mobi.createSymmetricRelation(frontRelationName);
			}
			relation.setClassA(classA);
			relation.setClassB(classB);
			if (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) { // token igual a
				// addRelation
				System.out.println("Token AddRrelation pan +" + tokenString);

				while (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) {

					nextToken(); // Caracter abre par�ntese ap�s o add Relation
					nextToken(); // Nome da InstanciaA ou caracter menos

					String nameInstanceA = this.tokenString;
					Instance instanceA = this.mobiLanguage
							.createInstance(nameInstanceA);
					if (instanceA != null) {
						this.mobiLanguage.createIsOneOf(instanceA, classA);
					}
//					this.mobiLanguage.createInstanceA(nameInstanceA);
//					if (this.mobiLanguage.getInstanceA() != null) {
//						this.mobiLanguage.createIsOneOfA(nameInstanceA);
//					}

					nextToken(); // Caracter virgula
					nextToken(); // Nome da InstanciaB ou caracter menos

					String nameInstanceB = this.tokenString;
					
					Instance instanceB = this.mobiLanguage
							.createInstance(nameInstanceB);
					if (instanceB != null) {
						this.mobiLanguage.createIsOneOf(instanceB, classB);
					}
					
//					this.mobiLanguage.createInstanceB(nameInstanceB);
//					if (this.mobiLanguage.getInstanceB() != null) {
//						this.mobiLanguage.createIsOneOfB(nameInstanceB);
//					}

					nextToken(); // Caracter fecha par�ntese ap�s o add Relation
					nextToken();
					System.out.println("TOOOKEN " + this.tokenString);

					if (this.tokenString.equals(",")) { // H� mais addRelation
						nextToken(); // fim de linha
						nextToken(); // addRelation
					}
					
					relation.addInstanceRelation(instanceA, instanceB);

//					this.mobiLanguage.addGenericRelation();
				}
				relation.processCardinality();
				mobi.addConcept(relation);
			}
		}
		// Relation rItemPedidoTemProduto =
		// mobi.createUnidirecionalCompositionRelationship("tem");
		// rItemPedidoTemProduto.setClassA(itemPedidoClass);
		// rItemPedidoTemProduto.setClassB(produtoClass);
		// rItemPedidoTemProduto.addInstanceRelation(ip1, prod1);
		// rItemPedidoTemProduto.addInstanceRelation(ip2, prod2);
		// rItemPedidoTemProduto.addInstanceRelation(ip3, prod3);
		// rItemPedidoTemProduto.addInstanceRelation(ip4, prod3);
		// rItemPedidoTemProduto.addInstanceRelation(ip5, prod6);
		// rItemPedidoTemProduto.addInstanceRelation(ip6, prod8);
		// rItemPedidoTemProduto.addInstanceRelation(ip7, prod2);
		// rItemPedidoTemProduto.processCardinality();
		// mobi.addConcept(rItemPedidoTemProduto);

	}

	private void createRelationOld() throws Exception {

		if (this.token == TokenEnum.CREATE_RELATION.getId()) {

			nextToken(); // Palavra reservada CREATE_RELATION
			nextToken(); // Cracter abre chave;
			nextToken(); // Nome da rela��o
			System.out.println("Nome relacao:" + this.tokenString);

			// Relation rItemPedidoTemProduto =
			// mobi.createUnidirecionalCompositionRelationship("tem");
			// rItemPedidoTemProduto.setClassA(itemPedidoClass);
			// rItemPedidoTemProduto.setClassB(produtoClass);
			// rItemPedidoTemProduto.addInstanceRelation(ip1, prod1);
			// rItemPedidoTemProduto.addInstanceRelation(ip2, prod2);
			// rItemPedidoTemProduto.addInstanceRelation(ip3, prod3);
			// rItemPedidoTemProduto.addInstanceRelation(ip4, prod3);
			// rItemPedidoTemProduto.addInstanceRelation(ip5, prod6);
			// rItemPedidoTemProduto.addInstanceRelation(ip6, prod8);
			// rItemPedidoTemProduto.addInstanceRelation(ip7, prod2);
			// rItemPedidoTemProduto.processCardinality();
			// mobi.addConcept(rItemPedidoTemProduto);

			this.mobiLanguage
					.setGenericRelation((GenericRelation) this.mobiLanguage
							.getMobi().createGenericRelation(this.tokenString));

			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // Tipo da rela��o
			nextToken(); // Caracter abre par�nteses
			nextToken(); // Nome do tipo da rela��o
			this.mobiLanguage.setTypeNameRelation(this.tokenString
					.toUpperCase());

			nextToken(); // Caracter virgula
			nextToken(); // nome da propriedade de ida
			this.mobiLanguage.setPropertyA(this.tokenString);

			nextToken(); // Caracter fecha par�ntese ou virgula
			if (this.tokenString.equals(",")) { // Composi��o � sim�trica ou �
												// bidirecional
				nextToken(); // nome da propriedade de volta
				this.mobiLanguage.setPropertyB(this.tokenString);
				nextToken(); // Caracter fecha par�ntese
			}
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // History ou ClassA
			System.out.println(this.tokenString);
			if (!this.tokenString.equals("CLASSA")) { // Foi inserida a hist�ria
														// da rela��o
				nextToken(); // Caracter abre par�ntese
				nextToken(); // Caracter abre aspas
				nextToken(); // Nome da hist�ria da rela��o
				System.out.println("Nome da Hist�ria adicionada a rela��o"
						+ this.tokenString);
				this.mobiLanguage.setNameTaleRelation(this.tokenString);

				nextToken(); // Caracter fecha aspas
				nextToken(); // Caracter fecha par�ntese
				nextToken(); // Caracter virgula
				nextToken(); // Fim de linha
				nextToken(); // ClASSA
			}

			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da ClASSA
			System.out.println("Nome ClasseA:" + this.tokenString);
			this.mobiLanguage.createClassA(this.tokenString);

			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha

			nextToken(); // ClASSB
			nextToken(); // Caracter abre par�ntese
			nextToken(); // Nome da ClASSB

			System.out.println("Nome ClasseB: " + this.tokenString);

			this.mobiLanguage.createClassB(this.tokenString);
			this.mobiLanguage.getGenericRelation().setClassA(
					this.mobiLanguage.getClassA());
			this.mobiLanguage.getGenericRelation().setClassB(
					this.mobiLanguage.getClassB());

			nextToken(); // Caracter fecha par�ntese
			nextToken(); // Caracter virgula
			nextToken(); // Fim de linha
			nextToken(); // ADDINSTACE_RELATION
		}
		if (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) { // token igual a
														// addRelation
			System.out.println("Token AddRrelation pan +" + tokenString);

			while (this.token == TokenEnum.ADDINSTACE_RELATION.getId()) {

				nextToken(); // Caracter abre par�ntese ap�s o add Relation
				nextToken(); // Nome da InstanciaA ou caracter menos

				String nameInstanceA = this.tokenString;
				this.mobiLanguage.createInstanceA(nameInstanceA);
				if (this.mobiLanguage.getInstanceA() != null) {
					this.mobiLanguage.createIsOneOfA(nameInstanceA);
				}

				nextToken(); // Caracter virgula
				nextToken(); // Nome da InstanciaB ou caracter menos

				String nameInstanceB = this.tokenString;
				this.mobiLanguage.createInstanceB(nameInstanceB);
				if (this.mobiLanguage.getInstanceB() != null) {
					this.mobiLanguage.createIsOneOfB(nameInstanceB);
				}

				nextToken(); // Caracter fecha par�ntese ap�s o add Relation
				nextToken();
				System.out.println("TOOOKENNNNN" + this.tokenString);

				if (this.tokenString.equals(",")) { // H� mais addRelation
					nextToken(); // fim de linha
					nextToken(); // addRalation
				}

				this.mobiLanguage.addGenericRelation();
				System.out.println(" addInstanceRelation "
						+ this.mobiLanguage.getInstanceA() + ","
						+ this.mobiLanguage.getInstanceB());
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
