package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.OAuth2UserBinding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * OAuth2用户绑定数据访问层
 */
@Mapper
public interface OAuth2UserBindingMapper extends BaseMapper<OAuth2UserBinding> {
    
    /**
     * 根据提供商和提供商用户ID查询绑定信息
     * 
     * @param provider 提供商
     * @param providerUserId 提供商用户ID
     * @return 用户绑定信息
     */
    @Select("SELECT * FROM oauth2_user_binding WHERE provider = #{provider} AND provider_user_id = #{providerUserId}")
    OAuth2UserBinding findByProviderAndProviderUserId(@Param("provider") String provider, 
                                                      @Param("providerUserId") String providerUserId);
    
    /**
     * 查询用户所有的OAuth2绑定
     * 
     * @param userId 本地用户ID
     * @return 用户绑定信息
     */
    @Select("SELECT * FROM oauth2_user_binding WHERE user_id = #{userId}")
    OAuth2UserBinding findByUserId(@Param("userId") Long userId);
} 