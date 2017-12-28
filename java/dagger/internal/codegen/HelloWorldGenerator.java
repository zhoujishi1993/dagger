package dagger.internal.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Optional;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;


/**
 * a hello world generator
 * @author zhoujishi
 * @since 2.0
 */
public class HelloWorldGenerator extends SourceFileGenerator<HelloWorldPara>{


    public HelloWorldGenerator(Filer filer, Elements elements) {
        super(filer, elements);
    }

    //return the class name of generated type
    @Override
    ClassName nameGeneratedType(HelloWorldPara input) {
        ClassName enclosingClassName = ClassName.get(input.typeElement());
        return enclosingClassName
                .topLevelClassName()
                .peerClass(
                        "HelloWorld");
    }

    @Override
    Optional<? extends Element> getElementForErrorReporting(HelloWorldPara input) {
        return Optional.empty();
    }

    @Override
    Optional<TypeSpec.Builder> write(ClassName generatedTypeName, HelloWorldPara input) {
        TypeSpec.Builder builder = classBuilder(generatedTypeName).addModifiers(Modifier.PUBLIC);
        MethodSpec.Builder methodBuilder = methodBuilder("HelloWorld").addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        methodBuilder.addCode(CodeBlock.of("System.out.println(\"Hello World " + input.para() + "\");"));
        builder.addMethod(methodBuilder.build());
        return Optional.of(builder);
    }

}
