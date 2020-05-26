package com.test.zfbbill;

import java.util.Random;

/**
 * created by lpw
 * on 2020/5/26
 */
public class StringRandom {
    public static String getNumberRandom(int length){
        StringBuffer val = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            if (i==0){
                val.append(random.nextInt(9)+1);
            }else {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }

    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {
            //数字的概率大点
            String charOrNum = random.nextInt(100) % 3 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }
}
