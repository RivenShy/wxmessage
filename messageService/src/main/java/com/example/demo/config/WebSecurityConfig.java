package com.example.demo.config;

import com.example.demo.controller.CustomerController;
import com.example.demo.handler.MyAccessDeniedHandler;
import com.example.demo.handler.MyAuthenticationEntryPoint;
import com.example.demo.handler.MyLogoutSuccessHandler;
import com.example.demo.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@SuppressWarnings("deprecation")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
//    @Autowired
//    private MyFilterInvocationSecurityMetadataSource myFilterInvocationSecurityMetadataSource;
    @Autowired
    private MyAccessDecisionManager myAccessDecisionManager;
    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
//        auth.userDetailsService(myUserDetailsService)
//                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/sys/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .and().logout().logoutUrl("/sys/logout").logoutSuccessUrl("/login.html").logoutSuccessHandler(myLogoutSuccessHandler)
                // 还需要设置微信回调接口的permit
                .and().authorizeRequests().antMatchers("/login.html", "/login","/wx/**","/userInfo/outhPage",
                "/userInfo/getQRcode", "/userInfo/getMessageInfo/**","/changeBindApply/add", "/userInfo/updateMessageClickTime/**",
                "/messageTemplate/getWarehouseNoticeMessageInfo/**", CustomerController.logoLogicPath + "**","/server/getServerByServerIp/**").permitAll()
//                .anyRequest().mvcMatchers(HttpMethod.POST,"/sys/login").permitAll()

                // myAccessDecisionService.hasPermission为true，任何用户都可以访问
//                .anyRequest().access("@myAccessDecisionService.hasPermission(request, authentication)")
                // 其它所有的请求都需要登录认证
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler).authenticationEntryPoint(myAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionFixation().migrateSession().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .maximumSessions(1).maxSessionsPreventsLogin(false).expiredSessionStrategy(new MyExpiredSessionStrategy());
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), myUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        http.csrf().disable();
//        http.addFilterAt(UserAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);
//        http.csrf().
//        http.csrf().disable().exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}