package CScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CScanner {
    static public final int ID_LENGTH = 16; // 명칭의 길이 제한
	static public final char EOF = '\255'; // EOF는 파일의 끝을 의미합니다
    static public final String SPECIAL_CHARS = "!=&|<>"; // 두글자로 조합가능한 특수문자들

    private String src; // 소스코드를 String으로 저장해줄 변수
    private Integer cursor; // 소스코드를 읽을 때 커서가 될 변수

    // 어떤 토큰인지 인식하기 위해 만든 상태들
    private enum State {
        Initial, Dec, Oct, Hex, Real, Const, tidOrReserved, Op, Zero, PreHex, PreReal, PreConst, SingleOperator
    }
    
    // 생성자
    public CScanner(String filePath) {
        src = parseFile(filePath);
        cursor = 0;
    }
    
    // 소스코드를 String으로 읽어주는 메소드
    private String parseFile(String filePath) {
        String src = "", readedString = ""; // src: 소스코드를 저장해놓을 String 변수, readedString: 소스코드의 한줄을 담아놓을 String 변수
        FileReader fileReader = null; // 소스코드를 읽을 File Reader
        try {
            fileReader = new FileReader(new File(filePath)); // 파일 경로를 이용해 File Reader 생성
        } catch (IOException e) {
            // 파일을 읽을 수 없음
            System.err.print(Error.getErrorMessage(Error.ErrorCode.CannotOpenFile));
            return "";
        }

        BufferedReader reader = new BufferedReader(fileReader); // BufferedReader 객체를 만들어 소스코드 파일을 읽음
        try {
            while ((readedString = reader.readLine()) != null) // 파일로부터 한줄 읽음
                src += readedString + "\n"; // 한줄 맨뒤에 개행문자 추가
            src += EOF;   // 파일의 끝을 의미하는 EOF 문자 추가
            reader.close();
        } catch(IOException e) {
            // 파일을 읽을 수 없음
            System.err.print(Error.getErrorMessage(Error.ErrorCode.CannotOpenFile));
            return "";
        }
        return src;
    }
    
    // EOF인지 검사하는 메소드
    private boolean isEOF(int idx) {
        return idx >= src.length();
    }
    
    // 주석 처리 메소드
    private boolean exceptComment() {
        char c;
        // 커서로부터 whitespace 문자들 모두 무시
        while(!isEOF(cursor) && Character.isWhitespace(src.charAt(cursor))){
        	cursor++;
        }
        if(isEOF(cursor)) {
        	return false; // 공백을 무시하고 EOF면 주석 제거 성공하면 false 반환
        }

        if (src.charAt(cursor) == '/') { // '/'가 나올 경우
            if(src.charAt(cursor+1) == '?') { // /? 는 블록 주석
            	cursor += 2; // "/?" 다음 문자로 커서 이동
                while(src.charAt(cursor) != '?' && src.charAt(cursor+1) != '/') { // ?/가 나올때 까지 반복
                    if(isEOF(cursor+1)) return true; // 찾지 못하면 실패, true 반환
                    cursor++;
                }
                cursor += 2; // "?/" 다음 문자로 커서 이동
            }
        }
        return false; // 성공적으로 제거하면 false 반환
    }
    
    // 문자가 1글자 연산자인지 확인하는 메소드
    private boolean isSingleSpecialToken(char c) {
        switch (c) {
            case '(': case ')': case ',': case ';': case '[': case ']': case '{': case '}': 
            case '+': case '-': case '*': case '/': case '%': case EOF:
                return true;
            default:
                return false;
        }
    }
    
    // 문자가 1글자 연산자가 아닌 특수문자인지 확인하는 메소드
    private boolean isSpecialChar(char c) {
        for (int i = 0; i < SPECIAL_CHARS.length(); ++i)
            if (SPECIAL_CHARS.charAt(i) == c)
                return true;
        return false;
    }
    
    private Token.SymType getSymbolType(State s) {
        switch (s) {
            case Dec: // 10, 8, 16, 0은 Digit 반환
            case Oct:
            case Hex:
            case Zero:
                return Token.SymType.Digit;
            case Real: // 실수인 경우 Real 반환
            	return Token.SymType.Real;
            case Const: // 상수인 경우 Const 반환
            	return Token.SymType.Const;
            case tidOrReserved: // 명칭이나 예약어인 경우 tidOrReserved반환
                return Token.SymType.tidOrReserved;
            case Op: // 연산자인 경우 OP반환
            case SingleOperator:
                return Token.SymType.Op;
            case Initial: // 종결상태가 아닌경우에는 실패이므로 NULL을 반환
            case PreHex:
            default:
                return Token.SymType.NULL;
        }
    }
    
    public Token getToken() {
    	Token token = new Token();
        Token.SymType symType = Token.SymType.NULL; // Symbol Type을 NULL로 설정
        String tokenString = "";

        State state = State.Initial;

        // 현재 커서로부터 Comment 제거
        if (exceptComment()) {
            // Comment를 지우는 도중에 ERROR가 발생했을 경우
            System.err.print(Error.getErrorMessage(Error.ErrorCode.InvalidComment));
            return token;
        }
        
        while(!isEOF(cursor)) {
        	char c = src.charAt(cursor++);
        	
        	if(Character.isWhitespace(c)) {
        		if(state != State.Initial) break;
        		else continue;
        	}else if(state == State.Initial && c == '0') {
        		state = State.Zero;
        	}else if(Character.isDigit(c)) { //숫자를 만났을때 이전의 상태에 따라 현재상태 정의
        		if(state == State.Initial) {
        			state = State.Dec;
        		}else if(state == State.Zero) {
        			state = State.Oct;
        		}else if(state == State.PreHex) {
        			state = State.Hex;
        		}else if(state == State.PreReal) {
        			state = State.Real;
        		}else if(state == State.PreConst) {
        			state = State.Const;
        		}else if(state == State.Op) { // 연산자가 나온 다음 숫자면 break
        			--cursor;
        			break;
        		}
        	}else if(state == State.Dec && c == '.') { // '숫자.' 을 인식한 경우
        		state = State.PreReal;
        	}else if(state == State.Zero && c == '.') { // '0.' 을 인식한 경우
        		state = State.PreReal;
        	}else if(state == State.Zero && c == 'x') { // '0x' 를 인식한 경우
        		state = State.PreHex;
        	}else if(state == State.Dec && c == 'e') { // '숫자e' 를 인식한 경우
        		state = State.PreReal;
        	}else if(state == State.PreReal && c == '+') { // 'e+' 를 인식한 경우
        		state = State.Real;
        	}else if(state == State.Initial && c == '\'') { // '''를 인식한 경우
        		state = State.PreConst;
        	}else if(state == State.Const && c == '\'') {
        		state = State.Const;
        	}else if(state == State.PreConst) { // 상수
        		state = State.Const;
        	}else if(isSingleSpecialToken(c)) { // '(', ')', '{', '}', ',', '[', ']', ';', EOF를 인식한 경우
        		if(state == State.Initial) {
        			state = State.SingleOperator;
        			tokenString = String.valueOf(c);
        		}else --cursor;
        		break;
        	}else if(isSpecialChar(c)) { // 문자 두개가 연산자가 될 수 있는 경우
        		if(state != State.Initial && state != State.Op) {
        			--cursor;
        			break;
        		}
        		state = State.Op;
        	}else if(Character.isAlphabetic(c)) { //알파벳을 인식한 경우
        		if(state != State.Initial && state != State.tidOrReserved) { // ID나 예약어가 아니면 break
        			--cursor;
        			break;
        		}
        		state = State.tidOrReserved;
        	}
        	tokenString += String.valueOf(c); // 글자 String으로 추가
        }
        symType = getSymbolType(state);
        if(symType == Token.SymType.tidOrReserved && tokenString.length() > ID_LENGTH) {
        	// ID의 길이가 16을 넘으면 에러처리
        	System.err.print(Error.getErrorMessage(Error.ErrorCode.AboveIDLimit));
            return token;
        }
        token.setSymbol(tokenString, symType);
    	return token;
    }
}
