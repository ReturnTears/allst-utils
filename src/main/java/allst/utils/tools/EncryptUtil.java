package allst.utils.tools;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 安全处理工具类
 * @author June
 * @since 2021年09月
 */
public class EncryptUtil {
    //	private static byte[] DES_KEY=null ;
    //	private static int DES_LEN=512;
    //	private static int RSA_LEN=1024;
    private static SecretKeyFactory desKeyFactory = null;
    private final static String DES = "DES";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static String encodeBase64(byte[] buf) {
        return Base64.getEncoder().encodeToString(buf);
    }

    /**
     * 此方法中是使用ISO-8859-1编码
     * 因为加密后内容只可能是asiic编码,所以编码无所谓.
     * @param data
     * @return
     */
    public static byte[] decodeBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static byte[] decodeBase64(byte[] data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * 配套使用
     */
    public static String encodeSafeBase64(byte[] buf) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        String s = encodeBase64(buf);
        return s.replace('=', '.').replace('/', '_').replace('+', '-');
    }

    /**
     * 配套使用
     */
    public static String encodeSafeBase64(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        return encodeSafeBase64(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 配套使用
     */
    public static String decodeSafeBase64(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        s = s.replace('.', '=').replace('_', '/').replace('-', '+');
        return new String(decodeBase64(s), StandardCharsets.UTF_8);
    }

    /**
     * 配套使用
     * @param s
     * @return
     */
    public static byte[] decodeSafeByteBase64(String s) {
        if (s == null || s.length() == 0) {
            return new byte[0];
        }
        s = s.replace('.', '=').replace('_', '/').replace('-', '+');
        return decodeBase64(s);
    }

    /**
     * 使用md5签名指定的内容,结果为十六进制数据
     * @param s
     * @return
     */
    public static String digestMd5(String s) {
        byte[] reslut = digestMd5(s.getBytes());
        return NumberUtil.parseByteToHex(reslut);
    }

    /**
     * 使用md5签名指定的内容
     * @return
     */
    public static byte[] digestMd5(byte[] buf) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest(buf);
    }

    /**
     * 使用md5加密流
     */
    public static byte[] digestMd5(InputStream is) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
            byte[] buf = new byte[1024 * 8];
            int n = 0;
            while ((n = is.read(buf)) > 0) {
                digest.update(buf, 0, n);
            }
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String digestMd5s(InputStream is) {
        byte[] buf = digestMd5(is);
        return NumberUtil.parseByteToHex(buf);
    }

    public static void main(String[] args) throws Exception {
        String data = "";
        for (int i = 0; i < 1000; i++) {
            data += String.valueOf(i);
        }

        String s3 = "";
        String s4 = "";
        //		data="洒家会中文....";
        //		System.out.println("数据长度:"+data.getBytes().length);
        System.out.println("元数据:" + data.length() + ":" + data.length() + ":" + data);

        String key = createRandomDESKeyString(1024);
        for (int i = 0; i < 10; i++) {
            s3 = encryptDES(data, key);
            s4 = decryptDES(s3, key);
        }
        System.out.println("---------DES---------");
        System.out.println("密匙:" + key.length() + ":" + key);
        System.out.println("加密:" + s3.length() + ":" + s3);
        System.out.println("解密:" + s4.length() + ":" + s4);

        System.out.println("--------RSA-----------");

        KeyPairByte pair = createRandomRSAKeyPair(1024);
        System.out.println("公匙:" + pair.getPublicHex().length() + ":" + pair.getPublicHex());
        System.out.println("密匙:" + pair.getPrivateHex().length() + ":" + pair.getPrivateHex());

        //		s3=encryptRSA(data, pair.getPrivateHex());
        //		System.out.println("加密:"+s3.length()+":"+s3);
        //		s4=decryptRSA(s3, pair.getPublicHex());

        //		byte[] temp=encryptRSA(data.getBytes(), pair.getPublic());
        //
        //		s3=NumberUtil.parseByteToHex(temp);
        //		System.out.println("加密:"+s3.length()+":"+s3);
        //
        //		temp=decryptRSA(temp, pair.getPrivate());
        //
        //		s4=NumberUtil.parseByteToHex(temp);
        //
        //		System.out.println("解密:"+temp.length+":"+new String(temp));
        //		System.out.println("解密:"+s4.length()+":"+s4);

        s3 = encryptRSA(data, pair.getPublicHex());
        s4 = decryptRSA(s3, pair.getPrivateHex());
        System.out.println("加密:" + s3.length() + ":" + s3);
        System.out.println("解密:" + s4.length() + ":" + s4);

    }

    /**
     * 使用des加密数据,产生的数据位数是不小于当前数的8的倍数
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptDES(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(buildDESKey(key));
        SecretKey securekey = getDESKeyFactroy().generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    public static byte[] encryptDES(byte[] src, byte[] key, byte[] iv) throws Exception {
        IvParameterSpec spec = new IvParameterSpec(iv);
        DESKeySpec dks = new DESKeySpec(buildDESKey(key));
        SecretKey securekey = getDESKeyFactroy().generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, securekey, spec);
        return cipher.doFinal(src);
    }

    /**
     * 使用des解密数据,数据必须是8的倍数,如果key非8的倍数,则自动补齐为8的倍数
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptDES(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(buildDESKey(key));
        SecretKey securekey = getDESKeyFactroy().generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    public static byte[] decryptDES(byte[] src, byte[] key, byte[] iv) throws Exception {
        IvParameterSpec spec = new IvParameterSpec(iv);
        DESKeySpec dks = new DESKeySpec(buildDESKey(key));
        SecretKey securekey = getDESKeyFactroy().generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey, spec);
        return cipher.doFinal(src);
    }

    /**
     * 构建des加密的key,如果长度不是8的倍数,则补齐成8的倍数
     * @param key
     * @return
     */
    public static byte[] buildDESKey(byte[] key) {
        if (key.length % 8 == 0) return key;
        int len = ((key.length - 1) / 8 + 1) * 8;
        byte[] bb = new byte[len];
        Arrays.fill(bb, (byte) 0);
        System.arraycopy(key, 0, bb, 0, key.length);
        return bb;
    }

    /**
     * 构建des加密的key,如果长度不是16的倍数,则补齐成8的倍数
     * @param key
     * @return
     */
    public static byte[] buildAESKey(byte[] key) {
        if (key.length % 16 == 0) return key;
        int len = ((key.length - 1) / 16 + 1) * 16;
        byte[] bb = new byte[len];
        Arrays.fill(bb, (byte) 0);
        System.arraycopy(key, 0, bb, 0, key.length);
        return bb;
    }

    /**
     * 使用des加密,产生的数据位数是8*2的倍数,十六进制
     * @param data
     * @return
     */
    public static String encryptDES(String data, String key) {
        try {
            return NumberUtil.parseByteToHex(encryptDES(data.getBytes(), key.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用des解密
     * @param data 格式:长度必须是8*2的倍数,每两位表示一个十六进制
     * @return
     */
    public static String decryptDES(String data, String key) {
        try {
            return new String(decryptDES(NumberUtil.parseHexToByte(data), key.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey buildRSAPublicKey(byte[] buf) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(buf);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey buildRSAPrivateKey(byte[] buf) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(buf);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 进行RSA处理. 加密或者解密
     * @param data
     * @param key
     * @param mode
     * @return
     */
    public static byte[] doRSA(Key key, byte[] data, int mode) {
        try {
            Cipher deCipher = Cipher.getInstance("RSA");
            deCipher.init(mode, key);

            int blockSize = 0;
            if (mode == Cipher.DECRYPT_MODE) {
                blockSize = data.length / 8 * 8;
            } else {
                blockSize = MAX_ENCRYPT_BLOCK;
            }
            //System.out.println("blockSize:"+blockSize);
            //blockSize = (mode == Cipher.DECRYPT_MODE ? MAX_DECRYPT_BLOCK*2 : MAX_ENCRYPT_BLOCK);
            int blockNum = (data.length - 1) / blockSize + 1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int offset = 0;
            byte[] temp = null;
            for (int i = 0; i < blockNum; i++) {
                offset = i * blockSize;
                if (offset + blockSize >= data.length) {
                    temp = deCipher.doFinal(data, offset, data.length - offset);
                } else {
                    temp = deCipher.doFinal(data, offset, blockSize);
                }
                bos.write(temp);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用公匙加密数据
     * @param data
     */
    public static byte[] encryptRSA(byte[] data, byte[] publicKey) {

        try {
            PublicKey puk = buildRSAPublicKey(publicKey);
            Cipher enCipher = Cipher.getInstance("RSA");
            enCipher.init(Cipher.ENCRYPT_MODE, puk);

            int blockSize = MAX_ENCRYPT_BLOCK;
            int blockNum = (data.length - 1) / blockSize + 1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] temp = null;
            int offset = 0;
            for (int i = 0; i < blockNum; i++) {
                offset = i * blockSize;
                if (offset + blockSize >= data.length) {
                    temp = enCipher.doFinal(data, offset, data.length - offset);
                } else {
                    temp = enCipher.doFinal(data, offset, blockSize);
                }
                bos.write(temp);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 密匙必须是十六进制
     */
    public static String encryptRSA(String data, String pukkey) {
        return NumberUtil.parseByteToHex(encryptRSA(data.getBytes(), NumberUtil.parseHexToByte(pukkey)));
    }

    /**
     * 使用使用私匙解密数据
     */
    public static byte[] decryptRSA(byte[] data, byte[] privateKey) {

        try {
            PrivateKey prk = buildRSAPrivateKey(privateKey);
            Cipher deCipher = Cipher.getInstance("RSA");
            deCipher.init(Cipher.DECRYPT_MODE, prk);

            int blockSize = data.length / 8 * 8;
            ;
            int blockNum = (data.length - 1) / blockSize + 1;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int offset = 0;
            byte[] temp = null;
            for (int i = 0; i < blockNum; i++) {
                offset = i * blockSize;
                if (offset + blockSize >= data.length) {
                    temp = deCipher.doFinal(data, offset, data.length - offset);
                } else {
                    temp = deCipher.doFinal(data, offset, blockSize);
                }
                bos.write(temp);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密数据必须是十六进制,公匙必须是十六进
     */
    public static String decryptRSA(String data, String priKey) {
        return new String(decryptRSA(NumberUtil.parseHexToByte(data), NumberUtil.parseHexToByte(priKey)));
    }

    /**
     * 创建指定长度的密匙对
     * @param len
     * @return
     */

    public static KeyPairByte createRandomRSAKeyPair(int len) {
        try {
            if (len % 8 != 0) throw new RuntimeException("Can't create rsa keypair,length not multiple of 8");
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(len);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            return new KeyPairByte(privateKey.getEncoded(), publicKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        //		return null;
    }

    /**
     * 获取随机DESkey
     */
    public static byte[] createRandomDESKey(int len) {
        if (len % 8 != 0) throw new RuntimeException("Can't create des key,length not multiple of 8");
        byte[] desKey = new byte[len];
        for (int i = 0; i < len; i++) {
            desKey[i] = (byte) NumberUtil.getRandom(0, 256);
        }
        return desKey;
    }

    /**
     * 获取随机DESKey
     * @param len
     * @return
     */
    public static String createRandomDESKeyString(int len) {
        if (len % 8 != 0) throw new RuntimeException("Can't create des key,length not multiple of 8");
        return NumberUtil.parseByteToHex(createRandomDESKey(len / 2));
    }

    /**
     * 获取des加密工厂对象
     * @return
     */
    private static SecretKeyFactory getDESKeyFactroy() {
        try {
            if (desKeyFactory == null) {
                synchronized (EncryptUtil.class) {
                    if (desKeyFactory == null) desKeyFactory = SecretKeyFactory.getInstance(DES);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return desKeyFactory;
    }

    //	public static void test(){
    //		Base
    //	}
    //
    public static class KeyPairByte {
        private byte[] priKey = null;
        private byte[] pubKey = null;

        private String priKeyHex = null;
        private String pubKeyHex = null;

        public KeyPairByte(byte[] pri, byte[] pub) {
            this.priKey = pri;
            this.pubKey = pub;
        }

        public KeyPairByte(String priKeyHex, String pubKeyHex) {
            this.priKeyHex = priKeyHex;
            this.pubKeyHex = pubKeyHex;
            this.priKey = NumberUtil.parseHexToByte(priKeyHex);
            this.pubKey = NumberUtil.parseHexToByte(pubKeyHex);
        }

        public byte[] getPublic() {
            return pubKey;
        }

        public String getPublicHex() {
            if (pubKeyHex == null) pubKeyHex = NumberUtil.parseByteToHex(pubKey);
            return pubKeyHex;
        }

        public byte[] getPrivate() {
            return priKey;
        }

        public String getPrivateHex() {
            if (priKeyHex == null) priKeyHex = NumberUtil.parseByteToHex(priKey);
            return priKeyHex;
        }
    }
}
