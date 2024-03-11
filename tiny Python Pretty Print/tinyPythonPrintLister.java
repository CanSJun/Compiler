import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

public class tinyPythonPrintLister  extends tinyPythonBaseListener{

    ParseTreeProperty<String> TREE = new ParseTreeProperty<String>();
   static int current_tab = 0;
    @Override public void exitFile_input(tinyPythonParser.File_inputContext ctx) {
        /*
        file_input:
        (NEWLINE | stmt)* EOF
        ;
        */
        String str = "";

        if(ctx.getChildCount() >= 2){
            for(int i = 0; i < ctx.getChildCount()-1; i++){
                if(null != ctx.stmt(i)){str += TREE.get(ctx.stmt(i)); }
                else if(null != ctx.NEWLINE(i)){str += ctx.NEWLINE(i).getText();  }
            }
        }

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
            TREE.put(ctx, "print "+ TREE.get(ctx.print_arg())); // printf " get "
    }
    @Override public void exitPrint_arg(tinyPythonParser.Print_argContext ctx) {
        /*
        print_arg:
        STRING
                | expr
        ;
         */
        String str = "";
        if(ctx.getChild(0) == ctx.STRING()) str += ctx.STRING().getText(); // "STRING"
        else if(ctx.getChild(0) == ctx.expr()) str += TREE.get(ctx.expr()); // expr
        TREE.put(ctx, str);
    }
    @Override public void exitAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx) {
        /*
        assignment_stmt:
        NAME '=' expr
        ;
        */
        TREE.put(ctx, ctx.NAME().getText() +" = "+ TREE.get(ctx.expr())); // A = 3;
    }

    @Override public void exitCompound_stmt(tinyPythonParser.Compound_stmtContext ctx) {
        /*
        compound_stmt:
                if_stmt
                | while_stmt
                | def_stmt
        ;
         */
        String str = "";
        if(ctx.getChild(0) == ctx.if_stmt()) str += TREE.get(ctx.if_stmt()); // IF문
        else if(ctx.getChild(0) == ctx.while_stmt()) str += TREE.get(ctx.while_stmt()); // While 문
        else if(ctx.getChild(0) == ctx.def_stmt()) str += TREE.get(ctx.def_stmt()); // DEF

        TREE.put(ctx, str);


    }

    @Override
    public void enterIf_stmt(tinyPythonParser.If_stmtContext ctx) {
        current_tab += 4; // 들어가기전 TAB 증가
    }
    public void exitIf_stmt(tinyPythonParser.If_stmtContext ctx) {
        /*
        if_stmt:
        'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
        ;

        'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?
          0   1   2    3     [ 4     5    6    7]       [ 4  5  6 ]
        */
        String str= "";
        if(ctx.getChildCount() == 4){ // if
            str += "if "+TREE.get(ctx.test(0))+":";
            str += TREE.get(ctx.suite(0));
        }else{ // elif , else인데 탭수를 현재 상태에서 -4로 출력을 해준다.
            str += "if "+TREE.get(ctx.test(0))+":";
            str += TREE.get(ctx.suite(0));
            int current_page = 1;
            for(int i = 4; i< ctx.getChildCount(); i++){
                if(ctx.getChild(i).getText().equals("elif")){
                    for(int tab = 0; tab < current_tab - 4; tab++){ str+= " ";}
                    str += "elif "+ TREE.get(ctx.test(current_page)) + ":" + TREE.get(ctx.suite(current_page));
                    current_page++;
                }else if(ctx.getChild(i).getText().equals("else")){
                    for(int tab = 0; tab < current_tab - 4; tab++){ str+= " ";}
                    str += "else:" + TREE.get(ctx.suite(current_page));
                    current_page++;
                }
            }
        }
        TREE.put(ctx, str);
        current_tab -= 4;
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
                str+="\n";
                for(int tab = 0; tab < current_tab; tab++){ str+= " ";} // 현재 탭에 맞추어준다
                str += TREE.get(ctx.simple_stmt());

            }
            else if(ctx.getChild(0) == ctx.NEWLINE()){


                str += ctx.NEWLINE().getText();
                for(int i = 0; i < ctx.getChildCount() - 1; i++) {
                    for(int tab = 0; tab < current_tab; tab++){ str+= " ";}// 현재 탭에 맞추어준다
                    str += TREE.get(ctx.stmt(i));

                }

            }
        TREE.put(ctx,str);
    }
    @Override public void enterDef_stmt(tinyPythonParser.Def_stmtContext ctx) {
      current_tab+= 4;// 탭증가
    }


    @Override public void exitDef_stmt(tinyPythonParser.Def_stmtContext ctx) {
        /*
        def_stmt:
        'def' NAME OPEN_PAREN args CLOSE_PAREN ':' suite
        ;
         */
        current_tab -= 4; // 탭감소
        TREE.put(ctx, "def "+ ctx.NAME().getText()+""+ctx.OPEN_PAREN().getText()+""+TREE.get(ctx.args()) +""+ctx.CLOSE_PAREN().getText()+":"+TREE.get(ctx.suite()));
        //def (a,b,c,d) 형식
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

    @Override public void enterWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
            current_tab += 4; // while tab증가
    }
    @Override public void exitWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
        /*
        while_stmt:
        'while' test ':' suite
        ;
        */
        TREE.put(ctx, "while "+TREE.get(ctx.test()) +":"+TREE.get(ctx.suite())); // while 1 : or while 1  < 4
        current_tab -= 4; // tab 감소

    }
    @Override public void exitBreak_stmt(tinyPythonParser.Break_stmtContext ctx) {
        /*
        break_stmt:
        'break'
        ;
         */
        TREE.put(ctx, "break"); // break
    }
    @Override public void exitContinue_stmt(tinyPythonParser.Continue_stmtContext ctx) {
        /*
        continue_stmt:
        'continue'
        ;*/
        TREE.put(ctx, "continue"); // continue
    }
    @Override public void exitArgs(tinyPythonParser.ArgsContext ctx) {
        /*
        args:

        | NAME (',' NAME)*
        ;
         */
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
    }
    @Override public void exitReturn_stmt(tinyPythonParser.Return_stmtContext ctx) {
        /*
        return_stmt:
        'return' expr? // 존재할수도 안할수도 있음
        ;
         */
        if(ctx.getChildCount() == 1) TREE.put(ctx, "return"); // return
        else TREE.put(ctx, "return " + TREE.get(ctx.expr())); // return expr

    }
    @Override public void exitTest(tinyPythonParser.TestContext ctx) {
        /*
        test:
        expr (comp_op expr)*
        ;
         */
        String str = null;
        if(ctx.getChildCount() == 1) TREE.put(ctx, TREE.get(ctx.expr(0))); // 하나인경우
        else { // 비교대상이 있는 경우
            str = TREE.get(ctx.expr(0));
            for(int i = 0; i < ctx.getChildCount(); i++) {
                if(ctx.comp_op(i) != null && ctx.expr(i + 1) != null )
                str += " " + TREE.get(ctx.comp_op(i)) + " " + TREE.get(ctx.expr(i + 1));
            }
            TREE.put(ctx, str);
        }
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
        if(ctx.getChildCount() == 1) TREE.put(ctx, ctx.NUMBER().getText()); // 숫자만 있을 시 
        else if(ctx.getChildCount() == 2){ TREE.put(ctx, ctx.NAME().getText() +""+ TREE.get(ctx.opt_paren())); } // 괄호있는건 구별하기 위함이라고 함
        else if(ctx.getChildCount() >= 3){ // expr + expr 형식

            if(ctx.getChild(0).getText().equals("(")){

                TREE.put(ctx, "("+ TREE.get(ctx.expr(0))+")");
            }else{
                String str ="";

                str += TREE.get(ctx.expr(0));
                for(int i = 1; i < ctx.getChildCount(); i++){
                    if(ctx.getChild(i).getText().equals("+")){
                        str+= " + " + TREE.get(ctx.expr(i));

                    }else if(ctx.getChild(i).getText().equals("-")){
                        str+= " - " + TREE.get(ctx.expr(i));
                    }
                }
                TREE.put(ctx, str);
            }
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
        if(ctx.getChildCount() == 2) str +="()"; // 아무것도 없는경우 
        else if (ctx.getChildCount() > 2){ // (expr) or (Expr , expr )이 있는 경우

            str += "(";
            str += TREE.get(ctx.expr(0));
            for(int i = 1; i < ctx.getChildCount(); i++){
                if(ctx.expr(i) != null)
                str += ", "+TREE.get(ctx.expr(i));
            }
            str += ")";

        }
        TREE.put(ctx, str);
    }
    @Override
    public void exitProgram(tinyPythonParser.ProgramContext ctx){
    String output = "";
        for(int i = 0; i < ctx.getChildCount(); i++) {
            TREE.put(ctx, ctx.file_input().getText()); // 파일 입력을 들고온다 
            output += TREE.get(ctx.getChild(i)); // 자식을 불러와 String에 추가
        }
        System.out.println(output);
        System.out.println("exit Program");
    }

}
