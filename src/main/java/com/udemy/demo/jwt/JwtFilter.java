package com.udemy.demo.jwt;

import com.udemy.demo.configuration.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter{

	public static final String AUTHORIZATION_HEADER = "authorization";
	
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
    MyUserDetailService service;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			String jwt = resolveFilter(request);
			if(StringUtils.hasText(jwt) && !isUrlPermitted(request)) {
				Authentication authentication = jwtUtils.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			
			filterChain.doFilter(request, response);
	}
	

    private boolean isUrlPermitted(HttpServletRequest request) {
        String url = request.getRequestURI();
        if(url.equals("/authenticate") || url.equals("/users")) {
            return true;
        }
        return false;
    }

	private String resolveFilter(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
