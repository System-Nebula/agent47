{
  pkgs,
  lib,
  config,
  ...
}:
{
  # https://devenv.sh/languages/
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk;
  };

  # https://devenv.sh/packages/
  packages = [
    pkgs.graalvmPackages.graalvm-ce
    pkgs.maven
  ];

  # https://devenv.sh/scripts/
  scripts = {
    build = {
      description = "compile the project";
      exec = "mvn clean package";
    };
    run = {
      exec = "java -jar target/untitled-1.0-SNAPSHOT.jar";
      description = "runs the tool";
    };
    test = {
      exec = "mvn test";
      description = "run unit tests";
    };

    regen = {
      exec = "mvn eclipse:eclipse";
      description = "regenerates the classpath";

    };
    native-compile = {
      exec = "mvn client:build";
      description = "run native build with graalvm";
    };
    devhelp = {
      description = "Prints this message";
      exec = ''
        echo 
        echo Helper scripts
        echo
        ${pkgs.gnused}/bin/sed -e 's| |••|g' -e 's|=| |' <<EOF | ${pkgs.util-linuxMinimal}/bin/column -t | ${pkgs.gnused}/bin/sed -e 's|^|🦾 |' -e 's|••| |g'
        ${lib.generators.toKeyValue { } (lib.mapAttrs (name: value: value.description) config.scripts)}
        EOF
        echo
      '';
    };
  };

  enterShell = ''
    java -version
    native-image --version
    devhelp
  '';

}
