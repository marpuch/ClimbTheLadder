package ski.puchal.ctl.ladder.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ski.puchal.ctl.add.boundary.Level;
import ski.puchal.ctl.add.boundary.ListItemBean;
import ski.puchal.ctl.add.boundary.ResultBean;

/**
 * @author Marek Puchalski, Capgemini
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LadderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LadderManager.class);

    private static final long TIME_TILL_ADD_POSSIBLE_MILISEC = 60 * 60 * 1000;
    private static final int SHORT_LIST_SIZE = 30;

    private final Map<String, LadderCounterBean> map = new HashMap<>();
    private List<List<LadderCounterBean>> list = new ArrayList<>();

    public synchronized void addLadderLevel1(final String name, final long timestamp) {
        final LadderCounterBean ctr = map.get(name);
        if (ctr == null) {
            map.put(name, new LadderCounterBean(name, Level.LEVEL1, 1, timestamp));
            recalculateList();
            LOGGER.info("New user added {}, ladder count {}", name, 1);
        } else if (!Level.LEVEL1.equals(ctr.getLevel())) {
            throw new LadderException("User is not playing level 1 any more");
        } else if (ctr.getTimestamp() + TIME_TILL_ADD_POSSIBLE_MILISEC > timestamp) {
            final long nextAddPossibleSec = (ctr.getTimestamp() + TIME_TILL_ADD_POSSIBLE_MILISEC - timestamp) / 1000;
            throw new LadderException("You can add a ladder again after " + nextAddPossibleSec + " seconds");
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
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .peek(Collections::sort)
                .collect(Collectors.toList());
    }

    public synchronized ResultBean getTopPlayers(final String name) {
        final List<ListItemBean> resultList = new ArrayList<>(SHORT_LIST_SIZE + 1);

        for (int i = 0; i < list.size() && resultList.size() < SHORT_LIST_SIZE; i++) {
            for (final LadderCounterBean bean : list.get(i)) {
                if (resultList.size() < SHORT_LIST_SIZE) {
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
                    resultList.add(new ListItemBean(i+1, bean.getName(),
                            bean.getTimestamp(), bean.getLadderCount(), bean.getLevel()));
                    return new ResultBean(resultList, resultList.size() - 1);
                }
            }
        }

        LOGGER.error("For some reason the name {} could not be found despite the map saying it should", name);

        throw new LadderException("Something went wrong. Look into the logs for more info.");
    }
}
