//package com.security.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
//
//// 配置授权中心配置信息
//@Configuration
//@EnableAuthorizationServer // 开启认证授权中心
//public class MyAuthorizationServerConfigurerAdapter extends AuthorizationServerConfigurerAdapter {
//	// accessToken有效期
//	private int accessTokenValiditySeconds = 7200; // 两小时
//
//	// 添加商户信息
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		// withClient appid申请获取appId和appKey(这里先写死)
//		clients.inMemory().withClient("client_1").secret(passwordEncoder().encode("123456"))
//			.authorizedGrantTypes("password","client_credentials","refresh_token").scopes("all").accessTokenValiditySeconds(accessTokenValiditySeconds);
//	}
//
//	// 设置token类型
//	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//		endpoints.authenticationManager(authenticationManager()).allowedTokenEndpointRequestMethods(HttpMethod.GET,
//			HttpMethod.POST);
//	}
//
//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
//		// 允许表单认证
//		oauthServer.allowFormAuthenticationForClients();
//		// 允许check_token访问
//		oauthServer.checkTokenAccess("permitAll()");
//	}
//
//	@Bean
//	AuthenticationManager authenticationManager() {
//		AuthenticationManager authenticationManager = new AuthenticationManager() {
//
//			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//				return daoAuhthenticationProvider().authenticate(authentication);
//			}
//		};
//		return authenticationManager;
//	}
//
//	@Bean
//	public AuthenticationProvider daoAuhthenticationProvider() {
//		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
//		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
//		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//		return daoAuthenticationProvider;
//	}
//
//	// 设置添加用户信息,正常应该从数据库中读取
//	@Bean
//	UserDetailsService userDetailsService() {
//		InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
//		userDetailsService.createUser(User.withUsername("user_1").password(passwordEncoder().encode("123456"))
//			.authorities("ROLE_USER").build());
//		userDetailsService.createUser(User.withUsername("user_2").password(passwordEncoder().encode("1234567"))
//			.authorities("ROLE_USER").build());
//		return userDetailsService;
//	}
//
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		// 加密方式
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		return passwordEncoder;
//	}
//}
