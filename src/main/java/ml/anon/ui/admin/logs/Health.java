package ml.anon.ui.admin.logs;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.beans.ConstructorProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mirco on 18.08.17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Health {

  private String status;

  public boolean isUp() {
    return "UP".equals(status);
  }
}
