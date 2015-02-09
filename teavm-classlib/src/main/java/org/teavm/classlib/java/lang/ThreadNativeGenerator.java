/*
 *  Copyright 2015 Alexey Andreev.
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
package org.teavm.classlib.java.lang;

import java.io.IOException;
import org.teavm.codegen.SourceWriter;
import org.teavm.dependency.DependencyAgent;
import org.teavm.dependency.DependencyPlugin;
import org.teavm.dependency.MethodDependency;
import org.teavm.javascript.spi.Generator;
import org.teavm.javascript.spi.GeneratorContext;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class ThreadNativeGenerator  implements Generator, DependencyPlugin {

    private static final MethodReference launchRef = new MethodReference(Thread.class,
            "launch", Thread.class, void.class);

    @Override
    public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef) throws IOException {
        if ( methodRef.getName().equals("start")){
            generateStart(context, writer);
        }
    }

    @Override
    public void methodAchieved(DependencyAgent agent, MethodDependency method, CallLocation location) {
        switch (method.getReference().getName()) {
            case "start": {
                MethodDependency performMethod = agent.linkMethod(launchRef, null);
                method.getVariable(0).connect(performMethod.getVariable(1));
                performMethod.use();
                break;
            }
        }
    }

    

    private void generateStart(GeneratorContext context, SourceWriter writer) throws IOException {
        String obj = context.getParameterName(0);
        writer.append("$rt_setTimeout(function() { $rt_rootInvocationAdapter(").appendMethodBody(launchRef).append(")(")
                .append(obj).append(");},0);").softNewLine();
    }

    
}
