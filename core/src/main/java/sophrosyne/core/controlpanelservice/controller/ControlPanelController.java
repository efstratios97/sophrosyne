package sophrosyne.core.controlpanelservice.controller;

import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne_api.core.controlpanelservice.api.IntApi;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControlPanelController implements IntApi {

  @Autowired private ControlPanelService controlPanelService;

  @Override
  public ResponseEntity<Void> createControlPanel(ControlPanel controlPanel) {
    controlPanelService.createControlPanel(controlPanel);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateControlPanel(String id, ControlPanel controlPanel) {
    if (controlPanelService.getControlPanelById(id).isPresent()) {
      controlPanelService.updateControlPanel(controlPanel);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<ControlPanel> getControlPanelById(String id) {
    if (controlPanelService.getControlPanelById(id).isPresent()) {
      return ResponseEntity.ok(
          controlPanelService.mapControlPanelDtoToControlPanel(
              controlPanelService.getControlPanelById(id).get()));
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<List<ControlPanel>> getControlPanels() {
    return ResponseEntity.ok(
        controlPanelService.getControlPanels().stream()
            .map(
                controlPanelDTO -> {
                  return controlPanelService.mapControlPanelDtoToControlPanel(controlPanelDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<Void> deleteControlPanel(String id) {
    if (controlPanelService.getControlPanelById(id).isPresent()) {
      controlPanelService.deleteControlPanel(controlPanelService.getControlPanelById(id).get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
