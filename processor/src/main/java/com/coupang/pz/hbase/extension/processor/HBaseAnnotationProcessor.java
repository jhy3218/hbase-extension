package com.coupang.pz.hbase.extension.processor;

import com.coupang.pz.hbase.extension.annotation.HColumn;
import com.coupang.pz.hbase.extension.annotation.HRowKey;
import com.coupang.pz.hbase.extension.annotation.HRowKeyConversion;
import com.coupang.pz.hbase.extension.annotation.HTableRow;
import com.coupang.pz.hbase.extension.processor.context.ColumnContext;
import com.coupang.pz.hbase.extension.processor.context.RowKeyContext;
import com.coupang.pz.hbase.extension.processor.context.TemplateContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Annotation processor that handle HBase annotations
 */
@SupportedAnnotationTypes("com.coupang.pz.hbase.extension.annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class HBaseAnnotationProcessor extends AbstractProcessor{
    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;

    /**
     * Initializes the processor with the processing environment by
     * setting the {@code processingEnv} field to the value of the
     * {@code processingEnv} argument.  An {@code
     * IllegalStateException} will be thrown if this method is called
     * more than once on the same object.
     *
     * @param processingEnv environment to access facilities the tool framework
     *                      provides to the processor
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(HTableRow.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                error(annotatedElement, "Only classes can be annotated with @%s", HTableRow.class.getSimpleName());
                return true;
            }

            TypeElement classElement = (TypeElement)annotatedElement;
            TemplateContext templateContext = createTemplateContext(classElement);

            VariableElement rowKeyElement = getRowKeyElement(classElement);
            if (rowKeyElement == null) {
                error(classElement, "%s should have a field that annotated with @%s",templateContext.getRowName(), HRowKey.class.getSimpleName());
                return true;
            }

            RowKeyContext rowKeyContext = createRowKeyContext(rowKeyElement);
            if (rowKeyContext == null) {
                return true;
            }

            List<VariableElement> columnElements = getColumnElements(classElement);
            List<ColumnContext> columnContexts = createColumnContexts(columnElements);
            if (columnContexts.isEmpty()) {
                warn(classElement, "%s doesn't have any field that annotated with @%s", templateContext.getRowName(), HColumn.class.getSimpleName());
            }

            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("tc", templateContext);
            velocityContext.put("rc", rowKeyContext);
            velocityContext.put("ccs", columnContexts);

            return !generateTemplate(velocityContext);
        }

        return false;
    }

    private boolean generateTemplate(VelocityContext velocityContext) {
        Properties props = new Properties();
        URL velocityPropertyUrl = this.getClass().getClassLoader().getResource("velocity.properties");
        try {
            props.load(velocityPropertyUrl.openStream());
        } catch (IOException e) {
            error(null, e.getMessage());
            return false;
        }

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        Template vt = ve.getTemplate("HBaseTemplateImplentation.vm");
        JavaFileObject javaFileObject;
        try {
            TemplateContext tc = (TemplateContext)velocityContext.get("tc");
            javaFileObject = filer.createSourceFile(tc.getPackageName() + "." + tc.getRowName() + "Template");
            note(null, "creating source file: %s", javaFileObject.toUri());
            Writer writer = javaFileObject.openWriter();
            vt.merge(velocityContext, writer);
            writer.close();
        } catch (IOException e) {
            error(null, e.getMessage());
            return false;
        }

        return true;
    }

    private List<ColumnContext> createColumnContexts(List<VariableElement> columnElements) {
        List<ColumnContext> columnContexts = Lists.newArrayList();

        for(VariableElement columnElement : columnElements) {
            ExecutableElement getter = lookupPublicGetter(columnElement.getEnclosingElement(), columnElement.getSimpleName().toString());
            ExecutableElement setter = lookupPublicSetter(columnElement.getEnclosingElement(), columnElement.getSimpleName().toString());
            HColumn column = columnElement.getAnnotation(HColumn.class);
            String family = column.cf();
            String qualifier = column.col();
            String getterName = getter.getSimpleName().toString();
            String setterName = setter.getSimpleName().toString();
            String typeName = columnElement.asType().toString();
            String declaredTypeName = ((TypeElement)typeUtils.asElement(columnElement.asType())).getQualifiedName().toString();

            List<String> typeArguments = Lists.newArrayList();
            DeclaredType type = (DeclaredType)columnElement.asType();
            for (TypeMirror tm : type.getTypeArguments()) {
                typeArguments.add(tm.toString());
            }

            columnContexts.add(new ColumnContext(family, qualifier, declaredTypeName, typeArguments, typeName, getterName, setterName));
        }

        return columnContexts;
    }

    private RowKeyContext createRowKeyContext(VariableElement rowKeyElement) {
        HRowKey rowKey = rowKeyElement.getAnnotation(HRowKey.class);
        HRowKeyConversion rowKeyConversion = rowKey.conversion();

        if(rowKeyConversion == HRowKeyConversion.REVERSED_LONG) {
            if (rowKeyElement.asType().getKind() != TypeKind.LONG) {
                error(rowKeyElement, "Only %s can be annotated with @%s", TypeKind.LONG.name(), HRowKeyConversion.REVERSED_LONG);
                return null;
            }
        }

        String rowKeyName = rowKeyElement.getSimpleName().toString();
        String rowKeyType = rowKeyElement.asType().toString();

        ExecutableElement rowKeyGetter = lookupPublicGetter(rowKeyElement.getEnclosingElement(), rowKeyName);
        if (rowKeyGetter == null) {
            error(rowKeyElement, "%s should have public getter named %s.", rowKeyName, nameGetter(rowKeyName));
            return null;
        }

        ExecutableElement rowKeySetter = lookupPublicSetter(rowKeyElement.getEnclosingElement(), rowKeyName);
        if (rowKeySetter == null) {
            error(rowKeyElement, "%s should have public setter named %s.", rowKeyName, nameSetter(rowKeyName));
            return null;
        }

        return new RowKeyContext(rowKeyConversion, rowKeyType, rowKeyGetter.getSimpleName().toString(), rowKeySetter.getSimpleName().toString());
    }

    private TemplateContext createTemplateContext(TypeElement classElement) {
        String tableName = classElement.getAnnotation(HTableRow.class).of();
        String packageName = ((PackageElement)classElement.getEnclosingElement()).getQualifiedName().toString();
        String rowName = classElement.getSimpleName().toString();
        return new TemplateContext(packageName, rowName, tableName);
    }

    private ExecutableElement lookupPublicSetter(Element clasElement, String rowKeyName) {
        return lookupPublicMethod(clasElement, nameSetter(rowKeyName));
    }

    private String nameSetter(String rowKeyName) {
        return "set" + WordUtils.capitalize(rowKeyName);
    }

    private String nameGetter(String rowKeyName) {
        return "get" + WordUtils.capitalize(rowKeyName);
    }

    private ExecutableElement lookupPublicGetter(Element classElement, String rowKeyName) {
        return lookupPublicMethod(classElement, nameGetter(rowKeyName));
    }

    private ExecutableElement lookupPublicMethod(Element classElement, String mothodName) {
        for (Element element : classElement.getEnclosedElements()){
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }

            if(element.getSimpleName().toString().equals(mothodName) && element.getModifiers().contains(Modifier.PUBLIC)) {
                return (ExecutableElement) element;
            }
        }

        return null;
    }


    private VariableElement getRowKeyElement(TypeElement classElement) {
        for (Element element : classElement.getEnclosedElements()) {
            Annotation annotation = element.getAnnotation(HRowKey.class);
            if (annotation == null) {
                continue;
            }

            if (element.getKind() != ElementKind.FIELD) {
                error(element, "%s is not a field. Only fields can be annotated with @%s. Skipping processing.", element.getSimpleName(), HRowKey.class.getSimpleName());
                break;
            }

            return (VariableElement)element;
        }

        return null;
    }

    private List<VariableElement> getColumnElements(TypeElement classElement) {
        List<VariableElement> columnElements = Lists.newArrayList();
        for (Element element : classElement.getEnclosedElements()) {
            Annotation annotation = element.getAnnotation(HColumn.class);
            if (annotation == null) {
                continue;
            }

            if(element.getKind() != ElementKind.FIELD) {
                warn(element, "%s is not a field. Only fields can be annotated with @%s. Skipping processing.", element.getSimpleName(), HColumn.class.getSimpleName());
                continue;
            }

            columnElements.add((VariableElement)element);
        }

        return columnElements;
    }
    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void warn(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                String.format(msg, args),
                e);
    }

    private void note(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args),
                e);
    }
}
