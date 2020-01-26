package pers.jiangyinzuo.carbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.jiangyinzuo.carbon.controller.validation.annotation.ID;
import pers.jiangyinzuo.carbon.domain.dto.CreditDTO;
import pers.jiangyinzuo.carbon.http.HttpResponseBody;
import pers.jiangyinzuo.carbon.service.FriendService;

import java.util.List;

/**
 * @author Jiang Yinzuo
 */
@RestController
public class CreditController {

    private FriendService friendService;

    @Autowired
    public void setFriendService(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/leaderboard")
    public HttpResponseBody<List<CreditDTO>> getFriendsCreditLeaderboard(@Validated @ID @RequestParam Long userId) {
        List<CreditDTO> data = friendService.getCreditLeaderBoard(userId);
        return new HttpResponseBody<>(0, "ok", data);
    }
}
