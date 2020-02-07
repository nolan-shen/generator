package com.sn.demo;

import cn.hutool.core.util.StrUtil;

public class testMail {

  public static void main(String[] args) {
    String s = StrUtil.toCamelCase("timeStamp");
    System.out.println(s);
    String s1 = StrUtil.upperFirstAndAddPre("tmsSSSS","");
    System.out.println(s1);
  }
}
