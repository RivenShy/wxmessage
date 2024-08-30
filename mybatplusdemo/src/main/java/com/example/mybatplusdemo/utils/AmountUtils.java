package com.example.mybatplusdemo.utils;

import cn.hutool.core.convert.NumberChineseFormatter;
import com.example.mybatplusdemo.config.Func;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmountUtils {

    //大写数字
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    //整数部分的单位
    private static final String[] INTEGER_UNIT = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "万", "拾", "佰", "仟"};
    //小数部分的单位
    private static final String[] DECIMAL_UNIT = {"角", "分"};

    /**
     * 数字转中文
     */
    public static String toChinese(Double amount){
        if(Func.isNull(amount)) {
            return null;
        }
        try {
            return NumberChineseFormatter.format(amount, true, true);
        } catch (Exception e) {
            log.error("人民币转大写失败！{}", e.getMessage());
        }
        return toChinese(amount.toString());
    }

    private static String toChinese(String str) {
        //判断输入的金额字符串是否符合要求
        if (str == null || str.equals("") || !str.matches("(-)?[\\d]*(.)?[\\d]*")) {
            System.out.println("抱歉，请输入数字！");
            return str;
        }

        if ("0".equals(str) || "0.00".equals(str) || "0.0".equals(str)) {
            return "零";
        }

        //判断是否存在负号"-"
        boolean negativeFlag = false;
        if (str.startsWith("-")) {
            negativeFlag = true;
            str = str.replaceAll("-", "");
        }

        str = str.replaceAll(",", "");//去掉","
        String integerStr;//整数部分数字
        String decimalStr;//小数部分数字

        //初始化：分离整数部分和小数部分
        if (str.indexOf(".") > 0) {
            integerStr = str.substring(0, str.indexOf("."));
            decimalStr = str.substring(str.indexOf(".") + 1);
        } else if (str.indexOf(".") == 0) {
            integerStr = "";
            decimalStr = str.substring(1);
        } else {
            integerStr = str;
            decimalStr = "";
        }

        //beyond超出计算能力，直接返回
        if (integerStr.length() > INTEGER_UNIT.length) {
            return str;
        }

        int[] integers = toIntArray(integerStr);//整数部分数字
        //判断整数部分是否存在输入012的情况
        if (integers.length > 1 && integers[0] == 0) {
            if (negativeFlag) {
                str = "-" + str;
            }
            return str;
        }
        boolean isBigThanTenThousand = isBigThanTenThousand(integerStr);//设置万单位
        int[] decimals = toIntArray(decimalStr);//小数部分数字
        String result = getChineseInteger(integers, isBigThanTenThousand);//返回最终的大写金额
        String decimalsStr = getChineseDecimal(decimals);
        if(Func.isNotBlank(decimalsStr)) {
            result += decimalsStr;
        }
        result += "整";
        if (negativeFlag) {
            return "负" + result;//如果是负数，加上"负"
        } else {
            return result;
        }
    }

    private static int[] toIntArray(String number) {
        int[] array = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            array[i] = Integer.parseInt(number.substring(i, i + 1));
        }
        return array;
    }

    public static String getChineseInteger(int[] integers, boolean isWan) {
        StringBuffer chineseInteger = new StringBuffer("");
        int length = integers.length;
        if (length == 1 && integers[0] == 0) {
            return "";
        }
        for (int i = 0; i < length; i++) {
            String key = "";
            if (integers[i] == 0) {
                if ((length - i) == 13)//万（亿）
                    key = INTEGER_UNIT[4];
                else if ((length - i) == 9) {//亿
                    key = INTEGER_UNIT[8];
                } else if ((length - i) == 5 && isWan) {//万
                    key = INTEGER_UNIT[4];
                } else if ((length - i) == 1) {//元
                    key = INTEGER_UNIT[0];
                }
                if ((length - i) > 1 && integers[i + 1] != 0) {
                    key += NUMBERS[0];
                }
            }
            chineseInteger.append(integers[i] == 0 ? key : (NUMBERS[integers[i]] + INTEGER_UNIT[length - i - 1]));
        }
        return chineseInteger.toString();
    }

    private static boolean isBigThanTenThousand(String integerStr) {
        int length = integerStr.length();
        if (length > 4) {
            String subInteger = "";
            if (length > 8) {
                subInteger = integerStr.substring(length - 8, length - 4);
            } else {
                subInteger = integerStr.substring(0, length - 4);
            }
            return Integer.parseInt(subInteger) > 0;
        } else {
            return false;
        }
    }

    private static String getChineseDecimal(int[] decimals) {
        StringBuilder chineseDecimal = new StringBuilder("");
        int length = decimals.length;
        if(length == 0 || (length == 1 && decimals[0] == 0)) {
            return "";
        }
        chineseDecimal.append(NUMBERS[decimals[0]]).append(DECIMAL_UNIT[0]);
        if(length == 1 || (length == 2 && decimals[1] == 0)) {
            return chineseDecimal.toString();
        }
        chineseDecimal.append(NUMBERS[decimals[1]]).append(DECIMAL_UNIT[1]);
        return chineseDecimal.toString();
    }
}