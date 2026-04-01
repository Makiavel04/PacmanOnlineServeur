## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
# PacmanOnlineServeur
mkdir -p build_server/libs
cd build_server/libs
jar xf ../../lib/json-20231013.jar
jar xf ../../lib/pacman-online-commun.jar
cd ../..
jar cvfe ServeurComplet.jar ServeurPacman -C bin . -C build_server/libs .

# touch stub_server.sh
#!/bin/sh
# On récupère le chemin du script
MYSELF=`which "$0" 2>/dev/null`
[ $? -gt 0 -a -f "$0" ] && MYSELF="./$0"
# On lance Java
exec java -jar "$MYSELF" "$@"

# exec
cat stub_server.sh ServeurComplet.jar > pacman-server
chmod +x pacman-server