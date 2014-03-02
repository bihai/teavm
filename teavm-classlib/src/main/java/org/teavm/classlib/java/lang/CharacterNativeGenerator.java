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
package org.teavm.classlib.java.lang;

import java.io.IOException;
import org.teavm.classlib.impl.unicode.UnicodeHelper;
import org.teavm.classlib.impl.unicode.UnicodeSupport;
import org.teavm.codegen.SourceWriter;
import org.teavm.dependency.DependencyChecker;
import org.teavm.dependency.DependencyPlugin;
import org.teavm.dependency.MethodDependency;
import org.teavm.javascript.ni.Generator;
import org.teavm.javascript.ni.GeneratorContext;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;

/**
 *
 * @author Alexey Andreev
 */
public class CharacterNativeGenerator implements Generator, DependencyPlugin {
    @Override
    public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef) throws IOException {
        switch (methodRef.getName()) {
            case "toLowerCase":
                if (methodRef.getDescriptor().parameterType(0) == ValueType.CHARACTER) {
                    generateToLowerCase(context, writer);
                } else {
                    generateToLowerCaseInt(context, writer);
                }
                break;
            case "obtainDigitMapping":
                generateObtainDigitMapping(writer);
                break;
            case "obtainClasses":
                generateObtainClasses(writer);
                break;
        }
    }

    @Override
    public void methodAchieved(DependencyChecker checker, MethodDependency method) {
        switch (method.getReference().getName()) {
            case "obtainDigitMapping":
            case "obtainClasses":
                method.getResult().propagate("java.lang.String");
                break;
        }
    }

    private void generateToLowerCase(GeneratorContext context, SourceWriter writer) throws IOException{
        writer.append("return String.fromCharCode(").append(context.getParameterName(1))
                .append(").toLowerCase().charCodeAt(0)|0;").softNewLine();
    }

    private void generateToLowerCaseInt(GeneratorContext context, SourceWriter writer) throws IOException{
        writer.append("return String.fromCharCode(").append(context.getParameterName(1))
                .append(").toLowerCase().charCodeAt(0);").softNewLine();
    }

    private void generateObtainDigitMapping(SourceWriter writer) throws IOException {
        String str = UnicodeHelper.encodeIntByte(UnicodeSupport.getDigitValues());
        writer.append("return $rt_str(");
        splitString(writer, str);
        writer.append(");").softNewLine();
    }

    private void generateObtainClasses(SourceWriter writer) throws IOException {
        String str = UnicodeHelper.compressRle(UnicodeSupport.getClasses());
        writer.append("return $rt_str(");
        splitString(writer, str);
        writer.append(");").softNewLine();
    }

    private void splitString(SourceWriter writer, String str) throws IOException {
        for (int i = 0; i < str.length(); i += 512) {
            if (i > 0) {
                writer.ws().append("+").newLine();
            }
            int j = Math.min(i + 512, str.length());
            writer.append("\"").append(str.substring(i, j)).append("\"");
        }
    }
}
