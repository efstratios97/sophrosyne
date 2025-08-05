package sophrosyne.core.configurationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.repository.ActionRecommendationRepository;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.repository.ActionRepository;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.repository.ApikeyRepository;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.repository.ControlPanelDashboardGroupRepository;
import sophrosyne.core.controlpanelservice.repository.ControlPanelDashboardRepository;
import sophrosyne.core.controlpanelservice.repository.ControlPanelRepository;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.repository.DynamicActionRepository;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.globalservices.utilservice.UtilService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.repository.UserServiceRepository;
import sophrosyne.core.userservice.service.UserService;

@Component
public class ConfigurationService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Value("${init.config.win.path}")
  private String WINDOWS_CONFIG_FILE_PATH;

  @Value("${init.config.lin.path}")
  private String LINUX_CONFIG_FILE_PATH;

  @Autowired private ApikeyService apikeyService;
  @Autowired private ApikeyRepository apikeyRepository;
  @Autowired private ActionService actionService;
  @Autowired private ActionRepository actionRepository;
  @Autowired private DynamicActionService dynamicActionService;
  @Autowired private DynamicActionRepository dynamicActionRepository;
  @Autowired private ActionRecommendationService actionRecommendationService;
  @Autowired private ActionRecommendationRepository actionRecommendationRepository;
  @Autowired private UserService userService;
  @Autowired private UserServiceRepository userServiceRepository;
  @Autowired private ControlPanelService controlPanelService;
  @Autowired private ControlPanelRepository controlPanelRepository;
  @Autowired private ControlPanelDashboardService controlPanelDashboardService;
  @Autowired private ControlPanelDashboardRepository controlPanelDashboardRepository;
  @Autowired private ControlPanelDashboardGroupService controlPanelDashboardGroupService;
  @Autowired private ControlPanelDashboardGroupRepository controlPanelDashboardGroupRepository;
  @Autowired private UtilService utilService;

  @PostConstruct
  public ConfigurationStatus importSophrosyneConfigurationFromFile() {
    ConfigurationStatus configurationStatus = new ConfigurationStatus();
    Optional<JsonNode> sophrosyneConfigJSON = readInitConfig();
    if (sophrosyneConfigJSON.isPresent()) {
      configurationStatus = configureSophrosyne(sophrosyneConfigJSON.get());
    } else {
      configurationStatus.addError(
          new ConfigurationStatus.ConfigurationError("Could not read sophrosyne.json"));
    }
    return configurationStatus;
  }

  public ConfigurationStatus importSophrosyneConfigurationFromFileForce() {
    deleteSophrosyne();
    return importSophrosyneConfigurationFromFile();
  }

  public ConfigurationStatus importSophrosyneConfiguration(Object sophrosyneConfigObject) {
    ConfigurationStatus configurationStatus = new ConfigurationStatus();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode sophrosyneConfigJSON = objectMapper.valueToTree(sophrosyneConfigObject);
      configurationStatus = configureSophrosyne(sophrosyneConfigJSON);
    } catch (Exception e) {
      configurationStatus.addError(
          new ConfigurationStatus.ConfigurationError(
              "Passed Object/Payload is not JSON serializable:" + e.getMessage()));
    }
    return configurationStatus;
  }

  public ConfigurationStatus importSophrosyneConfigurationForce(Object sophrosyneConfigObject) {
    deleteSophrosyne();
    return importSophrosyneConfiguration(sophrosyneConfigObject);
  }

  private ConfigurationStatus configureSophrosyne(JsonNode sophrosyneConfigJSON) {
    ConfigurationStatus status = new ConfigurationStatus();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
      UserDTO user = null;

      try {
        JsonNode apikeys = sophrosyneConfigJSON.get("apikeys");
        for (JsonNode apikeyJson : apikeys) {
          try {
            ApikeyDTO apikey = objectMapper.readValue(apikeyJson.asText(), ApikeyDTO.class);
            apikeyRepository.save(apikey);
          } catch (Exception e) {
            logger.error(e.getMessage());
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "Apikey configuration error: " + e.getMessage()));
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

      try {
        JsonNode users = sophrosyneConfigJSON.get("users");
        for (JsonNode userJson : users) {
          try {
            user = objectMapper.readValue(userJson.asText(), UserDTO.class);
            if (!userServiceRepository.findById(user.getId()).isPresent()) {
              userServiceRepository.save(user);
            }
          } catch (Exception e) {
            logger.error(e.getMessage());
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "User configuration error: " + e.getMessage()));
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

      try {
        JsonNode actions = sophrosyneConfigJSON.get("actions");
        for (JsonNode actionJson : actions) {
          try {
            ActionDTO action = objectMapper.readValue(actionJson.asText(), ActionDTO.class);
            actionRepository.save(action);
          } catch (Exception e) {
            logger.error(e.getMessage());
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "Action configuration error: " + e.getMessage()));
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

      try {
        JsonNode dynamicActions = sophrosyneConfigJSON.get("dynamic_actions");
        for (JsonNode dynamicActionJson : dynamicActions) {
          try {
            DynamicActionDTO dynamicAction =
                objectMapper.readValue(dynamicActionJson.asText(), DynamicActionDTO.class);
            dynamicActionRepository.save(dynamicAction);
          } catch (Exception e) {
            logger.error(e.getMessage());
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "DynamicAction configuration error: " + e.getMessage()));
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }

      try {
        JsonNode actionRecommendations = sophrosyneConfigJSON.get("action_recommendations");
        for (JsonNode actionRecommendation : actionRecommendations) {
          try {
            ActionRecommendationDTO actionRecommendationDTO =
                objectMapper.readValue(
                    actionRecommendation.asText(), ActionRecommendationDTO.class);
            actionRecommendationRepository.save(actionRecommendationDTO);
          } catch (Exception e) {
            logger.error(e.getMessage());
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "ActionRecommendation configuration error: " + e.getMessage()));
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
      try {
        JsonNode controlPanels = sophrosyneConfigJSON.get("control_panels");
        for (JsonNode controlPanel : controlPanels) {
          try {
            ControlPanelDTO controlPanelDTO =
                objectMapper.readValue(controlPanel.asText(), ControlPanelDTO.class);
            controlPanelRepository.save(controlPanelDTO);
            for (UserDTO userDTO : controlPanelDTO.getAssociatedUsers()) {
              if (userServiceRepository.findById(userDTO.getId()).isPresent()) {
                try {
                  userDTO.setControlPanelDTO(controlPanelDTO);
                  userServiceRepository.save(userDTO);
                } catch (Exception e) {
                  logger.error(e.getMessage());
                  status.addError(
                      new ConfigurationStatus.ConfigurationError(
                          "Control Panel Error configuration error due to User assignment: "
                              + e.getMessage()));
                }
              }
            }
          } catch (Exception e) {
            status.addError(
                new ConfigurationStatus.ConfigurationError(
                    "Control Panel Error configuration error: " + e.getMessage()));
            logger.error(e.getMessage());
          }
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return status;
  }

  public String getConfigurationDataJSON() {
    String exportDataJSON = "{}";
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      exportDataJSON = objectMapper.writeValueAsString(getConfigurationData());
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage());
    }
    return exportDataJSON;
  }

  public Map<String, List<String>> getConfigurationData() {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    // Apikeys
    List<String> apikeys =
        apikeyService.getApiDTOs().stream()
            .map(
                apikeyDTO -> {
                  try {
                    return ow.writeValueAsString(apikeyDTO);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // Actions
    List<String> actions =
        actionService.getActions().stream()
            .map(
                actionDTO -> {
                  try {
                    return ow.writeValueAsString(actionDTO);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // Dynamic Actions
    List<String> dynamicActions =
        dynamicActionService.getDynamicActions().stream()
            .map(
                dynamicActionDTO -> {
                  try {
                    return ow.writeValueAsString(dynamicActionDTO);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // Recommendation
    List<String> actionRecommendations =
        actionRecommendationService.getActionRecommendations().stream()
            .map(
                actionRecommendationDTO -> {
                  try {
                    return ow.writeValueAsString(actionRecommendationDTO);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // users
    List<String> users =
        userService.getUsers().stream()
            .map(
                user -> {
                  try {
                    return ow.writeValueAsString(user);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // Control Panels
    List<String> controlPanels =
        controlPanelService.getControlPanels().stream()
            .map(
                controlPanel -> {
                  try {
                    return ow.writeValueAsString(controlPanel);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();

    // Control Panel Dashboards
    List<String> controlPanelDashboards =
        controlPanelDashboardService.getControlPanelDashboards().stream()
            .map(
                controlPanelDashboard -> {
                  try {
                    return ow.writeValueAsString(controlPanelDashboard);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();
    // Control Panel Dashboard Groups
    List<String> controlPanelDashboardGroups =
        controlPanelDashboardGroupService.getControlPanelDashboardGroups().stream()
            .map(
                controlPanelDashboardGroup -> {
                  try {
                    return ow.writeValueAsString(controlPanelDashboardGroup);
                  } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                  }
                })
            .toList();

    Map<String, List<String>> dataForExport =
        new HashMap<>() {
          {
            put("apikeys", apikeys);
            put("users", users);
            put("actions", actions);
            put("dynamic_actions", dynamicActions);
            put("action_recommendations", actionRecommendations);
            put("control_panel_dashboard_groups", controlPanelDashboardGroups);
            put("control_panel_dashboards", controlPanelDashboards);
            put("control_panels", controlPanels);
          }
        };
    return dataForExport;
  }

  private Optional<JsonNode> readInitConfig() {
    try {
      String configFilePath = "";
      if (SystemUtils.IS_OS_LINUX) {
        configFilePath = LINUX_CONFIG_FILE_PATH;
      } else if (SystemUtils.IS_OS_WINDOWS) {
        configFilePath = WINDOWS_CONFIG_FILE_PATH;
      }
      ObjectMapper mapper = new ObjectMapper();
      return Optional.of(
          mapper.readTree(
              utilService.convertFileDataToString(utilService.readFile(configFilePath))));
    } catch (Exception ignore) {

    }
    return Optional.empty();
  }

  private void deleteSophrosyne() {
    try {
      actionService.deleteAllActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      actionRecommendationService.deleteAllActionRecommendations();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      controlPanelDashboardGroupService.deleteAllControlPanelDashboardGroups();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      controlPanelDashboardService.deleteAllControlPanelDashboards();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      controlPanelService.deleteAllControlPanels();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      apikeyService.deleteAllApikeys();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      userService.deleteAllUsers();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public ResponseEntity<String> getResponse(ConfigurationStatus configurationStatus) {
    if (configurationStatus.getStatus() == ConfigurationStatus.Status.SUCCESS) {
      return ResponseEntity.ok(configurationStatus.getFormattedStatusForApiResponse());
    }
    return ResponseEntity.status(HttpStatusCode.valueOf(207))
        .body(configurationStatus.getFormattedStatusForApiResponse());
  }

  @AllArgsConstructor
  public static class ConfigurationStatus {
    private List<ConfigurationError> errors;

    public ConfigurationStatus() {
      errors = new ArrayList<>();
    }

    public Status getStatus() {
      return errors.isEmpty() ? Status.SUCCESS : Status.ERROR;
    }

    public void addError(ConfigurationError error) {
      this.errors.add(error);
    }

    public String getFormattedStatusForApiResponse() {
      StringBuilder message = new StringBuilder();
      if (getStatus() == Status.ERROR) {
        for (ConfigurationError error : errors) {
          message.append(error.getMessage()).append("\n");
        }
      } else {
        message.append(new ConfigurationSuccess().getMessage());
      }
      return message.toString();
    }

    public enum Status {
      SUCCESS,
      ERROR
    }

    public static class ConfigurationError extends Error {
      public ConfigurationError(String message) {
        super(message);
      }
    }

    @NoArgsConstructor
    public static class ConfigurationSuccess {
      private static final String MESSAGE = "Successfully configured Sophrosyne";

      public String getMessage() {
        return MESSAGE;
      }
    }
  }
}
