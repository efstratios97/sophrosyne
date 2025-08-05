package sophrosyne.core.commonservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;

import java.util.List;
import java.util.Objects;

@Service
public class CommonAPIService {

    private final Logger logger = LogManager.getLogger(getClass());

    public boolean checkValidApikeyAction(ActionDTO actionDTO, String apikey) {
        boolean check = false;
        if (actionDTO instanceof DynamicActionDTO dynamicActionDTO) {
            check =
                    checkValidApikey(
                            dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
                                    .map(ApikeyDTO::getApikey)
                                    .toList(),
                            apikey);
        } else {
            check =
                    checkValidApikey(
                            actionDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikey).toList(), apikey);
        }
        return check;
    }

    public boolean checkValidApikeyActionRecommendation(
            ActionRecommendationDTO
                    actionRecommendationDTO,
            String apikey) {
        return checkValidApikey(
                actionRecommendationDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikey).toList(),
                apikey);
    }

    private boolean checkValidApikey(List<String> apikeys, String apikey) {
        boolean check = false;
        try {
            check =
                    apikeys.stream()
                            .anyMatch((apikeyFromRequest -> Objects.equals(apikeyFromRequest, apikey)));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return check;
        }
        return check;
    }
}
