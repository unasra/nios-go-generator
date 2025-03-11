package com.infoblox.codegen;

import io.swagger.v3.oas.models.Operation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.*;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenParameter;
import org.openapitools.codegen.languages.AbstractGoCodegen;
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

/*
 This is the main class for the Terraform Provider NIOS module code generator.
 The generator is based on the Go code generator.
 The generator is used to generate Terraform modules for the NIOS API.

 In processOpts() we check if "modules" is defined in the additionalProperties.
 If it is not defined, we log an error and return. No files will be generated.
*/

public class NiosTerraformCodegen extends GoClientCodegen {
    private final Logger LOGGER = LoggerFactory.getLogger(NiosTerraformCodegen.class);

    /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -g flag.
     *
     * @return the friendly name for the generator
     */
    public String getName() {
        return "terraform-provider-nios";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    public String getHelp() {
        return "Generates a terraform-provider-nios library.";
    }

    public NiosTerraformCodegen() {
        super();
    }

    @Override
    public String toModelName(String name) {
        name = removeModelPrefix(name);
        return super.toModelName(name);
    }

    @Override
    public String toVarName(String name) {
        name = removeModelPrefix(name);
        return super.toVarName(name);
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

    @Override
    public String toApiFilename(String name) {
        // replace - with _ e.g. created-at => created_at
        String api = name.replaceAll("-", "_");
        // e.g. PetApi.go => pet_api.go
        api = underscore(api);
        if (isReservedFilename(api)) {
            LOGGER.warn("{}.go with suffix (reserved word) cannot be used as filename. Renamed to {}_.go", name,
                    api);
            api += "_";
        }
        return api;
    }
}
