package Partie.Pacman.Agents.Strategies;

import java.util.Arrays;
import java.util.List;

import pacman.online.commun.moteur.TypeAgent;

public enum TypeStrategie {
    IMMOBILE(true, TypeAgent.PACMAN, TypeAgent.FANTOME),
    ALEATOIRE(true, TypeAgent.PACMAN, TypeAgent.FANTOME),
    CONTROLEE1(false, TypeAgent.PACMAN, TypeAgent.FANTOME),
    CONTROLEE2(false, TypeAgent.PACMAN, TypeAgent.FANTOME),
    PACMAN_INTELLIGENT(true, TypeAgent.PACMAN),
    GHOST_INTELLIGENT(true, TypeAgent.FANTOME),
    GHOST_BLINKY(true, TypeAgent.FANTOME),
    GHOST_CLYDE(true, TypeAgent.FANTOME),
    GHOST_INKY(true, TypeAgent.FANTOME),
    GHOST_PINKY(true, TypeAgent.FANTOME);

    private final boolean utilisableBot;
    private final List<TypeAgent> typesAgentsCompatibles;

    TypeStrategie(boolean utilisableBot, TypeAgent... typesAgentsCompatibles) {
        this.utilisableBot = utilisableBot;
        this.typesAgentsCompatibles = List.of(typesAgentsCompatibles);
    }

    public boolean isUtilisableBot() {
        return utilisableBot;
    }

    public boolean estCompatibleAvec(TypeAgent typeAgent) {
        return typesAgentsCompatibles.contains(typeAgent);
    }

    public static List<String> getNomStrategieCompatibles(TypeAgent typeAgent) {
        return Arrays.stream(TypeStrategie.values())
            .filter(v -> v.isUtilisableBot() && v.estCompatibleAvec(typeAgent))
            .map(Enum::name)//Remplace chaque typeStrategie par son nom
            .sorted()
            .toList();

    }
}
