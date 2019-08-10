package ski.puchal.ctl;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ExecutionTimeFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeFilter.class);

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        final long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        final long stop = System.currentTimeMillis();
        LOGGER.info("Execution of {} took {} ms", ((HttpServletRequest)request).getRequestURL(), stop-start);
    }
}