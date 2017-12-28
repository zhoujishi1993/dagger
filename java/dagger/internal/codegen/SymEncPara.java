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

    @AutoValue.Builder
    abstract static class Builder{
        abstract Builder setTypeElement(TypeElement typeElement);

        abstract Builder setClassName(String className);

        abstract SymEncPara build();
    }
}
