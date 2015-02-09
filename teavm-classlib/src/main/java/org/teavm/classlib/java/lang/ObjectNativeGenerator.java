/*
 *  Copyright 2013 Alexey Andreev.
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
import org.teavm.javascript.spi.Generator;
import org.teavm.javascript.spi.GeneratorContext;
import org.teavm.model.MethodReference;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class ObjectNativeGenerator implements Generator {
    @Override
    public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef) throws IOException {
        switch (methodRef.getDescriptor().getName()) {
            case "wait":
                generateWait(context, writer);
                break;
            case "notify":
                generateNotify(context, writer);
                break;
            case "notifyAll":
                generateNotifyAll(context, writer);
                break;
        }
    }


    

    
    
    
    private void generateWait(GeneratorContext context, SourceWriter writer) throws IOException {
        String pname = context.getParameterName(1);
        String obj = context.getParameterName(0);
        writer.append("(function(){").indent().softNewLine();
        writer.append("var completed = false;").softNewLine();
        writer.append("var retCallback = ").append(context.getCompleteContinuation()).append(";").softNewLine();
        writer.append("var callback = function(){").indent().softNewLine();
        writer.append("if (completed){return;} completed=true;").softNewLine();
        writer.append("retCallback($rt_asyncResult(null));").softNewLine();
        writer.outdent().append("};").softNewLine();
        writer.append("if (").append(pname).append(">0){").indent().softNewLine();
        writer.append("$rt_setTimeout(callback, ").append(pname).append(");").softNewLine();
        writer.outdent().append("}").softNewLine();
        addNotifyListener(context, writer, "callback");
        writer.outdent().append("})();").softNewLine();
        
        
        
    }
    
    private void generateNotify(GeneratorContext context, SourceWriter writer) throws IOException {
        sendNotify(context, writer);
    }
    
    private void generateNotifyAll(GeneratorContext context, SourceWriter writer) throws IOException {
        sendNotifyAll(context, writer);
    }
    
    private String getNotifyListeners(GeneratorContext context){
        return context.getParameterName(0)+".__notifyListeners";
    }
    
    private void addNotifyListener(GeneratorContext context, SourceWriter writer, String callback) throws IOException {
        String lArr = getNotifyListeners(context);
        writer.append(lArr).append("=").append(lArr).append("||[];").softNewLine();
        writer.append(lArr).append(".push(").append(callback).append(");").softNewLine();
    }
    
    private void sendNotify(GeneratorContext context, SourceWriter writer) throws IOException {
        String lArr = getNotifyListeners(context);
        writer.append("$rt_setTimeout(function(){").indent().softNewLine();
        writer.append("if (!").append(lArr).append(" || ").append(lArr).append(".length===0){return;}").softNewLine();
        writer.append(lArr).append(".shift().apply(null);").softNewLine();
        writer.outdent().append("}, 0);").softNewLine();
    }
    
    private void sendNotifyAll(GeneratorContext context, SourceWriter writer) throws IOException {
        String obj = context.getParameterName(0);
        String lArr = getNotifyListeners(context);
        writer.append("$rt_setTimeout(function(){").indent().softNewLine();
        writer.append("if (!").append(lArr).append("){return;}").softNewLine();
        writer.append("while (").append(lArr).append(".length>0){").indent().softNewLine();
        writer.append(lArr).append(".shift().call(null);").softNewLine();
        writer.outdent().append("}");
        writer.outdent().append("}, 0);").softNewLine();
        
    }

    
}
