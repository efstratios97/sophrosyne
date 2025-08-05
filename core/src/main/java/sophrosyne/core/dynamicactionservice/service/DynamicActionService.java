package sophrosyne.core.dynamicactionservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.repository.DynamicActionRepository;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@Service
public class DynamicActionService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ApikeyService apikeyService;

  @Autowired private DynamicActionRepository dynamicActionRepository;

  @Autowired @Lazy private ConfigurationService configurationService;

  public DynamicActionDTO createDynamicAction(DynamicAction action) {
    DynamicActionDTO dynamicActionDTO = createDynamicActionDTOFromDynamicAction(action);
    dynamicActionRepository.save(dynamicActionDTO);

    return dynamicActionDTO;
  }

  public Optional<DynamicActionDTO> getDynamicActionDTO(String id) {
    return dynamicActionRepository.findById(id);
  }

  public Optional<DynamicActionDTO> getDynamicActionDTOByID(String id) {
    return dynamicActionRepository.findById(id);
  }

  public List<DynamicActionDTO> getDynamicActions() {
    return dynamicActionRepository.findAll();
  }

  public void deleteDynamicAction(DynamicActionDTO dynamicActionDTO) throws NoSuchElementException {
    dynamicActionRepository.delete(dynamicActionDTO);
  }

  public void deleteAllDynamicActions() throws NoSuchElementException {
    dynamicActionRepository.deleteAll();
  }

  public void updateDynamicAction(String actionnameToUpdate, DynamicAction updatedDynamicAction)
      throws NoSuchElementException {
    Optional<DynamicActionDTO> dynamicActionDTO = getDynamicActionDTO(actionnameToUpdate);
    dynamicActionRepository.save(
        mapDynamicActionToDynamicActionDTO(updatedDynamicAction, dynamicActionDTO.get()));
  }

  public void updateDynamicAction(DynamicActionDTO updatedDynamicAction)
      throws NoSuchElementException {
    dynamicActionRepository.save(updatedDynamicAction);
  }

  public List<String> getParsedDynamicParameters(DynamicActionDTO dynamicActionDTO) {
    Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
    Matcher matcher = pattern.matcher(dynamicActionDTO.getDynamicParameters());
    List<String> parsedParameters = new ArrayList<>() {};
    while (matcher.find()) {
      parsedParameters.add(matcher.group(1).trim());
    }
    return parsedParameters;
  }

  public String createDynamicCommand(
      DynamicActionDTO dynamicActionDTO,
      String dynamicParameters,
      Optional<Map<Object, Object>> body) {
    return createDynamicCommandHelper(
        dynamicActionDTO, parseDynamicParametersValues(dynamicParameters), body);
  }

  public String createDynamicCommand(DynamicActionDTO dynamicActionDTO, Object dynamicParameters) {
    Map<String, String> passedParameters = new HashMap<>();
    for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) dynamicParameters).entrySet()) {
      String key = (String) entry.getKey();
      String value = "";
      try {
        value = (String) entry.getValue();
      } catch (Exception e) {
        logger.error(e.getMessage());
        try {
          value = String.valueOf(entry.getValue());
        } catch (Exception e2) {
          logger.error(e2);
          value = entry.getValue().toString();
        }
      }
      passedParameters.put(key, value);
    }
    return createDynamicCommandHelper(dynamicActionDTO, passedParameters, Optional.empty());
  }

  private String createDynamicCommandHelper(
      DynamicActionDTO dynamicActionDTO,
      Map<String, String> passedParameters,
      Optional<Map<Object, Object>> body) {
    Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
    Matcher matcher = pattern.matcher(dynamicActionDTO.getDynamicParameters());

    StringBuilder result = new StringBuilder();

    while (matcher.find()) {
      String variableName = matcher.group(1).trim();
      String replacement = passedParameters.getOrDefault(variableName, "");
      if (replacement.matches("^\\{\\{.*\\}\\}$")) {
        Pattern pattern2 = Pattern.compile("^\\{\\{(.*)\\}\\}$");
        Matcher matcher2 = pattern2.matcher(replacement);
        try {
          if (matcher2.find()) {
            Optional<String> res = processBody(body.get(), matcher2.group(1));
            replacement = res.get();
          }
        } catch (JsonProcessingException | NoSuchElementException e) {
          logger.error(e.getMessage());
          replacement = "";
        }
      }
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(result);

    return dynamicActionDTO.getCommand() + " " + result;
  }

  private Optional<String> processBody(Map body, String parameter) throws JsonProcessingException {
    String[] keys = parameter.split("\\.");

    Object current = body;
    for (String key : keys) {
      if (current instanceof Map) {
        current = ((Map<?, ?>) current).get(key);
      } else if (current instanceof List) {
        int index = Integer.parseInt(key);
        current = ((List<?>) current).get(index);
        if (current == null) {
          return Optional.empty();
        }
      }
    }
    return Optional.of(current.toString());
  }

  private Map<String, String> parseDynamicParametersValues(String dynamicParameters) {
    // Remove leading and trailing quotes
    dynamicParameters = dynamicParameters.replaceAll("^\"|\"$", "");

    // Define the regex pattern to match key-value pairs
    Pattern pattern = Pattern.compile("([^,]+?):([^,]+)");
    Matcher matcher = pattern.matcher(dynamicParameters);

    // Process matched key-value pairs
    StringBuilder jsonStringBuilder = new StringBuilder("{");
    while (matcher.find()) {
      String key = matcher.group(1).trim();
      String value = matcher.group(2).trim();
      jsonStringBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");
    }

    // Remove the last comma and close the JSON object
    if (jsonStringBuilder.length() > 1) {
      jsonStringBuilder.setLength(jsonStringBuilder.length() - 1);
    }
    jsonStringBuilder.append("}");

    // Convert the JSON string to a JsonNode
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;
    try {
      jsonNode = objectMapper.readTree(jsonStringBuilder.toString());
    } catch (JsonProcessingException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }

    // Convert JsonNode to Map
    Map<String, String> resultMap = new HashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      resultMap.put(field.getKey(), field.getValue().asText());
    }
    return resultMap;
  }

  public boolean checkActionDTOisDynamicActionDTO(ActionDTO actionDTO) {
    Optional<DynamicActionDTO> potentialDynamicAction = getDynamicActionDTOByID(actionDTO.getId());
    return potentialDynamicAction.isPresent();
  }

  public DynamicActionDTO createDynamicActionDTOFromDynamicAction(DynamicAction dynamicAction) {
    DynamicActionDTO dynamicActionDTO = new DynamicActionDTO();
    dynamicActionDTO.setId(UUID.randomUUID().toString());
    return mapDynamicActionToDynamicActionDTO(dynamicAction, dynamicActionDTO);
  }

  public DynamicActionDTO mapDynamicActionToDynamicActionDTO(
      DynamicAction dynamicAction, DynamicActionDTO dynamicActionDTO) {
    dynamicActionDTO.setName(dynamicAction.getName());
    dynamicActionDTO.setDescription(dynamicAction.getDescription());
    dynamicActionDTO.setCommand(dynamicAction.getCommand().trim());
    dynamicActionDTO.setDynamicParameters(dynamicAction.getDynamicParameters());
    dynamicActionDTO.setAllowedApikeysForDynamicActions(getAllowedApikeysFromAction(dynamicAction));
    dynamicActionDTO.setPostExecutionLogFilePath(dynamicAction.getPostExecutionLogFilePath());
    dynamicActionDTO.setVersion(dynamicAction.getVersion());
    dynamicActionDTO.setRequiresConfirmation(dynamicAction.getRequiresConfirmation());
    dynamicActionDTO.setKeepLatestConfirmationRequest(
        dynamicAction.getKeepLatestConfirmationRequest());
    dynamicActionDTO.setMuted(dynamicAction.getMuted());
    return dynamicActionDTO;
  }

  public sophrosyne_api.core.internalexecutorservice.model.DynamicAction
      mapDynamicActionDTOToDynamicAction(DynamicActionDTO dynamicActionDTO) {
    return new sophrosyne_api.core.internalexecutorservice.model.DynamicAction()
        .id(dynamicActionDTO.getId())
        .name(dynamicActionDTO.getName())
        .description(dynamicActionDTO.getDescription())
        .command(dynamicActionDTO.getCommand())
        .dynamicParameters(dynamicActionDTO.getDynamicParameters())
        .allowedApikeys(
            dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
                .map(sophrosyne.core.apikeyservice.dto.ApikeyDTO::getApikeyname)
                .toList())
        .version(dynamicActionDTO.getVersion())
        .postExecutionLogFilePath(dynamicActionDTO.getPostExecutionLogFilePath())
        .requiresConfirmation(dynamicActionDTO.getRequiresConfirmation())
        .keepLatestConfirmationRequest(dynamicActionDTO.getKeepLatestConfirmationRequest())
        .runningId(dynamicActionDTO.getRunningActionId())
        .muted(dynamicActionDTO.getMuted());
  }

  public DynamicAction mapDynamicActionDTOToDynamicActionService(
      DynamicActionDTO dynamicActionDTO) {
    return new DynamicAction()
        .id(dynamicActionDTO.getId())
        .name(dynamicActionDTO.getName())
        .description(dynamicActionDTO.getDescription())
        .command(dynamicActionDTO.getCommand())
        .dynamicParameters(dynamicActionDTO.getDynamicParameters())
        .allowedApikeys(
            dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
                .map(sophrosyne.core.apikeyservice.dto.ApikeyDTO::getApikeyname)
                .toList())
        .postExecutionLogFilePath(dynamicActionDTO.getPostExecutionLogFilePath())
        .version(dynamicActionDTO.getVersion())
        .requiresConfirmation(dynamicActionDTO.getRequiresConfirmation())
        .keepLatestConfirmationRequest(dynamicActionDTO.getKeepLatestConfirmationRequest())
        .muted(dynamicActionDTO.getMuted());
  }

  private HashSet<sophrosyne.core.apikeyservice.dto.ApikeyDTO> getAllowedApikeysFromAction(
      DynamicAction dynamicAction) {
    return new HashSet<sophrosyne.core.apikeyservice.dto.ApikeyDTO>(
        dynamicAction.getAllowedApikeys().stream()
            .map(
                apikey -> {
                  return apikeyService.getApiDTOByApikeyname(apikey).isPresent()
                      ? apikeyService.getApiDTOByApikeyname(apikey).get()
                      : null;
                })
            .toList());
  }
}
