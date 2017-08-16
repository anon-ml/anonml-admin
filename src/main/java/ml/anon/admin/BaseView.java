package ml.anon.admin;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by mirco on 16.08.17.
 */
public class BaseView extends VerticalLayout implements View {

  public BaseView() {
    setSizeFull();
    setSpacing(false);
  }
}
