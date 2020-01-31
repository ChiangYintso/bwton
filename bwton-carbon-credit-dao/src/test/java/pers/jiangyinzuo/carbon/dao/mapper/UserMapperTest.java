package pers.jiangyinzuo.carbon.dao.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import pers.jiangyinzuo.carbon.common.security.SaltGenerator;
import pers.jiangyinzuo.carbon.domain.dto.UserLoginDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserMapperTest extends BaseMybatisTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testLogin() {
        UserLoginDTO userLoginDTO = userMapper.getUserAccountByTelephone("13012345678");
        assertEquals("13012345678", userLoginDTO.getTelephone());
        assertEquals(64, userLoginDTO.getCipher().length());
    }

    @Test
    public void testRegister() {
        assertThrows(DuplicateKeyException.class, () -> userMapper.saveUserAccount("李四", "123456", SaltGenerator.getSalt32(), "13012345678"));
    }

    @Test
    public void testGetUsers() {
        List<Object> result = userMapper.getUsers(List.of(1L, 3L));
        assertEquals(2, result.size());
    }
}