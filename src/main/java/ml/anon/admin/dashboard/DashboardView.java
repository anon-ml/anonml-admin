package ml.anon.admin.dashboard;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import ml.anon.admin.BaseView;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
public class DashboardView extends BaseView {

  public static final String ID = "";

  public DashboardView() {
    super();
    Label label = new Label("Dashboard");
    addComponent(label);
  }


}