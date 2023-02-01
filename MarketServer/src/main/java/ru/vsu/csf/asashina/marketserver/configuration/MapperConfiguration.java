package ru.vsu.csf.asashina.marketserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vsu.csf.asashina.marketserver.mapper.CategoryMapper;
import ru.vsu.csf.asashina.marketserver.mapper.ProductMapper;
import ru.vsu.csf.asashina.marketserver.mapper.RoleMapper;
import ru.vsu.csf.asashina.marketserver.mapper.UserMapper;

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
}
