package com.artwook.nft.nftshop.common;

import com.artwook.nft.nftshop.vo.BaseResponseMessage;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice("com.artwook")
public class GlobalHandlerExceptionResolver {

	@ResponseBody
	@ExceptionHandler(value = { TypeMismatchException.class })
	public BaseResponseMessage<?> typeMismatchException(TypeMismatchException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	public BaseResponseMessage<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { MissingServletRequestParameterException.class })
	public BaseResponseMessage<?> missingServletRequestParameterException(MissingServletRequestParameterException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public BaseResponseMessage<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("Request parameter format is incorrect error:",
				e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL.getCode(),
				e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
	}

	@ResponseBody
	@ExceptionHandler(value = { HttpMessageNotReadableException.class })
	public BaseResponseMessage<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { ServletRequestBindingException.class })
	public BaseResponseMessage<?> servletRequestBindingException(ServletRequestBindingException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { MissingServletRequestPartException.class })
	public BaseResponseMessage<?> missingServletRequestPartException(MissingServletRequestPartException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public BaseResponseMessage<?> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL.getCode(),
				"The field file exceeds its maximum permitted size");
	}

	@ResponseBody
	@ExceptionHandler(value = { InvalidFormatException.class })
	public BaseResponseMessage<?> invalidFormatException(InvalidFormatException e) {
		log.error("Request parameter format is incorrect error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
	}

	@ResponseBody
	@ExceptionHandler(value = { BizException.class })
	public BaseResponseMessage<?> bizException(BizException e) {
		log.error("BizException error:", e);
		return new BaseResponseMessage<>(e.getCode(), e.getMsg());
	}

	@ResponseBody
	@ExceptionHandler(value = { Exception.class })
	public BaseResponseMessage<?> exception(Exception e) {
		log.error("Exception error:", e);
		return new BaseResponseMessage<Void>(BaseResponseCode.ERROR);
	}
}
