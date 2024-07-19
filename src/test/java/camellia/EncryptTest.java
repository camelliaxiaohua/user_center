package camellia;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密测试
 * @Datetime: 2024/7/10下午5:43
 * @author: Camellia.xioahua
 */

@SpringBootTest
public class EncryptTest {

    /**
     * 测试使用 MD5 算法进行摘要计算。
     *
     * @throws NoSuchAlgorithmException 如果指定的算法（MD5）不可用时抛出此异常
     */
    @Test
    void testDigest() throws NoSuchAlgorithmException {
        // 获取 MD5 摘要算法的实例
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        // 对字符串 "camellia24211" 进行编码并计算其摘要
        byte[] digest = md5.digest("camellia24211".getBytes());

        // 将摘要字节数组转换为十六进制字符串表示
        String digestHex = bytesToHex(digest);

        // 打印计算得到的摘要字符串
        System.out.println(digestHex);
    }


    /**
     * 将字节数组转换为十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b); // 将每个字节转换为两位十六进制表示
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 测试使用 MD5 算法计算字符串的十六进制摘要。<br>
     * 校验方法是将要验证的密码加密，判断与已加密的密码是否一致。<br>
     */
    @Test
    void testDigestAsHex() {
        // 原始密码，待加密的明文密码
        String rawPassword = "Camellia24211";
        // 盐值，用于增强密码的安全性
        String SALT = "a";

        // 使用 Apache Commons Codec 提供的 DigestUtils 计算字符串 "a" + "Camellia24211" 的 MD5 摘要并转换为十六进制字符串
        String encodedPassword = DigestUtils.md5DigestAsHex((SALT + rawPassword).getBytes());

        // 打印加密后的密码
        System.out.println("Encoded Password: " + encodedPassword);

        // 验证密码，判断原始密码加盐后的 MD5 摘要与加密后的密码是否匹配
        String checkPassword = DigestUtils.md5DigestAsHex((SALT + rawPassword).getBytes());
        boolean matches = encodedPassword.equals(checkPassword);

        // 打印验证结果
        System.out.println("Password Matches: " + matches);
    }


    /**
     * rawPassword: 这是需要加密的原始密码。在实际应用中，这个密码通常是用户输入的明文密码。<br>
     * BCryptPasswordEncoder 是 Spring Security 提供的一个类，用于对密码进行加密和验证。
     * 它使用 BCrypt 加密算法，该算法通过一个内置的 salt（盐）机制来增强密码的安全性。<br>
     */
    /*@Test
    void testBCrypt() {
        // 原始密码，待加密的明文密码
        String rawPassword = "camellia24211";

        // 创建一个 BCryptPasswordEncoder 实例，用于密码加密和验证
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 使用 BCryptPasswordEncoder 对原始密码进行加密
        String encodedPassword = encoder.encode(rawPassword);

        // 打印加密后的密码
        System.out.println("Encoded Password: " + encodedPassword);

        // 验证密码，判断原始密码与加密后的密码是否匹配
        boolean matches = encoder.matches(rawPassword, encodedPassword);

        // 打印验证结果
        System.out.println("Password Matches: " + matches);
    }
*/

    @Test
    void testArgon2() {
        // 原始密码，待加密的明文密码
        String rawPassword = "password123";

        // 创建一个 Argon2 实例，用于密码加密和验证
        Argon2 argon2 = Argon2Factory.create();

        try {
            // 使用 Argon2 对原始密码进行加密
            // 这里的参数分别是：迭代次数、内存消耗（KB）、并行度（线程数）、待加密的密码字符数组
            String hashedPassword = argon2.hash(10, 65536, 1, rawPassword.toCharArray());

            // 打印加密后的密码
            System.out.println("Hashed Password: " + hashedPassword);

            // 验证密码，判断原始密码与加密后的密码是否匹配
            boolean matches = argon2.verify(hashedPassword, rawPassword.toCharArray());

            // 打印验证结果
            System.out.println("Password Matches: " + matches);
        } finally {
            // 清理 Argon2 实例，确保密码数据不会在内存中残留
            argon2.wipeArray(rawPassword.toCharArray());
        }
    }

}
