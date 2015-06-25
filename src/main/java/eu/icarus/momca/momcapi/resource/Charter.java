package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.id.CharterId;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends ExistResource {

    @NotNull
    private final CharterId charterId;

    public Charter(@NotNull ExistResource existResource, @NotNull CharterId charterId) {
        super(existResource);
        this.charterId = charterId;
    }

    @NotNull
    public CharterId getCharterId() {
        return charterId;
    }

    @Override
    public String toString() {
        return "Charter{" +
                "charterId=" + charterId +
                "} " + super.toString();
    }

}
