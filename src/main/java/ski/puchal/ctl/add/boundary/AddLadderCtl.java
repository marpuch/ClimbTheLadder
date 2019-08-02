package ski.puchal.ctl.add.boundary;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ski.puchal.ctl.ladder.boundary.PersistentLadderData;
import ski.puchal.ctl.ladder.boundary.ResultBean;

/**
 * @author Marek Puchalski, Capgemini
 */
@RestController
public class AddLadderCtl {

    private final PersistentLadderData persistentLadderData;

    @Autowired
    public AddLadderCtl(final PersistentLadderData persistentLadderData) {
        this.persistentLadderData = persistentLadderData;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<ResultBean> add(@Valid @RequestBody final AddLadderBean bean) {
        try {
            persistentLadderData.addLadderLevel1(bean.getName(), bean.getTimestamp());
        } catch (final Exception e) {
            final ResultBean result = persistentLadderData.getTopPlayers();
            if (e.getCause() != null) {
                result.setErrorMessage(e.getCause().getMessage());
            } else {
                result.setErrorMessage(e.getMessage());
            }
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(persistentLadderData.getTopPlayers(bean.getName()));
    }


    @Bean
    public FilterRegistrationBean<SlowDownFilter> loggingFilter(){
        FilterRegistrationBean<SlowDownFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(new SlowDownFilter());
        filterRegistrationBean.addUrlPatterns("/add/*");

        return filterRegistrationBean;
    }
}
