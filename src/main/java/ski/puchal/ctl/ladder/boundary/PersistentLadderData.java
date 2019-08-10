package ski.puchal.ctl.ladder.boundary;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.setblack.airomem.core.Persistent;
import pl.setblack.airomem.core.Query;
import pl.setblack.airomem.core.VoidCommand;
import ski.puchal.ctl.ladder.control.LadderData;
import ski.puchal.ctl.ladder.entity.AccumulatedLevel2LaddersBean;

/**
 * @author Marek Puchalski, Capgemini
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PersistentLadderData {

    private static final Path STORE_FOLDER = Paths.get("./ladderData");

    @Value("${business.time_till_add_possible_milisec}")
    private long timeTillAddPossibleMilisec;

    @Value("${business.short_list_size}")
    private int shortListSize;

    private Persistent<LadderData> persistent;
    private Level2LadderManager level2LadderManager;

    @Autowired
    public PersistentLadderData(final Level2LadderManager level2LadderManager) {
        this.level2LadderManager = level2LadderManager;
    }

    @PostConstruct
    public void init() {
        persistent = Persistent.loadOptional(STORE_FOLDER, () ->
                new LadderData(timeTillAddPossibleMilisec, shortListSize));
    }

    public void addLadder(final String name, final long timestamp, final String level2Ladders) {
        if (isLevel2Player(name)) {
            final AccumulatedLevel2LaddersBean bean = level2LadderManager.deserialize(level2Ladders);
            level2LadderManager.validate(bean);
            level2LadderManager.consume();
            persistent.execute(new AddLadderCommand(name, timestamp, bean));
        } else {
            persistent.execute(new AddLadderCommand(name, timestamp, null));
        }
    }

    private boolean isLevel2Player(final String name) {
        return persistent.query(new IsLevel2PlayerCommand(name));
    }

    public ResultBean getTopPlayers(final String name) {
        final ResultBean result =  persistent.query(new GetTopLevelCommand(name));
        return addLevel2Data(addPlayersData(result));
    }

    private ResultBean addPlayersData(final ResultBean result) {
        result.setPlayersCount(persistent.query(new GetPlayersCountCommand()));
        return result;
    }

    private ResultBean addLevel2Data(final ResultBean result) {
        final AccumulatedLevel2LaddersBean bean = level2LadderManager.generate();
        result.setLevel2LadderCount(bean.getLadderCount());
        result.setLevel2LadderPayload(level2LadderManager.serialize(bean));
        return result;
    }

    public ResultBean getTopPlayers() {
        final ResultBean result = persistent.query(new GetTopLevelCommand());
        return addLevel2Data(addPlayersData(result));
    }

    private static final class AddLadderCommand implements VoidCommand<LadderData> {

        private String name;
        private long timestamp;
        private AccumulatedLevel2LaddersBean level2Ladders;

        AddLadderCommand(final String name, final long timestamp,
                final AccumulatedLevel2LaddersBean level2Ladders) {
            this.name = name;
            this.timestamp = timestamp;
            this.level2Ladders = level2Ladders;
        }

        @Override
        public void executeVoid(final LadderData ladderData) {
            ladderData.addLadder(name, timestamp, level2Ladders);
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

    private static final class IsLevel2PlayerCommand implements Query<LadderData, Boolean> {

        private String name;

        IsLevel2PlayerCommand(final String name) {
            this.name = name;
        }

        @Override
        public Boolean evaluate(final LadderData ladderData) {
            return ladderData.isLevel2Player(name);
        }
    }

    private static final class GetPlayersCountCommand implements Query<LadderData, Integer> {

        @Override
        public Integer evaluate(final LadderData ladderData) {
            return ladderData.getPlayersCount();
        }
    }

    @PreDestroy
    public void preDestroy() {
        persistent.close();
    }
}
