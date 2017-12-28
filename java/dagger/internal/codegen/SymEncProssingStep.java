package dagger.internal.codegen;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import dagger.SymEncrypt;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

import static javax.lang.model.util.ElementFilter.typesIn;

public class SymEncProssingStep implements BasicAnnotationProcessor.ProcessingStep  {

    SymEncGenerator generator;
    Messager messager;

    public SymEncProssingStep(SymEncGenerator generator, Messager messager) {
        this.generator = generator;
        this.messager = messager;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(SymEncrypt.class);
    }

    @Override
    public Set<? extends Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

        for(TypeElement typeElement: typesIn(elementsByAnnotation.values())){
            try{
                SymEncPara para = SymEncPara.builder().setClassName(ClassName.get(typeElement).simpleName()).setTypeElement(typeElement).build();
                generator.generate(para);
            }catch(SourceFileGenerationException e){
                e.printMessageTo(messager);
            }

        }
        return ImmutableSet.of();
    }
}
