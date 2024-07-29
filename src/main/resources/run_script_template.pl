#!/usr/bin/env perl 
use strict;
use warnings;
use feature 'say';

use File::Basename qw(dirname basename);
use File::Spec::Functions qw(catfile);
use File::Which qw(which);
use Term::ANSIColor qw(colored);

my $JUM_GENERATOR_VERSION = "%s";
my $JDK_VERSION = "%s";
my $SCRIPT_PATH = dirname $0;
my $SCRIPT_NAME = basename $0;
my $PATH_TO_JAVA= catfile $SCRIPT_PATH, "..", "jdk", $JDK_VERSION, "java";
my $JAR_PATH = catfile dirname($SCRIPT_PATH), "bin", $SCRIPT_NAME . ".jar";

my $arg_string = "";
if (scalar @ARGV gt 0) {
    $arg_string .= ' "' . $_ . '" ' for @ARGV;
}

if (! -f $PATH_TO_JAVA) {
    $PATH_TO_JAVA = which 'java';
    print colored("There is no JDK installed from JUM jdk manager for this utility. ", "red");
    say "Defaulting to system's java location ($PATH_TO_JAVA).";
}

if ($ENV{JUM_DEBUG}) {
    say "=======> \$JUM_DEBUG IS SET, THE FOLLOWING IS DEBUG INFO ABOUT THE SCRIPT";
    say "This script was generated with JUM v.$JUM_GENERATOR_VERSION";
    say "This script uses the JDK v.$JDK_VERSION";
    say "This script's name: $SCRIPT_NAME";
    say "Running the following command:";
    say "     '$PATH_TO_JAVA -jar $JAR_PATH $arg_string'";
    say "=======> \$JUM_DEBUG IS SET, THE PREVIOUS WAS INFO ABOUT THE SCRIPT";
}

system("$PATH_TO_JAVA -jar $JAR_PATH $arg_string");
