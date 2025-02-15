package kr.pe.playdata.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        config.addAllowedOrigin("http://localhost:8081/");
        config.addAllowedOrigin("http://127.0.0.1:8081/");
        config.addAllowedOrigin("http://dobbyvue.s3.ap-northeast-2.amazonaws.com/");
        config.addAllowedOrigin("http://dobbyvue.s3.ap-northeast-2.amazonaws.com:8081/");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
//        config.addExposedHeader(jwtTokenProvider.TOKEN_HAEDER);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.cors().configurationSource(corsConfigurationSource());
		
		http.httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
			.csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증할것이므로 세션필요없으므로 생성안함.
			.and()
				.authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
					.antMatchers("/*/signin", "/*/signup").permitAll() // 가입 및 인증 주소는 누구나 접근가능
					.antMatchers(HttpMethod.GET, "/exception").permitAll() // exception으로 시작하는 GET요청 . 리소스는 누구나 접근가능
					.antMatchers(HttpMethod.GET, "/posts/del").hasRole("manager") // posts/del GET요청 . manager만 접근가능
					.anyRequest().permitAll() // 그 외에는 인증 완료해야 접근가능
			.and()
            	.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
//          .and()	// 포스트맨에서 Exceeded maxRedirects 에러남
//				.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
			.and()
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 넣어라.
	}

//	// ignore check swagger resource
//	@Override
//	public void configure(WebSecurity web) {
//		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");
//	}

}
