package br.com.fastfood.payments.domain.exception;


import br.com.fastfood.payments.infra.enums.ExceptionEnum;

public class BusinessException extends RuntimeException{
    public BusinessException(ExceptionEnum message, Object... params) {
        super(String.format(message.getMessage(), params));
    }
    public BusinessException(ExceptionEnum message) {
        super(message.getMessage());
    }
}
