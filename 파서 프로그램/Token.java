package CScanner;

public class Token {
	public enum SymType { // 쉽게 분류하기 위한 대분류들
		Op, tidOrReserved, Digit, Real, Const, NULL
	}
	
	public enum Symbols { // 토큰의 심볼들
		NULL,
		var, begin, end, tident, tconst, tint, treal, print, NULL8, NULL9,
		Plus, Minus, Mul, Div, Mod, Assign, Not, And, Or, Equal,
		NotEqual, Less, Great, Lesser, Greater, NULL25, NULL26, NULL27, NULL28, NULL29,
		LBracket, RBracket, LBrace, RBrace, LParen, RParen, Comma, Semicolon, SingleQuote, NULL39,
		If, While, For, Const, Int, Float, Else, Return, Void, Break,
		Continue, Char, Then, EOF
	}
	
	private Symbols symbol; //토큰이 가진 심볼 변수
	private String value; // 값을 저장하는 변수
	private String tokenString; // 토큰의 String
	
	// 생성자
	public Token() {
		symbol = Symbols.NULL;
		value = "0";
		tokenString = "NULL";
	}
	
	// 토큰이 가진 심볼과 value를 올바르게 설정하는 메소드
	public void setSymbol(String token, SymType type) {
		tokenString = token;
		switch(type) {
		case tidOrReserved:
			symbol = GETtidentOrReservedWord(token);
			if(symbol == Symbols.tident) {
				value = token;
			}
			break;
		case Digit:
			symbol = Symbols.tint;
            value = Integer.toString(parseInt(token));
            break;
		case Real:
			symbol = Symbols.treal;
            value = token;
            break;
		case Const:
			symbol = Symbols.tconst;
            value = token;
            break;
        case Op:
            symbol = getOp(token);
            break;
        case NULL:
        default:
            break;
    }
}
	
	// 정수의 진법 계산 메소드
	private int parseInt(String s) {
        int radix = 10; // default 진법은 10진수
        if (s.startsWith("0x")){ // 16진수일 경우
            radix = 16; // 진법을 16진수로 설정
            s = s.substring(2); // prefix인 0x 제거
        } else if (s.startsWith("0") && s.length() > 1){ // 8진수일 경우
            radix = 8; // 진법을 8진수로 설정
        }
        return Integer.parseInt(s, radix); // 위에서 설정한 진법대로 진법 변환
    }
			
	// indent나 예약어를 받았을 때 구분해주는 메소드
	private Symbols GETtidentOrReservedWord(String token) {
		switch(token) {
		case "If":      return Symbols.If;
		case "While":   return Symbols.While;
		case "For":   return Symbols.For;
		case "Const":   return Symbols.Const;
		case "Int":   return Symbols.Int;
		case "Float":   return Symbols.Float;
		case "Else":   return Symbols.Else;
		case "Return":   return Symbols.Return;
		case "Void":   return Symbols.Void;
		case "Break":   return Symbols.Break;
		case "Continue":   return Symbols.Continue;
		case "Char":   return Symbols.Char;
		case "Then":   return Symbols.Then;
		
		case "var":      return Symbols.var;
		case "begin":      return Symbols.begin;
		case "end":      return Symbols.end;
		case "print":      return Symbols.print;
		
		default:
			return Symbols.tident;
		}
	}
	
	// 토큰이 연산자인 경우 구분해주는 메소드
	private Symbols getOp(String token) {
        switch (token) {
            case "!":   return Symbols.Not;
            case "!=":  return Symbols.NotEqual;
            case "%":   return Symbols.Mod;
            case "&&":  return Symbols.And;
            case "(":   return Symbols.LParen;
            case ")":   return Symbols.RParen;
            case "*":   return Symbols.Mul;
            case "+":   return Symbols.Plus;
            case ",":   return Symbols.Comma;
            case "-":   return Symbols.Minus;
            case "/":   return Symbols.Div;
            case ";":   return Symbols.Semicolon;
            case "<":   return Symbols.Less;
            case "<=":  return Symbols.Lesser;
            case "=":   return Symbols.Assign;
            case "==":  return Symbols.Equal;
            case ">":   return Symbols.Great;
            case ">=":  return Symbols.Greater;
            case "[":   return Symbols.LBracket;
            case "]":   return Symbols.RBracket;
            case "\255": return Symbols.EOF;
            case "{":   return Symbols.LBrace;
            case "||":  return Symbols.Or;
            case "}":   return Symbols.RBrace;
            case "'":	return Symbols.SingleQuote;
            case "&": // &하나만 있으면 에러
                System.err.print(Error.getErrorMessage(Error.ErrorCode.SingleAmpersand));
                break;
            case "|": // |하나만 있으면 에러
                System.err.print(Error.getErrorMessage(Error.ErrorCode.SingleBar));
                break;
            default: // 그 외 인식을 못하면 에러
                System.err.print(Error.getErrorMessage(Error.ErrorCode.InvalidChar));
                break;
        }
        return Symbols.NULL;
    }
	
	// 토큰 심볼을 숫자로 리턴해주는 메소드
	public int getSymbolOrdinal() {
        return symbol.ordinal()-1;  // NULL이 -1이기 때문에 -1 해야한다.
    }

    // 토큰이 명칭이나 숫자인 경우에 토큰의 값을 얻는 메소드
    public String getSymbolValue() {
        return value;
    }

    // 출력 편의를 위한 toString 메소드 -> parser와의 연계를 위해 @로 문자 파싱을 쉽게 만들어줬습니다.
    public String toString() {
        return tokenString + "\t @: @(" + getSymbolOrdinal() + "@ "+ getSymbolValue() + "@)";
    }
}
