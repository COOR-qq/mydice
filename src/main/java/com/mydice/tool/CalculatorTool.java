package com.mydice.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculatorTool {
    private String result;
    private String formula;
    private List<String> s = new ArrayList<String>();//中间结果

    /**
     * 判断字符是否为运算符，是为真，不是为假
     */
    private boolean isOprator(String c) {
        // TODO Auto-generated method stub
        try {
            if (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("(") || c.equals(")"))
                return true;

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }

    /**
     * 判断字符是否为‘)’，是为真，不是为假
     */
    private boolean isBracketRight(String c) {
        // TODO Auto-generated method stub
        try {
            if (c.equals(")"))
                return true;

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }

    private final static  Map Priority=new HashMap<String,String>(){{
        put("(","0");
        put("-","1");
        put("+","2");
        put("*","3");
        put("/","4");
        put(")","6");
    }};

    /**
     * 判断字符是否为‘(’，是为真，不是为假
     */
    private boolean isBracketLeft(String c) {
        // TODO Auto-generated method stub
        try {
            if (c.equals("("))
                return true;

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }

    /**
     * 中叠式 转 逆波兰式
     * 根据运算符优先级
     */
    private void reversePoli() {
        List<Character> s1 = new ArrayList<Character>();//运算符
        String temp = "";//存数字
        char c;
        for (int i = 0; i < formula.length(); i++) {
            c = formula.charAt(i);
            if (isOprator(String.valueOf(c))) {//判断是否是运算符,得到完整的一个数temp,放入s2
                if (!temp.equals("")) {
                    s.add(temp);
                }
                if(isBracketLeft(String.valueOf(c))){//判断是否是左括号，直接放入s1
                    s1.add(c);
                }else if (isBracketRight(String.valueOf(c))) {//是')',依次弹出s1中的符号到s2中，直到遇到‘(’
                    for (int j = s1.size() - 1; j > -1; j--) {
                        if (isBracketLeft(String.valueOf(s1.get(j)))) {
                            s1.remove(j);
                            break;
                        }else if (s1.get(j) != '(') {//除了‘(’以外的运算符加入s中
                            s.add(String.valueOf(s1.get(j)));
                        }
                        s1.remove(j);
                    }
                } else if (s1.size()!=0&&Integer.parseInt(Priority.get(String.valueOf(c)).toString())<=Integer.parseInt(Priority.get(String.valueOf(s1.get(s1.size()-1))).toString())) {
                    while(s1.size()!=0&&Integer.parseInt(Priority.get(String.valueOf(c)).toString())<=Integer.parseInt(Priority.get(String.valueOf(s1.get(s1.size()-1))).toString())){
                        s.add(String.valueOf(s1.get(s1.size()-1)));
                        s1.remove(s1.size()-1);
                    }
                    s1.add(c);
                } else {
                    s1.add(c);
                }
                temp = "";
            } else {
                temp += c;
            }
        }
        if (!temp.equals("")) {
            s.add(temp);
        }
        for (int i = s1.size()-1; i >-1 ; i--) {
            s.add(String.valueOf(s1.get(i)));
        }
    }

    public static String calculate(String formula) {
        CalculatorTool cal = new CalculatorTool();
        cal.setFormula(formula);
            try {
                double temp = Double.parseDouble(cal.getResult());
                if (temp % 1 != 0)
                    return cal.getResult();
                else {
                    String res = "" + (int) temp;
                    System.out.println(res);
                    return res;
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                System.out.println(e);
                System.out.println(e.getCause());
                return "ERROR";
        }

    }

    /**
     * 逆波兰式计算
     */
    private void count_result() {
        reversePoli();
        List<String> s1 = new ArrayList<String>();
        System.out.println(s);
        for (int i = 0; i < s.size(); i++) {
            if (isOprator(s.get(i))) {
                int len = s1.size();
                double num1 = Double.valueOf(s1.get(len - 2));
                double num2 = Double.valueOf(s1.get(len - 1));
                char[] op = s.get(i).toCharArray();
                double re = calc(num1, num2, op[0]);
                s1.remove(len - 1);
                s1.remove(len - 2);
                s1.add(String.valueOf(re));
            } else {
                s1.add(s.get(i));
            }
        }
        result = s1.get(0);
    }

    /**
     * 四则运算
     */
    private double calc(double num1, double num2, char op)
            throws IllegalArgumentException {
        switch (op) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0) throw new IllegalArgumentException("divisor can't be 0.");
                return num1 / num2;
            default:
                return 0; // will never catch up here
        }
    }

    public String getResult() {
        count_result();
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

}