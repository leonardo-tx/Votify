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
INSERT INTO TB_POLL (id, title, description, start_date, end_date, creator_id, poll_status)
VALUES
    (1, 'Melhor Linguagem de Programação', 'Qual você considera a melhor linguagem de programação?', CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 1, 'ACTIVE'),
    (2, 'Melhor Framework Web', 'Qual o melhor framework para desenvolvimento web?', CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 1, 'ACTIVE'),
    (3, 'Metodologia Ágil Favorita', 'Qual sua metodologia ágil preferida?', CURRENT_TIMESTAMP(), DATEADD('DAY', 7, CURRENT_TIMESTAMP()), 2, 'ACTIVE');

-- Inserir opções para as pesquisas
INSERT INTO TB_POLL_OPTION (id, text, poll_id)
VALUES
    (1, 'Java', 1),
    (2, 'Python', 1),
    (3, 'JavaScript', 1),
    (4, 'C#', 1),
    (5, 'Spring Boot', 2),
    (6, 'React', 2),
    (7, 'Angular', 2),
    (8, 'Django', 2),
    (9, 'Scrum', 3),
    (10, 'Kanban', 3),
    (11, 'XP', 3);

-- Inserir alguns votos
INSERT INTO TB_VOTE (id, poll_id, user_id, option_id, vote_date)
VALUES
    (1, 1, 3, 1, CURRENT_TIMESTAMP()),
    (2, 1, 4, 2, CURRENT_TIMESTAMP()),
    (3, 1, 5, 3, CURRENT_TIMESTAMP()),
    (4, 2, 3, 5, CURRENT_TIMESTAMP()),
    (5, 2, 4, 6, CURRENT_TIMESTAMP()),
    (6, 3, 3, 9, CURRENT_TIMESTAMP()),
    (7, 3, 4, 9, CURRENT_TIMESTAMP()),
    (8, 3, 5, 10, CURRENT_TIMESTAMP()); 