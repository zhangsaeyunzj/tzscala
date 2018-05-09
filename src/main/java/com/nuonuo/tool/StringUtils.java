package com.nuonuo.tool;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016-07-18.
 */
public class StringUtils {

    /**
     * 过滤特殊字符（\t,\r,\n）
     *
     * @param s
     * @return
     */
    public static String check(String s) {
        if (s != null && !"".equals(s)) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(s);
            return m.replaceAll(" ").trim();
        } else {
            return s;
        }
    }

    /**
     * 处理json串中异常产生的反斜杠，现将\"替换成空格，在过滤非法的反斜杠
     *
     * @param s
     * @return
     */
    public static String filter1(String s) {
        if (null == s || s.equals("")) {
            return s;
        } else {
            return s.replaceAll("\\\\\"", " ").trim().replace("\\", "");
        }
    }

    /**
     * 处理json串中异常产生的反斜杠，现将\"*(\"}和\",除外)替换成空格，在过滤非法的反斜杠
     * 有一定的精度损失，但是为了过滤非法字符先这么处理
     *
     * @param s
     * @return
     */
    public static String filter2(String s) {
        if (null == s || s.equals("")) {
            return s;
        } else {
            Pattern p = Pattern.compile("\\\\\"[^},\"\\\\]");
            Matcher m = p.matcher(s);
            String result = m.replaceAll(" ").trim();
            int i = 0;
            while (i <= 3) {
                i++;
                result = result.replace("\\\"\"", "\"");
                if (result.indexOf("\\\"\"") == -1) {
                    break;
                }
            }
            return result.replace("\\", "");
        }
    }

    public static Map parseJson2Map(String s) {
        try {
            return JSON.parseObject(s, Map.class);
        } catch (Exception e) {
            try {
                String src = filter1(s);//使用没有精度损失的过滤
                return JSON.parseObject(src, Map.class);
            } catch (Exception e1) {
                String src = filter2(s);//出现异常使用损失查询精度的过滤
                return JSON.parseObject(src, Map.class);
            }
        }
    }

    public static boolean isNotNull(String str) {
        boolean flag = true;
        if ("".equals(str) || null == str) {

            flag = false;

        }
        return flag;
    }

    /**
     * 补零至长度为19位
     *
     * @param s
     * @return
     */
    public static String addZero(String s) {
        while (s.length() < 19) {
            s += "0";
        }
        return s;
    }

    /**
     * 最小编辑距离计算
     *
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 最小编辑距离
     */
    public static int minEditDistance(final String s1, final String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int i = 1; i <= n; i++) {
            dp[0][i] = i;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int insertion = dp[i][j - 1] + 1;
                int deletion = dp[i - 1][j] + 1;
                int replace = dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1);
                dp[i][j] = Math.min(replace, Math.min(insertion, deletion));
            }
        }
        return dp[m][n];
    }
}
