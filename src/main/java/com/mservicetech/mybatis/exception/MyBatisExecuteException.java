
package com.mservicetech.mybatis.exception;




public class MyBatisExecuteException extends RuntimeException {

  private static final long serialVersionUID = -3284728621670758988L;

  public MyBatisExecuteException(String msg) {
    super(msg);
  }

  public MyBatisExecuteException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public MyBatisExecuteException(Throwable cause) {
    super(null, cause);
  }

}
