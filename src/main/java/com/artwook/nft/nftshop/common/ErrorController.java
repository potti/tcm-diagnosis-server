package com.artwook.nft.nftshop.common;

import javax.servlet.http.HttpServletRequest;

import com.artwook.nft.nftshop.vo.BaseResponseMessage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    /**
     * 默认错误
     */
    private static final String path_default = "/error";

    public String getErrorPath() {
        return path_default;
    }

    @RequestMapping(value = path_default, produces = { MediaType.APPLICATION_JSON_VALUE })
    public BaseResponseMessage<?> doHandleError(HttpServletRequest request, WebRequest webRequest) {
        log.error("ErrorController doHandleError");
        return new BaseResponseMessage<Void>(BaseResponseCode.ERROR_PARAMILLEGAL);
    }
}
