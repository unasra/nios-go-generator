package com.infoblox.codegen;

import io.swagger.v3.oas.models.Operation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.languages.GoClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationMap;
import org.openapitools.codegen.model.OperationsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.openapitools.codegen.utils.StringUtils.camelize;
import static org.openapitools.codegen.utils.StringUtils.underscore;

public class NiosGoClientCodegen extends GoClientCodegen {
    private final Logger LOGGER = LoggerFactory.getLogger(NiosGoClientCodegen.class);

    /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -g flag.
     *
     * @return the friendly name for the generator
     */
    public String getName() {
        return "nios-go-client";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    public String getHelp() {
        return "Generates a nios-go-client library.";
    }

    public NiosGoClientCodegen() {
        super();
    }

    @Override
    public OperationsMap postProcessOperationsWithModels(OperationsMap objs, List<ModelMap> allModels) {
        objs = super.postProcessOperationsWithModels(objs, allModels);
        OperationMap operations = objs.getOperations();

        List<CodegenOperation> operationList = operations.getOperation();
        for (CodegenOperation operation : operationList) {
            for (CodegenParameter cp : operation.allParams) {
                if (cp.isPathParam) {
                    if (cp.paramName.equals("id") && cp.dataType.equals("string")) {
                        cp.vendorExtensions.put("x-go-example", "\"a5183192-1e00-475f-b334-38e1f0bb1bc7\"");
                    }
                }
            }
        }
        return objs;
    }

    @Override
    public String toModelName(String name) {
        name = removeModelPrefix(name);
        return super.toModelName(name);
    }

    @Override
    public String toModelFilename(String name) {
        name = removeModelPrefix(name);
        return super.toModelFilename(name);
    }

    public String removeModelPrefix(String name) {
        if (!modelNameMapping.containsKey(name)) {
            if (additionalProperties.get("modelPrefixToRemove") != null) {
                String prefix = "^" + additionalProperties.get("modelPrefixToRemove");
                name = name.replaceFirst(prefix, "");
            }
        }
        return name;
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        co.operationId = removeTagPrefixFromOperationId(tag, co.operationId);
        co.nickname = co.operationId;
        super.addOperationToGroup(tag, resourcePath, operation, co, operations);
    }

    public String removeTagPrefixFromOperationId(String tag, String operationId) {
        String prefix = "^" + camelize(sanitizeName(removeNonNameElementToCamelCase(tag)));
        return operationId.replaceFirst(prefix, "");
    }
}
