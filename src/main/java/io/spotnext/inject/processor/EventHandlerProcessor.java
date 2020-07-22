package io.spotnext.inject.processor;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.spotnext.inject.AbstractEventMethodHandler;
import io.spotnext.inject.annotations.EventHandler;
import io.spotnext.inject.annotations.Inject;
import io.spotnext.inject.annotations.Service;
import io.spotnext.support.util.Loggable;

@Service
@SupportedOptions({ "debug", "verify" })
@SupportedAnnotationTypes("io.spotnext.inject.annotations.EventHandler")
public class EventHandlerProcessor extends AbstractProcessor implements Loggable {

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(EventHandler.class)) {

			if (element.getKind() == ElementKind.METHOD) {
				final var classElement = (TypeElement) element.getEnclosingElement();
				final var methodElement = (ExecutableElement) element;
				final var className = classElement.getSimpleName();
				final var packageName = classElement.getQualifiedName().toString();
				final var methodName = methodElement.getSimpleName();
				final var params = methodElement.getParameters();

				final var paramTypes = new HashMap<String, TypeMirror>();

				for (var p : params) {
					paramTypes.put(p.getSimpleName().toString(), p.asType());
				}

				final var firstParam = paramTypes.entrySet().iterator().next();

				try {
					final var handlerClassName = className + "_" + methodName;
					final var handlerPackageName = packageName.substring(0, packageName.length() - className.length() - 1);

					MethodSpec constructor = MethodSpec.constructorBuilder()
							.addModifiers(Modifier.PUBLIC)
							.addStatement("super($T.class)", TypeName.get(firstParam.getValue()))
							.build();

					MethodSpec handleEvent = MethodSpec.methodBuilder("handleEvent")
							.addModifiers(Modifier.PUBLIC)
							.addAnnotation(Override.class)
							.addParameter(TypeName.get(firstParam.getValue()), firstParam.getKey())
							.returns(void.class)
							.addException(RuntimeException.class)
							.addStatement("$L.$L(event)", "handler", methodName)
							.build();

					FieldSpec handlerField = FieldSpec.builder(TypeName.get(classElement.asType()), "handler")
							.addModifiers(Modifier.PRIVATE)
							.addAnnotation(Inject.class)
							.build();

//					TypeVariableName typeVariableName = TypeVariableName.get("T", TypeName.get(firstParam.getValue()));

					TypeSpec eventHandlerClass = TypeSpec.classBuilder(handlerClassName)
							.superclass(ParameterizedTypeName.get(ClassName.get(AbstractEventMethodHandler.class), TypeName.get(firstParam.getValue())))
							.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
							.addAnnotation(Service.class)
							.addField(handlerField)
							.addMethod(constructor)
							.addMethod(handleEvent)
							.build();

					JavaFile javaFile = JavaFile
							.builder(handlerPackageName, eventHandlerClass)
							.build();

					javaFile.writeTo(System.out);
					javaFile.writeTo(processingEnv.getFiler());
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}

				return true;
			}
		}

		return false;
	}

}
