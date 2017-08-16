package ml.anon.admin.rules;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ml.anon.admin.BaseView;
import ml.anon.recognition.rulebased.api.resource.RuleResource;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Slf4j
public class RulesView extends BaseView {

  public static final String ID = "RULESVIEW";


  @Resource
  private RuleResource ruleResource;

  public RulesView() {
    super();
    Label label = new Label("Rules");
    addComponent(label);

    Button b = new Button("test");
    b.addClickListener(e -> {
      ruleResource.findAll().forEach(System.out::println);
    });
    addComponent(b);
  }


}
