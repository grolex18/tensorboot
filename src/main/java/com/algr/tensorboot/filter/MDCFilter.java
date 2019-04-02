package com.algr.tensorboot.filter;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Filter for adding context to logs
 */
@Component
public class MDCFilter implements javax.servlet.Filter {
    private static final String CONTEXT = "ctx";

    private Random random;

    @Override
    public void init(FilterConfig filterConfig) {
        random = new SecureRandom();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.put(CONTEXT, HexUtils.toHexString(BigInteger.valueOf(random.nextInt()).toByteArray()));
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void destroy() {
    }
}
