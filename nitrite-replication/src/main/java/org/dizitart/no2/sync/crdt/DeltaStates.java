/*
 * Copyright (c) 2017-2020. Nitrite author or authors.
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

package org.dizitart.no2.sync.crdt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.sync.module.DocumentDeserializer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Anindya Chatterjee
 */
@Data
public class DeltaStates {
    @JsonDeserialize(contentUsing = DocumentDeserializer.class)
    private Set<Document> changeSet;
    private Map<String, Long> tombstoneMap;

    public DeltaStates() {
        changeSet = new LinkedHashSet<>();
        tombstoneMap = new LinkedHashMap<>();
    }
}
