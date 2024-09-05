package br.com.screenmatch.screenmatch.principal;

import br.com.screenmatch.screenmatch.model.DadosEpisodios;
import br.com.screenmatch.screenmatch.model.DadosSerie;
import br.com.screenmatch.screenmatch.model.DadosTemporada;
import br.com.screenmatch.screenmatch.model.Episodios;
import br.com.screenmatch.screenmatch.service.ConsumoApi;
import br.com.screenmatch.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class principal {
    private Scanner scanner = new Scanner(System.in);
    private  ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5d1def7";


    public void exibeMenu(){
        System.out.println("Digíte o nome da série");
        var nomeSerie = scanner.nextLine().toLowerCase();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        //https://www.omdbapi.com/?t=once+upon+a+time&apikey=5d1def7

        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i<= dados.totalTemporadas(); i++){
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+")+ "&season="+ i + API_KEY);
			DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        List<String> nomes = Arrays.asList("Jaque", "Paulo", "Nico", "Camila", "João");
//
//        nomes.stream()
//                .sorted()
//                .limit(4)
//                .filter(n -> n.startsWith("N"))
//                .map(n -> n.toUpperCase())
//                .forEach(System.out::println);

        List<DadosEpisodios> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\n Top 5 espiódio: ");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodios> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodios(t.numero(), d))
                ).collect(Collectors.toList())
    }
}
