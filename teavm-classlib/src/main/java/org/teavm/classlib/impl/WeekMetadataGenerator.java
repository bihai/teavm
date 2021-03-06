/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.impl;

import java.util.Map;
import org.teavm.classlib.impl.unicode.CLDRReader;
import org.teavm.model.MethodReference;
import org.teavm.platform.metadata.*;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public abstract class WeekMetadataGenerator implements MetadataGenerator {
    @Override
    public Resource generateMetadata(MetadataGeneratorContext context, MethodReference method) {
        ResourceMap<IntResource> map = context.createResourceMap();
        for (Map.Entry<String, Integer> entry : getWeekData(context.getService(CLDRReader.class)).entrySet()) {
            IntResource valueRes = context.createResource(IntResource.class);
            valueRes.setValue(entry.getValue());
            map.put(entry.getKey(), valueRes);
        }
        return map;
    }

    protected abstract Map<String, Integer> getWeekData(CLDRReader reader);
}
