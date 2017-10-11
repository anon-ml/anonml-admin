package ml.anon.ui.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {


    @Value("${frontend.service.url}")
    private String frontEndUrl;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public RedirectView redirect() {
        return new RedirectView("/overview");
    }

    @RequestMapping(value = {"/document/{id}"}, method = RequestMethod.GET)
    public RedirectView adminOverview(@PathVariable("id") String id) {
        return new RedirectView(frontEndUrl + "/document/" + id);
    }
}
