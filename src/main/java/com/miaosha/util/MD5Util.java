package com.imooc.miaosha.util;


import org.springframework.util.DigestUtils;

public class MD5Util {

  public static String md5(String src) {
    return DigestUtils.md5DigestAsHex(src.getBytes());
  }

  private static final String salt = "1a2b3c4d";

  public static String inputPassFormPass(String inputPass) {
    String str = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
    return md5(str);
  }

  public static String formPassToDBPass(String formPass, String salt) {
    String str = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
    return md5(str);
  }

  public static String inputPassToDBPass(String input, String saltDB) {
    String formPass = inputPassFormPass(input);
    //System.out.println(formPass);
    String dbPass = formPassToDBPass(formPass, saltDB);
    return dbPass;
  }


  public static void main(String[] args) {
    System.out.println(inputPassToDBPass("123456", salt));
    System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", salt));
  }
}
