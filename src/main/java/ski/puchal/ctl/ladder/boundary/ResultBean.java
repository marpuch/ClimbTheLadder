package ski.puchal.ctl.ladder.boundary;

import java.util.List;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class ResultBean {
    private String errorMessage;
    private List<ListItemBean> shortList;
    private int highlightIndex;
    private int level2LadderCount;
    private String level2LadderPayload;

    public ResultBean(final List<ListItemBean> shortList, final int highlightIndex) {
        this.shortList = shortList;
        this.highlightIndex = highlightIndex;
    }

    public ResultBean(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
