package CScanner;
import java.util.Stack;
import java.lang.String;

public class Parser {
	
	public void InitiateParser(){
		Stack<Integer> stateStack = new Stack(); //var, begin, end
		Stack<Integer> flowStack = new Stack(); //상태
		Stack<String> calStack = new Stack(); //연산 변수용 스택
		Stack<String> calculStack = new Stack();
		Stack<Float> IvarStack = new Stack();
		Stack<Float> FvarStack = new Stack();
		
		Object[][] intArray = new Object[5][2]; //int 변수용 테이블 어레이
		Object[][] floatArray = new Object[5][2]; //float 변수용 테이블 어레이
		
		String numberString = ""; //토큰번호 문자
		String valueString = ""; //값 문자
		String TOK = ""; //tok.toString()
		String tokenName = ""; //토큰 명 변수
		int code = 0; //토큰번호 숫자
		float value = 0; //값 숫자
		int NULLNUM = 777;
		float tempVALUE = 0; //계산시 사용할 임시저장 변수
		
		//변수 저장용 어레이 초기화
		for(int i = 0; i < intArray.length; i++) {
			for(int j = 0; j < 2; j++) {
				intArray[i][j] = "NONE";
			}
		}
		for(int i = 0; i < floatArray.length; i++) {
			for(int j = 0; j < 2; j++) {
				floatArray[i][j] = "NONE";
			}
		}

		CScanner sc = new CScanner("D:\\ProgramTest01.txt"); // 스캐너 객체
	    Token tok = null; // 토큰을 저장하기 위한 변수
	    while ((tok = sc.getToken()).getSymbolOrdinal() != -1) { // 스캐너가 끝날때까지 토큰을 출력
//	    	System.out.println(tok);
	    	TOK = tok.toString();
	    	
	    	//토큰 명
	    	tokenName = TOK.substring(0, TOK.indexOf("@:"));
	    	
	    	//토큰 넘버
	    	numberString = TOK.substring(TOK.indexOf("@(") + 2, TOK.indexOf("@ "));	    	
	    	code = Integer.parseInt(numberString);
	    	
	    	//토큰의 값
	    	valueString = TOK.substring(TOK.indexOf("@ ") + 2, TOK.indexOf("@)"));	    	
	    	if(valueString.matches("[a-zA-Z]")){
	    	}else {
	    		value = Float.parseFloat(valueString);
	    	}
	    	
	    	//var, begin, end 상태 저장
	    	if(code == 0) {
	    		//System.out.println("state 0");
	    		stateStack.push(code);
	    	}else if(code == 1) {
	    		for(int i = 0; i < stateStack.size(); i++) {
	    			stateStack.pop();
	    		}
	    		//System.out.println("state 1");
	    		stateStack.push(code);
	    	}else if(code == 2) {
	    		for(int i = 0; i < stateStack.size(); i++) {
	    			stateStack.pop();
	    		}
	    		//System.out.println("state 2");
	    		stateStack.push(code);
	    	}
	    	
	    	//44면 INT, 45면 FLOAT, 흐름스택에 저장
	    	if(code == 44 && stateStack.peek() == 0) {
	    		for(int i = 0; i < flowStack.size(); i++) {
	    			flowStack.pop();
	    		}
	    		flowStack.push(44);
	    	}else if(code == 45 && stateStack.peek() == 0) {
	    		for(int i = 0; i < flowStack.size(); i++) {
	    			flowStack.pop();
	    		}
	    		flowStack.push(45);
	    	}else if(code == 37 && stateStack.peek() == 0) { //37 세미콜론을 만나면 흐름스택 비우기
	    		for(int i = 0; i < flowStack.size(); i++) {
	    			flowStack.pop();
	    		}
	    		flowStack.push(NULLNUM);
	    	}
	    	
	    	int count = 0;//변수 저장 어레이에 중복된 값이 있는지 확인하는 변수
	    	
	    	//Int 변수명 저장
	    	if(code == 3 && stateStack.peek() == 0 && flowStack.peek() == 44) {
	    		for(int i = 0; i < intArray.length; i++) {
	    			if (intArray[i][0] == valueString) {
	    				count = 1;
	    				break;
	    			}
	    		}
	    		//중복된 값이 어레이에 없으면 변수명 저장
	    		if (count != 1){
	    			for(int j = 0; j < intArray.length; j++) {
	    				if(intArray[j][0] == "NONE") {
	    					intArray[j][0] = valueString;
	    					count = 0;
	    					break;
	    				}
	    			}
	    		}else {
	    			System.out.println("duplicate ERROR");
	    		}
	    	}
	    	
	    	//Float 변수명 저장
	    	if(code == 3 && stateStack.peek() == 0 && flowStack.peek() == 45) {
	    		for(int i = 0; i < floatArray.length; i++) {
	    			if (floatArray[i][0] == valueString) {
	    				count = 1;
	    				break;
	    			}
	    		}
	    		//중복된 값이 어레이에 없으면 변수명 저장
	    		if (count != 1){
	    			for(int j = 0; j < floatArray.length; j++) {
	    				if(floatArray[j][0] == "NONE") {
	    					floatArray[j][0] = valueString;
	    					count = 0;
	    					break;
	    				}
	    			}
	    		}else {
	    			System.out.println("duplicate ERROR");
	    		}
	    	}
	    	
	    	//begin 파트
	    	
	    	// 변수명 에 값을 넣을 경우
	    	if(code == 3 && stateStack.peek() == 1 && flowStack.peek() == NULLNUM) {	    		
	    		calStack.push(valueString);
	    		flowStack.push(code);
	    	}
	    	// 변수명에 값을 넣는데, =를 만난경우
	    	if(code == 15 && stateStack.peek() == 1 && flowStack.peek() == 3) {
	    		flowStack.push(code);
	    	}
	    	// =인 상태에서 값을 만난경우
	    	if(code == 5 && stateStack.peek() == 1 && flowStack.peek() == 15) {
	    		IvarStack.push(value);
	    	}
	    	if(code == 3 && stateStack.peek() == 1 && flowStack.peek() == 15) {
	    		calStack.push(valueString);
	    	}
	    	if(code == 10 && stateStack.peek() == 1 && flowStack.peek() == 15) {
	    		flowStack.push(code);
	    		calculStack.push(tokenName);
	    	}
	    	if(code == 3 && stateStack.peek() == 1 && flowStack.peek() == 10) {
	    		calStack.push(valueString);
	    	}
	    	if(code == 12 && stateStack.peek() == 1 && flowStack.peek() == 10) {
	    		flowStack.push(code);
	    		calculStack.push(tokenName);
	    	}
	    	if(code == 3 && stateStack.peek() == 1 && flowStack.peek() == 12) {
	    		float a = 0;
	    		float b = 0;
	    		
	    		calStack.push(valueString);
	    		
	    		String tempA = calStack.pop();
	    		for(int i = 0; i < intArray.length; i++) {
	    			if(tempA.equals(intArray[i][0])) {
	    				a = (Float) intArray[i][1];
	    				break;
	    			}
	    		}
	    		String tempB = calStack.pop();
	    		for(int i = 0; i < intArray.length; i++) {
	    			if(tempB.equals(intArray[i][0])) {
	    				b = (Float) intArray[i][1];			
	    				break;
	    			}
	    		}
	    		//곱하기 연산
	    		tempVALUE = a * b;
	    		
	    		calculStack.pop();
	    		flowStack.pop();
	    	}
	    	
	    	if(code == 37 && stateStack.peek() == 1 && flowStack.peek() == 10){
	    		float a = 0;
	    		float b = 0;
	    		
	    		String tempA = calStack.pop();
	    		for(int i = 0; i < intArray.length; i++) {
	    			if(tempA.equals(intArray[i][0])) {
//	    				System.out.println("FOUND");
	    				a = (Float) intArray[i][1];
	    				
	    				break;
	    			}
	    		}
	    		
	    		tempVALUE = a + tempVALUE;

	    		String cal = calStack.pop();
	    		
	    		for(int i = 0; i < floatArray.length; i++) {
	    			//floatVar에 floatArray에 저장된 변수명을 넣고 비교
	    			String floatVar = (String) floatArray[i][0];
	    			if (cal.equals(floatVar)) {
	    				float vf = tempVALUE;
	    				floatArray[i][1] = vf;
	    				break;
	    			}
	    		}
	    		
	    		//flowStack, calStack, calculStack 청소
	    		for(int i = 0; i < flowStack.size(); i++) {
	    			flowStack.pop();
	    		}
	    		for(int i = 0; i < calStack.size(); i++) {
	    			calStack.pop();
	    		}
	    		for(int i = 0; i < calculStack.size(); i++) {
	    			calculStack.pop();
	    		}

	    		calStack.push("NULL");
	    	}
	    	
	    	
	    	// 세미콜론이 flowStack = 과 만났을때
	    	if(code == 37 && stateStack.peek() == 1 && flowStack.peek() == 15) {
	    		String cal = calStack.pop();
	    		
	    		for(int i = 0; i < intArray.length; i++) {
	    			//intVar에 intArray에 저장된 변수명을 넣고 비교
	    			String intVar = (String) intArray[i][0];
	    			if (cal.equals(intVar)) {
	    				float v = IvarStack.pop();
	    				intArray[i][1] = v;
	    				break;
	    			}
	    		}
	    		for(int i = 0; i < floatArray.length; i++) {
	    			//floatVar에 floatArray에 저장된 변수명을 넣고 비교
	    			String floatVar = (String) floatArray[i][0];
	    			if (cal.equals(floatVar)) {
	    				float vf = FvarStack.pop();
	    				floatArray[i][1] = vf;
	    				break;
	    			}
	    		}

	    		//flowStack, calStack 청소
	    		for(int i = 0; i < flowStack.size(); i++) {
	    			flowStack.pop();
	    		}
	    		for(int i = 0; i < calStack.size(); i++) {
	    			calStack.pop();
	    		}

	    		calStack.push("NULL");
	    	}
	    	
	    	if(code == 7 && stateStack.peek() == 1) {
	    		flowStack.push(code);
	    	}
	    	if(code == 3 && stateStack.peek() == 1 && flowStack.peek() == 7) {    		
	    		for(int i = 0; i < intArray.length; i++) {
	    			//intVar에 intArray에 저장된 변수명을 넣고 비교
	    			String intVar = (String) intArray[i][0];
	    			if (valueString.equals(intVar)) {
	    				System.out.println("PRINT RESULT : " + intArray[i][1]);
	    				break;
	    			}
	    		}
	    		for(int i = 0; i < floatArray.length; i++) {
	    			//floatVar에 floatArray에 저장된 변수명을 넣고 비교
	    			String floatVar = (String) floatArray[i][0];
	    			if (valueString.equals(floatVar)) {
	    				System.out.println("PRINT RESULT : " + floatArray[i][1]);
	    				break;
	    			}
	    		}
	    	}
	    }
	    //테이블 출력
	    System.out.println("\n[INT   TABLE]\n");
    	for (Object[] row : intArray) { //변수 테이블 확인용 출력문
    		for (Object element : row) {
    			System.out.print(element + "\t");
    		}
    		System.out.println();
    	}
    	System.out.println("\n[FLOAT TABLE]\n");
    	for (Object[] row : floatArray) { //변수 테이블 확인용 출력문
            for (Object element : row) {
                System.out.print(element + "\t");
            }
            System.out.println();
        }
	}
}
