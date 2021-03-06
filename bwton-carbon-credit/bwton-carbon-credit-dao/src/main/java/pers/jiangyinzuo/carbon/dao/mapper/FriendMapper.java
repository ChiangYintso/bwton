package pers.jiangyinzuo.carbon.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import pers.jiangyinzuo.carbon.domain.entity.User;

import java.util.List;

/**
 * @author Jiang Yinzuo
 */
@Mapper
public interface FriendMapper {

    /**
     * 添加好友，用户1ID需小于等于用户2ID
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     */
    void addFriends(Long user1Id, Long user2Id);

    /**
     * 获取用户自身与好友基本信息
     * @param userId 用户ID
     * @return 好友列表
     */
    List<User> getUserAndFriends(Long userId);

    /**
     * 判断是不是好友
     * @param user1Id 用户1ID
     * @param user2Id 用户2ID
     * @return 是: true; 否: false
     */
    Boolean isFriend(Long user1Id, Long user2Id);
}
