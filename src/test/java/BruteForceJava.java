import com.milbar.Utils;
import kotlin.text.Charsets;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BruteForceJava {

    Cipher cipher;
    byte[] plainText;
    byte[] keyBytes;
    SecretKeyFactory secretKeyFactory;
    SecretKey key;

    @Before
    public void setup() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        cipher = Cipher.getInstance("DES/ECB/NoPadding");
        plainText = "KotlinIs".getBytes(Charsets.UTF_8);
        //keyBytes = "SuperKey".getBytes(Charsets.UTF_8);
        keyBytes = new byte[]{2, 2, 2, 2, 2, 2, 2, 2};
        secretKeyFactory = SecretKeyFactory.getInstance("DES");
        //TODO generate random and check how it looks
        //TODO test with all 1 and all 0
        key = secretKeyFactory.generateSecret(new DESKeySpec(keyBytes));

    }

    @Test
    public void testCastIntToByte() {
        byte value = (byte) 127;
        System.out.println("For value " + value);
        String s1 = String.format("Binary: %8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
        System.out.println(s1);
    }

    @Test
    public void bruteForceDES() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText);
        System.out.printf("Decrypted = %s", new String(encrypted));

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encrypted);
        System.out.printf("Decrypted = %s%n", new String(decrypted));

        System.out.println("Key encoded: " + Utils.byteArrayToHex(key.getEncoded()));
        System.out.println("Key in bytes: " + Utils.byteArrayToHex(keyBytes));
        assertEquals(Utils.byteArrayToHex(keyBytes), Utils.byteArrayToHex(key.getEncoded()));


        byte[] bytes = new byte[8];
        long iteration = 0;
        for (int i = 0; i <= 127; i++) {
            setValueAndParityBit((byte) i, bytes, 0);
            for (int j = 0; j <= 127; j++) {
                setValueAndParityBit((byte) j, bytes, 1);
                for (int k = 0; k <= 127; k++) {
                    setValueAndParityBit((byte) k, bytes, 2);
                    for (int l = 0; l <= 127; l++) {
                        setValueAndParityBit((byte) l, bytes, 3);
                        for (int m = 0; m <= 127; m++) {
                            setValueAndParityBit((byte) m, bytes, 4);
                            for (int n = 0; n <= 127; n++) {
                                setValueAndParityBit((byte) n, bytes, 5);
                                for (int o = 0; o <= 127; o++) {
                                    setValueAndParityBit((byte) o, bytes, 6);
                                    for (int p = 0; p <= 127; p++) {
                                        setValueAndParityBit((byte) p, bytes, 7);

                                        if (iteration == 1 || iteration == 2 || iteration == 3 || iteration % 500000 == 0) {
                                            System.out.println("Iteration: " + iteration + " bytes: " + Utils.byteArrayToHex(bytes));
                                            String s1 = String.format("Binary:%8s_%8s_%8s_%8s",
                                                    Integer.toBinaryString(bytes[4] & 0xFF),
                                                    Integer.toBinaryString(bytes[5] & 0xFF),
                                                    Integer.toBinaryString(bytes[6] & 0xFF),
                                                    Integer.toBinaryString(bytes[7] & 0xFF)).replace(' ', '0');
                                            System.out.println(s1);
                                        }
                                        iteration++;

                                        SecretKey candidateKey = secretKeyFactory.generateSecret(new DESKeySpec(bytes));
                                        testDecryptWithKey(candidateKey, encrypted);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void setValueAndParityBit(byte value, byte[] bytes, int index) {
        bytes[index] = setParityBit(value);
//        bytes[index] = (byte) (value << 1); // Clear last bit
//        byte temp = value;
//        short setBits = 0;
//        while (temp > 0) {
//            temp &= temp - 1;
//            setBits++;
//        }
//        if (setBits % 2 == 1)// is odd number
//            bytes[index] = (byte) (value | 1); // Set parity bit to 1
    }

    @Test
    public void parityTest() {
        assertEquals(setParityBit((byte) 0), (byte) 0b0000_0);
        assertEquals(setParityBit((byte) 1), (byte) 0b0001_1);
        assertEquals(setParityBit((byte) 2), (byte) 0b0010_1);
        assertEquals(setParityBit((byte) 3), (byte) 0b0011_0);
        assertEquals(setParityBit((byte) 4), (byte) 0b0100_1);
        assertEquals(setParityBit((byte) 5), (byte) 0b0101_0);
        assertEquals(setParityBit((byte) 6), (byte) 0b0110_0);
        assertEquals(setParityBit((byte) 7), (byte) 0b0111_1);
        assertEquals(setParityBit((byte) 8), (byte) 0b1000_1);
        assertEquals(setParityBit((byte) 9), (byte) 0b1001_0);
    }

    private byte setParityBit(byte value) {
        value = (byte) (value << 1);

        byte temp = value;
        short setBits = 0;
        while (temp > 0) {
            temp &= temp - 1;
            setBits++;
        }
        if (setBits % 2 == 1)// is odd number
            value = (byte) (value | 1); // Set parity bit to 1
        return value;

    }

    private void testDecryptWithKey(SecretKey candidateKey, byte[] encrypted) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, candidateKey);
        byte[] decrypted = cipher.doFinal(encrypted);
        if (Arrays.equals(decrypted, plainText))
            throw new RuntimeException(String.format("Found key: %s", Utils.byteArrayToHex(candidateKey.getEncoded())));
    }
}
