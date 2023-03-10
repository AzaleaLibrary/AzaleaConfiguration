<div>
  <a href="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/ci.yml">
    <img alt="Plugin CI" src="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/ci.yml/badge.svg?branch=master" />
  </a>
    <a href="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/cd.yml">
    <img alt="Plugin CD" src="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/cd.yml/badge.svg?branch=master" />
  </a>
  <a href="https://azalealibrary.net/#/releases/net/azalealibrary/configuration">
    <img src="https://azalealibrary.net/api/badge/latest/releases/net/azalealibrary/configuration?color=40c14a&name=Azalea%20Configuration&prefix=v" />
  </a>
</div>

# AzaleaConfiguration

## How to

### Import `AzaleaConfiguration`

In **pom.xml**:

```xml
    <repositories>
        <!-- other repositories -->
        <repository>
            <id>azalea-repo</id>
            <name>Azalea Repository</name>
            <url>https://azalealibrary.net/releases</url>
        </repository>
    </repositories>
    
    <dependencies>
        <!-- other dependencies -->
        <dependency>
            <groupId>net.azalealibrary</groupId>
            <artifactId>configuration</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
```

### Usage

#### 1. Creating a configuration file and registering it

Assume we want to a place to store some global configuration for a plugin. The first step is to declare a class implementing the `Configurable` interface:

```java
class MyConfiguration implements Configurable {

    private final Property<Integer> aNumber = new Property<>(PropertyType.INTEGER, () -> 1, "a_number", "some number description", true);
    private final Property<String> aString = new Property<>(PropertyType.STRING, () -> "text", "a_string", "some string description", false);
    private final ListProperty<Vector> someVectors = new ListProperty<>(PropertyType.VECTOR, ArrayList::new, "some_vectors", "some vector list description", false);

    public int getNumber() {
        return aNumber.get();
    }

    public String getString() {
        return aString.get();
    }

    public List<Vector> getSomeVectors() {
        return someVectors.get();
    }

    @Override
    public String getName() {
        return "my_configuration";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(aNumber, aString, someVectors);
    }
}
```

Now that we have defined some properties (`aNumber`, `aString` & `someVectors`), we want to load their values from a yml file.

We need to get a `FileConfiguration` object which represents a set of configuration data from a yml file, this is done via the `AzaleaConfigurationApi#load` method.

From a `FileConfiguration` object, we can then call the `FileConfiguration#load` method.

Once the data has been loaded, we can register it in order to configure the properties via the `/configure` command.

Here's an example of how we may load the configuration data into an instance of `MyConfiguration` and registering it:

```java
class MyPlugin extends JavaPlugin {

    public final MyConfiguration myConfiguration = new MyConfiguration();

    @Override
    public void onEnable() {
        // get the associated `FileConfiguration` object of `MY_CONFIGURATION` ("<plugin_data_folder>/my_configuration.yml")
        FileConfiguration fileConfiguration = AzaleaConfigurationApi.load(this, myConfiguration.getName());
        fileConfiguration.load(myConfiguration);       // load properties from file
        AzaleaConfigurationApi.register(myConfiguration);    // register config to command
    }

    @Override
    public void onDisable() {
        // save `myConfiguration` as a yaml file ("<plugin_data_folder>/my_configurations.yml")
        FileConfiguration fileConfiguration = AzaleaConfigurationApi.load(this, myConfiguration.getName());
        fileConfiguration.save(myConfiguration);       // save properties to file
    }
}
```

Note, the api does not handle saving the data of your configuration, therefore it is up to the implementer handle that accordingly.

This can be done during the disabling cycle of the plugin, as shown in the example above.

#### 2. Dynamic configurations

Suppose we have a list of `MyConfiguration` configurations we want to load from the `<plugin_data_folder>/my_configurations` directory with `AzaleaConfigurationApi#loadAll`.

```java
class MyPlugin extends JavaPlugin {

    private final List<MyConfiguration> configurations = new ArrayList<>();

    @Override
    public void onEnable() {
        // load all yaml files in "<plugin_data_folder>/my_configurations"
        for (FileConfiguration fileConfiguration : AzaleaConfigurationApi.getAllFileConfigurations(this, "my_configurations")) {
            MyConfiguration configuration = new MyConfiguration();  // create a new `MyConfiguration` object for every config
            fileConfiguration.load(configuration);                  // load properties from file
            AzaleaConfigurationApi.register(configuration);         // register config to command
            configurations.add(configuration);
        }
    }

    @Override
    public void onDisable() {
        // save all configs as yaml files in "<plugin_data_folder>/my_configurations"
        for (MyConfiguration configuration : configurations) {
            FileConfiguration fileConfiguration = AzaleaConfigurationApi.getAllFileConfiguration(this, "my_configurations", configuration.getName());
            fileConfiguration.save(configuration);                  // save properties to file
        }
    }
}
```

#### 3. Property types

// TODO