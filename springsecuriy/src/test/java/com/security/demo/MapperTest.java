package com.security.demo;

import com.security.demo.entity.User;
import com.security.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.List;

/**
 * @Author 三更  B站： https://space.bilibili.com/663528522
 */
@SpringBootTest
public class MapperTest {

    public static void main(String[] args) {
        String data = "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";
        byte[] bArr = Base64.getMimeDecoder().decode(data);
        byteToImage(bArr, "d:/photo/fromByteArray.png");
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUserMapper(){
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
    }

    @Test
    public void testbyteToFile() {
//        String data = "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AwtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgnW7IdxRKsnT2izI5zpI8/l8b0+m+HWABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsID1BbCizzAGxNqzdylcA2JtW+pu//sDQMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWM829lrTdocGkne4tqrJNl7bJq8bfQAWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtY42DtF+X+6/xyG3c/uTsQkBw1SHYTsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwPomWMmRhXMcnCud5HMlPyfZSPsDAe8Yazh3P8ACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAHrC2C9dWShe7jeHXR46rvOtfraNpnsnTX4gAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhaw8oMFyXLvfleyabttvFZj+1vg2vYPLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIC1eXS9NmrQvee1d/qOgZLk8IGxBmABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsICVB2sfx6daa624n7rn5NvpPsX+KEa344AFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsLTo/tF+cqiiu5klVzX5pGtjH7nXByxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrC2wRIRAZaIAEtEBFgiIsASEWCJiABLRARYIgIsERFgiYgAS0SAJSICLBERYIkIsEREgCUiAiwRAZaICLBERIAlIsASEQGWiAiwRARYIiLAEhEBlogAS0QEWCIiwBKRa/IPq1EXiG8WRx0AAAAASUVORK5CYII=";
        String data = "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQCAIAAAAP3aGbAAAM7UlEQVR42u3aUZLjIBBEQd//0rt3mKCrC5Tv2yNbEiQTAb9/knRJP49AErAkCViSgCVJwJIkYEkCliQBS5KAJQlYkgQsSQKWJGBJErAkCViSgCVJwJIkYEkCliQBS5KAJQlYkgQsSQKWJGBJErAkCViSgCVJl4H1W+1vv/Bvnzn1NE49w8GxcugukiPqO9e5cQ4CC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFrC+AlZxIuzy1XfmN6be8mz6G9dzIbJuDwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtY05Mk+cqTG8NJ+HY31+cQSb73JI79cxBYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAavhYSWH140ToG3xmPuu5AGO5FgFFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMAC1u6Vd6fE7gGFOYjnRubucgIsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgPU2WLs4zg3K/g3vG48jtB1wSY6NN+YgsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwPomWLub9D7jMz7TdlAGWAalz/gMsIDlMz4DLGABy2d8BljAMnB9xmeABSyf8RlgvQdWW/1HDRYHwfqxhv4n9uQRgSsnMrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMDqBmvu8MGN33UKiN0t57aDKW3b/0ni595X21IKLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsO4Ca27oJCfb7r23Ado2JXavvEtP25VjhAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAWsj4B1apLMgbW7tT/H99z0a3uqbRS2LULJfyyABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMAC1ntg7cI3N9WTrCQH5Rt/1XYgoB/QGD3AAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGAB67Ng7T7i3UGwexenfvMu1q7TMws+cawBWMACDbCABSxgAQtYwAIWsIAFLGABy3WABSxgAQtYwBp7VbtTIrkBn7z3JM393z5Hc/LKc+OwPGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIB1HVhJCvtxnJsAySMUye3tX1lzy8lHDigAC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFrI+ANbehmxw6b9xF/5Kze+W239P/LmL3BSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrDKwUoeNWj7qyQru0+jbfHYfV+7xyOSrCwuisACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAHrSbCSwz35Ok/91e5Q7r+vOb7f4HJ3fgELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAWsL4A1N3DnBsp3DgS0sTs3/fp/T9voTY4WYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgPUeWLu33b/p24bjje+9bfK3zYLk3AEWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwGp4fP2vc+4330jzd0bU7pJcjhGwgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jA+ghYu48vefShbXO97crJgyBz9zV3nf6FCljAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABK/+I+6fW7mRre4a72/Zz0y/5XbuILB59ABawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jlYPW/vOTk3908vnFL3p1mFiFgAQtYwAIWsIBlGgMLWMACFrCABSxgAQtYwAIWsExjYAFruuSrOvWIkxNgDr5T35Vkd+7b31iEks95958PYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgHU7WHNTKwnf4qt6ZqmYA3QXmjeO8iTHGLCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMB6D6zfEyVf1dwG/KuI7I7DL7+L5BsEFrCABSxgAQtYwAIWsIBlkgALWMACFrCABSxgAQtYwLr9WEMbB28cWUgOprbr9L+vG99F25IDLGABy7sAFrCABSxgAQtYwAIWsIAFLGB5F8AClkkCLGDdDtbfHnryM20TILnhfWqyzf3mue+agyZ5XzfOL2ABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsID1HljJQTlHz41/tYtR2zSee6pzm/398wJYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAevsLf2CJQdukvhdaN54g7vLSduTBxawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAmuap7cptd9G/Cb27LCXJ2P2rNyAGFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMC6l5W5dkF/crj/mzw+srsw3PgugAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAtZ7YJ0aKG/wtDuYkoNyl8s5jNqe/HfmDrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMC6C6zdhzV3neSVd481JCdSG2ptI7wfNWABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFrGnUTn1Xcqon6WkjY/cN3ojI3PNJvgtgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABax7OZgbcL+xdifSq4Dubv8nR4tjDcACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAFr+gXPTcjk8OrfqG67i+S06SesbfkvD1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGCVg5WcSLuonRooc9vSbVvyyamVfIZvfFdyKQUWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAQtY3wTrxg3d5GZ/Gxm7xyPKJ1LhWN39NwJYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAevsND71yne3nOc2qncndhvEu0vO7sKwe6fAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jTqCUHZfL33IhR2wToP4oxtwzszh1gAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABaw8B29sk8/9wn7Qb+xGmvuXSWABC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIB1F1j9W/JtEO+yMnfvc3faD/HcE3v1yQMLWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAWsbx5rmHt8uwcddgdK20b+qSvvLh6vLsnJpRRYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgvQdWchDMTezdKbFL6u5w3z2gMPebk+Nwd0QBC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGDlX0zbMJ0DPTm12o5Q9E+/N7BOLnjAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABy7GGs1uz/Vu8u0C0HX3YHXW7S/KN8MUCFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWOVgRe+hfqon773t+bQBceOBiRsPwQALWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAUsYHWidmpYJCnsv/e26ZcEfXdEJRd7YAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgAWstwdlkozdZ9g2jXevvHsgIHnUoJwnYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAhawgPUAWP0cJMnYZS55neREaruLtneaJB5YwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABSxgAessB7tDJ3kXNx4NmeN7boz1L5Nto2XxHxRgAQtYwAIWsIAFLGABC1jAAhawgAUsYAELWMACFrCABazANrmN/K0jFMlxeOMvnMO67Q0CC1jAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGDlH/GN2//929tvbO0nf+HcmO8HHVjAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGC9B1YbjrvT5sbh3n8gYO7JzwEx9waTCx6wgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGABC1jAAlbnBvPubz717bvXaRvubU9sdxnYHQnAAhawgAUsYAELWMACFrCABSxgAQtYwAIWsIAFLGAB65tgSRKwJAFLkoAlScCSBCxJApYkAUsSsCQJWJIELEnAkiRgSRKwJAFLkoAlScCSBCxJApYkAUsSsCQJWJIELEnAkiRgSRKwJAFLkoAlScCSdE3/ASXrisvn1GDnAAAAAElFTkSuQmCC";
        byte[] bArr = Base64.getMimeDecoder().decode(data);
        byteToImage(bArr, "d:/photo/fromByteArray.png");

    }

    // byte数组转图片
    public static void byteToImage(byte[] data, String path) {
        if(data.length < 3 || path.equals("")) {
            System.out.println("传入参数不合法");
            return;
        }
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("图片位置在：" + path);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("byte数组转图片失败");
        }
    }
}

