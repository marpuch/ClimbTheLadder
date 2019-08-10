package ski.puchal.ctl.add.boundary;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author Marek Puchalski, Capgemini
 */
@Data
public class AddLadderBean {
    @NotBlank
    @Size(max = 70)
    private String name;

    @Min(1563733571431L)
    @Max(Long.MAX_VALUE)
    private long timestamp;

    private String level2Ladders;
}
