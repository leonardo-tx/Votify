package br.com.votify.test;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserFactory {
    private static final List<String> USERNAMES = List.of(
            "admin", "moderator", "common", "byces", "jinx", "viego", "vi-valor",
            "dorian", "eden", "vvv", "panam", "sebastian", "hornet", "zack",
            "momo", "ken", "tanjiro", "nezuko", "eleven", "will", "eddie",
            "aloy", "kratos", "atreus", "cloud", "tifa", "sephiroth", "jin",
            "ellie", "joel", "faye", "spike", "asuna", "kirito", "mikasa",
            "eren", "levi", "luffy", "zoro", "sanji", "kakashi", "naruto",
            "sakura", "geralt", "yennefer", "ciri", "vesemir", "noPolls"
    );

    private static final List<String> NAMES = List.of(
            "Administrator", "Moderator", "Common", "Byces", "Jinx", "Viego King", "Vi Valor",
            "Dorian Sleep", "Eden Carter", "V Samurai", "Panam Palmer", "Sebastian V", "Hornet HK", "Zack LP",
            "Momo Ayase", "Ken Takakura", "Tanjiro Kamado", "Nezuko Kamado", "Eleven Byers", "Will Byers", "Eddie Munson",
            "Aloy", "Kratos God", "Atreus Loki", "Cloud Strife", "Tifa Lockhart", "Sephiroth", "Jin Sakai",
            "Ellie Williams", "Joel Miller", "Faye Valentine", "Spike Spiegel", "Asuna Yuuki", "Kirito Kazuto", "Mikasa Ackerman",
            "Eren Yeager", "Levi Ackerman", "Monkey D. Luffy", "Roronoa Zoro", "Vinsmoke Sanji", "Kakashi Hatake", "Naruto Uzumaki",
            "Sakura Haruno", "Geralt of Rivia", "Yennefer Vengerberg", "Ciri Fiona", "Vesemir Wolf", "No Polls"
    );

    private static final List<String> EMAILS = List.of(
            "admin@votify.com.br", "moderator@votify.com.br", "common@votify.com.br", "littledoge@votify.com.br",
            "jinx@arcane.com", "viego@ruination.com", "vi@piltover.net", "dorian@sleeptoken.com",
            "eden@bmthorizon.net", "v@cyberpunk2077.net", "panam@cyberpunkmail.com", "sebastian@stardewvalley.com",
            "hornet@hallownest.org", "zack@linkinmail.com", "momo@dandandan.jp", "ken@dandandan.jp",
            "tanjiro@slayercorp.jp", "nezuko@slayercorp.jp", "el@hawkinsmail.com", "will@hawkinsmail.com",
            "eddie@corpsclub.com", "aloy@horizonmail.com", "kratos@olympusmail.com", "atreus@olympusmail.com",
            "cloud@ffvii.com", "tifa@ffvii.com", "sephiroth@shinra.co", "jin@ghosttsushima.com",
            "ellie@tloumail.com", "joel@tloumail.com", "faye@cowboybebop.space", "spike@cowboybebop.space",
            "asuna@saoworld.net", "kirito@saoworld.net", "mikasa@aotmail.com", "eren@aotmail.com",
            "levi@aotmail.com", "luffy@onepiecemail.com", "zoro@onepiecemail.com", "sanji@onepiecemail.com",
            "kakashi@leafmail.jp", "naruto@leafmail.jp", "sakura@leafmail.jp", "geralt@witchermail.com",
            "yennefer@witchermail.com", "ciri@witchermail.com", "vesemir@witchermail.com", "noPolls@votify.com.br"
    );

    private static final List<String> ENCRYPTED_PASSWORDS = List.of(
            "$2a$10$iZxot5DeNjNJbm6nHgNjgun1s3NGDZitVB3bsezXntbORUQ2lF5Xi",
            "$2a$10$Wqhv6Ma5siD3i0kSkDp/MOEMPSrOXdX.7FMQ9L6JIFnJAbCZ9941S",
            "$2a$12$oqvjaQnm1NwXpsGyCgiFCOiGgpiqKW5RiH4BWUMPGDvSLzIuRMaFm",
            "$2a$12$/4KESJGq2MqkWhNLnpMG9uQ0cl4cJHK6lf7qprmmwr.WbJdDQ9amy",
            "$2a$12$cHHYcJRXbd4FK8G4s.20J.LCJPOXbhyaXax43FJCRdJQW58mGkT16",
            "$2a$12$kHF/7kU.yMaR2JovQaXtYOrFztIEPxZjJhztsGQjQhyOyuoYVo/lO",
            "$2a$12$ub1MkBvlrhAA1HXBLq2fUO5rVi08JUNViRyvwo72GadVjw6FMQK22",
            "$2a$12$RkrIPcZ0qfhDAC1RoKzuA.5fQsWiciB9LEhby16Ga8IrpTNyRi8se",
            "$2a$12$fC2vPEBVARvZfm7YuZftneC39DBF7DMJNHSKEJ0ybmgA1hJvvVsRi",
            "$2a$12$YRK68xDZOBtAIBLUfq9evOK6eClVEjokX3FXUDELhwoUJUWpPAiCm",
            "$2a$12$jaLeLPCtdOW4PFFbSTsZyu/Luqy37kjNjMAmtti7n9jd7/y0sM5nu",
            "$2a$12$t6f8k4y.OAT8dqfO8PS5KuQ/SyvuAaQYkba30i1Cq3TXkEXCqeqiC",
            "$2a$12$80VA2y3vxApUj9.6zDql7.IH8U9uf2R9WFGdnhQAO1E3JWAIPhLBq",
            "$2a$12$drutYlnnYt3.mU17jGkclOC.cMq3.ph7trbX388TEyBYUPLrii42W",
            "$2a$12$W2/x.7c1b4Xv0l3nQq9p.eWkT72XY40DfeY4PW0c3NSDKERPaBntm",
            "$2a$12$4eu9xwXM8XrpMfv2d.ew7OnDHQBpBNot9cC6MtVk2RdNGPtOHyn8W",
            "$2a$12$bhLzxYtKcDybq2kTI4ZAZepgbCHPPeNJL4Vun4R7oWqkKblvUC9PK",
            "$2a$12$77s6Hv4oVs2MfgcMl28J8OID.hvCSv/yrGje38KH6o91o3tOOaoNi",
            "$2a$12$E8gUSJgO85CLqNvBnxCrCO5sTZ5tTCsyu5Dfyos99UAJDFaZ8YdmW",
            "$2a$12$LyOwSK0mHhCI9U1HL4yTY.IF8Sgh7Hc2lN4agfZogNECb2Ua/6QeO",
            "$2a$12$nZ.E5jTTrbzFEP3WdVlMd.EldJtNh/NtaPwf8x6PqfzdAVAm4z27C",
            "$2a$12$jQ7Jq3ALUnvCxerHeDSI6enDNOwUOugJNMxJv0XBZquKiSSOkyGHm",
            "$2a$12$66DX5a2PfoHAuhh8ejOKK.5BlrM/BsIJ9oOIXneCqD02D/xBC1x12",
            "$2a$12$EI0UcJcrMsSTAcEgmhkcFum9O8iMABgcBjanAnRZqOC7EeKYtJuq6",
            "$2a$12$qvD1dRh7L3YYWDw8.8Gk2ebYwX4fKq.yJApJssxDXe/yDrFT624MO",
            "$2a$12$0.cLE/EzWrVte1tbX9Qhn.FLVu28wo.WfJsdueYIpRm4.LDrAu136",
            "$2a$12$sewhCD3NRzfH6lIGUPWQ3ue6agMLuPx6i5AnMlLoVvg6OTwK1ZcAa",
            "$2a$12$w29/BxNafpRTjyT85gXsEuq7/8dleuc.Ey6l3N9V7tvJXN7tqRdLS",
            "$2a$12$EPxH3fv/PB.bY3zjLJvBFOL673xTykCejxd869tAS06Cz0iK/1RSm",
            "$2a$12$yDvW73oMfqWPO5PDLq6Wlu3neOHA/KanDfwobKq74jq8Bk60.Giku",
            "$2a$12$4jjfbpOoBjcNSDOgc9yn8et52LKp0SkaL2MYZ7MZvIV4P/uvGcoA6",
            "$2a$12$5aD3K5lbAVZ0II3r6..8b.iAB2hV5luEwx7XAzPzEnReTjTFhlZjG",
            "$2a$12$qZLB62OU3YHSo3rjspEJ3udGA6pau5VKPk2c5GNGRMyYbAWmxzMRC",
            "$2a$12$ouNfSjrSe3k207yAXpI52.203l2QjWK.CVqgMDC2y1wixWhFKqWAa",
            "$2a$12$UTs4Er9/tPhwUPlJtOpBMOY4DV6YD00NrEaSJG8ABRDf8fvrL8Um6",
            "$2a$12$U52.YrHqx3Yl4ruQ9ZRLxOpU5GMMPT4j.WNrRiJq6Qhk6NFGNF16y",
            "$2a$12$D5NIsN9D5gcLACHXk/105u3pt1YR18XyP8CdVT5cKfDHEZ5NTtgDu",
            "$2a$12$iG/SwY7ptLbNpvBxaPfXJuy93EdgHs1mqjTwM./Uu.ZrAN3zQwRC6",
            "$2a$12$LLEixXMyb504h94FStoWse/ohrPA/YsIqYmpq2.4Vh3QG5ZeVcdSO",
            "$2a$12$FkxRTCT9mH6DCoW1PfDeJ.TOpNFuAcK59P7i2GRaBbPcHythMb2A6",
            "$2a$12$OsVNNWzJnR4JFb4UJiHurOHnr.mEDd5SMeXS2qNq9GqmrcUBD5jh2",
            "$2a$12$D9ydhGULN4nbiY4y4hx9bOJS1kNZqSAuYShx46OAaciBwAmhufjS6",
            "$2a$12$hw.VYMPLwYniSKSkNHLQsewdOD9dZdJK8RzN8hL16mcFbQ0VvvOhe",
            "$2a$12$hQGnob9bB9T2MQTV1XbMfeRrmCP8ebKfJEFdCG3sobKBqNbBPwIVq",
            "$2a$12$V7uMuH2vzmyOetm1QDpiMu399DfT.vRTdp8lDbrJc6FOURVIxlDLS",
            "$2a$12$RjfNS43f9sg8KA8HdLDRP.rJEDyT9dSYhvWcQdeOYhojYmcaRi6IG",
            "$2a$12$yGVdmY/TRUCUNBNPYYeqNu0KTyxvgTaDMOSzbXiZHhxHD5cNCbPm6",
            "$2a$12$yGVdmY/TRUCUNBNPYYeqNu0KTyxvgTaDMOSzbXiZHhxHD5cNCbPm6"
    );

    private static final List<String> PASSWORDS = List.of(
            "admin123",
            "moderator321",
            "password123",
            "?*@$%sS!バイセス!SS%$@*?",
            "jinxFire123",
            "ruinationKing!",
            "piltoverPunch77",
            "sleepDeity99",
            "bmthRocks22",
            "samuraiV77",
            "panamRide88",
            "valleyStar55",
            "silkCharm!",
            "numbShadowsX",
            "momoVision123",
            "kenSpiritX",
            "waterBreath5",
            "sleepyBlood99",
            "hawkins011",
            "upsideWill!",
            "hellfire666",
            "horizonDawn1",
            "ghostSparta!!",
            "boyLoki77",
            "busterSword!",
            "fistsTifa22",
            "oneWingX",
            "ghostKhan77",
            "greenPlace44",
            "smugglerDad!",
            "bountyStar99",
            "seeYouSpace!",
            "rapierHeart7",
            "dualBlade77",
            "ackermanFury",
            "titanWrath!",
            "bladeCaptain!",
            "gomuGomu77",
            "santoryu22",
            "blackLeg33",
            "copyNinja!",
            "believeIt77",
            "pinkBlossom44",
            "witcherWolf1",
            "chaosSorceress!",
            "elderBlood22",
            "wolfMentorX",
            "wolfMentorX"
    );

    public static Password getPasswordFrom(int index) {
        return Password.parseUnsafe(PASSWORDS.get(index));
    }

    public static Password getPasswordFrom(User user) {
        return Password.parseUnsafe(PASSWORDS.get((int)(user.getId() - 1)));
    }

    public static User createUser(int index, boolean active) {
        return User.parseUnsafe(
                (long)(index + 1),
                Email.parseUnsafe(EMAILS.get(index)),
                UserName.parseUnsafe(USERNAMES.get(index)),
                Name.parseUnsafe(NAMES.get(index)),
                ENCRYPTED_PASSWORDS.get(index),
                index == 0 ? UserRole.ADMIN :
                        (index == 1 || index == 3) ? UserRole.MODERATOR : UserRole.COMMON,
                active
        );
    }

    public static User createDefaultAdmin() {
        return createUser(0, true);
    }

    public static User createDefaultModerator() {
        return createUser(1, true);
    }

    public static User createDefaultCommon() {
        return createUser(2, true);
    }

    public static List<User> createUsers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createUser(i, true))
                .collect(Collectors.toList());
    }
}
