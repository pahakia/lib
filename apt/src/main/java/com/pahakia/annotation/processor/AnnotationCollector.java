package com.pahakia.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Collect all classes with annotations in the project and write them in the file META-INF/annotated-classes.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationCollector extends AbstractProcessor {
    private static final Logger logger = Logger.getLogger(AnnotationCollector.class.getName());

    private static final String DEST_FILE_NAME = "META-INF/annotated-classes";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger.fine("entering process");
        if (!roundEnv.processingOver()) {
            logger.fine("processing is not over");
            String oldContent = "";
            FileObject o = null;
            try {
                logger.fine("getting resource");
                // this is for IDE incremental build
                o = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", DEST_FILE_NAME);
                logger.fine("getting resource OK");
                CharSequence cs = o.getCharContent(true);
                oldContent = cs.toString();
            } catch (IOException ex) {
                logger.warning("getting resource exception: " + ex);
                logger.log(Level.FINE, "getting resource exception", ex);
                o = null;
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, ex.getMessage());
            }
            StringBuilder buf = new StringBuilder();
            for (Element elem : roundEnv.getRootElements()) {
                if (elem instanceof TypeElement) {
                    TypeElement te = (TypeElement) elem;
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, elem.getSimpleName());

                    List<? extends AnnotationMirror> ams = te.getAnnotationMirrors();
                    if (ams != null && !ams.isEmpty()) {
                        buf.append(te.getQualifiedName());
                        buf.append(":");
                        for (TypeElement ann : annotations) {
                            logger.fine("annotation: " + ann);
                            for (AnnotationMirror am : ams) {
                                logger.fine("    annotation mirror: " + am);
                                if (am.getAnnotationType().asElement().equals(ann)) {
                                    logger.fine("   **** matching annotation: " + ann + ", " + am);
                                    buf.append(" ");
                                    buf.append(ann.getQualifiedName());
                                }
                            }
                        }
                        buf.append("\n");
                    }
                    // remove old entry if exists
                    // this is for IDE incremental build
                    oldContent = oldContent.replaceAll(te.getQualifiedName() + ":[^\n]+\n", "");
                }
            }

            try {
                logger.fine("creating resource");
                o = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", DEST_FILE_NAME);
                logger.fine("creating resource: OK");
                logger.fine("writing resource");

                Writer w = o.openWriter();
                w.write(oldContent);
                w.append(buf);
                w.flush();
                w.close();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "File finished");
            } catch (IOException ex) {
                logger.warning("writing or creating resource exception: " + ex);
                logger.log(Level.FINE, "writing or creating resource exception", ex);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
            }
        }
        return true;
    }
}
