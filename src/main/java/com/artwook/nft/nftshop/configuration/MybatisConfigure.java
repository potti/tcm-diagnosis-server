package com.artwook.nft.nftshop.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = { "classpath:mapper.properties" })
@MapperScan(basePackages = { "com.artwook.nft.*.db.mapper" })
public class MybatisConfigure {

}
