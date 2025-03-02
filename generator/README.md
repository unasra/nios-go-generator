# OpenAPI Generator for the BloxOne client libraries

## Overview
This is a custom generator for the bloxone libraries, based on the [OpenAPI Generator](https://openapi-generator.tech) project.

## Usage
To customize the generator, follow these steps:

1. Modify the corresponding language implementation class (e.g., `BloxoneGoClientCodegen` for Go).
2. Run `mvn package` in your generator project.
3. Use the generated jar file with OpenAPI Generator. For example:

```bash
java -cp /path/to/openapi-generator-cli.jar:/path/to/your.jar org.openapitools.codegen.OpenAPIGenerator generate -g bloxone-go-client -i /path/to/openapi.yaml -o ./test
```

## Language implementations for this generator

### bloxone-go-client 

- Class: `com.infoblox.codegen.BloxoneGoClientCodegen`
- Custom features:
  - Removes prefix added by proto-gen-swagger to the models. 
  - Removes the operation group prefix for each operation in the client.

### universal-ddi-python-client

- Class: `com.infoblox.codegen.UniversalDdiPythonClientCodegen`
- Custom features:
  - Removes prefix added by proto-gen-swagger to the models.
  - Removes the operation group prefix for each operation in the client.
  - Moved README of each package to the package folder when generateSourceCodeOnly is true

### bloxone-ansible

- Class:`com.infoblox.codegen.BloxoneAnsibleModuleCodegen`
- Custom features:
  - New language implementation based on AbstractPythonCodegen
  - Accepts configuration options for the module in additional properties
  - Removes prefix added by proto-gen-swagger to the models.
  - Removes the operation group prefix for each operation in the client.
  - Uses HandlebarsTemplateEngine for more complex logic in the templates. Templates are in the `resources/bloxone-ansible-module` folder
- Configuration: The following configuration options are available for each module:

    | Name             | Description                                                          | Optional/Required | Default                        |
    |------------------|----------------------------------------------------------------------|-------------------|--------------------------------|
    | `name`           | The name of the module file that will be generated                   | Required          | None                           |
    | `api`            | The universal-ddi-python-client API class that the module will use         | Required          | None                           |
    | `model`          | The universal-ddi-python-client model class that the module will use       | Required          | None                           |
    | `createMethod`   | The method in the API class that will be used to create a new object | Optional          | `create`                       |
    | `readMethod`     | The method in the API class that will be used to read an object      | Optional          | `read`                         |
    | `updateMethod`   | The method in the API class that will be used to update an object    | Optional          | `update`                       |
    | `deleteMethod`   | The method in the API class that will be used to delete an object    | Optional          | `delete`                       |
    | `listMethod`     | The method in the API class that will be used to list objects        | Optional          | `list`                         |
    | `apiPackage`     | The package name of the universal-ddi-python-client API class              | Optional          | packageName                    |
    | `infoOnly`       | If true, only `{name}_info.py` will be generated                     | Optional          | `false`                        |
    | `primaryKeys`    | The primary key fields of the model                                  | Optional          | `name`                         |
    | `ddiInheritance` | If true, the module adds "_inherit=full" to all requests             | Optional          | `false`                        |
    | `requiredFields` | The required fields of the model                                     | Optional          | Required properties as in spec |
    | `excludeForDiff` | The fields that should be excluded from the diff calculation         | Optional          | `updated_at`                   |


    Each module has to be a different key within `modules` property. 
    The module key must be the camelized version of the operation group in the OpenAPI specification. For example, if the operation group is `fixed_address`, the module key should be `FixedAddress`.
    
    Here is an example configuration for a module:
    
    ```yaml
    additionalProperties:
      modules:
        FixedAddress:               
          name:           ipam_fixed_address
          api:            FixedAddressApi
          model:          FixedAddress
          createMethod:   create
          readMethod:     read
          updateMethod:   update
          deleteMethod:   delete
          listMethod:     list
          apiPackage:     ipam
          infoOnly:       false
          primaryKeys:    
            - address
            - ip_space
          ddiInheritance: true
          requiredFields: 
            - address
            - ip_space
            - match_type
            - match_value
          excludeForDiff: 
            - updated_at
    ```

## Other features

### HandlebarsTemplateEngine

The ansible generator uses the HandlebarsTemplateEngine to generate the files.  
This is a more powerful template engine than the default Mustache engine.  
It allows for more complex logic in the templates.

The custom adapter for the HandlebarsTemplateEngine is in the `com.infoblox.codegen.templating.CustomHandlebarsEngineAdapter`.
This custom adapter fixes a bug in the default HandlebarsTemplateEngine that causes it to not work with Java 11.<br>
See https://github.com/jknack/handlebars.java/issues/940

### SwaggerParser

The OpenAPI Generator uses the Swagger Parser to parse the OpenAPI specification.
BloxOne specifications are usually in Swagger 2.0 format. The Swagger 2.0 format does not allow siblings next to $ref.
However, our specifications do have siblings next to $ref especially fields such as `readOnly` and `description`.

The custom swagger converter in the `com.infoblox.codegen.converter.CustomSwaggerConverter` allows these siblings to be present in the parsed and converted OpenAPI 3.1 specification.
This allows us to generate the client code correctly for these properties.

### AnsibleDocumentationRenderer

The Ansible generator uses the `com.infoblox.codegen.renderer.AnsibleDocumentationRenderer` to render the documentation for the module.
This renderer extends org.commonmark.renderer.text.CoreTextContentNodeRenderer to convert the markdown content in the OpenAPI specification to plain text.
Supported ansible documentation macros are: 
- I(`text`) - italic
- B(`text`) - bold
- C(`text`) - inline code
