# JUM 

JUM stands for `Java Utility Manager`. This tool is able to install JAR files in the JUM repository *(`~/.jumrepo`)* and generate executable scripts *(currently, JUM only generates perl scripts for Unix systems, but in the future batch scripts will be generated for Windows systems too)* for those tools.

For more information about this tool, run JUM with the `--help` flag *(you have to package the project first `mvn package` and then run with `java -jar jum-jar.jar`)*.

# 1. Requirements

## 1.1 Installation requirements

- The [Maven cli utility](https://maven.apache.org/download.cgi).
- [Perl](https://www.perl.org/get.html)
- [Java 22](https://bell-sw.com/pages/downloads/#jdk-22)

## 1.2 Runtime requirements

- [Perl](https://www.perl.org/get.html) *(Only required in Unix, installed by default in most Unix systems)*
- [Java 22](https://bell-sw.com/pages/downloads/#jdk-22)


# 2. How to install

JUM is installed by using the [perl](https://www.perl.org/get.html) script located at `./scripts/self-install`. This script uses the `mvn` utility *(this must be [installed](https://maven.apache.org/download.cgi) in your system)*, and then installs itself using JUM.

This script compiles the java source code, and then uses the generated JAR *(the JUM utility)* to install itself just like any other utility.

## 2.1 NOTE:
 You must ensure that the JUM repository's bin directory *(most likely `~/.jumrepo/bin/`)* is present in the `$PATH` variable of your system for you to be able to call the installed utilities *(including JUM itself)*.

If this steps are followed, you should be able to run `jum --version` to check if JUM is properly installed *(you might have to restart your terminal in Windows for the value of the `$PATH` variable to refresh)*.

