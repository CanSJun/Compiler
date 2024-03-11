
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.io.*;

public class tinyPythonPrintLister  extends tinyPythonBaseListener{

    public class Symbol { // Symbol을 저장하고 관리 해야 함
        String data;
        int num;
        public Symbol(String data, int num) { // KEY , VALUE 저장
            this.data = data;
            this.num = num;
        }
        public int getIndex() { // 현재 내가 저장된 순서를 불러옥 ㅣ위함
            return num;
        }
    }
    static ParseTreeProperty<String> TREE = new ParseTreeProperty<String>();
    static Stack<HashMap<String, Symbol>> symbol_bags = new Stack<>(); // ARRAY LIST로 하니 너무 느리고 제약이 있어 HASH MAP 사용
    int label_number = 0; // 현재 label 넘버
    int loop_number = 0; // loop number;
    private String I_number = ""; // DEF I 개수 세주기 위해서
    public tinyPythonPrintLister() {
        symbol_bags.push(new HashMap<>());
        symbol_bags.peek().put("args", new Symbol("args", 0));
    }

 //  static int current_tab = 0;
    @Override public void exitFile_input(tinyPythonParser.File_inputContext ctx) {
        /*

        file_input:
	    defs
        (NEWLINE | stmt)* EOF
        ;
        */
        String init =".method public static main([Ljava/lang/String;)V\n" +
                "    .limit stack 32\n" +
                "    .limit locals 32\n";
        String str = "";
        for(int i = 0; i <ctx.stmt().size(); i++){ //  def (new|stmt) 구조
            if(null != ctx.stmt(i)){ str += TREE.get(ctx.stmt(i));}
            else if(null != ctx.NEWLINE(i)){ str += ctx.NEWLINE(i).getText(); }

        }
        String total = TREE.get(ctx.defs()) + init + str +"return\n.end method";
        TREE.put(ctx, total);
    }

    @Override public void exitDefs(tinyPythonParser.DefsContext ctx) {
        /*
                defs:
                (NEWLINE| def_stmt)*
                ;

        */

        String str = "";
        for(int i = 0; i < ctx.def_stmt().size(); i++)str+= TREE.get(ctx.def_stmt(i));
        //stmt 돌면서 저장
        TREE.put(ctx, str);
    }
    @Override public void exitStmt(tinyPythonParser.StmtContext ctx) {
        /*
        stmt:
                simple_stmt
                | compound_stmt
        ;*/
        if(ctx.getChild(0) == ctx.simple_stmt()){ TREE.put(ctx, TREE.get(ctx.simple_stmt())) ; }
        else if(ctx.getChild(0) == ctx.compound_stmt()){ TREE.put(ctx, TREE.get(ctx.compound_stmt())); } // 조건

    }


    @Override public void exitSimple_stmt(tinyPythonParser.Simple_stmtContext ctx) {
        /*
        simple_stmt:
        small_stmt NEWLINE
                ;

         */
        
        TREE.put(ctx, TREE.get(ctx.small_stmt()) +" "+ ctx.NEWLINE().getText());

    }
    @Override public void exitSmall_stmt(tinyPythonParser.Small_stmtContext ctx) {
        /*
        small_stmt:
        assignment_stmt
                | flow_stmt
                | print_stmt
                | return_stmt
        ;
         */

        String str = "";
        //getchild(0)번째를 체크를 하여서, flow인지 print인지, return인지 확인 후, 리턴해준 값을 구해와서 str에 저장을 한다
        if(ctx.getChild(0) == ctx.flow_stmt()) {str = TREE.get(ctx.flow_stmt());} // break , continue 등
        else if(ctx.getChild(0) == ctx.print_stmt()) str = TREE.get(ctx.print_stmt()); // print 관련
        else if(ctx.getChild(0) == ctx.return_stmt()) str = TREE.get(ctx.return_stmt()); // return 관련
        else if(ctx.getChild(0) == ctx.assignment_stmt()){ str = TREE.get(ctx.assignment_stmt());} // assignment관련

        TREE.put(ctx, str); // 저장한 str값을 tree에 put을 해준다.


    }
    @Override public void exitPrint_stmt(tinyPythonParser.Print_stmtContext ctx) {
        /*
        print_stmt:
        'print' print_arg
        ;
         */
          //  TREE.put(ctx, "print "+ TREE.get(ctx.print_arg())); // printf " get "
        //int랑 String 둘다 있다고 하였음
       // String state = "";
        if(ctx.print_arg().expr() != null)TREE.put(ctx, "getstatic java/lang/System/out Ljava/io/PrintStream;\n" + TREE.get(ctx.print_arg()) + "invokevirtual java/io/PrintStream/println(I)V\n"); // int 일시
        else TREE.put(ctx, "getstatic java/lang/System/out Ljava/io/PrintStream;\n" + TREE.get(ctx.print_arg()) + "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"); // String 일 시
    }
    @Override public void exitPrint_arg(tinyPythonParser.Print_argContext ctx) {
        /*
        print_arg:
        STRING
                | expr
        ;
         */
        if(ctx.getChild(0) == ctx.STRING()) {
            TREE.put(ctx,"ldc " + ctx.STRING().getText() +"\n"); // ldc "HELLO WORLD";
        }
        else if(ctx.getChild(0) == ctx.expr()) {
            TREE.put(ctx, TREE.get(ctx.expr())); // EXPR 불러오기
        }

    }
    @Override public void exitAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx) {
        /*
        assignment_stmt:
        NAME '=' expr
        ;
        */
       // TREE.put(ctx, ctx.NAME().getText() +" = "+ TREE.get(ctx.expr())); // A = 3;

        String id = ctx.NAME().getText();
        Symbol symbol = symbol_bags.peek().get(id);
        if(symbol == null){
            symbol = new Symbol("int", symbol_bags.peek().size());
            symbol_bags.peek().put(id, symbol);
        }
        String str = TREE.get(ctx.expr()) + "istore "+ symbol.getIndex() +"\n";
        TREE.put(ctx, str);

    }

    @Override public void exitCompound_stmt(tinyPythonParser.Compound_stmtContext ctx) {
        /*
compound_stmt:
      if_stmt
    | while_stmt
   // | def_stmt
    ;
        ;
         */
        String str = "";
        if(ctx.getChild(0) == ctx.if_stmt()) str += TREE.get(ctx.if_stmt()); // IF문
        else if(ctx.getChild(0) == ctx.while_stmt()) str += TREE.get(ctx.while_stmt()); // While 문
     //   else if(ctx.getChild(0) == ctx.def_stmt()) str += TREE.get(ctx.def_stmt()); // DEF

        TREE.put(ctx, str);


    }

    @Override

    public void exitIf_stmt(tinyPythonParser.If_stmtContext ctx) {
        /*
        if_stmt:
        'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
        ;

        'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
          0   1   2    3     [ 4     5    6    7]       [ 4  5  6 ]
        */
        String END_LABEL = "END_LABEL"+label_number;
        String NEXT_LABEL = "NEXT_LABEL"+label_number;
        label_number++;
        String str = "";


        /*
        현재 버그 if test : sute else: suite를 하면  goto end label next_label 이렇게 되는데 이렇게 하면 오류가 남.
        그래서 suite숫자 기반으로 하면 else쪽으로 첨프를 안하고 쭉 읽게 됨.



         */
      //  if(ctx.suite().size() > 1)
        str += TREE.get(ctx.test(0)) + NEXT_LABEL + "\n" + TREE.get(ctx.suite(0)) +"goto " + END_LABEL + "\n" + NEXT_LABEL + ":\n"; // if elif 있는 경우는 goto 달아줌
       // else
          //  str += TREE.get(ctx.test(0)) + NEXT_LABEL + "\n" + TREE.get(ctx.suite(0))  + NEXT_LABEL + ":\n"; // 그냥 if 거나 if else면? 굳이 goto가 필요없음
        //elif
        for(int i = 1; i < ctx.test().size(); i++){
            if( i == ctx.test().size() - 1 && !(ctx.suite().size() > ctx.test().size()))NEXT_LABEL = END_LABEL; // 마지막이고, else가 없다면?  END LABEL로
            else{NEXT_LABEL = "NEXT_LABEL"+label_number;label_number++;}
            str += TREE.get(ctx.test(i)) + NEXT_LABEL + "\n" + TREE.get(ctx.suite(i)) + "goto " + END_LABEL + "\n" + NEXT_LABEL + ":\n";
        }
        //else
        if(ctx.suite().size() > ctx.test().size())str += TREE.get(ctx.suite(ctx.suite().size() - 1));
        // 이 말은 test : suit에서 조건 test가 suite보다 적다는 것은 else가 있다는 것 ex) test : suit => 1개 else없음 test : suite ? suite 이러면 ?엔 else가 있다는 것
        str += END_LABEL + ":\n";
        TREE.put(ctx,str);
    }
    @Override public void exitSuite(tinyPythonParser.SuiteContext ctx) {
        /*
        suite:
                  simple_stmt
                | NEWLINE  stmt+
        ;
         */
            String str = "";
          //  System.out.println(" + " + ctx.getParent().getText() + " " + current_tab);
            if(ctx.getChild(0) == ctx.simple_stmt()){
          //      str+="\n";
          //      for(int tab = 0; tab < current_tab; tab++){ str+= " ";} // 현재 탭에 맞추어준다
                str += TREE.get(ctx.simple_stmt());
            }
            else if(ctx.getChild(0) == ctx.NEWLINE()){


                str += ctx.NEWLINE().getText();
                for(int i = 0; i < ctx.getChildCount() - 1; i++) {
              //      for(int tab = 0; tab < current_tab; tab++){ str+= " ";}// 현재 탭에 맞추어준다
                    str += TREE.get(ctx.stmt(i));

                }

            }
        TREE.put(ctx,str);
    }
    @Override public void exitDef_stmt(tinyPythonParser.Def_stmtContext ctx) {
        /*
        def_stmt:
        'def' NAME OPEN_PAREN args CLOSE_PAREN ':' suite
        ;


         */

       // current_tab -= 4; // 탭감소
       // TREE.put(ctx, "def "+ ctx.NAME().getText()+""+ctx.OPEN_PAREN().getText()+""+TREE.get(ctx.args()) +""+ctx.CLOSE_PAREN().getText()+":"+TREE.get(ctx.suite()));
        String Number = "";
        for(int i =0; i< ctx.args().NAME().size(); i++) Number += "I";
        symbol_bags.pop();
        TREE.put(ctx,".method public static "+ ctx.NAME().getText() +"("+Number+")I\n\t.limit stack 32\n\t.limit locals 32\n"+TREE.get(ctx.suite())+".end method\n\n");

    }


    @Override public void exitFlow_stmt(tinyPythonParser.Flow_stmtContext ctx) {
        /*
        flow_stmt:
                break_stmt
                | continue_stmt
        ;
         */
        if(ctx.getChild(0) == ctx.break_stmt()){TREE.put(ctx, TREE.get(ctx.break_stmt()));} // break
        else if(ctx.getChild(0) == ctx.continue_stmt())TREE.put(ctx, TREE.get(ctx.continue_stmt())); // continue
    }


    static ArrayList<String> startLabels = new ArrayList<>();
    static ArrayList<String> endLabels = new ArrayList<>();

    @Override public void enterWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
        String Start = "LOOP"+loop_number;
        String End = "LOOP_END"+loop_number;
        loop_number++;
        startLabels.add(Start); // 하나하나 씩 쌓아준다 Loop_Start_Label
        endLabels.add(End); // 하나하나 씩 쌓아준다 Loop_End_Label
    }

    @Override public void exitWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
        /*
        while_stmt:
        'while' test ':' suite
        ;
        */
      //  TREE.put(ctx, "while "+TREE.get(ctx.test()) +":"+TREE.get(ctx.suite())); // while 1 : or while 1  < 4
        TREE.put(ctx, startLabels.get(startLabels.size()-1)+":\n"+TREE.get(ctx.test())+endLabels.get(endLabels.size()-1)+"\n"+ TREE.get(ctx.suite())+"goto "+startLabels.get(startLabels.size()-1)+"\n"+endLabels.get(endLabels.size()-1)+":\n");
    }
    @Override public void exitBreak_stmt(tinyPythonParser.Break_stmtContext ctx) {
        /*
        break_stmt:
        'break'
        ;
         */
      //  TREE.put(ctx, "break"); // break

        if(!endLabels.isEmpty()){
            TREE.put(ctx, "goto "+endLabels.get(endLabels.size()-1)+"\n");
        }
    }
    @Override public void exitContinue_stmt(tinyPythonParser.Continue_stmtContext ctx) {
        /*
        continue_stmt:
        'continue'
        ;*/
       // TREE.put(ctx, "continue"); // continue

        if(!startLabels.isEmpty()){
            TREE.put(ctx, "goto "+startLabels.get(startLabels.size()-1)+"\n");
        }

    }
    @Override public void exitArgs(tinyPythonParser.ArgsContext ctx) {
        /*
        args:

        | NAME (',' NAME)*
        ;
         */
            /*
        if(ctx.getChildCount() == 1) TREE.put(ctx , ctx.NAME(0).getText()); // 하나인 경우
        else{ // a,  b,  c , d ...
            String str = "";
            str += ctx.NAME(0).getText();
            for(int i = 1; i <ctx.getChildCount(); i++){
                if(ctx.NAME(i) != null)
                str += ", " + ctx.NAME(i).getText();
            }
            TREE.put(ctx,str);
        }

        */
        symbol_bags.push(new HashMap<>());
        if(ctx.getChildCount() >= 1){
            for(int i = 0; i < ctx.getChildCount(); i++){  // 자식 수 만큼 쭉 돈다
                if(ctx.NAME(i) != null)symbol_bags.peek().put(ctx.NAME(i).getText(), new Symbol("int", i));
            }
        }
    }
    @Override public void exitReturn_stmt(tinyPythonParser.Return_stmtContext ctx) {
        /*
        return_stmt:
        'return' expr? // 존재할수도 안할수도 있음
        ;
         */
        if(ctx.getChildCount() == 1) TREE.put(ctx, "return"); // return
        else TREE.put(ctx, TREE.get(ctx.expr()) +"ireturn\n"); // return expr

    }
    @Override public void exitTest(tinyPythonParser.TestContext ctx) {
        /*
        test:
    expr comp_op expr
    ;

        ;
         */

       // TREE.put(ctx,TREE.get(ctx.expr(0)) +" "+TREE.get(ctx.comp_op())+" "+TREE.get(ctx.expr(1)));

        String op = TREE.get(ctx.comp_op());
        String state = "";

        if ("==".equals(op)) state = "if_icmpne ";
        else if ("!=".equals(op)) state = "if_icmpeq ";
        else if ("<".equals(op)) state = "if_icmpge ";
        else if (">".equals(op)) state = "if_icmple ";
        else if ("<=".equals(op)) state = "if_icmpgt ";
        else if (">=".equals(op)) state = "if_icmplt ";
        else state = "ERROR ";
        // OP에 맞도록 변경
        TREE.put(ctx, TREE.get(ctx.expr(0)) + TREE.get(ctx.expr(1)) + state);
    }
    @Override public void exitComp_op(tinyPythonParser.Comp_opContext ctx) {
        /*
        comp_op:
        '<'
                | '>'
                | '=='
                | '>='
                | '<='
                | '!='
        ;
         */

            TREE.put(ctx, ctx.getChild(0).getText()); // 부등식
    }

    @Override public void exitExpr(tinyPythonParser.ExprContext ctx) {
        /*
        expr:
                 NAME opt_paren
                | NUMBER
                | '(' expr ')'
                | expr (( '+' | '-' ) expr)+
        ;
         */

        String str = "";
        if(ctx.NUMBER() != null) {
            int num = Integer.parseInt(ctx.NUMBER().getText());
            String value = num >= -1 && num <= 5 ? "iconst_" :
                    num >= -128 && num <= 127 ? "bipush " :
                            num >= -32768 && num <= 32767 ? "sipush " : "ldc ";
            TREE.put(ctx, value  + num + "\n");
        } else if(ctx.NAME() != null) {
            String id = ctx.NAME().getText();
            if(ctx.opt_paren().CLOSE_PAREN() != null) {
                TREE.put(ctx, TREE.get(ctx.opt_paren()) + "invokestatic Test/" + id + "(" + I_number + ")I\n");
                I_number = ""; // II후 다시 공백으로 만들어 줘서 다음 것 받을 준비
            } else {
                Symbol symbol = symbol_bags.peek().get(id);
                if(symbol != null) str = "iload " + symbol.getIndex() + "\n";
                TREE.put(ctx, str);
            }
        } else if (ctx.expr().size() == 1) {
            TREE.put(ctx, TREE.get(ctx.expr(0)));
        } else if (ctx.expr().size() >= 2) {
            str = TREE.get(ctx.expr(0)) + TREE.get(ctx.expr(1));
            str += ctx.getChild(1).getText().equals("+") ? "iadd\n" : "isub\n";
            TREE.put(ctx, str);
        }

    }

    @Override public void exitOpt_paren(tinyPythonParser.Opt_parenContext ctx) {
        /*
        opt_paren:
                | '(' ')'
                | '(' expr (',' expr)* ')'
        ;
         */
        String str = "";
        if (ctx.expr().size() > 0) { // (expr) or (Expr , expr )이 있는 경우
            for (int i = 0; i < ctx.getChildCount(); i++) {
                if (ctx.expr(i) != null) {
                    str += TREE.get(ctx.expr(i));
                    I_number += "I";
                }
            }
        }
        TREE.put(ctx, str);
    }
    @Override
    public void exitProgram(tinyPythonParser.ProgramContext ctx){
        String output = ".class public Test\n .super java/lang/Object\n\n; standard initializer\n.method public <init>()V\n aload_0\n invokenonvirtual java/lang/Object/<init>()V\n return\n.end method\n\n";
        TREE.put(ctx, TREE.get(ctx.file_input()));
        output += TREE.get(ctx.file_input());
        System.out.println(output);
        try (FileWriter fw = new FileWriter(new File("Test.j"))) {
            fw.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("exit Program");
        }

    }
}
