package ski.puchal.ctl.add;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class AddLadderBean {
    @NotBlank
    private String name;
    private long timestamp;
}
