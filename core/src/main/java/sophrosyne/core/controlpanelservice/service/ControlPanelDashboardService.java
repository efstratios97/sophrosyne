package sophrosyne.core.controlpanelservice.service;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.repository.ControlPanelDashboardRepository;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;

@Service
public class ControlPanelDashboardService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired ControlPanelDashboardRepository controlPanelDashboardRepository;

  @Autowired ControlPanelDashboardGroupService controlPanelDashboardGroupService;

  @Autowired @Lazy private ConfigurationService configurationService;

  public ControlPanelDashboardDTO createControlPanelDashboard(
      ControlPanelDashboard controlPanelDashboard) {
    ControlPanelDashboardDTO controlPanelDashboardDTO =
        controlPanelDashboardRepository.save(
            createControlPanelDTOFromControlPanelDashboard(controlPanelDashboard));

    return controlPanelDashboardDTO;
  }

  public ControlPanelDashboardDTO updateControlPanelDashboard(
      ControlPanelDashboard controlPanelDashboard) {
    Optional<ControlPanelDashboardDTO> controlPanelDashboardDTO =
        getControlPanelDashboardDTOById(controlPanelDashboard.getId());
    if (controlPanelDashboardDTO.isPresent()) {
      unlinkControlPanelDashboardFromControlPanelDashboardGroup(controlPanelDashboardDTO.get());
      ControlPanelDashboardDTO controlPanelDashboardDTOToUpdate =
          createControlPanelDTOFromControlPanelDashboard(controlPanelDashboard);
      controlPanelDashboardDTOToUpdate.setId(controlPanelDashboardDTO.get().getId());
      ControlPanelDashboardDTO updatedControlPanelDashboardDTO =
          controlPanelDashboardRepository.save(controlPanelDashboardDTOToUpdate);

      return updatedControlPanelDashboardDTO;
    } else {
      throw new NoSuchElementException(
          "No ControlPanelDashboard with id: " + controlPanelDashboard.getId());
    }
  }

  private void unlinkControlPanelDashboardFromControlPanelDashboardGroup(
      ControlPanelDashboardDTO controlPanelDashboardDTO) {
    controlPanelDashboardDTO.setAssociatedControlPanelGroups(null);
    controlPanelDashboardRepository.save(controlPanelDashboardDTO);
  }

  public Optional<ControlPanelDashboardDTO> getControlPanelDashboardDTOById(
      String controlPanelDashboardId) {
    return controlPanelDashboardRepository.findById(controlPanelDashboardId);
  }

  public List<ControlPanelDashboardDTO> getControlPanelDashboards() {
    return controlPanelDashboardRepository.findAll();
  }

  public void deleteControlPanelDashboard(ControlPanelDashboardDTO controlPanelDashboardDTO) {
    unlinkControlPanelDashboardFromControlPanelDashboardGroup(controlPanelDashboardDTO);
    controlPanelDashboardRepository.delete(controlPanelDashboardDTO);
  }

  public void deleteAllControlPanelDashboards() {
    try {
      getControlPanelDashboards()
          .forEach(this::unlinkControlPanelDashboardFromControlPanelDashboardGroup);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      controlPanelDashboardRepository.deleteAll();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public ControlPanelDashboard mapControlPanelDashboardDTOToControlPanelDashboard(
      ControlPanelDashboardDTO controlPanelDashboardDTO) {
    return new ControlPanelDashboard()
        .id(controlPanelDashboardDTO.getId())
        .name(controlPanelDashboardDTO.getName())
        .description(controlPanelDashboardDTO.getDescription())
        .associatedControlPanelDashboardGroups(
            controlPanelDashboardDTO.getAssociatedControlPanelGroups().stream()
                .map(ControlPanelDashboardGroupDTO::getId)
                .toList())
        .position(controlPanelDashboardDTO.getPosition());
  }

  public ControlPanelDashboardDTO createControlPanelDTOFromControlPanelDashboard(
      ControlPanelDashboard controlPanelDashboard) {
    ControlPanelDashboardDTO controlPanelDashboardDTO = new ControlPanelDashboardDTO();
    controlPanelDashboardDTO.setId(UUID.randomUUID().toString());
    controlPanelDashboardDTO.setName(controlPanelDashboard.getName());
    controlPanelDashboardDTO.setDescription(controlPanelDashboard.getDescription());
    controlPanelDashboardDTO.setAssociatedControlPanelGroups(
        getAssociatedControlPanelGroups(controlPanelDashboard));
    controlPanelDashboardDTO.setPosition(controlPanelDashboard.getPosition());
    return controlPanelDashboardDTO;
  }

  private HashSet<ControlPanelDashboardGroupDTO> getAssociatedControlPanelGroups(
      ControlPanelDashboard controlPanelDashboard) {
    return new HashSet<ControlPanelDashboardGroupDTO>(
        controlPanelDashboard.getAssociatedControlPanelDashboardGroups().stream()
            .map(
                controlPanelDashboardGroupId -> {
                  return controlPanelDashboardGroupService
                          .getControlPanelDashboardGroupDTOById(controlPanelDashboardGroupId)
                          .isPresent()
                      ? controlPanelDashboardGroupService
                          .getControlPanelDashboardGroupDTOById(controlPanelDashboardGroupId)
                          .get()
                      : null;
                })
            .toList());
  }
}
