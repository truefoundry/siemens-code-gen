package PlayGround;

import CompositionRoot.IocBuilder;
import Enums.EResultData;
import Enums.ETestData;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Subhajit
{
    @Test
    void LoginTest()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "XXXXX", tc -> {
            tc.browser.start(WebDrv.CHROME, ETestData.QA_ENV_URL);
            tc.browser.localLogin();

        });
    }
}
