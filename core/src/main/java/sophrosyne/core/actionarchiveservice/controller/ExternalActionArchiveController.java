package sophrosyne.core.actionarchiveservice.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchive;
import sophrosyne_api.core.externalactionarchiveservice.api.ApiApi;

@RestController
public class ExternalActionArchiveController implements ApiApi {
  @Autowired private ActionArchiveService actionArchiveService;

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
}
