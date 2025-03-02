package com.infoblox.codegen;

import org.junit.Test;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

import java.util.HashMap;
import java.util.Map;

/***
 * This test allows you to easily launch your code generation software under a debugger.
 * Then run this test under debug mode.  You will be able to step through your java code
 * and then see the results in the out directory.
 *
 * To experiment with debugging your code generator:
 * 1) Set a break point in BloxoneGoClientGenerator.java in the postProcessOperationsWithModels() method.
 * 2) To launch this test in Eclipse: right-click | Debug As | JUnit Test
 *
 */
public class BloxoneGoClientCodegenTest {

    // use this test to launch you code generator in the debugger.
    // this allows you to easily set break points in MyclientcodegenGenerator.
    @Test
    public void launchCodeGenerator() {

        Map<String, Object> additionalProps = new HashMap<>();
        additionalProps.put("packageName", "ipam");
        additionalProps.put("goImportAlias", "ipam");
        additionalProps.put("packageVersion", "0.1.0");
        additionalProps.put("enumClassPrefix", true);
        additionalProps.put("generateInterfaces", true);
        additionalProps.put("dissalowAdditionalPropertiesIfNotPresent", false);
        additionalProps.put("modelPrefixToRemove", "ipamsvc");

        // to understand how the 'openapi-generator-cli' module is using 'CodegenConfigurator', have a look at the 'Generate' class:
        // https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-cli/src/main/java/org/openapitools/codegen/cmd/Generate.java
        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setEnablePostProcessFile(true)
                .setGeneratorName("bloxone-go-client")
                .setInputSpec("../.final-schemas/ipam.json")
                .setTemplateDir("../templates/openapi/go-client")
                .setAdditionalProperties(additionalProps)
                .setOutputDir("out/bloxone-go-client");

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(clientOptInput).generate();
    }
}
