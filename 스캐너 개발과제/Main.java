package CScanner;

public class Main {
    public static void main(String[] args) {       
        CScanner sc = new CScanner("D:\\SampleCode02.txt"); // 스캐너 객체
        Token tok = null; // 토큰을 저장하기 위한 변수
        while ((tok = sc.getToken()).getSymbolOrdinal() != -1) // 스캐너가 끝날때까지 토큰을 출력
            System.out.println(tok);
    }
}
