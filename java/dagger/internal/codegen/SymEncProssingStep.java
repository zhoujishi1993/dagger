package dagger.internal.codegen;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import dagger.EncryptConstant;
import dagger.SymEncrypt;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import static javax.lang.model.util.ElementFilter.typesIn;

public class SymEncProssingStep implements BasicAnnotationProcessor.ProcessingStep  {

    SymEncGenerator generator;
    SymEncValidator validator;
    Messager messager;

    public SymEncProssingStep(SymEncGenerator generator, SymEncValidator validator, Messager messager) {
        this.generator = generator;
        this.validator = validator;
        this.messager = messager;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(SymEncrypt.class);
    }

    //TODO find place to add validator
    @Override
    public Set<? extends Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

        for(TypeElement typeElement: typesIn(elementsByAnnotation.values())){
            try{
                ValidationReport<TypeElement> report = validator.validateAnnotationParameter(typeElement);
                report.printMessagesTo(messager);
                if(report.isClean()){
                    Optional<AnnotationMirror> mirror = MoreElements.getAnnotationMirror(typeElement, SymEncrypt.class);
                    if(mirror.isPresent()){
                        SymEncPara.Builder paraBuilder = SymEncPara.builder().setClassName(ClassName.get(typeElement).simpleName()).setTypeElement(typeElement)
                                .setAlgorithm(EncryptConstant.AES).setBlockMode(EncryptConstant.CBC).setPaddingMode(EncryptConstant.PKCS5PADDING).setKeySize(EncryptConstant.KEYSIZE);

                        for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.get().getElementValues().entrySet()){
                            Name elementName = entry.getKey().getSimpleName();
                            if(elementName.contentEquals("algorithm")){
                                paraBuilder = paraBuilder.setAlgorithm((String)(entry.getValue().getValue()));
                            }else if(elementName.contentEquals("blockMode")){
                                paraBuilder = paraBuilder.setBlockMode((String)(entry.getValue().getValue()));
                            }else if(elementName.contentEquals("paddingMode")){
                                paraBuilder = paraBuilder.setPaddingMode((String)(entry.getValue().getValue()));
                            }else if(elementName.contentEquals("keySize")){
                                paraBuilder = paraBuilder.setKeySize((Integer)(entry.getValue().getValue()));
                            }else{
                                //TODO deal with this error
                            }
                        }
                        generator.generate(paraBuilder.build());
                    }
                }
            }catch(SourceFileGenerationException e){
                e.printMessageTo(messager);
            }

        }
        return ImmutableSet.of();
    }
}
