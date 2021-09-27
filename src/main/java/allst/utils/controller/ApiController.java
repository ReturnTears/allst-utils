package allst.utils.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author June
 * @since 2021年09月
 */
@RestController
@RequestMapping("/api")
public class ApiController extends BaseController {
    @RequestMapping("/forward")
    public String forwardTo() {
        return FORWARD_PREFIX;
    }
}
