package ski.puchal.ctl.add;

import java.util.Collections;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Marek Puchalski, Capgemini
 */
@RestController
public class AddLadderCtl {

    @PostMapping(value = "/add")
    public ResultBean add(@Valid @RequestBody final AddLadderBean bean) {
        return new ResultBean(Collections.emptyList(), -1);
    }
}
