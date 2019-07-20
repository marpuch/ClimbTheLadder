package ski.puchal.ctl.add;

import java.util.List;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class ResultBean {
    private List<ListItemBean> shortList;
    private int highlightIndex;

    public ResultBean(final List<ListItemBean> shortList, final int highlightIndex) {
        this.shortList = shortList;
        this.highlightIndex = highlightIndex;
    }
}
