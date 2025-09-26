package util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // Formato padrão para exibição de datas
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Retorna a data atual formatada como string.
     */
    public static String formatarDataAtual() {
        return formatter.format(LocalDateTime.now());
    }

    /**
     * Formata uma data específica como string.
     */
    public static String formatarData(LocalDateTime data) {
        return formatter.format(data);
    }

    /**
     * Adiciona dias a uma data e retorna o resultado.
     */
    public static LocalDateTime adicionarDias(LocalDateTime data, int dias) {
        return data.plusDays(dias);
    }

    /**
     * Verifica se a data está em horário de verão na zona Brasil.
     * Obs: Pode não refletir mudanças futuras na legislação.
     */
    public static boolean estaEmHorarioDeVerao(LocalDateTime data) {
        ZoneId zonaBrasil = ZoneId.of("America/Sao_Paulo");
        ZonedDateTime zoned = data.atZone(zonaBrasil);
        return zonaBrasil.getRules().isDaylightSavings(zoned.toInstant());
    }

    /**
     * Converte uma data para outro fuso horário.
     * Retorna a data original se o fuso for inválido.
     */
    public static ZonedDateTime converterFuso(LocalDateTime data, String zonaDestino) {
        try {
            ZoneId destino = ZoneId.of(zonaDestino);
            return data.atZone(ZoneId.systemDefault()).withZoneSameInstant(destino);
        } catch (Exception e) {
            System.out.println("Fuso horário inválido: " + zonaDestino);
            return data.atZone(ZoneId.systemDefault());
        }
    }
}
