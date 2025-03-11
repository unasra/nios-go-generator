package com.infoblox.codegen;

import org.junit.Test;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

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
public class BloxoneTerraformCodegenTest {
    @Test
    public void launchCodeGenerator() {

        final CodegenConfigurator configurator = CodegenConfigurator.fromFile("openapi-generator-config/terraform/config.yaml")
                .setGeneratorName("terraform-provider-bloxone")
                .setOutputDir("generator/out/terraform-provider-bloxone")
                .setPackageName("ipamfederation")
                .setInputSpec(".final-schemas/ipam-federation.json")
                .addAdditionalProperty("modelPrefixToRemove", "federation");

        final ClientOptInput clientOptInput = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        generator.opts(clientOptInput).generate();
    }
}
