package sophrosyne.core.executorservice.handler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ActionHandler {

  private ConcurrentHashMap<
          String, CompletableFuture<ConcurrentHashMap<String, ConcurrentHashMap<String, Object>>>>
      runningActions = new ConcurrentHashMap<>();
}
