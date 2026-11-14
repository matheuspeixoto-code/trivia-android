package com.trivia.database.seed;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public final class Seed {

    private Seed() {
    }

    public static void inserir(SQLiteDatabase db) {
        inserirConhecimentosGerais(db);
        inserirCienciaENatureza(db);
        inserirEsportes(db);
        inserirHistoria(db);
        inserirGeografia(db);
        inserirLivros(db);
        inserirFilmes(db);
        inserirMusica(db);
        inserirTelevisao(db);
        inserirComputadores(db);
        inserirMatematica(db);
        inserirAnimais(db);
        inserirMitologia(db);
    }

    private static void inserirConhecimentosGerais(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "General Knowledge");

        inserirQuestao(db, categoria, "Qual é a capital do Japão?", "easy",
                "Tóquio", "Seul", "Pequim", "Bangkok");
        inserirQuestao(db, categoria, "Qual é o maior planeta do Sistema Solar?", "easy",
                "Júpiter", "Saturno", "Terra", "Netuno");
        inserirQuestao(db, categoria, "Qual é a moeda oficial da Suíça?", "medium",
                "Franco suíço", "Euro", "Libra esterlina", "Coroa sueca");
        inserirQuestao(db, categoria, "Quem escreveu o livro 1984?", "medium",
                "George Orwell", "Aldous Huxley", "J. R. R. Tolkien", "Ernest Hemingway");
        inserirQuestao(db, categoria, "Em que cidade ocorreram os primeiros Jogos Olímpicos modernos?", "hard",
                "Atenas", "Paris", "Londres", "Roma");
    }

    private static void inserirCienciaENatureza(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Science & Nature");

        inserirQuestao(db, categoria, "Qual é a fórmula química da água?", "easy",
                "H2O", "CO2", "O2", "NaCl");
        inserirQuestao(db, categoria, "Qual é a estrela mais próxima da Terra?", "easy",
                "Sol", "Sirius", "Proxima Centauri", "Alpha Centauri");
        inserirQuestao(db, categoria, "Quantos ossos tem aproximadamente um adulto humano?", "medium",
                "206", "201", "208", "212");
        inserirQuestao(db, categoria, "Como se chama o processo pelo qual as plantas utilizam luz para produzir alimento?", "medium",
                "Fotossíntese", "Respiração", "Fermentação", "Transpiração");
        inserirQuestao(db, categoria, "Qual é aproximadamente a velocidade da luz no vácuo, em km/s?", "hard",
                "300000", "150000", "200000", "450000");
    }

    private static void inserirEsportes(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Sports");

        inserirQuestao(db, categoria, "Quantos jogadores de cada equipe ficam em campo no futebol?", "easy",
                "11", "9", "10", "12");
        inserirQuestao(db, categoria, "Quantos pontos vale um lance livre no basquete?", "easy",
                "1", "2", "3", "4");
        inserirQuestao(db, categoria, "Qual é a distância oficial de uma maratona, em quilômetros?", "medium",
                "42,195", "40", "41,5", "45");
        inserirQuestao(db, categoria, "Quantos torneios compõem o Grand Slam de tênis?", "medium",
                "4", "2", "3", "5");
        inserirQuestao(db, categoria, "Em que ano foram realizados os primeiros Jogos Olímpicos modernos?", "hard",
                "1896", "1900", "1888", "1924");
    }

    private static void inserirHistoria(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "History");

        inserirQuestao(db, categoria, "Quem foi o primeiro presidente do Brasil?", "medium",
                "Deodoro da Fonseca", "Getúlio Vargas", "Floriano Peixoto", "Dom Pedro II");
        inserirQuestao(db, categoria, "Em que ano caiu o Muro de Berlim?", "easy",
                "1989", "1985", "1991", "1979");
        inserirQuestao(db, categoria, "Qual rio foi fundamental para o desenvolvimento do Egito Antigo?", "easy",
                "Rio Nilo", "Rio Amazonas", "Rio Tigre", "Rio Danúbio");
        inserirQuestao(db, categoria, "Em que ano terminou a Segunda Guerra Mundial?", "easy",
                "1945", "1939", "1942", "1950");
        inserirQuestao(db, categoria, "Em que ano começou a Revolução Francesa?", "medium",
                "1789", "1776", "1804", "1815");
    }

    private static void inserirGeografia(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Geography");

        inserirQuestao(db, categoria, "Qual é o maior oceano do planeta?", "easy",
                "Oceano Pacífico", "Oceano Atlântico", "Oceano Índico", "Oceano Ártico");
        inserirQuestao(db, categoria, "Qual é a capital da Austrália?", "medium",
                "Canberra", "Sydney", "Melbourne", "Perth");
        inserirQuestao(db, categoria, "Em qual continente está localizado o deserto do Saara?", "easy",
                "África", "Ásia", "América do Sul", "Oceania");
        inserirQuestao(db, categoria, "Em qual cordilheira está localizado o Monte Everest?", "medium",
                "Himalaia", "Andes", "Alpes", "Montanhas Rochosas");
        inserirQuestao(db, categoria, "Em qual continente fica o Japão?", "easy",
                "Ásia", "Europa", "África", "América do Norte");
    }

    private static void inserirLivros(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Entertainment: Books");

        inserirQuestao(db, categoria, "Quem escreveu o livro 1984?", "easy",
                "George Orwell", "Aldous Huxley", "Ernest Hemingway", "Charles Dickens");
        inserirQuestao(db, categoria, "Quem escreveu O Hobbit?", "easy",
                "J. R. R. Tolkien", "C. S. Lewis", "George R. R. Martin", "J. K. Rowling");
        inserirQuestao(db, categoria, "Quem criou o personagem Sherlock Holmes?", "medium",
                "Arthur Conan Doyle", "Agatha Christie", "Edgar Allan Poe", "Júlio Verne");
        inserirQuestao(db, categoria, "Quem escreveu Dom Quixote?", "medium",
                "Miguel de Cervantes", "William Shakespeare", "Victor Hugo", "Machado de Assis");
        inserirQuestao(db, categoria, "Quem escreveu O Pequeno Príncipe?", "easy",
                "Antoine de Saint-Exupéry", "Jules Verne", "Victor Hugo", "Albert Camus");
    }

    private static void inserirFilmes(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Entertainment: Film");

        inserirQuestao(db, categoria, "Quem dirigiu o filme Titanic, lançado em 1997?", "easy",
                "James Cameron", "Steven Spielberg", "Christopher Nolan", "Ridley Scott");
        inserirQuestao(db, categoria, "Qual estúdio produziu o filme Toy Story?", "easy",
                "Pixar", "DreamWorks", "Studio Ghibli", "Warner Bros.");
        inserirQuestao(db, categoria, "Quem criou a franquia Star Wars?", "medium",
                "George Lucas", "Stanley Kubrick", "James Cameron", "Peter Jackson");
        inserirQuestao(db, categoria, "Quem dirigiu o filme Tubarão, lançado em 1975?", "medium",
                "Steven Spielberg", "James Cameron", "Francis Ford Coppola", "Tim Burton");
        inserirQuestao(db, categoria, "Qual foi o primeiro longa-metragem de animação produzido pela Pixar?", "hard",
                "Toy Story", "Monstros S.A.", "Procurando Nemo", "Vida de Inseto");
    }

    private static void inserirMusica(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Entertainment: Music");

        inserirQuestao(db, categoria, "Qual instrumento musical tradicionalmente possui 88 teclas?", "easy",
                "Piano", "Violino", "Flauta", "Violoncelo");
        inserirQuestao(db, categoria, "Qual banda lançou a música Hey Jude?", "easy",
                "The Beatles", "Queen", "The Rolling Stones", "Pink Floyd");
        inserirQuestao(db, categoria, "Em qual país nasceu Ludwig van Beethoven?", "medium",
                "Alemanha", "Áustria", "França", "Itália");
        inserirQuestao(db, categoria, "Quantas cordas possui um violão tradicional de seis cordas?", "easy",
                "6", "4", "5", "8");
        inserirQuestao(db, categoria, "Qual compositor criou a ópera A Flauta Mágica?", "hard",
                "Wolfgang Amadeus Mozart", "Johann Sebastian Bach", "Frédéric Chopin", "Ludwig van Beethoven");
    }

    private static void inserirTelevisao(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Entertainment: Television");

        inserirQuestao(db, categoria, "Quem criou a série Os Simpsons?", "medium",
                "Matt Groening", "Seth MacFarlane", "Trey Parker", "Mike Judge");
        inserirQuestao(db, categoria, "Qual é o nome do protagonista de Breaking Bad?", "easy",
                "Walter White", "Saul Goodman", "Jesse Pinkman", "Hank Schrader");
        inserirQuestao(db, categoria, "Em qual cidade se passa principalmente a série Friends?", "easy",
                "Nova York", "Los Angeles", "Chicago", "Boston");
        inserirQuestao(db, categoria, "Qual emissora britânica produziu originalmente Doctor Who?", "hard",
                "BBC", "ITV", "Channel 4", "Sky");
        inserirQuestao(db, categoria, "Quem criou o desenho Bob Esponja?", "medium",
                "Stephen Hillenburg", "Walt Disney", "Genndy Tartakovsky", "Craig McCracken");
    }

    private static void inserirComputadores(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Science: Computers");

        inserirQuestao(db, categoria, "O que significa a sigla CPU?", "easy",
                "Central Processing Unit", "Computer Personal Unit", "Central Program Utility", "Computer Processing Utility");
        inserirQuestao(db, categoria, "Qual linguagem é utilizada para estruturar o conteúdo de páginas web?", "easy",
                "HTML", "SQL", "Python", "C");
        inserirQuestao(db, categoria, "Qual destes sistemas numéricos é utilizado como base na representação binária?", "easy",
                "Base 2", "Base 8", "Base 10", "Base 16");
        inserirQuestao(db, categoria, "Qual destas linguagens é conhecida por utilizar orientação a objetos?", "medium",
                "Java", "HTML", "CSS", "SQL");
        inserirQuestao(db, categoria, "Qual tipo de memória perde seus dados quando o computador é desligado?", "medium",
                "RAM", "SSD", "ROM", "Disco rígido");
    }

    private static void inserirMatematica(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Science: Mathematics");

        inserirQuestao(db, categoria, "Qual é o menor número primo?", "easy",
                "2", "1", "3", "0");
        inserirQuestao(db, categoria, "Qual é a raiz quadrada de 81?", "easy",
                "9", "8", "7", "10");
        inserirQuestao(db, categoria, "Qual é a soma dos ângulos internos de um triângulo?", "medium",
                "180 graus", "90 graus", "270 graus", "360 graus");
        inserirQuestao(db, categoria, "Qual é o valor aproximado de Pi?", "easy",
                "3,14", "2,14", "4,13", "3,41");
        inserirQuestao(db, categoria, "Quanto é 12 elevado ao quadrado?", "medium",
                "144", "124", "132", "156");
    }

    private static void inserirAnimais(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Animals");

        inserirQuestao(db, categoria, "Qual é o maior animal conhecido do planeta?", "easy",
                "Baleia-azul", "Elefante-africano", "Tubarão-branco", "Girafa");
        inserirQuestao(db, categoria, "Qual é o animal terrestre mais rápido?", "easy",
                "Guepardo", "Leão", "Cavalo", "Leopardo");
        inserirQuestao(db, categoria, "Como é chamado um grupo de leões?", "medium",
                "Alcateia", "Manada", "Cardume", "Rebanho");
        inserirQuestao(db, categoria, "Quantos corações possui um polvo?", "hard",
                "3", "1", "2", "4");
        inserirQuestao(db, categoria, "A qual grupo de animais pertence o pinguim?", "easy",
                "Aves", "Mamíferos", "Répteis", "Anfíbios");
    }

    private static void inserirMitologia(SQLiteDatabase db) {
        int categoria = obterOuCriarCategoria(db, "Mythology");

        inserirQuestao(db, categoria, "Quem era o principal deus do Olimpo na mitologia grega?", "easy",
                "Zeus", "Poseidon", "Ares", "Hermes");
        inserirQuestao(db, categoria, "Qual deus da mitologia nórdica está associado ao trovão?", "easy",
                "Thor", "Loki", "Odin", "Baldur");
        inserirQuestao(db, categoria, "Qual deus egípcio era representado com cabeça de chacal?", "medium",
                "Anúbis", "Rá", "Osíris", "Hórus");
        inserirQuestao(db, categoria, "Qual criatura mitológica tinha corpo humano e cabeça de touro?", "medium",
                "Minotauro", "Centauro", "Grifo", "Cérbero");
        inserirQuestao(db, categoria, "Qual deusa grega era associada à sabedoria?", "easy",
                "Atena", "Afrodite", "Hera", "Ártemis");
    }

    private static int obterOuCriarCategoria(SQLiteDatabase db, String nome) {
        try (Cursor cursor = db.query(
                "categoria",
                new String[]{"id"},
                "nome = ?",
                new String[]{nome},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            }
        }

        ContentValues valores = new ContentValues();
        valores.put("nome", nome);

        long id = db.insert("categoria", null, valores);
        return id == -1 ? -1 : (int) id;
    }

    private static void inserirQuestao(
            SQLiteDatabase db,
            int categoriaId,
            String enunciado,
            String dificuldade,
            String correta,
            String incorreta1,
            String incorreta2,
            String incorreta3) {

        if (categoriaId <= 0) return;

        try (Cursor cursor = db.query(
                "questao",
                new String[]{"id"},
                "enunciado = ? AND categoria_id = ?",
                new String[]{enunciado, String.valueOf(categoriaId)},
                null, null, null,
                "1")) {

            if (cursor.moveToFirst()) return;
        }

        ContentValues valores = new ContentValues();
        valores.put("enunciado", enunciado);
        valores.put("dificuldade", dificuldade);
        valores.put("tipo", "multiple");
        valores.put("categoria_id", categoriaId);
        valores.put("resposta_correta", correta);
        valores.put("resposta_incorreta_1", incorreta1);
        valores.put("resposta_incorreta_2", incorreta2);
        valores.put("resposta_incorreta_3", incorreta3);

        db.insert("questao", null, valores);
    }
}