package sophrosyne.core.controlpanelviewservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.controlpanelviewservice.service.ControlPanelViewService;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.controlpanelviewservice.api.IntApi;

@RestController
public class ControlPanelViewController implements IntApi {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ControlPanelViewService controlPanelViewService;

  @Autowired private UserService userService;

  @Override
  public ResponseEntity<Object> getControlPanelForUser(String userName) {

    if (userService.getUserByUserName(userName).isPresent()) {
      try {
        return ResponseEntity.ok(
            controlPanelViewService.createPanelViewByUser(
                userService.getUserByUserName(userName).get()));
      } catch (Exception e) {
        logger.warn("User has no ControlPanels: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.OK);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
