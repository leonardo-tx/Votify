package br.com.votify.test;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.model.poll.VoteOption;
import br.com.votify.core.model.poll.field.VoteOptionName;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PollFactory {
    private static final List<String> TITLES = List.of(
            "Melhor Linguagem de Programação",
            "Melhor Framework Web",
            "Metodologia Ágil Favorita",
            "Selecione seus jogos favoritos",
            "Qual seu refrigerante favorito?",
            "FAVORITE BAND!",
            "タイトル: 推しアニメは？教えてください！",
            "Pizza com abacaxi...",
            "Qual sua rede social favorita?",
            "Qual o seu principal sistema operacional?",
            "Sino ang pinakagusto mong karakter sa Until Then?",
            "你最喜欢《凸变英雄X》里的哪个英雄？",
            "¿Cuál es el melhor juego multijugador?"
    );

    private static final List<String> DESCRIPTIONS = List.of(
            "Qual você considera a melhor linguagem de programação?",
            "Qual o melhor framework para desenvolvimento web?",
            "Qual sua metodologia ágil preferida?",
            "Apenas o dessa lista, claro!\nNão dá pra colocar mais que 5 opções :P\n\n(Por favor devs, coloquem mais ;-;)",
            "É pro meu TCC...\n\n🥹👉👈",
            "I'm a big fan of these bands, I want to know which ones you guys like the most :D",
            "数ある名作アニメの中から、あなたが\"最高！\"と思う作品を教えてください。",
            "",
            "Vamos descobrir qual rede social vai dominar!",
            "",
            "Mahal ko ang larong ito! 💜",
            "",
            "Seleccione até 3 opções"
    );

    private static final List<Boolean> USER_REGISTRATIONS = List.of(
            false, true, false, false, true, false, false, false, true, false, false, false, false
    );

    private static final List<Integer> CHOICE_LIMITS = List.of(
            1, 1, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 3
    );

    private static final List<List<String>> OPTION_NAMES = List.of(
            List.of("Java", "Python", "JavaScript", "C#"),
            List.of("Spring Boot", "React", "Angular", "Django"),
            List.of("Scrum", "Kanban", "XP"),
            List.of("Hollow Knight", "Hades", "Baldur's Gate 3", "The Witcher 3", "It Takes Two"),
            List.of("Coca-Cola", "Pepsi", "Sprite", "Guaraná Antarctica", "Fanta (Uva/Laranja)"),
            List.of("Sleep Token", "Bring Me The Horizon", "Falling In Reverse", "Linkin Park", "Imagine Dragons"),
            List.of("鬼滅の刃 (Demon Slayer)", "進撃の巨人 (Attack on Titan)", "チェンソーマン (Chainsaw Man)", "ダンダダン (Dandadan)", "呪術廻戦 (Jujutsu Kaisen)"),
            List.of("NÃO, NEM PENSAR"),
            List.of("Instagram", "TikTok", "Twitter/X", "Facebook", "LinkedIn"),
            List.of("Windows", "Linux", "Mac OS", "Free BSD", "Outro"),
            List.of("Mark Borja", "Catherine Portillo", "Nicole Lacsamana", "Ridel Gonzales", "Louise Ordunia"),
            List.of("林凌 (Lin Ling)", "Hero X", "魂电 (E-Soul)", "女王 (Queen)", "幸运青 (Lucky Cyan)"),
            List.of("League of Legends", "Counter Strike", "Valorant", "Call of Duty", "Dota 2")
    );

    private static final List<List<Integer>> OPTION_COUNTS = List.of(
            List.of(5, 2, 3, 5),
            List.of(1, 1, 1, 0),
            List.of(1, 0, 0),
            List.of(2, 2, 2, 2, 2),
            List.of(0, 0, 0, 2, 0),
            List.of(2, 2, 1, 1, 1),
            List.of(2, 1, 1, 2, 1),
            List.of(5),
            List.of(18, 13, 7, 4, 2),
            List.of(0, 0, 0, 0, 0),
            List.of(10, 6, 10, 3, 5),
            List.of(0, 0, 0, 0, 0),
            List.of(0, 0, 0, 0, 0)
    );

    private static final List<Duration> START_DATE_OFFSETS = List.of(
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Duration.ofDays(-6),
            Duration.ofDays(-4),
            Duration.ofDays(-1),
            Duration.ofDays(-90),
            Duration.ofMinutes(5),
            Duration.ofDays(-2),
            Duration.ZERO,
            Duration.ZERO
    );

    private static final List<Duration> END_DATE_OFFSETS = List.of(
            Duration.ofDays(7),
            Duration.ofDays(7),
            Duration.ofDays(7),
            Duration.ofDays(14),
            Duration.ofDays(30),
            Duration.ofDays(1),
            Duration.ofHours(12),
            Duration.ofMinutes(30),
            Duration.ofDays(-60),
            Duration.ofDays(60),
            Duration.ofDays(1),
            Duration.ofDays(14),
            Duration.ofDays(3)
    );

    private static final List<Long> RESPONSIBLE_IDS = List.of(
            1L, 1L, 2L, 4L, 7L, 5L, 16L, 3L, 12L, 12L, 30L, 42L, 9L
    );

    public static Poll createPoll(int index) {
        Instant now = Instant.now();
        Instant startDate = now.plus(START_DATE_OFFSETS.get(index));
        Instant endDate = now.plus(END_DATE_OFFSETS.get(index));

        List<VoteOption> voteOptions = IntStream.range(0, OPTION_NAMES.get(index).size())
                .mapToObj(i -> VoteOption.parseUnsafe(
                        VoteOptionName.parseUnsafe(OPTION_NAMES.get(index).get(i)),
                        OPTION_COUNTS.get(index).get(i),
                        i,
                        (long) (index + 1)
                ))
                .collect(Collectors.toList());

        return Poll.parseUnsafe(
                (long) (index + 1),
                Title.parseUnsafe(TITLES.get(index)),
                Description.parseUnsafe(DESCRIPTIONS.get(index)),
                startDate,
                endDate,
                USER_REGISTRATIONS.get(index),
                voteOptions,
                CHOICE_LIMITS.get(index),
                RESPONSIBLE_IDS.get(index)
        );
    }

    public static Poll createDefaultPoll() {
        return createPoll(0);
    }

    public static List<Poll> createPolls(int count) {
        return IntStream.range(0, count)
                .mapToObj(PollFactory::createPoll)
                .collect(Collectors.toList());
    }

    public static List<Poll> createAllPolls() {
        return createPolls(TITLES.size());
    }
}
