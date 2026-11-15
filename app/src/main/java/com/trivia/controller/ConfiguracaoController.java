package com.trivia.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.trivia.api.ApiService;
import com.trivia.database.QuestaoRepositorio;
import com.trivia.model.Categoria;
import com.trivia.model.CategoriasResponse;
import com.trivia.model.Jogador;
import com.trivia.model.Partida;
import com.trivia.model.Questao;
import com.trivia.model.QuestaoApi;
import com.trivia.model.QuestoesResponse;
import com.trivia.retrofit.RetrofitCategoria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracaoController {

    private static final String TAG = "ConfiguracaoController";
    private static final String TIPO_QUESTAO = "multiple";

    private final Context context;
    private final Handler handler;
    private final ExecutorService executorBanco;

    private Call<CategoriasResponse> chamadaCategorias;
    private Call<QuestoesResponse> chamadaQuestoes;

    private boolean encerrado = false;

    public interface CategoriasCallback {
        void onSucesso(List<Categoria> categorias, boolean locais);
    }

    public interface PartidaCallback {
        void onErroValidacao(String mensagem);

        void onErro(String mensagem);

        void onPartidaPreparada(Partida partida, boolean offline);
    }

    public ConfiguracaoController(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler(Looper.getMainLooper());
        this.executorBanco = Executors.newSingleThreadExecutor();
    }

    public String validarQuantidade(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Informe a quantidade de questões.";
        }

        try {
            int quantidade = Integer.parseInt(texto.trim());

            if (quantidade < 1 || quantidade > 50) {
                return "Informe um valor entre 1 e 50.";
            }

        } catch (NumberFormatException e) {
            return "Quantidade inválida.";
        }

        return null;
    }

    public void carregarCategorias(CategoriasCallback callback) {

        ApiService api = RetrofitCategoria.getApiService();

        chamadaCategorias = api.buscarCategorias();

        chamadaCategorias.enqueue(new Callback<CategoriasResponse>() {

            @Override
            public void onResponse(
                    Call<CategoriasResponse> call,
                    Response<CategoriasResponse> response) {

                if (encerrado) return;

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getCategorias() != null) {

                    List<Categoria> categoriasApi =
                            response.body().getCategorias();

                    List<Categoria> categorias = new ArrayList<>();

                    categorias.add(new Categoria(0, "Todas as categorias"));
                    categorias.addAll(categoriasApi);

                    executorBanco.execute(() -> {

                        try {
                            QuestaoRepositorio repositorio =
                                    new QuestaoRepositorio(context);

                            repositorio.salvarCategoriasParaOffline(categoriasApi);

                            Log.d(TAG, "Categorias armazenadas no SQLite.");

                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao salvar categorias.", e);
                        }

                        handler.post(() -> {
                            if (!encerrado) {
                                callback.onSucesso(categorias, false);
                            }
                        });
                    });

                } else {
                    Log.w(TAG, "Falha ao carregar categorias online.");
                    carregarCategoriasOffline(callback);
                }
            }

            @Override
            public void onFailure(
                    Call<CategoriasResponse> call,
                    Throwable t) {

                if (encerrado || call.isCanceled()) return;

                Log.w(TAG, "Falha na conexão ao carregar categorias.", t);

                carregarCategoriasOffline(callback);
            }
        });
    }

    private void carregarCategoriasOffline(CategoriasCallback callback) {

        executorBanco.execute(() -> {

            List<Categoria> categorias;

            try {
                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(context);

                categorias = repositorio.listarCategoriasOffline();

            } catch (Exception e) {
                Log.e(TAG, "Erro ao consultar categorias locais.", e);

                categorias = new ArrayList<>();
            }

            if (categorias == null) {
                categorias = new ArrayList<>();
            }

            if (categorias.isEmpty()) {
                categorias.add(new Categoria(0, "Todas as categorias"));
            }

            List<Categoria> resultado = categorias;

            handler.post(() -> {
                if (!encerrado) {
                    callback.onSucesso(resultado, true);
                }
            });
        });
    }

    public void iniciarPartida(
            String nomeJogador,
            Categoria categoria,
            String textoQuantidade,
            String dificuldade,
            int tempo,
            PartidaCallback callback) {

        String erro = validarQuantidade(textoQuantidade);

        if (erro != null) {
            callback.onErroValidacao(erro);
            return;
        }

        if (categoria == null) {
            callback.onErro("Aguarde o carregamento das categorias.");
            return;
        }

        int quantidade = Integer.parseInt(textoQuantidade.trim());

        String nomeCategoria = categoria.getNome();

        Integer categoriaApiId =
                categoria.getId() == 0 ? null : categoria.getId();

        if (categoriaApiId == null) {
            buscarQuestoesOnline(
                    quantidade,
                    null,
                    nomeCategoria,
                    dificuldade,
                    tempo,
                    nomeJogador,
                    callback
            );
        } else {
            buscarQuestoesOnline(
                    quantidade,
                    categoriaApiId,
                    nomeCategoria,
                    dificuldade,
                    tempo,
                    nomeJogador,
                    callback
            );
        }
    }

    private void buscarQuestoesOnline(
            int quantidade,
            Integer categoriaId,
            String nomeCategoria,
            String dificuldade,
            int tempo,
            String nomeJogador,
            PartidaCallback callback) {

        chamadaQuestoes = RetrofitCategoria.getApiService()
                .buscarQuestoes(
                        quantidade,
                        categoriaId,
                        dificuldade,
                        TIPO_QUESTAO
                );

        chamadaQuestoes.enqueue(new Callback<QuestoesResponse>() {

            @Override
            public void onResponse(
                    Call<QuestoesResponse> call,
                    Response<QuestoesResponse> response) {

                if (encerrado) return;

                if (!response.isSuccessful() || response.body() == null) {
                    buscarQuestoesOffline(
                            quantidade, nomeCategoria, dificuldade,
                            tempo, nomeJogador, callback
                    );
                    return;
                }

                QuestoesResponse resultado = response.body();

                if (resultado.getCodigoResposta() != 0
                        || resultado.getQuestoes() == null
                        || resultado.getQuestoes().isEmpty()) {

                    Log.w(TAG, obterMensagemErroApi(
                            resultado.getCodigoResposta()
                    ));

                    buscarQuestoesOffline(
                            quantidade, nomeCategoria, dificuldade,
                            tempo, nomeJogador, callback
                    );
                    return;
                }

                List<Questao> questoes = converterQuestoes(
                        resultado.getQuestoes()
                );

                if (questoes.size() < quantidade) {
                    buscarQuestoesOffline(
                            quantidade, nomeCategoria, dificuldade,
                            tempo, nomeJogador, callback
                    );
                    return;
                }

                salvarCacheEPrepararPartida(
                        questoes,
                        quantidade,
                        tempo,
                        nomeJogador,
                        callback
                );
            }

            @Override
            public void onFailure(
                    Call<QuestoesResponse> call,
                    Throwable t) {

                if (encerrado || call.isCanceled()) return;

                Log.w(TAG, "Falha na busca online. Tentando SQLite.", t);

                buscarQuestoesOffline(
                        quantidade, nomeCategoria, dificuldade,
                        tempo, nomeJogador, callback
                );
            }
        });
    }

    private List<Questao> converterQuestoes(List<QuestaoApi> questoesApi) {

        List<Questao> questoes = new ArrayList<>();

        for (QuestaoApi questaoApi : questoesApi) {

            if (questaoApi == null
                    || questaoApi.getEnunciado() == null
                    || questaoApi.getRespostaCorreta() == null
                    || questaoApi.getRespostasIncorretas() == null
                    || questaoApi.getRespostasIncorretas().size() != 3) {
                continue;
            }

            Questao questao = new Questao(
                    questaoApi.getCategory(),
                    questaoApi.getDifficulty(),
                    questaoApi.getEnunciado(),
                    questaoApi.getRespostaCorreta(),
                    questaoApi.getRespostasIncorretas()
            );

            questoes.add(questao);
        }

        return questoes;
    }

    private void salvarCacheEPrepararPartida(
            List<Questao> questoes,
            int quantidade,
            int tempo,
            String nomeJogador,
            PartidaCallback callback) {

        executorBanco.execute(() -> {

            try {
                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(context);

                repositorio.salvarQuestoesParaOffline(questoes);

                Log.d(TAG, "Questões armazenadas no cache offline.");

            } catch (Exception e) {
                Log.e(TAG, "Falha ao salvar questões no cache.", e);
            }

            handler.post(() -> {
                if (encerrado) return;

                Partida partida = criarPartida(
                        questoes, tempo, nomeJogador
                );

                callback.onPartidaPreparada(partida, false);
            });
        });
    }

    private void buscarQuestoesOffline(
            int quantidade,
            String nomeCategoria,
            String dificuldade,
            int tempo,
            String nomeJogador,
            PartidaCallback callback) {

        executorBanco.execute(() -> {

            List<Questao> questoes = new ArrayList<>();

            try {
                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(context);

                questoes = repositorio.buscarQuestoesOffline(
                        quantidade,
                        nomeCategoria,
                        dificuldade
                );

            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar questões no SQLite.", e);
            }

            List<Questao> resultado = questoes;

            handler.post(() -> {

                if (encerrado) return;

                if (resultado == null || resultado.size() < quantidade) {

                    int disponiveis = resultado == null
                            ? 0 : resultado.size();

                    callback.onErro(
                            "Sem conexão ou questões insuficientes no cache "
                                    + "para essa categoria/dificuldade ("
                                    + disponiveis + "/" + quantidade + ")."
                    );

                    return;
                }

                Partida partida = criarPartida(
                        resultado, tempo, nomeJogador
                );

                callback.onPartidaPreparada(partida, true);
            });
        });
    }

    private Partida criarPartida(
            List<Questao> questoes,
            int tempo,
            String nomeJogador) {

        Jogador jogador = new Jogador(0, nomeJogador);

        return new Partida(
                0,
                jogador,
                questoes,
                0,
                0,
                0,
                tempo
        );
    }

    private String obterMensagemErroApi(int codigo) {

        switch (codigo) {
            case 1:
                return "A API não tem questões suficientes nessa configuração.";
            case 2:
                return "Parâmetros inválidos enviados à API.";
            case 3:
                return "Token de sessão não encontrado.";
            case 4:
                return "Token de sessão esgotado.";
            case 5:
                return "Limite de requisições da API atingido.";
            default:
                return "Erro desconhecido ao buscar questões.";
        }
    }

    public void encerrar() {
        encerrado = true;

        if (chamadaCategorias != null) {
            chamadaCategorias.cancel();
        }

        if (chamadaQuestoes != null) {
            chamadaQuestoes.cancel();
        }

        executorBanco.shutdownNow();
    }
}
