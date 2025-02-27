package CompositionRoot;

import ControlImplementation.*;
import ControlImplementation.Table.TableControl;
import Enums.EResultData;
import fate.core.CompositionRoot.CoreHandlerConfig;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.TestExecuter;
import fate.core.Enums.ResultPlatform;
import java.time.Duration;
import static org.picocontainer.Characteristics.CACHE;

public class IocBuilder extends CoreIocBuilder
{
    @SafeVarargs
    public static void execute(Duration duration, EResultData iocTestPath, String iocTcName,
                               TestExecuter.ThrowingConsumer<MdlaHandler, Exception> testFunction,
                               TestExecuter.ThrowingConsumer<MdlaHandler, Exception>... clean)
    {
        MdlaHandler test = IocBuilder.createNewTest(iocTestPath, iocTcName);
        try
        {
            testFunction.accept(test);
        }
        catch(Exception exception)
        {
            TcLog.error("Exception was thrown.", exception);
        }
        catch(Error error)
        {
            TcLog.error("Error was thrown.", new Exception(error));
            throw error;
        }
        finally
        {
            TestExecuter.execute(duration, handler -> {}, test, clean);
        }
    }

    /**
     * Selects control modules for selected project.
     *
     * @param IocTestPath - Path to TestRun\TestSet.
     * @param IocTcName   - Test case ID.
     */
    public static MdlaHandler createNewTest(EResultData IocTestPath, String IocTcName)
    {
        MdlaHandler handler = null;
        try
        {
            CoreIocBuilder.initialize();

            //Base
            if(getContainer().getComponent(CoreHandlerConfig.class) != null && !"".equals(IocTcName))
            {
                getContainer().dispose();
                resetContainer();
            }

            String prefix = "";
            if(IocTestPath.getPlatform().equals(ResultPlatform.HP_ALM))
            {
                prefix = "[1]";
            }

            if(!"".equals(IocTcName))
            {
                getContainer().as(CACHE).addComponent(new CoreHandlerConfig(IocTestPath, prefix + IocTcName));
            }

            registerSampleProject();
            handler = (MdlaHandler) CoreIocBuilder.getHandler(IocTestPath.getType());
            TcLog.config("Running instance.");
        }
        catch(Exception e)
        {
            TcLog.fatal(e.getMessage());
        }
        return handler;
    }

    private static void registerSampleProject()
    {
        try
        {
            CoreIocBuilder.getContainer().as(CACHE).addComponent(CoreCssControl.class);
            CoreIocBuilder.getContainer().as(CACHE).addComponent(BrowserControl.class);
            CoreIocBuilder.getContainer().addComponent(ButtonControl.class);
            CoreIocBuilder.getContainer().addComponent(EditControl.class);
            CoreIocBuilder.getContainer().addComponent(ModalControl.class);
            CoreIocBuilder.getContainer().addComponent(MenuControl.class);
            CoreIocBuilder.getContainer().addComponent(TileControl.class);
            CoreIocBuilder.getContainer().addComponent(ToggleControl.class);
            CoreIocBuilder.getContainer().addComponent(TabControl.class);
            CoreIocBuilder.getContainer().addComponent(ProgressBarControl.class);
            CoreIocBuilder.getContainer().addComponent(ComboBoxControl.class);
            CoreIocBuilder.getContainer().addComponent(CheckBoxControl.class);
            CoreIocBuilder.getContainer().addComponent(TableControl.class);
            CoreIocBuilder.getContainer().addComponent(NotificationControl.class);

            //handlers
            CoreIocBuilder.getContainer().as(CACHE).addComponent(MdlaHandler.class);
        }
        catch(Exception e)
        {
            TcLog.fatal(e.getMessage());
        }
    }
}
