package pers.jiangyinzuo.carbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pers.jiangyinzuo.carbon.domain.dto.FriendshipDTO;
import pers.jiangyinzuo.carbon.http.HttpResponseUtil;
import pers.jiangyinzuo.carbon.service.FriendService;
import pers.jiangyinzuo.carbon.util.HttpHeaderUtil;

/**
 * @author Jiang Yinzuo
 */
@RestController
public class FriendController {

    private FriendService friendService;

    @Autowired
    public void setFriendService(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/friend")
    public ResponseEntity<Object> addFriend(
            @RequestHeader("Authorization") String authToken,
            @Validated @RequestBody FriendshipDTO friendshipDTO
    ) {
        Long userId = HttpHeaderUtil.getUserId(authToken);

        if (!friendshipDTO.userId().equals(userId)) {
            return HttpResponseUtil.FORBIDDEN;
        }
        if (!friendshipDTO.isValid()) {
            return HttpResponseUtil.badRequest("不能加自己为好友");
        }

        if (friendService.addFriend(friendshipDTO)) {
            return HttpResponseUtil.OK;
        } else {
            return HttpResponseUtil.badRequest("该用户不存在");
        }
    }
}
