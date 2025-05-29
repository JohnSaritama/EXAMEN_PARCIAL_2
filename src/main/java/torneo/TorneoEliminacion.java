package torneo;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TorneoEliminacion {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Integer> jugadores = new ArrayList<>();
        for (int i = 1; i <= 16; i++) jugadores.add(i);
        String[] rondas = {"OCTAVOS DE FINAL", "CUARTOS DE FINAL", "SEMIFINAL", "FINAL"};
        int ronda = 0;

        ExecutorService executor = Executors.newFixedThreadPool(8);

        while (jugadores.size() > 1) {
            System.out.println("===== " + rondas[ronda++] + " =====");
            List<Future<ResultadoPartido>> resultados = new ArrayList<>();
            List<Integer> ganadores = new ArrayList<>();
            int n = jugadores.size();

            for (int i = 0; i < n / 2; i++) {
                int j1 = jugadores.get(i);
                int j2 = jugadores.get(n - 1 - i);
                Callable<ResultadoPartido> partido = new Partido(j1, j2);
                resultados.add(executor.submit(partido));
            }

            for (Future<ResultadoPartido> f : resultados) {
                ResultadoPartido res = f.get();
                res.imprimir();
                ganadores.add(res.ganador);
            }

            jugadores = ganadores;
        }

        executor.shutdown();
        System.out.println("\n\uD83C\uDFC6 ¡Campeón del torneo: Jugador " + jugadores.get(0) + "!");
    }
}

class Partido implements Callable<ResultadoPartido> {
    int j1, j2;

    public Partido(int j1, int j2) {
        this.j1 = j1;
        this.j2 = j2;
    }

    public ResultadoPartido call() throws Exception {
        List<Integer> sets = new ArrayList<>();
        int s1 = 0, s2 = 0;
        Random r = new Random();

        while (s1 < 2 && s2 < 2) {
            Thread.sleep(1500 + r.nextInt(501));
            int ganador = r.nextBoolean() ? j1 : j2;
            sets.add(ganador);
            if (ganador == j1) s1++; else s2++;
        }

        int ganadorPartido = s1 == 2 ? j1 : j2;
        return new ResultadoPartido(j1, j2, sets, ganadorPartido);
    }
}

class ResultadoPartido {
    int j1, j2, ganador;
    List<Integer> sets;

    public ResultadoPartido(int j1, int j2, List<Integer> sets, int ganador) {
        this.j1 = j1;
        this.j2 = j2;
        this.sets = sets;
        this.ganador = ganador;
    }

    public void imprimir() {
        System.out.println("Jugador " + j1 + " vs Jugador " + j2);
        for (int i = 0; i < sets.size(); i++) {
            System.out.println("Set " + (i + 1) + ": Jugador " + sets.get(i));
        }
        System.out.println("Ganador del partido: Jugador " + ganador + "\n");
    }
}