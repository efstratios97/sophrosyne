package sophrosyne.core.actionarchiveservice.repository;

import java.time.LocalDateTime;

public interface ActionArchiveMetadata {

  Long getId();

  String getActionId();

  String getActionName();

  String getActionCommand();

  LocalDateTime getExecutionStartPoint();

  LocalDateTime getExecutionEndPoint();

  int getExitCode();

  String getType();

  String getVersion();
}
