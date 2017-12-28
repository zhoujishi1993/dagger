package dagger.internal.codegen;

import com.google.auto.value.AutoValue;


import javax.lang.model.element.TypeElement;

@AutoValue
public abstract class HelloWorldPara {
    static Builder builder(){
        return new AutoValue_HelloWorldPara.Builder();
    }

    abstract String para();

    abstract TypeElement typeElement();

    @AutoValue.Builder
    abstract static class Builder{
        abstract Builder setPara(String para);

        abstract Builder setTypeElement(TypeElement typeElement);

        abstract HelloWorldPara build();
    }
}
