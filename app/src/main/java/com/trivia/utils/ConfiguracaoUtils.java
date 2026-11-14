package com.trivia.utils;

public class ConfiguracaoUtils {
    public static String obterDificuldade(int posicao) {

        switch (posicao) {
            case 1:
                return "easy";

            case 2:
                return "medium";

            case 3:
                return "hard";

            default:
                return null;
        }
    }
}
