package ru.optimax.testautomation;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class TemplateProcessor {
    private static final Configuration configuration = new Configuration();

    public static String processTemplate(String templateFile, Object data) throws IOException, TemplateException {
        try(Writer output = new StringWriter()){
            Template template = configuration.getTemplate(templateFile);
            template.process(data, output);
            return output.toString();
        }
    }
}
