---- Usuários ----
INSERT INTO TB_USER (id, userName, name, email, password, USER_TYPE)
VALUES
    -- Senha admin123
    (1, 'admin', 'Administrator', 'admin@votify.com.br', '$2a$10$iZxot5DeNjNJbm6nHgNjgun1s3NGDZitVB3bsezXntbORUQ2lF5Xi', 'ADMIN'),
    -- Senha moderator321
    (2, 'moderator', 'Moderator', 'moderator@votify.com.br', '$2a$10$Wqhv6Ma5siD3i0kSkDp/MOEMPSrOXdX.7FMQ9L6JIFnJAbCZ9941S', 'MODERATOR'),
    -- Senha password123
    (3, 'common', 'Common', 'common@votify.com.br', '$2a$12$oqvjaQnm1NwXpsGyCgiFCOiGgpiqKW5RiH4BWUMPGDvSLzIuRMaFm', 'COMMON'),
    -- Senha ?*@$%sS!バイセス!SS%$@*?
    (4, 'byces', 'Byces', 'littledoge@votify.com.br', '$2a$12$/4KESJGq2MqkWhNLnpMG9uQ0cl4cJHK6lf7qprmmwr.WbJdDQ9amy', 'MODERATOR'),
    -- Senha jinxFire123
    (5, 'jinx', 'Jinx', 'jinx@arcane.com', '$2a$12$cHHYcJRXbd4FK8G4s.20J.LCJPOXbhyaXax43FJCRdJQW58mGkT16', 'COMMON'),
    -- Senha ruinationKing!
    (6, 'viego', 'Viego King', 'viego@ruination.com', '$2a$12$kHF/7kU.yMaR2JovQaXtYOrFztIEPxZjJhztsGQjQhyOyuoYVo/lO', 'COMMON'),
    -- Senha piltoverPunch77
    (7, 'vi-valor', 'Vi Valor', 'vi@piltover.net', '$2a$12$ub1MkBvlrhAA1HXBLq2fUO5rVi08JUNViRyvwo72GadVjw6FMQK22', 'COMMON'),
    -- Senha sleepDeity99
    (8, 'dorian', 'Dorian Sleep', 'dorian@sleeptoken.com', '$2a$12$RkrIPcZ0qfhDAC1RoKzuA.5fQsWiciB9LEhby16Ga8IrpTNyRi8se', 'COMMON'),
    -- Senha bmthRocks22
    (9, 'eden', 'Eden Carter', 'eden@bmthorizon.net', '$2a$12$fC2vPEBVARvZfm7YuZftneC39DBF7DMJNHSKEJ0ybmgA1hJvvVsRi', 'COMMON'),
    -- Senha samuraiV77
    (10, 'vvv', 'V Samurai', 'v@cyberpunk2077.net', '$2a$12$YRK68xDZOBtAIBLUfq9evOK6eClVEjokX3FXUDELhwoUJUWpPAiCm', 'COMMON'),
    -- Senha panamRide88
    (11, 'panam', 'Panam Palmer', 'panam@cyberpunkmail.com', '$2a$12$jaLeLPCtdOW4PFFbSTsZyu/Luqy37kjNjMAmtti7n9jd7/y0sM5nu', 'COMMON'),
    -- Senha valleyStar55
    (12, 'sebastian', 'Sebastian V', 'sebastian@stardewvalley.com', '$2a$12$t6f8k4y.OAT8dqfO8PS5KuQ/SyvuAaQYkba30i1Cq3TXkEXCqeqiC', 'COMMON'),
    -- Senha silkCharm!
    (13, 'hornet', 'Hornet HK', 'hornet@hallownest.org', '$2a$12$80VA2y3vxApUj9.6zDql7.IH8U9uf2R9WFGdnhQAO1E3JWAIPhLBq', 'COMMON'),
    -- Senha numbShadowsX
    (14, 'zack', 'Zack LP', 'zack@linkinmail.com', '$2a$12$drutYlnnYt3.mU17jGkclOC.cMq3.ph7trbX388TEyBYUPLrii42W', 'COMMON'),
    -- Senha momoVision123
    (15, 'momo', 'Momo Ayase', 'momo@dandandan.jp', '$2a$12$W2/x.7c1b4Xv0l3nQq9p.eWkT72XY40DfeY4PW0c3NSDKERPaBntm', 'COMMON'),
    -- Senha kenSpiritX
    (16, 'ken', 'Ken Takakura', 'ken@dandandan.jp', '$2a$12$4eu9xwXM8XrpMfv2d.ew7OnDHQBpBNot9cC6MtVk2RdNGPtOHyn8W', 'COMMON'),
    -- Senha waterBreath5
    (17, 'tanjiro', 'Tanjiro Kamado', 'tanjiro@slayercorp.jp', '$2a$12$bhLzxYtKcDybq2kTI4ZAZepgbCHPPeNJL4Vun4R7oWqkKblvUC9PK', 'COMMON'),
    -- Senha sleepyBlood99
    (18, 'nezuko', 'Nezuko Kamado', 'nezuko@slayercorp.jp', '$2a$12$77s6Hv4oVs2MfgcMl28J8OID.hvCSv/yrGje38KH6o91o3tOOaoNi', 'COMMON'),
    -- Senha hawkins011
    (19, 'eleven', 'Eleven Byers', 'el@hawkinsmail.com', '$2a$12$E8gUSJgO85CLqNvBnxCrCO5sTZ5tTCsyu5Dfyos99UAJDFaZ8YdmW', 'COMMON'),
    -- Senha upsideWill!
    (20, 'will', 'Will Byers', 'will@hawkinsmail.com', '$2a$12$LyOwSK0mHhCI9U1HL4yTY.IF8Sgh7Hc2lN4agfZogNECb2Ua/6QeO', 'COMMON'),
    -- Senha hellfire666
    (21, 'eddie', 'Eddie Munson', 'eddie@corpsclub.com', '$2a$12$nZ.E5jTTrbzFEP3WdVlMd.EldJtNh/NtaPwf8x6PqfzdAVAm4z27C', 'COMMON'),
    -- Senha horizonDawn1
    (22, 'aloy', 'Aloy', 'aloy@horizonmail.com', '$2a$12$jQ7Jq3ALUnvCxerHeDSI6enDNOwUOugJNMxJv0XBZquKiSSOkyGHm', 'COMMON'),
    -- Senha ghostSparta!!
    (23, 'kratos', 'Kratos God', 'kratos@olympusmail.com', '$2a$12$66DX5a2PfoHAuhh8ejOKK.5BlrM/BsIJ9oOIXneCqD02D/xBC1x12', 'COMMON'),
    -- Senha boyLoki77
    (24, 'atreus', 'Atreus Loki', 'atreus@olympusmail.com', '$2a$12$EI0UcJcrMsSTAcEgmhkcFum9O8iMABgcBjanAnRZqOC7EeKYtJuq6', 'COMMON'),
    -- Senha busterSword!
    (25, 'cloud', 'Cloud Strife', 'cloud@ffvii.com', '$2a$12$qvD1dRh7L3YYWDw8.8Gk2ebYwX4fKq.yJApJssxDXe/yDrFT624MO', 'COMMON'),
    -- Senha fistsTifa22
    (26, 'tifa', 'Tifa Lockhart', 'tifa@ffvii.com', '$2a$12$0.cLE/EzWrVte1tbX9Qhn.FLVu28wo.WfJsdueYIpRm4.LDrAu136', 'COMMON'),
    -- Senha oneWingX
    (27, 'sephiroth', 'Sephiroth', 'sephiroth@shinra.co', '$2a$12$sewhCD3NRzfH6lIGUPWQ3ue6agMLuPx6i5AnMlLoVvg6OTwK1ZcAa', 'COMMON'),
    -- Senha ghostKhan77
    (28, 'jin', 'Jin Sakai', 'jin@ghosttsushima.com', '$2a$12$w29/BxNafpRTjyT85gXsEuq7/8dleuc.Ey6l3N9V7tvJXN7tqRdLS', 'COMMON'),
    -- Senha greenPlace44
    (29, 'ellie', 'Ellie Williams', 'ellie@tloumail.com', '$2a$12$EPxH3fv/PB.bY3zjLJvBFOL673xTykCejxd869tAS06Cz0iK/1RSm', 'COMMON'),
    -- Senha smugglerDad!
    (30, 'joel', 'Joel Miller', 'joel@tloumail.com', '$2a$12$yDvW73oMfqWPO5PDLq6Wlu3neOHA/KanDfwobKq74jq8Bk60.Giku', 'COMMON'),
    -- Senha bountyStar99
    (31, 'faye', 'Faye Valentine', 'faye@cowboybebop.space', '$2a$12$4jjfbpOoBjcNSDOgc9yn8et52LKp0SkaL2MYZ7MZvIV4P/uvGcoA6', 'COMMON'),
    -- Senha seeYouSpace!
    (32, 'spike', 'Spike Spiegel', 'spike@cowboybebop.space', '$2a$12$5aD3K5lbAVZ0II3r6..8b.iAB2hV5luEwx7XAzPzEnReTjTFhlZjG', 'COMMON'),
    -- Senha rapierHeart7
    (33, 'asuna', 'Asuna Yuuki', 'asuna@saoworld.net', '$2a$12$qZLB62OU3YHSo3rjspEJ3udGA6pau5VKPk2c5GNGRMyYbAWmxzMRC', 'COMMON'),
    -- Senha dualBlade77
    (34, 'kirito', 'Kirito Kazuto', 'kirito@saoworld.net', '$2a$12$ouNfSjrSe3k207yAXpI52.203l2QjWK.CVqgMDC2y1wixWhFKqWAa', 'COMMON'),
    -- Senha ackermanFury
    (35, 'mikasa', 'Mikasa Ackerman', 'mikasa@aotmail.com', '$2a$12$UTs4Er9/tPhwUPlJtOpBMOY4DV6YD00NrEaSJG8ABRDf8fvrL8Um6', 'COMMON'),
    -- Senha titanWrath!
    (36, 'eren', 'Eren Yeager', 'eren@aotmail.com', '$2a$12$U52.YrHqx3Yl4ruQ9ZRLxOpU5GMMPT4j.WNrRiJq6Qhk6NFGNF16y', 'COMMON'),
    -- Senha bladeCaptain!
    (37, 'levi', 'Levi Ackerman', 'levi@aotmail.com', '$2a$12$D5NIsN9D5gcLACHXk/105u3pt1YR18XyP8CdVT5cKfDHEZ5NTtgDu', 'COMMON'),
    -- Senha gomuGomu77
    (38, 'luffy', 'Monkey D. Luffy', 'luffy@onepiecemail.com', '$2a$12$iG/SwY7ptLbNpvBxaPfXJuy93EdgHs1mqjTwM./Uu.ZrAN3zQwRC6', 'COMMON'),
    -- Senha santoryu22
    (39, 'zoro', 'Roronoa Zoro', 'zoro@onepiecemail.com', '$2a$12$LLEixXMyb504h94FStoWse/ohrPA/YsIqYmpq2.4Vh3QG5ZeVcdSO', 'COMMON'),
    -- Senha blackLeg33
    (40, 'sanji', 'Vinsmoke Sanji', 'sanji@onepiecemail.com', '$2a$12$FkxRTCT9mH6DCoW1PfDeJ.TOpNFuAcK59P7i2GRaBbPcHythMb2A6', 'COMMON'),
    -- Senha copyNinja!
    (41, 'kakashi', 'Kakashi Hatake', 'kakashi@leafmail.jp', '$2a$12$OsVNNWzJnR4JFb4UJiHurOHnr.mEDd5SMeXS2qNq9GqmrcUBD5jh2', 'COMMON'),
    -- Senha believeIt77
    (42, 'naruto', 'Naruto Uzumaki', 'naruto@leafmail.jp', '$2a$12$D9ydhGULN4nbiY4y4hx9bOJS1kNZqSAuYShx46OAaciBwAmhufjS6', 'COMMON'),
    -- Senha pinkBlossom44
    (43, 'sakura', 'Sakura Haruno', 'sakura@leafmail.jp', '$2a$12$hw.VYMPLwYniSKSkNHLQsewdOD9dZdJK8RzN8hL16mcFbQ0VvvOhe', 'COMMON'),
    -- Senha witcherWolf1
    (44, 'geralt', 'Geralt of Rivia', 'geralt@witchermail.com', '$2a$12$hQGnob9bB9T2MQTV1XbMfeRrmCP8ebKfJEFdCG3sobKBqNbBPwIVq', 'COMMON'),
    -- Senha chaosSorceress!
    (45, 'yennefer', 'Yennefer Vengerberg', 'yennefer@witchermail.com', '$2a$12$V7uMuH2vzmyOetm1QDpiMu399DfT.vRTdp8lDbrJc6FOURVIxlDLS', 'COMMON'),
    -- Senha elderBlood22
    (46, 'ciri', 'Ciri Fiona', 'ciri@witchermail.com', '$2a$12$RjfNS43f9sg8KA8HdLDRP.rJEDyT9dSYhvWcQdeOYhojYmcaRi6IG', 'COMMON'),
    -- Senha wolfMentorX
    (47, 'vesemir', 'Vesemir Wolf', 'vesemir@witchermail.com', '$2a$12$yGVdmY/TRUCUNBNPYYeqNu0KTyxvgTaDMOSzbXiZHhxHD5cNCbPm6', 'COMMON');



---- Enquetes ----
INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (1, 'Melhor Linguagem de Programação', 'Qual você considera a melhor linguagem de programação?', FALSE, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 7 DAY, 1);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (1, 0, 'Java', 0),
    (1, 1, 'Python', 0),
    (1, 2, 'JavaScript', 0),
    (1, 3, 'C#', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (1, 1, 1), (1, 6, 1), (1, 30, 1), (1, 14, 1), (1, 15, 1), -- Java
    (1, 7, 2), (1, 47, 2), -- Python
    (1, 4, 4), (1, 46, 4), (1, 22, 4), -- Javascript
    (1, 2, 8), (1, 5, 8), (1, 33, 8), (1, 13, 8), (1, 11, 8); -- C#


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (2, 'Melhor Framework Web', 'Qual o melhor framework para desenvolvimento web?', TRUE, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 7 DAY, 1);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (2, 0, 'Spring Boot', 0),
    (2, 1, 'React', 0),
    (2, 2, 'Angular', 0),
    (2, 3, 'Django', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (2, 4, 1), -- Spring Boot
    (2, 1, 2), -- React
    (2, 6, 4); -- Angular


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (3, 'Metodologia Ágil Favorita', 'Qual sua metodologia ágil preferida?', FALSE, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 7 DAY, 2);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (3, 0, 'Scrum', 0),
    (3, 1, 'Kanban', 0),
    (3, 2, 'XP', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES (3, 1, 1); -- Scrum


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (4, 'Selecione seus jogos favoritos', CONCAT('Apenas o dessa lista, claro!', CHAR(10), 'Não dá pra colocar mais que 5 opções :P', CHAR(10), CHAR(10), '(Por favor devs, coloquem mais ;-;)'), FALSE, 5, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 14 DAY, 4);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (4, 0, 'Hollow Knight', 0),
    (4, 1, 'Hades', 0),
    (4, 2, 'Baldur''s Gate 3', 0),
    (4, 3, 'The Witcher 3', 0),
    (4, 4, 'It Takes Two', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (4, 1, 31), -- All options
    (4, 2, 3), -- Hollow Knight & Hades
    (4, 4, 4), -- Baldur's Gate 3
    (4, 5, 29); -- All options, except Hades


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (5, 'Qual seu refrigerante favorito?', CONCAT('É pro meu TCC...', CHAR(10), CHAR(10), '🥹👉👈'), TRUE, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 1 MONTH, 7);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (5, 0, 'Coca-Cola', 0),
    (5, 1, 'Pepsi', 0),
    (5, 2, 'Sprite', 0),
    (5, 3, 'Guaraná Antarctica', 0),
    (5, 4, 'Fanta (Uva/Laranja)', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (5, 7, 8),
    (5, 16, 8);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (6, 'FAVORITE BAND!', 'I''m a big fan of these bands, I want to know which ones you guys like the most :D', FALSE, 1, UTC_TIMESTAMP() - INTERVAL 6 DAY, UTC_TIMESTAMP() + INTERVAL 1 DAY, 5);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (6, 0, 'Sleep Token', 0),
    (6, 1, 'Bring Me The Horizon', 0),
    (6, 2, 'Falling In Reverse', 0),
    (6, 3, 'Linkin Park', 0),
    (6, 4, 'Imagine Dragons', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (6, 1, 1), (6, 6, 1), -- Sleep Token
    (6, 3, 2), (6, 7, 2), -- Bring Me The Horizon
    (6, 4, 4), -- Falling In Reverse
    (6, 5, 8), -- Linkin Park
    (6, 2, 16); -- Imagine Dragons


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (7, 'タイトル: 推しアニメは？教えてください！', '数ある名作アニメの中から、あなたが"最高！"と思う作品を教えてください。', FALSE, 1, UTC_TIMESTAMP() - INTERVAL 4 DAY, UTC_TIMESTAMP() + INTERVAL 12 HOUR, 16);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (7, 0, '鬼滅の刃 (Demon Slayer)', 0),
    (7, 1, '進撃の巨人 (Attack on Titan)', 0),
    (7, 2, 'チェンソーマン (Chainsaw Man)', 0),
    (7, 3, 'ダンダダン (Dandadan)', 0),
    (7, 4, '呪術廻戦 (Jujutsu Kaisen)', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (7, 1, 1), (7, 6, 1), -- 鬼滅の刃 (Demon Slayer)
    (7, 2, 2), -- 進撃の巨人 (Attack on Titan)
    (7, 3, 4), -- チェンソーマン (Chainsaw Man)
    (7, 4, 8), (7, 7, 8), -- ダンダダン (Dandadan)
    (7, 5, 16); -- 呪術廻戦 (Jujutsu Kaisen)


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (8, 'Pizza com abacaxi...', '', FALSE, 1, UTC_TIMESTAMP() - INTERVAL 1 DAY, UTC_TIMESTAMP() + INTERVAL 30 MINUTE, 3);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES (8, 0, 'NÃO, NEM PENSAR', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (8, 2, 1),
    (8, 3, 1),
    (8, 4, 1),
    (8, 6, 1),
    (8, 7, 1);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (9, 'Qual sua rede social favorita?', 'Vamos descobrir qual rede social vai dominar!', TRUE, 1, UTC_TIMESTAMP() - INTERVAL 3 MONTH, UTC_TIMESTAMP() - INTERVAL 2 MONTH, 12);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (9, 0, 'Instagram', 0),
    (9, 1, 'TikTok', 0),
    (9, 2, 'Twitter/X', 0),
    (9, 3, 'Facebook', 0),
    (9, 4, 'LinkedIn', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    -- Instagram
    (9, 1, 1), (9, 2, 1), (9, 4, 1), (9, 7, 1), (9, 11, 1),
    (9, 13, 1), (9, 16, 1), (9, 18, 1), (9, 20, 1), (9, 23, 1),
    (9, 25, 1), (9, 27, 1), (9, 30, 1), (9, 31, 1), (9, 33, 1),
    (9, 38, 1), (9, 40, 1), (9, 44, 1),

    -- TikTok
    (9, 3, 2), (9, 6, 2), (9, 9, 2), (9, 14, 2),
    (9, 17, 2), (9, 21, 2), (9, 24, 2), (9, 28, 2), (9, 32, 2),
    (9, 36, 2), (9, 39, 2), (9, 43, 2), (9, 47, 2),

    -- Twitter/X
    (9, 5, 4), (9, 19, 4), (9, 29, 4), (9, 41, 4), (9, 42, 4),
    (9, 45, 4), (9, 46, 4),

    -- Facebook
    (9, 8, 8), (9, 26, 8), (9, 37, 8), (9, 12, 8),

    -- LinkedIn
    (9, 15, 16), (9, 22, 16);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (10, 'Qual o seu principal sistema operacional?', '', FALSE, 1, UTC_TIMESTAMP() + INTERVAL 5 MINUTE, UTC_TIMESTAMP() + INTERVAL 2 MONTH, 12);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (10, 0, 'Windows', 0),
    (10, 1, 'Linux', 0),
    (10, 2, 'Mac OS', 0),
    (10, 3, 'Free BSD', 0),
    (10, 4, 'Outro', 0);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (11, 'Sino ang pinakagusto mong karakter sa Until Then?', 'Mahal ko ang larong ito! 💜', FALSE, 1, UTC_TIMESTAMP() - INTERVAL 2 DAY, UTC_TIMESTAMP() + INTERVAL 1 DAY, 30);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (11, 0, 'Mark Borja', 0),
    (11, 1, 'Catherine Portillo', 0),
    (11, 2, 'Nicole Lacsamana', 0),
    (11, 3, 'Ridel Gonzales', 0),
    (11, 4, 'Louise Ordunia', 0);

INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    -- Mark Borja
    (11, 1, 1), (11, 2, 1), (11, 3, 1), (11, 4, 1), (11, 5, 1),
    (11, 6, 1), (11, 7, 1), (11, 8, 1), (11, 9, 1), (11, 10, 1),

    -- Catherine Portillo
    (11, 11, 2), (11, 12, 2), (11, 13, 2), (11, 14, 2), (11, 15, 2),
    (11, 16, 2),

    -- Nicole Lacsamana
    (11, 17, 4), (11, 18, 4), (11, 19, 4), (11, 20, 4), (11, 21, 4),
    (11, 22, 4), (11, 23, 4), (11, 24, 4), (11, 25, 4), (11, 26, 4),

    -- Ridel Gonzales
    (11, 27, 8), (11, 28, 8), (11, 29, 8),

    -- Louise Ordunia
    (11, 30, 16), (11, 31, 16), (11, 32, 16), (11, 33, 16), (11, 34, 16);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (12, '你最喜欢《凸变英雄X》里的哪个英雄？', '', FALSE, 1, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 14 DAY, 42);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (12, 0, '林凌 (Lin Ling)', 0),
    (12, 1, 'Hero X', 0),
    (12, 2, '魂电 (E-Soul)', 0),
    (12, 3, '女王 (Queen)', 0),
    (12, 4, '幸运青 (Lucky Cyan)', 0);


INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES (13, '¿Cuál es el mejor juego multijugador?', 'Seleccione hasta 3 opciones', FALSE, 3, UTC_TIMESTAMP(), UTC_TIMESTAMP() + INTERVAL 3 DAY, 9);

INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (13, 0, 'League of Legends', 0),
    (13, 1, 'Counter Strike', 0),
    (13, 2, 'Valorant', 0),
    (13, 3, 'Call of Duty', 0),
    (13, 4, 'Dota 2', 0);



UPDATE TB_VOTE_OPTION
SET count = (
    SELECT COUNT(*)
    FROM TB_VOTE
    WHERE TB_VOTE.poll_id = TB_VOTE_OPTION.poll_id
    AND (TB_VOTE.option_value & POWER(2, TB_VOTE_OPTION.sequence)) > 0
);