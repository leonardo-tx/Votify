-- Inicialização de dados para ambiente de desenvolvimento
INSERT INTO TB_USER (id, userName, name, email, password, USER_TYPE)
VALUES
    -- Senha admin123
    (1, 'admin', 'Administrator', 'admin@votify.com.br', '$2a$10$iZxot5DeNjNJbm6nHgNjgun1s3NGDZitVB3bsezXntbORUQ2lF5Xi', 'ADMIN'),
    -- Senha moderator321
    (2, 'moderator', 'Moderator', 'moderator@votify.com.br', '$2a$10$Wqhv6Ma5siD3i0kSkDp/MOEMPSrOXdX.7FMQ9L6JIFnJAbCZ9941S', 'MODERATOR'),
    -- Senha password123
    (3, 'common1', 'Common User 1', 'common1@votify.com.br', '$2a$10$dfrodFN2XzDooeL3EYOjJep8eb9/L0Cy4Y5Hvg2YnNNug.JcWKUe2', 'COMMON'),
    (4, 'common2', 'Common User 2', 'common2@votify.com.br', '$2a$10$dfrodFN2XzDooeL3EYOjJep8eb9/L0Cy4Y5Hvg2YnNNug.JcWKUe2', 'COMMON'),
    (5, 'common3', 'Common User 3', 'common3@votify.com.br', '$2a$10$dfrodFN2XzDooeL3EYOjJep8eb9/L0Cy4Y5Hvg2YnNNug.JcWKUe2', 'COMMON');

-- Exemplo de dados adicionais para o ambiente de desenvolvimento
-- Inserir algumas pesquisas de exemplo
INSERT INTO TB_POLL (id, title, description, user_registration, choice_limit_per_user, start_date, end_date, responsible_id)
VALUES
    (1, 'Melhor Linguagem de Programação', 'Qual você considera a melhor linguagem de programação?', FALSE, 1, CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 1),
    (2, 'Melhor Framework Web', 'Qual o melhor framework para desenvolvimento web?', TRUE, 1, CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 1),
    (3, 'Metodologia Ágil Favorita', 'Qual sua metodologia ágil preferida?', FALSE, 1, CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 2),
    (4, 'Selecione seus jogos favoritos', 'Apenas o dessa lista, claro!\n Não dá pra colocar mais que 5 opções :P (Por favor devs, coloquem mais ;-;)', FALSE, 5, CURRENT_TIMESTAMP(), DATEADD('DAY', 14, CURRENT_TIMESTAMP()), 5);

-- Inserir opções para as pesquisas
INSERT INTO TB_VOTE_OPTION (poll_id, sequence, name, count)
VALUES
    (1, 0, 'Java', 2),
    (1, 1, 'Python', 0),
    (1, 2, 'JavaScript', 1),
    (1, 3, 'C#', 2),
    (2, 0, 'Spring Boot', 1),
    (2, 1, 'React', 1),
    (2, 2, 'Angular', 1),
    (2, 3, 'Django', 0),
    (3, 0, 'Scrum', 1),
    (3, 1, 'Kanban', 0),
    (3, 2, 'XP', 0),
    (4, 0, 'Hollow Knight', 3),
    (4, 1, 'Hades', 2),
    (4, 2, 'Baldur''s Gate 3', 3),
    (4, 3, 'The Witcher 3', 2),
    (4, 4, 'It Takes Two', 2);

-- Inserir alguns votos
INSERT INTO TB_VOTE (poll_id, user_id, option_value)
VALUES
    (1, 1, 1),
    (1, 2, 8),
    (1, 3, 4),
    (1, 4, 8),
    (1, 5, 1),
    (2, 1, 2),
    (2, 3, 1),
    (2, 5, 4),
    (3, 1, 1),
    (4, 1, 31),
    (4, 2, 3),
    (4, 3, 4),
    (4, 4, 29);