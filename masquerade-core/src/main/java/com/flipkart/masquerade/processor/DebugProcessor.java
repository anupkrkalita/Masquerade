/*
 * Copyright 2017 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.masquerade.processor;

import com.flipkart.masquerade.Configuration;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.flipkart.masquerade.util.Strings.DEBUG_LIST;
import static com.flipkart.masquerade.util.Strings.OBJECT_PARAMETER;

/**
 * Created by shrey.garg on 15/07/17.
 */
public class DebugProcessor {
    private final Configuration configuration;
    private final TypeSpec.Builder cloakBuilder;

    /**
     * @param configuration Configuration for the current processing cycle
     * @param cloakBuilder Entry class under construction for the cycle
     */
    public DebugProcessor(Configuration configuration, TypeSpec.Builder cloakBuilder) {
        this.configuration = configuration;
        this.cloakBuilder = cloakBuilder;
    }

    public void addGetter() {
        if (!configuration.isDebugMode()) {
            return;
        }

        cloakBuilder.addField(FieldSpec
                .builder(ParameterizedTypeName.get(Set.class, String.class), DEBUG_LIST, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$T.newKeySet()", ConcurrentHashMap.class).build());
        cloakBuilder.addMethod(MethodSpec.methodBuilder("getMissingClasses")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(Set.class, String.class))
                .addStatement("return $L", DEBUG_LIST).build());
    }

    public void addDebugCollector(MethodSpec.Builder objectMaskBuilder) {
        if (!configuration.isDebugMode() || configuration.fallback() != null) {
            return;
        }

        objectMaskBuilder.nextControlFlow("else");
        objectMaskBuilder.addStatement("$L.add($L.getClass().getName())", DEBUG_LIST, OBJECT_PARAMETER);
    }
}
