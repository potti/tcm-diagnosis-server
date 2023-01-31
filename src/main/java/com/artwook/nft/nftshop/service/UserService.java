package com.artwook.nft.nftshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.artwook.nft.nftshop.db.mapper.UserMapper;
import com.artwook.nft.nftshop.db.model.User;
import com.artwook.nft.nftshop.db.model.UserExample;
import com.artwook.nft.nftshop.utils.ShaUtils;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
@Service
public class UserService {

    private UserMapper userMapper;

    public User getUserByUUID(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public User newUser(String name, String password) {
        User aUser = new User();
        aUser.setName(name);
        aUser.setPassword(password);
        userMapper.insertSelective(aUser);
        return aUser;
    }

    public User verifyPassword(String name, String password) {
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(name).andPasswordEqualTo(ShaUtils.sha256(password));
        log.info("password: {} {} {}", name, password, ShaUtils.sha256(password));
        List<User> list = userMapper.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }
}
