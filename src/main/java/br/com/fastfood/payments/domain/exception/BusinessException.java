package br.com.fastfood.payments.domain.exception;


import br.com.fastfood.payments.infra.enums.ExceptionEnum;

public class BusinessException extends RuntimeException{

    private final ExceptionEnum exceptionEnum;

    public BusinessException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }
}
