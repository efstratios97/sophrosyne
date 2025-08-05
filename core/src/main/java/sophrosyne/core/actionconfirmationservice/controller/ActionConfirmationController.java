package sophrosyne.core.actionconfirmationservice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionconfirmationservice.service.ActionConfirmationService;
import sophrosyne_api.core.actionconfirmationservice.api.IntApi;
import sophrosyne_api.core.actionconfirmationservice.model.Confirmation;

@RestController
@Transactional
@jakarta.transaction.Transactional
public class ActionConfirmationController implements IntApi {

  @Autowired ActionConfirmationService actionConfirmationService;

  @Override
  public ResponseEntity<List<Confirmation>> getActionConfirmations() {
    try {
      return ResponseEntity.ok(actionConfirmationService.getConfirmations());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  public ResponseEntity<Void> handleActionConfirmation(Confirmation confirmation) {
    actionConfirmationService.evaluateConfirmationResponse(confirmation);
    return ResponseEntity.ok().build();
  }
}
