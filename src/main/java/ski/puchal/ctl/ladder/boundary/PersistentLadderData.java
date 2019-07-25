package ski.puchal.ctl.ladder.boundary;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.setblack.airomem.core.Persistent;
import pl.setblack.airomem.core.VoidCommand;
import ski.puchal.ctl.ladder.control.LadderData;

/**
 * @author Marek Puchalski, Capgemini
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PersistentLadderData {

    private final static Path STORE_FOLDER = Paths.get("./ladderData");

    private Persistent<LadderData> persistent;

    private LadderData ladderData;

    @Autowired
    public PersistentLadderData(final LadderData ladderData) {
        this.ladderData = ladderData;
    }

    @PostConstruct
    public void init() {
        persistent = Persistent.loadOptional(STORE_FOLDER, () -> ladderData);
    }

    public void addLadderLevel1(final String name, final long timestamp) {
        persistent.execute(new AddLadderLevel1Command(name, timestamp));
    }

    public ResultBean getTopPlayers(final String name) {
        return ladderData.getTopPlayers(name);
    }

    public ResultBean getTopPlayers() {
        return ladderData.getTopPlayers();
    }

    private static final class AddLadderLevel1Command implements VoidCommand<LadderData> {

        private String name;
        private long timestamp;

        public AddLadderLevel1Command(final String name, final long timestamp) {
            this.name = name;
            this.timestamp = timestamp;
        }

        @Override
        public void executeVoid(final LadderData ladderData) {
            ladderData.addLadderLevel1(name, timestamp);
        }
    }
}
