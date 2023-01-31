package com.artwook.nft.nftshop.controller.user;

import java.util.Date;

import com.artwook.nft.nftshop.common.BaseResponseCode;
import com.artwook.nft.nftshop.common.BizException;
import com.artwook.nft.nftshop.common.PassToken;
import com.artwook.nft.nftshop.db.model.User;
import com.artwook.nft.nftshop.service.UserService;
import com.artwook.nft.nftshop.vo.BaseResponseMessage;
import com.artwook.nft.nftshop.vo.login.LoginUserRequest;
import com.artwook.nft.nftshop.vo.login.LoginUserVO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Value("${app.token.timeoutdays}")
    private int timeoutdays;

    @PassToken
    @PostMapping("/doLogin")
    public BaseResponseMessage<LoginUserVO> login(@RequestBody LoginUserRequest user) {
        User aUser = userService.verifyPassword(user.getName(), user.getPassword());
        if (aUser == null) {
            throw new BizException(BaseResponseCode.ERROR_NOT_LOGIN);
        }
        String token = getToken(aUser);
        return new BaseResponseMessage<LoginUserVO>(new LoginUserVO(aUser.getName(), token));

    }

    @PassToken
    @PostMapping("/sendVerifyCode")
    public BaseResponseMessage<String> sendVerifyCode(@RequestBody LoginUserRequest user) {
        return new BaseResponseMessage<String>(BaseResponseCode.SUCCESS_CODE.getValue());
    }

    private String getToken(User user) {
        String token = JWT.create().withAudience(user.getId() + "")
                .withExpiresAt(new Date(System.currentTimeMillis() + timeoutdays * 24L * 3600L * 1000L))
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}
