package ml.anon.ui.management;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import ml.anon.ui.admin.dashboard.DashboardView;
import ml.anon.ui.admin.logs.LogsView;
import ml.anon.ui.admin.ml.MLView;
import ml.anon.ui.admin.rules.RulesView;

import javax.annotation.Resource;

@SpringUI(path = "/overview")
@Theme("valo")
@Slf4j
public class ManagementUI extends UI {

    private Navigator navigator;

    @Resource
    private DocumentOverviewView docView;

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("AnonML Dokumentenmanagement");

        VerticalLayout base = new VerticalLayout();
        base.setSizeFull();
        base.setMargin(false);
        base.setSpacing(false);
        Panel container = new Panel();
        container.setSizeFull();

        base.addComponents(container);
        base.setExpandRatio(container, 0.99f);

        navigator = new Navigator(this, container);
        navigator.addView(DocumentOverviewView.ID, docView);
        setContent(base);
        setPollInterval(60000);
        addPollListener(docView);
    }
}
