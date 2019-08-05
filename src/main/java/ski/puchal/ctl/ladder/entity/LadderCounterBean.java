package ski.puchal.ctl.ladder.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ski.puchal.ctl.ladder.boundary.Level;

/**
 * @author Marek Puchalski, Capgemini
 */
@Getter
@ToString
@EqualsAndHashCode
public class LadderCounterBean implements Comparable<LadderCounterBean>, Serializable {
    private final String name;
    private final Level level;
    private final long ladderCount;
    private final long timestamp;

    public LadderCounterBean(final String name, final Level level, final long ladderCount, final long timestamp) {
        this.name = name;
        this.level = level;
        this.ladderCount = ladderCount;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(final LadderCounterBean o) {
        return name.compareTo(o.name);
    }
}
