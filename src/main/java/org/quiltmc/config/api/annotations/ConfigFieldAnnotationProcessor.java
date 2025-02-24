/*
 * Copyright 2022-2023 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.impl.ConfigFieldAnnotationProcessors;

import java.lang.annotation.Annotation;
import java.util.Collections;

/**
 * Converts data in an annotation on a field to metadata on a {@link TrackedValue}.
 *
 * <p>See {@link Comment}
 */
public interface ConfigFieldAnnotationProcessor<T extends Annotation> {
	void process(T annotation, MetadataContainerBuilder<?> builder);

	static <T extends Annotation> void register(Class<T> annotationClass, ConfigFieldAnnotationProcessor<T> processor) {
		ConfigFieldAnnotationProcessors.register(annotationClass, processor);

	}

	static void applyAnnotationProcessors(Annotation annotation, MetadataContainerBuilder<?> builder) {
		ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, builder);
	}
}
