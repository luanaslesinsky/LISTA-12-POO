package br.com.technexus.desafio;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

// Desafio de Engenharia / Seguranca: quebra a senha "lamXXX" por forca bruta
public class ForcaBruta {

    public static void main(String[] args) {
        String encryptedB64 = "U2FsdGVkX189CvKETNa+k2wHIMpbCwNk7HKB3nBRzOD9bBaPt2nFMCdElvKoRfTmmqVv41Trh37ORXFWRVNOX3vpgPHULkkaoyh9DfmzrGBXkGnu/SJfQkGuU08zbgMQNSwCGTwoIHkUMzRFQELN0Q==";
        encryptedB64 = encryptedB64.replaceAll("\\s", "");

        byte[] fullData = Base64.getDecoder().decode(encryptedB64);

        // Cabecalho "Salted__" (8 bytes) + Salt (8 bytes) + texto cifrado
        byte[] salt = Arrays.copyOfRange(fullData, 8, 16);
        byte[] cipherTextBytes = Arrays.copyOfRange(fullData, 16, fullData.length);

        String charset = "abcdefghijklmnopqrstuvwxyz0123456789";

        System.out.println("Iniciando forca bruta no cofre da TechNexus...");
        long startTime = System.currentTimeMillis();

        // A senha comeca com "lam" + 3 caracteres desconhecidos (36^3 = 46.656 combinacoes)
        for (char c1 : charset.toCharArray()) {
            for (char c2 : charset.toCharArray()) {
                for (char c3 : charset.toCharArray()) {
                    String testPass = "lam" + c1 + c2 + c3;
                    try {
                        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                        PBEKeySpec spec = new PBEKeySpec(testPass.toCharArray(), salt, 1000, 48 * 8);
                        byte[] keyAndIv = factory.generateSecret(spec).getEncoded();

                        byte[] key = Arrays.copyOfRange(keyAndIv, 0, 32);
                        byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);

                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

                        byte[] decryptedBytes = cipher.doFinal(cipherTextBytes);
                        String result = new String(decryptedBytes, StandardCharsets.UTF_8);

                        if (result.contains("http")) {
                            long endTime = System.currentTimeMillis();
                            System.out.println("\nCOFRE ABERTO!");
                            System.out.println("Senha encontrada: " + testPass);
                            System.out.println("URL secreta: " + result.trim());
                            System.out.println("Tempo de execucao: " + (endTime - startTime) + "ms");
                            return;
                        }
                    } catch (Exception e) {
                        // Senha errada -> BadPaddingException. Ignora e segue tentando a proxima.
                    }
                }
            }
        }

        System.out.println("\nForca bruta concluida. Senha nao encontrada.");
    }
}
