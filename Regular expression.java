import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import java.util.regex.Pattern;
public class Main {


    public  static boolean phone_number_check(String text){
        return Pattern.matches("^[0-9]{3}-[0-9]{4}-[0-9]{4}$", text);
        //XXX-XXXX-XXXX니깐, 3번 반복, 4번 반복, 4번 반복을 체크를 해줍니다. 이때 범위는 0-9범위를 체크 해줍니다.
    }
    public  static boolean email_check(String text){
        return Pattern.matches("^([a-zA-Z0-9]|_)+@[a-zA-Z]+(\\.[a-zA-Z]+)+", text);
        //test123@test.com형식 입니다. 이때 .com.com이 가능하니 .a-zA-Z가 한개 이상되도록 합니다. 이때 (.com)이 한쌍이 되도록 합니다.
    }
    public  static boolean phone_type_check(String text){
        return Pattern.matches("^(iPhone|Galaxy)\\s+[0-9]{1,3}+", text);
        //iPhone 123 , Galaxy 123를 체크 합니다. 즉 기종 숫자를 체크합니다. 반복범위는 3까지만
    }

    public  static boolean File_type_check(String text){
        return Pattern.matches("^([a-zA-Z0-9]|_)+(\\.(c|java|py|ml))", text);
        //파일을 체크합니다. 이때 파일명하고 .c or java or py or ml을 체크합니다.
    }

    public static void main(String[] args) throws IOException {
        String read; // 문자열을 읽기 위해 선언 합니다.
        BufferedReader reader = new BufferedReader( new FileReader("./input.txt")); // input file read


                while ((read = reader.readLine()) != null) {
                String[] text = read.split("[\n]+"); // \n기준으로 문자열을 잘라 줍니다.
                for(String current : text) { // 한줄씩 읽음습니다.
                    if(phone_number_check(current)) System.out.println("Match : The input string is ["+current+"] Matched Type is [Phone Number]");
                    else if(email_check(current))System.out.println("Match : The input string is ["+current+"] Matched Type is [e-mail]");
                    else if(phone_type_check(current))System.out.println("Match : The input string is ["+current+"] Matched Type is [Phone Type]");
                    else if(File_type_check(current))System.out.println("Match : The input string is ["+current+"] Matched Type is [Source File]");
                    else System.out.println("Error : The input does not belong to the given string type ["+current+"]");

                }
            }
    }
}