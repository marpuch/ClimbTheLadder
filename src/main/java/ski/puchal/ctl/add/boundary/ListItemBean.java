package ski.puchal.ctl.add.boundary;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class ListItemBean {
    private int position;
    private String name;
    private long timestamp;
    private long ladderCount;
    private Level level;

    public ListItemBean(final int position, final String name, final long timestamp, final long ladderCount,
            final Level level) {
        this.position = position;
        this.name = name;
        this.timestamp = timestamp;
        this.ladderCount = ladderCount;
        this.level = level;
    }
}
