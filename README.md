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
        "com.valtechmobility.gradle.plugin.credentials.onepassword:$CURRENT_VERSION"
    }
}


````

With the dependency handling out of the way, the actual maven can now be configured:
build.gradle
````groovy
repositories {
    
}
````