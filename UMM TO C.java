import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static  int value_count(String s){ // value 값을 세는 함수
        int num = 0; // 기본 0
        String[] s_ = s.split(" "); // 공백 기준으로 짜릅니다.
        /*
        ex) ... ... 이 들어오면
          ... / ... 이렇게 짤리게 됩니다.
          그래서 만약에 공백으로 짤렷다면 " "이 포함 즉 곱셈이 포함이 되니, 다 곱해 줍니다..
         */
            Queue<Integer> q = new LinkedList<Integer>(); // .과 ,을 차례대로 쌓기 위해서 Queue를 선언합니다.
            for (String x : s_) { // 공백기준으로 짜른 문자열을 반복하여 읽습니다.
               // System.out.println(x);
                if(x.contains("어")) continue; //어가 들어오면 제외
                int count = 0; // .와 , 개수 세기 위해서 선언 합니다.
                for (int i = 0; i < x.length(); i++) { // 그러면 짤리게 된 문자열을 반복 합니다.
                    switch (x.charAt(i)) { // 한 글자식 읽어 .이면 +1, ,이면 -1을 시켜 줍니다.
                        case '.':
                            ++count;
                            break;
                        case ',':
                            --count;
                            break;
                    }
                }
                q.add(count); // 하나 하나씩 Queue에 쌓습니다.
            }
            num = q.peek(); // queue의 가장 첫번째 원소를 가져 옵니다.
            q.poll(); // 첫번째 요소를 읽었으니 POP 시켜 줍니다.
            for (int i : q) num *= i; // queue에 저장된 원소를 다 곱해 줍니다. 이때 Queue에 쌓이게 된 원소들은 잘려진 문자열을 기준으로 계산이 된 원소 입니다.

        return num;
    }


    public static void main(String[] args) throws IOException {

        String read; // 문자열을 읽기 위해 선언 합니다.
        BufferedReader reader = new BufferedReader( new FileReader("./test.umm")); // umm 파일 read하기 위함 입니다.
        BufferedOutputStream out_file = new BufferedOutputStream(new FileOutputStream("./test.c")); // 파일 출력하기 위함 입니다.

        int[] _input = new int[500]; // 변수 500개를 사용 할 수 있게, 배열을 선언 해줍니다. 그래서 만약에 접근한 배열이 0 이면 한번도 선언이 되지 않은 것이고, 1이면 선언이 한번이 되었던 적이 있던 변수 입니다.

        try {
            // 기본적인 셋팅----------------------
            out_file.write("#include <stdio.h>\r\n".getBytes());
            out_file.write("int main() {\r\n".getBytes());
            //----------------------------------
            while ((read = reader.readLine()) != null) { // 전체 문서를 읽습니다.
                String current_string; // 현재 위치를 체크하기 위해서 선언 해줍니다.
                String[] s = read.split("[\n]+"); // \n기준으로 문자열을 잘라 줍니다.
                    for(String c : s){ // 한줄씩 읽음습니다.
                        if(c.equals("어떻게") || c.equals("이 사람이름이냐ㅋㅋ")) continue; // 이 부분은 제외 합니다.
                        if(c.startsWith("엄") ){ // 만약에 시작이 엄으로 시작을 하는지 체크를 해줍니다.
                            if(c.contains("어")) { // 엄어 엄어.. 엄어.. 어.. 같은 경우
                                String start = c.substring(c.indexOf("엄") + 1); // 엄 뒤 부터 시작합니다.
                                int u_count = 0; // 어를 count를 세기 위해서 선언 해줍니다.
                                for(int i = 0; i < start.length(); i++){ // 엄 이후에 "어"를 만나지 않을 때 까지 반복을 해줍니다.
                                    if(start.charAt(i) == '어') u_count++; // 어가 이어질 경우 변수가 늘어나는 거니, 어의 개수를 세어줍니다..
                                    else break;  // 이 경우는 뒤에 어가 안오고 ... 이 오는 경우는 멈춰줍니다..
                                }
                                String current = c.substring(c.indexOf("엄") + 1) ;// 기본으로 현재 위치에서 +1를 해준걸 할당 해줍니다.
                                if(u_count == 0) {
                                    u_count = 1; // 이때는 엄... 어.. 인 경우 입니다. 그래서 기본 셋팅을 1로 해줍니다.
                                }
                                else{
                                    current = c.substring(c.indexOf("엄") + u_count+ 1) ; // 만약에 어가 있었다면 어 개수 만큼 더해줍니다.
                                }

                                if(_input[u_count] == 0){ // 어 개수를 세어 변수가 선언이 되었던 적이 있는지 체크를 해줍니다.
                                    out_file.write(("int _input"+u_count+";\r\n").getBytes()); // 문자열 출력
                                    _input[u_count] = 1;
                                }


                                int len = current.length(); // 길이 변환을 시켜 줍니다.
                                if(len == 1){ // 길이가 1이라면 [ . , ]
                                    if(current.equals(".")){ // ++
                                        out_file.write(("_input"+u_count+"++;\r\n").getBytes());
                                    }else if(current.equals(",")){ // --
                                        out_file.write(("_input"+u_count+"--;\r\n").getBytes());
                                    }
                                }else{ // 엄... 어.. 엄어어.... 어어... 엄 ... 같은 경우 입니다.
                                    if(current.contains("어")){ // 만약에 엄어... 어.. 일 경우입니다.
                                        int u_len = current.replaceAll("[ |.|,]", "").length(); // .와 ,와 " "을 제외한 어 개수만 세기 위해서 선언 해줍니다.
                                        if(_input[u_len] == 0){ // 선언이 되어있지 않았다면 선언을 해줍니다.
                                            out_file.write(("int _input"+u_len+";\r\n").getBytes()); // 문자열 출력
                                            _input[u_len] = 1;
                                        }
                                        String After = current.substring(current.indexOf("어") + 1); // 어 이후를 보기 위함
                                        String what = current.substring(current.indexOf("어") - 1, current.lastIndexOf("어")); // 어바로 이전을 보기 위해서


                                        if (current.charAt(0) == ' ') { // 만약에 엄 ...인지 체크를 하기 위해서 입니다. 만약에 그러면 input *= 형태로 나타 냅니다.
                                            current = current.replaceAll("[ ]", "");
                                            out_file.write(("_input" + u_count + " *= " + value_count(current) + ";\r\n").getBytes());
                                        } else { // 그냥 엄.... 일 경우는 이 조건에 만족이 됩니다.
                                            int result = value_count(current);

                                            if(current.contains(" ")){
                                                out_file.write(("_input" + u_count + " = _input" + u_count + " * " + value_count(current) + ";\r\n").getBytes());
                                            }else {
                                                if (result < 0) {
                                                    out_file.write(("_input" + u_count + " = _input" + u_count + " " + value_count(current) + ";\r\n").getBytes());
                                                } else {
                                                    out_file.write(("_input" + u_count + " = _input" + u_count + " + " + value_count(current) + ";\r\n").getBytes());
                                                }
                                            }
                                        }

                                        if (After.charAt(0) == ' ') {
                                            After = After.replaceAll("[ ]", "");
                                            out_file.write(("_input" + u_count + " *= " + value_count(After) + ";\r\n").getBytes());
                                        } else { // 그냥 엄.... 일 경우는 이 조건에 만족이 됩니다.
                                            int result = value_count(After);

                                            if(After.contains(" ")){
                                                out_file.write(("_input" + u_count + " = _input" + u_count + " * " + value_count(After) + ";\r\n").getBytes());
                                            }else {
                                                if (result < 0) {
                                                    out_file.write(("_input" + u_count + " = _input" + u_count + " " + value_count(After) + ";\r\n").getBytes());
                                                } else {
                                                    out_file.write(("_input" + u_count + " = _input" + u_count + " + " + value_count(After) + ";\r\n").getBytes());
                                                }
                                            }
                                        }

                                        switch (what.charAt(0)){
                                            case '.':
                                                out_file.write(("_input" + u_count + " += _input" + u_len + ";\r\n").getBytes());
                                                break;
                                            case ',':
                                                out_file.write(("_input" + u_count + " -= _input" + u_len + ";\r\n").getBytes());
                                                break;
                                            case ' ':
                                                out_file.write(("_input" + u_count + " *= _input" + u_len + ";\r\n").getBytes());
                                                break;
                                        }

                                    }else {
                                        if (current.charAt(0) == ' ') { // 만약에 엄 ...인지 체크를 하기 위해서 입니다. 만약에 그러면 input *= 형태로 나타 냅니다.
                                            current = current.replaceAll("[ ]", "");
                                            out_file.write(("_input" + u_count + " *= " + value_count(current) + ";\r\n").getBytes());
                                        } else { // 그냥 엄.... 일 경우는 이 조건에 만족이 됩니다.

                                            int result = value_count(current);

                                            if(current.contains(" ")){
                                                out_file.write(("_input" + u_count + " = _input" + u_count + " * " + result + ";\r\n").getBytes());
                                            }else {
                                                out_file.write(("_input" + u_count + " = " + result + ";\r\n").getBytes());
                                            }
                                        }
                                    }
                                }
                            }
                          else if(c.endsWith("식?")){ // 엄식?을 체크하기 위합니다.
                              if(_input[1] == 0){ // input1이라는 변수가 선언이 되지 않았다면 선언을 합니다.
                                  out_file.write(("int _input1;\r\n").getBytes()); // 문자열 출력
                                  _input[1] = 1;
                              }
                              out_file.write(("printf(\"input1 : \");\r\n").getBytes()); // 문자열 출력
                              out_file.write(("scanf(\"%d\", &_input1);\r\n").getBytes()); // 문자열 출력
                          }else{ // 엄.... 엄.... ... 엄.. ,,인 경우를 체크하기 위함입니다.
                              if(_input[1] == 0){ // input1이라는 변수가 선언이 되지 않았다면 선언
                                  out_file.write(("int _input1;\r\n").getBytes()); // 문자열 출력
                                  _input[1] = 1;
                              }
                              current_string = c.substring(c.indexOf("엄") + 1);
                              out_file.write(("_input1 = "+value_count(current_string)+";\r\n").getBytes());
                          }
                        }else if(c.startsWith("식") && c.endsWith("!")) { // 정수 출력  식..! 식어!를 체크하기 위함입니다.
                            if(c.contains("어")) { // 식어! 식어어!인 경우

                                String text = c.substring(c.indexOf("식") + 1, c.lastIndexOf("!")); // Substring을 해줍니다 기준은 식에서 +1, 마지막은 !인 경우


                                if(_input[text.length()] == 0){ // 만약에 해당된 변수가 선언이 안되어있으면 선언을 해줍니다.
                                    out_file.write(("int _input"+text.length()+";\r\n").getBytes()); // 문자열 출력
                                    _input[text.length()] = 1;
                                }

                                out_file.write(("printf(\"%d\", _input"+text.length()+");\r\n").getBytes());

                            }else{ // 식...! , 식,,,!인 경우 입니다.
                                current_string = c.substring(c.indexOf("식") + 1, c.lastIndexOf("!"));
                                out_file.write(("printf(\"%d\", "+value_count(current_string)+");\r\n").getBytes());
                            }
                        }else if(c.startsWith("식") && c.endsWith("ㅋ")) { // 문자 출력을 하기 위함입니다.  식... ...ㅋ 석어ㅋ
                            if(c.contains("어")) { // 식어ㅋ 식어어ㅋ인 경우입니다.
                                String text = c.substring(c.indexOf("식") + 1, c.lastIndexOf("ㅋ"));

                                if(_input[text.length()] == 0){ // 만약에 해당된 변수가 선언이 안되어있으면 선언
                                    out_file.write(("int _input"+text.length()+";\r\n").getBytes()); // 문자열 출력
                                    _input[text.length()] = 1;
                                }
                                out_file.write(("printf(\"%c\", _input"+text.length()+");\r\n").getBytes());
                            }else{ // 식...ㅋ , 식,,,ㅋ인 경우를 출력 합니다.
                                current_string = c.substring(c.indexOf("식") + 1, c.lastIndexOf("ㅋ"));
                                out_file.write(("printf(\"%c\", "+value_count(current_string)+");\r\n").getBytes());
                            }
                        }else if(c.startsWith("어")) { // 어로 시작 할 경우 어엄 어어엄 어엄... 어엄.. .. ..을 체크하기 위함입니다.
                            if(c.contains("엄") && !c.endsWith("식?")){ // 어엄 어어엄 어어어어엄

                                int length = c.trim().replaceAll("[.|,| |엄]", "").length() + 1; // 어 개수를 세줍니다.
                                // +1을 해준 이유는 어엄 자체가 input2이기 때문입니다.
                                if(_input[length] == 0){ // 만약에 해당된 변수가 선언이 안되어있으면 선언을 해줍니다.
                                    out_file.write(("int _input"+length+";\r\n").getBytes()); // 문자열 출력
                                    _input[length] = 1;
                                }

                                current_string = c.substring(c.indexOf("엄") + 1); // 어어어엄 이 있으면 엄 이후 시작합니다.

                                int len = current_string.length(); // 길이 변환를 변환 합니다.
                                if(len == 1){ // 길이가 1이라면 [ . , ] 어엄.. 어엄... 같은 경우.
                                    if(current_string.equals(".")){
                                        out_file.write(("_input"+length+"= 1;\r\n").getBytes());
                                    }else if(current_string.equals(",")){
                                        out_file.write(("_input"+length+"= -1;\r\n").getBytes());
                                    }
                                }else{
                                        out_file.write(("_input" + length + " = " + value_count(current_string) + ";\r\n").getBytes());
                                }
                            }else if(c.endsWith("식?")){ // 어엄식? 어어엄식?을 체크하기 위함입니다.


                                    String text = c.substring(c.indexOf("어"), c.lastIndexOf("식?")); 
                                    text = text.replaceAll("엄", ""); // 엄을 삭제하여 어 개수 확인을 합니다.

                                    int len = text.length()+ 1; // 어엄자체가 input2이니 +1를 시켜줍니다
                                if(_input[len] == 0){ // 만약에 해당된 변수가 선언이 안되어있으면 선언을 해줍니다.
                                    out_file.write(("int _input"+len+";\r\n").getBytes()); // 문자열 출력
                                    _input[len] = 1;
                                }

                                out_file.write(("printf(\"input"+len+" : \");\r\n").getBytes()); // 문자열 출력
                                out_file.write(("scanf(\"%d\", &_input"+len+");\r\n").getBytes()); // 문자열 출력
                            }
                            else if(c.endsWith("어")) { // 어어 어어어 어어어어 같은 경우입니다.
                                int len = c.length(); // 어어어 어어 어어어이니 길이는 전체 문자열 길이.
                                if(_input[len] == 0){ // 만약에 해당된 변수가 선언이 안되어있으면 선언을 해줍니다.
                                    out_file.write(("int _input"+len+";\r\n").getBytes()); // 문자열 출력
                                    _input[len] = 1;
                                }
                                out_file.write(("_input" + len +";\r\n").getBytes());
                            }
                        }
                    }
            }
        }catch (Exception e) {
         } finally {
            out_file.write("\treturn 0;\r\n".getBytes()); // 마무리로 리턴을 추가해줍니다
            out_file.write("}\r\n".getBytes()); // 닫아줍니다
            out_file.close(); // 파일을 닫아줍니다
            reader.close(); // 닫아줍니다.
         }
    }
}