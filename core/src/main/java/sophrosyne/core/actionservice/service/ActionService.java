package sophrosyne.core.actionservice.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.repository.ActionRepository;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne_api.core.actionservice.model.Action;

@Service
public class ActionService {

  @Autowired private ApikeyService apikeyService;

  @Autowired private ActionRepository actionRepository;

  @Autowired @Lazy private ConfigurationService configurationService;

  public ActionDTO createAction(Action action) {
    ActionDTO actionDTO = createActionDTOFromAction(action);
    actionRepository.save(actionDTO);

    return actionDTO;
  }

  public Optional<ActionDTO> getActionDTO(String id) {
    return actionRepository.findById(id);
  }

  public Optional<ActionDTO> getActionDTOByID(String id) {
    return actionRepository.findById(id);
  }

  public List<ActionDTO> getActions() {
    return actionRepository.findAll();
  }

  public void deleteAction(ActionDTO actionDTO) throws NoSuchElementException {
    actionRepository.delete(actionDTO);
  }

  public void deleteAllActions() throws NoSuchElementException {
    actionRepository.deleteAll();
  }

  public void updateAction(String actionnameToUpdate, Action updatedAction)
      throws NoSuchElementException {
    Optional<ActionDTO> actionDTO = getActionDTO(actionnameToUpdate);
    actionRepository.save(mapActionToActionDTO(updatedAction, actionDTO.get()));
  }

  public void updateAction(ActionDTO updatedAction) throws NoSuchElementException {
    actionRepository.save(updatedAction);
  }

  public ActionDTO createActionDTOFromAction(Action action) {
    return ActionDTO.builder()
        .id(UUID.randomUUID().toString())
        .name(action.getName())
        .description(action.getDescription())
        .command(action.getCommand())
        .version(action.getVersion())
        .postExecutionLogFilePath(action.getPostExecutionLogFilePath())
        .allowedApikeys(getAllowedApikeysFromAction(action))
        .requiresConfirmation(action.getRequiresConfirmation())
        .muted(action.getMuted())
        .build();
  }

  public ActionDTO mapActionToActionDTO(Action action, ActionDTO actionDTO) {
    actionDTO.setName(action.getName());
    actionDTO.setDescription(action.getDescription());
    actionDTO.setCommand(action.getCommand());
    actionDTO.setVersion(action.getVersion());
    actionDTO.setAllowedApikeys(getAllowedApikeysFromAction(action));
    actionDTO.setPostExecutionLogFilePath(action.getPostExecutionLogFilePath());
    actionDTO.setRequiresConfirmation(action.getRequiresConfirmation());
    actionDTO.setMuted(action.getMuted());
    return actionDTO;
  }

  public Action mapActionDTOToAction(ActionDTO actionDTO) {
    return new Action()
        .id(actionDTO.getId())
        .name(actionDTO.getName())
        .description(actionDTO.getDescription())
        .command(actionDTO.getCommand())
        .version(actionDTO.getVersion())
        .allowedApikeys(
            actionDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikeyname).toList())
        .postExecutionLogFilePath(actionDTO.getPostExecutionLogFilePath())
        .requiresConfirmation(actionDTO.getRequiresConfirmation())
        .muted(actionDTO.getMuted());
  }

  public sophrosyne_api.core.internalexecutorservice.model.Action
      mapActionDTOToExecutorServiceAction(ActionDTO actionDTO) {
    return new sophrosyne_api.core.internalexecutorservice.model.Action()
        .id(actionDTO.getId())
        .name(actionDTO.getName())
        .description(actionDTO.getDescription())
        .command(actionDTO.getCommand())
        .allowedApikeys(
            getAllowedApikeysFromAction(mapActionDTOToAction(actionDTO)).stream()
                .map(sophrosyne.core.apikeyservice.dto.ApikeyDTO::getApikeyname)
                .toList())
        .version(actionDTO.getVersion())
        .postExecutionLogFilePath(actionDTO.getPostExecutionLogFilePath())
        .requiresConfirmation(actionDTO.getRequiresConfirmation())
        .muted(actionDTO.getMuted());
  }

  private HashSet<ApikeyDTO> getAllowedApikeysFromAction(Action action) {
    return new HashSet<ApikeyDTO>(
        action.getAllowedApikeys().stream()
            .map(
                apikey -> {
                  return apikeyService.getApiDTOByApikeyname(apikey).get();
                })
            .toList());
  }
}
