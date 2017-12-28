package dagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * a Hello world annotation
 * @author zhoujishi
 * @since 2.0
 */
@Retention(RUNTIME) // Allows runtimes to have specialized behavior interoperating with Dagger.
@Target(TYPE)
@Documented
public @interface HelloWorld {
    String value();
}
