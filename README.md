<div>
  <a href="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/ci.yml">
    <img alt="Azalea Configuration CI" src="https://github.com/AzaleaLibrary/AzaleaConfiguration/actions/workflows/ci.yml/badge.svg?branch=master" />
  </a>
  <a href="https://azalealibrary.net/#/releases/net/azalealibrary/configuration">
    <img src="https://azalealibrary.net/api/badge/latest/releases/net/azalealibrary/configuration?color=40c14a&name=Azalea%20Configuration&prefix=v" />
  </a>
</div>

# AzaleaConfiguration

### How to

#### Import `AzaleaConfiguration`

In **pom.xml**:

```xml
    <repositories>
        <!-- other repositories -->
        <repository>
            <id>azalea-repo-releases</id>
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
