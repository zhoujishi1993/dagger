package dagger.internal.codegen;

import com.google.auto.value.AutoValue;

import javax.lang.model.element.TypeElement;

@AutoValue
public abstract class SymEncPara {
    static Builder builder(){
        return new AutoValue_SymEncPara.Builder();
    }

    abstract TypeElement typeElement();

    abstract String className();

    abstract String algorithm();

    abstract String blockMode();

    abstract String paddingMode();

    abstract int keySize();

    @AutoValue.Builder
    abstract static class Builder{
        abstract Builder setTypeElement(TypeElement typeElement);

        abstract Builder setClassName(String className);

        abstract Builder setAlgorithm(String algorithm);

        abstract Builder setBlockMode(String blockMode);

        abstract Builder setPaddingMode(String paddingMode);

        abstract Builder setKeySize(int keySize);

        abstract SymEncPara build();
    }
}
