package sophrosyne.core.controlpanelservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne_api.core.controlpaneldashboardservice.api.IntApi;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;

import java.util.List;

@RestController
public class ControlPanelDashboardController implements IntApi {

    @Autowired
    private ControlPanelDashboardService
            controlPanelDashboardService;

    @Override
    public ResponseEntity<Void> createControlPanelDashboard(
            ControlPanelDashboard controlPanelDashboard) {
        controlPanelDashboardService.createControlPanelDashboard(controlPanelDashboard);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateControlPanelDashboard(
            String id, ControlPanelDashboard controlPanelDashboard) {
        if (controlPanelDashboardService.getControlPanelDashboardDTOById(id).isPresent()) {
            controlPanelDashboardService.updateControlPanelDashboard(controlPanelDashboard);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ControlPanelDashboard> getControlPanelDashboardById(String id) {
        if (controlPanelDashboardService.getControlPanelDashboardDTOById(id).isPresent()) {
            return ResponseEntity.ok(
                    controlPanelDashboardService.mapControlPanelDashboardDTOToControlPanelDashboard(
                            controlPanelDashboardService.getControlPanelDashboardDTOById(id).get()));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<ControlPanelDashboard>> getControlPanelDashboards() {
        return ResponseEntity.ok(
                controlPanelDashboardService.getControlPanelDashboards().stream()
                        .map(
                                controlPanelDTO -> {
                                    return controlPanelDashboardService
                                            .mapControlPanelDashboardDTOToControlPanelDashboard(controlPanelDTO);
                                })
                        .toList());
    }

    @Override
    public ResponseEntity<Void> deleteControlPanelDashboard(String id) {
        if (controlPanelDashboardService.getControlPanelDashboardDTOById(id).isPresent()) {
            controlPanelDashboardService.deleteControlPanelDashboard(
                    controlPanelDashboardService.getControlPanelDashboardDTOById(id).get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
