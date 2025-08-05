package sophrosyne.core.actionarchiveservice.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne_api.core.actionarchiveservice.api.IntApi;
import sophrosyne_api.core.actionarchiveservice.api.PullApi;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchive;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchiveFiledata;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchiveMetadata;

@RestController
public class ActionArchiveController implements IntApi, PullApi {
  @Autowired private ActionArchiveService actionArchiveService;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return IntApi.super.getRequest();
  }

  @Override
  public ResponseEntity<List<ActionArchive>> getArchivedActions() {
    return ResponseEntity.ok(
        actionArchiveService.getActionArchives().stream()
            .map(
                actionArchiveDTO -> {
                  return actionArchiveService.mapActionArchiveDtoToActionArchive(actionArchiveDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<List<ActionArchive>> getArchivedActionsByActionId(String actionId) {
    return ResponseEntity.ok(
        actionArchiveService.getActionArchivesByActionId(actionId).stream()
            .map(
                actionArchiveDTO -> {
                  return actionArchiveService.mapActionArchiveDtoToActionArchive(actionArchiveDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<ActionArchive> getLastArchivedActionsByActionId(String actionId) {
    Optional<ActionArchiveDTO> actionArchiveDTO =
        actionArchiveService.getLastActionArchiveByActionId(actionId);
    return actionArchiveDTO
        .map(
            archiveDTO ->
                ResponseEntity.ok(
                    actionArchiveService.mapActionArchiveDtoToActionArchive(archiveDTO)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @Override
  public ResponseEntity<ActionArchiveFiledata> getArchivedActionsFiledata(Long id) {
    return actionArchiveService
        .getActionArchiveDTO(id)
        .map(
            (actionArchiveDTO ->
                ResponseEntity.ok(
                    actionArchiveService.mapActionArchiveDTOToActionArchiveFiledata(
                        actionArchiveDTO))))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @Override
  public ResponseEntity<List<ActionArchiveMetadata>> getArchivedActionsMetadata() {
    return ResponseEntity.ok(
        actionArchiveService.getActionArchiveMetadata().stream()
            .map(
                (actionArchiveMetadata) ->
                    actionArchiveService.mapActionArchiveMetadataInterfaceToActionArchiveMetadata(
                        actionArchiveMetadata))
            .toList());
  }

  @Override
  public ResponseEntity<Void> purgeActionArchive() {
    actionArchiveService.purgeCompleteArchive();
    return ResponseEntity.ok().build();
  }
}
