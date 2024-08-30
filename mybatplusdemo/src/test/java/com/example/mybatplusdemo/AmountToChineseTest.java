package com.example.mybatplusdemo;

import com.example.mybatplusdemo.utils.AmountUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AmountToChineseTest {

    @Test
    public void test() {
        System.out.println(AmountUtils.toChinese(123456789d));
        System.out.println("a");
    }
}
