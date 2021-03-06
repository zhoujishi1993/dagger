# Copyright (C) 2017 The Dagger Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Description:
#   A JSR-330 compliant dependency injection system for android and java

package(default_visibility = ["//:src"])

load("//:build_defs.bzl", "DOCLINT_HTML_AND_SYNTAX", "DOCLINT_REFERENCES")

EXPERIMENTAL_VISUALIZER_SRCS = ["BindingNetworkVisualizer.java"]

KYTHE_SRCS = [
    "KytheBindingGraphFactory.java",
    "DaggerKythePlugin.java",
    "KytheFormatting.java",
    "KeyVNameFactory.java",
]

CODEGEN_SRCS = glob(
    ["*.java"],
    exclude = EXPERIMENTAL_VISUALIZER_SRCS + KYTHE_SRCS,
)

CODEGEN_PLUGINS = [
    # TODO(cgruber): Enable Beta checker.
]

CODEGEN_SHARED_DEPS = [
    "//third_party:auto_service",
    "//third_party:auto_value",
    "//third_party:auto_common",
    "//third_party:error_prone_annotations",
    "//third_party:google_java_format",
    "//third_party:javapoet",
    "@local_jdk//:lib/tools.jar",
    "//third_party:jsr250_annotations",
    "//third_party:jsr330_inject",
    "//java/dagger:core",
    "//java/dagger/producers",
    "//java/dagger/model",
]

CODEGEN_DEPS = CODEGEN_SHARED_DEPS + [
    "//third_party:guava",
]

# Common types needed across all of the codegen package
java_library(
    name = "base",
    srcs = [
        "Accessibility.java",
        "AnnotationSpecs.java",
        "BindingKey.java",
        "CodeBlocks.java",
        "CompilerOptions.java",
        "ContributionType.java",
        "DaggerElements.java",
        "DaggerStreams.java",
        "DaggerTypes.java",
        "Expression.java",
        "FeatureStatus.java",
        "ForwardingNetwork.java",
        "FrameworkTypes.java",
        "InjectionAnnotations.java",
        "Keys.java",
        "MapKeyAccessibility.java",
        "MapType.java",
        "MethodSignature.java",
        "MoreAnnotationMirrors.java",
        "MoreAnnotationValues.java",
        "MultibindingAnnotations.java",
        "OptionalType.java",
        "Optionals.java",
        "SetType.java",
        "SimpleAnnotationMirror.java",
        "SimpleTypeAnnotationValue.java",
        "SourceFileGenerationException.java",  # Used in :writing and :processor
        "SourceFileGenerator.java",  # Needed by InjectBindingRegistry in :binding and also :writing
        "TypeNames.java",
        "TypeSpecs.java",
        "UniqueNameSet.java",
        "Util.java",
        "ValidationType.java",
        "package-info.java",
    ],
    plugins = CODEGEN_PLUGINS,
    tags = ["maven:merged"],
    deps = CODEGEN_DEPS,
)

# Classes that help to build a model of the binding graph
java_library(
    name = "binding",
    srcs = [
        "AnnotationExpression.java",
        "Binding.java",
        "BindingDeclaration.java",
        "BindingGraph.java",
        "BindingGraphPlugin.java",  # TODO(ronshapiro): this should move to it's own model target
        "BindingNetwork.java",
        "BindingType.java",
        "BindingTypeMapper.java",
        "BindingVariableNamer.java",  # needed by FrameworkField
        "BindsTypeChecker.java",
        "ComponentDescriptor.java",
        "ComponentRequirement.java",
        "ComponentTreeTraverser.java",
        "ConfigurationAnnotations.java",  # Uses ModuleDescriptors
        "ContributionBinding.java",
        "DelegateDeclaration.java",
        "DependencyRequest.java",
        "DependencyVariableNamer.java",  # Used by SourceFiles
        "ErrorMessages.java",  # Consider splitting this up as it pulls in too much
        "FrameworkDependency.java",
        "FrameworkField.java",  # Used by SourceFiles
        "FrameworkType.java",
        "HelloWorldPara.java",
        "InjectBindingRegistry.java",
        "KeyFactory.java",
        "MapKeys.java",
        "MembersInjectionBinding.java",
        "ModuleDescriptor.java",
        "MultibindingDeclaration.java",
        "OptionalBindingDeclaration.java",
        "ProductionBinding.java",
        "ProvisionBinding.java",
        "ResolvedBindings.java",
        "Scope.java",
        "SourceFiles.java",  # Consider splitting this up?
        "SubcomponentDeclaration.java",
        "SymEncPara.java",
    ],
    plugins = CODEGEN_PLUGINS,
    tags = ["maven:merged"],
    deps = CODEGEN_DEPS + [":base"],
)

# Code related to validating the user-written Dagger code
java_library(
    name = "validation",
    srcs = [
        "AnyBindingMethodValidator.java",
        "BindingDeclarationFormatter.java",
        "BindingGraphValidator.java",
        "BindingMethodProcessingStep.java",
        "BindingMethodValidator.java",
        "BindsInstanceProcessingStep.java",
        "BindsMethodValidator.java",
        "BindsOptionalOfMethodValidator.java",
        "BuilderValidator.java",
        "CanReleaseReferencesValidator.java",
        "ComponentHierarchyValidator.java",
        "ComponentValidator.java",
        "DependencyRequestFormatter.java",
        "ForReleasableReferencesValidator.java",
        "Formatter.java",
        "InjectValidator.java",
        "MapKeyValidator.java",
        "MethodSignatureFormatter.java",
        "MissingBindingSuggestions.java",
        "ModuleValidator.java",
        "MultibindingAnnotationsProcessingStep.java",
        "MultibindsMethodValidator.java",
        "ProducesMethodValidator.java",
        "ProvidesMethodValidator.java",
        "SymEncValidator.java",
        "ValidationReport.java",
    ],
    plugins = CODEGEN_PLUGINS,
    tags = ["maven:merged"],
    deps = CODEGEN_DEPS + [
        ":base",
        ":binding",
    ],
)

# Classes that assemble the model of the generated code and write to the Filer
java_library(
    name = "writing",
    srcs = [
        "AbstractComponentWriter.java",
        "AnnotationCreatorGenerator.java",
        "BindingExpression.java",
        "BoundInstanceBindingExpression.java",
        "ComponentBindingExpressions.java",
        "ComponentBuilder.java",
        "ComponentGenerator.java",
        "ComponentInstanceBindingExpression.java",
        "ComponentProvisionBindingExpression.java",
        "ComponentRequirementField.java",
        "ComponentRequirementFields.java",
        "ComponentWriter.java",
        "DelegateBindingExpression.java",
        "FactoryGenerator.java",
        "FrameworkFieldInitializer.java",
        "FrameworkInstanceBindingExpression.java",
        "GeneratedComponentModel.java",
        "GwtCompatibility.java",
        "HelloWorldGenerator.java",
        "InjectionMethods.java",
        "MapBindingExpression.java",
        "MemberSelect.java",
        "MembersInjectionBindingExpression.java",
        "MembersInjectionMethods.java",
        "MembersInjectorFieldInitializer.java",
        "MembersInjectorGenerator.java",
        "MonitoringModuleGenerator.java",
        "MonitoringModuleProcessingStep.java",
        "OptionalBindingExpression.java",
        "OptionalFactories.java",
        "PrivateMethodBindingExpression.java",
        "ProducerFactoryGenerator.java",
        "ProducerFromProviderFieldInitializer.java",
        "ProductionExecutorModuleGenerator.java",
        "ProviderOrProducerFieldInitializer.java",
        "ReferenceReleasingManagerFields.java",
        "SetBindingExpression.java",
        "SimpleInvocationBindingExpression.java",
        "SimpleMethodBindingExpression.java",
        "SubcomponentBuilderBindingExpression.java",
        "SubcomponentNames.java",
        "SubcomponentWriter.java",
        "SymEncGenerator.java",
        "UnwrappedMapKeyGenerator.java",
    ],
    plugins = CODEGEN_PLUGINS,
    tags = ["maven:merged"],
    deps = CODEGEN_DEPS + [
        ":base",
        ":binding",
    ],
)

# The processor's "main", if you will
java_library(
    name = "processor",
    srcs = [
        "CanReleaseReferencesProcessingStep.java",
        "ComponentHjarProcessingStep.java",
        "ComponentProcessingStep.java",
        "ComponentProcessor.java",
        "HelloWorldProcessingStep.java",
        "InjectBindingRegistryImpl.java",
        "InjectProcessingStep.java",
        "MapKeyProcessingStep.java",
        "ModuleProcessingStep.java",
        "ProductionExecutorModuleProcessingStep.java",
        "SymEncProssingStep.java",
    ],
    plugins = CODEGEN_PLUGINS,
    deps = CODEGEN_DEPS + [
        ":base",
        ":binding",
        ":writing",
        ":validation",
    ],
)

java_library(
    name = "kythe",
    srcs = KYTHE_SRCS,
    deps = [
        ":base",
        ":binding",
        ":processor",
        ":validation",
        "//java/dagger:core",
        "//java/dagger/model",
        "//java/dagger/producers",
        "//third_party:auto_common",
        "//third_party:auto_service",
        "//third_party:guava",
        "//third_party:javapoet",
        "//third_party:kythe_plugin",
        "@bazel_tools//third_party/java/jdk/langtools:javac",
    ],
)

load("//tools:javadoc.bzl", "javadoc_library")

javadoc_library(
    name = "codegen-javadoc",
    srcs = CODEGEN_SRCS,
    root_packages = ["dagger.internal.codegen"],
    deps = [":processor"],
)

java_library(
    name = "check-package-javadoc",
    testonly = 1,
    srcs = CODEGEN_SRCS,
    javacopts = DOCLINT_HTML_AND_SYNTAX + DOCLINT_REFERENCES,
    deps = CODEGEN_DEPS,
)

java_plugin(
    name = "dagger-experimental-visualizer",
    srcs = EXPERIMENTAL_VISUALIZER_SRCS,
    deps = [
        ":base",
        ":binding",
        "//third_party:auto_service",
        "//third_party:error_prone_annotations",
        "//third_party:guava",
        "//third_party:javapoet",
    ],
)

java_plugin(
    name = "component-codegen",
    generates_api = 1,
    processor_class = "dagger.internal.codegen.ComponentProcessor",
    deps = [":processor"],
)
