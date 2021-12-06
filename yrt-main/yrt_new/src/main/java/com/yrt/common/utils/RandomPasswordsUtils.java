package com.yrt.common.utils;

import java.util.Random;

public class RandomPasswordsUtils {
	
	/**
	 * 随机生成密码
     * @param length 密码的长度
     * @return 最终生成的密码
     */
	public static String randomPasswords (int length) {
        // 最终生成的密码
        String password = "";
        Random random = new Random();
        for (int i = 0; i < length; i ++) {
            // 随机生成0或1，用来确定是当前使用数字还是字母 (0则输出数字，1则输出字母)
            int charOrNum = random.nextInt(2);
            if (charOrNum == 1) {
                // 随机生成0或1，用来判断是大写字母还是小写字母 (0则输出小写字母，1则输出大写字母)
                //int temp = random.nextInt(2) == 1 ? 65 : 97;
                password += (char) (random.nextInt(26) + 65);
            } else {
                // 生成随机数字
                password += random.nextInt(10);
            }
        }
        return password;
    }
	
}
