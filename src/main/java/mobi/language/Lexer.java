/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobi.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

/**
 * 
 * @author Ramon Mota
 * 
 */
public class Lexer {
	// TODO current token should be an TokenEnum instead of int
	public static String tokenCurrent = "";
	public int token = 0;

	private StreamTokenizer input;

	public Lexer(InputStream in) {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		input = new StreamTokenizer(r);
		input.resetSyntax();
		input.eolIsSignificant(true);

		input.wordChars('a', 'z');
		input.wordChars('A', 'Z');
		// TODO habilitar d�gitos para as vari�veis
		input.wordChars('0', '9');
		input.wordChars('_', '_');
		input.whitespaceChars('\u0000', '\u0020');
		input.ordinaryChar('-');
		input.ordinaryChar(';');
		input.ordinaryChar('(');
		input.ordinaryChar(')');
		input.ordinaryChar(',');
		input.ordinaryChar('{');
		input.ordinaryChar('}');
		input.ordinaryChar('"');
		input.ordinaryChar(':');
	}

	public int nextToken() throws Exception {
		int token = 0;
		try {
			int nextToken = input.nextToken();
			System.out.println("CURRENT INPUT " + input);
			System.out.println("CURRENT VAL: " + input.sval);
			switch (nextToken) {
			case StreamTokenizer.TT_EOF:
				token = TokenEnum.EOF.getId();
				tokenCurrent = TokenEnum.EOF.getName();
				break;
			case StreamTokenizer.TT_EOL:
				tokenCurrent = TokenEnum.EOL.getName();
				token = TokenEnum.EOL.getId();
				break;
			case StreamTokenizer.TT_WORD:				
				tokenCurrent = input.sval;				
				if (input.sval.equalsIgnoreCase(TokenEnum.CREATE_HISTORY.getName())) {
					token = TokenEnum.CREATE_HISTORY.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.CREATE_RELATION.getName())) {
					token = TokenEnum.CREATE_RELATION.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.ADDINSTACE_RELATION.getName())) {
					token = TokenEnum.ADDINSTACE_RELATION.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.TYPE.getName())) {
					token = TokenEnum.TYPE.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.HISTORY.getName())) {
					token = TokenEnum.HISTORY.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.ClASSA.getName())) {
					token = TokenEnum.ClASSA.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.ClASSB.getName())) {
					token = TokenEnum.ClASSB.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.INHERITANCE.getName())) {
					token = TokenEnum.INHERITANCE.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.EQUIVALENCE.getName())) {
					token = TokenEnum.EQUIVALENCE.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.COMPOSITION.getName())) {
					token = TokenEnum.COMPOSITION.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.SYMMETRIC.getName())) {
					token = TokenEnum.COMPOSITION.getId();
				} 
				/*else if (input.sval.equalsIgnoreCase("CREATE_CLASS")) {					
					token = TokenEnum.CREATE_CLASS.getId();
				} else if (input.sval.equalsIgnoreCase("ADD_ATTRIBUTE")) {					
					token = TokenEnum.ADD_ATTRIBUTE.getId();
				}*/ else if (input.sval.equalsIgnoreCase(TokenEnum.DECLARE_ATTRIBUTES.getName())) {					
					token = TokenEnum.DECLARE_ATTRIBUTES.getId();
				} else if (input.sval.equalsIgnoreCase(TokenEnum.INSTANCE_ATTRIBUTES.getName())) {					
					token = TokenEnum.INSTANCE_ATTRIBUTES.getId();
				}
				break;
			case '{':
				tokenCurrent = "{";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case '}':
				tokenCurrent = "}";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case ';':
				tokenCurrent = ";";
				token = TokenEnum.FINAL_LINE.getId();
				break;
			case '(':
				tokenCurrent = "(";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case ',':
				tokenCurrent = ",";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case ')':
				tokenCurrent = ")";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case '-':
				tokenCurrent = "-";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case '"':
				tokenCurrent = "\"";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case ':':
				tokenCurrent = ":";
				token = TokenEnum.NO_TOKENS.getId();
				break;
			case StreamTokenizer.TT_NUMBER:
				System.err.println("entrou no TT_NUMBER");
				break;
			default:
				token = TokenEnum.INVALID.getId();
				throw new Exception(
						"ERRO LEXICO: Cadeia de caracteres invalida.");
			}
		} catch (IOException e) {
			token = TokenEnum.EOF.getId();
		}
		return token;
	}

}
