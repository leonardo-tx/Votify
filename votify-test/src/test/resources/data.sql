
INSERT INTO TB_USER (id, userName, name, email, password, USER_TYPE)
VALUES
    -- Senha admin123
    (1, 'admin', 'Administrator', 'admin@votify.com.br', '$2a$10$iZxot5DeNjNJbm6nHgNjgun1s3NGDZitVB3bsezXntbORUQ2lF5Xi', 'ADMIN'),
    -- Senha moderator321
    (2, 'moderator', 'Moderator', 'moderator@votify.com.br', '$2a$10$Wqhv6Ma5siD3i0kSkDp/MOEMPSrOXdX.7FMQ9L6JIFnJAbCZ9941S', 'MODERATOR'),
    -- Senha password123
    (3, 'common', 'Common', 'common@votify.com.br', '$2a$10$dfrodFN2XzDooeL3EYOjJep8eb9/L0Cy4Y5Hvg2YnNNug.JcWKUe2', 'COMMON');