package dagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
@Documented
public @interface SymEncrypt {
    String algorithm() default EncryptConstant.AES;
    String blockMode() default EncryptConstant.CBC;
    String paddingMode() default EncryptConstant.PKCS5PADDING;
    int keySize() default 128;
}
