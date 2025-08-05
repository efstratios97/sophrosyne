package sophrosyne.core.actionrecommendationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.repository.ActionRecommendationRepository;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.globalservices.utilservice.UtilService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;

import java.util.*;

@Service
public class ActionRecommendationService {

    @Autowired
    private ActionRecommendationRepository actionRecommendationRepository;

    @Autowired
    private UtilService utilService;

    @Autowired
    private ApikeyService apikeyService;

    @Autowired
    @Lazy
    private ConfigurationService configurationService;

    public ActionRecommendationDTO
    createActionRecommendation(ActionRecommendation actionRecommendation) {
        ActionRecommendationDTO
                actionRecommendationDTO =
                actionRecommendationRepository.save(
                        createActionRecommendationDTOFromActionRecommendation(actionRecommendation));

        return actionRecommendationDTO;
    }

    public ActionRecommendationDTO
    updateActionRecommendation(ActionRecommendation actionRecommendation)
            throws NoSuchElementException {
        ActionRecommendationDTO
                actionRecommendationDTO = getActionRecommendationByID(actionRecommendation.getId()).get();
        ActionRecommendationDTO
                actionRecommendationDTOtmp =
                createActionRecommendationDTOFromActionRecommendation(actionRecommendation);
        actionRecommendationDTOtmp.setId(actionRecommendationDTO.getId());
        ActionRecommendationDTO
                updateActionRecommendationDTO =
                actionRecommendationRepository.save(actionRecommendationDTOtmp);

        return updateActionRecommendationDTO;
    }

    public Optional<ActionRecommendationDTO>
    getActionRecommendationByID(String id) {
        return actionRecommendationRepository.findById(id);
    }

    public List<ActionRecommendationDTO>
    getActionRecommendations() {
        return actionRecommendationRepository.findAll();
    }

    public void deleteActionRecommendation(
            ActionRecommendationDTO
                    actionRecommendationDTO) {
        actionRecommendationRepository.delete(actionRecommendationDTO);

    }

    public void deleteAllActionRecommendations() {
        actionRecommendationRepository.deleteAll();
    }

    public ActionRecommendationDTO
    createActionRecommendationDTOFromActionRecommendation(
            ActionRecommendation actionRecommendation) {
        return ActionRecommendationDTO.builder()
                .id(UUID.randomUUID().toString())
                .name(actionRecommendation.getName())
                .description(actionRecommendation.getDescription())
                .contactInformation(actionRecommendation.getContactInformation())
                .responsibleEntity(actionRecommendation.getResponsibleEntity())
                .allowedApikeys(getAllowedApikeysFromActionRecommendation(actionRecommendation))
                .additionalDocumentation(
                        actionRecommendation.getAdditionalDocumentation() != null
                                ? actionRecommendation.getAdditionalDocumentation()
                                : null)
                .build();
    }

    public ActionRecommendation mapActionRecommendationDTOToActionRecommendation(
            ActionRecommendationDTO
                    actionRecommendationDTO) {
        return new ActionRecommendation()
                .id(actionRecommendationDTO.getId())
                .name(actionRecommendationDTO.getName())
                .description(actionRecommendationDTO.getDescription())
                .contactInformation(actionRecommendationDTO.getContactInformation())
                .responsibleEntity(actionRecommendationDTO.getResponsibleEntity())
                .allowedApikeys(
                        actionRecommendationDTO.getAllowedApikeys().stream()
                                .map(ApikeyDTO::getApikeyname)
                                .toList())
                .additionalDocumentation(
                        actionRecommendationDTO.getAdditionalDocumentation() != null
                                ? actionRecommendationDTO.getAdditionalDocumentation()
                                : null);
    }

    private HashSet<ApikeyDTO> getAllowedApikeysFromActionRecommendation(
            ActionRecommendation actionRecommendation) {
        return new HashSet<ApikeyDTO>(
                actionRecommendation.getAllowedApikeys().stream()
                        .map(
                                apikey -> {
                                    return apikeyService.getApiDTOByApikeyname(apikey).get();
                                })
                        .toList());
    }
}
