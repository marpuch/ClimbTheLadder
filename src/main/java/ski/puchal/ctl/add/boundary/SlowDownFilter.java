package ski.puchal.ctl.add.boundary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;

@Order(1)
public class SlowDownFilter implements Filter {

    private static final long FRAME_SIZE = 2000;
    private Map<String, Long> map = new HashMap<>();

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        final String ip = request.getRemoteAddr();
        if (map.containsKey(ip)) {
            if (System.currentTimeMillis() > map.get(ip) + FRAME_SIZE) {
                updateMapAndForwardRequest(request, response, chain, ip);
            } else {
                final HttpServletResponse r = (HttpServletResponse) response;
                r.sendError(429, "Slow down, you're moving to fast..."); // rate limiting
            }
        } else {
            updateMapAndForwardRequest(request, response, chain, ip);
        }
    }

    private void updateMapAndForwardRequest(final ServletRequest request, final ServletResponse response,
            final FilterChain chain, final String ip) throws IOException, ServletException {
        map.put(ip, System.currentTimeMillis());
        chain.doFilter(request, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) {
        // nothing
    }

    @Override
    public void destroy() {
        // nothing
    }
}
