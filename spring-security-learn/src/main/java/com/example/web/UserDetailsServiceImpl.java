package com.example.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 自定义数据库查询过程(从数据库中查询用户名密码)，返回User对象
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 从数据库中根据用户名查询用户
        QueryWrapper<com.example.web.User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        com.example.web.User user = userMapper.selectOne(wrapper);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("找不到用户");
        }

        // 角色前面需要加 'ROLE_' 字符串
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("read,ROLE_teacher");
        // 返回从数据库中查出来的用户名密码
        return new User(user.getUsername(), encoder.encode(user.getPassword()), authorities);
    }
}
