package sophrosyne.core.actionconfirmationservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionconfirmationservice.service.ActionConfirmationService;
import sophrosyne_api.core.actionconfirmationservice.model.Confirmation;
import sophrosyne_api.core.externalactionconfirmation.api.ApiApi;

@RestController
public class ExternalActionConfirmationController implements ApiApi {

  @Autowired ActionConfirmationService actionConfirmationService;

  @Override
  public ResponseEntity<Confirmation> getActionConfirmationById(String id, String apikey) {
    if (actionConfirmationService.getConfirmationById(id).isPresent()) {
      return ResponseEntity.ok(actionConfirmationService.getConfirmationById(id).get());
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @Override
  public ResponseEntity<List<Confirmation>> getAllActionConfirmations(String apikey) {
    return ResponseEntity.ok(actionConfirmationService.getConfirmations());
  }

  @Override
  public ResponseEntity<Void> confirmAnyAction(String id, String apikey) {
    if (!actionConfirmationService.confirmAction(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> rejectAnyAction(String id, String apikey) {
    if (!actionConfirmationService.rejectAction(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
