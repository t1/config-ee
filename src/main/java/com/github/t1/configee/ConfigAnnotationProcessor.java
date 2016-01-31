package com.github.t1.configee;

import static javax.lang.model.SourceVersion.*;

import java.util.List;

import javax.annotation.processing.SupportedSourceVersion;

import com.github.t1.exap.*;
import com.github.t1.exap.generator.*;
import com.github.t1.exap.reflection.*;
import com.github.t1.exap.reflection.Package;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationClasses({ Config.class })
public class ConfigAnnotationProcessor extends ExtendedAbstractProcessor {
    static Type type(Class<?> type) {
        return ReflectionProcessingEnvironment.ENV.type(type);
    }

    private Round round;

    @Override
    public boolean process(Round round) {
        log.info("process configurations - round {}", round.number());
        this.round = round;
        List<Field> configFields = round.fieldsAnnotatedWith(Config.class);
        Package commonSuperPackage = commonSuperPackage(configFields);
        if (commonSuperPackage != null)
            try (TypeGenerator generator = commonSuperPackage.openTypeGenerator("Configuration")) {
                generator.annotation(type(lombok.Data.class));
                for (Field field : configFields) {
                    log.debug("found field {}", field.getName());
                    FieldGenerator fieldGenerator = generator.addField(field.getName());
                    fieldGenerator.type(field.getType());
                }
            }
        return false;
    }

    private Package commonSuperPackage(List<Field> fields) {
        return fields.stream() //
                .map(field -> field.getDeclaringType().getPackage()) //
                .reduce(null, this::commonSuperPackage);
    }

    private Package commonSuperPackage(Package left, Package right) {
        if (left == null)
            return right;
        if (left.equals(right) || left.isSuperPackageOf(right))
            return left;
        if (right.isSuperPackageOf(left))
            return right;
        return round.getRootPackage(); // FIXME must implement this properly!
    }

}
