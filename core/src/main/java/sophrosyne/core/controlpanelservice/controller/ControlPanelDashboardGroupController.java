package sophrosyne.core.controlpanelservice.controller;

import java.util.List;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne_api.core.controlpaneldashboardgroupservice.api.IntApi;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControlPanelDashboardGroupController implements IntApi {

  @Autowired private ControlPanelDashboardGroupService controlPanelDashboardGroupService;

  @Override
  public ResponseEntity<Void> createControlPanelDashboardGroup(
      ControlPanelDashboardGroup controlPanelDashboardGroup) {
    controlPanelDashboardGroupService.createControlPanelDashboardGroup(controlPanelDashboardGroup);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateControlPanelDashboardGroup(
      String id, ControlPanelDashboardGroup controlPanelDashboardGroup) {
    if (controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(id).isPresent()) {
      controlPanelDashboardGroupService.updateControlPanelDashboardGroup(
          controlPanelDashboardGroup);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ControlPanelDashboardGroup> getControlPanelDashboardGroupById(String id) {
    if (controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(id).isPresent()) {
      return ResponseEntity.ok(
          controlPanelDashboardGroupService
              .mapControlPanelDashboardGroupDTOToControlPanelDashboardGroup(
                  controlPanelDashboardGroupService
                      .getControlPanelDashboardGroupDTOById(id)
                      .get()));
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<List<ControlPanelDashboardGroup>> getControlPanelDashboardGroups() {
    return ResponseEntity.ok(
        controlPanelDashboardGroupService.getControlPanelDashboardGroups().stream()
            .map(
                controlPanelDashboardGroupDTO -> {
                  return controlPanelDashboardGroupService
                      .mapControlPanelDashboardGroupDTOToControlPanelDashboardGroup(
                          controlPanelDashboardGroupDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<Void> deleteControlPanelDashboardGroup(String id) {
    if (controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(id).isPresent()) {
      controlPanelDashboardGroupService.deleteControlPanelDashboardGroup(
          controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(id).get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
