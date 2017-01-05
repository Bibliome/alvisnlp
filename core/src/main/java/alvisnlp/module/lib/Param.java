/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



package alvisnlp.module.lib;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for getters bound to a module parameter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface Param {

    /**
     * Name of the parameter to which the annotated field is bound.
     * If the value is empty, the the parameter has the same name as the getter (without "get", first character uncapitalized).
     */
    String publicName() default "";

    /**
     * Either if the parameter is mandatory.
     * @return true, if mandatory
     */
    boolean mandatory() default true;

    String nameType() default "";
    
    /**
     * Default documentation.
     */
    String defaultDoc() default "";
    
    String defaultValue() default "null";
}
