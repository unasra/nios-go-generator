package com.infoblox.codegen.templating;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.api.TemplatingExecutor;
import org.openapitools.codegen.templating.TemplateNotFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class CustomHandlebarsEngineAdapter extends org.openapitools.codegen.templating.HandlebarsEngineAdapter {
    /**
     * Provides an identifier used to load the adapter. This could be a name, uuid, or any other string.
     *
     * @return A string identifier.
     */
    @Override
    public String getIdentifier() {
        return "handlebars-custom";
    }

    public String compileTemplate(TemplatingExecutor executor,
                                  Map<String, Object> bundle, String templateFile) throws IOException {
        TemplateLoader loader = new AbstractTemplateLoader() {
            @Override
            public TemplateSource sourceAt(String location) {
                return findTemplate(executor, location);
            }
        };

        Context context = Context
                .newBuilder(bundle)
                .resolver(
                        MapValueResolver.INSTANCE,
                        JavaBeanValueResolver.INSTANCE,
                        MethodValueResolver.INSTANCE)
                .build();

        Handlebars handlebars = new Handlebars(loader);
        handlebars.registerHelperMissing((obj, options) -> {
            log.warn(String.format(Locale.ROOT, "Unregistered helper name '%s', processing template:%n%s", options.helperName, options.fn.text()));
            return "";
        });
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        StringHelpers.register(handlebars);
        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelpers(org.openapitools.codegen.templating.handlebars.StringHelpers.class);
        handlebars.setInfiniteLoops(true);
        handlebars.setPrettyPrint(true);
        Template tmpl = handlebars.compile(templateFile);
        return tmpl.apply(context);
    }
}

