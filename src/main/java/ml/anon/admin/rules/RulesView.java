package ml.anon.admin.rules;

import com.google.common.base.Joiner;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import ml.anon.admin.BaseView;
import ml.anon.recognition.rulebased.api.model.Rule;
import ml.anon.recognition.rulebased.api.resource.RuleResource;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by mirco on 16.08.17.
 */
@SpringComponent
@UIScope
@Slf4j
public class RulesView extends BaseView {

  public static final String ID = "RULESVIEW";

  private Grid<Rule> grid = new Grid<>();


  private RuleResource ruleResource;

  @Autowired
  public RulesView(RuleResource ruleResource) {
    super();
    this.ruleResource = ruleResource;
    RuleEditor editor = new RuleEditor(ruleResource, grid, this);
    VerticalLayout mainLayout = new VerticalLayout(grid, editor);
    mainLayout.setMargin(false);

    addComponent(mainLayout);

    grid.asSingleSelect().addValueChangeListener(e -> {
      if (e.getValue() != null) {
        editor.changeBoundRule((e.getValue()));
      }
    });

    mainLayout.setSizeFull();

    loadRules(ruleResource, editor);
    grid.addColumn(r -> BooleanUtils.toString(r.isActive(), "T", "F")).setCaption("Aktiv");
    Grid.Column<Rule, String> name = grid.addColumn(Rule::getName).setCaption("Name");
    Grid.Column<Rule, ml.anon.anonymization.model.Label> label = grid
        .addColumn(Rule::getLabel).setCaption("Label");
    Grid.Column<Rule, Double> weight = grid.addColumn(Rule::getWeight)
        .setCaption("Gewicht");
    Grid.Column<Rule, String> regEx = grid
        .addColumn(r -> StringUtils.abbreviate(StringUtils.defaultString(r.getRegExp()), 50))
        .setCaption("RegEx");

    Grid.Column<Rule, String> constraints = grid
        .addColumn(r -> Joiner.on(", ").join(r.getConstrains()))
        .setCaption("Constraints");

    Grid.Column<Rule, String> deleteable = grid
        .addColumn(r -> BooleanUtils.toStringYesNo(!r.isCore()))
        .setCaption("Deleteable");
    name.setExpandRatio(1);
    label.setExpandRatio(1);
    regEx.setExpandRatio(1);

    grid.setSizeFull();
    editor.setWidth(100, Unit.PERCENTAGE);

  }

  private void loadRules(RuleResource ruleResource, RuleEditor editor) {
    try {
      grid.setItems(ruleResource.findAll());
    } catch (Exception e) {
      log.error("Rule service not available");
      log.error(e.getLocalizedMessage());
      grid.setEnabled(false);
      editor.setEnabled(false);

    }
  }

  public void refresh() {
    grid.setItems(ruleResource.findAll());
  }

}
