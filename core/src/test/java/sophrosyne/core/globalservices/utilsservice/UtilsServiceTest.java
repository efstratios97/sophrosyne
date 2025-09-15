package sophrosyne.core.globalservices.utilsservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.globalservices.utilservice.UtilService;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class UtilsServiceTest {

    @Autowired
    private UtilService sut_utilService;

    @Test
    public void test_readFile() {
        File resourcesDirectory = new File("src/test/resources/testFile.txt");

        byte[] file = sut_utilService.readFile(resourcesDirectory.getAbsolutePath()).get();

        assertThat(file).isInstanceOf(byte[].class);
    }
}
