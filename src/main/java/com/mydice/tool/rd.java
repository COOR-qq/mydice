package com.mydice.tool;

import com.mydice.tool.CalculatorTool;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class rd {
    private static Pattern pattern1 = Pattern.compile("([+*/\\-dDrR#\\d]+)");
    private static Pattern pattern = Pattern.compile("([+*/\\-dD#\\d]+)");
    private static Pattern chin = Pattern.compile("[\u4e00-\u9fa5]+");

    public static int roll(int d) {
        Random r = new Random();
        int res = r.nextInt(d) + 1;
        return res;
    }

    public static int roll() {
        return roll(100);
    }

    public static String dicecommand(String message){
        message=message.toLowerCase().substring(1);
        if(message.equals("help")){
            return "基于mirai的骰子测试版\n";
        }else if(message.startsWith("r")&&!message.startsWith("ra"))
            return r(message);
        return null;
    }

    public static String r(String msgString) {
        Matcher matcher = pattern.matcher(msgString);
        String strdice = "";
        if (matcher.find()) {
            strdice = matcher.group();
        }
        msgString = msgString.replaceFirst(pattern1.toString(), "");
        StringBuilder stringBuilder = new StringBuilder();
        if(msgString.length()!=0)stringBuilder.append("由于"+msgString+",");
        stringBuilder.append(strdice);
        String formula;
        if (strdice.length() == 0){
            formula = regxdx("d");
            stringBuilder.append("d100=");
        }
        else{
            stringBuilder.append("=");
            formula = regxdx(strdice);
        }
        stringBuilder.append(formula);
        stringBuilder.append("=");
        String result = CalculatorTool.calculate(formula);
        if (result.equals("ERROR"))
            stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(result);
        return stringBuilder.toString();
    }

    public static String regxdx(String msg) {
        Pattern function = Pattern.compile("(\\d*)[dD](\\d*)");
        Matcher mFunction = function.matcher(msg);
        StringBuffer sb = new StringBuffer();
        int times = 1, maxRolls = 100;
        while (mFunction.find()) {
//            取第一个X，是骰点次数，这个times已经默认设定为1，如果取到则覆盖
            if (!mFunction.group(1).isEmpty()) {
                times = Integer.parseInt(mFunction.group(1));
            }
//            取第二个X，是骰点最大值，这个maxRolls已根据设定的默认最大值或100设定，如果取到则覆盖
            if (!mFunction.group(2).isEmpty()) {
                try {
                    maxRolls = Integer.parseInt(mFunction.group(2));
                } catch (NumberFormatException e) {
                    System.out.println(e);
                }
            }
            if (times != 1) mFunction.appendReplacement(sb, "(" + rollmanydice(times, maxRolls) + ")");
            else mFunction.appendReplacement(sb, rollmanydice(times, maxRolls));
        }
        mFunction.appendTail(sb);
        return sb.toString();
    }

    public static String rollmanydice(int times, int maxRolls) {
        StringBuilder stringBuilder = new StringBuilder();
        if (times == 1) return stringBuilder.append(roll(maxRolls)).toString();
        int result = 0;
        for (int i = 0; i < times; i++) {
            int temp = roll(maxRolls);
            stringBuilder.append(temp);
            if (i != times - 1) stringBuilder.append("+");
            result = result + temp;
        }
        return stringBuilder.toString();
    }
}
