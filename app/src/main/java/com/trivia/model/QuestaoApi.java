
package com.trivia.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuestaoApi {

    private String category;
    private String type;
    private String difficulty;

    @SerializedName("question")
    private String enunciado;

    @SerializedName("correct_answer")
    private String respostaCorreta;

    @SerializedName("incorrect_answers")
    private List<String> respostasIncorretas;

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public List<String> getRespostasIncorretas() {
        return respostasIncorretas;
    }
}
