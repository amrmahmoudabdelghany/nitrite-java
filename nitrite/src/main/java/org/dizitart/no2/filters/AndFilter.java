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

package org.dizitart.no2.filters;

import lombok.Getter;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.common.tuples.Pair;
import org.dizitart.no2.exceptions.FilterException;

/**
 * Represents an And filter.
 *
 * @author Anindya Chatterjee
 * @since 1.0
 */
@Getter
public class AndFilter extends LogicalFilter {

    /**
     * Instantiates a new And filter.
     *
     * @param filters the filters
     */
    AndFilter(Filter... filters) {
        super(filters);

        for (int i = 1; i < filters.length; i++) {
            if (filters[i] instanceof TextFilter) {
                throw new FilterException("Text filter must be the first filter in AND operation");
            }
        }
    }

    @Override
    public boolean apply(Pair<NitriteId, Document> element) {
        for (Filter filter : getFilters()) {
            if (!filter.apply(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < getFilters().size(); i++) {
            Filter filter = getFilters().get(i);
            if (i > 0) {
                stringBuilder.append(" && ");
            }
            stringBuilder.append(filter.toString());
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
