package sophrosyne.core.broadcastservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SophrosyneBroadcaster {

  private final Logger logger = LogManager.getLogger(getClass());

  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public SophrosyneBroadcaster(SimpMessagingTemplate messagingTemplate) {

    this.messagingTemplate = messagingTemplate;
  }

  public void sendText(String dynamicDestination, String text) {
    try {
      Thread.sleep(0, 1000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    messagingTemplate.convertAndSend("/topic/sophrosyne/" + dynamicDestination, text);
  }
}
