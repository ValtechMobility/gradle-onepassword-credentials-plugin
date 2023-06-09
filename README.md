# gradle-onepassword-credentials-plugin

Gradle plugin that allows to authenticate to a repository through one password (cli).

## Usage
To use this plugin, you first need to setup 1Password CLI:
https://developer.1password.com/docs/cli/get-started

After this is completed, the plugin can be used in the project:

build.gradle
````groovy
buildscript {
    repositories {
        mavenCentral()
    }
    
    dependencies {
        "io.github.valtechmobility:gradle-credentials-onepassword:$CURRENT_VERSION"
    }
}

````

With the dependency handling out of the way, the actual maven can now be configured:
build.gradle
````groovy
import io.github.valtechmobility.gradle.credentials.onepassword.OnepasswordAccessCredentials

// Singleton improves performance as accessing the cli is quite slow: 
final def securedMavenCredentials = new OnepasswordAccessCredentials("Example Work Credentials")
allprojects {
    repositories {
        maven {
            url = "https://maven.yourcompany.com/example"
            configuredCredentials = securedMavenCredentials
            authentication {
                basic(BasicAuthentication)
            }
        }
    }
}
````
