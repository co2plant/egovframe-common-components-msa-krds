package egovframework.com.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthorizeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String secretCodeId = request.getHeader("X-CODE-ID");

        if (!request.getRequestURI().startsWith("/mip/profile") &&
                !request.getRequestURI().startsWith("/mip/vp")
        ) {
            String secretCode = "-WzAnecnlNewSEQwDgJ2BQ";
            if (ObjectUtils.isEmpty(secretCodeId) || !secretCode.equals(secretCodeId)) {
                log.warn("##### Access Denied: Unauthorized Access Attempt");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                String errorPage = "/error/403.html";
                response.sendRedirect(request.getContextPath() + errorPage);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|woff|woff2|ttf|otf|eot|ico|html)$");
    }

}
