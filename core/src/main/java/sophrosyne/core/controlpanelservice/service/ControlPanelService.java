package sophrosyne.core.controlpanelservice.service;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.repository.ControlPanelRepository;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;

@Service
public class ControlPanelService {
  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired ControlPanelRepository controlPanelRepository;

  @Autowired ControlPanelDashboardService controlPanelDashboardService;

  @Autowired UserService userService;

  @Autowired @Lazy private ConfigurationService configurationService;

  public ControlPanelDTO createControlPanel(ControlPanel controlPanel) {
    ControlPanelDTO controlPanelDTO =
        controlPanelRepository.save(createControlPanelDTOFromControlPanel(controlPanel));
    controlPanel
        .getAssociatedUsers()
        .forEach(
            username -> {
              if (userService.getUserByUserName(username).isPresent()) {
                UserDTO userDTO = userService.getUserByUserName(username).get();
                userDTO.setControlPanelDTO(controlPanelDTO);
                userService.updateUser(userDTO);
              }
            });

    return controlPanelDTO;
  }

  public ControlPanelDTO updateControlPanel(ControlPanel controlPanel) {
    Optional<ControlPanelDTO> controlPanelDTO = getControlPanelById(controlPanel.getId());
    if (controlPanelDTO.isPresent()) {
      unlinkControlPanelFromUsers(controlPanelDTO.get());
      unlinkControlPanelFromControlPanelDashboard(controlPanelDTO.get());
      ControlPanelDTO controlPanelDTOToUpdate = createControlPanelDTOFromControlPanel(controlPanel);
      controlPanelDTOToUpdate.setId(controlPanelDTO.get().getId());
      controlPanel
          .getAssociatedUsers()
          .forEach(
              username -> {
                if (userService.getUserByUserName(username).isPresent()) {
                  UserDTO userDTO = userService.getUserByUserName(username).get();
                  userDTO.setControlPanelDTO(controlPanelDTOToUpdate);
                  userService.updateUser(userDTO);
                } else if (userService.getUserById(username).isPresent()) {
                  UserDTO userDTO = userService.getUserById(username).get();
                  userDTO.setControlPanelDTO(controlPanelDTOToUpdate);
                  userService.updateUser(userDTO);
                }
              });

      return controlPanelRepository.save(controlPanelDTOToUpdate);
    } else {
      throw new NoSuchElementException("No ControlPanel with id: " + controlPanel.getId());
    }
  }

  public Optional<ControlPanelDTO> getControlPanelById(String controlPanelId) {
    return controlPanelRepository.findById(controlPanelId);
  }

  public List<ControlPanelDTO> getControlPanels() {
    return controlPanelRepository.findAll();
  }

  public void deleteControlPanel(ControlPanelDTO controlPanelDTO) {
    unlinkControlPanelFromUsers(controlPanelDTO);
    unlinkControlPanelFromControlPanelDashboard(controlPanelDTO);
    controlPanelRepository.delete(controlPanelDTO);
  }

  public void deleteAllControlPanels() {
    List<ControlPanelDTO> controlPanelDTOS = getControlPanels();
    try {
      controlPanelDTOS.forEach(this::unlinkControlPanelFromUsers);
    } catch (RuntimeException e) {
      logger.error(e.getMessage());
    }
    try {
      controlPanelDTOS.forEach(this::unlinkControlPanelFromControlPanelDashboard);
    } catch (RuntimeException e) {
      logger.error(e.getMessage());
    }
    controlPanelRepository.deleteAll();
  }

  private void unlinkControlPanelFromUsers(ControlPanelDTO controlPanelDTO) {
    controlPanelDTO
        .getAssociatedUsers()
        .forEach(
            userDTO -> {
              userDTO.setControlPanelDTO(null);
              userService.updateUser(userDTO);
            });
  }

  private void unlinkControlPanelFromControlPanelDashboard(ControlPanelDTO controlPanelDTO) {
    controlPanelDTO.setAssociatedControlPanelDashboards(null);
    controlPanelRepository.save(controlPanelDTO);
  }

  public ControlPanelDTO createControlPanelDTOFromControlPanel(ControlPanel controlPanel) {
    ControlPanelDTO controlPanelDTO = new ControlPanelDTO();
    controlPanelDTO.setId(UUID.randomUUID().toString());
    controlPanelDTO.setName(controlPanel.getName());
    controlPanelDTO.setDescription(controlPanel.getDescription());
    controlPanelDTO.setAssociatedUsers(getAssociatedUsers(controlPanel));
    controlPanelDTO.setAssociatedControlPanelDashboards(
        getAssociatedControlPanelDashboards(controlPanel));
    return controlPanelDTO;
  }

  public ControlPanel mapControlPanelDtoToControlPanel(ControlPanelDTO controlPanelDTO) {
    return new ControlPanel()
        .id(controlPanelDTO.getId())
        .name(controlPanelDTO.getName())
        .description(controlPanelDTO.getDescription())
        .associatedUsers(controlPanelDTO.getAssociatedUsers().stream().map(UserDTO::getId).toList())
        .associatedControlPanelDashboards(
            controlPanelDTO.getAssociatedControlPanelDashboards().stream()
                .map(ControlPanelDashboardDTO::getId)
                .toList());
  }

  private Set<UserDTO> getAssociatedUsers(ControlPanel controlPanel) {
    return new HashSet<>(
        controlPanel.getAssociatedUsers().stream()
            .map(
                username -> {
                  return userService.getUserByUserName(username).isPresent()
                      ? userService.getUserByUserName(username).get()
                      : null;
                })
            .toList());
  }

  private HashSet<ControlPanelDashboardDTO> getAssociatedControlPanelDashboards(
      ControlPanel controlPanel) {
    return new HashSet<ControlPanelDashboardDTO>(
        controlPanel.getAssociatedControlPanelDashboards().stream()
            .map(
                controlPanelDashboardId -> {
                  return controlPanelDashboardService
                          .getControlPanelDashboardDTOById(controlPanelDashboardId)
                          .isPresent()
                      ? controlPanelDashboardService
                          .getControlPanelDashboardDTOById(controlPanelDashboardId)
                          .get()
                      : null;
                })
            .toList());
  }
}
