package dagger.internal.codegen;

import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.Arrays.stream;

public class SymEncGenerator extends SourceFileGenerator<SymEncPara> {
    public static final String PREFIX = "SecureDagger_";

    private DaggerTypes types;

    public SymEncGenerator(Filer filer, DaggerTypes types, Elements elements) {
        super(filer, elements);
        this.types = types;
    }

    @Override
    ClassName nameGeneratedType(SymEncPara input) {
        ClassName enclosingClassName = ClassName.get(input.typeElement());

        return enclosingClassName.topLevelClassName().peerClass(PREFIX + input.className());
    }

    @Override
    Optional<? extends Element> getElementForErrorReporting(SymEncPara input) {
        return Optional.empty();
    }

    @Override
    Optional<TypeSpec.Builder> write(ClassName generatedTypeName, SymEncPara input) {
        TypeSpec.Builder builder = classBuilder(generatedTypeName).addModifiers(Modifier.PUBLIC);
        writeField(builder, input);
        writeConstructors(builder, input);
        writeMethods(builder, input);
        return Optional.of(builder);
    }


    private void writeField(TypeSpec.Builder builder, SymEncPara input){
        addField(builder, "javax.crypto", "SecretKey", "secretKey", Modifier.PRIVATE);
        addField(builder, "javax.crypto.spec", "IvParameterSpec", "ivParameterSpec", Modifier.PRIVATE);
        addField(builder, "java.lang", "String", "algorithms", Modifier.PRIVATE);
        addField(builder, "javax.crypto", "Cipher", "cipher", Modifier.PRIVATE);
        builder.addField(FieldSpec.builder(nameGeneratedType(input), "instance", Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE).build());
        builder.addField(FieldSpec.builder(ClassName.get("java.lang", "Object"), "syn").addModifiers(Modifier.PRIVATE,Modifier.STATIC).initializer(makeNewBlock(CodeBlock.of("$L", ClassName.get("java.lang", "Object")), Optional.empty())).build());
    }

    private void addField(TypeSpec.Builder builder, String packageName, String simpleName, String fieldName, Modifier... modifiers){
        builder.addField(FieldSpec.builder(ClassName.get(packageName, simpleName), fieldName).addModifiers(modifiers).build());
    }


    private void writeConstructors(TypeSpec.Builder builder, SymEncPara input){
        MethodSpec.Builder constructorBuilder = constructorBuilder().addModifiers(Modifier.PRIVATE);
        CodeBlock.Builder tryBuilder = CodeBlock.builder();
        ClassName secureRandomClass = ClassName.get("java.security", "SecureRandom");
        addStatement(tryBuilder, makeAssignBlock(secureRandomClass, "secureRandom", makeNewBlock(CodeBlock.of("$T", secureRandomClass), Optional.empty())));
        addStatement(tryBuilder, makeAssignThisBlock("ivParameterSpec", makeNewBlock(CodeBlock.of("$T", ClassName.get("javax.crypto.spec", "IvParameterSpec")), Optional.of(makeInvokeCodeBlock(CodeBlock.of("$L", "secureRandom"), "generateSeed", Optional.of(CodeBlock.of("16")))))));
        addStatement(tryBuilder, makeAssignBlock(ClassName.get("javax.crypto", "KeyGenerator"),"keyGenerator", makeInvokeCodeBlock(CodeBlock.of("$L", "KeyGenerator"), "getInstance", Optional.of(CodeBlock.of("\"AES\"")))));
        addStatement(tryBuilder, makeInvokeCodeBlock(CodeBlock.of("$L","keyGenerator"), "init", Optional.of(CodeBlock.of("128"))));
        addStatement(tryBuilder, makeAssignThisBlock("secretKey", makeInvokeCodeBlock(CodeBlock.of("$L", "keyGenerator"), "generateKey", Optional.empty())));
        //This line may have bugs because of $L
        addStatement(tryBuilder, makeAssignThisBlock( "algorithms", CodeBlock.of("\"AES/CBC/PKCS5Padding\"")));
        addStatement(tryBuilder, makeAssignThisBlock("cipher", makeInvokeCodeBlock(CodeBlock.of("$L", "Cipher"), "getInstance", Optional.of(CodeBlock.of("algorithms")))));



        constructorBuilder.addCode(makeTryCatchBlock(tryBuilder.build(), getDefaultCatchBlock(), ClassName.get("java.security", "NoSuchAlgorithmException"), ClassName.get("javax.crypto", "NoSuchPaddingException")));

        builder.addMethod(constructorBuilder.build());
    }

    //get the default catch block
    private CodeBlock getDefaultCatchBlock(){
        CodeBlock.Builder catchBuilder = CodeBlock.builder();
        addStatement(catchBuilder, makeInvokeCodeBlock(CodeBlock.of("$L", "e"), "printStackTrace", Optional.empty()));
        return catchBuilder.build();
    }

    private void addStatement(CodeBlock.Builder builder, CodeBlock codeBlock){
        builder.add("$[");
        builder.add(codeBlock);
        builder.add(";\n$]");
    }

    /**
     *make an assignBlock based on typeName, varName and assignBlock, the assignBlock may be a constant, new block or method invoke block
     */
    private CodeBlock makeAssignBlock(TypeName typeName, String varName, CodeBlock assignBlock){
        return CodeBlock.of("$1T $2L = $3L", typeName, CodeBlock.of(varName), assignBlock);
    }

    /**
     * make block like
     * new String(),
     */
    private CodeBlock makeNewBlock(CodeBlock typeName, Optional<CodeBlock> parameterBlock){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(parameterBlock.isPresent()){
            builder.add("new $1L($2L)", typeName, parameterBlock.get());
        }else{
            builder.add("new $1L()", typeName);
        }
        return builder.build();
    }

    /**
     * make block like
     * this.name = name, the assign block may be new block or method invoke block
     */
    private CodeBlock makeAssignThisBlock(String fieldName, CodeBlock assignBlock){
        return CodeBlock.of("this.$1N = $2L", fieldName, assignBlock);
    }

    //TODO verify the usage of static and nonstatic method call
//    private CodeBlock makeInvokeCodeBlock(String instance, String method, Optional<CodeBlock> parameterBlock){
//        CodeBlock.Builder builder = CodeBlock.builder();
//        if(parameterBlock.isPresent()){
//            builder.add("$1L.$2L($3L)", instance, method, parameterBlock.get());
//        }else{
//            builder.add("$1L.$2L()", instance, method);
//        }
//        return builder.build();
//    }


    /**
     * make invoke block like a.b(), notice that a may be an invoke block, instance or static type
     */
    //TODO notice that many method invocation use code block as their only parameter, we need a more convenient way to transfer single element to CodeBlock
    private CodeBlock makeInvokeCodeBlock(CodeBlock instance, String method, Optional<CodeBlock> parameterBlock){
        CodeBlock.Builder builder = CodeBlock.builder();
        if(parameterBlock.isPresent()){
            builder.add("$1L.$2L($3L)", instance, method, parameterBlock.get());
        }else{
            builder.add("$1L.$2L()", instance, method);
        }
        return builder.build();
    }

//    private CodeBlock makeInvokeCodeBlock(TypeName typeName, String method, Optional<CodeBlock> parameterBlock){
//        CodeBlock.Builder builder = CodeBlock.builder();
//        if(parameterBlock.isPresent()){
//            builder.add("$1T.$2L($3L)", typeName, method, parameterBlock.get());
//        }else{
//            builder.add("$1T.$2L()", typeName, method);
//        }
//        return builder.build();
//    }

    /**
     * connect the parameter of method invocation into a block, the parameters may not be string
     */

    //TODO change the parameters from String list to code block list, the parameter may also be an invoke block
    private CodeBlock makeParametersCodeBlock(LinkedList<String> parameters){
        return CodeBlocks.makeParametersCodeBlock(parameters.stream().map(para -> CodeBlock.of("$L", para)).collect(Collectors.toList()));
    }


    private CodeBlock makeTryCatchBlock(CodeBlock tryStatement, CodeBlock catchStatement, TypeName... exception){
       return CodeBlock.builder().beginControlFlow("try").add(tryStatement).nextControlFlow("catch($L $L)",
               formatCatchParaCodeBlock(exception), CodeBlock.of("e")).add(catchStatement).endControlFlow().build();
    }

    private CodeBlock formatCatchParaCodeBlock(TypeName... parameters){
        List<CodeBlock> list = stream(parameters).map(parameter -> CodeBlock.of("$T", parameter)).collect(Collectors.toList());
        return list.stream().collect(CodeBlocks.joiningCodeBlocks("|"));
    }

    private void writeMethods(TypeSpec.Builder builder, SymEncPara input){
        addEncMethod(builder, input);
        addDecMethod(builder,input);
        addGetMethods(builder, input);
    }


    private void addEncMethod(TypeSpec.Builder builder, SymEncPara input){
        MethodSpec.Builder encMethodBuilder = MethodSpec.methodBuilder("encrypt").addModifiers(Modifier.PUBLIC).returns(ClassName.get("java.lang","String"));
        encMethodBuilder.addParameter(ClassName.get("java.lang","String"), "input");
        CodeBlock.Builder tryBuilder = CodeBlock.builder();
        LinkedList<String> cipherInitPara = new LinkedList<>();
        cipherInitPara.add("Cipher.ENCRYPT_MODE");
        cipherInitPara.add("secretKey");
        cipherInitPara.add("ivParameterSpec");
        addStatement(tryBuilder, makeInvokeCodeBlock(CodeBlock.of("$L", "cipher"), "init", Optional.of(makeParametersCodeBlock(cipherInitPara))));
        tryBuilder.addStatement("return $L", makeInvokeCodeBlock(makeInvokeCodeBlock(CodeBlock.of("$T", ClassName.get("java.util", "Base64")), "getEncoder", Optional.empty()), "encodeToString",
                Optional.of(makeInvokeCodeBlock(CodeBlock.of("$L", "cipher"), "doFinal", Optional.of(makeInvokeCodeBlock(CodeBlock.of("$L", "input"), "getBytes", Optional.empty()))))));
        encMethodBuilder.addCode(makeTryCatchBlock(tryBuilder.build(), getDefaultCatchBlock(), ClassName.get("java.security", "InvalidAlgorithmParameterException"),
                ClassName.get("java.security", "InvalidKeyException"),ClassName.get("javax.crypto", "BadPaddingException"),
                ClassName.get("javax.crypto", "IllegalBlockSizeException")));
        encMethodBuilder.addCode("return null;");
        builder.addMethod(encMethodBuilder.build());
    }

    private void addDecMethod(TypeSpec.Builder builder, SymEncPara input){
        MethodSpec.Builder decMethodBuilder = MethodSpec.methodBuilder("decrypt").addModifiers(Modifier.PUBLIC).returns(ClassName.get("java.lang","String"));
        decMethodBuilder.addParameter(ClassName.get("java.lang", "String"), "input");
        CodeBlock.Builder tryBuilder = CodeBlock.builder();
        LinkedList<String> cipherInitPara = new LinkedList<>();
        cipherInitPara.add("Cipher.DECRYPT_MODE");
        cipherInitPara.add("secretKey");
        cipherInitPara.add("ivParameterSpec");
        addStatement(tryBuilder, makeInvokeCodeBlock(CodeBlock.of("cipher"), "init", Optional.of(makeParametersCodeBlock(cipherInitPara))));
        tryBuilder.addStatement("return $L", makeNewBlock(CodeBlock.of("$T", ClassName.get("java.lang","String")), Optional.of(makeInvokeCodeBlock(CodeBlock.of("$L", "cipher"), "doFinal",
                Optional.of(makeInvokeCodeBlock(makeInvokeCodeBlock(CodeBlock.of("$T", ClassName.get("java.util", "Base64")), "getDecoder", Optional.empty()), "decode",
                        Optional.of(makeInvokeCodeBlock(CodeBlock.of("$L", "input"),"getBytes", Optional.empty()))))))));
        decMethodBuilder.addCode(makeTryCatchBlock(tryBuilder.build(), getDefaultCatchBlock(), ClassName.get("java.security", "InvalidAlgorithmParameterException"),
                ClassName.get("java.security", "InvalidKeyException"),ClassName.get("javax.crypto", "BadPaddingException"),
                ClassName.get("javax.crypto", "IllegalBlockSizeException")));
        decMethodBuilder.addCode("return null;");
        builder.addMethod(decMethodBuilder.build());
    }

    private void addGetMethods(TypeSpec.Builder builder, SymEncPara input){
        MethodSpec.Builder getKey = MethodSpec.methodBuilder("getSecretKey").returns(ClassName.get("javax.crypto", "SecretKey")).addModifiers(Modifier.PUBLIC);
        getKey.addStatement("return $L", CodeBlock.of("secretKey"));
        builder.addMethod(getKey.build());

        MethodSpec.Builder getIvPara = MethodSpec.methodBuilder("getIvParameterSpec").returns(ClassName.get("javax.crypto.spec", "IvParameterSpec")).addModifiers(Modifier.PUBLIC);
        getIvPara.addStatement("return $L", CodeBlock.of("ivParameterSpec"));
        builder.addMethod(getIvPara.build());

        MethodSpec.Builder getAlgorithms = MethodSpec.methodBuilder("getAlgorithms").returns(ClassName.get("java.lang", "String")).addModifiers(Modifier.PUBLIC);
        getAlgorithms.addStatement("return $L", CodeBlock.of("algorithms"));
        builder.addMethod(getAlgorithms.build());


        addGetInstance(builder, input);
    }


    private void addGetInstance(TypeSpec.Builder builder, SymEncPara input){
        MethodSpec.Builder getInstance = MethodSpec.methodBuilder("getInstance").returns(nameGeneratedType(input)).addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        CodeBlock.Builder bodyBuilder = CodeBlock.builder();
        bodyBuilder.add(makeIfBlock(CodeBlock.of("instance == null"), makeSynchronizedBLock(CodeBlock.of("syn"), makeIfBlock(CodeBlock.of("instance == null"),
                CodeBlock.builder().add(CodeBlock.of("instance = $L;", makeNewBlock(CodeBlock.of("$L", nameGeneratedType(input)) , Optional.empty()))).build(), Optional.empty())), Optional.empty()));
        addStatement(bodyBuilder, CodeBlock.of("$L", "return instance"));
        getInstance.addCode(bodyBuilder.build());

        builder.addMethod(getInstance.build());
    }

    private CodeBlock makeIfBlock(CodeBlock condition,  CodeBlock ifBlock, Optional<CodeBlock> elseBlock){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("if($L)", condition);
        builder.add(ifBlock);
        if(elseBlock.isPresent()){
            builder.nextControlFlow("else");
            addStatement(builder, elseBlock.get());
            builder.endControlFlow();
        }else{
            builder.endControlFlow();
        }
        return builder.build();
    }

    private CodeBlock makeSynchronizedBLock(CodeBlock obj, CodeBlock body){
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("synchronized($L)", obj);
        builder.add(body);
        builder.endControlFlow();
        return builder.build();
    }
}
