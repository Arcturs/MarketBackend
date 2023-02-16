package ru.vsu.csf.asashina.marketserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vsu.csf.asashina.marketserver.mapper.*;

@Configuration
public class MapperConfiguration {

    @Bean
    public ProductMapper productMapper() {
        return ProductMapper.INSTANCE;
    }

    @Bean
    public CategoryMapper categoryMapper() {
        return CategoryMapper.INSTANCE;
    }

    @Bean
    public UserMapper userMapper() {
        return UserMapper.INSTANCE;
    }

    @Bean
    public RoleMapper roleMapper() {
        return RoleMapper.INSTANCE;
    }

    @Bean
    public OrderMapper orderMapper() {
        return OrderMapper.INSTANCE;
    }

    @Bean
    public OrderProductMapper orderProductMapper() {
        return OrderProductMapper.INSTANCE;
    }
}
