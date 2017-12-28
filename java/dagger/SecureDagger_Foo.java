package dagger;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureDagger_Foo {
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;
    private String algorithms;
    private Cipher cipher;
    private static volatile SecureDagger_Foo instance;
    private static Object syn = new Object();

    private SecureDagger_Foo(){
        try{
            SecureRandom secureRandom = new SecureRandom();
            this.ivParameterSpec = new IvParameterSpec(secureRandom.generateSeed(16));
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            this.secretKey =  keyGenerator.generateKey();
            this.algorithms = "AES/CBC/PKCS5Padding";
            this.cipher = Cipher.getInstance(algorithms);
        }catch (NoSuchAlgorithmException | NoSuchPaddingException e){
            e.printStackTrace();
        }
    }

    public String encrypt(String input){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(input.getBytes()));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String input){
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(input.getBytes())));
        }catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public String getAlgorithms() {
        return algorithms;
    }

    public static SecureDagger_Foo getInstance(){
        if(instance == null){
            synchronized (syn){
                if(instance == null){
                    instance = new SecureDagger_Foo();
                }
            }
        }
        return instance;
    }
}
