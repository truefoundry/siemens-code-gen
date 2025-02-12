package CompositionRoot;

import ControlImplementation.*;
import ControlImplementation.Table.TableControl;
import Enums.ETestData;
import fate.core.CompositionRoot.AbstractCoreHandler;
import fate.core.CompositionRoot.CoreHandlerConfig;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.Enums.ResultPlatform;
import fate.core.Enums.TestType;
import fate.core.Interfaces.ICoreCssControl;

public class MdlaHandler extends AbstractCoreHandler
{
    public final ICoreCssControl css;
    public final BrowserControl browser;
    public final ButtonControl button;
    public final EditControl edit;
    public final ModalControl modal;
    public final MenuControl menu;
    public final TileControl tile;
    public final ToggleControl toggle;
    public final TabControl tab;
    public final ProgressBarControl progressBar;
    public final ComboBoxControl combo;
    public final CheckBoxControl checkBox;
    public final TableControl table;
    public final NotificationControl notification;


    public MdlaHandler(CoreHandlerConfig config, CoreCssControl cssControl, BrowserControl browserControl,
                       ButtonControl buttonControl, EditControl editControl, ModalControl modalControl,
                       MenuControl menuControl, TileControl tileControl, ToggleControl toggle, TabControl tabControl,
                       ProgressBarControl progressBarControl, ComboBoxControl comboBoxControl, CheckBoxControl
                       checkBoxControl, TableControl tableControl, NotificationControl notificationControl)
    {
        this.setType(TestType.WEB_UI);
        CoreIocBuilder.getContainer().getComponent(CoreHandlerConfig.class).setPushConfig(ETestData.PROJECT_DATA);

        this.testSetOrRun = config.getTcPath();

        if(config.getTcPath().getPlatform().equals(ResultPlatform.HP_ALM))
        {
            this.testcaseName = config.getTcName().substring(3);
        }
        else
        {
            this.testcaseName = config.getTcName();
        }
        this.css = cssControl;
        this.browser = browserControl;
        this.button = buttonControl;
        this.edit = editControl;
        this.modal = modalControl;
        this.menu = menuControl;
        this.tile = tileControl;
        this.toggle = toggle;
        this.tab = tabControl;
        this.progressBar = progressBarControl;
        this.combo = comboBoxControl;
        this.checkBox = checkBoxControl;
        this.table = tableControl;
        this.notification = notificationControl;

        TcLog.info("Test case with %s ID: %s is running.".formatted(config.getTcPath().getPlatform(),  this.getTestcaseName()));
    }
}
