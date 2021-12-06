package com.tersesystems.echopraxia;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes(" com.tersesystems.echopraxia.Argument")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ArgumentProcessor extends AbstractProcessor{
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) { // <4>
        annotations.forEach(annotation -> {
            Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
            elements.stream()
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .map(TypeElement::getQualifiedName)
                .map(name -> "Class " + name + " is annotated with " + annotation.getQualifiedName())
                .forEach(System.out::println);
        });
        return true;
    }
}
