package dagger.internal.codegen;

import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import dagger.EncryptConstant;
import dagger.SymEncrypt;

import javax.lang.model.element.*;
import javax.lang.model.util.Types;
import java.util.Map;

/**
 * this class may need refactor but not now
 */
public class SymEncValidator {
    private final DaggerElements elements;
    private final Types types;

    private ImmutableSet<String> algorithmSet;
    private ImmutableSet<String> blockModeSet;
    private ImmutableSet<String> paddingModeSet;
    private ImmutableSet<Integer> keySizeSet;


    static final String NO_SUCH_PAPR = "no such parameter %s for %s";
    static final String INSECURE_BLOCK_MODE = "in secure block mode ECB for encryption, please use CBC instead";

    public SymEncValidator(DaggerElements elements, Types types) {
        this.elements = elements;
        this.types = types;
        this.algorithmSet = ImmutableSet.<String>builder().add(EncryptConstant.AES).add(EncryptConstant.DES).build();
        this.blockModeSet = ImmutableSet.<String>builder().add(EncryptConstant.CBC).add(EncryptConstant.ECB).build();
        this.paddingModeSet = ImmutableSet.<String>builder().add(EncryptConstant.PKCS5PADDING).build();
        this.keySizeSet = ImmutableSet.<Integer>builder().add(32).add(64).add(128).build();
    }

    //This function is a little long, how to simplify it ?
    public ValidationReport<TypeElement> validateAnnotationParameter(TypeElement typeElement){
        ValidationReport.Builder<TypeElement> builder = ValidationReport.about(typeElement);
        Optional<AnnotationMirror> annotationMirror = MoreElements.getAnnotationMirror(typeElement, SymEncrypt.class);
        if(annotationMirror.isPresent()){
            for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.get().getElementValues().entrySet()){
                Name elementName = entry.getKey().getSimpleName();
                if(elementName.contentEquals("algorithm")){
                    String value = (String)(entry.getValue().getValue());
                    if(!algorithmSet.contains(value)){
                        builder.addError(String.format(NO_SUCH_PAPR, value, "algorithm"), typeElement);
                    }else{
                        //Other error are not specified yet
                    }
                }else if(elementName.contentEquals("blockMode")){
                   String value = (String)(entry.getValue().getValue());
                   if(!blockModeSet.contains(value)){
                       builder.addError(String.format(NO_SUCH_PAPR, value, "block mode"), typeElement);
                   }else{
                       if(value.equals(EncryptConstant.ECB)){
                           builder.addError(INSECURE_BLOCK_MODE, typeElement);
                       }
                   }
                }else if(elementName.contentEquals("paddingMode")){
                    String value = (String)(entry.getValue().getValue());
                    if(!paddingModeSet.contains(value)){
                        builder.addError(String.format(NO_SUCH_PAPR, value, "paddingMode"), typeElement);
                    }
                }else if(elementName.contentEquals("keySize")){
                    Integer keySize = (Integer)(entry.getValue().getValue());
                    if(!keySizeSet.contains(keySize)){
                        builder.addError(String.format(NO_SUCH_PAPR, String.valueOf(keySize), "keySize"), typeElement);
                    }
                }else{
                    //TODO deal with this error
                }
            }
        }
        return builder.build();
    }


}
