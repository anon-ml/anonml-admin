package ml.anon.admin.dashboard;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by mirco on 16.08.17.
 */
public class DashboardView extends VerticalLayout implements View {

  public static final String ID = "";

  public DashboardView() {
    setSizeFull();
    Label label = new Label("Dashboard");
    addComponent(label);
  }


}
