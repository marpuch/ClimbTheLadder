package ski.puchal.ctl.ladder.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ski.puchal.ctl.ladder.boundary.LadderException;
import ski.puchal.ctl.ladder.boundary.Level;
import ski.puchal.ctl.ladder.boundary.ListItemBean;
import ski.puchal.ctl.ladder.boundary.ResultBean;
import ski.puchal.ctl.ladder.entity.AccumulatedLevel2LaddersBean;
import ski.puchal.ctl.ladder.entity.LadderCounterBean;

/**
 * @author Marek Puchalski, Capgemini
 */
public class LadderData implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LadderData.class);

    private final long timeTillAddPossibleMilisec;

    private final int shortListSize;

    private final Map<String, LadderCounterBean> map = new HashMap<>();
    private List<List<LadderCounterBean>> list = new ArrayList<>();

    public LadderData(final long timeTillAddPossibleMilisec, final int shortListSize) {
        this.timeTillAddPossibleMilisec = timeTillAddPossibleMilisec;
        this.shortListSize = shortListSize;
    }

    public synchronized void addLadder(final String name, final long timestamp,
            final AccumulatedLevel2LaddersBean level2Ladders) {
        final LadderCounterBean ctr = map.get(name);
        if (ctr == null) {
            map.put(name, new LadderCounterBean(name, Level.LEVEL1, 1, timestamp));
            recalculateList();
            LOGGER.info("New user added {}, ladder count {}", name, 1);
        } else if (Level.LEVEL2.equals(ctr.getLevel())) {
            playLevel2(name, level2Ladders, ctr);
        } else {
            playLevel1(name, timestamp, ctr);
        }
    }

    private void playLevel2(final String name, final AccumulatedLevel2LaddersBean level2Ladders,
            final LadderCounterBean ctr) {
        map.put(name, new LadderCounterBean(ctr.getName(), ctr.getLevel(),
                ctr.getLadderCount() + level2Ladders.getLadderCount(), System.currentTimeMillis()));
        recalculateList();
    }

    private void playLevel1(final String name, final long timestamp, final LadderCounterBean ctr) {
        if (ctr.getTimestamp() + timeTillAddPossibleMilisec > timestamp) {
            final long nextAddPossibleSec = (ctr.getTimestamp() + timeTillAddPossibleMilisec - timestamp) / 1000;
            if (nextAddPossibleSec < 60) {
                throw new LadderException("You can add a ladder again after " + nextAddPossibleSec + " seconds");
            } else {
                throw new LadderException("You can add a ladder again after " + nextAddPossibleSec/60 + " minutes");
            }
        } else if (ctr.getLadderCount() == 9) {
            // promote to level 2
            map.put(name, new LadderCounterBean(name, Level.LEVEL2, ctr.getLadderCount() + 1, timestamp));
            recalculateList();
            LOGGER.info("User {} promoted to level 2 with {} ladders", name, ctr.getLadderCount() + 1);
        } else {
            map.put(name, new LadderCounterBean(name, Level.LEVEL1, ctr.getLadderCount() + 1, timestamp));
            recalculateList();
            LOGGER.info("Added ladder to user {}. Ladder count is {}", name, ctr.getLadderCount() + 1);
        }
    }

    private synchronized void recalculateList() {
        final Map<Long, List<LadderCounterBean>> groupedValues = map.values().stream()
                .collect(Collectors.groupingBy(LadderCounterBean::getLadderCount));
        list = groupedValues.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(Map.Entry::getKey)))
                .map(Map.Entry::getValue)
                .peek(Collections::sort)
                .collect(Collectors.toList());
    }

    public synchronized ResultBean getTopPlayers(final String name) {
        final List<ListItemBean> resultList = new ArrayList<>(shortListSize + 1);

        for (int i = 0; i < list.size() && resultList.size() < shortListSize; i++) {
            for (final LadderCounterBean bean : list.get(i)) {
                if (resultList.size() < shortListSize) {
                    resultList.add(new ListItemBean(i+1, bean.getName(),
                            bean.getTimestamp(), bean.getLadderCount(), bean.getLevel()));
                } else {
                    break;
                }
            }
        }

        if (name == null || ! map.containsKey(name)) {
            return new ResultBean(resultList, -1);
        }

        // check if player already on the list
        for (int i = 0; i < resultList.size(); i++) {
            if (name.equals(resultList.get(i).getName())) {
                return new ResultBean(resultList, i);
            }
        }

        // if not, find the player and add him to the list
        for (int i = 0; i < list.size(); i++) {
            for (final LadderCounterBean bean : list.get(i)) {
                if (name.equals(bean.getName())) {
                    // separator line
                    resultList.add(new ListItemBean(-1, "---", -1, -1,
                            Level.LEVEL2));
                    resultList.add(new ListItemBean(i+1, bean.getName(),
                            bean.getTimestamp(), bean.getLadderCount(), bean.getLevel()));
                    return new ResultBean(resultList, resultList.size() - 1);
                }
            }
        }

        LOGGER.error("For some reason the name {} could not be found despite the map saying it should", name);

        throw new LadderException("Something went wrong. Look into the logs for more info.");
    }

    public synchronized Boolean isLevel2Player(final String name) {
        final LadderCounterBean ctr = map.get(name);
        return ctr != null && Level.LEVEL2.equals(ctr.getLevel());
    }
}
