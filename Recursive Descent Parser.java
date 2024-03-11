import java.util.Scanner;
public class Main {
    static String text = ""; // 저장할 텍스트
    static int num = 0; // 현재 위치
    static int ERR_CHECK = 0; // 에러 체크
    
    public static void pS(){
        // S -> aA | bB
        if(num < text.length() &&text.charAt(num) == 'a'){ // a와 매칭 -> aA
                pa(); // aA의 a와 매칭
                pA(); // aA의 A와 매칭
        }else if(num < text.length() &&text.charAt(num) == 'b'){ // b와 매칭 -> bB
                pb(); // bB의 b와 매칭
                pB(); // bB의 B와 매칭
        }else{
            ERR_CHECK++;// ERR
        }
    }
    public static void pA(){

        //A -> aBb | bBb | cBb
        if(num < text.length() &&text.charAt(num) == 'a'){ // a매칭  -> aBb
            pa(); // aBb의 a와 매칭
            pB(); // aBb의 B와 매칭
            pb(); // aBb의 b와 매칭 
        }else if(num < text.length() &&text.charAt(num) == 'b'){ // b 매칭 -> bBb
            pb(); // bBb의 b와 매칭
            pB(); // bBb의 B와 매칭
            pb(); // bBb의 b와 매칭
        }else if(num < text.length() &&text.charAt(num) == 'c'){ // c 매칭 -> cBb
            pc(); //cBb의 c와 매칭
            pB(); //cBb의 B와 매칭
            pb(); //cBb의 b와 매칭
        }else{
            ERR_CHECK++;// ERR
        }

    }


    public static void pB(){
        //B -> d|e|f
        if(num < text.length() &&text.charAt(num) == 'd'){ // d 매칭
            num++; // Next Token
        }else if(num < text.length() &&text.charAt(num) == 'e'){ // e 매칭
            num++;// Next Token
        }else if(num < text.length() &&text.charAt(num) == 'f'){ // f 매칭
            num++;// Next Token
        }else{ // d | e | f 와 매칭이 안될 경우
            ERR_CHECK++;// ERR
        }


    }
    public static void pa(){

        if(num < text.length() &&text.charAt(num) == 'a'){ //a와 매칭
                num++;// Next Token 
        }else{ //a와 매칭이 안될 경우
            ERR_CHECK++;// ERR
        }


    }
    public static void pb(){
        if(num < text.length() &&text.charAt(num) == 'b'){ // b와 매칭
            num++;// Next Token
        }else{ // b와 매칭이 안될 경우
            ERR_CHECK++;// ERR
        }
    }
    public static void pc(){
        if(num < text.length() &&text.charAt(num) == 'c'){ // c와 매칭
            num++;// Next Token
        }else{ // c와 매칭이 안될 경우
            ERR_CHECK++;// ERR
        }
    }


    public static void main(String[] args) {


        Scanner input = new Scanner(System.in);
        //  while(true) {
            text = input.next();
            //  num = 0;
            //  ERR_CHECK = 0;
            //  if(text.equals( "break")) break;
        
            pS(); // 가장 먼저 start을 호출
            if (ERR_CHECK == 0 && text.length() == num) { // 만약에 Error가 없고, text길이 전부를 체크 하였다면 OK
                System.out.println("OK");
            } else { // 길이가 짧거나 에러가 있는 경우
                System.out.println("FAIL");
            }
            
       // }
    }
}