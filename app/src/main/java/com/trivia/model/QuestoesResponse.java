
package com.trivia.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuestoesResponse {

    @SerializedName("response_code")
    private int codigoResposta;

    @SerializedName("results")
    private List<QuestaoApi> questoes;

    public int getCodigoResposta() {
        return codigoResposta;
    }

    public List<QuestaoApi> getQuestoes() {
        return questoes;
    }
}
