package ml.anon.ui.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController("/")
public class RedirectController {

    @Value("${server.contextPath}")
    private String serverContextPath;

    @GetMapping
    public RedirectView redirect() {
        return new RedirectView(serverContextPath + "/overview");
    }
}
