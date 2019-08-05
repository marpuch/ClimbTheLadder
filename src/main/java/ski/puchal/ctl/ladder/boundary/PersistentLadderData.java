package ski.puchal.ctl.ladder.boundary;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.setblack.airomem.core.Persistent;
import pl.setblack.airomem.core.Query;
import pl.setblack.airomem.core.VoidCommand;
import ski.puchal.ctl.ladder.control.LadderData;

/**
 * @author Marek Puchalski, Capgemini
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PersistentLadderData {

    private static final Path STORE_FOLDER = Paths.get("./ladderData");

    private Persistent<LadderData> persistent;

    @PostConstruct
    public void init() {
        persistent = Persistent.loadOptional(STORE_FOLDER, LadderData::new);
    }

    public void addLadder(final String name, final long timestamp) {
        persistent.execute(new AddLadderLevel1Command(name, timestamp));
    }

    public ResultBean getTopPlayers(final String name) {
        return persistent.query(new GetTopLevelCommand(name));
    }

    public ResultBean getTopPlayers() {
        return persistent.query(new GetTopLevelCommand());
    }

    private static final class AddLadderLevel1Command implements VoidCommand<LadderData> {

        private String name;
        private long timestamp;

        AddLadderLevel1Command(final String name, final long timestamp) {
            this.name = name;
            this.timestamp = timestamp;
        }

        @Override
        public void executeVoid(final LadderData ladderData) {
            ladderData.addLadderLevel1(name, timestamp);
        }
    }

    private static final class GetTopLevelCommand implements Query<LadderData, ResultBean> {

        private String name;

        GetTopLevelCommand(final String name) {
            this.name = name;
        }

        GetTopLevelCommand() {
        }

        @Override
        public ResultBean evaluate(final LadderData ladderData) {
            return ladderData.getTopPlayers(name);
        }
    }

    @PreDestroy
    public void preDestroy() {
        persistent.close();
    }
}
