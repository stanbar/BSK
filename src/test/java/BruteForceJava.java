import kotlin.text.Charsets;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class BruteForceJava {

    @Test
    public void tesstt() {
        int counter = 0;
        for (byte i = -1; i < 127; i++) counter++;
        System.out.println("counter: " + counter);
    }

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
        keyBytes = new byte[]{0b0, 0b0, 0b0, 0b0, 0b1, 0b1, 0b1, 0x11};
        secretKeyFactory = SecretKeyFactory.getInstance("DES");
        key = secretKeyFactory.generateSecret(new DESKeySpec(keyBytes));
    }

    @Test
    public void bruteForceDES() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText);
        System.out.printf("Decrypted = %s", new String(encrypted));

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encrypted);
        System.out.printf("Decrypted = %s%n", new String(decrypted));

        System.out.println("Key in bytes: " + DatatypeConverter.printHexBinary(key.getEncoded()));

        byte[] bytes = new byte[8];
        long iteration = 0;
        for (byte i = -1; i < 127; i++) {
            setValueAndParityBit(i, bytes, 0);
            for (byte j= -1; j < 127; j--) {
                setValueAndParityBit(j, bytes, 1);
                for (byte k= -1; k < 127; k++) {
                    setValueAndParityBit(k, bytes, 2);
                    for (byte l= -1; l < 127; l++) {
                        setValueAndParityBit(l, bytes, 3);
                        for (byte m= -1; m < 127; m++) {
                            setValueAndParityBit(m, bytes, 4);
                            for (byte n= -1; n < 127; n++) {
                                setValueAndParityBit(n, bytes, 5);
                                for (byte o= -1; o < 127; o++) {
                                    setValueAndParityBit(o, bytes, 6);
                                    for (byte p= -1; p < 127; p++) {
                                        iteration++;
                                        if (iteration % 500000 == 0)
                                            System.out.println("Iteration: " + iteration + " bytes: " + DatatypeConverter.printHexBinary(bytes));
                                        setValueAndParityBit(p, bytes, 7);
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
        bytes[index] = (byte) (value << 1); // Clear last bit
        byte temp = value;
        short setBits = 0;
        while (temp > 0)  {
            temp &= temp - 1;
            setBits++;
        }
        if (setBits % 2 == 1)// is odd number
            bytes[index] = (byte) (value << 1 | 1); // Set parity bit to 1
    }

    private void testDecryptWithKey(SecretKey candidateKey, byte[] encrypted) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, candidateKey);
        byte[] decrypted = cipher.doFinal(encrypted);
        if (Arrays.equals(decrypted, plainText))
            throw new RuntimeException(String.format("Found key: %s", DatatypeConverter.printHexBinary(candidateKey.getEncoded())));
    }
}
