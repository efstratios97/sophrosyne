package sophrosyne.core.globalservices.exceptionservice;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @PostConstruct
  public void init() {
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    logger.error(e.getMessage());

    if (e instanceof IllegalStateException) {
      // Suppress the exception
      logger.error("An error occurred", e);
      System.err.println("Suppressed IllegalStateException: " + e.getMessage());
      // Optionally, log or perform other actions
    } else {
      // If it's not IllegalStateException, handle it differently or re-throw
      throw new RuntimeException(e);
    }
  }
}
