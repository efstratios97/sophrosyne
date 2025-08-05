package sophrosyne.core.executorservice.handler;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ActionHandler {

  private HashMap<String, CompletableFuture<HashMap<String, HashMap<String, Object>>>>
      runningActions = new HashMap<>();
}
