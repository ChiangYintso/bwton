package pers.jiangyinzuo.carbon.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CreditServiceTest {

    @Autowired
    private CreditService creditService;

    @Test
    public void testGetTotalLeaderboard()  {
        var result = creditService.getLeaderBoard(1L, "total");
        assertNotNull(result);
    }
}