package ski.puchal.ctl.ladder.entity;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class AccumulatedLevel2LaddersBean {
    private int ladderCount;
    private long timestamp;

    public AccumulatedLevel2LaddersBean(final int ladderCount, final long timestamp) {
        this.ladderCount = ladderCount;
        this.timestamp = timestamp;
    }
}
