package dagger.internal.codegen;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import dagger.HelloWorld;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

/**
 * a hello world generation step
 * @author zhoujishi
 * @since 2.0
 */

public class HelloWorldProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private HelloWorldGenerator generator;
    private Messager messager;

    public HelloWorldProcessingStep(HelloWorldGenerator generator, Messager messager) {
        this.generator = generator;
        this.messager = messager;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(HelloWorld.class);
    }

    @Override
    public Set<? extends Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

//        for(Map.Entry<Class<? extends Annotation>, Element> elementByAnnotation : elementsByAnnotation.entries()){
//
//        }
        for(TypeElement typeElement: typesIn(elementsByAnnotation.values())){
            try{

                Optional<AnnotationMirror> mirror = MoreElements.getAnnotationMirror(typeElement, HelloWorld.class);
                if(mirror.isPresent()){
                    for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: mirror.get().getElementValues().entrySet()){
                        String para = (String)entry.getValue().getValue();
                        HelloWorldPara helloWorldPara = HelloWorldPara.builder().setTypeElement(typeElement).setPara(para).build();
                        generator.generate(helloWorldPara);
                    }
                }
//                generator.generate(typeElement);
            }catch(SourceFileGenerationException e){
                e.printMessageTo(messager);
            }

        }
        return ImmutableSet.of();
    }
}
