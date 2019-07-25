package ski.puchal.ctl.add.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ski.puchal.ctl.ladder.boundary.PersistentLadderData;
import ski.puchal.ctl.ladder.boundary.ResultBean;

/**
 * @author Marek Puchalski, Capgemini
 */
@RestController
public class QueryLadderCtl {

    private final PersistentLadderData persistentLadderData;

    @Autowired
    public QueryLadderCtl(final PersistentLadderData persistentLadderData) {
        this.persistentLadderData = persistentLadderData;
    }

    @GetMapping(value = "/query")
    public ResponseEntity<ResultBean> query() {
        return ResponseEntity.ok(persistentLadderData.getTopPlayers());
    }
}
