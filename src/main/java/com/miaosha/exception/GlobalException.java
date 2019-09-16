package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException {

  private CodeMsg codeMsg;

  public GlobalException(CodeMsg codeMsg) {
    super(codeMsg.toString());
    this.codeMsg = codeMsg;
  }

  public CodeMsg getCodeMsg() {
    return codeMsg;
  }
}
