package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: miaosha_1
 * @description: 异常处理
 * @author: WenYuan
 * @create: 2019-05-28 09:27
 **/

@Slf4j
public class BaseAggregationLayerGlobalExceptionHandler {
  /**
   * 处理运行时系统异常（反500错误码）
   */
  protected Result<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
    log.error("handleRuntimeException start, uri:{}, caused by: ", request.getRequestURI(), e);
    //TODO 可通过邮件、微信公众号等方式发送信息至开发人员、记录存档等操作
    return Result.error(CodeMsg.SERVER_ERROR);
  }
}
