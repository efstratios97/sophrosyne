package sophrosyne.core.actionarchiveservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchiveMetadata;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.dashboardstatistics.model.Stats;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionArchiveServiceIT extends PostgresIntegrationTestBase {

    @Autowired
    private ActionArchiveService sut_actionArchiveService;

    @Autowired
    private ActionService actionService;

    @AfterEach
    public void purgeArchive() {
        sut_actionArchiveService.purgeCompleteArchive();
    }

    @Test
    public void test_createActionArchive() {
        Action action = createAPIAction();
        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);

        ActionArchiveDTO sut_actionArchiveDTO =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        1,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");

        Optional<ActionArchiveDTO> actionArchivedDTO =
                sut_actionArchiveService.getActionArchiveDTO(sut_actionArchiveDTO.getId());

        assertThat(actionArchivedDTO.get().getActionName()).isEqualTo(actionDTO.getName());
        assertThat(actionArchivedDTO.get().getExitCode()).isEqualTo(1);
        assertThat(actionArchivedDTO.get().getActionId()).isEqualTo(actionDTO.getId());
        assertThat(actionArchivedDTO.get().getVersion()).isEqualTo(actionDTO.getVersion());
    }

    @Test
    public void get_ActionArchives() {
        Action action = createAPIAction();
        Action action2 = createAPIAction().name("test2");

        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);
        ActionDTO actionDTO2 = actionService.createActionDTOFromAction(action2);

        ActionArchiveDTO actionArchiveDTO =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        1,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");
        ActionArchiveDTO actionArchiveDTO2 =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO2,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        0,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");

        List<ActionArchiveDTO> actionArchivedDTOs = sut_actionArchiveService.getActionArchives();

        assertThat(actionArchivedDTOs)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                        "executionStartPoint", "executionEndPoint")
                .containsAll(List.of(actionArchiveDTO, actionArchiveDTO2));
    }

    @Test
    public void get_LatestActionArchive() {
        Action action = createAPIAction();

        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);
        ActionDTO actionDTO2 = actionService.createActionDTOFromAction(action);
        actionDTO2.setName("random");

        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                0,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                0,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO2,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");

        Optional<ActionArchiveDTO> actionArchivedDTOSUT =
                sut_actionArchiveService.getLastActionArchiveByActionId(actionDTO.getId());

        List<ActionArchiveDTO> actionArchiveDTOS =
                sut_actionArchiveService.getActionArchivesByActionId(actionDTO.getId());
        ActionArchiveDTO actionArchiveDTOCorrect =
                actionArchiveDTOS.stream()
                        .sorted(Comparator.comparing(ActionArchiveDTO::getExecutionStartPoint))
                        .toList()
                        .getLast();

        assertThat(actionArchivedDTOSUT.get()).isEqualTo(actionArchiveDTOCorrect);
    }

    @Test
    public void get_ActionArchives_whenActionNamedDefined() {
        Action action = createAPIAction();

        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);
        ActionDTO actionDTO2 = actionService.createActionDTOFromAction(action);
        actionDTO2.setName("random");

        ActionArchiveDTO actionArchiveDTO =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        1,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");
        ActionArchiveDTO actionArchiveDTO2 =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        0,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");
        ActionArchiveDTO actionArchiveDTO3 =
                sut_actionArchiveService.createActionArchiveDTO(
                        actionDTO,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        new byte[]{},
                        new byte[]{},
                        0,
                        ActionArchiveDTO.TYPES.EXTERNAL.name(),
                        "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO2,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");

        List<ActionArchiveDTO> actionArchivedDTOs =
                sut_actionArchiveService.getActionArchivesByActionId(actionDTO.getId());

        assertThat(actionArchivedDTOs)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                        "executionStartPoint", "executionEndPoint")
                .containsAll(List.of(actionArchiveDTO, actionArchiveDTO2, actionArchiveDTO3));
    }

    @Test
    public void get_ActionArchivesMetadata() {
        Action action = createAPIAction();

        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);

        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{1, 33, 44, 5},
                new byte[]{1, 56, 23, 4},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");

        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{1, 33, 44, 5, 8},
                new byte[]{1, 56, 23, 4, 9, 77},
                0,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");

        List<sophrosyne.core.actionarchiveservice.repository.ActionArchiveMetadata> actionArchiveMetadata =
                sut_actionArchiveService.getActionArchiveMetadata();

        List<ActionArchiveMetadata>
                actionArchiveMetadataModels =
                actionArchiveMetadata.stream()
                        .map(
                                actionArchiveMetadata1 -> {
                                    return sut_actionArchiveService
                                            .mapActionArchiveMetadataInterfaceToActionArchiveMetadata(
                                                    actionArchiveMetadata1);
                                })
                        .toList();

        assertThat(actionArchiveMetadataModels).hasSize(2);
        assertThat(actionArchiveMetadataModels.getFirst().getActionName())
                .isEqualTo(actionArchiveMetadata.getFirst().getActionName());
    }

    @Test
    public void test_Stats() {
        Action action = createAPIAction();

        ActionDTO actionDTO = actionService.createActionDTOFromAction(action);
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                1,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                0,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                0,
                ActionArchiveDTO.TYPES.EXTERNAL.name(),
                "v.1.0.0");
        sut_actionArchiveService.createActionArchiveDTO(
                actionDTO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new byte[]{},
                new byte[]{},
                0,
                ActionArchiveDTO.TYPES.INTERNAL.name(),
                "v.1.0.0");

        Stats stats = new Stats();
        sut_actionArchiveService.getAllStats(stats);

        assertThat(stats.getTotalRun()).isEqualTo(6);
        assertThat(stats.getTotalExternal()).isEqualTo(5);
        assertThat(stats.getTotalInternal()).isEqualTo(1);
        assertThat(stats.getTotalFail()).isEqualTo(3);
        assertThat(stats.getTotalSuccess()).isEqualTo(3);
    }

    private Action createAPIAction() {
        return new Action()
                .name("Test_Action")
                .description("Test_Description")
                .command("ansible-playbook")
                .allowedApikeys(List.of("apikey_fictional"))
                .postExecutionLogFilePath("/etc/file/")
                .version("v.1.0.0")
                .requiresConfirmation(1);
    }
}

