package darkorg.lightconfig.fabric.platform.services;

import darkorg.lightconfig.common.platform.services.IEventHelper;
import darkorg.lightconfig.fabric.event.FabricClientEvents;
import darkorg.lightconfig.fabric.event.FabricEvents;

public final class FabricEventHelper implements IEventHelper {
    @Override
    public void initListeners() {
        FabricEvents.init();
    }

    @Override
    public void initClientListeners() {
        FabricClientEvents.init();
    }
}
